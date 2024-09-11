package com.misha.sh.devicemanagementmicroservice.model.device;

import com.misha.sh.devicemanagementmicroservice.model.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "devices")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Device {
    //For registration device
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String deviceName;

    @Enumerated(EnumType.STRING)
    private DeviceType deviceType;
    private String deviceDescription;
    private String manufacturer;
    private String deviceModel;
    private String serialNumber;
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;


    private boolean isActive;


    @ManyToOne(fetch = FetchType.LAZY)
    private Mode mode;
    @ManyToOne(fetch = FetchType.EAGER)
    private User user;



    private boolean isConnected;
    private boolean turnOn;
    private boolean turnOff;



    @Enumerated(EnumType.STRING)
    private DeviceStatus status;
    private String location;
    private Double batteryLevel;
    private Double voltage;
    private Integer amps;
    private String energyConsumingPerHours;


    private String lowEnergyConsumingMode;
    private String highEnergyConsumingMode;

}

