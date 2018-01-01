package com.github.igorperikov.jraft.service;

import com.github.igorperikov.jraft.Node;
import com.github.igorperikov.jraft.NodeState;
import com.github.igorperikov.jraft.service.client.CasMessageHandler;
import com.github.igorperikov.jraft.service.client.DeleteMessageHandler;
import com.github.igorperikov.jraft.service.client.ReadMessageHandler;
import com.github.igorperikov.jraft.service.client.WriteMessageHandler;
import com.github.igorperikov.jraft.service.infrastructure.MaelstromMessage;
import com.github.igorperikov.jraft.service.infrastructure.MessageDispatcher;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RaftInitHandlerTest {
    @Test
    void should_correctly_init_node_state() {
        Node node = new Node();
        MessageDispatcher dispatcher = buildMessageDispatcher(node);
        String nodeId = "my-node-id";
        Integer msgId = 111;

        List<String> nodeIds = Lists.newArrayList(nodeId, "my-node-id-2");
        MaelstromMessage message = buildMessage(nodeId, nodeIds, msgId);
        MaelstromMessage response = dispatcher.dispatchMessage(message);

        assertAll(
                () -> assertEquals(nodeId, node.getId()),
                () -> assertIterableEquals(nodeIds, node.getNodeIds()),
                () -> assertEquals(2, node.getNodeIds().size()),
                () -> assertEquals(MessageTypes.RAFT_INIT_OK, response.getBody().get(MessageFields.BODY_MSG_TYPE)),
                () -> assertEquals(msgId, response.getBody().get(MessageFields.BODY_MSG_IN_REPLY_TO)),
                () -> assertEquals(NodeState.FOLLOWER, node.getNodeState()),
                () -> assertEquals(response.getDest(), message.getDest()),
                () -> assertEquals(nodeId, response.getSrc()),
                () -> assertTrue(node.isInitialized())
        );
    }

    private MaelstromMessage buildMessage(String nodeId, List<String> nodeIds, Integer msgId) {
        Map<String, Object> body = new HashMap<>();
        body.put(MessageFields.BODY_MSG_TYPE, MessageTypes.RAFT_INIT);
        body.put(MessageFields.BODY_MSG_RAFT_INIT_NODE_ID, nodeId);
        body.put(MessageFields.BODY_MSG_RAFT_INIT_NODE_IDS, nodeIds);
        body.put(MessageFields.BODY_MSG_ID, msgId);
        return new MaelstromMessage(MessageFields.SRC, nodeId, body);
    }

    private MessageDispatcher buildMessageDispatcher(Node node) {
        return new MessageDispatcher(
                new RaftInitHandler(node),
                new WriteMessageHandler(node),
                new ReadMessageHandler(node),
                new CasMessageHandler(node),
                new DeleteMessageHandler(node)
        );
    }
}
