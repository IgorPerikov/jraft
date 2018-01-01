package com.github.igorperikov.jraft.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.igorperikov.jraft.domain.Command;
import com.github.igorperikov.jraft.domain.LogEntry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBasedPersistenceServiceTest {
    private static final String NODE_ID = "node_id";

    private PersistenceService persistenceService;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setupInitialState() {
        persistenceService = new FileBasedPersistenceService(NODE_ID, objectMapper);
        deleteFiles();
    }

    @Test
    void persistentStateShouldBeInitialisedWithDefaultValues() {
        assertAll(
                () -> assertEquals(0, persistenceService.getCurrentTerm()),
                () -> assertEquals("", persistenceService.getVotedFor()),
                () -> assertEquals(0, persistenceService.getLog().size())
        );
    }

    @Test
    void shouldSaveStateAfterRestart() {
        int currentTerm = 2;
        String votedFor = "some-other-node";
        LogEntry entry = new LogEntry(new Command("x", "10"), 1);

        persistenceService.setCurrentTerm(currentTerm);
        persistenceService.setVotedFor(votedFor);
        persistenceService.appendEntry(entry);

        // imitate jvm restart
        PersistenceService newPersistenceService = new FileBasedPersistenceService(NODE_ID, objectMapper);

        assertAll(
                () -> assertEquals(currentTerm, newPersistenceService.getCurrentTerm()),
                () -> assertEquals(votedFor, newPersistenceService.getVotedFor()),
                () -> assertEquals(1, newPersistenceService.getLog().size()),
                () -> assertEquals(entry, newPersistenceService.getLog().iterator().next())
        );
    }

    @AfterEach
    void destroyInitialState() {
        deleteFiles();
    }

    private void deleteFiles() {
        persistenceService.destroy();
    }
}
