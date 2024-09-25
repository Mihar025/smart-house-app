package com.misha.sh.devicemanagementmicroservice.repository;

import com.misha.sh.devicemanagementmicroservice.model.smokeSensor.SmokeSensor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SmokeSensorRepository extends JpaRepository<SmokeSensor, Integer> {


    @Query(
            """
        select s
        from SmokeSensor s
        WHERE s.user.id = :userId
"""
    )
    Page<SmokeSensor> findAllUserSmokeSensor(Pageable pageable, @Param("userId") Integer userId);
}
