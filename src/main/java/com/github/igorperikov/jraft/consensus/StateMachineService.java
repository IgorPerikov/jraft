package com.github.igorperikov.jraft.consensus;

import com.github.igorperikov.jraft.log.Command;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class StateMachineService {
    private final Map<String, String> currentState = new HashMap<>();

    public void applyCommand(Command command) {
        if (command.getValue() == null) {
            currentState.remove(command.getKey());
        } else {
            currentState.put(command.getKey(), command.getValue());
        }
    }

    public Optional<String> get(String key) {
        return Optional.ofNullable(currentState.get(key));
    }
}
