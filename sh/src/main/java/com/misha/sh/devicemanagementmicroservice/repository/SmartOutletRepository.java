package com.misha.sh.devicemanagementmicroservice.repository;

import com.misha.sh.devicemanagementmicroservice.model.device.DeviceStatus;
import com.misha.sh.devicemanagementmicroservice.model.smartOutlet.SmartOutlet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SmartOutletRepository extends JpaRepository<SmartOutlet, Integer> {

    @Query(
            """
        SELECT o FROM SmartOutlet o
        WHERE o.user.id= :userId
            """
    )
    Page<SmartOutlet> findAllOutletsByUser(Pageable pageable,@Param("userId") Integer userId);

    @Query("SELECT s FROM SmartOutlet s WHERE s.scheduledOn <= :now AND s.status = :status")
    List<SmartOutlet> findByScheduledOnBeforeAndStatus(
            @Param("now") LocalDateTime now,
            @Param("status") DeviceStatus status
    );


  //  List<SmartOutlet> findByScheduledOnBeforeAndStatus(LocalDateTime now, DeviceStatus deviceStatus);
}
