package com.misha.sh.devicemanagementmicroservice.model.airQualtiySensor;

import com.misha.sh.devicemanagementmicroservice.model.device.Device;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
public class AirQualitySensor extends Device {

    // Концентрация частиц PM2.5 (мкг/м³)
    private Double pm25Level;

    // Концентрация частиц PM10 (мкг/м³)
    private Double pm10Level;

    // Уровень CO2 (ppm)
    private Integer co2Level;

    // Уровень CO (ppm)
    private Double coLevel;

    // Уровень NO2 (ppb)
    private Double no2Level;

    // Уровень O3 (ppb)
    private Double o3Level;

    // Уровень летучих органических соединений (VOC) (ppb)
    private Integer vocLevel;

    // Температура (°C)
    private Double temperature;

    // Влажность (%)
    private Double humidity;

    // Атмосферное давление (гПа)
    private Double pressure;

    // Индекс качества воздуха (AQI)
    private Integer airQualityIndex;

    // Частота обновления данных (в секундах)
    private Integer updateFrequency;

    // Порог уведомления для PM2.5
    private Double pm25AlertThreshold;

    // Порог уведомления для CO2
    private Integer co2AlertThreshold;

    // Флаг наличия Wi-Fi модуля
    private Boolean hasWifiModule;

    // Точность измерений (%)
    private Double accuracy;

    // Дата последней калибровки
    private LocalDateTime lastCalibrationDate;

}
