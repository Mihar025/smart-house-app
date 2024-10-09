package com.misha.sh.devicemanagementmicroservice.model.sensor;


import com.misha.sh.devicemanagementmicroservice.model.device.Device;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
public class WeatherSensor extends Device {

    private Double latitude;
    private Double longitude;
    private Double temperature;
    private Double humidity;
    private Double pressure;
    private Double windSpeed;
    private String windDirection;
    private Double precipitation;
    private LocalDateTime lastUpdateTime;
}