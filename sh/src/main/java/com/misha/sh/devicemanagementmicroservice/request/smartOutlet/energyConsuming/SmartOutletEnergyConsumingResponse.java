package com.misha.sh.devicemanagementmicroservice.request.smartOutlet.energyConsuming;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SmartOutletEnergyConsumingResponse {

    private Integer outletId;
    private Double voltage;
    private Integer amps;
    private String energyConsumingPerHours;
    private Integer ownerId;

}
