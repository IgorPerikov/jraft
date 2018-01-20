package com.github.igorperikov.jraft.handler.client;

import com.github.igorperikov.jraft.Node;
import com.github.igorperikov.jraft.NodeState;
import com.github.igorperikov.jraft.consensus.StateMachineService;
import com.github.igorperikov.jraft.infrastructure.MaelstromMessage;
import com.github.igorperikov.jraft.infrastructure.constants.MessageErrorCodes;
import com.github.igorperikov.jraft.infrastructure.constants.MessageFields;
import com.github.igorperikov.jraft.infrastructure.constants.MessageTypes;
import com.github.igorperikov.jraft.log.Command;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class ReadMessageHandlerTest {
    private ReadMessageHandler handler;

    private final String existingKey = "existing";
    private final String missingKey = "missing";

    private final String value = "value";

    @BeforeEach
    void setup() {
        Node node = Mockito.mock(Node.class);
        Mockito.when(node.getNodeState()).thenReturn(NodeState.LEADER);

        StateMachineService stateMachineService = new StateMachineService();
        stateMachineService.applyCommand(new Command(existingKey, value));

        handler = new ReadMessageHandler(node, stateMachineService);
    }

    @Test
    void should_return_last_known_command() {
        MaelstromMessage request = MaelstromMessage.of(
                "src",
                "dst",
                MessageTypes.READ,
                42,
                MessageFields.BODY_MSG_CLIENT_KEY, existingKey
        );
        MaelstromMessage response = handler.handle(request);
        assertAll(
                () -> assertEquals(MessageTypes.READ_OK, response.getBody().get(MessageFields.BODY_MSG_TYPE)),
                () -> assertEquals(value, response.getBody().get(MessageFields.BODY_MSG_CLIENT_READ_VALUE))
        );
    }

    @Test
    void should_correctly_return_error_response_if_key_does_not_exist() {
        MaelstromMessage request = MaelstromMessage.of(
                "src",
                "dst",
                MessageTypes.READ,
                42,
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
