package nl.ckramer.doorstroombackend.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "auth_permission")
@NoArgsConstructor
@Data
public class Permission {

    @Id
    @Column(name = "permission_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 50)
    private String name;

    @Column(name = "description", length = 250)
    private String description;

    @Column(name = "adminOnly", columnDefinition = "boolean default false")
    private Boolean adminOnly = false;

    @Column(name = "deleted_yn", columnDefinition = "boolean default false")
    private Boolean deleted = false;

    @ManyToMany(mappedBy = "permissions", fetch = FetchType.LAZY)
    Set<Role> roles = new HashSet<>();

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
            Permission other = (Permission) obj;
            EqualsBuilder e = new EqualsBuilder();
            e.append(getId(), other.getId());
            e.append(getName(), other.getName());
            e.append(getDescription(), other.getDescription());
            return e.isEquals();
        }
        return false;
    }
}