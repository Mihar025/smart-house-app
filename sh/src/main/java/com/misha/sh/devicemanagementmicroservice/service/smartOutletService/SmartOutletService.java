package com.misha.sh.devicemanagementmicroservice.service.smartOutletService;

import com.misha.sh.devicemanagementmicroservice.exception.BusinessException;
import com.misha.sh.devicemanagementmicroservice.mapper.SmartOutletMapper;
import com.misha.sh.devicemanagementmicroservice.model.device.DeviceStatus;
import com.misha.sh.devicemanagementmicroservice.model.smartOutlet.SmartOutlet;
import com.misha.sh.devicemanagementmicroservice.model.User;
import com.misha.sh.devicemanagementmicroservice.pagination.PageResponse;
import com.misha.sh.devicemanagementmicroservice.repository.SmartOutletRepository;
import com.misha.sh.devicemanagementmicroservice.request.smartOutlet.SmartOutletRequest;
import com.misha.sh.devicemanagementmicroservice.request.smartOutlet.SmartOutletResponse;
import com.misha.sh.devicemanagementmicroservice.request.smartOutlet.energyConsuming.SmartOutletEnergyConsumingRequest;
import com.misha.sh.devicemanagementmicroservice.request.smartOutlet.energyConsuming.SmartOutletEnergyConsumingResponse;
import com.misha.sh.devicemanagementmicroservice.request.smartOutlet.lastActivity.SmartOutletLastActivityResponse;
import com.misha.sh.devicemanagementmicroservice.request.smartOutlet.scheduling.SmartOutletScheduleResponse;
import com.misha.sh.devicemanagementmicroservice.request.smartOutlet.turnOnRequests.SmartOutletTurnOffResponse;
import com.misha.sh.devicemanagementmicroservice.request.smartOutlet.turnOnRequests.SmartOutletTurnOnResponse;
import jakarta.persistence.EntityNotFoundException;
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
 * This is SmartOutletService class
 * This class target is for working with SmartOutlet!
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class SmartOutletService {

    private final static double voltage = 110;
    private final static Integer amps = 50;
    private final static String energyConsumingPerHours = "0.27";



    private final SmartOutletRepository smartOutletRepository;
    private final SmartOutletMapper smartOutletMapper;

    /**
     * Adds a new smart outlet to the user's account.
     * Setting default electricity parameters
     * This method takes a SmartOutletRequest object, maps it to a SmartOutlet entity,
     * saves it to the database, and returns a SmartOutletResponse object.
     *
     * @param smartOutlet The SmartOutletRequest object containing the details of the smart outlet to be added.
     * @return A SmartOutletResponse object representing the newly added smart outlet.
     */
    //working
    public SmartOutletResponse addSmartOutlet(SmartOutletRequest smartOutlet, Authentication authentication) {
        User user = ((User) authentication.getPrincipal());
        var mappedSmartOutlet = smartOutletMapper.toSmartOutlet(smartOutlet);
        mappedSmartOutlet.setUser(user);
        setDefaultEnergyConsuming(mappedSmartOutlet);
        smartOutletRepository.save(mappedSmartOutlet);
        return smartOutletMapper.toSmartOutletResponse(mappedSmartOutlet);
    }

    /**
     * Finds a user's smart outlet by its ID.
     *
     * This method performs the following steps:
     * 1. Authenticates the user using the authenticateUser method.
     * 2. Searches for the outlet in the database using the provided outletId.
     * 3. If the outlet is found, it maps it to a SmartOutletResponse entity.
     * 4. If the outlet is not found, it throws an EntityNotFoundException.
     *
     * @param outletId The ID of the smart outlet to find.
     * @param authentication The authentication object to verify user's authentication status.
     * @return A SmartOutletResponse object representing the found smart outlet.
     * @throws EntityNotFoundException If no outlet is found with the given ID.
     * @throws SecurityException If the user is not authenticated or not authorized to access this outlet.
     */
    //working
    public SmartOutletResponse findOutletById(Integer outletId, Authentication authentication) {
      // authenticateUser(authentication, ownerId);
        User user = ((User) authentication.getPrincipal());
        var outlet = smartOutletRepository.findById(outletId)
                .orElseThrow(() -> new EntityNotFoundException("Cannot find outlet with id " + outletId));
        if(!user.getId().equals(outlet.getUser().getId())){
            throw new AccessDeniedException("You dont have permission to access this outlet");
        }
        return smartOutletMapper.toSmartOutletResponse(outlet);
    }


    /**
     * Retrieves a paginated list of smart outlets for a specific user.
     *
     * This method performs the following operations:
     * 1. Authenticates the user and verifies their access rights.
     * 2. Creates a Pageable object for pagination and sorting.
     * 3. Retrieves smart outlets from the repository based on the user ID and pagination settings.
     * 4. Maps the retrieved SmartOutlet entities to SmartOutletResponse objects.
     * 5. Constructs and returns a PageResponse containing the paginated results.
     *
     * @param authentication The authentication object of the current user.
     * @param size The number of items per page.
     * @param page The page number (0-indexed) to retrieve.
     * @return A PageResponse containing SmartOutletResponse objects and pagination metadata.
     * @throws AccessDeniedException If the authenticated user does not match the requested owner ID.
     * @throws IllegalArgumentException If the pagination parameters are invalid.
     */
    public PageResponse<SmartOutletResponse> findAllOutlets(
                                                                   Authentication authentication,
                                                                   int size,
                                                                   int page) {
                User user = ((User) authentication.getPrincipal());
                Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
                Page <SmartOutlet> smartOutlets = smartOutletRepository.findAllOutletsByUser(pageable, user.getId());
                List<SmartOutletResponse> smartOutletResponseList = smartOutlets.getContent().stream()
                        .map(smartOutlet -> {
                            if(user.getId().equals(smartOutlet.getUser().getId())) {
                                return smartOutletMapper.toSmartOutletResponse(smartOutlet);
                            }
                            else{
                                throw new AccessDeniedException("You do not have permission to access this resource");
                            }
                        })
                        .collect(Collectors.toList());

                return new PageResponse<>(
                        smartOutletResponseList,
                        smartOutlets.getNumber(),
                        smartOutlets.getSize(),
                        smartOutlets.getTotalElements(),
                        smartOutlets.getTotalPages(),
                        smartOutlets.isFirst(),
                        smartOutlets.isLast()
                );
         }


    /**
     * Turns on a specific smart outlet.
     *
     * This method performs the following actions:
     * 1. Authenticates the user and verifies their access rights to the outlet.
     * 2. Retrieves the smart outlet information from the repository.
     * 3. Updates the outlet's status to 'on' and sets the last on time to the current time.
     * 4. Saves the updated outlet information in the repository.
     * 5. Returns the updated status of the outlet.
     *
     * @param outletId The unique identifier of the smart outlet to be turned on.
     * @param authentication The authentication object of the current user.
     * @return A SmartOutletTurnOnResponse object containing the updated outlet information.
     * @throws EntityNotFoundException If no outlet is found with the given ID.
     * @throws AccessDeniedException If the authenticated user does not have permission to access the outlet.
     */

    public SmartOutletTurnOnResponse turnOnSmartOutlet(Integer outletId, Authentication authentication){
        User user = ((User) authentication.getPrincipal());
        var outlet = smartOutletRepository.findById(outletId)
                .orElseThrow(() -> new EntityNotFoundException("Cannot find outlet with id " + outletId));
        if(!outlet.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You do not have permission to access this resource");
        }
        outlet.setLastOnTime(LocalDateTime.now());
        outlet.setOn(true);
        smartOutletRepository.save(outlet);

        return new SmartOutletTurnOnResponse(
                outlet.getId(),
                outlet.isOn(),
                outlet.getLastOnTime()
        );
    }

    /**
     * Turns off a specific smart outlet.
     *
     * This method performs the following actions:
     * 1. Authenticates the user and verifies their access rights to the outlet.
     * 2. Retrieves the smart outlet information from the repository.
     * 3. Updates the outlet's status to 'off' and sets the last off time to the current time.
     * 4. Saves the updated outlet information in the repository.
     * 5. Returns the updated status of the outlet.
     *
     * @param outletId The unique identifier of the smart outlet to be turned off.
     * @param authentication The authentication object of the current user.
     * @return A SmartOutletTurnOffResponse object containing the updated outlet information.
     * @throws EntityNotFoundException If no outlet is found with the given ID.
     * @throws AccessDeniedException If the authenticated user does not have permission to access the outlet.
     */

    public SmartOutletTurnOffResponse turnOffSmartOutlet(Integer outletId, Authentication authentication){
        User user = ((User) authentication.getPrincipal());
        var outlet = smartOutletRepository.findById(outletId)
                .orElseThrow(() -> new EntityNotFoundException("Cannot find outlet with id " + outletId));
        if(!outlet.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You do not have permission to access this resource");
        }
        outlet.setLastOnTime(LocalDateTime.now());
        outlet.setOn(false);
        smartOutletRepository.save(outlet);

        return new SmartOutletTurnOffResponse(
                outlet.getId(),
                outlet.isOn(),
                outlet.getLastOnTime()
        );
    }









    /**
     * Schedules a smart outlet to turn on at a specified time.
     *
     * @param outletId       The unique identifier of the smart outlet.
     * @param scheduledTime  The scheduled time to turn on the outlet.
     * @param authentication The authentication object of the current user.
     * @return               A SmartOutletScheduleResponse object containing information about the scheduled turn-on.
     * @throws EntityNotFoundException If the outlet with the given id is not found.
     * @throws AccessDeniedException   If the user doesn't have permission to control this outlet
     *                                 or if the scheduled time is in the past.
     *
     * This method performs the following actions:
     * 1. Verifies the existence of the outlet with the given id.
     * 2. Checks if the outlet belongs to the current user.
     * 3. Ensures that the scheduled time is in the future.
     * 4. Sets the scheduled turn-on time and updates the outlet's status.
     * 5. Saves the updated outlet information in the repository.
     * 6. Returns a response with the scheduled turn-on information.
     */

    //working
        public SmartOutletScheduleResponse scheduleTurnOn (Integer outletId, LocalDateTime scheduledTime, Authentication authentication) {
            User user = ((User) authentication.getPrincipal());
            var outlet = smartOutletRepository.findById(outletId)
                    .orElseThrow(() -> new EntityNotFoundException("Cannot find outlet with id " + outletId));
            if(!outlet.getUser().getId().equals(user.getId())) {
                throw new AccessDeniedException("You do not have permission to access this resource");
            }
            if(scheduledTime.isBefore(LocalDateTime.now())) {
                throw new AccessDeniedException("You do not have permission to access this resource");
            }

            outlet.setScheduledOn(scheduledTime);
            outlet.setStatus(DeviceStatus.SCHEDULED);
            var updatedOutlet = smartOutletRepository.save(outlet);
            return smartOutletMapper.toSmartOutletScheduleResponse(updatedOutlet);
        }

    /**
     * Schedule a smart outlet to turn off at a specific time
     *
      * @param outletId         The unique identifier of the smart outlet
     * @param scheduledTime     The scheduled time to turn off the outlet
     * @param authentication    The authentication object of the current user
     * @return                 A SmartOutletScheduleResponse object containing information about the scheduled turn-off
     * @throws EntityNotFoundException If the outlet with the given id is not found.
     * @throws AccessDeniedException   If the user doesn't have permission to control this outlet
     * @throws IllegalArgumentException If user wrote wrong time for scheduling smart outlet turn-on
     *
     * This method performs the following actions:
     * 1. Verifies the existence of the outlet with the given id.
     * 2. Checks if the outlet belongs to the current user.
     * 3. Ensures that the scheduled time is in the future.
     * 4. Sets the scheduled turn-off time and updates the outlet's status.
     * 5. Saves the updated outlet information in the repository.
     * 6. Returns a response with the scheduled turn-on information.
     */
//working
    public SmartOutletScheduleResponse scheduleTurnOff(Integer outletId, LocalDateTime scheduledTime, Authentication authentication) {
        User user = ((User) authentication.getPrincipal());
        var outlet = smartOutletRepository.findById(outletId)
                .orElseThrow(() -> new EntityNotFoundException("Cannot find outlet with id " + outletId));
        if (!outlet.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You do not have permission to access this resource");
        }

        if (scheduledTime == null) {
            throw new IllegalArgumentException("Scheduled time cannot be null");
        }

        if (scheduledTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Scheduled time must be in the future");
        }

        outlet.setScheduledOff(scheduledTime);
        outlet.setStatus(DeviceStatus.SCHEDULED);
        var updatedOutlet = smartOutletRepository.save(outlet);
        return smartOutletMapper.toSmartOutletScheduleResponse(updatedOutlet);
    }

    /**
     * Retrieves the current power consumption information for a specific smart outlet.
     *
     * @param outletId       The unique identifier of the smart outlet.
     * @param authentication The authentication object of the current user.
     * @return               A SmartOutletEnergyConsumingResponse object containing the current power consumption information.
     * @throws EntityNotFoundException If the outlet with the given id is not found.
     * @throws AccessDeniedException   If the user doesn't have permission to access this outlet's information.
     *
     * This method performs the following actions:
     * 1. Extracts the user information from the authentication object.
     * 2. Retrieves the smart outlet from the repository using the provided id.
     * 3. Verifies that the current user has permission to access the outlet's information.
     * 4. Maps the outlet data to a SmartOutletEnergyConsumingResponse object.
     *
     * Note: The actual power consumption calculation is not shown in this method. It's assumed to be
     * handled either by the mapper or stored in the outlet entity.
     */
    public SmartOutletEnergyConsumingResponse currentPowerUsing(Integer outletId,Authentication authentication) {
        User user = ((User) authentication.getPrincipal());
        var outlet = smartOutletRepository.findById(outletId)
                .orElseThrow(() -> new EntityNotFoundException("Cannot find outlet with id " + outletId));
        if(!outlet.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You do not have permission to access this resource");
        }
        return smartOutletMapper.toSmartOutletEnergyConsumingResponse(outlet);
    }

    /**
     * Sets custom power consumption information for a specific smart outlet.
     *
     * @param outletId                           The unique identifier of the smart outlet.
     * @param smartOutletEnergyConsumingRequest  The request object containing new power consumption data.
     * @param authentication                     The authentication object of the current user.
     * @return                                   A SmartOutletEnergyConsumingResponse object containing the updated power consumption information.
     * @throws EntityNotFoundException           If the outlet with the given id is not found.
     * @throws AccessDeniedException             If the user doesn't have permission to access this outlet's information.
     * @throws BusinessException                 If the outlet is not currently turned on.
     *
     * This method performs the following actions:
     * 1. Extracts the user information from the authentication object.
     * 2. Retrieves the smart outlet from the repository using the provided id.
     * 3. Verifies that the current user has permission to access and modify the outlet's information.
     * 4. Checks if the outlet is currently turned on.
     * 5. If the outlet is on, updates its power consumption information with the provided data.
     * 6. Saves the updated outlet information in the repository.
     * 7. Returns a response with the updated power consumption information.
     *
     * Note: This method only allows setting custom power consumption for outlets that are currently turned on.
     * Attempting to set power consumption for a turned-off outlet will result in a BusinessException.
     */
       public SmartOutletEnergyConsumingResponse setCustomPowerUsing(Integer outletId,
                                                                      SmartOutletEnergyConsumingRequest smartOutletEnergyConsumingRequest,
                                                                      Authentication authentication) {
        User user = ((User) authentication.getPrincipal());
        var outlet = smartOutletRepository.findById(outletId)
                .orElseThrow(() -> new EntityNotFoundException("Cannot find outlet with id " + outletId));
           if(!outlet.getUser().getId().equals(user.getId())) {
             throw new AccessDeniedException("You do not have permission to access this resource");
           }
        if(outlet.isOn()) {
            var updatedOutlet = updateSmartOutletEnergyConsuming(outlet, smartOutletEnergyConsumingRequest);
            var savedOutlet = smartOutletRepository.save(updatedOutlet);
            return smartOutletMapper.toSmartOutletEnergyConsumingResponse(savedOutlet);
        }
        else{
            throw new BusinessException("You do not have permission to access this resource");
        }
    }



    /**
     * Resets the power consumption of a smart outlet to its default values.
     *
     * @param outletId       The unique identifier of the smart outlet.
     * @param authentication The authentication object of the current user.
     * @return               A SmartOutletEnergyConsumingResponse object containing the updated power consumption information.
     * @throws EntityNotFoundException If the outlet with the given id is not found.
     * @throws AccessDeniedException   If the user doesn't have permission to access this outlet's information.
     *
     * This method performs the following actions:
     * 1. Extracts the user information from the authentication object.
     * 2. Retrieves the smart outlet from the repository using the provided id.
     * 3. Verifies that the current user has permission to access and modify the outlet's information.
     * 4. Resets the outlet's power consumption to default values using the setDefaultEnergyConsuming method.
     * 5. Saves the updated outlet information in the repository.
     * 6. Returns a response with the updated power consumption information.
     *
     * Note: This method allows resetting the power consumption to default values regardless of the outlet's current state.
     */
    public SmartOutletEnergyConsumingResponse setDefaultPowerUsing(Integer outletId,
                                                                  Authentication authentication) {
        User user = ((User) authentication.getPrincipal());
        var outlet = smartOutletRepository.findById(outletId)
                .orElseThrow(() -> new EntityNotFoundException("Cannot find outlet with id " + outletId));
        if(!outlet.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You do not have permission to access this resource");
        }
            setDefaultEnergyConsuming(outlet);
            var savedOutlet = smartOutletRepository.save(outlet);
            return smartOutletMapper.toSmartOutletEnergyConsumingResponse(savedOutlet);
    }

    /**
     * Retrieves the last activity information for a specific smart outlet.
     *
     * This method performs the following actions:
     * 1. Authenticates the user and verifies their access rights to the outlet.
     * 2. Retrieves the smart outlet information from the repository.
     * 3. Returns the last on/off times and current status of the outlet.
     *
     * @param outletId The unique identifier of the smart outlet.
     * @param authentication The authentication object of the current user.
     * @return A SmartOutletLastActivityResponse object containing the outlet's last activity information.
     * @throws EntityNotFoundException If no outlet is found with the given ID.
     * @throws AccessDeniedException If the authenticated user does not have permission to access the outlet.
     */

    public SmartOutletLastActivityResponse getLastActivity(Integer outletId, Authentication authentication) {
        User user = ((User) authentication.getPrincipal());
        var outlet = smartOutletRepository.findById(outletId)
                .orElseThrow(() -> new EntityNotFoundException("Cannot find outlet with id " + outletId));

        if (!outlet.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You do not have permission to access this resource");
        }
        return new SmartOutletLastActivityResponse(
                outlet.getId(),
                outlet.getScheduledOn(),
                outlet.getScheduledOff(),
                outlet.isOn()
        );
    }

    /**
     * Sets default electrical parameters for a smart outlet.
     *
     * @param smartOutlet The SmartOutlet entity to be updated with default values.
     *                    This entity extends the Device class, which contains fields for electrical logic.
     *
     * This method performs the following actions:
     * 1. Sets the voltage of the smart outlet to a predefined default value.
     * 2. Sets the amperage (current) of the smart outlet to a predefined default value.
     * 3. Sets the energy consumption per hour to a predefined default value.
     * 4. Sets the status of the smart outlet to ACTIVE.
     *
     * Note: The default values (voltage, amps, energyConsumingPerHours) are assumed to be class-level
     * constants or variables, as they are not passed as parameters to this method.
     *
     * This method is typically called when resetting a smart outlet to its default state or
     * when initializing a new smart outlet with standard values.
     */
    private void setDefaultEnergyConsuming(SmartOutlet smartOutlet) {
        smartOutlet.setVoltage(voltage);
        smartOutlet.setAmps(amps);
        smartOutlet.setEnergyConsumingPerHours(energyConsumingPerHours);
        smartOutlet.setStatus(DeviceStatus.ACTIVE);
        smartOutlet.setActive(true);
        smartOutlet.setOn(true);
    }


    /**
     * Authenticates the user and verifies their access rights.
     *
     * This method performs the following steps:
     * 1. Extracts the User object from the Authentication principal.
     * 2. Compares the ID of the authenticated user with the provided owner ID.
     * 3. If the IDs don't match, throws an AccessDeniedException.
     * 4. If authentication is successful, logs an info message.
     *
     * @param authentication The Authentication object containing the user's credentials and details.
     * @param ownerId The ID of the owner to be verified against the authenticated user.
     * @throws AccessDeniedException If the authenticated user's ID doesn't match the provided owner ID.
     * @throws ClassCastException If the principal in the Authentication object is not of type User.
     */
    private void authenticateUser(Authentication authentication, Integer ownerId){
        User user = ((User) authentication.getPrincipal());
        if (!user.getId().equals(ownerId)) {
            throw new AccessDeniedException("Cannot find user with  id " + ownerId);
        }
        log.info("User with id is authenticated: {}", user.getId());
    }


    private SmartOutlet updateSmartOutletEnergyConsuming(SmartOutlet existingOutlet, SmartOutletEnergyConsumingRequest request) {
        existingOutlet.setVoltage(request.getVoltage());
        existingOutlet.setAmps(request.getAmps());
        existingOutlet.setEnergyConsumingPerHours(request.getEnergyConsumingPerHours());
        return existingOutlet;
    }



}
