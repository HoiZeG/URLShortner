package org.url_shortener.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.url_shortener.entity.Hash;
import org.url_shortener.entity.Url;
import org.url_shortener.repository.HashRepository;
import org.url_shortener.repository.UrlRepository;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CleanerScheduler {
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;
    LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);

    @Scheduled(cron = "${scheduler.cron}")
    @Transactional
    public void clean() {
        List<Url> urlList = urlRepository.findUrlsOlderThanOneYear(oneYearAgo);
        List<String> hashStringList = urlList.stream().map(Url::getHash).toList();
        urlRepository.deleteAll(urlList);
        List<Hash> hashList = hashStringList.stream().map(Hash::new).toList();
        hashRepository.saveAll(hashList);
    }

}
