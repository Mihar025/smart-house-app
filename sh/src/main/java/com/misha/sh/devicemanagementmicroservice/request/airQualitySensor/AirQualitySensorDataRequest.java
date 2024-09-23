package com.misha.sh.devicemanagementmicroservice.request.airQualitySensor;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AirQualitySensorDataRequest {
    @NotNull(message = "PM2.5 level is required")
    @PositiveOrZero(message = "PM2.5 level must be non-negative")
    private Double pm25Level;

    @NotNull(message = "PM10 level is required")
    @PositiveOrZero(message = "PM10 level must be non-negative")
    private Double pm10Level;

    @NotNull(message = "CO2 level is required")
    @PositiveOrZero(message = "CO2 level must be non-negative")
    private Integer co2Level;

    @NotNull(message = "CO level is required")
    @PositiveOrZero(message = "CO level must be non-negative")
    private Double coLevel;

    @NotNull(message = "NO2 level is required")
    @PositiveOrZero(message = "NO2 level must be non-negative")
    private Double no2Level;

    @NotNull(message = "O3 level is required")
    @PositiveOrZero(message = "O3 level must be non-negative")
    private Double o3Level;

    @NotNull(message = "VOC level is required")
    @PositiveOrZero(message = "VOC level must be non-negative")
    private Integer vocLevel;

    @NotNull(message = "Temperature is required")
    @DecimalMin(value = "-273.15", message = "Temperature must be above absolute zero")
    private Double temperature;

    @NotNull(message = "Humidity is required")
    @DecimalMin(value = "0.0", message = "Humidity must be at least 0%")
    @DecimalMax(value = "100.0", message = "Humidity cannot exceed 100%")
    private Double humidity;

    @NotNull(message = "Pressure is required")
    @Positive(message = "Pressure must be positive")
    private Double pressure;

    @NotNull(message = "Air Quality Index is required")
    @Min(value = 0, message = "Air Quality Index must be at least 0")
    @Max(value = 500, message = "Air Quality Index cannot exceed 500")
    private Integer airQualityIndex;

    @NotNull(message = "Update frequency is required")
    @Positive(message = "Update frequency must be positive")
    private Integer updateFrequency;

    @NotNull(message = "PM2.5 alert threshold is required")
    @Positive(message = "PM2.5 alert threshold must be positive")
    private Double pm25AlertThreshold;

    @NotNull(message = "CO2 alert threshold is required")
    @Positive(message = "CO2 alert threshold must be positive")
    private Integer co2AlertThreshold;
}
