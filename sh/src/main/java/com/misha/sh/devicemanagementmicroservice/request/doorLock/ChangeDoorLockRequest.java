package com.misha.sh.devicemanagementmicroservice.request.doorLock;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChangeDoorLockRequest {
    @NotNull(message = "Access code is required")
    @Digits(integer = 6, fraction = 0, message = "Access code should contain only digits")
    @Min(value = 1000, message = "Access code should be at least 4 digits")
    @Max(value = 999999, message = "Access code should not exceed 6 digits")
    private Integer doorCode;
}