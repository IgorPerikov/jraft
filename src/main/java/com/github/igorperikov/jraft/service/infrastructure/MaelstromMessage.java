package com.github.igorperikov.jraft.service.infrastructure;

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

    public static MaelstromMessage of(String src, String dest, String k1, Object v1) {
        Map<String, Object> body = new HashMap<>();
        body.put(k1, v1);
        return new MaelstromMessage(src, dest, body);
    }

    public static MaelstromMessage of(String src, String dest, String k1, Object v1, String k2, Object v2) {
        Map<String, Object> body = new HashMap<>();
        body.put(k1, v1);
        body.put(k2, v2);
        return new MaelstromMessage(src, dest, body);
    }

    public static MaelstromMessage of(
            String src,
            String dest,
            String k1,
            Object v1,
            String k2,
            Object v2,
            String k3,
            Object v3
    ) {
        Map<String, Object> body = new HashMap<>();
        body.put(k1, v1);
        body.put(k2, v2);
        body.put(k3, v3);
        return new MaelstromMessage(src, dest, body);
    }

    public static MaelstromMessage of(
            String src,
            String dest,
            String k1,
            Object v1,
            String k2,
            Object v2,
            String k3,
            Object v3,
            String k4,
            Object v4
    ) {
        Map<String, Object> body = new HashMap<>();
        body.put(k1, v1);
        body.put(k2, v2);
        body.put(k3, v3);
        body.put(k4, v4);
        return new MaelstromMessage(src, dest, body);
    }

    public static MaelstromMessage of(
            String src,
            String dest,
            String k1,
            Object v1,
            String k2,
            Object v2,
            String k3,
            Object v3,
            String k4,
            Object v4,
            String k5,
            Object v5
    ) {
        Map<String, Object> body = new HashMap<>();
        body.put(k1, v1);
        body.put(k2, v2);
        body.put(k3, v3);
        body.put(k4, v4);
        body.put(k5, v5);
        return new MaelstromMessage(src, dest, body);
    }
}
