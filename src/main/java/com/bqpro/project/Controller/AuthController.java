package com.bqpro.project.Controller;

import com.bqpro.project.Enums.ERole;
import com.bqpro.project.Model.Role;
import com.bqpro.project.Model.User;
import com.bqpro.project.Repository.RoleRepository;
import com.bqpro.project.Repository.UserRepository;
import com.bqpro.project.Request.LoginRequest;
import com.bqpro.project.Request.SignupRequest;
import com.bqpro.project.Response.JwtResponse;
import com.bqpro.project.Response.MessageResponse;
import com.bqpro.project.Security.Jwt.JwtUtils;
import com.bqpro.project.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;

@CrossOrigin
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final String ROLE_NOT_FOUND_ERROR = "Error: Role is not found.";

    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder encoder;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    UserService userService;
    @Autowired
    JwtUtils jwtUtils;


    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        JwtResponse jwtUserServiceToken = userService.generateJwtUserServiceToken(loginRequest);
        return ResponseEntity.ok(jwtUserServiceToken);
    }


    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {

        if (signUpRequest.getFirstname().isEmpty()||signUpRequest.getLastname().isEmpty()||signUpRequest.getUsername().isEmpty()||signUpRequest.getPassword().isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: All Fields are required!"));
        }
        if (!isValidName(signUpRequest.getFirstname())||!isValidName(signUpRequest.getLastname())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Firstname or Secondname contains stranger characters !"));
        }
        if (userRepository.existsByUsername(signUpRequest.getUsername()).booleanValue()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }



        User user = new User(signUpRequest.getFirstname(), signUpRequest.getLastname(), signUpRequest.getUsername(), encoder.encode(signUpRequest.getPassword()));
        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();


        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException(ROLE_NOT_FOUND_ERROR));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                if ("admin".equals(role)) {
                    Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                            .orElseThrow(() -> new RuntimeException(ROLE_NOT_FOUND_ERROR));
                    roles.add(adminRole);
                } else {
                    Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                            .orElseThrow(() -> new RuntimeException(ROLE_NOT_FOUND_ERROR));
                    roles.add(userRole);
                }
            });
        }



        user.setRoles(roles);
        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    private boolean isValidName(String name) {
        return name.matches("^[a-zA-Z ]+$");
    }
    }

