package org.url_shortener.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.url_shortener.dto.UrlDto;
import org.url_shortener.dto.UrlResponse;
import org.url_shortener.exceptions.DataValidationException;
import org.url_shortener.service.UrlService;

@RestController
@RequestMapping(("/api/v1"))
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;

    @PostMapping("/url")
    public ResponseEntity<UrlResponse> shortenUrl(@RequestBody UrlDto urlDto) {
        validateUrl(urlDto.getUrl());
        UrlDto urlDto1 = urlService.shortenUrl(urlDto);
        return ResponseEntity.ok(new UrlResponse(urlDto1.getUrl()));
    }

    @GetMapping("/{hash}")
    public ResponseEntity<UrlResponse> getUrl(@PathVariable String hash) {
        UrlDto urlDto = urlService.getNormalUrl(hash);
        return ResponseEntity.ok(new UrlResponse(urlDto.getUrl()));
    }

    private void validateUrl(String url) {
        if (!url.contains("https://")) {
            throw new DataValidationException("Invalid URL: " + url);
        }
    }
}
