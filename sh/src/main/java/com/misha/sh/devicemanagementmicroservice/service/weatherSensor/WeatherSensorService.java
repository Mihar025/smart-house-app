package com.misha.sh.devicemanagementmicroservice.service.weatherSensor;

import com.misha.sh.devicemanagementmicroservice.exception.BusinessCreatingFailedException;
import com.misha.sh.devicemanagementmicroservice.exception.BusinessDeleteException;
import com.misha.sh.devicemanagementmicroservice.exception.BusinessException;
import com.misha.sh.devicemanagementmicroservice.mapper.WeatherMapper;
import com.misha.sh.devicemanagementmicroservice.model.sensor.WeatherSensor;
import com.misha.sh.devicemanagementmicroservice.model.User;
import com.misha.sh.devicemanagementmicroservice.pagination.PageResponse;
import com.misha.sh.devicemanagementmicroservice.repository.WeatherSensorRepository;
import com.misha.sh.devicemanagementmicroservice.request.weatherSensor.addSensor.WeatherSensorRequest;
import com.misha.sh.devicemanagementmicroservice.request.weatherSensor.addSensor.WeatherSensorResponse;
import com.misha.sh.devicemanagementmicroservice.request.weatherSensor.energyConsumingSensor.WeatherSensorEnergyRequest;
import com.misha.sh.devicemanagementmicroservice.request.weatherSensor.energyConsumingSensor.WeatherSensorEnergyResponse;
import com.misha.sh.devicemanagementmicroservice.request.weatherSensor.weatherData.WeatherDataRequest;
import com.misha.sh.devicemanagementmicroservice.request.weatherSensor.weatherData.WeatherDataResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherSensorService {


    private final WeatherSensorRepository weatherSensorRepository;
    private final WeatherMapper weatherMapper;

    public PageResponse<WeatherSensorResponse> findAllAvailableSensors(int page, int size, Authentication authentication) {
        User user = ((User) authentication.getPrincipal());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<WeatherSensor> sensor = weatherSensorRepository.findAll(pageable);

        List<WeatherSensorResponse> responses = sensor.getContent().stream()
                .map(sensors -> {
                    if(user.getId().equals(sensors.getUser().getId())) {
                        return weatherMapper.toWeatherSensorResponse(sensors);
                    }
                    else{
                        throw new BusinessException("You dont have an access to this device!");
                    }
                })
                .toList();

        return new PageResponse<>(
                responses,
                sensor.getNumber(),
                sensor.getSize(),
                sensor.getTotalElements(),
                sensor.getTotalPages(),
                sensor.isFirst(),
                sensor.isLast()
        );
    }

    //WORKING
    public WeatherSensorResponse findSensorById(Integer sensorId, Authentication authentication) {
        User user = ((User) authentication.getPrincipal());
        var weatherSensor = weatherSensorRepository.findById(sensorId)
                .orElseThrow(() -> new BusinessException("Cannot find WeatherSensor with provided id"));
        if(user.getId().equals(weatherSensor.getUser().getId())) {
            return weatherMapper.toWeatherSensorResponse(weatherSensor);
        }
        else {
            throw new EntityNotFoundException("Cannot find WeatherSensor with provided id");
        }
    }

    //WORKING
    @Transactional(rollbackOn = Exception.class)
    public WeatherSensorResponse addSensor(WeatherSensorRequest sensor, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        var mappedWeatherSensor = weatherMapper.toWeatherSensor(sensor);
        mappedWeatherSensor.setUser(user);
        mappedWeatherSensor.setActive(true);
        mappedWeatherSensor.setConnected(true);
        defaultEnergyConsuming(mappedWeatherSensor);
        var savedWeatherSensor = weatherSensorRepository.save(mappedWeatherSensor);
        return weatherMapper.toWeatherSensorResponse(savedWeatherSensor);
    }

    //Working
        @Transactional(rollbackOn = BusinessDeleteException.class)
        public void removeSensor(Integer id, Authentication authentication) {
        User user = ((User) authentication.getPrincipal());
            var foundedSensor = weatherSensorRepository.findById(id)
                    .orElseThrow(() -> new BusinessException("Sensor not found"));
            if(user.getId().equals(foundedSensor.getUser().getId())) {
                weatherSensorRepository.delete(foundedSensor);
            }
            else{
                throw new BusinessDeleteException("Cannot delete WeatherSensor with provided id");
            }
        }
    //WORKING

     public WeatherSensorEnergyResponse getWeatherSensorEnergyConsuming(Integer sensorId, Authentication authentication) {
        User user = ((User) authentication.getPrincipal());
        var foundedSensor = weatherSensorRepository.findById(sensorId)
                .orElseThrow(() -> new BusinessException("Sensor not found"));
        if(!user.getId().equals(foundedSensor.getUser().getId())) {
            throw new BusinessDeleteException("You dont have permission to access this sensor");
        }
        WeatherSensorEnergyResponse response =  weatherMapper.toWeatherSensorEnergyResponse(foundedSensor);
        if(response.getVoltage() == null &&
                response.getAmps()==null &&
                response.getEnergyConsumingPerHours() == null) {
                throw new BusinessException("Data doesnt exist!");
        }
        return response;
    }
// Working
    public WeatherSensorEnergyResponse changeOnCustomEnergyConsuming(WeatherSensorEnergyRequest request,Integer sensorId, Authentication authentication) {
        User user = ((User) authentication.getPrincipal());
            var foundedSensor = weatherSensorRepository.findById(sensorId)
                    .orElseThrow(() -> new BusinessException("Sensor not found"));
            if(!user.getId().equals(foundedSensor.getUser().getId())) {
                throw new BusinessException("You dont have permission to access this sensor");
            }
            if(foundedSensor.isActive() && foundedSensor.isConnected()){
                if (request.getAmps() == null && request.getEnergyConsumingPerHours() == null && request.getVoltage() == null) {
                    throw new BusinessException("Data doesnt exist!");
                }
                    customEnergyConsuming(request, foundedSensor);
                    weatherSensorRepository.save(foundedSensor);
            }
            else {
                throw new BusinessException("Sensor is not active or not connected!");
            }

        return weatherMapper.toWeatherSensorEnergyResponse(foundedSensor);
    }

        //Working
        public WeatherDataResponse sendAndUpdateWeatherData(WeatherDataRequest weatherDataRequest, Integer sensorId,Authentication authentication) {
        User user = ((User) authentication.getPrincipal());
            var foundedSensor = weatherSensorRepository.findById(sensorId)
                    .orElseThrow(() -> new BusinessException("Sensor not found"));
            if(!user.getId().equals(foundedSensor.getUser().getId())) {
                throw new BusinessException("You dont have permission to access this sensor");
            }

            updateWeatherSensorData(weatherDataRequest, foundedSensor);
            log.info("Successfully send data!");
            weatherSensorRepository.save(foundedSensor);
            log.info("Successfully saved all data!");
            return weatherMapper.toWeatherDataResponse(foundedSensor);
        }
            //Working
            public WeatherDataResponse findWeatherData(Integer sensorId, Authentication authentication) {
            User user = ((User) authentication.getPrincipal());
            var foundedSensor = weatherSensorRepository.findById(sensorId)
                    .orElseThrow(() -> new BusinessException("Sensor not found"));
            if(!user.getId().equals(foundedSensor.getUser().getId())) {
                throw new BusinessDeleteException("You dont have permission to access this sensor");
            }
            return weatherMapper.toWeatherDataResponse(foundedSensor);
            }





    private void customEnergyConsuming(WeatherSensorEnergyRequest request, WeatherSensor weatherSensor){
        weatherSensor.setVoltage(request.getVoltage());
        weatherSensor.setAmps(request.getAmps());
        weatherSensor.setEnergyConsumingPerHours(request.getEnergyConsumingPerHours());
    }

    private void defaultEnergyConsuming(WeatherSensor weatherSensor){
        weatherSensor.setVoltage(5.0);
        weatherSensor.setAmps(3);
        weatherSensor.setEnergyConsumingPerHours("0.05Kw");
    }

    private void updateWeatherSensorData(WeatherDataRequest request, WeatherSensor weatherSensor) {
        weatherSensor.setLatitude(request.getLatitude());
        weatherSensor.setLongitude(request.getLongitude());
        weatherSensor.setTemperature(request.getTemperature());
        weatherSensor.setHumidity(request.getHumidity());
        weatherSensor.setPressure(request.getPressure());
        weatherSensor.setWindSpeed(request.getWindSpeed());
        weatherSensor.setWindDirection(request.getWindDirection());
        weatherSensor.setPrecipitation(request.getPrecipitation());
        weatherSensor.setLastUpdateTime(LocalDateTime.now());
    }










}
