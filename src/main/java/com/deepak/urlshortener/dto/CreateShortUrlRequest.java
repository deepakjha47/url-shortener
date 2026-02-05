package com.deepak.urlshortener.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

@Getter
@Setter
public class CreateShortUrlRequest {

    @NotBlank
    @URL
    private String longUrl;
}
