package com.github.igorperikov.jraft.service;

public class MessageFields {
    public static final String SRC = "src";
    public static final String DEST = "dest";
    public static final String BODY = "body";

    public static final String BODY_MSG_TYPE = "type";
    public static final String BODY_MSG_ID = "msg_id";
    public static final String BODY_MSG_IN_REPLY_TO = "in_reply_to";

    public static final String BODY_MSG_RAFT_INIT_NODE_ID = "node_id";
    public static final String BODY_MSG_RAFT_INIT_NODE_IDS = "node_ids";

    public static final String BODY_MSG_ERROR_CODE = "code";
}
