package com.github.igorperikov.jraft.handler;

import com.github.igorperikov.jraft.infrastructure.MaelstromMessage;

public interface MessageHandler {
    MaelstromMessage handle(MaelstromMessage maelstromMessage);
}
