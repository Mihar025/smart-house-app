package com.misha.sh.devicemanagementmicroservice.request.userRequests;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private String errorMsg;
    private String firstName;
    private String lastName;
    private LocalDateTime dateOfBirth;
    private String email;
    private String street;
    private List<String> allBusinesses;

    public UserResponse(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
