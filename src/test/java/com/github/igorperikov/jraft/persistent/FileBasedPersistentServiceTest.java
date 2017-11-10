package com.github.igorperikov.jraft.persistent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.igorperikov.jraft.domain.Command;
import com.github.igorperikov.jraft.domain.LogEntry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBasedPersistentServiceTest {
    private static final String NODE_ID = "node_id";

    private FileBasedPersistentService persistentService;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setupInitialState() {
        persistentService = new FileBasedPersistentService(NODE_ID, objectMapper);
        deleteFiles();
    }

    @Test
    void persistentStateShouldBeInitialisedWithDefaultValues() {
        assertAll(
                () -> assertEquals(0, persistentService.getCurrentTerm()),
                () -> assertEquals("", persistentService.getVotedFor()),
                () -> assertEquals(0, persistentService.getLog().size())
        );
    }

    @Test
    void shouldSaveStateAfterRestart() {
        int currentTerm = 2;
        String votedFor = "some-other-node";
        LogEntry entry = new LogEntry(new Command("x", "10"), 1);

        persistentService.setCurrentTerm(currentTerm);
        persistentService.setVotedFor(votedFor);
        persistentService.appendEntry(entry);

        // imitate jvm restart
        PersistentService newPersistentService = new FileBasedPersistentService(NODE_ID, objectMapper);

        assertAll(
                () -> assertEquals(currentTerm, newPersistentService.getCurrentTerm()),
                () -> assertEquals(votedFor, newPersistentService.getVotedFor()),
                () -> assertEquals(1, newPersistentService.getLog().size()),
                () -> assertEquals(entry, newPersistentService.getLog().iterator().next())
        );
    }

    @AfterEach
    void destroyInitialState() {
        deleteFiles();
    }

    private void deleteFiles() {
        persistentService.pathsToFiles().stream()
                .filter(Files::exists)
                .forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }
}
