package com.github.igorperikov.jraft.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.igorperikov.jraft.Node;
import com.github.igorperikov.jraft.message.MessageDispatcher;
import com.github.igorperikov.jraft.message.MessageValidator;
import com.github.igorperikov.jraft.message.MessageWriter;
import com.github.igorperikov.jraft.message.MessagesReader;
import com.github.igorperikov.jraft.message.handler.*;
import com.github.igorperikov.jraft.persistent.FileBasedPersistentService;
import com.github.igorperikov.jraft.persistent.PersistentService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfiguration {
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public PersistentService persistentService(Node node, ObjectMapper objectMapper) {
        return new FileBasedPersistentService(node.getId(), objectMapper);
    }

    @Bean(destroyMethod = "destroy")
    public MessagesReader incomingMessagesReader(
            ObjectMapper objectMapper,
            MessageDispatcher messageDispatcher,
            MessageValidator messageValidator,
            MessageWriter messageWriter
    ) {
        return new MessagesReader(objectMapper, messageDispatcher, messageValidator, messageWriter);
    }

    @Bean
    public MessageWriter messageWriter(ObjectMapper objectMapper) {
        return new MessageWriter(objectMapper);
    }

    @Bean
    public MessageDispatcher messageDispatcher(
            RaftInitHandler raftInitHandler,
            WriteMessageHandler writeMessageHandler,
            ReadMessageHandler readMessageHandler,
            CasMessageHandler casMessageHandler,
            DeleteMessageHandler deleteMessageHandler
    ) {
        return new MessageDispatcher(
                raftInitHandler,
                writeMessageHandler,
                readMessageHandler,
                casMessageHandler,
                deleteMessageHandler
        );
    }

    @Bean
    public MessageValidator messageValidator() {
        return new MessageValidator();
    }

    @Bean
    public RaftInitHandler raftInitHandler(Node node) {
        return new RaftInitHandler(node);
    }

    @Bean
    public WriteMessageHandler writeMessageHandler(Node node) {
        return new WriteMessageHandler(node);
    }

    @Bean
    public ReadMessageHandler readMessageHandler(Node node) {
        return new ReadMessageHandler(node);
    }

    @Bean
    public CasMessageHandler casMessageHandler(Node node) {
        return new CasMessageHandler(node);
    }

    @Bean
    public DeleteMessageHandler deleteMessageHandler(Node node) {
        return new DeleteMessageHandler(node);
    }
}
