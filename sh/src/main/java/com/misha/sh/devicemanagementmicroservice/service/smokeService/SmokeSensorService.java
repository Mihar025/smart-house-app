package com.misha.sh.devicemanagementmicroservice.service.smokeService;

import com.misha.sh.devicemanagementmicroservice.exception.BusinessException;
import com.misha.sh.devicemanagementmicroservice.mapper.SmokeSensorMapper;
import com.misha.sh.devicemanagementmicroservice.model.User;
import com.misha.sh.devicemanagementmicroservice.model.smokeSensor.SmokeSensor;
import com.misha.sh.devicemanagementmicroservice.pagination.PageResponse;
import com.misha.sh.devicemanagementmicroservice.repository.SmokeSensorRepository;
import com.misha.sh.devicemanagementmicroservice.request.smokeSensor.SmokeSensorRequest;
import com.misha.sh.devicemanagementmicroservice.request.smokeSensor.SmokeSensorResponse;
import com.misha.sh.devicemanagementmicroservice.request.smokeSensor.alarm.AlarmRequest;
import com.misha.sh.devicemanagementmicroservice.request.smokeSensor.alarm.AlarmResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.jackson.JsonMixinModuleEntries;
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
@Service
@RequiredArgsConstructor
public class SmokeSensorService {

    private final SmokeSensorRepository smokeSensorRepository;
    private final SmokeSensorMapper smokeSensorMapper;
    private final JsonMixinModuleEntries jsonMixinModuleEntries;

    //todo find all
    //todo findById


    @Transactional
    public SmokeSensorResponse addNewSmokeSensor(SmokeSensorRequest smokeSensorRequest) {
        var mappedSensor = smokeSensorMapper.toSmokeSensor(smokeSensorRequest);
        mappedSensor.setActive(true);
        mappedSensor.setTurnOn(true);
        mappedSensor.setConnected(true);
        smokeSensorRepository.save(mappedSensor);
        log.info("Added new smoke sensor: {}", mappedSensor);
        log.info("Beginning smoke sensor mapping");
        return smokeSensorMapper.toSmokeSensorResponse(mappedSensor);
    }
        public SmokeSensorResponse findSensorById(Integer smokeSensorId, Authentication authentication) {
            User user = ((User) authentication.getPrincipal());
            var smokeSensor = smokeSensorRepository.findById(smokeSensorId).orElseThrow(()
            -> new RuntimeException("Smoke sensor not found")
            );
            log.info("Smoke sensor was successfully found with id: {}", smokeSensorId);
                    if(!user.getId().equals(smokeSensor.getUser().getId())) {
                        log.warn("Smoke sensor {} is not owned by user {}", smokeSensorId, user.getId());
                        throw new AccessDeniedException("You dont have permission to access smoke sensor");
                    }
            log.info("Beginning mapping smoke sensor!");
            return smokeSensorMapper.toSmokeSensorResponse(smokeSensor);
        }
            @Transactional(rollbackOn = BusinessException.class)
            public void removeSmokeSensor(Integer smokeSensorId, Authentication authentication) {
                    User user = ((User) authentication.getPrincipal());
                    var smokeSensor = smokeSensorRepository.findById(smokeSensorId)
                            .orElseThrow(
                                    () -> new EntityNotFoundException("Smoke sensor not found")
                            );
                    log.info("Smoke sensor was successfully found: {}", smokeSensor);
                    if(!user.getId().equals(smokeSensor.getUser().getId())) {
                        log.warn("Smoke sensor {} is not owned by user {}", smokeSensorId, user.getId());
                        throw new AccessDeniedException("You dont have permission to access smoke sensor");
                    }
                    log.info("Beginning removing smoke sensor with id: {}!", smokeSensorId);
                    smokeSensorRepository.deleteById(smokeSensorId);
            }
                public PageResponse<SmokeSensorResponse> findAllSmokeSensors(int page, int size, Authentication authentication) {
                                User user = ((User) authentication.getPrincipal());
                        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
                    Page<SmokeSensor> smokeSensors = smokeSensorRepository.findAllUserSmokeSensor(pageable, user.getId());
                    List<SmokeSensorResponse> smokeSensorResponses = smokeSensors.getContent().stream()
                            .map(smokeSensor -> {
                                if(smokeSensor.getUser().getId().equals(user.getId())) {
                                    return smokeSensorMapper.toSmokeSensorResponse(smokeSensor);
                                }
                                else{
                                    throw new AccessDeniedException("You dont have permission to access smoke sensor");
                                }
                            })
                            .toList();
                                return new PageResponse<>(
                                        smokeSensorResponses,
                                        smokeSensors.getNumber(),
                                        smokeSensors.getSize(),
                                        smokeSensors.getTotalElements(),
                                        smokeSensors.getTotalPages(),
                                        smokeSensors.isFirst(),
                                        smokeSensors.isLast()
                                );
    }


        /*  todo make function which will check current smoke level! if smoke threshold is higher than normal turn on alarm function!
            todo alarm function turnOn
            todo alarm function turnOff
            todo set alarm sensitivity
            todo function which making self testing
            todo function which showing you last MaintenanceDate!
            todo function which service people setting last MaintenanceDate!
            todo  function turn on smoke sensor
            todo function turnOff smoke sensor
            todo function setLowEnergyConsuming mode
            todo function setDefaultEnergy consuming mode!
         */

            private static final Double smokeThreshold = 0.5; // in ppm
            private static final Double smokeLevel

    //
        public AlarmResponse turnOnAlarmSmokeLevelHigh(Integer smokeSensorId,
                                                       @Valid AlarmRequest request) {
            var smokeSensor = smokeSensorRepository.findById(smokeSensorId)
                    .orElseThrow(
                            () -> new EntityNotFoundException("Smoke sensor not found")
                    );
                    smokeSensor.setAlarmActive(true);
                    if(smokeSensor.getAlarmActive().equals(true)){
                        setRequestedAlarmData(smokeSensor, request);
                    }
                    var savedSmokesensor = smokeSensorRepository.save(smokeSensor);










            // send notification to the user in email!

        }

        private SmokeSensor setRequestedAlarmData(SmokeSensor smokeSensor, AlarmRequest request) {
            if(request.getSmokeThreshold() >= request.getSmokeLevel()) {
                smokeSensor.setSmokeLevel(request.getSmokeLevel());
                smokeSensor.setSmokeThreshold(request.getSmokeThreshold());
                smokeSensor.setLastAlarmTime(LocalDateTime.now());
            }
            else{
                throw new BusinessException("Problem in setRequestedAlarmData function!");
            }
            return smokeSensor;
        }
            private void sendNotificationToTheUserEmailAboutAlarm(){

            }




    // function which will turn on Alarm
    private void  turnOnAlarm(Authentication authentication, Integer smokeSensorId){
        User user = ((User) authentication.getPrincipal());
    }

}
