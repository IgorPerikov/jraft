package com.github.igorperikov.jraft.consensus;

import com.github.igorperikov.jraft.Node;
import com.github.igorperikov.jraft.NodeState;
import com.github.igorperikov.jraft.consensus.rpc.RequestVoteSender;
import com.github.igorperikov.jraft.log.LogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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

    private ScheduledExecutorService heartbeatExecutor;

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
    }

    private void convertToCandidate() {
        log.info("Converting to candidate");
        stopListeningForHeartbeats();
        node.setNodeState(NodeState.CANDIDATE);
        long currentTerm = logRepository.getCurrentTerm();
        long nextTerm = currentTerm + 1;
        log.info("Previous term={}, next term={}", currentTerm, nextTerm);
        logRepository.setCurrentTerm(nextTerm);
        voteForYourself();
        startSendingRequestVotes();
        log.info("conversion completed");
    }

    public void convertToLeader() {
        log.info("Converting to leader");
        node.setNodeState(NodeState.LEADER);
    }

    private void startListenForHeartbeats() {
        // TODO: launch executor loop
        heartbeatExecutor = Executors.newSingleThreadScheduledExecutor();
        int electionTimeout = calculateElectionTimeout();
        log.info("Start listening for heartbeats with election timeout={}", electionTimeout);
        heartbeatExecutor.scheduleWithFixedDelay(
                this::verifyHeartbeat,
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
        heartbeatExecutor.shutdownNow();
    }

    private int calculateElectionTimeout() {
        return random.nextInt(maxElectionTimeout - minElectionTimeout) + minElectionTimeout;
    }

    private void startSendingRequestVotes() {
        log.info("Start sending request votes to other nodes");
        // TODO:
    }

    private void voteForYourself() {
        log.info("Granting vote to yourself");
        // TODO:
    }
}
