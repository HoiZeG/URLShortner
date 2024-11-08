package org.url_shortener.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.url_shortener.dto.UrlDto;
import org.url_shortener.entity.Url;
import org.url_shortener.generator.HashGenerator;
import org.url_shortener.repository.UrlRepository;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureMockMvc
class UrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UrlController urlController;

    @Autowired
    private HashGenerator hashGenerator;

    @Autowired
    private UrlRepository urlRepository;

    @Container
    public static PostgreSQLContainer<?> POSTGRESQL_CONTAINER
            = new PostgreSQLContainer<>("postgres:13.3");

    @Container
    public static final RedisContainer REDIS_CONTAINER =
            new RedisContainer(DockerImageName.parse("redis/redis-stack:latest"));

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);

        registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
        registry.add("spring.data.redis.port", () -> REDIS_CONTAINER.getMappedPort(6379));

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeAll
    static void beforeAll() {
        REDIS_CONTAINER.start();
        POSTGRESQL_CONTAINER.start();
    }

    @AfterAll
    static void afterAll() {
        REDIS_CONTAINER.stop();
        POSTGRESQL_CONTAINER.stop();
    }

    @Test
    void testShortenUrl_InternalServerError() throws Exception {
        mockMvc.perform(
                post("/api/v1/url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content("{}")
        ).andExpect(status().isInternalServerError());
    }

    @Test
    void testShortenUrl() throws Exception {
        hashGenerator.generateBatch();
        mockMvc.perform(
                post("/api/v1/url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(UrlDto.builder()
                                .url("https://google.com")
                                .build()))
        ).andExpect(status().isOk());
    }

    @Test
    void getUrl() throws Exception {
        Url url = Url.builder()
                .url("https://google.com")
                .hash("1")
                .createdAt(LocalDateTime.now())
                .build();
        urlRepository.save(url);
        MvcResult result = mockMvc.perform(
                get("/api/v1/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content("{\"url\":\"https://faang.school/1\"}")
        ).andExpect(status().isOk())
                .andReturn();
        String originUrl = String.format("{\"url\":\"%s\"}", url.getUrl());
        String resultUrl = result.getResponse().getContentAsString();
        Assertions.assertEquals(originUrl, resultUrl);
    }
}