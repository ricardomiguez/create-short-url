package com.ricardomiguez.CreateShortUrl;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Main implements RequestHandler<Map<String, Object>, Map<String, String>> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Map<String, String> handleRequest(Map<String, Object> request, Context context) {
        String requestBody = (String) request.get("body");

        Map<String, String> requestBodyDeserialized;

        try {
            requestBodyDeserialized = objectMapper.readValue(requestBody, Map.class);
        } catch(Exception exception) {
            throw new RuntimeException("Error parsing JSON body: " + exception.getMessage(), exception);
        }

        String url = requestBodyDeserialized.get("url");
        String expirationTime = requestBodyDeserialized.get("expirationTime");

        String urlId = UUID.randomUUID().toString().substring(0, 8);

        Map<String, String> response = new HashMap<>();

        response.put("urlId", urlId);

        return response;
    }
}