package com.github.igorperikov.jraft.message;

import com.github.igorperikov.jraft.message.handler.RaftInitHandler;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MessageDispatcher {
    private static final String TYPE_KEY_NAME = "type";

    private final RaftInitHandler raftInitHandler;

    public MaelstromMessage dispatchMessage(MaelstromMessage message) {
        // TODO: casting
        String type = (String) message.getBody().get(TYPE_KEY_NAME);
        switch (type) {
            case MessageTypes.RAFT_INIT:
                return raftInitHandler.handle(message);
            default:
                throw new RuntimeException("'" + type + "' type is not handled yet");
        }
    }
}
