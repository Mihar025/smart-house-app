package com.misha.sh.devicemanagementmicroservice.mapper;

import com.misha.sh.devicemanagementmicroservice.model.smartOutlet.SmartOutlet;
import com.misha.sh.devicemanagementmicroservice.model.User;
import com.misha.sh.devicemanagementmicroservice.repository.UserRepository;
import com.misha.sh.devicemanagementmicroservice.request.smartOutlet.SmartOutletRequest;
import com.misha.sh.devicemanagementmicroservice.request.smartOutlet.SmartOutletResponse;
import com.misha.sh.devicemanagementmicroservice.request.smartOutlet.energyConsuming.SmartOutletEnergyConsumingRequest;
import com.misha.sh.devicemanagementmicroservice.request.smartOutlet.energyConsuming.SmartOutletEnergyConsumingResponse;
import com.misha.sh.devicemanagementmicroservice.request.smartOutlet.scheduling.SmartOutletScheduleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SmartOutletMapper {
    private final UserRepository userRepository;

    public SmartOutlet toSmartOutlet(SmartOutletRequest smartOutlet) {
        return SmartOutlet.builder()
                .deviceName(smartOutlet.getDeviceName())
                .deviceType(smartOutlet.getDeviceType())
                .deviceDescription(smartOutlet.getDeviceDescription())
                .manufacturer(smartOutlet.getManufacturer())
                .deviceModel(smartOutlet.getDeviceModel())
                .serialNumber(smartOutlet.getSerialNumber())
                .status(smartOutlet.getStatus())
                .voltage(smartOutlet.getVoltage())
                .amps(smartOutlet.getAmps())
                .energyConsumingPerHours(smartOutlet.getEnergyConsumingPerHours())
                .build();
    }






    public SmartOutletResponse toSmartOutletResponse(SmartOutlet smartOutlet) {
        return SmartOutletResponse.builder()
                .deviceId(smartOutlet.getId())
                .deviceName(smartOutlet.getDeviceName())
                .deviceType(smartOutlet.getDeviceType())
                .deviceDescription(smartOutlet.getDeviceDescription())
                .manufacturer(smartOutlet.getManufacturer())
                .deviceModel(smartOutlet.getDeviceModel())
                .serialNumber(smartOutlet.getSerialNumber())
                .status(smartOutlet.getStatus())
                .voltage(smartOutlet.getVoltage())
                .amps(smartOutlet.getAmps())
                .energyConsumingPerHours(smartOutlet.getEnergyConsumingPerHours())
                .ownerId(smartOutlet.getUser().getId())
                .build();
    }

    public SmartOutletScheduleResponse toSmartOutletScheduleResponse(SmartOutlet smartOutlet) {
        return SmartOutletScheduleResponse.builder()
                .outletId(smartOutlet.getId())
                .scheduledOn(smartOutlet.getScheduledOn())
                .scheduledOff(smartOutlet.getScheduledOff())
                .build();
    }



    public SmartOutlet toSmartOutletEnergyConsuming(SmartOutletEnergyConsumingRequest request){
        return SmartOutlet.builder()
                .voltage(request.getVoltage())
                .amps(request.getAmps())
                .energyConsumingPerHours(request.getEnergyConsumingPerHours())
                .build();
    }

    public SmartOutletEnergyConsumingResponse toSmartOutletEnergyConsumingResponse(SmartOutlet outlet) {
        return SmartOutletEnergyConsumingResponse.builder()
                .outletId(outlet.getId())
                .voltage(outlet.getVoltage())
                .amps(outlet.getAmps())
                .energyConsumingPerHours(outlet.getEnergyConsumingPerHours())
                .ownerId(outlet.getUser().getId())
                .build();
    }







}
