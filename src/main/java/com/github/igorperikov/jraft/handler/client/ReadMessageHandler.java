package com.github.igorperikov.jraft.handler.client;

import com.github.igorperikov.jraft.Node;
import com.github.igorperikov.jraft.NodeState;
import com.github.igorperikov.jraft.consensus.StateMachineService;
import com.github.igorperikov.jraft.infrastructure.MaelstromMessage;
import com.github.igorperikov.jraft.infrastructure.constants.MessageErrorCodes;
import com.github.igorperikov.jraft.infrastructure.constants.MessageFields;
import com.github.igorperikov.jraft.infrastructure.constants.MessageTypes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ReadMessageHandler extends ClientMessageHandler {
    private final Node node;
    private final StateMachineService stateMachineService;

    @Autowired
    public ReadMessageHandler(Node node, StateMachineService stateMachineService) {
        this.node = node;
        this.stateMachineService = stateMachineService;
    }

    @Override
    public MaelstromMessage handle(MaelstromMessage request) {
        log.info("Got message {}", request);
        if (node.getNodeState() == NodeState.LEADER) {
            String key = (String) request.getBody().get(MessageFields.BODY_MSG_CLIENT_KEY);
            return stateMachineService.get(key)
                    .map(value -> buildOkResponse(request, value))
                    .orElseGet(() -> buildKeyDoesNotExistResponse(request));
        } else {
            return buildDenyResponse(request);
        }
    }

    private MaelstromMessage buildOkResponse(MaelstromMessage request, String value) {
        return MaelstromMessage.of(
                request.getSrc(),
                request.getDest(),
                MessageFields.BODY_MSG_TYPE, MessageTypes.READ_OK,
                MessageFields.BODY_MSG_IN_REPLY_TO, request.getBody().get(MessageFields.BODY_MSG_ID),
                MessageFields.BODY_MSG_CLIENT_READ_VALUE, value
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
