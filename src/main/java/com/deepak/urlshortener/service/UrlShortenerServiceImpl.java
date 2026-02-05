package com.deepak.urlshortener.service;

import com.deepak.urlshortener.domain.ShortUrl;
import com.deepak.urlshortener.dto.CreateShortUrlRequest;
import com.deepak.urlshortener.dto.CreateShortUrlResponse;
import com.deepak.urlshortener.exception.UrlNotFoundException;
import com.deepak.urlshortener.repository.ShortUrlRepository;
import com.deepak.urlshortener.util.Base62Encoder;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UrlShortenerServiceImpl implements UrlShortenerService {

    private final ShortUrlRepository shortUrlRepository;
    private final Base62Encoder base62Encoder;

    @Override
    @Transactional
    public CreateShortUrlResponse createShortUrl(CreateShortUrlRequest request) {

        // generate unique shortCode
        String shortCode;
        do {
            shortCode = base62Encoder.encode(System.nanoTime());
        } while (shortUrlRepository.existsByShortCode(shortCode));

        ShortUrl shortUrl = new ShortUrl();
        shortUrl.setShortCode(shortCode);
        shortUrl.setLongUrl(request.getLongUrl());
        shortUrl.setCreatedAt(LocalDateTime.now());

        shortUrlRepository.save(shortUrl);

        return new CreateShortUrlResponse(shortCode,
                "http://localhost:8080/" + shortCode);
    }

    @Override
    @Cacheable(value = "short_url", key = "#shortCode")
    @Transactional(readOnly = true)
    public String getLongUrl(String shortCode) {
        return shortUrlRepository.findByShortCode(shortCode)
                .map(ShortUrl::getLongUrl)
                .orElseThrow(() -> new UrlNotFoundException("Short URL not found"));
    }
}
