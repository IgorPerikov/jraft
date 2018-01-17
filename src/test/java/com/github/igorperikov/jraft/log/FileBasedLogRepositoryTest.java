package com.github.igorperikov.jraft.log;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBasedLogRepositoryTest {
    private static final String NODE_ID = "node_id";

    private LogRepository logRepository;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setupInitialState() {
        logRepository = new FileBasedLogRepository(NODE_ID, objectMapper);
        deleteFiles();
    }

    @Test
    void persistentStateShouldBeInitialisedWithDefaultValues() {
        assertAll(
                () -> assertEquals(0, logRepository.getCurrentTerm()),
                () -> assertEquals("", logRepository.getVotedFor()),
                () -> assertEquals(0, logRepository.getLog().size())
        );
    }

    @Test
    void shouldSaveStateAfterRestart() {
        int currentTerm = 2;
        String votedFor = "some-other-node";
        LogEntry entry = new LogEntry(new Command("x", "10"), 1);

        logRepository.setCurrentTerm(currentTerm);
        logRepository.setVotedFor(votedFor);
        logRepository.appendEntry(entry);

        // imitate jvm restart
        LogRepository newLogRepository = new FileBasedLogRepository(NODE_ID, objectMapper);

        assertAll(
                () -> assertEquals(currentTerm, newLogRepository.getCurrentTerm()),
                () -> assertEquals(votedFor, newLogRepository.getVotedFor()),
                () -> assertEquals(1, newLogRepository.getLog().size()),
                () -> assertEquals(entry, newLogRepository.getLog().iterator().next())
        );
    }

    @AfterEach
    void destroyInitialState() {
        deleteFiles();
    }

    private void deleteFiles() {
        logRepository.destroy();
    }
}
