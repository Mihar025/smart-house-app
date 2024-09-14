package com.misha.sh.devicemanagementmicroservice.mapper;

import com.misha.sh.devicemanagementmicroservice.model.doorLock.DoorLock;
import com.misha.sh.devicemanagementmicroservice.request.doorLock.DoorLockResponse;
import com.misha.sh.devicemanagementmicroservice.request.doorLock.DoorLockStatus;
import org.springframework.stereotype.Service;

@Service
public class DoorLockMapper {

    public DoorLockResponse toDoorLockResponse(DoorLock doorLock) {
        return DoorLockResponse.builder()
                .deviceId(doorLock.getId())
                .deviceName(doorLock.getDeviceName())
                .deviceType(doorLock.getDeviceType())
                .deviceDescription(doorLock.getDeviceDescription())
                .manufacturer(doorLock.getManufacturer())
                .deviceModel(doorLock.getDeviceModel())
                .serialNumber(doorLock.getSerialNumber())
                .status(doorLock.getStatus())
                .lockStatus(doorLock.getLockStatus())
                .lockMechanism(doorLock.getLockMechanism())
                .ownerId(doorLock.getUser().getId())
                .build();
    }

    public DoorLockStatus toDoorLockStatus(DoorLock doorLock){
        return DoorLockStatus.builder()
                .doorLockId(doorLock.getId())
                .isOpened(doorLock.isOpened())
                .lastOpenedTime(doorLock.getLastOpenedAt())
                .build();
    }
}
