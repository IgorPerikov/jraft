package com.github.igorperikov.jraft.consensus;

import com.github.igorperikov.jraft.ExecutorUtility;
import com.github.igorperikov.jraft.Node;
import com.github.igorperikov.jraft.NodeState;
import com.github.igorperikov.jraft.consensus.rpc.RequestVoteSender;
import com.github.igorperikov.jraft.handler.RaftInitHandler;
import com.github.igorperikov.jraft.handler.client.CasMessageHandler;
import com.github.igorperikov.jraft.handler.client.DeleteMessageHandler;
import com.github.igorperikov.jraft.handler.client.ReadMessageHandler;
import com.github.igorperikov.jraft.handler.client.WriteMessageHandler;
import com.github.igorperikov.jraft.handler.consensus.AppendEntriesHandler;
import com.github.igorperikov.jraft.handler.consensus.RequestVoteHandler;
import com.github.igorperikov.jraft.infrastructure.MessageDispatcher;
import com.github.igorperikov.jraft.log.InMemoryLogRepository;
import com.github.igorperikov.jraft.log.LogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

// TODO: dirty test
class CandidateConversionTest {
    private final int electionTimeout = 100;

    private ElectionService electionService;
    private LogRepository logRepository;
    private String nodeId = "my-node-id";
    private Node node;

    @BeforeEach
    void init() {
        node = new Node();
        node.setId(nodeId);
        logRepository = new InMemoryLogRepository();
        electionService = new ElectionService(
                node,
                Mockito.mock(RequestVoteSender.class),
                logRepository,
                electionTimeout,
                electionTimeout + 1
        );
        MessageDispatcher dispatcher = new MessageDispatcher(
                new RaftInitHandler(node, electionService),
                new WriteMessageHandler(node),
                new ReadMessageHandler(node, Mockito.mock(StateMachineService.class)),
                new CasMessageHandler(node),
                new DeleteMessageHandler(node),
                new AppendEntriesHandler(node),
                new RequestVoteHandler(node)
        );
        dispatcher.dispatchMessage(
                RaftInitMessageBuilder.buildRaftInitMessage(
                        nodeId,
                        Collections.singletonList(nodeId),
                        42
                )
        );
    }

    @Test
    void should_convert_to_candidate_state_on_election_timeout() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep((long) (electionTimeout * 1.5));

        assertAll(
                () -> assertEquals(node.getNodeState(), NodeState.CANDIDATE),
                () -> assertEquals(1, logRepository.getCurrentTerm())
        );
    }

    @Test
    void should_not_convert_to_candidate_state_on_correct_heartbeats() throws InterruptedException {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleWithFixedDelay(
                () -> ExecutorUtility.runWithExceptions(() -> electionService.receiveHeartbeat()),
                electionTimeout / 4,
                electionTimeout / 4,
                TimeUnit.MILLISECONDS
        );
        TimeUnit.MILLISECONDS.sleep((long) (electionTimeout * 1.5));
        executor.shutdownNow();

        assertAll(
                () -> assertEquals(node.getNodeState(), NodeState.FOLLOWER),
                () -> assertEquals(0, logRepository.getCurrentTerm())
        );
    }
}
