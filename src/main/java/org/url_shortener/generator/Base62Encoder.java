package org.url_shortener.generator;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class Base62Encoder {
    public static final BigInteger BASE = BigInteger.valueOf(62);
    public static final String DIGITS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    @Async("asyncExecutor2")
    public CompletableFuture<List<String>> encode(List<Long> numbers) {
        List<String> hashes = new ArrayList<>();
        numbers.forEach(num -> {
            BigInteger number = BigInteger.valueOf(num);
            String hash = prepareHash(number);
            hashes.add(hash);
        });
        return CompletableFuture.completedFuture(hashes);
    }

    private String prepareHash(BigInteger number) {
        if (number.compareTo(BigInteger.ZERO) < 0) { // number < 0
            throw new IllegalArgumentException("number must not be negative");
        }
        StringBuilder result = new StringBuilder();
        while (number.compareTo(BigInteger.ZERO) > 0) { // number > 0
            BigInteger[] divmod = number.divideAndRemainder(BASE);
            number = divmod[0];
            int digit = divmod[1].intValue();
            result.insert(0, DIGITS.charAt(digit));
        }
        return (result.isEmpty()) ? DIGITS.substring(0, 1) : result.toString();
    }
}
