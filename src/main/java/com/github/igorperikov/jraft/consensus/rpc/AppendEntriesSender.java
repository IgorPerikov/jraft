package com.github.igorperikov.jraft.consensus.rpc;

import com.github.igorperikov.jraft.infrastructure.MessageWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AppendEntriesSender {
    private final MessageWriter messageWriter;

    @Autowired
    public AppendEntriesSender(MessageWriter messageWriter) {
        this.messageWriter = messageWriter;
    }
}
