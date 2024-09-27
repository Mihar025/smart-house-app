package com.misha.sh.devicemanagementmicroservice.mapper;

import com.misha.sh.devicemanagementmicroservice.model.device.Mode;
import com.misha.sh.devicemanagementmicroservice.request.device.mode.ModeRequest;
import com.misha.sh.devicemanagementmicroservice.request.device.mode.ModeResponse;
import org.springframework.stereotype.Service;

@Service
public class ModeMapper {

    public Mode toMode(ModeRequest modeRequest){
        return Mode.builder()
                .lowEnergyConsumingMode(modeRequest.getLowEnergyConsumingMode())
                .highEnergyConsumingMode(modeRequest.getHighEnergyConsumingMode())
                .isTurnedOn(modeRequest.isTurnedOn())
                .activateFrom(modeRequest.getActivateFrom())
                .activateTo(modeRequest.getActivateTo())
                .devices(modeRequest.getDevices())
                .electricityConsuming(modeRequest.getElectricityConsuming())
                .voltage(modeRequest.getVoltage())
                .amps(modeRequest.getAmps())
                .build();
    }

    public ModeResponse toModeResponseLow(Mode mode){
        return ModeResponse.builder()
                .id(mode.getId())
                .lowEnergyConsumingMode(mode.getLowEnergyConsumingMode())
                .isTurnedOn(mode.isTurnedOn())
                .activateFrom(mode.getActivateFrom())
                .activateTo(mode.getActivateTo())
                .devices(mode.getDevices())
                .electricityConsuming(mode.getElectricityConsuming())
                .voltage(mode.getVoltage())
                .amps(mode.getAmps())
                .build();
    }


    public ModeResponse toModeResponseHigh(Mode mode){
        return ModeResponse.builder()
                .id(mode.getId())
                .highEnergyConsumingMode(mode.getHighEnergyConsumingMode())
                .isTurnedOn(mode.isTurnedOn())
                .activateFrom(mode.getActivateFrom())
                .activateTo(mode.getActivateTo())
                .devices(mode.getDevices())
                .electricityConsuming(mode.getElectricityConsuming())
                .voltage(mode.getVoltage())
                .amps(mode.getAmps())
                .build();
    }

    public ModeResponse toModeResponseDefault(Mode mode){
        return ModeResponse.builder()
                .id(mode.getId())
                .defaultMode(mode.getDefaultMode())
                .isTurnedOn(mode.isTurnedOn())
                .activateFrom(mode.getActivateFrom())
                .activateTo(mode.getActivateTo())
                .devices(mode.getDevices())
                .electricityConsuming(mode.getElectricityConsuming())
                .voltage(mode.getVoltage())
                .amps(mode.getAmps())
                .build();
    }


    public Mode toModeDefault(ModeResponse mode){
        return Mode.builder()
                .id(mode.getId())
                .defaultMode(mode.getDefaultMode())
                .isTurnedOn(mode.isTurnedOn())
                .activateFrom(mode.getActivateFrom())
                .activateTo(mode.getActivateTo())
                .devices(mode.getDevices())
                .electricityConsuming(mode.getElectricityConsuming())
                .voltage(mode.getVoltage())
                .amps(mode.getAmps())
                .build();
    }


    public Mode toModeLow(ModeResponse mode){
        return Mode.builder()
                .id(mode.getId())
                .lowEnergyConsumingMode(mode.getLowEnergyConsumingMode())
                .isTurnedOn(mode.isTurnedOn())
                .activateFrom(mode.getActivateFrom())
                .activateTo(mode.getActivateTo())
                .devices(mode.getDevices())
                .electricityConsuming(mode.getElectricityConsuming())
                .voltage(mode.getVoltage())
                .amps(mode.getAmps())
                .build();
    }

    public Mode toModeHigh(ModeResponse mode){
        return Mode.builder()
                .id(mode.getId())
                .highEnergyConsumingMode(mode.getHighEnergyConsumingMode())
                .isTurnedOn(mode.isTurnedOn())
                .activateFrom(mode.getActivateFrom())
                .activateTo(mode.getActivateTo())
                .devices(mode.getDevices())
                .electricityConsuming(mode.getElectricityConsuming())
                .voltage(mode.getVoltage())
                .amps(mode.getAmps())
                .build();
    }

    public ModeResponse toModeResponse(Mode mode){
        return ModeResponse.builder()
                .id(mode.getId())
                .lowEnergyConsumingMode(mode.getLowEnergyConsumingMode())
                .highEnergyConsumingMode(mode.getHighEnergyConsumingMode())
                .isTurnedOn(mode.isTurnedOn())
                .activateFrom(mode.getActivateFrom())
                .activateTo(mode.getActivateTo())
                .devices(mode.getDevices())
                .electricityConsuming(mode.getElectricityConsuming())
                .voltage(mode.getVoltage())
                .amps(mode.getAmps())
                .build();
    }















}
