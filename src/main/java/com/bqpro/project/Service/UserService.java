package com.bqpro.project.Service;

import com.bqpro.project.Model.User;
import com.bqpro.project.Repository.UserRepository;
import com.bqpro.project.Request.LoginRequest;
import com.bqpro.project.Response.JwtResponse;
import com.bqpro.project.Security.Jwt.JwtUtils;
import com.bqpro.project.Security.Services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    AuthenticationManager authenticationManager;


    public User create(User user){
        return userRepository.save(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }



    public void delete(Long id){
        userRepository.deleteById(id);
    }

    public Optional<User> findByID(Long id){
        return userRepository.findById(id);
    }

    public Optional<User> getByUsername(String nombreUsuario){
        return userRepository.findByUsername(nombreUsuario);
    }


    public boolean existsByUsername(String nombreUsuario){
        return userRepository.existsByUsername(nombreUsuario);
    }

    public void save(User user){
        userRepository.save(user);
    }


    public JwtResponse generateJwtUserServiceToken(LoginRequest loginRequest){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());
        return new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                roles);
    }

}


