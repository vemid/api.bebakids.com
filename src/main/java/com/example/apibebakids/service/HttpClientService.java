package com.example.apibebakids.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class HttpClientService {

    @Autowired
    private RestTemplate restTemplate;

    public ResponseEntity<String> forwardPostRequest(String data) {
        String targetUrl = "https://example.com/target-api";
        return restTemplate.postForEntity(targetUrl, data, String.class);
    }
}