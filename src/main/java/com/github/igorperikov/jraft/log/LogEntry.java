package com.github.igorperikov.jraft.log;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class LogEntry {
    private final Command command;
    private final int term;
}
