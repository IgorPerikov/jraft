package com.github.igorperikov.jraft.service;

import com.github.igorperikov.jraft.service.infrastructure.MaelstromMessage;

public interface MessageHandler {
    MaelstromMessage handle(MaelstromMessage maelstromMessage);
}
