package com.misha.sh.devicemanagementmicroservice.service.airQualitySensorService;

import com.misha.sh.devicemanagementmicroservice.exception.BusinessException;
import com.misha.sh.devicemanagementmicroservice.mapper.AirQualitySensorMapper;
import com.misha.sh.devicemanagementmicroservice.mapper.SmartOutletMapper;
import com.misha.sh.devicemanagementmicroservice.model.User;
import com.misha.sh.devicemanagementmicroservice.model.airQualtiySensor.AirQualitySensor;
import com.misha.sh.devicemanagementmicroservice.pagination.PageResponse;
import com.misha.sh.devicemanagementmicroservice.repository.AirQualitySensorRepository;
import com.misha.sh.devicemanagementmicroservice.request.airQualitySensor.AirQualitySensorDataRequest;
import com.misha.sh.devicemanagementmicroservice.request.airQualitySensor.AirQualitySensorDataResponse;
import com.misha.sh.devicemanagementmicroservice.request.airQualitySensor.AirQualitySensorRequest;
import com.misha.sh.devicemanagementmicroservice.request.airQualitySensor.AirQualitySensorResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class AirQualitySensorService {

    private final AirQualitySensorRepository airQualitySensorRepository;
    private final AirQualitySensorMapper airQualitySensorMapper;
    private final SmartOutletMapper smartOutletMapper;

    @Transactional(rollbackOn = Exception.class)
    public AirQualitySensorResponse addNewAirQualitySensor(AirQualitySensorRequest request, Authentication authentication) {
        User user = ((User) authentication.getPrincipal());
        AirQualitySensor sensor = airQualitySensorMapper.toAirQualitySensor(request);
        sensor.setUser(user);
        sensor.setBatteryLevel(100.0);
        turnOn(sensor);
        DefaultPowerNode(sensor);
        airQualitySensorRepository.save(sensor);
        return airQualitySensorMapper.toAirQualitySensorResponse(sensor);
    }

    public void removeAirQualitySensor(Integer sensorId, Authentication authentication) {
        User user = ((User) authentication.getPrincipal());
        AirQualitySensor sensor = airQualitySensorRepository.findById(sensorId).orElse(null);
        if(!sensor.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You dont have permission to access this sensor");
        }
        airQualitySensorRepository.deleteById(sensorId);
    }

    public AirQualitySensorResponse findAirQualitySensorById(Integer sensorId,
                                                             Authentication authentication) {
        return findSensorById(sensorId, authentication);
    }

    public PageResponse<AirQualitySensorResponse> findAllAirQualitySensors(int page,
                                                                           int size,
                                                                           Authentication authentication) {
        User user = ((User) authentication.getPrincipal());

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<AirQualitySensor> airQualitySensors = airQualitySensorRepository.findAllAirQualitySensorByUser(pageable, user.getId());
        List<AirQualitySensorResponse> airQualitySensorResponses = airQualitySensors.getContent()
                .stream()
                .map(airQualitySensor -> {
                    if (user.getId().equals(airQualitySensor.getUser().getId())) {
                        return airQualitySensorMapper.toAirQualitySensorResponse(airQualitySensor);
                    } else {
                        throw new AccessDeniedException("You dont have permission to access this sensor");
                    }
                })
                .toList();
        return new PageResponse<>(
                airQualitySensorResponses,
                airQualitySensors.getNumber(),
                airQualitySensors.getSize(),
                airQualitySensors.getTotalElements(),
                airQualitySensors.getTotalPages(),
                airQualitySensors.isFirst(),
                airQualitySensors.isLast()
        );
    }

    public AirQualitySensorDataResponse sendAirData(Integer sensorId,
                                                    AirQualitySensorDataRequest request,
                                                    Authentication authentication) {
        User user = ((User) authentication.getPrincipal());
        authenticateUser(authentication, user.getId());
        var airQualitySensor = airQualitySensorRepository
                .findById(sensorId).orElseThrow(() -> new EntityNotFoundException("No air quality sensor found"));
        var updatedAirQualitySensorData = updateSensorAirQualityData(request, airQualitySensor);
         return airQualitySensorMapper.toAirQualitySensorDataResponse(updatedAirQualitySensorData);
    }

    public AirQualitySensorResponse getAllAirData(Integer sensorId,
                                                  Authentication authentication) {
        User user = ((User) authentication.getPrincipal());
        authenticateUser(authentication, user.getId());
        var airQualitySensor = airQualitySensorRepository.findById(sensorId).orElseThrow(
                () -> new EntityNotFoundException("No air quality sensor found")
        );
        return airQualitySensorMapper.toAirQualitySensorResponse(airQualitySensor);
    }
    @Transactional(rollbackOn = Exception.class)
        public void setLowPowerMode(Integer sensorId,
                                    Authentication authentication) {
            User user = ((User) authentication.getPrincipal());
            authenticateUser(authentication, user.getId());
            var airQualitySensor = airQualitySensorRepository.findById(sensorId).orElseThrow(
                    () -> new EntityNotFoundException("No air quality sensor found")
            );
            if (!airQualitySensor.isActive()) {
                log.warn("Sensor with id are not active:{}", airQualitySensor.getId());
                throw new BusinessException("Sensor with this id are not active!" +
                        " Please turn on device!");
            }
            log.warn("Turning on Low power mode!");
            LowPowerMode(airQualitySensor);
            log.info("Low mode was turn on successfully!");
    }
    @Transactional(rollbackOn = Exception.class)
    public void setDefaultPowerMode(Integer sensorId, Authentication authentication) {
        User user = ((User) authentication.getPrincipal());
        authenticateUser(authentication, user.getId());
        var airQualitySensor = airQualitySensorRepository.findById(sensorId).orElseThrow(
                () -> new EntityNotFoundException("No air quality sensor found")
        );
            log.warn("Turning on default power mode!");
            DefaultPowerNode(airQualitySensor);
            log.info("Default mode was turn on successfully!");
    }

        public void turnOnAirQualitySensor(Integer sensorId, Authentication authentication) {
        User user = ((User) authentication.getPrincipal());
        authenticateUser(authentication, user.getId());
        var airQualitySensor = airQualitySensorRepository.findById(sensorId).orElseThrow(
                () -> new EntityNotFoundException("No air quality sensor found")
        );
        if(airQualitySensor.isActive()
                    && airQualitySensor.isTurnOn()
                        && !airQualitySensor.isTurnOff()) {
            log.warn("Turning on sensor!");
            throw new BusinessException("Sensor with this id is not active!");
        }
            DefaultPowerNode(airQualitySensor);
            turnOn(airQualitySensor);

        }

        public void turnOffAirQualitySensor(Integer sensorId, Authentication authentication) {
        User user = ((User) authentication.getPrincipal());
        authenticateUser(authentication, user.getId());
        var airQualitySensor = airQualitySensorRepository.findById(sensorId).orElseThrow(
                () -> new EntityNotFoundException("No air quality sensor found")
        );
        if(     !airQualitySensor.isActive()
                    && airQualitySensor.isTurnOff()
                     && !airQualitySensor.isTurnOn()) {
            log.warn("Sensor are already turned off!");
            throw new BusinessException("Sensor with this id is not active!");
        }
        turnOff(airQualitySensor);
        }


        private void turnOn(AirQualitySensor airQualitySensor) {
            airQualitySensor.setActive(true);
            airQualitySensor.setConnected(true);
            airQualitySensor.setTurnOn(true);
            airQualitySensor.setTurnOff(false);
            airQualitySensorRepository.save(airQualitySensor);
        }

        private void turnOff(AirQualitySensor airQualitySensor) {
            airQualitySensor.setActive(false);
            airQualitySensor.setConnected(false);
            airQualitySensor.setTurnOn(false);
            airQualitySensor.setTurnOff(true);
            airQualitySensorRepository.save(airQualitySensor);
    }

    private void LowPowerMode(AirQualitySensor airQualitySensor){
            airQualitySensor.setVoltage(5.0);
            airQualitySensor.setAmps(5);
            airQualitySensor.setEnergyConsumingPerHours("0.5Kw");
            airQualitySensorRepository.save(airQualitySensor);
    }


    public void DefaultPowerNode(AirQualitySensor airQualitySensor){
        airQualitySensor.setVoltage(20.0);
        airQualitySensor.setAmps(10);
        airQualitySensor.setEnergyConsumingPerHours("0.8Kw");
        airQualitySensorRepository.save(airQualitySensor);
    }


    private AirQualitySensor updateSensorAirQualityData(AirQualitySensorDataRequest request, AirQualitySensor sensor){
        BeanUtils.copyProperties(request, sensor);
        sensor.setLastCalibrationDate(LocalDateTime.now());
        sensor.setUpdatedAt(LocalDateTime.now());
        return airQualitySensorRepository.save(sensor);
    }


    private AirQualitySensorResponse findSensorById(Integer sensorId, Authentication authentication) {
        User user = ((User) authentication.getPrincipal());
        authenticateUser(authentication, user.getId());
        var sensor = airQualitySensorRepository.findById(sensorId)
                .orElseThrow(() -> new EntityNotFoundException("Sensor with provided id not found"));
        return  airQualitySensorMapper.toAirQualitySensorResponse(sensor);
    }

    private void authenticateUser(Authentication authentication, Integer userId) {
        User user = ((User) authentication.getPrincipal());
        if (!user.getId().equals(userId)) {
            log.warn("You dont have permission to access this sensor");
            throw new AccessDeniedException("You dont have permission to access this sensor");
        }
    }










}
