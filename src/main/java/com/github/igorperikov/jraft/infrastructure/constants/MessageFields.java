package com.github.igorperikov.jraft.infrastructure.constants;

public class MessageFields {
    public static final String SRC = "src";
    public static final String DEST = "dest";
    public static final String BODY = "body";

    public static final String BODY_MSG_TYPE = "type";
    public static final String BODY_MSG_ID = "msg_id";
    public static final String BODY_MSG_IN_REPLY_TO = "in_reply_to";

    // specific constants for concrete message types
    public static final String BODY_MSG_RAFT_INIT_NODE_ID = "node_id";
    public static final String BODY_MSG_RAFT_INIT_NODE_IDS = "node_ids";

    public static final String BODY_MSG_REQUEST_VOTE_CANDIDATE_TERM = "candidate_term";
    public static final String BODY_MSG_REQUEST_VOTE_LAST_LOG_INDEX = "last_log_index";
    public static final String BODY_MSG_REQUEST_VOTE_LAST_LOG_TERM = "last_log_term";

    public static final String BODY_MSG_ERROR_CODE = "code";

    public static final String BODY_MSG_CLIENT_KEY = "key";
    public static final String BODY_MSG_CLIENT_READ_VALUE = "value";
}
