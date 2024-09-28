package com.misha.sh.devicemanagementmicroservice.request.smokeSensor.turnOnAndOffSmokeSensor;

import lombok.*;

@Data
@Builder
public class SmokeSensorTurnOnResponse {

        private Integer smokeSensorId;

        private boolean isConnected;

        private boolean turnOn;

        private boolean turnOff;
}
