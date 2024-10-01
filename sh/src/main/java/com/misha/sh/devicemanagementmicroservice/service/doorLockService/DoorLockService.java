package com.misha.sh.devicemanagementmicroservice.service.doorLockService;

import com.misha.sh.devicemanagementmicroservice.exception.BusinessException;
import com.misha.sh.devicemanagementmicroservice.mapper.DoorLockMapper;
import com.misha.sh.devicemanagementmicroservice.model.device.DeviceStatus;
import com.misha.sh.devicemanagementmicroservice.model.device.DeviceType;
import com.misha.sh.devicemanagementmicroservice.model.doorLock.DoorLock;
import com.misha.sh.devicemanagementmicroservice.model.doorLock.FingerPrintCode;
import com.misha.sh.devicemanagementmicroservice.model.doorLock.LockMechanism;
import com.misha.sh.devicemanagementmicroservice.model.doorLock.LockStatus;
import com.misha.sh.devicemanagementmicroservice.model.User;
import com.misha.sh.devicemanagementmicroservice.model.smartOutlet.SmartOutlet;
import com.misha.sh.devicemanagementmicroservice.pagination.PageResponse;
import com.misha.sh.devicemanagementmicroservice.repository.DoorLockRepository;
import com.misha.sh.devicemanagementmicroservice.repository.UserRepository;
import com.misha.sh.devicemanagementmicroservice.request.doorLock.*;
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

import static com.misha.sh.devicemanagementmicroservice.model.doorLock.FingerPrintCode.FINGER_PRINT_CODE;
import static com.misha.sh.devicemanagementmicroservice.model.doorLock.LockMechanism.FINGER_PRINT;
import static com.misha.sh.devicemanagementmicroservice.model.doorLock.LockMechanism.PIN_COD;

@Slf4j
@Service
@RequiredArgsConstructor
public class DoorLockService {

    private static final int AUTO_LOCK_DELAY_MINUTES = 5;



    private final DoorLockRepository doorLockRepository;
    private final DoorLockMapper doorLockMapper;
    private final UserRepository userRepository;
    private final LockSchedulerService lockSchedulerService;


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
    @Transactional(rollbackOn = Exception.class)
    public void deleteDoorLockById(Integer doorLockId, Authentication authentication) {
        User user = ((User) authentication.getPrincipal());
        var doorLock = doorLockRepository.findById(doorLockId).orElseThrow(
                () -> new EntityNotFoundException("DoorLock with this id does not exist")
        );
        if(!user.getId().equals(doorLock.getUser().getId())) {
            throw new AccessDeniedException("You do not have permission to access this resource");
        }
        doorLockRepository.deleteById(doorLock.getId());
    }



        public DoorLockResponse findLockById(Integer lockId, Authentication authentication) {
        User user = ((User) authentication.getPrincipal());
        var smartLock = doorLockRepository.findById(lockId).orElseThrow(EntityNotFoundException::new);
        if(!user.getId().equals(smartLock.getUser().getId())) {
            throw new BusinessException("User with this id does not match");
        }
        return doorLockMapper.toDoorLockResponse(smartLock);
        }

    public void changeAccessCode(Authentication authentication, ChangeDoorLockRequest request, Integer doorLockId) {
        User user = authenticateUser(authentication);
        DoorLock doorLock = doorLockRepository.findById(doorLockId)
                .orElseThrow(() -> new EntityNotFoundException("DoorLock with this id does not exist"));

        if (!doorLock.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You don't have permission to change this door lock's code");
        }

        if (doorLock.getAccessCode().equals(request.getDoorCode())) {
            throw new BusinessException("New access code must be different from the current one");
        }

        doorLock.setAccessCode(request.getDoorCode());
        doorLockRepository.save(doorLock);
    }


            // SWITCH CASE : PIN CODE, OR FINGER PRINT
            public void setOpeningType(Integer doorLockId, LockMechanism openingType, Integer accessCode,  Authentication authentication) {
                User user = ((User) authentication.getPrincipal());
                var doorLock = doorLockRepository.findById(doorLockId)
                        .orElseThrow(() -> new EntityNotFoundException("DoorLock with this id does not exist"));
                if(!user.getId().equals(doorLock.getUser().getId())) {
                    throw new BusinessException("User with this id does not match");
                }
                switch (openingType){
                    case PIN_COD:
                        if(accessCode == null){
                            throw new BusinessException("Access code is required and cannot be null");
                        }
                        setDoorLockAccessCode(doorLock, accessCode);
                        break;
                    case FINGER_PRINT:
                        setFingerPrintOnDoorLock(doorLock);
                        break;
                    case BOTH:
                        if (accessCode == null) {
                            throw new IllegalArgumentException("Access code is required for BOTH type");
                        }
                        setDoorLockAccessCode(doorLock, accessCode);
                        setFingerPrintOnDoorLock(doorLock);
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid opening type");
                }
                doorLockRepository.save(doorLock);
            }





