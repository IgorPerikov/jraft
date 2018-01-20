package com.github.igorperikov.jraft.infrastructure;

import com.github.igorperikov.jraft.handler.RaftInitHandler;
import com.github.igorperikov.jraft.handler.client.CasMessageHandler;
import com.github.igorperikov.jraft.handler.client.DeleteMessageHandler;
import com.github.igorperikov.jraft.handler.client.ReadMessageHandler;
import com.github.igorperikov.jraft.handler.client.WriteMessageHandler;
import com.github.igorperikov.jraft.handler.consensus.AppendEntriesHandler;
import com.github.igorperikov.jraft.handler.consensus.RequestVoteHandler;
import com.github.igorperikov.jraft.infrastructure.constants.MessageFields;
import com.github.igorperikov.jraft.infrastructure.constants.MessageTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;

@Component
public class MessageDispatcher {
    private final RaftInitHandler raftInitHandler;
    private final WriteMessageHandler writeMessageHandler;
    private final ReadMessageHandler readMessageHandler;
    private final CasMessageHandler casMessageHandler;
    private final DeleteMessageHandler deleteMessageHandler;
    private final AppendEntriesHandler appendEntriesHandler;
    private final RequestVoteHandler requestVoteHandler;

    @Autowired
    public MessageDispatcher(
            RaftInitHandler raftInitHandler,
            WriteMessageHandler writeMessageHandler,
            ReadMessageHandler readMessageHandler,
            CasMessageHandler casMessageHandler,
            DeleteMessageHandler deleteMessageHandler,
            AppendEntriesHandler appendEntriesHandler,
            RequestVoteHandler requestVoteHandler
    ) {
        this.raftInitHandler = raftInitHandler;
        this.writeMessageHandler = writeMessageHandler;
        this.readMessageHandler = readMessageHandler;
        this.casMessageHandler = casMessageHandler;
        this.deleteMessageHandler = deleteMessageHandler;
        this.appendEntriesHandler = appendEntriesHandler;
        this.requestVoteHandler = requestVoteHandler;
    }

    @Nullable
    public MaelstromMessage dispatchMessage(MaelstromMessage message) {
        // TODO: casting
        String type = (String) message.getBody().get(MessageFields.BODY_MSG_TYPE);
        switch (type) {
            case MessageTypes.RAFT_INIT:
                return raftInitHandler.handle(message);
            case MessageTypes.WRITE:
                writeMessageHandler.handle(message);
                return null;
            case MessageTypes.READ:
                readMessageHandler.handle(message);
                return null;
            case MessageTypes.CAS:
                casMessageHandler.handle(message);
                return null;
            case MessageTypes.DELETE:
                deleteMessageHandler.handle(message);
                return null;
            case MessageTypes.APPEND_ENTRIES_RPC:
                appendEntriesHandler.handle(message);
                return null;
            case MessageTypes.REQUEST_VOTE_RPC:
                requestVoteHandler.handle(message);
                return null;
            default:
                throw new RuntimeException("'" + type + "' type is not supported");
        }
    }
}
