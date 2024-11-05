package org.url_shortener.hash;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.url_shortener.generator.HashGenerator;
import org.url_shortener.repository.HashRepository;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
public class HashCache {
    @Value("${executor.queueCapacity}")
    private int cacheSize;

    @Qualifier("executorService")
    private final ExecutorService executorService;
    private BlockingQueue<String> hashQueue;
    private final AtomicBoolean isRefilling = new AtomicBoolean(false);
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;

    double howTo20Percent;

    @PostConstruct
    private void init() {
        this.hashQueue = new LinkedBlockingQueue<>(cacheSize);
        this.howTo20Percent = cacheSize * 0.2;
    }

    public String getHash() {
        if (hashQueue.size() > howTo20Percent) {
            return hashQueue.poll();
        } else {
            if (isRefilling.compareAndSet(false, true)) {
                executorService.execute(() -> {
                    int batchSize = cacheSize - hashQueue.size();
                    List<String> hashes = hashRepository.getHashBatch(batchSize);
                    hashes.forEach(System.out::println);
                    System.out.println("Before adding to queue: " + hashQueue);
                    hashes.forEach(hashQueue::offer);
                    System.out.println("After adding to queue: " + hashQueue);
                    hashGenerator.generateBatch();
                    isRefilling.set(false);
                });
            }
            while (hashQueue.isEmpty()) {
                // Небольшая задержка для ожидания
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            return hashQueue.poll();
        }
    }
}
