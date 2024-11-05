package org.url_shortener.service;

import org.url_shortener.dto.UrlDto;

public interface UrlService {
    UrlDto shortenUrl(UrlDto urlDto);
    UrlDto getNormalUrl(String hash);
}
