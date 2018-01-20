package com.github.igorperikov.jraft.log;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Bin;
import com.aerospike.client.Key;
import com.aerospike.client.Record;
import com.aerospike.client.policy.Policy;
import com.aerospike.client.policy.WritePolicy;
import com.github.igorperikov.jraft.Node;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@AllArgsConstructor
@Slf4j
public class AerospikeLogRepository implements LogRepository {
    private static final String NAMESPACE = "test";
    private static final String SETNAME = "jraft";

    private static final String TERM_BIN_NAME = "term";
    private static final String VOTED_FOR_BIN_NAME = "voted-for";
    private static final String LOG_BIN_NAME = "log";

    private static final int DEFAULT_CURRENT_TERM = 0;
    private static final String DEFAULT_VOTED_FOR = "";
    private static final List<LogEntry> DEFAULT_LOG = Collections.emptyList();

    private final AerospikeClient aerospikeClient;
    private final Node node;

    @Override
    public long getCurrentTerm() {
        Record record = aerospikeClient.get(new Policy(), buildKey(), TERM_BIN_NAME);
        if (record == null || record.bins == null) {
            return DEFAULT_CURRENT_TERM;
        } else {
            return (long) record.bins.get(TERM_BIN_NAME);
        }
    }

    @Override
    public void setCurrentTerm(long currentTerm) {
        aerospikeClient.put(new WritePolicy(), buildKey(), new Bin(TERM_BIN_NAME, currentTerm));
    }

    @Override
    public String getVotedFor() {
        Record record = aerospikeClient.get(new Policy(), buildKey(), VOTED_FOR_BIN_NAME);
        if (record == null || record.bins == null) {
            return DEFAULT_VOTED_FOR;
        } else {
            return (String) record.bins.get(VOTED_FOR_BIN_NAME);
        }
    }

    @Override
    public void setVotedFor(String votedFor) {
        aerospikeClient.put(new WritePolicy(), buildKey(), new Bin(VOTED_FOR_BIN_NAME, votedFor));
    }

    @Nonnull
    @Override
    public List<LogEntry> getLog() {
        Record record = aerospikeClient.get(new Policy(), buildKey(), LOG_BIN_NAME);
        if (record == null || record.bins == null) {
            return DEFAULT_LOG;
        } else {
            return (List<LogEntry>) record.getList(LOG_BIN_NAME);
        }
    }

    @Override
    public void appendEntry(LogEntry entry) {
        Record record = aerospikeClient.get(new Policy(), buildKey(), LOG_BIN_NAME);
        List<LogEntry> entries;
        if (record == null || record.bins == null) {
            entries = new ArrayList<>();
        } else {
            entries = (List<LogEntry>) record.getList(LOG_BIN_NAME);
        }
        entries.add(entry);
        aerospikeClient.put(new WritePolicy(), buildKey(), new Bin(LOG_BIN_NAME, entries));
    }

    private Key buildKey() {
        return new Key(NAMESPACE, SETNAME, node.getId());
    }
}
