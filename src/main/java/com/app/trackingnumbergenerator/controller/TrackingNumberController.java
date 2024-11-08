package com.app.trackingnumbergenerator.controller;

import com.app.trackingnumbergenerator.dto.TrackingNumberResponse;
import com.app.trackingnumbergenerator.service.TrackingNumberGeneratorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.constraints.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.NoSuchAlgorithmException;
import java.time.Instant;

@RestController
@RequestMapping("/api/v1")
@Validated
public class TrackingNumberController {

    Logger logger = LoggerFactory.getLogger(TrackingNumberController.class);

    @Autowired
    private TrackingNumberGeneratorService trackingNumberGeneratorService;

    @Operation(
            summary = "Generate unique tracking number",
            description = "Generates a unique tracking number.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Tracking number generated successfully",
                            content = @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TrackingNumberResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid input parameters"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error"
                    )
            }
    )
    @PostMapping("/generate-tracking-number")
    public ResponseEntity<TrackingNumberResponse> generateTrackingNumber(

            @Parameter(
                    description = "Origin country ID in ISO 3166-1 alpha-2 format.",
                    required = true,
                    example = "US"
            )
            @NotBlank
            @Size(min = 2, max = 2, message = "Origin country ID must be 2 characters long.")
            @Pattern(regexp = "^[A-Za-z]{2}$", message = "Origin country ID must be in ISO 3166-1 alpha-2 format.")
            @RequestParam("origin_country_id") String originCountryId,

            @Parameter(
                    description = "Destination country ID in ISO 3166-1 alpha-2 format.",
                    required = true,
                    example = "IN"
            )
            @NotBlank
            @Size(min = 2, max = 2, message = "Destination country ID must be 2 characters long.")
            @Pattern(regexp = "^[A-Za-z]{2}$", message = "Destination country ID must be in ISO 3166-1 alpha-2 format.")
            @RequestParam("destination_country_id") String destinationCountryId,

            @Parameter(
                    description = "Weight of the package in kilograms (between 0.001 and 999.999).",
                    required = true,
                    example = "5.555"
            )
            @DecimalMin(value = "0.001", message = "Weight must be greater than 0.")
            @DecimalMax(value = "999.999", message = "Weight must not exceed 999.999 kilograms.")
            @RequestParam @NotNull String weight,

            @Parameter(
                    description = "Creation timestamp of the tracking request.",
                    required = true,
                    example = "2024-11-07T12:00:00+08:00"
            )
            @NotBlank
            @RequestParam("created_at") String createdAt,

            @Parameter(
                    description = "Unique customer ID (UUID format).",
                    required = true,
                    example = "123e4567-e89b-12d3-a456-426614174000"
            )
            @NotNull
            @Pattern(regexp = "^[a-f0-9-]{36}$", message = "Customer ID must be a valid UUID.")
            @RequestParam("customer_id") String customerId,

            @Parameter(
                    description = "Name of the customer.",
                    required = true,
                    example = "RedBox Logistics"
            )
            @NotBlank
            @RequestParam("customer_name") String customerName,

            @Parameter(
                    description = "Slug for the customer (kebab-case format).",
                    required = true,
                    example = "redbox-logistics"
            )
            @NotBlank
            @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$", message = "Customer slug must be in kebab-case.")
            @RequestParam("customer_slug")
            String customerSlug) throws NoSuchAlgorithmException {

        String trackingNumber = trackingNumberGeneratorService.generateTrackingNumber(
                originCountryId, destinationCountryId, weight, createdAt, customerId, customerName, customerSlug);

        // Return the response with the generated tracking number and timestamp
        return ResponseEntity.ok(new TrackingNumberResponse(trackingNumber, Instant.now().toString()));
    }
}