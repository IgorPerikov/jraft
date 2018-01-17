package com.github.igorperikov.jraft.consensus;

import com.github.igorperikov.jraft.infrastructure.MaelstromMessage;
import com.github.igorperikov.jraft.infrastructure.constants.MessageFields;
import com.github.igorperikov.jraft.infrastructure.constants.MessageTypes;

import java.util.List;

// TODO: is it correct place for this class?
class RaftInitMessageBuilder {
    static MaelstromMessage buildRaftInitMessage(String nodeId, List<String> nodeIds, Integer msgId) {
        return MaelstromMessage.of(
                MessageFields.SRC,
                nodeId,
                MessageFields.BODY_MSG_TYPE, MessageTypes.RAFT_INIT,
                MessageFields.BODY_MSG_RAFT_INIT_NODE_ID, nodeId,
                MessageFields.BODY_MSG_RAFT_INIT_NODE_IDS, nodeIds,
                MessageFields.BODY_MSG_ID, msgId
        );
    }
}
