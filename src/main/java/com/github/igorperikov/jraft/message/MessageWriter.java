package com.github.igorperikov.jraft.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@AllArgsConstructor
@Slf4j
public class MessageWriter {
    private final ObjectMapper objectMapper;

    public void write(MaelstromMessage response) {
        try {
            objectMapper.writeValue(System.out, response);
        } catch (IOException e) {
            log.error("", e);
        }
    }
}
