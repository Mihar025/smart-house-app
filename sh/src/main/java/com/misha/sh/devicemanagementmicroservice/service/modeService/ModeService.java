package com.misha.sh.devicemanagementmicroservice.service.modeService;

import com.misha.sh.devicemanagementmicroservice.exception.BusinessException;
import com.misha.sh.devicemanagementmicroservice.mapper.ModeMapper;import com.misha.sh.devicemanagementmicroservice.model.device.Mode;
import com.misha.sh.devicemanagementmicroservice.repository.ModeRepository;
import com.misha.sh.devicemanagementmicroservice.request.device.mode.ModeResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class ModeService {

    private final ModeRepository modeRepository;
    private final ModeMapper modeMapper;


    public List<ModeResponse> findAllModes() {
        try {
            List<Mode> modes = modeRepository.findAll();
            return modes.stream()
                    .map(modeMapper::toModeResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error while fetching all modes: ", e);
            throw new BusinessException("Failed to fetch modes");
        }
    }


    public ModeResponse setLowEnergyConsumingMode(String lowMode) {
         var foundedLowMode = modeRepository.findModeByLowEnergyConsumingMode(lowMode)
                 .orElseThrow(() -> new BusinessException("Cannot find low energy mode"));
        isModeTurnedOn(foundedLowMode.getId());
        log.info("Low mode is not turned on");
        lowMode(foundedLowMode);
        log.info("Mode is turned on successfully! {}", foundedLowMode.getId());
        modeRepository.save(foundedLowMode);
        log.info("Mode is saved successfully!");
        return modeMapper.toModeResponseLow(foundedLowMode);
    }

    public ModeResponse setHighEnergyConsumingMode(String highMode) {
        var foundedMode = modeRepository.findModeByHighEnergyConsumingMode(highMode)
                .orElseThrow(() -> new BusinessException("Cannot find high energy mode"));
        isModeTurnedOn(foundedMode.getId());
        log.info(" High mode is not turned on");
        highMode(foundedMode);
        log.info("Mode is turned on successfully to high ! {}", foundedMode.getId());
        modeRepository.save(foundedMode);
        log.info("High mode is saved successfully!");
        return modeMapper.toModeResponseHigh(foundedMode);
    }

    public ModeResponse setDefaultEnergyConsumingMode(String defaultMode) {

        var foundedMode = modeRepository.findModeByDefaultEnergyConsumingMode(defaultMode)
                .orElseThrow(() -> new BusinessException("Cannot find default energy mode"));
        isModeTurnedOn(foundedMode.getId());
        log.info("Default Mode is not turned on");
        defaultMode(foundedMode);
        log.info("Mode is turned on successfully to default ! {}", foundedMode.getId());
        modeRepository.save(foundedMode);
       return modeMapper.toModeResponseDefault(foundedMode);
    }



    public ModeResponse scheduledLowEnergyConsumingModeActivation (String lowMode, LocalDateTime from, LocalDateTime to) {
        var foundedMode = modeRepository.findModeByLowEnergyConsumingMode(lowMode)
                .orElseThrow(() -> new BusinessException("Cannot find low energy mode"));
        lowMode(foundedMode);
        setDate(from, to, foundedMode);
        modeRepository.save(foundedMode);
        return modeMapper.toModeResponseLow(foundedMode);
    }

    public ModeResponse scheduledHighEnergyConsumingModeActivation(String highMode, LocalDateTime from, LocalDateTime to) {
        var foundedMode = modeRepository.findModeByHighEnergyConsumingMode(highMode)
                .orElseThrow(() -> new BusinessException("Cannot find low energy mode"));
        highMode(foundedMode);
        setDate(from, to, foundedMode);
        modeRepository.save(foundedMode);
        return modeMapper.toModeResponseHigh(foundedMode);
    }
// todo
    public ModeResponse scheduledDefaultEnergyConsumingModeActivation(String defaultMode, LocalDateTime from, LocalDateTime to) {
        var foundedMode = modeRepository.findModeByDefaultEnergyConsumingMode(defaultMode)
                .orElseThrow(() -> new BusinessException("Cannot find default energy mode"));
        defaultMode(foundedMode);
        setDate(from, to, foundedMode);
        modeRepository.save(foundedMode);
        return modeMapper.toModeResponseDefault(foundedMode);
    }


    public ModeResponse findLowEnergyConsumingMode(String lowMode) {
        var foundedMode = modeRepository.findModeByLowEnergyConsumingMode(lowMode)
                .orElseThrow(() -> new BusinessException("Cannot find low energy mode"));
        log.info("Founded mode: {}", foundedMode);
        if(!foundedMode.getLowEnergyConsumingMode().equals(lowMode)) {
            throw new BusinessException("Cannot find low energy mode");
        }
        return modeMapper.toModeResponseLow(foundedMode);
    }

    public ModeResponse findHighEnergyConsumingMode(String highMode) {
        var foundedMode = modeRepository.findModeByHighEnergyConsumingMode(highMode)
                .orElseThrow(() -> new BusinessException("Cannot find high energy mode"));
        log.info("Founded high mode: {}", foundedMode);
        if(!foundedMode.getHighEnergyConsumingMode().equals(highMode)) {
            throw new BusinessException("Cannot find high energy mode");
        }
        return modeMapper.toModeResponseHigh(foundedMode);
    }


    public ModeResponse findDefaultEnergyConsumingMode(String defaultMode) {
        var foundedMode = modeRepository.findModeByDefaultEnergyConsumingMode(defaultMode)
                .orElseThrow(() -> new BusinessException("Cannot find default energy mode"));
        log.info("Founded default mode : {}", foundedMode);
        if(!foundedMode.getDefaultMode().equals(defaultMode)) {
            throw new BusinessException("Cannot find default energy mode");
        }
        return modeMapper.toModeResponseDefault(foundedMode);
    }




    @Transactional
    public ModeResponse cancelMode(Integer modeId, String defaultMode) {
        Mode foundMode = modeRepository.findById(modeId)
                .orElseThrow(() -> new EntityNotFoundException("Cannot find mode with id: " + modeId));

        if (!foundMode.isTurnedOn()) {
            log.info("Mode {} is already turned off", modeId);
            return modeMapper.toModeResponse(foundMode);
        }
        foundMode.setTurnedOn(false);
        setNullMode(foundMode);
        defaultMode(foundMode);
        modeRepository.save(foundMode);
        log.info("Mode {} has been cancelled and set to default mode: {}", modeId, defaultMode);
        return modeMapper.toModeResponseDefault(foundMode);
    }




    private void setNullMode(Mode modeRequest) {
        modeRequest.setLowEnergyConsumingMode(null);
        modeRequest.setHighEnergyConsumingMode(null);
    }

    private void isModeTurnedOn(Integer modeId) {
        var mode = modeRepository.findById(modeId)
                .orElseThrow(() -> new BusinessException("Cannot find mode"));
        if(mode.isTurnedOn()){
            throw new BusinessException("Mode already turned on");
        }
    }


    private void lowMode(Mode mode) {
        mode.setTurnedOn(true);
        mode.setVoltage(5);
        mode.setAmps(1);
        mode.setElectricityConsuming(10.0);
        mode.setTime(LocalDateTime.now());
    }

    private void highMode(Mode mode) {
        mode.setTurnedOn(true);
        mode.setVoltage(15);
        mode.setAmps(4);
        mode.setElectricityConsuming(30.0);
        mode.setTime(LocalDateTime.now());
    }

    private void defaultMode(Mode mode){
        mode.setTurnedOn(true);
        mode.setVoltage(3);
        mode.setAmps(2);
        mode.setElectricityConsuming(15.0);
        mode.setTime(LocalDateTime.now());
    }

    private void setDate(LocalDateTime from, LocalDateTime to, Mode mode) {
        mode.setStartTime(from);
        mode.setFinisTime(to);
    }







}
