package org.url_shortener.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.url_shortener.dto.UrlDto;
import org.url_shortener.entity.Hash;
import org.url_shortener.entity.Url;
import org.url_shortener.hash.HashCache;
import org.url_shortener.repository.UrlRedisRepository;
import org.url_shortener.repository.UrlRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceImplTest {

    @InjectMocks
    private UrlServiceImpl urlService;
    @Mock
    private HashCache hashCache;
    @Mock
    private UrlRepository urlRepository;
    @Mock
    private UrlRedisRepository urlRedisRepository;

    @Test
    void testShortenUrl_Success() {
        UrlDto urlDto = UrlDto.builder()
                .url("http://localhost:9080/shorten")
                .build();
        when(hashCache.getHash()).thenReturn("1v2b3n");

        UrlDto result = urlService.shortenUrl(urlDto);
        assertEquals("http://faang.school/1v2b3n", result.getUrl());
    }

    @Test
    void testGetNormalUrl_SuccessRedis() {
        Url url = Url.builder()
                .url("http://localhost:9080/shorten")
                .hash("1v2b3n")
                .createdAt(LocalDateTime.now())
                .build();

        Hash hash = new Hash("1v2b3n");
        when(urlRedisRepository.find("1v2b3n")).thenReturn(Optional.of(url));
        UrlDto result = urlService.getNormalUrl("1v2b3n");
        assertEquals("http://localhost:9080/shorten", result.getUrl());
    }

    @Test
    void testGetNormalUrl_SuccessBD() {
        Url url = Url.builder()
                .url("http://localhost:9080/shorten")
                .hash("1v2b3n")
                .createdAt(LocalDateTime.now())
                .build();

        Hash hash = new Hash("1v2b3n");
        when(urlRepository.findByHash("1v2b3n")).thenReturn(Optional.of(url));
        UrlDto result = urlService.getNormalUrl("1v2b3n");
        assertEquals("http://localhost:9080/shorten", result.getUrl());
    }

    @Test
    void testGetNormalUrl_HashNotFound() {
        assertThrows(EntityNotFoundException.class, () -> urlService.getNormalUrl("1v2b3n"));
    }
}