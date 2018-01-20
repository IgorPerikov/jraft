package com.github.igorperikov.jraft.log;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class LogEntry implements Serializable {
    private final Command command;
    private final long term;
}
