package org.url_shortener.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.url_shortener.dto.UrlDto;
import org.url_shortener.exceptions.DataValidationException;
import org.url_shortener.service.UrlService;

@RestController("/api/v1")
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;

    @PostMapping("/url ")
    public void shortenUrl(@RequestBody UrlDto urlDto) {
        validateUrl(urlDto.getUrl());
        urlService.shortenUrl(urlDto);
    }

    private void validateUrl(String url) {
        if(!url.contains("https://")) {
        throw new DataValidationException("Invalid URL: " + url);
        }
    }
}
