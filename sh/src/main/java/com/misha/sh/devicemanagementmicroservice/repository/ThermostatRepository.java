package com.misha.sh.devicemanagementmicroservice.repository;

import com.misha.sh.devicemanagementmicroservice.model.thermostat.Thermostat;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;

@Repository
public interface ThermostatRepository extends JpaRepository<Thermostat, Integer> {

    @Query("""
    SELECT t FROM Thermostat t
    WHERE t.user.id = :userId
    """)
    Page<Thermostat> findAllThermostatsByUserId(@Param("userId") Integer userId, Pageable pageable);

    @Query("SELECT t FROM Thermostat t JOIN FETCH t.user")
    Page<Thermostat> findAllWithUser(Pageable pageable);

    @Query("SELECT t FROM Thermostat t WHERE t.user.id = :userId")
    Page<Thermostat> findAllByUserId(@Param("userId") Integer userId, Pageable pageable);
}
