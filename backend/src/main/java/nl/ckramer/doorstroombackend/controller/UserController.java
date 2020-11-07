package nl.ckramer.doorstroombackend.controller;

import nl.ckramer.doorstroombackend.entity.Role;
import nl.ckramer.doorstroombackend.entity.User;
import nl.ckramer.doorstroombackend.model.request.UserRequest;
import nl.ckramer.doorstroombackend.model.response.ApiResponse;
import nl.ckramer.doorstroombackend.model.response.JwtAuthenticationResponse;
import nl.ckramer.doorstroombackend.model.response.LoginResponse;
import nl.ckramer.doorstroombackend.repository.RoleRepository;
import nl.ckramer.doorstroombackend.repository.UserRepository;
import nl.ckramer.doorstroombackend.security.JwtTokenProvider;
import nl.ckramer.doorstroombackend.security.UserPrincipal;
import nl.ckramer.doorstroombackend.service.RoleService;
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
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    RoleService roleService;

    @Autowired
    UserService userService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtTokenProvider tokenProvider;

    @PostMapping("/create")
    @Transactional
    public ResponseEntity<?> createUser(@Valid @RequestBody UserRequest userRequest) {

        ApiResponse apiResponse = userService.validateUser(userRequest);
        if (!apiResponse.getSuccess()) {
            return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
        }

        // Creating user's account
        User user = new User(userRequest.getName(), userRequest.getSurname(), userRequest.getEmail(), passwordEncoder.encode(userRequest.getPassword()));
        if (userRequest.getRole() != null) {
            user.setRole(userRequest.getRole());
        } else {
            Role defaultRole = roleService.getDefaultRole();
            user.setRole(defaultRole);
        }

        user = userRepository.save(user);

        URI URILocation = ServletUriComponentsBuilder.fromCurrentContextPath().path("/users/{email}").buildAndExpand(user.getEmail()).toUri();
        return ResponseEntity.created(URILocation).body(new ApiResponse(true, "User registered successfully", user));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        if (id != null) {
            Optional<User> userOptional = userRepository.findById(id);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                if (user.getDeleted()) {
                    return new ResponseEntity<>(new ApiResponse(false, "The user has already been deleted!"), HttpStatus.BAD_REQUEST);
                }
                user.setDeleted(true);
                userRepository.save(user);
                return new ResponseEntity<>(new ApiResponse(true, "User has been deleted succesfully!", user), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(new ApiResponse(false, "Could not delete the user!"), HttpStatus.BAD_REQUEST);
    }
}
