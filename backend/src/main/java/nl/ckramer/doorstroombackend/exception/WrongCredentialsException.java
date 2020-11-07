package nl.ckramer.doorstroombackend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.NOT_FOUND)
public class WrongCredentialsException extends AuthenticationException {
    public WrongCredentialsException(String msg) {
        super(msg);
    }

    public WrongCredentialsException(String msg, Throwable t) {
        super(msg, t);
    }
}