package com.github.igorperikov.jraft.service.client;

import com.github.igorperikov.jraft.Node;
import com.github.igorperikov.jraft.NodeState;
import com.github.igorperikov.jraft.domain.Command;
import com.github.igorperikov.jraft.domain.LogEntry;
import com.github.igorperikov.jraft.persistence.FileBasedPersistenceService;
import com.github.igorperikov.jraft.persistence.PersistenceService;
import com.github.igorperikov.jraft.service.MessageErrorCodes;
import com.github.igorperikov.jraft.service.MessageFields;
import com.github.igorperikov.jraft.service.MessageTypes;
import com.github.igorperikov.jraft.service.infrastructure.MaelstromMessage;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReadMessageHandlerTest {
    private ReadMessageHandler handler;

    private final String existingKey = "existing";
    private final String missingKey = "missing";

    private final String lastKnownValue = "value";

    @BeforeEach
    void setup() {
        Node node = Mockito.mock(Node.class);
        Mockito.when(node.getNodeState()).thenReturn(NodeState.LEADER);

        PersistenceService persistenceService = Mockito.mock(FileBasedPersistenceService.class);
        LogEntry logEntry1 = new LogEntry(new Command(existingKey, "42"), 0);
        LogEntry logEntry2 = new LogEntry(new Command(existingKey, "42"), 0);
        LogEntry logEntry3 = new LogEntry(new Command(existingKey, lastKnownValue), 0);
        List<LogEntry> mockedLog = Lists.newArrayList(logEntry1, logEntry2, logEntry3);
        Mockito.when(persistenceService.getLog()).thenReturn(mockedLog);
        Mockito.when(persistenceService.getLastKnownCommand(Mockito.any())).thenCallRealMethod();

        handler = new ReadMessageHandler(node, persistenceService);
    }

    @Test
    void should_return_last_known_command() {
        MaelstromMessage request = MaelstromMessage.of(
                "src",
                "dst",
                MessageFields.BODY_MSG_TYPE, MessageTypes.READ,
                MessageFields.BODY_MSG_CLIENT_KEY, existingKey
        );
        MaelstromMessage response = handler.handle(request);
        assertAll(
                () -> assertEquals(MessageTypes.READ_OK, response.getBody().get(MessageFields.BODY_MSG_TYPE)),
                () -> assertEquals(lastKnownValue, response.getBody().get(MessageFields.BODY_MSG_CLIENT_READ_VALUE))
        );
    }

    @Test
    void should_correctly_return_error_response_if_key_does_not_exist() {
        MaelstromMessage request = MaelstromMessage.of(
                "src",
                "dst",
                MessageFields.BODY_MSG_TYPE, MessageTypes.READ,
                MessageFields.BODY_MSG_CLIENT_KEY, missingKey
        );
        MaelstromMessage response = handler.handle(request);
        assertAll(
                () -> assertEquals(MessageTypes.ERROR, response.getBody().get(MessageFields.BODY_MSG_TYPE)),
                () -> assertEquals(MessageErrorCodes.KEY_DOESNT_EXIST, response.getBody().get(MessageFields.BODY_MSG_ERROR_CODE)),
                () -> assertNull(response.getBody().get(MessageFields.BODY_MSG_CLIENT_READ_VALUE))
        );
    }
}
