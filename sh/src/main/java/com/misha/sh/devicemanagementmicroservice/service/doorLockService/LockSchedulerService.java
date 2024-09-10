package com.misha.sh.devicemanagementmicroservice.service.doorLockService;

import com.misha.sh.devicemanagementmicroservice.model.doorLock.DoorLock;
import com.misha.sh.devicemanagementmicroservice.repository.DoorLockRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
@AllArgsConstructor
public class LockSchedulerService {

    private final TaskScheduler taskScheduler;
    private final DoorLockRepository doorLockRepository;

    public void scheduleLock(Integer lockId, int minutes) {
        DoorLock lock = doorLockRepository.findById(lockId)
                .orElseThrow(() -> new EntityNotFoundException("DoorLock with id " + lockId + " not found"));

        LocalDateTime lockTime = LocalDateTime.now().plusMinutes(minutes);
        lock.setScheduledLockTime(lockTime);
        doorLockRepository.save(lock);

        taskScheduler.schedule(() -> lockDoor(lockId), Date.from(lockTime.atZone(ZoneId.systemDefault()).toInstant()));
    }

    @Transactional
    public void lockDoor(Integer lockId) {
        DoorLock lock = doorLockRepository.findById(lockId)
                .orElseThrow(() -> new EntityNotFoundException("DoorLock with id " + lockId + " not found"));

        if (lock.isOpened()) {
            lock.setOpened(false);
            lock.setLocked(true);
            doorLockRepository.save(lock);
        }
    }
}