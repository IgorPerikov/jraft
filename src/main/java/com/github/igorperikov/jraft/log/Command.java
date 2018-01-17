package com.github.igorperikov.jraft.log;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class Command {
    private final String key;
    private final String value;
}
