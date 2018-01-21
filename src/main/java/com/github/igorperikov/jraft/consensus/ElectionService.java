package com.github.igorperikov.jraft.consensus;

import com.github.igorperikov.jraft.ExecutorUtility;
import com.github.igorperikov.jraft.Node;
import com.github.igorperikov.jraft.NodeState;
import com.github.igorperikov.jraft.consensus.rpc.RequestVoteSender;
import com.github.igorperikov.jraft.log.LogRepository;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

// TODO: split service on smaller ones
@Service
@Slf4j
public class ElectionService {
    private final Node node;
    private final RequestVoteSender requestVoteSender;
    private final LogRepository logRepository;

    private final int minElectionTimeout;
    private final int maxElectionTimeout;

    private final Random random = new Random();

    private volatile boolean heartbeatAcquired = false;

    private LeaderElectionStatus leaderElectionStatus;

    private ScheduledExecutorService heartbeatListenerExecutor;
    private ScheduledExecutorService requestVoteSenderExecutor;

    public ElectionService(
            Node node,
            RequestVoteSender requestVoteSender,
            LogRepository logRepository,
            @Value("${raft.min-election-timeout}") int minElectionTimeout,
            @Value("${raft.max-election-timeout}") int maxElectionTimeout
    ) {
        this.node = node;
        this.logRepository = logRepository;
        this.requestVoteSender = requestVoteSender;
        this.minElectionTimeout = minElectionTimeout;
        this.maxElectionTimeout = maxElectionTimeout;
    }

    public void receiveHeartbeat() {
        log.info("Received heartbeat");
        // TODO: interrupt current actions if newer leader exists etc..
        heartbeatAcquired = true;
    }

    public void receiveVote(String voteGrantedFrom) {
        if (node.getNodeState() != NodeState.CANDIDATE) {
            log.error("Vote received for non-candidate node");
        }
        // TODO:
    }

    public void convertToFollower() {
        log.info("Converting to follower");
        node.setNodeState(NodeState.FOLLOWER);
        startListenForHeartbeats();
        // TODO:
    }

    private void convertToCandidate() {
        long nextTerm = logRepository.getCurrentTerm() + 1;
        log.info("Setting term={}", nextTerm);
        logRepository.setCurrentTerm(nextTerm);
        node.setNodeState(NodeState.CANDIDATE);
        logRepository.setVotedFor(node.getId());
        leaderElectionStatus = new LeaderElectionStatus(node);
        startSendingRequestVotes();
        launchElectionTimeoutClock();
        log.info("Conversion to candidate state completed");
    }

    private void launchElectionTimeoutClock() {
        // TODO: next election happens in election-timeout-clock thread, but it shouldn't
        Executors.newSingleThreadScheduledExecutor(
                new ThreadFactoryBuilder().setNameFormat("election-timeout-clock").build()
        ).schedule(
                this::initiateNewVote,
                maxElectionTimeout, // TODO:
                TimeUnit.MILLISECONDS
        );
    }

    // TODO: incorrect name, split or merge with convertToCandidate()
    private void initiateNewVote() {
        log.info("Node hasn't won the election, initiate a new one");
        stopSendingRequestVotes();
        convertToCandidate();
    }

    public void convertToLeader() {
        log.info("Converting to leader");
        node.setNodeState(NodeState.LEADER);
        // TODO:
    }

    private void startListenForHeartbeats() {
        heartbeatListenerExecutor = Executors.newSingleThreadScheduledExecutor(
                new ThreadFactoryBuilder().setNameFormat("heartbeat-listener").build()
        );
        int electionTimeout = calculateElectionTimeout();
        log.info("Start listening for heartbeats with election timeout={}", electionTimeout);
        heartbeatListenerExecutor.scheduleWithFixedDelay(
                () -> ExecutorUtility.runWithExceptions(this::verifyHeartbeat),
                electionTimeout,
                electionTimeout,
                TimeUnit.MILLISECONDS
        );
    }

    private void verifyHeartbeat() {
        if (heartbeatAcquired) {
            log.info("Heartbeat was acquired during last session, keep staying as follower");
            heartbeatAcquired = false;
        } else {
            log.info("No heartbeat was acquired during last session, converting to candidate");
            convertToCandidate();
            stopListeningForHeartbeats();
        }
    }

    private void stopListeningForHeartbeats() {
        heartbeatListenerExecutor.shutdownNow();
    }

    private int calculateElectionTimeout() {
        return random.nextInt(maxElectionTimeout - minElectionTimeout) + minElectionTimeout;
    }

    private void startSendingRequestVotes() {
        log.info("Start sending request votes to other nodes");
        requestVoteSenderExecutor = Executors.newSingleThreadScheduledExecutor(
                new ThreadFactoryBuilder().setNameFormat("request-vote-sender").build()
        );
        requestVoteSenderExecutor.scheduleWithFixedDelay(
                () -> ExecutorUtility.runWithExceptions(() -> {
                    Set<String> nodes = leaderElectionStatus.getNodesWhoHasntVotedYet();
                    log.info("Acquiring nodes, who hasn't voted yet={}", nodes);
                    for (String nodeId : nodes) {
                        requestVoteSender.sendRequestVote(nodeId);
                    }
                }),
                0,
                maxElectionTimeout / 3, // TODO: specify delay
                TimeUnit.MILLISECONDS
        );
    }

    private void stopSendingRequestVotes() {
        requestVoteSenderExecutor.shutdownNow();
    }

    private static class LeaderElectionStatus {
        final int quorum;
        final Set<String> all = new HashSet<>();
        final Set<String> rejected = new HashSet<>();
        final Set<String> acquiredVotes = new HashSet<>();

        LeaderElectionStatus(Node node) {
            quorum = (int) Math.floor(node.getNodeIds().size() / (double) 2) + 1;
            all.addAll(node.getNodeIds());
            provideVote(node.getId());
        }

        void provideVote(String nodeId) {
            acquiredVotes.add(nodeId);
        }

        void rejectVote(String nodeId) {
            rejected.add(nodeId);
        }

        /**
         * @return nodes, who haven't voted yet
         */
        Set<String> getNodesWhoHasntVotedYet() {
            HashSet<String> result = new HashSet<>(all);
            result.removeAll(rejected);
            result.removeAll(acquiredVotes);
            return result;
        }
    }
}
