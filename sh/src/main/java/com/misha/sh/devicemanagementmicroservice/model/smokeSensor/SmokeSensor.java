package com.misha.sh.devicemanagementmicroservice.model.smokeSensor;

import com.misha.sh.devicemanagementmicroservice.model.device.Device;
import jakarta.persistence.*;
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
public class SmokeSensor extends Device {

    @Column(nullable = false)
    private Double smokeLevel; // Текущий уровень дыма (в ppm)

    @Column(nullable = false)
    private Double smokeThreshold; // Пороговое значение для срабатывания тревоги (в ppm)

    @Column(nullable = false)
    private Boolean alarmActive; // Флаг активности тревоги

    @Column(nullable = false)
    private LocalDateTime lastAlarmTime; // Время последнего срабатывания тревоги

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SensorSensitivity sensitivity; // Уровень чувствительности сенсора

    @Column(nullable = false)
    private Double valueForSensitivity;

    @Column(nullable = false)
    private Boolean selfTestPassed; // Результат последнего самотестирования

    @Column(nullable = false)
    private LocalDateTime lastMaintenanceDate; // Дата последнего технического обслуживания

}
