package com.misha.sh.devicemanagementmicroservice.mapper;

import com.misha.sh.devicemanagementmicroservice.model.sensor.WeatherSensor;
import com.misha.sh.devicemanagementmicroservice.model.User;
import com.misha.sh.devicemanagementmicroservice.request.weatherSensor.addSensor.WeatherSensorRequest;
import com.misha.sh.devicemanagementmicroservice.request.weatherSensor.addSensor.WeatherSensorResponse;
import com.misha.sh.devicemanagementmicroservice.request.weatherSensor.energyConsumingSensor.WeatherSensorEnergyRequest;
import com.misha.sh.devicemanagementmicroservice.request.weatherSensor.energyConsumingSensor.WeatherSensorEnergyResponse;
import com.misha.sh.devicemanagementmicroservice.request.weatherSensor.weatherData.WeatherDataRequest;
import com.misha.sh.devicemanagementmicroservice.request.weatherSensor.weatherData.WeatherDataResponse;
import org.springframework.stereotype.Service;

@Service
public class WeatherMapper {
    public WeatherSensor toWeatherSensor(WeatherSensorRequest weatherSensor) {
        return WeatherSensor.builder()
                .deviceName(weatherSensor.getDeviceName())
                .deviceType(weatherSensor.getDeviceType())
                .deviceDescription(weatherSensor.getDeviceDescription())
                .manufacturer(weatherSensor.getManufacturer())
                .deviceModel(weatherSensor.getDeviceModel())
                .serialNumber(weatherSensor.getSerialNumber())
                .status(weatherSensor.getStatus())
                .isActive(true)
                .build();
    }


    public WeatherSensorResponse toWeatherSensorResponse(WeatherSensor weatherSensor) {
        return WeatherSensorResponse.builder()
                .id(weatherSensor.getId())
                .deviceName(weatherSensor.getDeviceName())
                .deviceType(weatherSensor.getDeviceType())
                .deviceDescription(weatherSensor.getDeviceDescription())
                .manufacturer(weatherSensor.getManufacturer())
                .deviceModel(weatherSensor.getDeviceModel())
                .isActive(weatherSensor.isActive())
                .isConnected(weatherSensor.isConnected())
                .voltage(weatherSensor.getVoltage())
                .amps(weatherSensor.getAmps())
                .energyConsumingPerHours(weatherSensor.getEnergyConsumingPerHours())
                .createdAt(weatherSensor.getCreatedDate())
                .updatedAt(weatherSensor.getUpdatedAt())
                .status(weatherSensor.getStatus())
                .isConnected(true)
                .ownerId(weatherSensor.getUser().getId())
                .build();
    }



    public WeatherSensorEnergyResponse toWeatherSensorEnergyResponse(WeatherSensor weatherSensor) {
        return WeatherSensorEnergyResponse.builder()
                .sensorId(weatherSensor.getId())
                .voltage(weatherSensor.getVoltage())
                .amps(weatherSensor.getAmps())
                .energyConsumingPerHours(weatherSensor.getEnergyConsumingPerHours())
                .ownerId(weatherSensor.getUser().getId())
                .build();
    }
    public WeatherSensor toWeatherSensorEnergy(WeatherSensorEnergyRequest weatherSensor) {
        return WeatherSensor.builder()
                .voltage(weatherSensor.getVoltage())
                .amps(weatherSensor.getAmps())
                .energyConsumingPerHours(weatherSensor.getEnergyConsumingPerHours())
                .build();
    }






    public WeatherSensor toWeatherData(WeatherDataRequest weatherSensor) {
        return WeatherSensor.builder()
                .latitude(weatherSensor.getLatitude())
                .longitude(weatherSensor.getLongitude())
                .temperature(weatherSensor.getTemperature())
                .humidity(weatherSensor.getHumidity())
                .pressure(weatherSensor.getPressure())
                .windSpeed(weatherSensor.getWindSpeed())
                .windDirection(weatherSensor.getWindDirection())
                .precipitation(weatherSensor.getPrecipitation())
                .lastUpdateTime(weatherSensor.getLastUpdateTime())
                .build();
    }

    public WeatherDataResponse toWeatherDataResponse(WeatherSensor weatherSensor) {
        return WeatherDataResponse.builder()
                .sensorId(weatherSensor.getId())
                .longitude(weatherSensor.getLongitude())
                .latitude(weatherSensor.getLatitude())
                .temperature(weatherSensor.getTemperature())
                .humidity(weatherSensor.getHumidity())
                .pressure(weatherSensor.getPressure())
                .windSpeed(weatherSensor.getWindSpeed())
                .windDirection(weatherSensor.getWindDirection())
                .precipitation(weatherSensor.getPrecipitation())
                .lastUpdateTime(weatherSensor.getLastUpdateTime())
                .ownerId(weatherSensor.getUser().getId())
                .build();
    }




}
