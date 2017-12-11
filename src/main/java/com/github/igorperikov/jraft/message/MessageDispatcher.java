package com.github.igorperikov.jraft.message;

import com.github.igorperikov.jraft.message.handler.*;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MessageDispatcher {
    private static final String TYPE_KEY_NAME = "type";

    private final RaftInitHandler raftInitHandler;
    private final WriteMessageHandler writeMessageHandler;
    private final ReadMessageHandler readMessageHandler;
    private final CasMessageHandler casMessageHandler;
    private final DeleteMessageHandler deleteMessageHandler;

    public MaelstromMessage dispatchMessage(MaelstromMessage message) {
        String type = (String) message.getBody().get(TYPE_KEY_NAME);
        switch (type) {
            case MessageTypes.RAFT_INIT:
                return raftInitHandler.handle(message);
            case MessageTypes.WRITE:
                return writeMessageHandler.handle(message);
            case MessageTypes.READ:
                return readMessageHandler.handle(message);
            case MessageTypes.CAS:
                return casMessageHandler.handle(message);
            case MessageTypes.DELETE:
                return deleteMessageHandler.handle(message);
            default:
                throw new RuntimeException("'" + type + "' type is not supported");
        }
    }
}
