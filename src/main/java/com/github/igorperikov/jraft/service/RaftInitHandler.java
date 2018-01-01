package com.github.igorperikov.jraft.service;

import com.github.igorperikov.jraft.Node;
import com.github.igorperikov.jraft.service.infrastructure.MaelstromMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class RaftInitHandler implements MessageHandler {
    private final Node node;

    @Autowired
    public RaftInitHandler(Node node) {
        this.node = node;
    }

    public MaelstromMessage handle(MaelstromMessage request) {
        log.info("Got message {}", request);

        // TODO: casting
        String nodeId = (String) request.getBody().get(MessageFields.BODY_MSG_RAFT_INIT_NODE_ID);
        node.setId(nodeId);

        // TODO: casting
        List<String> nodeIds = (List<String>) request.getBody().get(MessageFields.BODY_MSG_RAFT_INIT_NODE_IDS);
        node.addNodeIds(nodeIds);

        node.setInitialized(true);

        Map<String, Object> body = new HashMap<>();
        body.put(MessageFields.BODY_MSG_TYPE, MessageTypes.RAFT_INIT_OK);
        body.put(MessageFields.BODY_MSG_IN_REPLY_TO, request.getBody().get(MessageFields.BODY_MSG_ID));
        return new MaelstromMessage(nodeId, request.getDest(), body);
    }
}
