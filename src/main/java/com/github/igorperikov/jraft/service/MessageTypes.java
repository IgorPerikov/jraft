package com.github.igorperikov.jraft.service;

public class MessageTypes {
    // message types
    public static final String RAFT_INIT = "raft_init";
    public static final String RAFT_INIT_OK = "raft_init_ok";
    public static final String ERROR = "error";
    public static final String WRITE = "write";
    public static final String WRITE_OK = "write_ok";
    public static final String READ = "read";
    public static final String READ_OK = "read_ok";
    public static final String CAS = "cas";
    public static final String CAS_OK = "cas_ok";
    public static final String DELETE = "delete";
    public static final String DELETE_OK = "delete_ok";
    public static final String APPEND_ENTRIES_RPC = "append_entries";
    public static final String REQUEST_VOTE_RPC = "request_vote";
}
