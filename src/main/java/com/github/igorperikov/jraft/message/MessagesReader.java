package com.github.igorperikov.jraft.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor
@Slf4j
public class MessagesReader {
    private final ObjectMapper objectMapper;
    private final MessageDispatcher messageDispatcher;
    private final MessageValidator messageValidator;
    private final MessageWriter messageWriter;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @PostConstruct
    private void start() {
        executorService.execute(() -> {
            Scanner scanner = new Scanner(System.in);
            for (;;) {
                if (scanner.hasNext()) {
                    String jsonMessage = scanner.nextLine();
                    try {
                        readMessage(jsonMessage);
                    } catch (IOException e) {
                        log.error("", e);
                    }
                }
                try {
                    // TODO: reduce load on system
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    log.error("", e);
                }
            }
        });
    }

    private void readMessage(String jsonMessage) throws IOException {
        MaelstromMessage maelstromMessage = objectMapper.readValue(jsonMessage, MaelstromMessage.class);
        messageValidator.validate(maelstromMessage);
        MaelstromMessage response = messageDispatcher.dispatchMessage(maelstromMessage);
        messageWriter.write(response);
    }

    private void destroy() {
        executorService.shutdownNow();
    }
}
