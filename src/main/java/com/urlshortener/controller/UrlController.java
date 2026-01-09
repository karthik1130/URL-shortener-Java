package com.urlshortener.controller;

import com.urlshortener.dto.UrlRequest;
import com.urlshortener.dto.UrlResponse;
import com.urlshortener.model.UrlEntity;
import com.urlshortener.service.UrlService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class UrlController {

    @Autowired
    private UrlService urlService;

    @PostMapping("/shorten")
    public ResponseEntity<?> shortenUrl(@Valid @RequestBody UrlRequest request, HttpServletRequest httpRequest) {
        try {
            UrlEntity urlEntity = urlService.shortenUrl(request.getUrl());
            String baseUrl = getBaseUrl(httpRequest);
            String shortUrl = baseUrl + "/api/" + urlEntity.getShortCode();

            UrlResponse response = new UrlResponse(
                    urlEntity.getOriginalUrl(),
                    shortUrl,
                    urlEntity.getShortCode(),
                    urlEntity.getCreatedAt(),
                    urlEntity.getClickCount()
            );

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"An error occurred while shortening the URL\"}");
        }
    }

    @GetMapping("/urls")
    public ResponseEntity<List<UrlResponse>> getAllUrls(HttpServletRequest httpRequest) {
        String baseUrl = getBaseUrl(httpRequest);
        List<UrlResponse> responses = urlService.getAllUrls().stream()
                .map(urlEntity -> new UrlResponse(
                        urlEntity.getOriginalUrl(),
                        baseUrl + "/" + urlEntity.getShortCode(),
                        urlEntity.getShortCode(),
                        urlEntity.getCreatedAt(),
                        urlEntity.getClickCount()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @GetMapping("{shortCode}")
    public ResponseEntity<?> redirect(@PathVariable String shortCode) {
        return urlService.getOriginalUrl(shortCode)
                .map(urlEntity -> ResponseEntity.status(HttpStatus.FOUND)
                        .header("Location", urlEntity.getOriginalUrl())
                        .build())
                .orElse(ResponseEntity.notFound().build());
    }

    private String getBaseUrl(HttpServletRequest request) {
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();
        String contextPath = request.getContextPath();

        StringBuilder url = new StringBuilder();
        url.append(scheme).append("://").append(serverName);

        if ((serverPort != 80) && (serverPort != 443)) {
            url.append(":").append(serverPort);
        }

        url.append(contextPath);

        return url.toString();
    }
}
