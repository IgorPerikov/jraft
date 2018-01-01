package com.github.igorperikov.jraft.persistence;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.igorperikov.jraft.domain.LogEntry;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;

import javax.annotation.Nonnull;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@AllArgsConstructor
public class FileBasedPersistenceService implements PersistenceService {
    private static final String JRAFT_STORAGE_PREFIX_NAME = "jraft";

    private static final String CURRENT_TERM_FILE_NAME = "term";
    private static final String VOTED_FOR_FILE_NAME = "voted-for";
    private static final String LOG_ENTRIES_FILE_NAME = "log";

    private static final int DEFAULT_CURRENT_TERM = 0;
    private static final String DEFAULT_VOTED_FOR = "";
    private static final List<LogEntry> DEFAULT_LOG = Collections.emptyList();

    private final String nodeId;
    private final ObjectMapper objectMapper;

    // latest term server has seen(init to 0 on first boot, inc monotonically)
    @Override
    public int getCurrentTerm() {
        Path directoryPath = buildDirectoryPath();
        directoryPath.toFile().mkdirs();
        Path filePath = buildFilePath(CURRENT_TERM_FILE_NAME);
        File file = filePath.toFile();
        try {
            if (file.exists()) {
                List<String> strings = Files.readAllLines(filePath);
                return Integer.valueOf(strings.get(0));
            } else {
                writeToFile(filePath, String.valueOf(DEFAULT_CURRENT_TERM));
                return DEFAULT_CURRENT_TERM;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setCurrentTerm(int currentTerm) {
        try {
            Path filePath = buildFilePath(CURRENT_TERM_FILE_NAME);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
            writeToFile(filePath, String.valueOf(currentTerm));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // candidateId that received vote in currentTerm(or null if none)
    @Override
    public String getVotedFor() {
        Path directoryPath = buildDirectoryPath();
        directoryPath.toFile().mkdirs();
        Path filePath = buildFilePath(VOTED_FOR_FILE_NAME);
        File file = filePath.toFile();
        try {
            if (file.exists()) {
                List<String> strings = Files.readAllLines(filePath);
                return strings.get(0);
            } else {
                writeToFile(filePath, DEFAULT_VOTED_FOR);
                return DEFAULT_VOTED_FOR;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setVotedFor(String votedFor) {
        try {
            Path filePath = buildFilePath(VOTED_FOR_FILE_NAME);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
            writeToFile(filePath, votedFor);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // log entries, each entry contains command for state machine, and term when entry was received by leader(first index is 1)
    @Nonnull
    @Override
    public List<LogEntry> getLog() {
        Path directoryPath = buildDirectoryPath();
        directoryPath.toFile().mkdirs();
        Path filePath = buildFilePath(LOG_ENTRIES_FILE_NAME);
        File file = filePath.toFile();
        try {
            if (file.exists()) {
                return objectMapper.readValue(filePath.toFile(), new TypeReference<List<LogEntry>>(){});
            } else {
                writeToFile(filePath, objectMapper.writeValueAsString(DEFAULT_LOG));
                return DEFAULT_LOG;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void appendEntry(LogEntry entry) {
        try {
            Path filePath = buildFilePath(LOG_ENTRIES_FILE_NAME);
            List<LogEntry> list;
            File resultFile = filePath.toFile();
            if (Files.exists(filePath)) {
                list = objectMapper.readValue(resultFile, new TypeReference<List<LogEntry>>(){});
                list.add(entry);
                Files.delete(filePath);
            } else {
                list = Lists.newArrayList(entry);
            }
            resultFile.createNewFile();
            objectMapper.writeValue(resultFile, list);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void destroy() {
        Stream.of(
                buildFilePath(CURRENT_TERM_FILE_NAME),
                buildFilePath(VOTED_FOR_FILE_NAME),
                buildFilePath(LOG_ENTRIES_FILE_NAME)
        ).filter(Files::exists)
                .forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    /**
     * assumes that file does not exists
     */
    private void writeToFile(Path filePath, String value) throws IOException {
        File file = filePath.toFile();
        file.createNewFile();
        BufferedWriter bufferedWriter = Files.newBufferedWriter(filePath);
        bufferedWriter.write(value);
        bufferedWriter.close();
    }

    private Path buildDirectoryPath() {
        return Paths.get(System.getProperty("java.io.tmpdir"), JRAFT_STORAGE_PREFIX_NAME, nodeId);
    }

    private Path buildFilePath(String fileName) {
        return Paths.get(buildDirectoryPath().toString(), fileName);
    }
}
