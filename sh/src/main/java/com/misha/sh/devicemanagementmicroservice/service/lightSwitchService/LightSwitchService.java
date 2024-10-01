package com.misha.sh.devicemanagementmicroservice.service.lightSwitchService;


import com.misha.sh.devicemanagementmicroservice.exception.BusinessException;
import com.misha.sh.devicemanagementmicroservice.mapper.LightSwitchMapper;
import com.misha.sh.devicemanagementmicroservice.model.swtichLight.LightSwitch;
import com.misha.sh.devicemanagementmicroservice.model.User;
import com.misha.sh.devicemanagementmicroservice.pagination.PageResponse;
import com.misha.sh.devicemanagementmicroservice.repository.LightSwitchRepository;
import com.misha.sh.devicemanagementmicroservice.request.lightSwitch.addSwitch.LightSwitchRequest;
import com.misha.sh.devicemanagementmicroservice.request.lightSwitch.addSwitch.LightSwitchResponse;
import com.misha.sh.devicemanagementmicroservice.request.lightSwitch.brightness.ColorTemperatureResponse;
import com.misha.sh.devicemanagementmicroservice.request.lightSwitch.brightness.BrightnessRequest;
import com.misha.sh.devicemanagementmicroservice.request.lightSwitch.brightness.BrightnessResponse;
import com.misha.sh.devicemanagementmicroservice.request.lightSwitch.brightness.ColorTemperatureRequest;
import com.misha.sh.devicemanagementmicroservice.request.lightSwitch.switchTurnOff.SwitchTurnOffResponse;
import com.misha.sh.devicemanagementmicroservice.request.lightSwitch.switchTurnOn.SwitchTurnOnRequest;
import com.misha.sh.devicemanagementmicroservice.request.lightSwitch.switchTurnOn.SwitchTurnOnResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.misha.sh.devicemanagementmicroservice.model.swtichLight.ColorTemperature.NEUTRAL;
import static com.misha.sh.devicemanagementmicroservice.model.swtichLight.LightSwitchMode.DEFAULT_MODE;
import static com.misha.sh.devicemanagementmicroservice.model.swtichLight.LightSwitchMode.LOW_MODE;

@Service
@RequiredArgsConstructor
public class LightSwitchService {

    private final LightSwitchRepository lightSwitchRepository;
    private final LightSwitchMapper lightSwitchMapper;





    public PageResponse<LightSwitchResponse> findAllSwitches(int size,
                                                             int page,
                                                             Authentication authentication) {
        User user = ((User) authentication.getPrincipal());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<LightSwitch> lightSwitches = lightSwitchRepository.findAllLightSwitchesByUser(pageable, user.getId());
        List<LightSwitchResponse> lightSwitchResponses = lightSwitches.getContent()
                .stream()
                .map(switches -> {
                    if(user.getId().equals(switches.getUser().getId())) {
                        return lightSwitchMapper.toLightSwitchResponse(switches);
                    }
                    else{
                        throw new AccessDeniedException("You do not have permission to access this resource");
                    }
                })
                .collect(Collectors.toList());
        return new PageResponse<>(
                lightSwitchResponses,
                lightSwitches.getNumber(),
                lightSwitches.getSize(),
                lightSwitches.getTotalElements(),
                lightSwitches.getTotalPages(),
                lightSwitches.isFirst(),
                lightSwitches.isLast()
        );
    }



    public LightSwitchResponse findSwitchById(Integer switchId, Authentication authentication) {
        User user = ((User) authentication.getPrincipal());
        var lightSwitch = lightSwitchRepository.findById(switchId)
                .orElseThrow(EntityNotFoundException::new);
        if(!user.getId().equals(lightSwitch.getUser().getId())) {
            throw new AccessDeniedException("You dont have permission for this actions!");
        }
        return lightSwitchMapper.toLightSwitchResponse(lightSwitch);
    }



