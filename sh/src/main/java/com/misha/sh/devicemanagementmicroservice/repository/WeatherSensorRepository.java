package com.misha.sh.devicemanagementmicroservice.repository;

import com.misha.sh.devicemanagementmicroservice.model.sensor.WeatherSensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WeatherSensorRepository extends JpaRepository<WeatherSensor, Integer> {


}
