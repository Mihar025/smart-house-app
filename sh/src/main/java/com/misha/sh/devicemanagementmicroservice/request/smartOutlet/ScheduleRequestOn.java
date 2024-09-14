package com.misha.sh.devicemanagementmicroservice.request.smartOutlet;


import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleRequestOn {
    private String scheduledTimeOn;

}
