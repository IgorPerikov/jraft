package com.github.igorperikov.jraft.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@AllArgsConstructor
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
                        e.printStackTrace(System.err);
                    }
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
