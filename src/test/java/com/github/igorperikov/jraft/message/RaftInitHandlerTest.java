package com.github.igorperikov.jraft.message;

import com.github.igorperikov.jraft.Node;
import com.github.igorperikov.jraft.NodeState;
import com.github.igorperikov.jraft.message.handler.RaftInitHandler;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class RaftInitHandlerTest {
    private static final String BODY_MESSAGE_NODE_ID_NAME = "node_id";
    private static final String BODY_MESSAGE_NODE_IDS_NAME = "node_ids";
    private static final String BODY_MESSAGE_MSG_ID_NAME = "msg_id";

    @Test
    void shouldCorrectlyInitNodeState() {
        Node node = new Node();
        MessageDispatcher dispatcher = new MessageDispatcher(new RaftInitHandler(node));
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
        body.put(BODY_MESSAGE_NODE_ID_NAME, nodeId);
        body.put(BODY_MESSAGE_NODE_IDS_NAME, nodeIds);
        body.put(BODY_MESSAGE_MSG_ID_NAME, msgId);
        return new MaelstromMessage("src", nodeId, body);
    }
}
