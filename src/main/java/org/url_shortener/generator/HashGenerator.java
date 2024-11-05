package org.url_shortener.generator;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.url_shortener.repository.HashRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HashGenerator {
    @Value("${hash.n}")
    private int n;

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Async("asyncExecutor")
    public void generateBatch() {
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(n);
        base62Encoder.encode(uniqueNumbers).thenAccept(hashes ->
                hashes.forEach(hashRepository::save)
        );
    }

}
