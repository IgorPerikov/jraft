package com.github.igorperikov.jraft.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;

@AllArgsConstructor
public class IncomingMessagesReader {
    private final ObjectMapper objectMapper;
    private final ExecutorService executorService;
    private final MessageDispatcher messageDispatcher;

    @PostConstruct
    private void start() {
        executorService.execute(() -> {
            Scanner scanner = new Scanner(System.in);
            for (;;) {
                if (scanner.hasNext()) {
                    String jsonMessage = scanner.nextLine();
                    try {
                        MaelstromMessage maelstromMessage = objectMapper.readValue(jsonMessage, MaelstromMessage.class);
                        messageDispatcher.dispatchMessage(maelstromMessage);
                    } catch (IOException e) {
                        e.printStackTrace(System.err);
                    }
                }
            }
        });
    }

    private void destroy() {
        executorService.shutdownNow();
    }
}
