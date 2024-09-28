package com.misha.sh.devicemanagementmicroservice.request.device.mode;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

import com.misha.sh.devicemanagementmicroservice.model.device.Device;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ModeResponse {


    private Integer id;

    private String lowEnergyConsumingMode;
    private String defaultMode;
    private String highEnergyConsumingMode;

    private boolean isTurnedOn;

    private LocalDateTime activateFrom;

    private LocalDateTime activateTo;

    private double electricityConsuming;
    private double voltage;
    private int amps;

    private List<Device> devices;



}
