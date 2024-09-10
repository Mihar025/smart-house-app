package com.misha.sh.devicemanagementmicroservice.request.doorLock;

import com.misha.sh.devicemanagementmicroservice.model.device.DeviceStatus;
import com.misha.sh.devicemanagementmicroservice.model.device.DeviceType;
import com.misha.sh.devicemanagementmicroservice.model.doorLock.LockMechanism;
import com.misha.sh.devicemanagementmicroservice.model.doorLock.LockStatus;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DoorLockResponse {

    private Integer deviceId;
    private String deviceName;
    private DeviceType deviceType;
    private String deviceDescription;
    private String manufacturer;
    private String deviceModel;
    private String serialNumber;
    private DeviceStatus status;
    private LockStatus lockStatus;
    private LockMechanism lockMechanism;
    private Integer ownerId;


}
