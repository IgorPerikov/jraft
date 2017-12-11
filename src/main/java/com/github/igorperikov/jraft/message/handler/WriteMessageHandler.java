package com.github.igorperikov.jraft.message.handler;

import com.github.igorperikov.jraft.Node;
import com.github.igorperikov.jraft.message.MaelstromMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
public class WriteMessageHandler implements MessageHandler {
    private final Node node;

    @Override
    public MaelstromMessage handle(MaelstromMessage message) {
        log.info("Got message {}", message);
        return null;
    }
}
