package com.misha.sh.devicemanagementmicroservice.repository;

import com.misha.sh.devicemanagementmicroservice.model.doorLock.DoorLock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DoorLockRepository extends JpaRepository<DoorLock, Integer> {



    boolean existsBySerialNumber(String serialNumber);
}
