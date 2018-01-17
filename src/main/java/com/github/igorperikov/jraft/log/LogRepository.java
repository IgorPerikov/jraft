package com.github.igorperikov.jraft.log;

import javax.annotation.Nonnull;
import java.util.List;

public interface LogRepository {
    // latest term server has seen(init to 0 on first boot, inc monotonically)
    int getCurrentTerm();

    void setCurrentTerm(int currentTerm);

    // candidateId that received vote in currentTerm(or null if none)
    String getVotedFor();

    void setVotedFor(String votedFor);

    // log entries, each entry contains command for state machine, and term when entry was received by leader(first index is 1)
    @Nonnull
    List<LogEntry> getLog();

    void appendEntry(LogEntry entry);

    void destroy();
}
