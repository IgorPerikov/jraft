package com.github.igorperikov.jraft.service.infrastructure;

import com.github.igorperikov.jraft.service.MessageFields;
import com.github.igorperikov.jraft.service.MessageTypes;
import com.github.igorperikov.jraft.service.RaftInitHandler;
import com.github.igorperikov.jraft.service.client.CasMessageHandler;
import com.github.igorperikov.jraft.service.client.DeleteMessageHandler;
import com.github.igorperikov.jraft.service.client.ReadMessageHandler;
import com.github.igorperikov.jraft.service.client.WriteMessageHandler;
import com.github.igorperikov.jraft.service.consensus.AppendEntriesHandler;
import com.github.igorperikov.jraft.service.consensus.RequestVoteHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    public MaelstromMessage dispatchMessage(MaelstromMessage message) {
        // TODO: casting
        String type = (String) message.getBody().get(MessageFields.BODY_MSG_TYPE);
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
            case MessageTypes.APPEND_ENTRIES_RPC:
                return appendEntriesHandler.handle(message);
            case MessageTypes.REQUEST_VOTE_RPC:
                return requestVoteHandler.handle(message);
            default:
                throw new RuntimeException("'" + type + "' type is not supported");
        }
    }
}
