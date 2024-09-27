package com.misha.sh.devicemanagementmicroservice.service.thermostatService;

import com.misha.sh.devicemanagementmicroservice.exception.BusinessException;
import com.misha.sh.devicemanagementmicroservice.mapper.ThermostatMapper;
import com.misha.sh.devicemanagementmicroservice.model.thermostat.TemperatureMode;
import com.misha.sh.devicemanagementmicroservice.model.thermostat.Thermostat;
import com.misha.sh.devicemanagementmicroservice.model.User;
import com.misha.sh.devicemanagementmicroservice.pagination.PageResponse;
import com.misha.sh.devicemanagementmicroservice.repository.ThermostatRepository;
import com.misha.sh.devicemanagementmicroservice.request.thermostat.addThermostat.ThermostatRequest;
import com.misha.sh.devicemanagementmicroservice.request.thermostat.addThermostat.ThermostatResponse;
import com.misha.sh.devicemanagementmicroservice.request.thermostat.temperatureMode.ThermostatCoolingModeRequest;
import com.misha.sh.devicemanagementmicroservice.request.thermostat.temperatureMode.ThermostatCoolingModeResponse;
import com.misha.sh.devicemanagementmicroservice.request.thermostat.temperatureMode.ThermostatHeatModeRequest;
import com.misha.sh.devicemanagementmicroservice.request.thermostat.temperatureMode.ThermostatHeatModeResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
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
import java.util.stream.Collectors;


/**
 * Service class for managing thermostat operations.
 * This class handles various operations related to thermostats, including adding,
 * finding, removing, and controlling thermostat modes.
 */


@Slf4j
@Service
@RequiredArgsConstructor
public class ThermostatService {

    // maximal temperature which will start cooling function!
    private static final double COOLING_MAX = 100.0;
    // minimal temperature which will start cooling function!
    private static final double COOLING_MIN = 68.0;
    // maximal temperature which will start heat function!
    private static final double HEATING_MAX = 80;
    // minimal temperature which will start heat function!
    private static final double HEATING_MIN = 50;

    private static final double TEMPERATURE_TOLERANCE = 1.0;


    private final ThermostatRepository thermostatRepository;
    private final ThermostatMapper thermostatMapper;



    /**
     * Adds a new thermostat to the system.
     *
     * @param thermostatRequest The request containing thermostat details
     * @param authentication The authentication object of the current user
     * @return ThermostatResponse object containing the details of the added thermostat
     */
    @Transactional
    public ThermostatResponse addThermostat(ThermostatRequest thermostatRequest, Authentication authentication) {
        User user = ((User) authentication.getPrincipal());
        Thermostat thermostat = thermostatMapper.toThermostat(thermostatRequest);
        thermostat.setUser(user);
        thermostat.setCreatedDate(LocalDateTime.now());
        thermostat.setUpdatedAt(LocalDateTime.now());
        thermostatRepository.save(thermostat);
        return thermostatMapper.toThermostatResponse(thermostat);
    }
    /**
     * Retrieves a paginated list of all thermostats for the authenticated user.
     *
     * @param authentication The authentication object of the current user
     * @param size The number of items per page
     * @param page The page number to retrieve
     * @return PageResponse containing a list of ThermostatResponse objects
     */
    public PageResponse<ThermostatResponse> findAllUserThermostats(
                                                                   int page,
                                                                   int size,
                                                                   Authentication authentication) {
        User user = ((User) authentication.getPrincipal());
        log.info("User was successfully found with id {}", user.getId());

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Thermostat> thermostats = thermostatRepository.findAllByUserId(user.getId(), pageable);
        log.info("Thermostats found: {}", thermostats.getTotalElements());

        List<ThermostatResponse> thermostatResponses = thermostats.getContent().stream()
                .map(thermostat -> {
                    if(user.getId().equals(thermostat.getUser().getId())) {
                        return thermostatMapper.toThermostatResponse(thermostat);
                    }
                    else{
                        throw new BusinessException("You dont have an Access to this devide");
                    }
                })
                        .toList();

        log.info("Successfully mapped all user thermostats!");
        return new PageResponse<>(
                thermostatResponses,
                thermostats.getNumber(),
                thermostats.getSize(),
                thermostats.getTotalElements(),
                thermostats.getTotalPages(),
                thermostats.isFirst(),
                thermostats.isLast()
        );
    }
    /**
     * Finds a specific thermostat by its ID.
     *
     * @param thermostatId The ID of the thermostat to find
     * @param authentication The authentication object of the current user
     * @return ThermostatResponse object containing the details of the found thermostat
     * @throws EntityNotFoundException if the thermostat is not found
     */
            public ThermostatResponse findThermostatById(Integer thermostatId, Authentication authentication) {
                    User user = ((User) authentication.getPrincipal());
                    log.info("User founded with id {}", user.getId());
                    var foundedThermostat = thermostatRepository.findById(thermostatId)
                            .orElseThrow(() -> new EntityNotFoundException("Thermostat was not found with id " + thermostatId));
                    if(!user.getId().equals(foundedThermostat.getUser().getId())) {
                        throw new EntityNotFoundException("Thermostat not found");
                    }
                log.info("Successfully founded thermostat with id {}", thermostatId);
                log.info("Begin mapping process!");
                return thermostatMapper.toThermostatResponse(foundedThermostat);
            }

