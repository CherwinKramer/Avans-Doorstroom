package nl.ckramer.doorstroombackend.model.request;

import lombok.Data;
import nl.ckramer.doorstroombackend.entity.Role;

@Data
public class UserRequest {

    private Long id;

    private String name;
    private String surname;
    private String email;
    private String password;

    private Role role;

}
