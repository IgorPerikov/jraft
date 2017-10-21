package com.github.igorperikov.jraft.message;

import java.util.Objects;

public class MessageValidator {
    public void validate(MaelstromMessage message) {
        Objects.requireNonNull(message.getDest());
        Objects.requireNonNull(message.getBody());
        Objects.requireNonNull(message.getBody().get("type"));
    }
}