    /**
     * Removes a thermostat from the system.
     *
     * @param thermostatId The ID of the thermostat to remove
     * @param authentication The authentication object of the current user
     */
                @Transactional(rollbackOn = BusinessException.class)
                public void removeThermostat(Integer thermostatId, Authentication authentication) {
                    User user = ((User) authentication.getPrincipal());
                    var thermostat = thermostatRepository.findById(thermostatId)
                            .orElseThrow(() -> new EntityNotFoundException("Thermostat was not found with id " + thermostatId));
                    if(!user.getId().equals(thermostat.getUser().getId())) {
                        throw new EntityNotFoundException("Thermostat not found");
                    }
                    thermostatRepository.deleteById(thermostatId);
                    log.info("Successfully deleted thermostat with id {}", thermostatId);
                }


    /**
     * Turns off a specific thermostat.
     *
     * @param thermostatId The ID of the thermostat to turn off
     * @param authentication The authentication object of the current user
     * @throws EntityNotFoundException if the thermostat is not found
     */

        public void turnOffThermostat(Integer thermostatId, Authentication authentication) {
        User user = ((User) authentication.getPrincipal());
        var thermostat = thermostatRepository.findById(thermostatId)
                .orElseThrow(() -> new EntityNotFoundException("Thermostat was not found with id " + thermostatId));
        if(!user.getId().equals(thermostat.getUser().getId())) {
            throw new EntityNotFoundException("Thermostat not found");
        }
        turnOff(thermostat);
        thermostatRepository.save(thermostat);
        }

    public void turnOnThermostat(Integer thermostatId, Authentication authentication) {
        User user = ((User) authentication.getPrincipal());
        var thermostat = thermostatRepository.findById(thermostatId)
                .orElseThrow(() -> new EntityNotFoundException("Thermostat not found with id " + thermostatId));

        if (!user.getId().equals(thermostat.getUser().getId())) {
            throw new AccessDeniedException("User is not authorized to access this thermostat");
        }

        if (thermostat.isTurnOn()) {
            log.warn("Thermostat {} is already turned on", thermostatId);
            throw new BusinessException("Thermostat is already turned on");
        }

        if (thermostat.isTurnOff()) {
            turnOn(thermostat);
            thermostatRepository.save(thermostat);
        } else {
            log.warn("Thermostat {} is in an unexpected state", thermostatId);
            throw new BusinessException("Thermostat is in an unexpected state");
        }
    }

    /**
     * Sets a thermostat to cooling mode.
     *
     * @param request The request containing cooling mode details
     * @param thermostatId The ID of the thermostat to set to cooling mode
     * @param authentication The authentication object of the current user
     * @return ThermostatCoolingModeResponse object containing the updated thermostat details
     */
    @Transactional
    public ThermostatCoolingModeResponse setThermostatCoolingMode(ThermostatCoolingModeRequest request,
                                                                  Integer thermostatId,
                                                                  Authentication authentication) {
        Thermostat thermostat = setCoolingMode(request, thermostatId, authentication);
        return thermostatMapper.toThermostatCoolingModeResponse(thermostat);
    }

    /**
     * Sets a thermostat to heat mode.
     *
     * @param request The request containing heat mode details
     * @param thermostatId The ID of the thermostat to set to heat mode
     * @param authentication The authentication object of the current user
     * @return ThermostatHeatModeResponse object containing the updated thermostat details
     */
    public ThermostatHeatModeResponse setThermostatHeatMode(ThermostatHeatModeRequest request,
                                                            Integer thermostatId,
                                                            Authentication authentication) {

        Thermostat thermostat = setHeatMode(request, thermostatId, authentication);
        return thermostatMapper.toThermostatHeatModeResponse(thermostat);
    }



    private Thermostat setCoolingMode(ThermostatCoolingModeRequest thermostatTemperatureRequest, Integer thermostatId, Authentication authentication) {
        User user = ((User) authentication.getPrincipal());
        var foundThermostat = thermostatRepository.findById(thermostatId)
                .orElseThrow(() -> new EntityNotFoundException("Thermostat with id " + thermostatId + " not found"));
        if(!user.getId().equals(foundThermostat.getUser().getId())) {
            throw new EntityNotFoundException("Thermostat not found");
        }
            setCoolingModeIfValid(
                    thermostatTemperatureRequest.getCurrentTemperature(),
                    thermostatTemperatureRequest.getTargetTemperature(),
                    foundThermostat
            );
            log.info("After setting cooling mode: {}", foundThermostat);

            if(thermostatTemperatureRequest.isTemporaryMode()){
                foundThermostat.setTemporaryMode(Boolean.TRUE);
                foundThermostat.setAutoMode(Boolean.FALSE);
            }
            else if(thermostatTemperatureRequest.isAutoMode()){
                foundThermostat.setTemporaryMode(Boolean.FALSE);
                foundThermostat.setAutoMode(Boolean.TRUE);
            }
            else{
                throw new IllegalArgumentException("Problem in setCoolingMode method");
            }
            thermostatRepository.save(foundThermostat);
        log.info("Thermostat with id {} has been set to cooling mode", thermostatId);

        return foundThermostat;
    }

