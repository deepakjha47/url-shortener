package com.deepak.urlshortener.service;

import com.deepak.urlshortener.dto.CreateShortUrlRequest;
import com.deepak.urlshortener.dto.CreateShortUrlResponse;

public interface UrlShortenerService {

    CreateShortUrlResponse createShortUrl(CreateShortUrlRequest request);

    String getLongUrl(String shortCode);
}
