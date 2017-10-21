package com.github.igorperikov.jraft;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ErrorCode {
    // network errors
    CODE_0("The request timed out"),
    CODE_1("The node a message was sent to does not exist"),

    // generic errors
    CODE_10("The given operation is not supported"),
    CODE_11("This node is temporarily unable to serve this type of request"),

    // key-value errors
    CODE_20("The given key does not exist"),
    CODE_21("The given key already exists"),
    CODE_22("A precondition (e.g. a compare-and-set comparison) failed");

    @Getter
    private String details;
}
