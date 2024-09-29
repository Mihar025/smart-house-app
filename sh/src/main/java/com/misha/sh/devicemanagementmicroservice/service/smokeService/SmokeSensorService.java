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
import com.misha.sh.devicemanagementmicroservice.request.smokeSensor.modes.SmokeSensorDefaultEnergyConsumingModeResponse;
import com.misha.sh.devicemanagementmicroservice.request.smokeSensor.modes.SmokeSensorLowEnergyConsumingModeResponse;
import com.misha.sh.devicemanagementmicroservice.request.smokeSensor.turnOnAndOffSmokeSensor.SmokeSensorTurnOffResponse;
import com.misha.sh.devicemanagementmicroservice.request.smokeSensor.turnOnAndOffSmokeSensor.SmokeSensorTurnOnResponse;
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



    /**
     * Adds a new smoke sensor to the system.
     *
     * @param smokeSensorRequest The request containing new smoke sensor data
     * @return A response containing the created smoke sensor data
     */
    @Transactional
    public SmokeSensorResponse addNewSmokeSensor(SmokeSensorRequest smokeSensorRequest) {
         var mappedSensor = smokeSensorMapper.toSmokeSensor(smokeSensorRequest);
        setSmokeSensorActive(mappedSensor);
        smokeSensorRepository.save(mappedSensor);
        log.info("Added new smoke sensor: {}", mappedSensor);
        log.info("Beginning smoke sensor mapping");
        return smokeSensorMapper.toSmokeSensorResponse(mappedSensor);
    }
    /**
     * Finds a smoke sensor by its ID.
     *
     * @param smokeSensorId The ID of the smoke sensor
     * @param authentication The authentication data of the user
     * @return A response containing the found smoke sensor data
     * @throws AccessDeniedException if the user doesn't have permission to access the sensor
     */
        public SmokeSensorResponse findSensorById(Integer smokeSensorId, Authentication authentication) {
            var smokeSensor = checkAccessAndGetSensor(smokeSensorId, authentication);
            log.info("Beginning mapping smoke sensor!");
            return smokeSensorMapper.toSmokeSensorResponse(smokeSensor);
        }

    /**
     * Removes a smoke sensor from the system.
     *
     * @param smokeSensorId The ID of the smoke sensor to remove
     * @param authentication The authentication data of the user
     * @throws AccessDeniedException if the user doesn't have permission to access the sensor
     * @throws BusinessException if an error occurs during removal
     */
            @Transactional(rollbackOn = BusinessException.class)
            public void removeSmokeSensor(Integer smokeSensorId, Authentication authentication) {
                var smokeSensor = checkAccessAndGetSensor(smokeSensorId, authentication);
                    log.info("Beginning removing smoke sensor with id: {}!", smokeSensorId);
                    smokeSensorRepository.deleteById(smokeSensor.getId());
            }

    /**
     * Finds all smoke sensors for a user with pagination.
     *
     * @param page The page number
     * @param size The size of the page
     * @param authentication The authentication data of the user
     * @return A paginated response containing smoke sensor data
     */
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


    /**
     * Turns on the alarm when smoke sensor level is high.
     *
     * @param smokeSensorId The ID of the smoke sensor
     * @param request The alarm request data
     * @param authentication The authentication data of the user
     * @return A response containing the alarm data
     * @throws MessagingException if there's an error sending the alarm email
     * @throws BusinessException if there's a problem with the Email Alarm Template
     */
        @Transactional(rollbackOn = BusinessException.class)
        public AlarmResponse turnOnAlarmSmokeSensorLevelHigh(Integer smokeSensorId,
                                                       @Valid AlarmRequest request,
                                                       Authentication authentication) throws MessagingException {
                        var smokeSensor = checkAccessAndGetSensor(smokeSensorId, authentication);
                        setRequestedAlarmData(smokeSensor, request);
                        log.info("Alarm data was successfully set!");

                        var savedSmokesensor = smokeSensorRepository.save(smokeSensor);
                        log.info("Alarm data was successfully saved!");

                            if(smokeSensor.getAlarmActive().equals(true)){
                                log.info("Sending alarm email");
                                sendAlarmEmail(smokeSensor.getUser());

                             }
                                 else {
                                     throw new BusinessException("Big problem with Email Alarm Template!");
                                      }
                                return smokeSensorMapper.toAlarmResponse(savedSmokesensor);
        }

    /**
     * Turns on the alarm through the application.
     *
     * @param authentication The authentication data of the user
     * @param smokeSensorId The ID of the smoke sensor
     * @throws MessagingException if there's an error sending the alarm email
     */
    public  void  turnOnAlarmThroughApplication(Authentication authentication,
                                                Integer smokeSensorId) throws MessagingException {
        var smokeSensor = checkAccessAndGetSensor(smokeSensorId, authentication);
        setAlarmTurnOn(smokeSensor);
    }
    /**
     * Turns off the alarm through the application.
     *
     * @param smokeSensorId The ID of the smoke sensor
     * @param authentication The authentication data of the user
     */
    public void turnOffAlarmThroughApplication( Integer smokeSensorId,
                                                Authentication authentication){
                var smokeSensor = checkAccessAndGetSensor(smokeSensorId, authentication);
                setAlarmTurnOff(smokeSensor);
    }


    /**
     * Sets the alarm sensitivity for a smoke sensor.
     *
     * @param alarmSensitivityRequest The request containing sensitivity data
     * @param smokeSensorId The ID of the smoke sensor
     * @param authentication The authentication data of the user
     * @return A response containing the updated alarm sensitivity data
     * @throws BusinessException if the sensitivity value is invalid or already set
     */
    @Transactional(rollbackOn = BusinessException.class)
        public AlarmSensitivityResponse setAlarmSensitivity(AlarmSensitivityRequest alarmSensitivityRequest,
                                                            Integer smokeSensorId,
                                                            Authentication  authentication   ){
        var smokeSensor = checkAccessAndGetSensor(smokeSensorId, authentication);
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

    /**
     * Shows the last maintenance date of a smoke sensor.
     *
     * @param smokeSensorId The ID of the smoke sensor
     * @param authentication The authentication data of the user
     * @return A response containing the last maintenance date
     */
        public AlarmMaintenanceDateResponse showLastMaintenanceDate (Integer smokeSensorId, Authentication authentication){
            var smokeSensor = checkAccessAndGetSensor(smokeSensorId, authentication);
            return smokeSensorMapper.toAlarmMaintenanceDateResponse(smokeSensor);
        }
    /**
     * Creates a new last maintenance date for a smoke sensor.
     *
     * @param smokeSensorId The ID of the smoke sensor
     * @param authentication The authentication data of the user
     * @return A response containing the updated maintenance date
     */
        public AlarmMaintenanceDateResponse createLastMaintenanceDate(Integer smokeSensorId, Authentication authentication){
            var smokeSensor = checkAccessAndGetSensor(smokeSensorId, authentication);
                smokeSensor.setLastMaintenanceDate(LocalDateTime.now());
                smokeSensorRepository.save(smokeSensor);
                return smokeSensorMapper.toAlarmMaintenanceDateResponse(smokeSensor);
        }

    /**
     * Turns on a smoke sensor.
     *
     * @param smokeSensorId The ID of the smoke sensor
     * @param authentication The authentication data of the user
     * @return A response indicating the smoke sensor has been turned on
     */
            @Transactional
            public SmokeSensorTurnOnResponse turnOnSmokeSensor(Integer smokeSensorId, Authentication authentication){
                var smokeSensor = checkAccessAndGetSensor(smokeSensorId, authentication);
                        smokeSensor.setTurnOn(true);
                        smokeSensor.setActive(true);
                        smokeSensor.setConnected(true);
                        smokeSensorRepository.save(smokeSensor);
                        return smokeSensorMapper.toSmokeSensorTurnOnResponse(smokeSensor);
            }
    /**
     * Turns off a smoke sensor.
     *
     * @param smokeSensorId The ID of the smoke sensor
     * @param authentication The authentication data of the user
     * @return A response indicating the smoke sensor has been turned off
     */
                        @Transactional
                        public SmokeSensorTurnOffResponse turnOffSmokeSensor(Integer smokeSensorId, Authentication authentication){
                            var smokeSensor = checkAccessAndGetSensor(smokeSensorId, authentication);
                            smokeSensor.setTurnOn(false);
                            smokeSensor.setActive(false);
                            smokeSensor.setConnected(false);
                            smokeSensor.setTurnOff(true);
                            smokeSensorRepository.save(smokeSensor);
                             return smokeSensorMapper.toSmokeSensorTurnOffResponse(smokeSensor);
                            }
    /**
     * Sets the smoke sensor to low energy consuming mode.
     *
     * @param smokeSensorId The ID of the smoke sensor
     * @param authentication The authentication data of the user
     * @return A response indicating the smoke sensor is in low energy consuming mode
     */
                            public SmokeSensorLowEnergyConsumingModeResponse setLowEnergyConsumingMode(Integer smokeSensorId, Authentication authentication){
                                     var smokeSensor = checkAccessAndGetSensor(smokeSensorId, authentication);
                                     setLowEnergyConsumingMode(smokeSensor);
                                       return smokeSensorMapper.toSmokeLowEnergyConsumingMode(smokeSensor);
                         }
    /**
     * Sets the smoke sensor to default energy consuming mode.
     *
     * @param smokeSensorId The ID of the smoke sensor
     * @param authentication The authentication data of the user
     * @return A response indicating the smoke sensor is in default energy consuming mode
     */
                        public SmokeSensorDefaultEnergyConsumingModeResponse setDefaultEnergyConsumingMode(Integer smokeSensorId, Authentication authentication){
                            var smokeSensor = checkAccessAndGetSensor(smokeSensorId, authentication);
                            setDefaultEnergyConsumingMode(smokeSensor);
                            return smokeSensorMapper.toSmokeDefaultEnergyConsumingMode(smokeSensor);
                     }

    /**
     * Performs a self-test on the smoke sensor.
     *
     * @param smokeSensorId The ID of the smoke sensor
     * @param authentication The authentication data of the user
     * @return A boolean indicating whether the self-test passed
     */
    public Boolean selfTestPassed(Integer smokeSensorId, Authentication authentication) {
        var smokeSensor = checkAccessAndGetSensor(smokeSensorId, authentication);
        return testSmokeSensor(smokeSensor);
    }

    private Boolean testSmokeSensor(SmokeSensor sensor) {
        double tolerance = 0.1;
        double lowerBound = sensor.getSmokeThreshold() * (1 - tolerance);
        double upperBound = sensor.getSmokeThreshold() * (1 + tolerance);

        boolean testPassed = sensor.getSmokeLevel() >= lowerBound &&
                sensor.getSmokeLevel() <= upperBound;
        sensor.setSelfTestPassed(testPassed);
        smokeSensorRepository.save(sensor);
        return testPassed;
    }




                private SmokeSensor setLowEnergyConsumingMode(SmokeSensor smokeSensor){
                    smokeSensor.setLowEnergyConsumingMode("LOW_ENERGY_CONSUMING_MODE");
                    smokeSensor.setEnergyConsumingPerHours("0.2KW");
                    smokeSensor.setAmps(5);
                    smokeSensor.setVoltage(10.0);
                    return smokeSensorRepository.save(smokeSensor);
                }

                private SmokeSensor setDefaultEnergyConsumingMode(SmokeSensor smokeSensor){
                        smokeSensor.setEnergyConsumingPerHours("0.8KW");
                        smokeSensor.setAmps(8);
                        smokeSensor.setVoltage(23.0);
                        return smokeSensorRepository.save(smokeSensor);
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


    private void setSmokeSensorActive(SmokeSensor smokeSensor){
        smokeSensor.setActive(true);
        smokeSensor.setTurnOn(true);
        smokeSensor.setConnected(true);
        smokeSensor.setValueForSensitivity(maxSmokeThreshold);
    }

    private SmokeSensor checkAccessAndGetSensor(Integer smokeSensorId, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        SmokeSensor smokeSensor = smokeSensorRepository.findById(smokeSensorId)
                .orElseThrow(() -> new EntityNotFoundException("Smoke sensor not found"));

        if (!user.getId().equals(smokeSensor.getUser().getId())) {
            log.warn("Smoke sensor {} is not owned by user {}", smokeSensorId, user.getId());
            throw new AccessDeniedException("You don't have permission to access this smoke sensor");
        }

        return smokeSensor;
    }

}
