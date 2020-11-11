package nl.ckramer.doorstroombackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.ckramer.doorstroombackend.model.request.ArtistRequest;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name = "backend_artist")
@Data
@NoArgsConstructor
public class Artist extends Auditable {

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

    @Column(name = "deleted_yn", columnDefinition = "boolean default false")
    private Boolean deleted = false;

    public void setArtistRequest(ArtistRequest artistRequest) {
        this.name = artistRequest.getName();
        this.surname = artistRequest.getSurname();
        this.place = artistRequest.getPlace();
    }

    @Override
    @JsonIgnore
    public User getUser() {
        return super.getUser();
    }

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