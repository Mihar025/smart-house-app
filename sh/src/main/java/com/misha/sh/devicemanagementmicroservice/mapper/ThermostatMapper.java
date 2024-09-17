package com.misha.sh.devicemanagementmicroservice.mapper;

import com.misha.sh.devicemanagementmicroservice.model.thermostat.Thermostat;
import com.misha.sh.devicemanagementmicroservice.model.User;
import com.misha.sh.devicemanagementmicroservice.repository.UserRepository;
import com.misha.sh.devicemanagementmicroservice.request.thermostat.addThermostat.ThermostatRequest;
import com.misha.sh.devicemanagementmicroservice.request.thermostat.addThermostat.ThermostatResponse;
import com.misha.sh.devicemanagementmicroservice.request.thermostat.temperatureMode.ThermostatCoolingModeResponse;
import com.misha.sh.devicemanagementmicroservice.request.thermostat.temperatureMode.ThermostatHeatModeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ThermostatMapper {

    private final UserRepository userRepository;

    public Thermostat toThermostat(ThermostatRequest thermostatRequest) {
        return Thermostat.builder()
                .deviceName(thermostatRequest.getDeviceName())
                .deviceType(thermostatRequest.getDeviceType())
                .deviceDescription(thermostatRequest.getDeviceDescription())
                .manufacturer(thermostatRequest.getManufacturer())
                .deviceModel(thermostatRequest.getDeviceModel())
                .status(thermostatRequest.getStatus())
                .location(thermostatRequest.getLocation())
                .isConnected(thermostatRequest.isConnected())
                .voltage(thermostatRequest.getVoltage())
                .amps(thermostatRequest.getAmps())
                .energyConsumingPerHours(thermostatRequest.getEnergyConsumingPerHours())
                .build();
    }


    public ThermostatResponse toThermostatResponse(Thermostat thermostat) {
        ThermostatResponse thermostatResponse = ThermostatResponse.builder()
                .deviceId(thermostat.getId())
                .deviceName(thermostat.getDeviceName())
                .deviceType(thermostat.getDeviceType())
                .deviceDescription(thermostat.getDeviceDescription())
                .deviceModel(thermostat.getDeviceModel())
                .manufacturer(thermostat.getManufacturer())
                .status(thermostat.getStatus())
                .location(thermostat.getLocation())
                .isConnected(thermostat.isConnected())
                .voltage(thermostat.getVoltage())
                .amps(thermostat.getAmps())
                .energyConsumingPerHours(thermostat.getEnergyConsumingPerHours())
                .ownerId(thermostat.getUser().getId())
                .build();

        return thermostatResponse;
    }


    public ThermostatCoolingModeResponse toThermostatCoolingModeResponse(Thermostat thermostat) {
        return ThermostatCoolingModeResponse.builder()
                .thermostatId(thermostat.getId())
                .currentTemperature(thermostat.getCurrentTemperature())
                .targetTemperature(thermostat.getTargetTemperature())
                .temperatureMode(thermostat.getTemperatureMode())
                .ownerId(thermostat.getUser().getId())
                .temporaryMode(thermostat.getTemporaryMode())
                .autoMode(thermostat.getAutoMode())
                .build();
    }


    public ThermostatHeatModeResponse toThermostatHeatModeResponse(Thermostat thermostat) {
        return ThermostatHeatModeResponse.builder()
                .thermostatId(thermostat.getId())
                .currentTemperature(thermostat.getCurrentTemperature())
                .targetTemperature(thermostat.getTargetTemperature())
                .temperatureMode(thermostat.getTemperatureMode())
                .ownerId(thermostat.getUser().getId())
                .temporaryMode(thermostat.getTemporaryMode())
                .autoMode(thermostat.getAutoMode())
                .build();
    }





}
