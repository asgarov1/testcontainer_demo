package com.asgarov.domain;

import org.junit.Before;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.OutputFrame;
import org.testcontainers.containers.output.ToStringConsumer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
public class CachedPriceTest {
    private static final DockerImageName imageName = DockerImageName.parse("redis:6.2.3-alpine");

    @Container
    public static GenericContainer redis = new GenericContainer(imageName).withExposedPorts(6379);

    public static ToStringConsumer consumer = new ToStringConsumer();

    @BeforeAll
    public static void setUp() {
        redis.followOutput(consumer,
                OutputFrame.OutputType.STDOUT,
                OutputFrame.OutputType.STDERR);
    }

    @AfterAll
    public static void tearDown() throws IOException {
        Path log = Path.of("./testcontainers.log");
        byte[] bytes = consumer.toUtf8String().getBytes();
        Files.write(log, bytes, StandardOpenOption.CREATE);
    }

    @Test
    public void cached() {
        var jedis = getJedisConnection();
        jedis.set("price", "20");

        CachedPrice price = new CachedPrice(new StubPrice(), jedis);
        BigDecimal result = price.getInitialPrice();

        assertEquals(new BigDecimal("20"), result);
    }

    @Test
    public void noCache() {
        var jedis = getJedisConnection();
        jedis.del("price");

        CachedPrice price = new CachedPrice(new StubPrice(), jedis);
        BigDecimal result = price.getInitialPrice();

        assertEquals(new BigDecimal("10"), result);
    }

    private Jedis getJedisConnection() {
        HostAndPort hostAndPort = new HostAndPort(
                redis.getHost(),
                redis.getFirstMappedPort());
        return new Jedis(hostAndPort);
    }


}