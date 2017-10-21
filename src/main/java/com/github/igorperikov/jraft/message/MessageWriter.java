package com.github.igorperikov.jraft.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;

import java.io.IOException;

@AllArgsConstructor
public class MessageWriter {
    private final ObjectMapper objectMapper;

    public void write(MaelstromMessage response) {
        try {
            objectMapper.writeValue(System.out, response);
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }
}
