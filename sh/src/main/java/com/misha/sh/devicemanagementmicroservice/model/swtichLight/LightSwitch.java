package com.misha.sh.devicemanagementmicroservice.model.swtichLight;

import com.misha.sh.devicemanagementmicroservice.model.device.Device;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;


@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
@Entity
public class LightSwitch extends Device {

    private Boolean isOn;
    @Max(value = 100)
    private Integer brightness;

    @Enumerated(EnumType.STRING)
    private ColorTemperature colorTemperature;

    private Integer maxWattage;

    private Integer minWattage;

    private LocalDateTime startAt;

    private LocalDateTime endAt;

    @Enumerated(EnumType.STRING)
    private LightSwitchMode lightSwitchMode;



}
