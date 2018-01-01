package com.github.igorperikov.jraft.service;

public class ErrorCodes {
    // network errors
    public static final int REQUEST_TIMED_OUT = 0;
    public static final int NODE_DOESNT_EXISTS = 1;

    // generic errors
    public static final int OPERATION_IS_NOT_SUPPORTED = 10;
    public static final int UNABLE_TO_SERVE = 11; // Or its not a leader

    // kv errors
    public static final int KEY_DOESNT_EXISTS = 20;
    public static final int KEY_ALREADY_EXISTS = 21;
    public static final int PRECONDITION_FAILED = 22;
}
