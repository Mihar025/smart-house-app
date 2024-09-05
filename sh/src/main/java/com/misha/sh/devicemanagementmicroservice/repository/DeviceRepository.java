package com.misha.sh.devicemanagementmicroservice.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.misha.sh.devicemanagementmicroservice.model.device.Device;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Integer> {




}
