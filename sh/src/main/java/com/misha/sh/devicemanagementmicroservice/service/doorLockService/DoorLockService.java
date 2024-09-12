package com.misha.sh.devicemanagementmicroservice.service.doorLockService;

import com.misha.sh.devicemanagementmicroservice.exception.BusinessException;
import com.misha.sh.devicemanagementmicroservice.mapper.DoorLockMapper;
import com.misha.sh.devicemanagementmicroservice.model.device.DeviceStatus;
import com.misha.sh.devicemanagementmicroservice.model.device.DeviceType;
import com.misha.sh.devicemanagementmicroservice.model.doorLock.DoorLock;
import com.misha.sh.devicemanagementmicroservice.model.doorLock.LockMechanism;
import com.misha.sh.devicemanagementmicroservice.model.doorLock.LockStatus;
import com.misha.sh.devicemanagementmicroservice.model.User;
import com.misha.sh.devicemanagementmicroservice.repository.DoorLockRepository;
import com.misha.sh.devicemanagementmicroservice.repository.UserRepository;
import com.misha.sh.devicemanagementmicroservice.request.doorLock.DoorLockAccessCodeRequest;
import com.misha.sh.devicemanagementmicroservice.request.doorLock.DoorLockRequest;
import com.misha.sh.devicemanagementmicroservice.request.doorLock.DoorLockResponse;
import com.misha.sh.devicemanagementmicroservice.request.doorLock.DoorLockStatus;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class DoorLockService {

    private static final int AUTO_LOCK_DELAY_MINUTES = 5;



    private final DoorLockRepository doorLockRepository;
    private final DoorLockMapper doorLockMapper;
    private final UserRepository userRepository;
    private final LockSchedulerService lockSchedulerService;

    //1) todo findAllLocks
    //2) TODO find lock by id
    //3) todo add lock to user and auto add PinCode and set LockStatus
    //4)

    @Transactional(rollbackOn = Exception.class)
    public DoorLockResponse addDoorLock(DoorLockRequest doorLockRequest, Authentication authentication) {
            User user = authenticateUser(authentication);
            DoorLock doorLock = new DoorLock();
                doorLock.setUser(user);
                    addLock(doorLockRequest, doorLock);
                    if(doorLockRepository.existsBySerialNumber(doorLock.getSerialNumber())){
                        throw new BusinessException("DoorLock with this serial number already exists");
                    }
                        doorLockRepository.save(doorLock);
                            return doorLockMapper.toDoorLockResponse(doorLock);
    }

        public void changeAccessCode(Authentication authentication, Integer accessCode, Integer doorLockId) {
            authenticateUser(authentication);
                    var doorLock = doorLockRepository.findById(doorLockId)
                            .orElseThrow(() -> new EntityNotFoundException("DoorLock with this id does not exist"));
                    if(doorLock.getAccessCode().equals(accessCode)){
                        throw new BusinessException("DoorLock with this access code already exists");
                    }
                    doorLock.setAccessCode(accessCode);
                    doorLockRepository.save(doorLock);
        }

            public DoorLockStatus openDoorLock(Authentication authentication, Integer doorLockId, DoorLockAccessCodeRequest accessCodeRequest) {
                  User user =  authenticateUser(authentication);
                  log.info("User was successfully authenticated");
                  DoorLock doorLock = getDoorLock(doorLockId);
                  log.info("Door lock was successfully founded!");

                if(!accessCodeRequest.getAccessCode().equals(doorLock.getAccessCode())){
                    log.warn("Invalid access code attempt for door lock {}", doorLockId);
                    throw new BusinessException("DoorLock with this access code does not exist");
                }

                    doorLock.setLastOpenedAt(LocalDateTime.now());
                    doorLock.setOpened(true);
                    doorLock.setLockStatus(LockStatus.UNLOCKED);
                    doorLockRepository.save(doorLock);
                    lockSchedulerService.scheduleLock(doorLockId,AUTO_LOCK_DELAY_MINUTES);

                    return doorLockMapper.toDoorLockStatus(doorLock);
            }

                public DoorLockStatus closeDoorLock(Authentication authentication, Integer doorLockId, DoorLockAccessCodeRequest accessCodeRequest) {
                        User user =  authenticateUser(authentication);
                        log.info("User was successfully  authenticated");
                        DoorLock doorLock = getDoorLock(doorLockId);
                         log.info("Door  lock was successfully founded!");
                    if(!accessCodeRequest.getAccessCode().equals(doorLock.getAccessCode())){
                        log.warn("Invalid  access code attempt for door lock {}", doorLockId);
                        throw new BusinessException("DoorLock with this access code does not exist");
                    }
                        doorLock.setLastClosedAt(LocalDateTime.now());
                        doorLock.setOpened(false);
                        doorLock.setLocked(true);
                        doorLock.setLockStatus(LockStatus.LOCKED);
                        doorLockRepository.save(doorLock);
                    return doorLockMapper.toDoorLockStatus(doorLock);
                }

    private void addLock(DoorLockRequest doorLockRequest, DoorLock doorLock) {
        doorLock.setDeviceName(doorLockRequest.getDeviceName());
        doorLock.setDeviceType(DeviceType.LOCK);
        doorLock.setDeviceDescription(doorLockRequest.getDeviceDescription());
        doorLock.setManufacturer(doorLockRequest.getManufacturer());
        doorLock.setDeviceModel(doorLockRequest.getDeviceModel());
        doorLock.setSerialNumber(doorLockRequest.getSerialNumber());
        doorLock.setStatus(DeviceStatus.ACTIVE);
        doorLock.setAccessCode(doorLockRequest.getAccessCode());
        doorLock.setLockMechanism(LockMechanism.PIN_COD);
        doorLock.setRemoteAccessEnabled(true);
    }

    private User authenticateUser(Authentication authentication) {
        User user = ((User) authentication.getPrincipal());
       return userRepository.findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("User with id " + user.getId() + " not found"));

    }

    private DoorLock getDoorLock(Integer doorLockId) {
        return doorLockRepository.findById(doorLockId)
                .orElseThrow(() -> new EntityNotFoundException("DoorLock with this id does not exist"));
    }

}
