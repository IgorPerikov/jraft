package com.github.igorperikov.jraft.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.igorperikov.jraft.Node;
import com.github.igorperikov.jraft.persistent.FileBasedPersistentService;
import com.github.igorperikov.jraft.persistent.PersistentService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("maelstrom")
public class AppConfiguration {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public PersistentService persistentService(Node node, ObjectMapper objectMapper) {
        return new FileBasedPersistentService(node.getId(), objectMapper);
    }
}
