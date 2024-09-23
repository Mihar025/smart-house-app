package com.misha.sh.devicemanagementmicroservice.repository;

import com.misha.sh.devicemanagementmicroservice.model.airQualtiySensor.AirQualitySensor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AirQualitySensorRepository extends JpaRepository<AirQualitySensor, Integer> {

    @Query(
            """
            SELECT aqs FROM AirQualitySensor aqs
            WHERE aqs.user.id= :userId
            """
    )
    Page<AirQualitySensor> findAllAirQualitySensorByUser(Pageable pageable, @Param("userId") Integer userId);
}
