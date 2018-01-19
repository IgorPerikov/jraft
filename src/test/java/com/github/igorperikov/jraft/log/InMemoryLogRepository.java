package com.github.igorperikov.jraft.log;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * we dont need realy persistent storage for testing purposes
 */
public class InMemoryLogRepository implements LogRepository {
    private long currentTerm = 0;
    private String votedFor = "";
    private List<LogEntry> log = new ArrayList<>();

    @Override
    public long getCurrentTerm() {
        return currentTerm;
    }

    @Override
    public void setCurrentTerm(long currentTerm) {
        this.currentTerm = currentTerm;
    }

    @Override
    public String getVotedFor() {
        return votedFor;
    }

    @Override
    public void setVotedFor(String votedFor) {
        this.votedFor = votedFor;
    }

    @Nonnull
    @Override
    public List<LogEntry> getLog() {
        return log;
    }

    @Override
    public void appendEntry(LogEntry entry) {
        log.add(entry);
    }
}
