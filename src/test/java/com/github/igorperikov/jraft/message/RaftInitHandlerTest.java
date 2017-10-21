package com.github.igorperikov.jraft.message;

import com.github.igorperikov.jraft.Node;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class RaftInitHandlerTest {
    /**
     * these names defined on Maelstrom side
     */
    private static final String BODY_MESSAGE_NODE_ID_NAME = "node_id";
    private static final String BODY_MESSAGE_NODE_IDS_NAME = "node_ids";

    @Test
    void shouldCorrectlyInitNodeState() {
        Node node = new Node();
        MessageDispatcher dispatcher = new MessageDispatcher(new RaftInitHandler(node));
        String nodeId = "my-node-id";
        List<String> nodeIds = Lists.newArrayList(nodeId, "my-node-id-2");
        Map<String, Object> body = new HashMap<>();
        body.put("type", MessageTypes.RAFT_INIT);
        body.put(BODY_MESSAGE_NODE_ID_NAME, nodeId);
        body.put(BODY_MESSAGE_NODE_IDS_NAME, nodeIds);
        MaelstromMessage message = new MaelstromMessage("src", nodeId, body);
        dispatcher.dispatchMessage(message);

        assertAll(
                () -> assertEquals(nodeId, node.getId()),
                () -> assertIterableEquals(nodeIds, node.getNodeIds()),
                () -> assertEquals(2, node.getNodeIds().size())
        );
    }
}
