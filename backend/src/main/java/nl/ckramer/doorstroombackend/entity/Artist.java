package nl.ckramer.doorstroombackend.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.annotation.CreatedBy;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Objects;

@Entity
@Table(name = "backend_artist")
@Data
@NoArgsConstructor
public class Artist extends BaseEntity {

    @Id
    @Column(name = "artist_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "name")
    @Size(max = 30)
    private String name;

    @NotBlank
    @Column(name = "surname")
    @Size(max = 30)
    private String surname;

    @Column(name = "place")
    @Size(max = 50)
    private String place;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @CreatedBy
    private User user;

    @Column(name = "deleted_yn", columnDefinition = "boolean default false")
    private Boolean deleted = false;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Artist) {
            Artist other = (Artist) obj;
            EqualsBuilder e = new EqualsBuilder();
            e.append(getId(), other.getId());
            e.append(getName(), other.getName());
            e.append(getSurname(), other.getSurname());
            return e.isEquals();
        }
        return false;
    }

    @Override
    public int hashCode() {
        HashCodeBuilder b = new HashCodeBuilder();
        b.append(getId());
        b.append(getName());
        b.append(getSurname());
        return b.toHashCode();
    }

    @Override
    public String toString() {
        return this.getName() + " " + this.getSurname();
    }
}