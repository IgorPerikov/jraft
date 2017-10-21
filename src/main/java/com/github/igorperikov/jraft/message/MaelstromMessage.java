package com.github.igorperikov.jraft.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@AllArgsConstructor
@Getter
public class MaelstromMessage {
    private final String src;
    private final String dest;
    private Map<String, Object> body;
}
