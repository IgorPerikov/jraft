package com.github.igorperikov.jraft.consensus;

import com.github.igorperikov.jraft.Node;
import com.github.igorperikov.jraft.NodeState;
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

    private final int minElectionTimeout;
    private final int maxElectionTimeout;

    private final Random random = new Random();

    private volatile boolean heartbeatAcquired = false;

    private ScheduledExecutorService heartbeatExecutor;

    public ElectionService(
            Node node,
            @Value("${raft.min-election-timeout}") int minElectionTimeout,
            @Value("${raft.max-election-timeout}") int maxElectionTimeout
    ) {
        this.node = node;
        this.minElectionTimeout = minElectionTimeout;
        this.maxElectionTimeout = maxElectionTimeout;
    }

    public void receiveHeartbeat() {
        heartbeatAcquired = true;
    }

    public void convertToFollower() {
        log.info("Converting to follower");
        node.setNodeState(NodeState.FOLLOWER);
        startListenForHeartbeats();
        // TODO:
    }

    private void convertToCandidate() {
        log.info("Converting to candidate");
        node.setNodeState(NodeState.CANDIDATE);
        startSendingRequestVotes();
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
            stopListenForHeartbeats();
        }
    }

    private void stopListenForHeartbeats() {
        heartbeatExecutor.shutdownNow();
    }

    private int calculateElectionTimeout() {
        return random.nextInt(maxElectionTimeout - minElectionTimeout) + minElectionTimeout;
    }

    private void startSendingRequestVotes() {
        log.info("Start sending request votes to other nodes");
    }
}
