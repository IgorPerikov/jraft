package com.github.igorperikov.jraft.service.client;

import com.github.igorperikov.jraft.Node;
import com.github.igorperikov.jraft.NodeState;
import com.github.igorperikov.jraft.domain.Command;
import com.github.igorperikov.jraft.persistence.PersistenceService;
import com.github.igorperikov.jraft.service.MessageErrorCodes;
import com.github.igorperikov.jraft.service.MessageFields;
import com.github.igorperikov.jraft.service.MessageTypes;
import com.github.igorperikov.jraft.service.infrastructure.MaelstromMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ReadMessageHandler extends ClientMessageHandler {
    private final Node node;
    private final PersistenceService persistenceService;

    @Autowired
    public ReadMessageHandler(Node node, PersistenceService persistenceService) {
        this.node = node;
        this.persistenceService = persistenceService;
    }

    @Override
    public MaelstromMessage handle(MaelstromMessage request) {
        log.info("Got message {}", request);
        if (node.getNodeState() == NodeState.LEADER) {
            String key = (String) request.getBody().get(MessageFields.BODY_MSG_CLIENT_KEY);
            return persistenceService.getLastKnownCommand(key)
                    .map(command -> buildOkResponse(request, command))
                    .orElseGet(() -> buildKeyDoesNotExistResponse(request));
        } else {
            return buildDenyResponse(request);
        }
    }

    private MaelstromMessage buildOkResponse(MaelstromMessage request, Command command) {
        return MaelstromMessage.of(
                request.getSrc(),
                request.getDest(),
                MessageFields.BODY_MSG_TYPE, MessageTypes.READ_OK,
                MessageFields.BODY_MSG_IN_REPLY_TO, request.getBody().get(MessageFields.BODY_MSG_ID),
                MessageFields.BODY_MSG_CLIENT_READ_VALUE, command.getValue()
        );
    }

    private MaelstromMessage buildKeyDoesNotExistResponse(MaelstromMessage request) {
        return MaelstromMessage.of(
                request.getSrc(),
                request.getDest(),
                MessageFields.BODY_MSG_TYPE, MessageTypes.ERROR,
                MessageFields.BODY_MSG_IN_REPLY_TO, request.getBody().get(MessageFields.BODY_MSG_ID),
                MessageFields.BODY_MSG_ERROR_CODE, MessageErrorCodes.KEY_DOESNT_EXIST
        );
    }
}
