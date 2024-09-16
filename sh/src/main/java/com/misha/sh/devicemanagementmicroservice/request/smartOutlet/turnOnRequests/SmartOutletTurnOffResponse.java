package com.misha.sh.devicemanagementmicroservice.request.smartOutlet.turnOnRequests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SmartOutletTurnOffResponse {

    private Integer outletId;
    private boolean isOn;
    private LocalDateTime lastOffTime;

}
