package nl.ckramer.doorstroombackend.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "auth_role")
@NoArgsConstructor
@Data
@EqualsAndHashCode
public class Role {

    public final static String USER = "User";
    public final static String ADMIN = "Admin";

    public Role(String name, String description){
        this.name = name;
        this.description = description;
    }

    @Id
    @Column(name = "role_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "auth_role_permission",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id"))
    private Set<Permission> permissions = new HashSet<>();

    @Column(name = "name", length = 60)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "standard_yn", columnDefinition = "boolean default false")
    private Boolean standard = false;

    @Column(name = "deleted_yn", columnDefinition = "boolean default false")
    private Boolean deleted = false;

    @Override
    public int hashCode() {
        HashCodeBuilder b = new HashCodeBuilder();
        b.append(getId());
        b.append(getName());
        b.append(getDescription());
        return b.toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Permission) {
            Role other = (Role) obj;
            EqualsBuilder e = new EqualsBuilder();
            e.append(getId(), other.getId());
            e.append(getName(), other.getName());
            e.append(getDescription(), other.getDescription());
            return e.isEquals();
        }
        return false;
    }

}