package com.github.igorperikov.jraft.message.handler;

import com.github.igorperikov.jraft.message.MaelstromMessage;

public interface MessageHandler {
    MaelstromMessage handle(MaelstromMessage maelstromMessage);
}
