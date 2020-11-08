package nl.ckramer.doorstroombackend.controller;

import nl.ckramer.doorstroombackend.entity.User;
import nl.ckramer.doorstroombackend.repository.UserRepository;
import nl.ckramer.doorstroombackend.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public abstract class AbstractController {

    @Autowired
    protected UserRepository userRepository;

    protected User findUserByUserPrincipal(UserPrincipal userPrincipal) {
        if (userPrincipal != null && userPrincipal.getId() != null) {
            Optional<User> userOptional = userRepository.findById(userPrincipal.getId());
            if (userOptional.isPresent()) {
                return userOptional.get();
            }
        }
        return null;
    }

}
