package com.github.igorperikov.jraft.message;

public interface MessageHandler {
    MaelstromMessage handle(MaelstromMessage maelstromMessage);
}
