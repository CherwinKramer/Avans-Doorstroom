package nl.ckramer.doorstroombackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.ckramer.doorstroombackend.entity.base.Auditable;
import nl.ckramer.doorstroombackend.model.dto.GenreDto;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Entity
@Table(name = "backend_genre")
@Data
@NoArgsConstructor
public class Genre extends Auditable implements Serializable {

    @Id
    @Column(name = "genre_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "name")
    @Size(max = 30)
    private String name;

    @Column(name = "deleted_yn", columnDefinition = "boolean default false")
    private Boolean deleted = false;

    public void setGenreDto(GenreDto genreDto) {
        this.name = genreDto.getName();
    }

    @Override
    @JsonIgnore
    public User getUser() {
        return super.getUser();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Genre) {
            Genre other = (Genre) obj;
            EqualsBuilder e = new EqualsBuilder();
            e.append(getId(), other.getId());
            e.append(getName(), other.getName());
            return e.isEquals();
        }
        return false;
    }

    @Override
    public int hashCode() {
        HashCodeBuilder b = new HashCodeBuilder();
        b.append(getId());
        b.append(getName());
        return b.toHashCode();
    }

    @Override
    public String toString() {
        return this.getName();
    }
}