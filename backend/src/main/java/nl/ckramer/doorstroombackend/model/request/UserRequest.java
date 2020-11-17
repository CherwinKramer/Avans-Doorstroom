package nl.ckramer.doorstroombackend.model.request;

import lombok.Data;

@Data
public class UserRequest {

    private Long id;

    private String name;
    private String surname;
    private String email;
    private String password;

}
