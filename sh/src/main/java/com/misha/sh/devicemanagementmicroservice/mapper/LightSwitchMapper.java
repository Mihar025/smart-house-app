package com.misha.sh.devicemanagementmicroservice.mapper;

import com.misha.sh.devicemanagementmicroservice.model.swtichLight.LightSwitch;
import com.misha.sh.devicemanagementmicroservice.model.User;
import com.misha.sh.devicemanagementmicroservice.request.lightSwitch.addSwitch.LightSwitchRequest;
import com.misha.sh.devicemanagementmicroservice.request.lightSwitch.addSwitch.LightSwitchResponse;
import com.misha.sh.devicemanagementmicroservice.request.lightSwitch.brightness.ColorTemperatureResponse;
import com.misha.sh.devicemanagementmicroservice.request.lightSwitch.brightness.BrightnessRequest;
import com.misha.sh.devicemanagementmicroservice.request.lightSwitch.brightness.BrightnessResponse;
import com.misha.sh.devicemanagementmicroservice.request.lightSwitch.switchTurnOff.SwitchTurnOffRequest;
import com.misha.sh.devicemanagementmicroservice.request.lightSwitch.switchTurnOff.SwitchTurnOffResponse;
import com.misha.sh.devicemanagementmicroservice.request.lightSwitch.switchTurnOn.SwitchTurnOnRequest;
import com.misha.sh.devicemanagementmicroservice.request.lightSwitch.switchTurnOn.SwitchTurnOnResponse;
import org.springframework.stereotype.Service;

@Service
public class LightSwitchMapper {


    public LightSwitch toLightSwitchTurnOnRequest(SwitchTurnOnRequest lightSwitch) {
        return LightSwitch.builder()
                .brightness(lightSwitch.getBrightness())
                .colorTemperature(lightSwitch.getColorTemperature())
                .maxWattage(lightSwitch.getMaxWattage())
                .minWattage(lightSwitch.getMinWattage())
                .lightSwitchMode(lightSwitch.getMode())
                .build();
    }


    public SwitchTurnOnResponse toSwitchTurnOnResponse(LightSwitch lightSwitch) {
        return SwitchTurnOnResponse.builder()
                .switchId(lightSwitch.getId())
                .isOn(lightSwitch.getIsOn())
                .brightness(lightSwitch.getBrightness())
                .colorTemperature(lightSwitch.getColorTemperature())
                .maxWattage(lightSwitch.getMaxWattage())
                .minWattage(lightSwitch.getMinWattage())
                .mode(lightSwitch.getLightSwitchMode())
                .ownerId(lightSwitch.getUser().getId())
                .build();
    }





    public SwitchTurnOffResponse toDeviceTurnOffForSwitch(LightSwitch lightSwitch) {
        return SwitchTurnOffResponse.builder()
                .ownerId(lightSwitch.getUser().getId())
                .switchId(lightSwitch.getId())
                .isOn(lightSwitch.getIsOn())
                .ownerId(lightSwitch.getUser().getId())
                .build();
    }


    public BrightnessResponse toBrightLightSwitchResponse(LightSwitch foundedSwitch) {
        return BrightnessResponse.builder()
                .switchId(foundedSwitch.getId())
                .colorTemperature(foundedSwitch.getColorTemperature())
                .brightness(foundedSwitch.getBrightness())
                .ownerId(foundedSwitch.getUser().getId())
                .build();
    }

    public ColorTemperatureResponse toColorTemperatureResponse(LightSwitch foundedSwitch) {
        return ColorTemperatureResponse.builder()
                .colorTemperature(foundedSwitch.getColorTemperature())
                .lightSwitchId(foundedSwitch.getId())
                .ownerId(foundedSwitch.getUser().getId())
                .build();
    }

    public LightSwitch toLight(LightSwitchRequest lightSwitchRequest) {
        return LightSwitch.builder()
                .deviceName(lightSwitchRequest.getDeviceName())
                .deviceType(lightSwitchRequest.getDeviceType())
                .deviceDescription(lightSwitchRequest.getDeviceDescription())
                .manufacturer(lightSwitchRequest.getManufacturer())
                .deviceModel(lightSwitchRequest.getDeviceModel())
                .serialNumber(lightSwitchRequest.getSerialNumber())
                .status(lightSwitchRequest.getStatus())
                .location(lightSwitchRequest.getLocation())
                .isConnected(lightSwitchRequest.isConnected())
                .batteryLevel(lightSwitchRequest.getBatteryLevel())
                .voltage(lightSwitchRequest.getVoltage())
                .amps(lightSwitchRequest.getAmps())
                .energyConsumingPerHours(lightSwitchRequest.getEnergyConsumingPerHours())
                .build();
    }


    public LightSwitchResponse toLightSwitchResponse(LightSwitch lightSwitch) {
        return LightSwitchResponse.builder()
                .id(lightSwitch.getId())
                .deviceName(lightSwitch.getDeviceName())
                .deviceType(lightSwitch.getDeviceType())
                .deviceDescription(lightSwitch.getDeviceDescription())
                .manufacturer(lightSwitch.getManufacturer())
                .deviceModel(lightSwitch.getDeviceModel())
                .serialNumber(lightSwitch.getSerialNumber())
                .status(lightSwitch.getStatus())
                .location(lightSwitch.getLocation())
                .isConnected(lightSwitch.isConnected())
                .batteryLevel(lightSwitch.getBatteryLevel())
                .voltage(lightSwitch.getVoltage())
                .amps(lightSwitch.getAmps())
                .createdAt(lightSwitch.getCreatedDate())
                .updatedAt(lightSwitch.getUpdatedAt())
                .energyConsumingPerHours(lightSwitch.getEnergyConsumingPerHours())
                .ownerId(lightSwitch.getUser().getId())
                .build();


    }








}
