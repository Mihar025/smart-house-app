package com.misha.sh.devicemanagementmicroservice.request.smokeSensor.turnOnAndOffSmokeSensor;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SmokeSensorTurnOffResponse {

    private Integer smokeSensorId;

    private boolean isConnected;

    private boolean turnOn;

    private boolean turnOff;
}
