package com.github.igorperikov.jraft.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.igorperikov.jraft.ExecutorUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class MessagesReader {
    private final Scanner scanner = new Scanner(System.in);
    private final ScheduledExecutorService readExecutor = Executors.newSingleThreadScheduledExecutor();

    private final ObjectMapper objectMapper;
    private final MessageDispatcher messageDispatcher;
    private final MessageValidator messageValidator;
    private final MessageWriter messageWriter;

    public MessagesReader(
            ObjectMapper objectMapper,
            MessageDispatcher messageDispatcher,
            MessageValidator messageValidator,
            MessageWriter messageWriter
    ) {
        this.objectMapper = objectMapper;
        this.messageDispatcher = messageDispatcher;
        this.messageValidator = messageValidator;
        this.messageWriter = messageWriter;
    }

    @PostConstruct
    private void start() {
        readExecutor.scheduleWithFixedDelay(
                () -> ExecutorUtility.runWithExceptions(() -> {
                    if (scanner.hasNext()) {
                        String jsonMessage = scanner.nextLine();
                        try {
                            readMessage(jsonMessage);
                        } catch (IOException e) {
                            log.error("", e);
                        }
                    }
                }),
                20,
                20,
                TimeUnit.MILLISECONDS
        );
    }

    private void readMessage(String jsonMessage) throws IOException {
        MaelstromMessage maelstromMessage = objectMapper.readValue(jsonMessage, MaelstromMessage.class);
        messageValidator.validate(maelstromMessage);
        MaelstromMessage response = messageDispatcher.dispatchMessage(maelstromMessage);
        if (response != null) {
            messageWriter.write(response);
        }
    }

    @PreDestroy
    private void destroy() {
        readExecutor.shutdownNow();
    }
}
