package com.app.trackingnumbergenerator.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class TrackingNumberGeneratorService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private final String LOCK_KEY = "tracking_number_lock";

    private static final String TRACKING_NUMBER_PREFIX = "tracking_number:";

    // Atomic counter to generate unique tracking numbers
    private final AtomicLong counter = new AtomicLong(System.currentTimeMillis());

    public String generateTrackingNumber(String originCountryId, String destinationCountryId,
                                         String weight, String createdAt, String customerId,
                                         String customerName, String customerSlug) throws NoSuchAlgorithmException {

        // Attempt to acquire a distributed lock (with a timeout)
        Boolean lockAcquired = redisTemplate.opsForValue().setIfAbsent(LOCK_KEY, "LOCKED", Duration.ofMillis(100));

        if (lockAcquired != null && lockAcquired) {
            try {
                return generateTrackingNumberInternal(originCountryId, destinationCountryId, weight, createdAt,
                        customerId, customerName, customerSlug);
            } finally {
                // Release the lock
                redisTemplate.delete(LOCK_KEY);
            }
        } else {
            throw new IllegalStateException("Unable to acquire lock for tracking number generation");
        }
    }

    private String generateTrackingNumberInternal(String originCountryId, String destinationCountryId,
                                                  String weight, String createdAt, String customerId,
                                                  String customerName, String customerSlug)
            throws NoSuchAlgorithmException {
        synchronized (this) {
            // Combine parameters  along with an atomic counter into a raw string
            String rawString = originCountryId + destinationCountryId + weight + createdAt +
                    customerId + customerName + customerSlug + counter.getAndIncrement();

            // Generate hash to ensure uniqueness and match regex pattern
            String uniqueHash = generateUniqueHash(rawString);

            return uniqueHash;
        }
    }

    // Check if the generated tracking number already exists in Redis
    private boolean isTrackingNumberExists(String trackingNumber) {
        return redisTemplate.hasKey(TRACKING_NUMBER_PREFIX + trackingNumber);
    }

    // Save the generated tracking number to Redis
    private void saveTrackingNumberToRedis(String trackingNumber) {
        redisTemplate.opsForValue().set(TRACKING_NUMBER_PREFIX + trackingNumber, "exists");
    }

    // Generate SHA-256 hash
    private String generateUniqueHash(String rawString) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(rawString.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            hexString.append(String.format("%02x", b));
        }
        // Return the first 16 characters as the tracking number (ensures it matches ^[A-Z0-9]{1,16}$)
        return hexString.substring(0, 16).toUpperCase();
    }
}
