package nl.ckramer.doorstroombackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Objects;

@Entity
@Table(name = "auth_user", uniqueConstraints = {
        @UniqueConstraint(columnNames = {
                "email"
        })
})
@Data
@NoArgsConstructor
public class User extends BaseEntity {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "name")
    @Size(max = 40)
    private String name;

    @NotBlank
    @Column(name = "surname")
    @Size(max = 40)
    private String surname;

    @NaturalId
    @NotBlank
    @Column(name = "email")
    @Size(max = 40)
    @Email
    private String email;

    @NotBlank
    @Column(name = "password")
    @Size(max = 100)
    @JsonIgnore
    private String password;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @Column(name = "deleted_yn", columnDefinition = "boolean default false")
    private Boolean deleted = false;

    public User(String name, String surname, String email, String password) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return this.getName() + " " + this.getSurname();
    }
}