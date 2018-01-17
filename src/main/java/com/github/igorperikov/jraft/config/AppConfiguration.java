package com.github.igorperikov.jraft.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.igorperikov.jraft.Node;
import com.github.igorperikov.jraft.log.FileBasedLogRepository;
import com.github.igorperikov.jraft.log.LogRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfiguration {
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public LogRepository persistenceRepository(Node node, ObjectMapper objectMapper) {
        return new FileBasedLogRepository(node.getId(), objectMapper);
    }
}
