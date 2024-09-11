package com.misha.sh.devicemanagementmicroservice.authcontroller;



import com.misha.sh.devicemanagementmicroservice.request.registrationRequests.RegistrationBusinessAccountRequest;
import com.misha.sh.devicemanagementmicroservice.request.registrationRequests.RegistrationRequest;
import com.misha.sh.devicemanagementmicroservice.security.AuthenticationRequest;
import com.misha.sh.devicemanagementmicroservice.security.AuthenticationResponse;
import com.misha.sh.devicemanagementmicroservice.security.AuthenticationService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody @Valid RegistrationRequest request) throws MessagingException {
            authenticationService.registerUser(request);
            return ResponseEntity.ok("User register successfully");
    }


    @PostMapping("/register/business")
    public ResponseEntity<?> registerBusinessAccount(@RequestBody @Valid RegistrationBusinessAccountRequest request) throws MessagingException {
        authenticationService.registerBusinessAccount(request);
        return ResponseEntity.ok("User register successfully");
    }

    @PostMapping("/register/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> registerAdmin(@RequestBody @Valid RegistrationRequest request) throws MessagingException {
        authenticationService.registerAdmin(request);
        return ResponseEntity.ok("User register successfully");
    }


    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody @Valid AuthenticationRequest authenticationRequest
    ){
        return ResponseEntity.ok(authenticationService.authenticate(authenticationRequest));
    }

    @PostMapping("/activate-account")
    public void confirm(
            @RequestParam String token
    ) throws MessagingException {
        authenticationService.activateAccount(token);
    }
    @PostMapping("/logout")
    public ResponseEntity<String> logout (@RequestHeader("Authorization") String token){
        authenticationService.logout(token);
        return ResponseEntity.ok("Logout successfully");
    }

    @GetMapping("/check-business-owner/{userId}")
    public ResponseEntity<Boolean> checkBusinessOwner(@PathVariable Integer userId, Authentication authentication) {
        try {
            boolean isBusinessOwner = authenticationService.checkUserOwnership(userId, authentication);
            return ResponseEntity.ok(isBusinessOwner);
        } catch (AccessDeniedException | java.nio.file.AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(false);
        }
    }





}
