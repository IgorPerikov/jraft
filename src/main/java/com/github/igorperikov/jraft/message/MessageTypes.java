package com.github.igorperikov.jraft.message;

public class MessageTypes {
    public static final String RAFT_INIT = "raft_init";
    public static final String ERROR = "error";
    public static final String WRITE = "write";
    public static final String WRITE_OK = "write_ok";
    public static final String READ = "read";
    public static final String READ_OK = "read_ok";
    public static final String CAS = "cas";
    public static final String CAS_OK = "cas_ok";
    public static final String DELETE = "delete";
    public static final String DELETE_OK = "delete_ok";
}