    public SwitchTurnOnResponse turnOnLightSwitch(Integer switchId, Authentication authentication) throws BusinessException {
        User user = ((User) authentication.getPrincipal());
        var lightSwitch = lightSwitchRepository.findById(switchId).orElseThrow(
                () -> new EntityNotFoundException("Switch not found")
        );
        if(!user.getId().equals(lightSwitch.getUser().getId())) {
            throw new BusinessException("You dont have permission for this actions!");
        }
            if (!lightSwitch.getIsOn()) {
                if(lightSwitch.getBatteryLevel() >20){
                    turnOnDefaultLightSettings(lightSwitch);
                     lightSwitchRepository.save(lightSwitch);
                }
                else if(lightSwitch.getBatteryLevel()<20){
                    turnOnLowLightSettings(lightSwitch);
                    lightSwitchRepository.save(lightSwitch);
                }
            }else{
                throw new IllegalStateException("Switch with id " + switchId + " is already turned on");
            }
        return lightSwitchMapper.toSwitchTurnOnResponse(lightSwitch);
    }

    @Transactional(rollbackOn = Exception.class)
    public SwitchTurnOnResponse userLightSwitchPreferences(Integer switchId, SwitchTurnOnRequest turnOnRequest, Authentication authentication) throws BusinessException {
        User user = ((User) authentication.getPrincipal());
        var lightSwitch = lightSwitchRepository.findById(switchId).orElseThrow(
                () -> new EntityNotFoundException("Switch not found")
        );
        if(!user.getId().equals(lightSwitch.getUser().getId())) {
            throw new BusinessException("You dont have permission for this actions!");
        }

        if (!lightSwitch.getIsOn()) {
            if(lightSwitch.getBatteryLevel() > 20) {
                turnOnCustom(lightSwitch, turnOnRequest);
                lightSwitchRepository.save(lightSwitch);
            }
            else if (lightSwitch.getBatteryLevel() < 20) {
                turnOnCustomLowBatteryLightSettings(lightSwitch);
                lightSwitchRepository.save(lightSwitch);
            }
        }else{
            throw new IllegalStateException("Switch with id " + switchId + " is already turned on");
        }
        return lightSwitchMapper.toSwitchTurnOnResponse(lightSwitch);
    }


    @Transactional(rollbackOn = BusinessException.class)
            public SwitchTurnOffResponse switchTurnOff(Integer switchId, Authentication authentication) {
                User user = ((User) authentication.getPrincipal());
        var foundedSwitch = lightSwitchRepository.findById(switchId)
                        .orElseThrow(
                 ()
                         -> new EntityNotFoundException("Switch not found")
                                    );
                if(!user.getId().equals(foundedSwitch.getUser().getId())){
                    throw new BusinessException("You dont have permission for this actions!");
                }
                    turnOffDefaultLightSettings(foundedSwitch);
                    lightSwitchRepository.save(foundedSwitch);
                return   lightSwitchMapper.toDeviceTurnOffForSwitch(foundedSwitch);

    }

    @Transactional(rollbackOn = BusinessException.class)
    public BrightnessResponse changeBrightness(BrightnessRequest brightness, Integer switchId,  Authentication authentication) {
        User user = ((User) authentication.getPrincipal());
        var foundSwitch = lightSwitchRepository.findById(switchId)
                .orElseThrow(() -> new EntityNotFoundException("Switch not found with id: " + switchId));

        if(!user.getId().equals(foundSwitch.getUser().getId())){
            throw new BusinessException("You dont have permission for this actions!");
        }

        if (!foundSwitch.getIsOn()) {
            throw new IllegalStateException("Cannot change brightness of a switched off light");
        }

        Integer newBrightness = brightness.getBrightness();
        var newColorTemperature = brightness.getColorTemperature();
        if (newBrightness < 0 || newBrightness > 100) {
            throw new IllegalArgumentException("Brightness should be between 0 and 100");
        }

        if (foundSwitch.getBatteryLevel() > 20) {
            foundSwitch.setBrightness(newBrightness);
            foundSwitch.setColorTemperature(newColorTemperature);
        } else if (foundSwitch.getBatteryLevel() <= 20) {
            foundSwitch.setBrightness(Math.min(45, newBrightness));
            foundSwitch.setColorTemperature(newColorTemperature);
        } else {
            throw new IllegalStateException("Unexpected switch state: battery level = "
                    + foundSwitch.getBatteryLevel() + ", mode = " + foundSwitch.getMode());
        }
        LightSwitch savedSwitch = lightSwitchRepository.save(foundSwitch);
        return lightSwitchMapper.toBrightLightSwitchResponse(savedSwitch);
    }


