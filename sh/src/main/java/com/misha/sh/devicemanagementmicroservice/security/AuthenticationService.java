package com.misha.sh.devicemanagementmicroservice.security;



import com.misha.sh.devicemanagementmicroservice.exception.EmailorPasswordAlreadyExistException;
import com.misha.sh.devicemanagementmicroservice.model.user.User;
import com.misha.sh.devicemanagementmicroservice.model.user.UserRoles;
import com.misha.sh.devicemanagementmicroservice.request.registrationRequests.RegistrationBusinessAccountRequest;
import com.misha.sh.devicemanagementmicroservice.request.registrationRequests.RegistrationRequest;
import com.misha.sh.devicemanagementmicroservice.repository.RoleRepository;
import com.misha.sh.devicemanagementmicroservice.repository.TokenRepository;
import com.misha.sh.devicemanagementmicroservice.repository.UserRepository;
import com.misha.sh.devicemanagementmicroservice.role.Role;
import com.misha.sh.devicemanagementmicroservice.service.EmailService;
import com.misha.sh.devicemanagementmicroservice.service.EmailTemplateName;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;


@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;


    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;



    public boolean checkUserOwnership(Integer userId, Authentication authentication) throws AccessDeniedException {
        User user = (User) authentication.getPrincipal();
        if (!user.getId().equals(userId)) {
            throw new AccessDeniedException("User with id " + userId + " is not the authenticated user");
        }
        return (user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("BUSINESS_OWNER")));
    }



    public void register(RegistrationRequest request, UserRoles userRoles ) throws MessagingException {
        if(userRepository.findByEmail(request.getEmail()).isPresent()){
            throw new EmailorPasswordAlreadyExistException("User with email: " +  request.getEmail() + " already exist");
        }
        Role userRole = roleRepository.findByName(userRoles.name())
                .orElseThrow(() -> new IllegalStateException("ROLE"+ userRoles.name()+  " was not initiated"));

        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .accountLocked(false)
                .enabled(false)
                .roles(Collections.singletonList(userRole))
                .build();

            userRepository.save(user);
            sendValidationEmail(user);

    }



    public void registerBusiness(RegistrationBusinessAccountRequest request, UserRoles userRoles ) throws MessagingException {
        if(userRepository.findByEmail(request.getEmail()).isPresent()){
            throw new EmailorPasswordAlreadyExistException("User with email: " +  request.getEmail() + " already exist");
        }
        Role userRole = roleRepository.findByName(userRoles.name())
                .orElseThrow(() -> new IllegalStateException("ROLE"+ userRoles.name()+  " was not initiated"));

        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .businessType(request.getBusinessType())
                .accountLocked(false)
                .enabled(false)
                .roles(Collections.singletonList(userRole))
                .build();

            userRepository.save(user);
            sendValidationEmail(user);
    }


    private String generateAndSaveActivationToken(User user) {
        String generatedToken = generateActivationCode(6);
        var token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();
        tokenRepository.save(token);
        return generatedToken;
    }

    private void sendValidationEmail(User user) throws MessagingException {
        var newToken = generateAndSaveActivationToken(user);
        emailService.sendEmail(
                user.getEmail(),
                user.getFullName(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                activationUrl,
                newToken,
                "Account activation"
        );
    }

    private String generateActivationCode(int length) {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();

        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        }

        return codeBuilder.toString();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest){
        try {
            var auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authenticationRequest.getEmail(),
                            authenticationRequest.getPassword()
                    )

            );
            var claims = new HashMap<String, Object>();
            var user = ((User) auth.getPrincipal());
            claims.put("fullName", user.getFullName());
            claims.put("userId", user.getId());
            var jwtToken = jwtService.generateToken(claims, user);
            saveToken(user, jwtToken);
            return AuthenticationResponse.builder()
                    .token(jwtToken).build();
        } catch (DisabledException e){
            throw new AccountDissabledException("Account is disabled");
        }
    }

    private void saveToken(User user,String jwt){
        var token = Token.builder()
                .token(jwt)
                .user(user)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .build();
        tokenRepository.save(token);
    }


    // @Transactional
    public void activateAccount(String token) throws MessagingException {
        Token savedToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));
        if(LocalDateTime.now().isAfter(savedToken.getExpiresAt())){
            sendValidationEmail(savedToken.getUser());
            throw new RuntimeException("Activation token has expired. A new token has been send to the same email address");
        }
        var user = userRepository.findById(savedToken.getUser().getId())

                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setEnabled(true);
        userRepository.save(user);
        savedToken.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(savedToken);
    }


    public void registerUser(RegistrationRequest request) throws MessagingException {
        register(request, UserRoles.USER);
    }

    public void registerBusinessAccount(RegistrationBusinessAccountRequest request) throws MessagingException {
        registerBusiness(request, UserRoles.BUSINESS_OWNER);
    }


    public void registerAdmin(RegistrationRequest request) throws MessagingException {
        register(request, UserRoles.ADMIN);
    }

    public void logout(String token) {
        String tokenWithoutBearer = token.substring(7);
        Token token1 = tokenRepository.findByToken(tokenWithoutBearer)
                .orElseThrow(() -> new RuntimeException("Invalid token"));
        token1.setExpiresAt(LocalDateTime.now());
        tokenRepository.save(token1);
    }
}