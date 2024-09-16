package com.misha.sh.devicemanagementmicroservice.request.smartOutlet.scheduling;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SmartOutletScheduleResponse {
    private Integer outletId;
    private LocalDateTime scheduledOn;
    private LocalDateTime scheduledOff;

}