    private Thermostat setHeatMode(ThermostatHeatModeRequest thermostatHeatModeRequest, Integer thermostatId, Authentication authentication) {
        User user = ((User) authentication.getPrincipal());
        var foundedThermostat = thermostatRepository.findById(thermostatId)
                .orElseThrow(() -> new EntityNotFoundException("Thermostat with id " + thermostatId + " not found"));
        if(!user.getId().equals(foundedThermostat.getUser().getId())) {
            throw new EntityNotFoundException("Thermostat not found");
        }
            setHeatModeIfValid(
                    thermostatHeatModeRequest.getCurrentTemperature(),
                    thermostatHeatModeRequest.getTargetTemperature(),
                    foundedThermostat
            );
        log.info("Thermostat with id {} has been set to heating mode", thermostatId);

        if(thermostatHeatModeRequest.isTemporaryMode()){
            foundedThermostat.setTemporaryMode(Boolean.TRUE);
            foundedThermostat.setAutoMode(Boolean.FALSE);
        }
        else if(thermostatHeatModeRequest.isAutoMode()){
            foundedThermostat.setTemporaryMode(Boolean.FALSE);
            foundedThermostat.setAutoMode(Boolean.TRUE);
        }
        else{
            throw new IllegalArgumentException("Problem in setHeatingMode method");
        }
        thermostatRepository.save(foundedThermostat);
        return foundedThermostat;

    }





    private void setCoolingModeIfValid(double currentTemperature, double targetTemperature, Thermostat thermostat) {
        log.info("Setting cooling mode. Current: {}, Target: {}", currentTemperature, targetTemperature);
        if (currentTemperature <= COOLING_MIN) {
            log.warn("Cannot use cooling mode, temperature is already too low.");
            throw new BusinessException("Cannot use cooling mode, temperature is already too low.");
        }
        if (targetTemperature >= COOLING_MIN + TEMPERATURE_TOLERANCE && targetTemperature <= COOLING_MAX) {
            thermostat.setCurrentTemperature(currentTemperature);
            thermostat.setTargetTemperature(targetTemperature);
            thermostat.setTemperatureMode(TemperatureMode.Cool);
            thermostat.setIsCooling(true);
            thermostat.setIsHeating(false);
            log.info("Saving thermostat: {}", thermostat);
            log.info("Thermostat saved. Starting gradual cooling.");
      //      thermostatRepository.save(thermostat);
        } else {
            log.warn("Target temperature is outside the safe cooling range.");
            throw new BusinessException("Target temperature is outside the safe cooling range.");
        }
    }

    private void setHeatModeIfValid(double currentTemperature, double targetTemperature, Thermostat thermostat ) {
        if(currentTemperature > HEATING_MAX){
            throw new BusinessException("Cannot use heat mode, temperature is too high.");
        }
        if (targetTemperature >= HEATING_MIN && targetTemperature <= HEATING_MAX) {
            thermostat.setCurrentTemperature(currentTemperature);
            thermostat.setTargetTemperature(targetTemperature);
            thermostat.setTemperatureMode(TemperatureMode.HEAT);
            thermostat.setIsCooling(false);
            thermostat.setIsHeating(true);
            log.info("Saving thermostat : {}", thermostat);
            log.info("Thermostat saved. Starting gradual heating.");
        }

        else {
            throw new BusinessException("Target temperature is outside the safe heating range.");
        }
    }


    private void turnOff(Thermostat thermostat){
        thermostat.setActive(false);
        thermostat.setTurnOn(Boolean.FALSE);
        thermostat.setTurnOff(Boolean.TRUE);
        thermostat.setAutoMode(Boolean.FALSE);
        thermostat.setTemporaryMode(Boolean.FALSE);
        thermostat.setTemperatureMode(null);
        thermostat.setHumidity(0);
        thermostat.setIsCooling(Boolean.FALSE);
        thermostat.setIsHeating(Boolean.FALSE);
        thermostat.setTargetTemperature(0.0);
        thermostat.setCurrentTemperature(0.0);
    }

    private void turnOn(Thermostat thermostat) {
        thermostat.setActive(true);
        thermostat.setTurnOn(Boolean.TRUE);
        thermostat.setTurnOff(Boolean.FALSE);
        thermostat.setAutoMode(Boolean.FALSE);
        thermostat.setTemporaryMode(Boolean.FALSE);
        thermostat.setTemperatureMode(null);
        thermostat.setHumidity(0);
        thermostat.setIsCooling(Boolean.FALSE);
        thermostat.setIsHeating(Boolean.FALSE);
        thermostat.setTargetTemperature(0.0);
        thermostat.setCurrentTemperature(0.0);
    }








}

