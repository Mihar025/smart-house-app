package com.misha.sh.devicemanagementmicroservice.model.smartOutlet;

import com.misha.sh.devicemanagementmicroservice.model.device.Device;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;


@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
@Entity
@EntityListeners(AuditingEntityListener.class)
public class SmartOutlet extends Device {



    @Column(nullable = false)
    private boolean isOn;

    @Column(name = "current_power_usage")
    private Double currentPowerUsage;

    @Column(name = "last_on_time")
    private LocalDateTime lastOnTime;

    @Column(name = "last_off_time")
    private LocalDateTime lastOffTime;

    @Column(name = "scheduled_on")
    private LocalDateTime scheduledOn;

    @Column(name = "scheduled_off")
    private LocalDateTime scheduledOff;


}
