package com.github.igorperikov.jraft.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class MessageWriter {
    private final ObjectMapper objectMapper;

    @Autowired
    public MessageWriter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void write(MaelstromMessage response) {
        log.info("Writing message: {}", response);
        try {
            objectMapper.writeValue(System.out, response);
        } catch (IOException e) {
            log.error("", e);
        }
    }
}
