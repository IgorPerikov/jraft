package com.github.igorperikov.jraft;

import lombok.extern.slf4j.Slf4j;

// TODO: is it good enough?
@Slf4j
public class ExecutorUtility {
    public static void runWithExceptions(Runnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            log.error("Error", e);
        }
    }
}
