package com.misha.sh.devicemanagementmicroservice.request.device.battery;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeviceBatteryRequest {

    private Double batteryPercentage;


}
