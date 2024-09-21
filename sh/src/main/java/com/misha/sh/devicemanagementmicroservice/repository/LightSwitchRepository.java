package com.misha.sh.devicemanagementmicroservice.repository;

import com.misha.sh.devicemanagementmicroservice.model.swtichLight.LightSwitch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LightSwitchRepository extends JpaRepository<LightSwitch, Integer> {

    @Query(
            """
            SELECT o FROM LightSwitch o
            WHERE o.user.id= :userId
        
    """
    )
    Page<LightSwitch> findAllLightSwitchesByUser(Pageable pageable,@Param("userId") Integer userId);
}
