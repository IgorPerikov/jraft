package com.github.igorperikov.jraft.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.igorperikov.jraft.domain.Command;
import com.github.igorperikov.jraft.domain.LogEntry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBasedPersistenceRepositoryTest {
    private static final String NODE_ID = "node_id";

    private PersistenceRepository persistenceRepository;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setupInitialState() {
        persistenceRepository = new FileBasedPersistenceRepository(NODE_ID, objectMapper);
        deleteFiles();
    }

    @Test
    void persistentStateShouldBeInitialisedWithDefaultValues() {
        assertAll(
                () -> assertEquals(0, persistenceRepository.getCurrentTerm()),
                () -> assertEquals("", persistenceRepository.getVotedFor()),
                () -> assertEquals(0, persistenceRepository.getLog().size())
        );
    }

    @Test
    void shouldSaveStateAfterRestart() {
        int currentTerm = 2;
        String votedFor = "some-other-node";
        LogEntry entry = new LogEntry(new Command("x", "10"), 1);

        persistenceRepository.setCurrentTerm(currentTerm);
        persistenceRepository.setVotedFor(votedFor);
        persistenceRepository.appendEntry(entry);

        // imitate jvm restart
        PersistenceRepository newPersistenceRepository = new FileBasedPersistenceRepository(NODE_ID, objectMapper);

        assertAll(
                () -> assertEquals(currentTerm, newPersistenceRepository.getCurrentTerm()),
                () -> assertEquals(votedFor, newPersistenceRepository.getVotedFor()),
                () -> assertEquals(1, newPersistenceRepository.getLog().size()),
                () -> assertEquals(entry, newPersistenceRepository.getLog().iterator().next())
        );
    }

    @AfterEach
    void destroyInitialState() {
        deleteFiles();
    }

    private void deleteFiles() {
        persistenceRepository.destroy();
    }
}
