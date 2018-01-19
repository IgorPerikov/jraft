package com.github.igorperikov.jraft.log;

import com.aerospike.client.AerospikeClient;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;

import static org.junit.Assert.assertEquals;

// TODO: convert to integration
public class AerospikeLogRepositoryTest {
    private AerospikeLogRepository repository;

    @ClassRule
    public static GenericContainer aerospike =
            new GenericContainer("aerospike/aerospike-server:3.15.1.4")
                    .withExposedPorts(3000);

    @BeforeClass
    public static void waitAerospikeInitialization() throws InterruptedException {
        Thread.sleep(2000);
    }

    @Before
    public void beforeEach() {
        String containerIpAddress = aerospike.getContainerIpAddress();
        Integer mappedPort = aerospike.getMappedPort(3000);
        AerospikeClient client = new AerospikeClient(containerIpAddress, mappedPort);
        repository = new AerospikeLogRepository(client, "n1");
    }

    @Test
    public void should_correctly_set_and_read_term() {
        assertEquals(0, repository.getCurrentTerm());
        int newTerm = 2;
        repository.setCurrentTerm(newTerm);
        assertEquals(newTerm, repository.getCurrentTerm());
    }

    @Test
    public void should_correctly_set_and_read_voted_for() {
        assertEquals("", repository.getVotedFor());
        String votedFor = "n2";
        repository.setVotedFor(votedFor);
        assertEquals(votedFor, repository.getVotedFor());
    }

    @Test
    public void should_correctly_append_entries_to_log() {
        assertEquals(0, repository.getLog().size());
        LogEntry entry1 = new LogEntry(new Command("k1", "v1"), 0);
        LogEntry entry2 = new LogEntry(new Command("k2", "v2"), 0);
        repository.appendEntry(entry1);
        repository.appendEntry(entry2);
        assertEquals(Lists.newArrayList(entry1, entry2), repository.getLog());
    }
}
