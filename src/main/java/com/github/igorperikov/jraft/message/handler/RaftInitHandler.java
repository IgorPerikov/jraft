package com.github.igorperikov.jraft.message.handler;

import com.github.igorperikov.jraft.Node;
import com.github.igorperikov.jraft.message.MaelstromMessage;
import com.github.igorperikov.jraft.message.MessageTypes;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Slf4j
public class RaftInitHandler implements MessageHandler {
    private static final String MSG_ID_KEY = "msg_id";
    private static final String NODE_ID_KEY = "node_id";
    private static final String NODE_IDS_KEY = "node_ids";
    /**
     "msg_id"   An integer
     "node_id"  A string identifying this node
     "node_ids" An array of all node ids in the cluster, including this node
     */
    private final Node node;

    public MaelstromMessage handle(MaelstromMessage message) {
        log.info("Got message {}", message);

        // TODO: casting
        String nodeId = (String) message.getBody().get(NODE_ID_KEY);
        node.setId(nodeId);

        List<String> nodeIds = (List<String>) message.getBody().get(NODE_IDS_KEY);
        node.addNodeIds(nodeIds);

        Map<String, Object> body = new HashMap<>();
        body.put("type", MessageTypes.RAFT_INIT_OK);
        body.put("in_reply_to", message.getBody().get(MSG_ID_KEY));
        return new MaelstromMessage(nodeId, message.getDest(), body);
    }
}
