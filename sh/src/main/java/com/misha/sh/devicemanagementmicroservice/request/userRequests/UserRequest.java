package com.misha.sh.devicemanagementmicroservice.request.userRequests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@Builder
public class UserRequest {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @Past(message = "Date of birth must be in the past")
    private LocalDateTime dateOfBirth;

    @Email(message = "Invalid email format")
    private String email;

    private String street;

    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;




}
