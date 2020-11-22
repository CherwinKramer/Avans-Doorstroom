package nl.ckramer.doorstroombackend.controller;

import nl.ckramer.doorstroombackend.entity.User;
import nl.ckramer.doorstroombackend.model.request.UserRequest;
import nl.ckramer.doorstroombackend.model.response.ApiResponse;
import nl.ckramer.doorstroombackend.model.response.JwtAuthenticationResponse;
import nl.ckramer.doorstroombackend.model.response.LoginResponse;
import nl.ckramer.doorstroombackend.repository.UserRepository;
import nl.ckramer.doorstroombackend.security.CurrentUser;
import nl.ckramer.doorstroombackend.security.JwtTokenProvider;
import nl.ckramer.doorstroombackend.security.UserPrincipal;
import nl.ckramer.doorstroombackend.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.net.URI;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtTokenProvider tokenProvider;

    @Autowired
    UserService userService;

    @GetMapping("/me")
    public ResponseEntity<?> showDetails(@CurrentUser UserPrincipal userPrincipal) {
        Optional<User> userOptional = userRepository.findById(userPrincipal.getId());
        if (!userOptional.isPresent()) {
            return new ResponseEntity<>(new ApiResponse(false, "Couldn't authenticate the logged in user"), HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(new ApiResponse(true, userOptional.get()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody Map<String, Object> body) {
        String email = body.getOrDefault("email", "").toString();
        String password = body.getOrDefault("password", "").toString();

        if(StringUtils.isAnyEmpty(email, password)) {
            return new ResponseEntity<>(new ApiResponse(false, "The registration request was not valid, please try again."), HttpStatus.BAD_REQUEST);
        }

        LoginResponse loginResponse = generateJWTAuthenticationResponse(email, password);
        if (loginResponse == null) {
            return new ResponseEntity<>(new ApiResponse(false, "Wrong credentials!"), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new ApiResponse(true, loginResponse), HttpStatus.OK);
    }

    @PostMapping("/register")
    @Transactional
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRequest userRequest) {

        ApiResponse apiResponse = userService.validateUser(userRequest);
        if (!apiResponse.getSuccess()) {
            return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
        }

        // Creating user's account
        User user = new User(userRequest.getName(), userRequest.getSurname(), userRequest.getEmail(), passwordEncoder.encode(userRequest.getPassword()));
        user = userRepository.save(user);

        URI URILocation = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/users/{email}")
                .buildAndExpand(user.getEmail()).toUri();


        LoginResponse loginResponse = generateJWTAuthenticationResponse(userRequest.getEmail(), userRequest.getPassword());
        return ResponseEntity.created(URILocation).body(new ApiResponse(true, "User registered successfully", loginResponse));
    }

    private LoginResponse generateJWTAuthenticationResponse(String email, String password) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        } catch (AuthenticationException e) {
            return null;
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String jwt = tokenProvider.generateToken(authentication);
        return new LoginResponse(new JwtAuthenticationResponse(jwt).getAccessToken(), userPrincipal.getEmail(), userPrincipal.getName(), userPrincipal.getSurname(), userPrincipal.getId());
    }

}
