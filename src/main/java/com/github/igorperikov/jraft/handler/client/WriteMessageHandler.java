package com.github.igorperikov.jraft.handler.client;

import com.github.igorperikov.jraft.Node;
import com.github.igorperikov.jraft.NodeState;
import com.github.igorperikov.jraft.infrastructure.MaelstromMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class WriteMessageHandler extends ClientMessageHandler {
    private final Node node;

    @Autowired
    public WriteMessageHandler(Node node) {
        this.node = node;
    }

    @Override
    public MaelstromMessage handle(MaelstromMessage request) {
        log.info("Got message {}", request);
        if (node.getNodeState() == NodeState.LEADER) {
            return null;
        } else {
            return buildDenyResponse(request, node.getNextMessageId());
        }
    }
}
