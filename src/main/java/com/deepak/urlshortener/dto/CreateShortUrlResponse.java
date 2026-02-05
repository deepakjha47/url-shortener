package com.deepak.urlshortener.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor // must be public
public class CreateShortUrlResponse {
    private String shortCode;
    private String shortUrl;
}
