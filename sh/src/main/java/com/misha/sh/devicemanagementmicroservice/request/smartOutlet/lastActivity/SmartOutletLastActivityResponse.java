package com.misha.sh.devicemanagementmicroservice.request.smartOutlet.lastActivity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SmartOutletLastActivityResponse {
    private Integer outletId;
    private LocalDateTime lastOnTime;
    private LocalDateTime lastOffTime;
    private boolean isCurrentlyOn;
}