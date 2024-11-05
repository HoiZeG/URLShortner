package org.url_shortener.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.url_shortener.dto.UrlDto;
import org.url_shortener.entity.Url;
import org.url_shortener.hash.HashCache;
import org.url_shortener.repository.UrlRedisRepository;
import org.url_shortener.repository.UrlRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {
    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlRedisRepository urlRedisRepository;

    @Transactional
    @Override
    public UrlDto shortenUrl(UrlDto urlDto) {
        String normalizedUrl = urlDto.getUrl();
        String hash = hashCache.getHash();
        Url url = new Url(hash, normalizedUrl, LocalDateTime.now());
        urlRepository.save(url);
        urlRedisRepository.save(hash, url);
        UrlDto urlDto1 = new UrlDto();
        urlDto1.setUrl(String.format("http://localhost:9080/%s", hash));
        return urlDto1;
    }

    public UrlDto getNormalUrl(String hash) {
        Optional<Url> urlFromRedis = urlRedisRepository.find(hash);
        if (urlFromRedis.isPresent()) {
            return new UrlDto(urlFromRedis.get().getUrl());
        } else {
            Url urlFromDB = urlRepository.findByHash(hash).orElseThrow(() -> new RuntimeException("Hash not found"));
            return new UrlDto(urlFromDB.getUrl());
        }
    }
}
