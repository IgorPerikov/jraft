package com.github.igorperikov.jraft.consensus;

import com.github.igorperikov.jraft.Node;
import com.github.igorperikov.jraft.NodeState;
import com.github.igorperikov.jraft.handler.RaftInitHandler;
import com.github.igorperikov.jraft.handler.client.CasMessageHandler;
import com.github.igorperikov.jraft.handler.client.DeleteMessageHandler;
import com.github.igorperikov.jraft.handler.client.ReadMessageHandler;
import com.github.igorperikov.jraft.handler.client.WriteMessageHandler;
import com.github.igorperikov.jraft.handler.consensus.AppendEntriesHandler;
import com.github.igorperikov.jraft.handler.consensus.RequestVoteHandler;
import com.github.igorperikov.jraft.infrastructure.MaelstromMessage;
import com.github.igorperikov.jraft.infrastructure.MessageDispatcher;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

// TODO: dirty test
class CandidateConversionTest {
    private ElectionService electionService;

    @Test
    void should_convert_to_candidate_state_on_election_timeout() throws InterruptedException {
        Node node = new Node();
        int electionTimeout = 100;
        initNode(node, electionTimeout);
        TimeUnit.MILLISECONDS.sleep((long) (electionTimeout * 1.5));

        assertEquals(node.getNodeState(), NodeState.CANDIDATE);
    }

    @Test
    void should_not_convert_to_candidate_state_on_correct_heartbeats() throws InterruptedException {
        Node node = new Node();
        int electionTimeout = 100;
        initNode(node, electionTimeout);
        Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(
                () -> electionService.receiveHeartbeat(),
                electionTimeout / 4,
                electionTimeout / 4,
                TimeUnit.MILLISECONDS
        );
        TimeUnit.MILLISECONDS.sleep((long) (electionTimeout * 1.5));
        assertEquals(node.getNodeState(), NodeState.FOLLOWER);
    }

    private void initNode(@Nonnull Node node, int electionTimeout) {
        String nodeId = "my-node-id";
        MaelstromMessage message = RaftInitMessageBuilder.buildRaftInitMessage(
                nodeId,
                Collections.singletonList(nodeId),
                42
        );
        MessageDispatcher dispatcher = buildMessageDispatcher(node, electionTimeout, electionTimeout + 1);
        dispatcher.dispatchMessage(message);
    }

    private MessageDispatcher buildMessageDispatcher(Node node, int minElectionTimeout, int maxElectionTimeout) {
        electionService = new ElectionService(node, minElectionTimeout, maxElectionTimeout);
        return new MessageDispatcher(
                new RaftInitHandler(node, electionService),
                new WriteMessageHandler(node),
                new ReadMessageHandler(node, Mockito.mock(StateMachineService.class)),
                new CasMessageHandler(node),
                new DeleteMessageHandler(node),
                new AppendEntriesHandler(node),
                new RequestVoteHandler(node)
        );
    }
}
