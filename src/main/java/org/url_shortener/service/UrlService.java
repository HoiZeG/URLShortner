package org.url_shortener.service;

import org.url_shortener.dto.UrlDto;

public interface UrlService {
    void shortenUrl(UrlDto urlDto);
}
