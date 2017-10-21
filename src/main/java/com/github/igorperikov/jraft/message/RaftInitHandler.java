package com.github.igorperikov.jraft.message;

import com.github.igorperikov.jraft.Node;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class RaftInitHandler {
    private static final String NODE_ID_KEY = "node_id";
    private static final String NODE_IDS_KEY = "node_ids";
    /**
     "msg_id"   An integer
     "node_id"  A string identifying this node
     "node_ids" An array of all node ids in the cluster, including this node
     */
    private final Node node;

    public void handle(MaelstromMessage message) {
        // TODO: casting
        String nodeId = (String) message.getBody().get(NODE_ID_KEY);
        node.setId(nodeId);

        List<String> nodeIds = (List<String>) message.getBody().get(NODE_IDS_KEY);
        node.addNodeIds(nodeIds);
    }
}
