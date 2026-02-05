package com.deepak.urlshortener.util;

import org.springframework.stereotype.Component;

@Component
public class Base62Encoder {

    private static final char[] BASE62_CHARS =
            "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    private static final int BASE = 62;

    public String encode(long value) {
        if (value == 0) {
            return String.valueOf(BASE62_CHARS[0]);
        }

        StringBuilder sb = new StringBuilder();
        while (value > 0) {
            sb.append(BASE62_CHARS[(int) (value % BASE)]);
            value /= BASE;
        }
        return sb.reverse().toString();
    }
}
