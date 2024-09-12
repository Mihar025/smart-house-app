package com.misha.sh.devicemanagementmicroservice.mapper;



import com.misha.sh.devicemanagementmicroservice.model.device.Device;
import com.misha.sh.devicemanagementmicroservice.request.device.battery.DeviceBatteryResponse;
import com.misha.sh.devicemanagementmicroservice.request.device.condition.DeviceTurnOffResponse;
import com.misha.sh.devicemanagementmicroservice.request.device.condition.DeviceTurnOnResponse;
import com.misha.sh.devicemanagementmicroservice.request.device.devReq.DeviceResponse;
import com.misha.sh.devicemanagementmicroservice.request.device.devTechReq.DeviceTechnicalResponse;
import org.springframework.stereotype.Service;

@Service
public class DeviceMapper {

    public DeviceResponse toDeviceResponse(Device device){
        return DeviceResponse.builder()
                .id(device.getId())
                .deviceName(device.getDeviceName())
                .deviceType(device.getDeviceType())
                .deviceDescription(device.getDeviceDescription())
                .manufacturer(device.getManufacturer())
                .deviceModel(device.getDeviceModel())
                .serialNumber(device.getSerialNumber())
                .createdAt(device.getCreatedDate())
                .updatedAt(device.getUpdatedAt())
                .isActive(device.isActive())
                .batteryLevel(device.getBatteryLevel())
                .voltage(device.getVoltage())
                .amps(device.getAmps())
                .energyConsumingPerHours(device.getEnergyConsumingPerHours())
                .lowEnergyConsumingMode(device.getLowEnergyConsumingMode())
                .highEnergyConsumingMode(device.getHighEnergyConsumingMode())
                .build();
    }


    public DeviceBatteryResponse toDeviceBatteryResponse(Device device){
        return DeviceBatteryResponse.builder()
                .deviceId(device.getId())
                .batteryPercentage(device.getBatteryLevel())
                .build();
    }





    public DeviceTechnicalResponse toTechnicalDeviceResponse(Device deviceTechnicalResponse){
        return DeviceTechnicalResponse.builder()
                .deviceId(deviceTechnicalResponse.getId())
                .isActive(deviceTechnicalResponse.isActive())
                .voltage(deviceTechnicalResponse.getVoltage())
                .amps(deviceTechnicalResponse.getAmps())
                .energyConsumingPerHours(deviceTechnicalResponse.getEnergyConsumingPerHours())
                .batteryLevel(deviceTechnicalResponse.getBatteryLevel())
                .build();
    }




    public DeviceTurnOnResponse toTurnedOnDevice(Device deviceResponse){
        return DeviceTurnOnResponse.builder()
                .turnOn(deviceResponse.isTurnOn())
                .build();
    }

    public DeviceTurnOffResponse toTurnedOffDevice(Device device){
        return DeviceTurnOffResponse.builder()
                .turnOff(device.isTurnOff())
                .build();
    }




}
