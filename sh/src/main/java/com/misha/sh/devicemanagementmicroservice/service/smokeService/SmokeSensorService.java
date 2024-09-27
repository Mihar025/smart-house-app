package com.misha.sh.devicemanagementmicroservice.service.smokeService;

import com.misha.sh.devicemanagementmicroservice.exception.BusinessException;
import com.misha.sh.devicemanagementmicroservice.mapper.SmokeSensorMapper;
import com.misha.sh.devicemanagementmicroservice.model.User;
import com.misha.sh.devicemanagementmicroservice.model.smokeSensor.SmokeSensor;
import com.misha.sh.devicemanagementmicroservice.pagination.PageResponse;
import com.misha.sh.devicemanagementmicroservice.repository.SmokeSensorRepository;
import com.misha.sh.devicemanagementmicroservice.request.smokeSensor.SmokeSensorRequest;
import com.misha.sh.devicemanagementmicroservice.request.smokeSensor.SmokeSensorResponse;
import com.misha.sh.devicemanagementmicroservice.request.smokeSensor.alaramMaintenence.AlarmMaintenanceDateResponse;
import com.misha.sh.devicemanagementmicroservice.request.smokeSensor.alarm.AlarmRequest;
import com.misha.sh.devicemanagementmicroservice.request.smokeSensor.alarm.AlarmResponse;
import com.misha.sh.devicemanagementmicroservice.request.smokeSensor.alarmSensitivity.AlarmSensitivityResponse;
import com.misha.sh.devicemanagementmicroservice.request.smokeSensor.alarmSensitivity.AlarmSensitivityRequest;
import com.misha.sh.devicemanagementmicroservice.service.emailService.EmailAlarmTemplate;
import com.misha.sh.devicemanagementmicroservice.service.emailService.EmailService;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private static final  Double maxSmokeThreshold = 1.0; // Ths is maximal available value before beginning alarm yell; IN PPM

    private final SmokeSensorRepository smokeSensorRepository;
    private final SmokeSensorMapper smokeSensorMapper;
    private final EmailService emailService;

    //todo find all
    //todo findById


    @Transactional
    public SmokeSensorResponse addNewSmokeSensor(SmokeSensorRequest smokeSensorRequest) {
        var mappedSensor = smokeSensorMapper.toSmokeSensor(smokeSensorRequest);
        mappedSensor.setActive(true);
        mappedSensor.setTurnOn(true);
        mappedSensor.setConnected(true);
        mappedSensor.setValueForSensitivity(maxSmokeThreshold);
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
        //    todo alarm function turnOn
        //    todo alarm function turnOff
       //     todo set alarm sensitivity
            todo function which making self testing
            todo function which showing you last MaintenanceDate!
            todo function which service people setting last MaintenanceDate!
            todo  function turn on smoke sensor
            todo function turnOff smoke sensor
            todo function setLowEnergyConsuming mode
            todo function setDefaultEnergy consuming mode!

            First thing tomorrow!
            todo change authentication in one single method and use for all methods
         */

    // while smoke in apartment should equal danger value alarm working
    // , when value will equals like default value should  turnOffAuto!
        @Transactional(rollbackOn = BusinessException.class)
        public AlarmResponse turnOnAlarmSmokeSensorLevelHigh(Integer smokeSensorId,
                                                       @Valid AlarmRequest request,
                                                       Authentication authentication) throws MessagingException {
            User user = ((User) authentication.getPrincipal());
            var smokeSensor = smokeSensorRepository.findById(smokeSensorId)
                    .orElseThrow(
                            () -> new EntityNotFoundException("Smoke sensor not found")
                    );
                        log.info("Smoke sensor was found: {}", smokeSensor);
                        if(!user.getId().equals(smokeSensor.getUser().getId())) {
                            log.warn("Smoke sensor {} is not owned by user {}", smokeSensorId, user.getId());
                            throw new AccessDeniedException("You dont have permission to access smoke sensor");
                        }
                        setRequestedAlarmData(smokeSensor, request);
                        log.info("Alarm data was successfully set!");

                        var savedSmokesensor = smokeSensorRepository.save(smokeSensor);
                        log.info("Alarm data was successfully saved!");

                            if(smokeSensor.getAlarmActive().equals(true)){
                                log.info("Sending alarm email");
                                sendAlarmEmail(user);

                             }
                                 else {
                                     throw new BusinessException("Big problem with Email Alarm Template!");
                                      }
                                return smokeSensorMapper.toAlarmResponse(savedSmokesensor);
        }

    // function which will turn on Alarm
    public  void  turnOnAlarmThroughApplication(Authentication authentication,
                                                Integer smokeSensorId) throws MessagingException {
        User user = ((User) authentication.getPrincipal());
        var smokeSensor = smokeSensorRepository.findById(smokeSensorId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Smoke sensor not found")
                );
        if(!user.getId().equals(smokeSensor.getUser().getId())) {
            log.warn("Smoke sensor {} is not owned by user {}", smokeSensorId, user.getId());
            throw new AccessDeniedException("You dont have permission to access smoke sensor");
        }
        setAlarmTurnOn(smokeSensor);
    }
    // function for turning off
    public void turnOffAlarmThroughApplication( Integer smokeSensorId,
                                                Authentication authentication){
            User user = ((User) authentication.getPrincipal());
            var smokeSensor = smokeSensorRepository.findById(smokeSensorId)
                    .orElseThrow(
                            () -> new EntityNotFoundException("Smoke sensor not found")
                    );
                if(!user.getId().equals(smokeSensor.getUser().getId())) {
                    log.warn("Smoke sensor {} is not owned by user {}", smokeSensorId, user.getId());
                    throw new AccessDeniedException("You dont have permission to access smoke sensor");
                }
                setAlarmTurnOff(smokeSensor);
    }





    // set alarm sensitivity!
    @Transactional(rollbackOn = BusinessException.class)
        public AlarmSensitivityResponse setAlarmSensitivity(AlarmSensitivityRequest alarmSensitivityRequest,
                                                            Integer smokeSensorId,
                                                            Authentication  authentication   ){
                    User user = ((User) authentication.getPrincipal());
                        var smokeSensor = smokeSensorRepository.findById(smokeSensorId)
                             .orElseThrow(
                                     () -> new EntityNotFoundException("Smoke sensor not found")
                                );
                        if(!user.getId().equals(smokeSensor.getUser().getId())) {
                            log.warn("Smoke sensor {} is not owned by user {}", smokeSensorId, user.getId());
                            throw new AccessDeniedException("You dont have permission to access smoke sensor");
                            }
                    if(alarmSensitivityRequest.getValueForSensitivity() > maxSmokeThreshold){
                        log.info("Value should be between 0 and 1");
                         throw new BusinessException("Value should be between 0 and 1");
                    }
                    if(smokeSensor.getValueForSensitivity().equals(alarmSensitivityRequest.getValueForSensitivity())){
                        throw new BusinessException("Value already set! Try another value!");
                 }
             var newSmokeSensor = setAlarmSensitivity(alarmSensitivityRequest, smokeSensor);
                    return smokeSensorMapper.toAlarmSensitivityResponse(newSmokeSensor);
        }

        public AlarmMaintenanceDateResponse showLastMaintenanceDate (Integer smokeSensorId, Authentication authentication){
            User user = ((User) authentication.getPrincipal());
            var smokeSensor = smokeSensorRepository.findById(smokeSensorId)
                    .orElseThrow(() -> new EntityNotFoundException("Smoke sensor not found"));
            if(!user.getId().equals(smokeSensor.getUser().getId())) {
                log.warn("Smoke sensor {} is not owned by user {}", smokeSensorId, user.getId());
                throw new AccessDeniedException("You dont have permission to access smoke sensor");
            }
            return smokeSensorMapper.toAlarmMaintenanceDateResponse(smokeSensor);
        }

        public AlarmMaintenanceDateResponse createLastMaintenanceDate(Integer smokeSensorId, Authentication authentication){
            User user = ((User) authentication.getPrincipal());
            var smokeSensor = smokeSensorRepository.findById(smokeSensorId)
                    .orElseThrow(() -> new EntityNotFoundException("Smoke sensor not found"));
            if(!user.getId().equals(smokeSensor.getUser().getId())) {
                log.warn("Smoke sensor {} is not owned by user {}", smokeSensorId, user.getId());
                throw new AccessDeniedException("You dont have permission to access smoke sensor");
            }
                smokeSensor.setLastMaintenanceDate(LocalDateTime.now());
                smokeSensorRepository.save(smokeSensor);
                return smokeSensorMapper.toAlarmMaintenanceDateResponse(smokeSensor);
        }
            




        private SmokeSensor setAlarmSensitivity(AlarmSensitivityRequest alarmSensitivityRequest, SmokeSensor smokeSensor){

            smokeSensor.setValueForSensitivity(alarmSensitivityRequest.getValueForSensitivity());
            smokeSensor.setSensitivity(alarmSensitivityRequest.getSensitivity());
           return smokeSensorRepository.save(smokeSensor);
        }


        private void setRequestedAlarmData(SmokeSensor smokeSensor, AlarmRequest request) {
            if(request.getSmokeThreshold() >= request.getSmokeLevel() && smokeSensor.getAlarmActive().equals(false)) {
                smokeSensor.setSmokeLevel(request.getSmokeLevel());
                smokeSensor.setSmokeThreshold(request.getSmokeThreshold());
                smokeSensor.setLastAlarmTime(LocalDateTime.now());
                smokeSensor.setAlarmActive(true);
                smokeSensorRepository.save(smokeSensor);
            }
            else{
                throw new BusinessException("Problem in setRequestedAlarmData function!");
            }
        }

        private void setAlarmTurnOn(SmokeSensor smokeSensor){
            smokeSensor.setAlarmActive(true);
            smokeSensor.setLastAlarmTime(LocalDateTime.now());
            smokeSensorRepository.save(smokeSensor);
        }

        private void setAlarmTurnOff(SmokeSensor smokeSensor){
            smokeSensor.setAlarmActive(false);
            smokeSensorRepository.save(smokeSensor);
        }



    private void sendAlarmEmail(User user) throws MessagingException {
        emailService.sendAlarmEmail(
                user.getEmail(),
                user.getFullName(),
                EmailAlarmTemplate.ACTIVATE_ALARM,
                "Turning on Alarm!"
        );
    }





}
