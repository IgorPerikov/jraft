package com.github.igorperikov.jraft.message;

public class MessageKeys {
    /**
     * "msg_id"   An integer
     * "node_id"  A string identifying this node
     * "node_ids" An array of all node ids in the cluster, including this node
     */
    public static final String MSG_ID_KEY = "msg_id";
    public static final String NODE_ID_KEY = "node_id";
    public static final String NODE_IDS_KEY = "node_ids";
}
