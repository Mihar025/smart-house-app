package com.misha.sh.devicemanagementmicroservice.service.doorLockService;

import com.misha.sh.devicemanagementmicroservice.exception.BusinessException;
import com.misha.sh.devicemanagementmicroservice.mapper.DoorLockMapper;
import com.misha.sh.devicemanagementmicroservice.model.device.DeviceStatus;
import com.misha.sh.devicemanagementmicroservice.model.device.DeviceType;
import com.misha.sh.devicemanagementmicroservice.model.doorLock.DoorLock;
import com.misha.sh.devicemanagementmicroservice.model.doorLock.LockMechanism;
import com.misha.sh.devicemanagementmicroservice.model.doorLock.LockStatus;
import com.misha.sh.devicemanagementmicroservice.model.User;
import com.misha.sh.devicemanagementmicroservice.model.smartOutlet.SmartOutlet;
import com.misha.sh.devicemanagementmicroservice.pagination.PageResponse;
import com.misha.sh.devicemanagementmicroservice.repository.DoorLockRepository;
import com.misha.sh.devicemanagementmicroservice.repository.UserRepository;
import com.misha.sh.devicemanagementmicroservice.request.doorLock.DoorLockAccessCodeRequest;
import com.misha.sh.devicemanagementmicroservice.request.doorLock.DoorLockRequest;
import com.misha.sh.devicemanagementmicroservice.request.doorLock.DoorLockResponse;
import com.misha.sh.devicemanagementmicroservice.request.doorLock.DoorLockStatus;
import com.misha.sh.devicemanagementmicroservice.request.smartOutlet.SmartOutletResponse;
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
                doorLock.setLockStatus(LockStatus.LOCKED);
                    addLock(doorLockRequest, doorLock);
                    if(doorLockRepository.existsBySerialNumber(doorLock.getSerialNumber())){
                        throw new BusinessException("DoorLock with this serial number already exists");
                    }
                        doorLockRepository.save(doorLock);
                            return doorLockMapper.toDoorLockResponse(doorLock);
    }

    public PageResponse<DoorLockResponse> findAllDoorLocks(
            Authentication authentication,
            int size,
            int page) {
        User user = ((User) authentication.getPrincipal());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<DoorLock> doorLocks = doorLockRepository.findAll(pageable);
        List<DoorLockResponse> doorLockResponses = doorLocks.getContent().stream()
                .map(doorLock -> {
                    if(user.getId().equals(doorLock.getUser().getId())) {
                        return doorLockMapper.toDoorLockResponse(doorLock);
                    }
                    else{
                        throw new AccessDeniedException("You do not have permission to access this resource");
                    }
                })
                .collect(Collectors.toList());

        return new PageResponse<>(
                doorLockResponses,
                doorLocks.getNumber(),
                doorLocks.getSize(),
                doorLocks.getTotalElements(),
                doorLocks.getTotalPages(),
                doorLocks.isFirst(),
                doorLocks.isLast()
        );
    }










        public DoorLockResponse findLockById(Integer lockId, Authentication authentication) {
        User user = ((User) authentication.getPrincipal());
        var smartLock = doorLockRepository.findById(lockId).orElseThrow(EntityNotFoundException::new);
        if(!user.getId().equals(smartLock.getUser().getId())) {
            throw new BusinessException("User with this id does not match");
        }
        return doorLockMapper.toDoorLockResponse(smartLock);
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

            public DoorLockStatus openDoorLock(Authentication authentication, Integer doorLockId, Integer accessCode) {
                  User user =  authenticateUser(authentication);
                  log.info("User was successfully authenticated");
                  DoorLock doorLock = getDoorLock(doorLockId);
                  log.info("Door lock was successfully founded!");

                  var lock = doorLockRepository.findById(doorLockId)
                          .orElseThrow(EntityNotFoundException::new);

                    if(!user.getId().equals(lock.getUser().getId())) {
                        log.warn("User with this id does not match");
                        throw new BusinessException("User with this id does not match");
                    }

                  if(!accessCode.equals(doorLock.getAccessCode())){
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

                public DoorLockStatus closeDoorLock(Authentication authentication, Integer doorLockId, Integer accessCode) {
                        User user =  authenticateUser(authentication);
                        DoorLock doorLock = getDoorLock(doorLockId);
                        if(!user.getId().equals(doorLock.getUser().getId())) {
                            log.warn("User with this id does not match");
                            throw new BusinessException("User with this id does not match");
                        }
                        log.info("User was successfully  authenticated");

                    if(!accessCode.equals(doorLock.getAccessCode())){
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
