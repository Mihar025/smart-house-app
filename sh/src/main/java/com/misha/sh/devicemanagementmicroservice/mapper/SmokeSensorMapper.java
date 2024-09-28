package com.misha.sh.devicemanagementmicroservice.mapper;

import com.misha.sh.devicemanagementmicroservice.model.smokeSensor.SmokeSensor;
import com.misha.sh.devicemanagementmicroservice.request.smokeSensor.SmokeSensorRequest;
import com.misha.sh.devicemanagementmicroservice.request.smokeSensor.SmokeSensorResponse;
import com.misha.sh.devicemanagementmicroservice.request.smokeSensor.alaramMaintenence.AlarmMaintenanceDateResponse;
import com.misha.sh.devicemanagementmicroservice.request.smokeSensor.alarm.AlarmResponse;
import com.misha.sh.devicemanagementmicroservice.request.smokeSensor.alarmSensitivity.AlarmSensitivityResponse;
import com.misha.sh.devicemanagementmicroservice.request.smokeSensor.turnOnAndOffSmokeSensor.SmokeSensorTurnOffResponse;
import com.misha.sh.devicemanagementmicroservice.request.smokeSensor.turnOnAndOffSmokeSensor.SmokeSensorTurnOnResponse;
import org.springframework.stereotype.Service;

@Service
public class SmokeSensorMapper {
    public SmokeSensor toSmokeSensor(SmokeSensorRequest smokeSensorRequest) {
        return SmokeSensor.builder()
                .deviceName(smokeSensorRequest.getDeviceName())
                .deviceDescription(smokeSensorRequest.getDeviceDescription())
                .manufacturer(smokeSensorRequest.getManufacturer())
                .deviceModel(smokeSensorRequest.getDeviceModel())
                .serialNumber(String.valueOf(smokeSensorRequest.getSerialNumber()))
                .build();
    }

    public SmokeSensorResponse toSmokeSensorResponse(SmokeSensor smokeSensor) {
        return SmokeSensorResponse.builder()
                .smokeSensorId(smokeSensor.getId())
                .deviceName(smokeSensor.getDeviceName())
                .deviceDescription(smokeSensor.getDeviceDescription())
                .manufacturer(smokeSensor.getManufacturer())
                .deviceModel(smokeSensor.getDeviceModel())
                .serialNumber(smokeSensor.getSerialNumber())
                .build();
    }


    public AlarmResponse toAlarmResponse(SmokeSensor savedSmokesensor) {
        return AlarmResponse.builder()
                .alarmId(savedSmokesensor.getId())
                .alarmName(savedSmokesensor.getDeviceName())
                .SmokeValue(savedSmokesensor.getSmokeLevel())
                .alarmThreshold(savedSmokesensor.getSmokeThreshold())
                .lastAlarmTime(savedSmokesensor.getLastAlarmTime())
                .alarmActive(savedSmokesensor.getAlarmActive())
                .build();
    }

        public AlarmSensitivityResponse toAlarmSensitivityResponse(SmokeSensor newSmokeSensor) {
            return AlarmSensitivityResponse.builder()
                    .smokeSensorId(newSmokeSensor.getId())
                    .valueForSensitivity(newSmokeSensor.getSmokeLevel())
                    .sensitivity(newSmokeSensor.getSensitivity())
                    .build();
        }


    public AlarmMaintenanceDateResponse toAlarmMaintenanceDateResponse(SmokeSensor smokeSensor) {
        return AlarmMaintenanceDateResponse.builder()
                .smokeSensorId(smokeSensor.getId())
                .lastMaintenanceDate(smokeSensor.getLastMaintenanceDate())
                .build();
    }

    public SmokeSensorTurnOnResponse toSmokeSensorTurnOnResponse(SmokeSensor smokeSensor) {
        return SmokeSensorTurnOnResponse.builder()
                .smokeSensorId(smokeSensor.getId())
                .turnOn(smokeSensor.isTurnOn())
                .turnOff(smokeSensor.isTurnOff())
                .isConnected(smokeSensor.isConnected())
                .build();
    }

    public SmokeSensorTurnOffResponse toSmokeSensorTurnOffResponse(SmokeSensor smokeSensor) {
        return SmokeSensorTurnOffResponse.builder()
                .smokeSensorId(smokeSensor.getId())
                .turnOn(smokeSensor.isTurnOn())
                .turnOff(smokeSensor.isTurnOff())
                .isConnected(smokeSensor.isConnected())
                .build();
    }
}
