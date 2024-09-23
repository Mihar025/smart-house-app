package com.misha.sh.devicemanagementmicroservice.mapper;

import com.misha.sh.devicemanagementmicroservice.model.airQualtiySensor.AirQualitySensor;
import com.misha.sh.devicemanagementmicroservice.request.airQualitySensor.AirQualitySensorDataRequest;
import com.misha.sh.devicemanagementmicroservice.request.airQualitySensor.AirQualitySensorDataResponse;
import com.misha.sh.devicemanagementmicroservice.request.airQualitySensor.AirQualitySensorRequest;
import com.misha.sh.devicemanagementmicroservice.request.airQualitySensor.AirQualitySensorResponse;
import org.springframework.stereotype.Service;

@Service
public class AirQualitySensorMapper {


    public AirQualitySensor toAirQualitySensor(AirQualitySensorRequest request) {
        return AirQualitySensor.builder()
                .deviceName(request.getDeviceName())
                .deviceDescription(request.getDeviceDescription())
                .manufacturer(request.getManufacturer())
                .deviceModel(request.getDeviceModel())
                .serialNumber(request.getSerialNumber())
                .location(request.getLocation())
                .pm25AlertThreshold(request.getPm25AlertThreshold())
                .co2AlertThreshold(request.getCo2AlertThreshold())
                .accuracy(request.getAccuracy())
                .updateFrequency(request.getUpdateFrequency())
                .build();
    }



    public AirQualitySensorResponse toAirQualitySensorResponse(AirQualitySensor sensor) {
        return AirQualitySensorResponse.builder()
                .id(sensor.getId())
                .deviceName(sensor.getDeviceName())
                .isActive(sensor.isActive())
                .turnOn(sensor.isTurnOn())
                .pm25Level(sensor.getPm25Level())
                .pm10Level(sensor.getPm10Level())
                .co2Level(sensor.getCo2Level())
                .coLevel(sensor.getCoLevel())
                .no2Level(sensor.getNo2Level())
                .o3Level(sensor.getO3Level())
                .vocLevel(sensor.getVocLevel())
                .temperature(sensor.getTemperature())
                .humidity(sensor.getHumidity())
                .pressure(sensor.getPressure())
                .airQualityIndex(sensor.getAirQualityIndex())
                .updateFrequency(sensor.getUpdateFrequency())
                .pm25AlertThreshold(sensor.getPm25AlertThreshold())
                .co2AlertThreshold(sensor.getCo2AlertThreshold())
                .hasWifiModule(sensor.getHasWifiModule())
                .accuracy(sensor.getAccuracy())
                .lastCalibrationDate(sensor.getLastCalibrationDate())
                .build();
    }

    public AirQualitySensor toAirQualitySensorData(AirQualitySensorDataRequest request) {
        return AirQualitySensor.builder()
                .pm25Level(request.getPm25Level())
                .pm10Level(request.getPm10Level())
                .co2Level(request.getCo2Level())
                .coLevel(request.getCoLevel())
                .no2Level(request.getNo2Level())
                .o3Level(request.getO3Level())
                .vocLevel(request.getVocLevel())
                .temperature(request.getTemperature())
                .humidity(request.getHumidity())
                .pressure(request.getPressure())
                .airQualityIndex(request.getAirQualityIndex())
                .updateFrequency(request.getUpdateFrequency())
                .pm25AlertThreshold(request.getPm25AlertThreshold())
                .co2AlertThreshold(request.getCo2AlertThreshold())
                .build();
    }

    public AirQualitySensorDataResponse toAirQualitySensorDataResponse(AirQualitySensor airQualitySensor) {
        return AirQualitySensorDataResponse.builder()
                .sensorId(airQualitySensor.getId())
                .pm25Level(airQualitySensor.getPm25Level())
                .pm10Level(airQualitySensor.getPm10Level())
                .co2Level(airQualitySensor.getCo2Level())
                .coLevel(airQualitySensor.getCoLevel())
                .no2Level(airQualitySensor.getNo2Level())
                .o3Level(airQualitySensor.getO3Level())
                .vocLevel(airQualitySensor.getVocLevel())
                .temperature(airQualitySensor.getTemperature())
                .humidity(airQualitySensor.getHumidity())
                .pressure(airQualitySensor.getPressure())
                .airQualityIndex(airQualitySensor.getAirQualityIndex())
                .updateFrequency(airQualitySensor.getUpdateFrequency())
                .pm25AlertThreshold(airQualitySensor.getPm25AlertThreshold())
                .co2AlertThreshold(airQualitySensor.getCo2AlertThreshold())
                .build();
    }


}
