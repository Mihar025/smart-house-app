package com.misha.sh.devicemanagementmicroservice.request.smartOutlet.turnOnRequests;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleRequestOff {
    private String scheduledTimeOff;
}
