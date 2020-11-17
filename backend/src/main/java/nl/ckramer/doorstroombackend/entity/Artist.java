package nl.ckramer.doorstroombackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.ckramer.doorstroombackend.entity.base.Auditable;
import nl.ckramer.doorstroombackend.model.dto.ArtistDto;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Entity
@Table(name = "backend_artist")
@Data
@NoArgsConstructor
public class Artist extends Auditable implements Serializable {

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

    public void setArtistDto(ArtistDto artistDto) {
        this.name = artistDto.getName();
        this.surname = artistDto.getSurname();
        this.place = artistDto.getPlace();
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