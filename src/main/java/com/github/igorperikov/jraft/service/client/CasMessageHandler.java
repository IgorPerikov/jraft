package com.github.igorperikov.jraft.service.client;

import com.github.igorperikov.jraft.Node;
import com.github.igorperikov.jraft.NodeState;
import com.github.igorperikov.jraft.service.infrastructure.MaelstromMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CasMessageHandler extends ClientMessageHandler {
    private final Node node;

    @Autowired
    public CasMessageHandler(Node node) {
        this.node = node;
    }

    @Override
    public MaelstromMessage handle(MaelstromMessage request) {
        log.info("Got message {}", request);
        if (node.getNodeState() == NodeState.LEADER) {
            return null;
        } else {
            return buildDenyResponse(request);
        }
    }
}
