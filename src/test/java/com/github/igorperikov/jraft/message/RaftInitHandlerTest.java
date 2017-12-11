package com.github.igorperikov.jraft.message;

import com.github.igorperikov.jraft.Node;
import com.github.igorperikov.jraft.NodeState;
import com.github.igorperikov.jraft.message.handler.*;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class RaftInitHandlerTest {
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
                () -> assertEquals(MessageTypes.RAFT_INIT_OK, response.getBody().get("type")),
                () -> assertEquals(msgId, response.getBody().get("in_reply_to")),
                () -> assertEquals(NodeState.FOLLOWER, node.getNodeState()),
                () -> assertEquals(response.getDest(), message.getDest()),
                () -> assertEquals(nodeId, response.getSrc())
        );
    }

    private MaelstromMessage buildMessage(String nodeId, List<String> nodeIds, Integer msgId) {
        Map<String, Object> body = new HashMap<>();
        body.put("type", MessageTypes.RAFT_INIT);
        body.put(MessageKeys.NODE_ID_KEY, nodeId);
        body.put(MessageKeys.NODE_IDS_KEY, nodeIds);
        body.put(MessageKeys.MSG_ID_KEY, msgId);
        return new MaelstromMessage("src", nodeId, body);
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
