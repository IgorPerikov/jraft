package com.github.igorperikov.jraft.service.client;

import com.github.igorperikov.jraft.service.MessageErrorCodes;
import com.github.igorperikov.jraft.service.MessageFields;
import com.github.igorperikov.jraft.service.MessageHandler;
import com.github.igorperikov.jraft.service.MessageTypes;
import com.github.igorperikov.jraft.service.infrastructure.MaelstromMessage;

public abstract class ClientMessageHandler implements MessageHandler {
    protected MaelstromMessage buildDenyResponse(MaelstromMessage request) {
        return MaelstromMessage.of(
                request.getSrc(),
                request.getDest(),
                MessageFields.BODY_MSG_TYPE, MessageTypes.ERROR,
                MessageFields.BODY_MSG_ERROR_CODE, MessageErrorCodes.UNABLE_TO_SERVE,
                MessageFields.BODY_MSG_IN_REPLY_TO, request.getBody().get(MessageFields.BODY_MSG_ID)
        );
    }
}
