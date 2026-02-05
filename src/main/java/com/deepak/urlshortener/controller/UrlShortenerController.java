package com.deepak.urlshortener.controller;

import com.deepak.urlshortener.dto.CreateShortUrlRequest;
import com.deepak.urlshortener.dto.CreateShortUrlResponse;
import com.deepak.urlshortener.service.UrlShortenerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class UrlShortenerController {

    private final UrlShortenerService urlShortenerService;

    /**
     * Create short URL (POST)
     */
    @PostMapping("/api/v1/shorten")
    public ResponseEntity<CreateShortUrlResponse> shortenUrl(
            @Valid @RequestBody CreateShortUrlRequest request) {

        CreateShortUrlResponse response =
                urlShortenerService.createShortUrl(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * Redirect to long URL (GET)
     */
    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirectToLongUrl(@PathVariable String shortCode) {
        String longUrl = urlShortenerService.getLongUrl(shortCode);

        // prepend https if missing
        if (!longUrl.startsWith("http")) {
            longUrl = "https://" + longUrl;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(longUrl));

        return new ResponseEntity<>(headers, HttpStatus.FOUND); // 302
    }
}
