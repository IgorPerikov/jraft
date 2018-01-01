package com.github.igorperikov.jraft.service.infrastructure;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;

@AllArgsConstructor
@Getter
@ToString
public class MaelstromMessage {
    private final String src;
    private final String dest;
    private Map<String, Object> body;
}
