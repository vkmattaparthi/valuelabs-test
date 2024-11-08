package com.app.trackingnumbergenerator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TrackingNumberResponse {
    private String trackingNumber;
    private String createdAt;
}
