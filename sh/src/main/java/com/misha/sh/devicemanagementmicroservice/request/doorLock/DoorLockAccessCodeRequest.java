package com.misha.sh.devicemanagementmicroservice.request.doorLock;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DoorLockAccessCodeRequest {

    private Integer accessCode;
    private String fingerPrintCode;



}
