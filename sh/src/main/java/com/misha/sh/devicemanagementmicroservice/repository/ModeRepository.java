package com.misha.sh.devicemanagementmicroservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.misha.sh.devicemanagementmicroservice.model.device.Mode;

import javax.swing.text.html.Option;
import java.util.Optional;

@Repository
public interface ModeRepository extends JpaRepository<Mode, Integer> {

    @Query(
            """
            SELECT m
            FROM Mode m
            WHERE :lowEnergyConsumingMode IS NULL AND m.lowEnergyConsumingMode IS NULL
                OR LOWER(m.lowEnergyConsumingMode) = LOWER(:lowEnergyConsumingMode)
            """
    )
    Optional<Mode> findModeByLowEnergyConsumingMode (@Param("lowEnergyConsumingMode") String lowEnergyConsumingMode);

    @Query(
            """
        SELECT m
        FROM Mode m
        WHERE :highEnergyConsumingMode IS NULL AND m.highEnergyConsumingMode IS NULL
            OR LOWER(m.highEnergyConsumingMode) = LOWER(:highEnergyConsumingMode)
    """
    )
    Optional<Mode> findModeByHighEnergyConsumingMode (@Param("highEnergyConsumingMode") String highEnergyConsumingMode);

    @Query("""
    SELECT m
    FROM Mode m
    WHERE (:defaultMode IS NULL AND m.defaultMode IS NULL)
       OR LOWER(m.defaultMode) = LOWER(:defaultMode)
""")
    Optional<Mode> findModeByDefaultEnergyConsumingMode(@Param("defaultMode") String defaultMode);
}
