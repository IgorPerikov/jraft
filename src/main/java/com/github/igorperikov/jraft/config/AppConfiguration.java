package com.github.igorperikov.jraft.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.igorperikov.jraft.Node;
import com.github.igorperikov.jraft.persistence.FileBasedPersistenceService;
import com.github.igorperikov.jraft.persistence.PersistenceService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfiguration {
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public PersistenceService persistentService(Node node, ObjectMapper objectMapper) {
        return new FileBasedPersistenceService(node.getId(), objectMapper);
    }
}
