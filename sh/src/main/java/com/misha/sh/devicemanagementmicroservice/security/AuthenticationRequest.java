package com.misha.sh.devicemanagementmicroservice.security;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthenticationRequest {

    @Email(message = "EmailRequest is not well formatted! \n Example user123@mail.com")
    @NotEmpty(message = "EmailRequest field cannot be empty")
    @NotNull(message = "EmailRequest is mandatory")
    private String email;
    @NotEmpty(message = "Password field cannot be empty")
    @NotNull(message = "Password cannot be null")
    @Size(min = 8, message = "Password should be minimum 8 long characters")
    private String password;


}
