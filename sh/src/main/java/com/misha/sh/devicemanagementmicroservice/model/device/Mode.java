package com.misha.sh.devicemanagementmicroservice.model.device;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_mode")
public  class Mode {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;


    private String lowEnergyConsumingMode;
    private String defaultMode;
    private String highEnergyConsumingMode;
    private boolean isTurnedOn;
    private LocalDateTime activateFrom;
    private LocalDateTime activateTo;

    @OneToMany(mappedBy = "mode")
    private List<Device> devices;


    private double electricityConsuming;
    private double voltage;
    private int amps;

    private LocalDateTime time;
    private LocalDateTime startTime;
    private LocalDateTime finisTime;




}
