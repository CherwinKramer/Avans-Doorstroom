package nl.ckramer.doorstroombackend.service;

import nl.ckramer.doorstroombackend.model.request.UserRequest;
import nl.ckramer.doorstroombackend.model.response.ApiResponse;
import nl.ckramer.doorstroombackend.repository.UserRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public ApiResponse validateUser(UserRequest userRequest) {
        if (StringUtils.isAnyEmpty(userRequest.getName(), userRequest.getSurname(), userRequest.getEmail(), userRequest.getPassword())) {
            return new ApiResponse(false, "The registration request was not valid, please try again.");
        }

        if (EmailValidator.getInstance().isValid(userRequest.getEmail())) {
            if (userRepository.existsByEmail(userRequest.getEmail())) {
                return new ApiResponse(false, "The given email is already in use!");
            } else {
                return new ApiResponse(true);
            }
        } else {
            return new ApiResponse(false, "The given email was not valid, please try again.");
        }
    }

}
