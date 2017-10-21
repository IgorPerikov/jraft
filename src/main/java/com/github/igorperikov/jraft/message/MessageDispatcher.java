package com.github.igorperikov.jraft.message;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MessageDispatcher {
    private static final String TYPE_KEY_NAME = "type";

    private final RaftInitHandler raftInitHandler;

    public void dispatchMessage(MaelstromMessage message) {
        // TODO: casting
        String type = (String) message.getBody().get(TYPE_KEY_NAME);
        switch (type) {
            case MessageTypes.RAFT_INIT:
                raftInitHandler.handle(message);
                break;
            default:
                throw new RuntimeException("'" + type + "' type is not handled yet");
        }
    }
}