    public DoorLockStatus openDoorLock(Authentication authentication, Integer doorLockId, Integer accessCode, String fingerPrintCode) {
        User user = authenticateUser(authentication);
        log.info("User was successfully authenticated");
        var doorLock = getDoorLock(doorLockId, authentication);
        log.info("Door lock was successfully found!");

        switch (doorLock.getLockMechanism()) {
            case PIN_COD:
                if (accessCode == null || !accessCode.equals(doorLock.getAccessCode())) {
                    log.warn("Invalid access code attempt for door lock {}", doorLockId);
                    throw new BusinessException("Invalid access code");
                }
                break;
            case FINGER_PRINT:
                if (fingerPrintCode == null || !fingerPrintCode.equals(doorLock.getFingerPrintCode())) {
                    log.warn("Invalid fingerprint attempt for door lock {}", doorLockId);
                    throw new BusinessException("Invalid fingerprint");
                }
                break;
            case BOTH:
                if ((accessCode == null || !accessCode.equals(doorLock.getAccessCode())) &&
                        (fingerPrintCode == null || !fingerPrintCode.equals(doorLock.getFingerPrintCode()))) {
                    log.warn("Invalid access attempt for door lock {}", doorLockId);
                    throw new BusinessException("Invalid access code and fingerprint");
                }
                break;
            default:
                throw new BusinessException("Invalid lock mechanism");
        }
        var updatedSettingDoorLock = setDoorLockOpenedSetting(doorLock);
        lockSchedulerService.scheduleLock(doorLockId, AUTO_LOCK_DELAY_MINUTES);
        return doorLockMapper.toDoorLockStatus(updatedSettingDoorLock);
    }


                public DoorLockStatus closeDoorLock(Authentication authentication, Integer doorLockId, Integer accessCode) {
                        User user =  authenticateUser(authentication);
                        DoorLock doorLock = getDoorLock(doorLockId, authentication);
                        if(!user.getId().equals(doorLock.getUser().getId())) {
                            log.warn("User with this id does not match");
                            throw new BusinessException("User with this id does not match");
                        }
                        log.info("User was successfully  authenticated");

                    if(!accessCode.equals(doorLock.getAccessCode())){
                        log.warn("Invalid  access code attempt for door lock {}", doorLockId);
                        throw new BusinessException("DoorLock with this access code does not exist");
                    }
                     var closedDoorLock = setClosingDoorLockSettings(doorLock);
                    return doorLockMapper.toDoorLockStatus(closedDoorLock);
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
        doorLock.setLockMechanism(PIN_COD);
        doorLock.setRemoteAccessEnabled(true);
    }

    private DoorLock setClosingDoorLockSettings(DoorLock doorLock){
        doorLock.setLastClosedAt(LocalDateTime.now());
        doorLock.setOpened(false);
        doorLock.setLocked(true);
        doorLock.setLockStatus(LockStatus.LOCKED);
        doorLockRepository.save(doorLock);
        return doorLock;
    }


    private User authenticateUser(Authentication authentication) {
        User user = ((User) authentication.getPrincipal());
       return userRepository.findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("User with id " + user.getId() + " not found"));

    }

    private DoorLock getDoorLock(Integer doorLockId, Authentication authentication) {
        User user = authenticateUser(authentication);
        var doorLock =  doorLockRepository.findById(doorLockId)
                .orElseThrow(() -> new EntityNotFoundException("DoorLock with this id does not exist"));
        if(!user.getId().equals(doorLock.getUser().getId())) {
            log.warn("User with this id does not match");
            throw new BusinessException("User with this id does not match");
        }
        return doorLock;
    }




    private DoorLock setDoorLockOpenedSetting(DoorLock doorLock){
        doorLock.setLastOpenedAt(LocalDateTime.now());
        doorLock.setOpened(true);
        doorLock.setLockStatus(LockStatus.UNLOCKED);
        doorLockRepository.save(doorLock);
        return doorLock;
    }



    // FUNCTION FOR SETTING ACCESS CODE
    private void setDoorLockAccessCode(DoorLock doorLock, Integer accessCode){
        doorLock.setAccessCode(accessCode);
        doorLock.setLockMechanism(PIN_COD);
        setDoorCloseAfterChangingOrAddingLockType(doorLock);
    }

    private void setFingerPrintOnDoorLock(DoorLock doorLock){
        doorLock.setFingerPrintCode(FINGER_PRINT_CODE);
        doorLock.setLockMechanism(FINGER_PRINT);
        setDoorCloseAfterChangingOrAddingLockType(doorLock);
    }

    private void setDoorCloseAfterChangingOrAddingLockType(DoorLock doorLock){
        doorLock.setLockStatus(LockStatus.LOCKED);
        doorLock.setLocked(true);
        doorLock.setOpened(false);
    }



}
