package com.github.igorperikov.jraft.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.igorperikov.jraft.Node;
import com.github.igorperikov.jraft.persistence.FileBasedPersistenceRepository;
import com.github.igorperikov.jraft.persistence.PersistenceRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfiguration {
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public PersistenceRepository persistenceRepository(Node node, ObjectMapper objectMapper) {
        return new FileBasedPersistenceRepository(node.getId(), objectMapper);
    }
}
