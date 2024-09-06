package com.misha.sh.devicemanagementmicroservice.repository;

import com.misha.sh.devicemanagementmicroservice.model.thermostat.Thermostat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThermostatRepository extends JpaRepository<Thermostat, Integer> {
}
