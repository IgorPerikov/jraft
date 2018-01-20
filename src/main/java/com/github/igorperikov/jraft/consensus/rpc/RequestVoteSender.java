package com.github.igorperikov.jraft.consensus.rpc;

import com.github.igorperikov.jraft.Node;
import com.github.igorperikov.jraft.infrastructure.MaelstromMessage;
import com.github.igorperikov.jraft.infrastructure.MessageWriter;
import com.github.igorperikov.jraft.infrastructure.constants.MessageFields;
import com.github.igorperikov.jraft.infrastructure.constants.MessageTypes;
import com.github.igorperikov.jraft.log.LogEntry;
import com.github.igorperikov.jraft.log.LogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class RequestVoteSender {
    private final MessageWriter messageWriter;
    private final Node node;
    private final LogRepository logRepository;

    @Autowired
    public RequestVoteSender(MessageWriter messageWriter, Node node, LogRepository logRepository) {
        this.messageWriter = messageWriter;
        this.node = node;
        this.logRepository = logRepository;
    }

    public void sendRequestVote(String to) {
        log.info("Preparing to send vote");
        long term = logRepository.getCurrentTerm();
        String candidateId = node.getId();
        List<LogEntry> logEntries = logRepository.getLog();
        int lastLogIndex = 0;
        long lastLogTerm = 0;
        if (!logEntries.isEmpty()) {
            lastLogIndex = logEntries.size() - 1;
            lastLogTerm = logEntries.get(lastLogIndex).getTerm();
        }
        MaelstromMessage requestVoteMessage = buildMessage(to, term, candidateId, lastLogIndex, lastLogTerm);
        messageWriter.write(requestVoteMessage);
    }

    /**
     * @param lastLogIndex - zero based index
     */
    private MaelstromMessage buildMessage(
            String to,
            long term,
            String candidateId,
            int lastLogIndex,
            long lastLogTerm
    ) {
        return MaelstromMessage.of(
                candidateId,
                to,
                MessageTypes.REQUEST_VOTE_RPC,
                node.getNextMessageId(),
                MessageFields.BODY_MSG_REQUEST_VOTE_CANDIDATE_TERM, term,
                MessageFields.BODY_MSG_REQUEST_VOTE_LAST_LOG_INDEX, lastLogIndex,
                MessageFields.BODY_MSG_REQUEST_VOTE_LAST_LOG_TERM, lastLogTerm
        );
    }
}
