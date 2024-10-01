package com.misha.sh.devicemanagementmicroservice.model.doorLock;

import com.misha.sh.devicemanagementmicroservice.model.device.Device;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class DoorLock  extends Device {

    private Integer accessCode;
    private FingerPrintCode fingerPrintCode;

    private boolean autoLockEnabled;

    private boolean locked;
    private boolean opened;

    private int autoLockDelaySeconds;

    private boolean tamperAlarmEnabled;

    private boolean remoteAccessEnabled;

    private LocalDateTime lastOpenedAt;
    private LocalDateTime lastClosedAt;

    private LocalDateTime scheduledLockTime;
    private LocalDateTime scheduledOpenTime;

    @Enumerated(EnumType.STRING)
    private DoorSensor doorSensor;

    @Enumerated(EnumType.STRING)
    private LockStatus lockStatus;

    @Enumerated(EnumType.STRING)
    private LockMechanism lockMechanism;




    public boolean isAccessCodeValid(Integer accessCode){
       return this.accessCode.equals(accessCode);
    }

}
