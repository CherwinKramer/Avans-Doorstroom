package nl.ckramer.doorstroombackend.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {

    private String accessToken;
    private String email;
    private String name;
    private String surname;
    private Long userId;

}