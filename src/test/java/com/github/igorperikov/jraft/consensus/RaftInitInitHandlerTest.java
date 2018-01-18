package com.github.igorperikov.jraft.consensus;

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
import com.github.igorperikov.jraft.infrastructure.MaelstromMessage;
import com.github.igorperikov.jraft.infrastructure.MessageDispatcher;
import com.github.igorperikov.jraft.infrastructure.constants.MessageFields;
import com.github.igorperikov.jraft.infrastructure.constants.MessageTypes;
import com.github.igorperikov.jraft.log.LogRepository;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RaftInitInitHandlerTest {
    @Test
    void should_correctly_init_node_state() {
        Node node = new Node();
        MessageDispatcher dispatcher = buildMessageDispatcher(node);
        String nodeId = "my-node-id";
        Integer msgId = 111;

        List<String> nodeIds = Lists.newArrayList(nodeId, "my-node-id-2");
        MaelstromMessage message = RaftInitMessageBuilder.buildRaftInitMessage(nodeId, nodeIds, msgId);
        MaelstromMessage response = dispatcher.dispatchMessage(message);

        assertAll(
                () -> assertEquals(nodeId, node.getId()),
                () -> assertIterableEquals(nodeIds, node.getNodeIds()),
                () -> assertEquals(2, node.getNodeIds().size()),
                () -> assertEquals(NodeState.FOLLOWER, node.getNodeState()),
                () -> assertTrue(node.isInitialized()),
                () -> assertEquals(MessageTypes.RAFT_INIT_OK, response.getBody().get(MessageFields.BODY_MSG_TYPE)),
                () -> assertEquals(msgId, response.getBody().get(MessageFields.BODY_MSG_IN_REPLY_TO)),
                () -> assertEquals(response.getDest(), message.getDest()),
                () -> assertEquals(nodeId, response.getSrc())
        );
    }

    MessageDispatcher buildMessageDispatcher(Node node) {
        ElectionService electionService = new ElectionService(
                node,
                Mockito.mock(RequestVoteSender.class),
                Mockito.mock(LogRepository.class),
                50,
                100
        );
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
