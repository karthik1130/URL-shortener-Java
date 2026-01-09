package com.urlshortener.service;

import com.urlshortener.model.UrlEntity;
import com.urlshortener.repository.UrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
public class UrlService {

    @Autowired
    private UrlRepository urlRepository;

    public UrlEntity shortenUrl(String originalUrl) {
        // Validate URL
        if (!isValidUrl(originalUrl)) {
            throw new IllegalArgumentException("Invalid URL format");
        }

        // Check if URL already exists
        Optional<UrlEntity> existing = urlRepository.findAll().stream()
                .filter(url -> url.getOriginalUrl().equals(originalUrl))
                .findFirst();

        if (existing.isPresent()) {
            return existing.get();
        }

        // Generate short code
        String shortCode = generateShortCode(originalUrl);

        // Ensure uniqueness
        while (urlRepository.existsByShortCode(shortCode)) {
            shortCode = generateShortCode(originalUrl + System.currentTimeMillis());
        }

        UrlEntity urlEntity = new UrlEntity(originalUrl, shortCode);
        return urlRepository.save(urlEntity);
    }

    public Optional<UrlEntity> getOriginalUrl(String shortCode) {
        Optional<UrlEntity> urlEntity = urlRepository.findByShortCode(shortCode);
        if (urlEntity.isPresent()) {
            UrlEntity entity = urlEntity.get();
            entity.incrementClickCount();
            urlRepository.save(entity);
        }
        return urlEntity;
    }

    public List<UrlEntity> getAllUrls() {
        return urlRepository.findAll();
    }

    private String generateShortCode(String url) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(url.getBytes(StandardCharsets.UTF_8));
            String base64 = Base64.getUrlEncoder().withoutPadding().encodeToString(hashBytes);
            // Take first 8 characters and remove any special characters
            return base64.substring(0, Math.min(8, base64.length())).replaceAll("[^a-zA-Z0-9]", "a");
        } catch (NoSuchAlgorithmException e) {
            // Fallback to simple hash
            return String.valueOf(url.hashCode()).replace("-", "x").substring(0, Math.min(8, String.valueOf(url.hashCode()).length()));
        }
    }

    private boolean isValidUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return false;
        }
        try {
            // Add protocol if missing
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "https://" + url;
            }
            new java.net.URL(url).toURI();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
