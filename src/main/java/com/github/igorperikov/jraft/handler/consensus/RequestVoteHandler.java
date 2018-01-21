package com.github.igorperikov.jraft.handler.consensus;

import com.github.igorperikov.jraft.Node;
import com.github.igorperikov.jraft.handler.MessageHandler;
import com.github.igorperikov.jraft.infrastructure.MaelstromMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RequestVoteHandler implements MessageHandler {
    private final Node node;

    public RequestVoteHandler(Node node) {
        this.node = node;
    }

    @Override
    public MaelstromMessage handle(MaelstromMessage maelstromMessage) {
        return null;
    }
}
