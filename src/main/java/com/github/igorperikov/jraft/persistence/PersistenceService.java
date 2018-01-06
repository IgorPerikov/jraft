package com.github.igorperikov.jraft.persistence;

import com.github.igorperikov.jraft.domain.Command;
import com.github.igorperikov.jraft.domain.LogEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class PersistenceService {
    private final PersistenceRepository persistenceRepository;

    @Autowired
    public PersistenceService(PersistenceRepository persistenceRepository) {
        this.persistenceRepository = persistenceRepository;
    }

    public Optional<Command> getLastKnownCommand(String key) {
        List<LogEntry> log = persistenceRepository.getLog();
        Collections.reverse(log);
        return log.stream()
                .filter(entry -> entry.getCommand().getKey().equals(key))
                .findFirst()
                .map(LogEntry::getCommand);
    }
}
