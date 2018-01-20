package com.github.igorperikov.jraft.handler;

import com.github.igorperikov.jraft.Node;
import com.github.igorperikov.jraft.consensus.ElectionService;
import com.github.igorperikov.jraft.infrastructure.MaelstromMessage;
import com.github.igorperikov.jraft.infrastructure.constants.MessageFields;
import com.github.igorperikov.jraft.infrastructure.constants.MessageTypes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class RaftInitHandler implements MessageHandler {
    private final Node node;
    private final ElectionService electionService;

    @Autowired
    public RaftInitHandler(Node node, ElectionService electionService) {
        this.node = node;
        this.electionService = electionService;
    }

    public MaelstromMessage handle(MaelstromMessage request) {
        log.info("Got message {}", request);

        // TODO: casting
        String nodeId = (String) request.getBody().get(MessageFields.BODY_MSG_RAFT_INIT_NODE_ID);
        node.setId(nodeId);

        // TODO: casting
        List<String> nodeIds = (List<String>) request.getBody().get(MessageFields.BODY_MSG_RAFT_INIT_NODE_IDS);
        node.addNodeIds(nodeIds);

        init();
        return MaelstromMessage.of(
                nodeId,
                request.getDest(),
                MessageTypes.RAFT_INIT_OK,
                node.getNextMessageId(),
                MessageFields.BODY_MSG_IN_REPLY_TO, request.getBody().get(MessageFields.BODY_MSG_ID)
        );
    }

    private void init() {
        node.setInitialized(true);
        electionService.convertToFollower();
    }
}
