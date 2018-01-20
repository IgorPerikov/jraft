package com.github.igorperikov.jraft.handler.client;

import com.github.igorperikov.jraft.handler.MessageHandler;
import com.github.igorperikov.jraft.infrastructure.MaelstromMessage;
import com.github.igorperikov.jraft.infrastructure.constants.MessageErrorCodes;
import com.github.igorperikov.jraft.infrastructure.constants.MessageFields;
import com.github.igorperikov.jraft.infrastructure.constants.MessageTypes;

public abstract class ClientMessageHandler implements MessageHandler {
    protected MaelstromMessage buildDenyResponse(MaelstromMessage request, int nextMessageId) {
        return MaelstromMessage.of(
                request.getSrc(),
                request.getDest(),
                MessageTypes.ERROR,
                nextMessageId,
                MessageFields.BODY_MSG_ERROR_CODE, MessageErrorCodes.UNABLE_TO_SERVE,
                MessageFields.BODY_MSG_IN_REPLY_TO, request.getBody().get(MessageFields.BODY_MSG_ID)
        );
    }
}
