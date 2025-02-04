package com.ricardomiguez.CreateShortUrl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class UrlData {
    private String url;
    private long expirationTime;
}
