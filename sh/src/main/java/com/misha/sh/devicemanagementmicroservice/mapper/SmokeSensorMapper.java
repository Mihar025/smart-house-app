package com.misha.sh.devicemanagementmicroservice.mapper;

import com.misha.sh.devicemanagementmicroservice.model.smokeSensor.SmokeSensor;
import com.misha.sh.devicemanagementmicroservice.request.smokeSensor.SmokeSensorRequest;
import com.misha.sh.devicemanagementmicroservice.request.smokeSensor.SmokeSensorResponse;
import org.springframework.stereotype.Service;

@Service
public class SmokeSensorMapper {
    public SmokeSensor toSmokeSensor(SmokeSensorRequest smokeSensorRequest) {
        return SmokeSensor.builder()
                .deviceName(smokeSensorRequest.getDeviceName())
                .deviceDescription(smokeSensorRequest.getDeviceDescription())
                .manufacturer(smokeSensorRequest.getManufacturer())
                .deviceModel(smokeSensorRequest.getDeviceModel())
                .serialNumber(String.valueOf(smokeSensorRequest.getSerialNumber()))
                .build();
    }

    public SmokeSensorResponse toSmokeSensorResponse(SmokeSensor smokeSensor) {
        return SmokeSensorResponse.builder()
                .smokeSensorId(smokeSensor.getId())
                .deviceName(smokeSensor.getDeviceName())
                .deviceDescription(smokeSensor.getDeviceDescription())
                .manufacturer(smokeSensor.getManufacturer())
                .deviceModel(smokeSensor.getDeviceModel())
                .serialNumber(smokeSensor.getSerialNumber())
                .build();
    }
}