    @Transactional(rollbackOn = BusinessException.class)
    public ColorTemperatureResponse changeColorTemperature(ColorTemperatureRequest colorTemperature, Integer switchId, Authentication authentication) {
        User user = ((User) authentication.getPrincipal());
        var foundedSwitch = lightSwitchRepository.findById(switchId)
                .orElseThrow(() -> new EntityNotFoundException("Switch not found"));
        if(!user.getId().equals(foundedSwitch.getUser().getId())){
            throw new BusinessException("You dont have permission for this actions!");
        }

        if(!foundedSwitch.getIsOn())
            throw new IllegalStateException("Cannot change color temperature of a switched off light");
        if(foundedSwitch.getBatteryLevel() > 20) {
            foundedSwitch.setColorTemperature(colorTemperature.getColorTemperature());
        }
        else if(foundedSwitch.getBatteryLevel() <= 20) {
            foundedSwitch.setColorTemperature(colorTemperature.getColorTemperature());
        }
        else{
            throw  new BusinessException("Cannot change color temperature of a switched off light");
        }
        return lightSwitchMapper.toColorTemperatureResponse(foundedSwitch);
    }




    @Transactional(rollbackOn = BusinessException.class)
    public LightSwitchResponse addSwitchLight(LightSwitchRequest lightSwitchRequest, Authentication authentication) {
        User user = ((User) authentication.getPrincipal());
        LightSwitch lightSwitch =  lightSwitchMapper.toLight(lightSwitchRequest);
                lightSwitch.setUser(user);
                lightSwitch.setIsOn(false);
                lightSwitchRepository.save(lightSwitch);
        return lightSwitchMapper.toLightSwitchResponse(lightSwitch);
    }



    @Transactional(rollbackOn = BusinessException.class)
    public void deleteSwitchLight(Integer switchId, Authentication authentication) {
        User user = ((User) authentication.getPrincipal());
        var lightSwitch = lightSwitchRepository.findById(switchId)
                .orElseThrow(() -> new EntityNotFoundException("Switch not found with id: " + switchId));
        if(!user.getId().equals(lightSwitch.getUser().getId())){
            throw new BusinessException("You dont have permission for this actions!");
        }
        lightSwitchRepository.delete(lightSwitch);
    }


    private void turnOnDefaultLightSettings(LightSwitch lightSwitch){
        lightSwitch.setIsOn(Boolean.TRUE);
        lightSwitch.setBrightness(100);
        lightSwitch.setColorTemperature(NEUTRAL);
        lightSwitch.setMinWattage(10);
        lightSwitch.setMaxWattage(75);
        lightSwitch.setLightSwitchMode(DEFAULT_MODE);
    }

    private void turnOnLowLightSettings(LightSwitch lightSwitch){
        lightSwitch.setIsOn(Boolean.TRUE);
        lightSwitch.setBrightness(50);
        lightSwitch.setColorTemperature(NEUTRAL);
        lightSwitch.setMinWattage(5);
        lightSwitch.setMaxWattage(45);
        lightSwitch.setLightSwitchMode(LOW_MODE);
    }



    private void turnOnCustom(LightSwitch lightSwitch, SwitchTurnOnRequest switchTurnOnRequest){
        lightSwitch.setIsOn(Boolean.TRUE);
        lightSwitch.setBrightness(switchTurnOnRequest.getBrightness());
        lightSwitch.setColorTemperature(switchTurnOnRequest.getColorTemperature());
        lightSwitch.setMinWattage(switchTurnOnRequest.getMinWattage());
        lightSwitch.setMaxWattage(switchTurnOnRequest.getMaxWattage());
        lightSwitch.setLightSwitchMode(switchTurnOnRequest.getMode());
    }


    private void turnOnCustomLowBatteryLightSettings(LightSwitch lightSwitch){
        lightSwitch.setIsOn(Boolean.TRUE);
        lightSwitch.setBrightness(50);
        lightSwitch.setColorTemperature(NEUTRAL);
        lightSwitch.setMinWattage(5);
        lightSwitch.setMaxWattage(45);
        lightSwitch.setLightSwitchMode(LOW_MODE);


    }


    private void turnOffDefaultLightSettings(LightSwitch lightSwitch){
        lightSwitch.setIsOn(Boolean.FALSE);
        lightSwitch.setBrightness(0);
        lightSwitch.setColorTemperature(null);
        lightSwitch.setMinWattage(0);
        lightSwitch.setMaxWattage(0);
        lightSwitch.setStartAt(null);
        lightSwitch.setEndAt(null);
        lightSwitch.setLightSwitchMode(null);

    }



















}
