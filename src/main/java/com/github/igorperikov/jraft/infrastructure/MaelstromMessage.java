package com.github.igorperikov.jraft.infrastructure;

import com.github.igorperikov.jraft.infrastructure.constants.MessageFields;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@Getter
@ToString
public class MaelstromMessage {
    private final String src;
    private final String dest;
    private Map<String, Object> body;

    public static MaelstromMessage of(String src, String dest, String type, int messageId) {
        Map<String, Object> body = new HashMap<>();
        body.put(MessageFields.BODY_MSG_TYPE, type);
        body.put(MessageFields.BODY_MSG_ID, messageId);
        return new MaelstromMessage(src, dest, body);
    }

    public static MaelstromMessage of(String src, String dest, String type, int messageId, String k1, Object v1) {
        MaelstromMessage message = of(src, dest, type, messageId);
        message.getBody().put(k1, v1);
        return message;
    }

    public static MaelstromMessage of(
            String src,
            String dest,
            String type,
            int messageId,
            String k1,
            Object v1,
            String k2,
            Object v2
    ) {
        MaelstromMessage message = of(src, dest, type, messageId, k1, v1);
        message.getBody().put(k2, v2);
        return message;
    }

    public static MaelstromMessage of(
            String src,
            String dest,
            String type,
            int messageId,
            String k1,
            Object v1,
            String k2,
            Object v2,
            String k3,
            Object v3
    ) {
        MaelstromMessage message = of(src, dest, type, messageId, k1, v1, k2, v2);
        message.getBody().put(k3, v3);
        return message;
    }

    public static MaelstromMessage of(
            String src,
            String dest,
            String type,
            int messageId,
            String k1,
            Object v1,
            String k2,
            Object v2,
            String k3,
            Object v3,
            String k4,
            Object v4
    ) {
        MaelstromMessage message = of(src, dest, type, messageId, k1, v1, k2, v2, k3, v3);
        message.getBody().put(k4, v4);
        return message;
    }
}
