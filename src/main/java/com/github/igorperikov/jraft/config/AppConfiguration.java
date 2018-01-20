package com.github.igorperikov.jraft.config;

import com.aerospike.client.AerospikeClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.igorperikov.jraft.Node;
import com.github.igorperikov.jraft.log.AerospikeLogRepository;
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
    public LogRepository persistenceRepository(AerospikeClient aerospikeClient, Node node) {
        return new AerospikeLogRepository(aerospikeClient, node);
    }

    @Bean(destroyMethod = "close")
    public AerospikeClient aerospikeClient() {
        return new AerospikeClient("127.0.0.1", 3000);
    }
}
