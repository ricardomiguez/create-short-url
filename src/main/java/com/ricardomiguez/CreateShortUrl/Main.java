package com.ricardomiguez.CreateShortUrl;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Main implements RequestHandler<Map<String, Object>, Map<String, String>> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final S3Client s3Client = S3Client.builder().build();

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

        long expirationTimeInSeconds = Long.parseLong(expirationTime);

        String urlId = UUID.randomUUID().toString().substring(0, 8);

        UrlData urlData = new UrlData(url, expirationTimeInSeconds);

        try {
            String urlDataJson = objectMapper.writeValueAsString(urlData);

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket("ricardomiguez-url-shortener-storage")
                    .key(urlId + ".json")
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromString(urlDataJson));
        } catch (Exception exception) {
            throw new RuntimeException("Error saving data to S3: " + exception.getMessage(), exception);
        }

        Map<String, String> response = new HashMap<>();

        response.put("urlId", urlId);

        return response;
    }
}