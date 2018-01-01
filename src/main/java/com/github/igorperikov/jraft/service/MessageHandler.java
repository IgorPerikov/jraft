package com.github.igorperikov.jraft.service;

import com.github.igorperikov.jraft.service.infrastructure.MaelstromMessage;

import java.util.HashMap;
import java.util.Map;

public interface MessageHandler {
    MaelstromMessage handle(MaelstromMessage maelstromMessage);

    default MaelstromMessage buildDenyResponse(MaelstromMessage request) {
        Map<String, Object> body = new HashMap<>();
        body.put(MessageFields.BODY_MSG_TYPE, MessageTypes.ERROR);
        body.put(MessageFields.BODY_MSG_ERROR_CODE, ErrorCodes.UNABLE_TO_SERVE);
        body.put(MessageFields.BODY_MSG_IN_REPLY_TO, request.getBody().get(MessageFields.BODY_MSG_ID));
        return new MaelstromMessage(request.getSrc(), request.getDest(), body);
    }
}
