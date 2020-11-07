package nl.ckramer.doorstroombackend.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name = "backend_song")
@Data
@NoArgsConstructor
public class Song extends BaseEntity {

    @Id
    @Column(name = "song_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "name")
    @Size(max = 30)
    private String name;

    @ManyToOne
    @JoinColumn(name = "artist_id")
    private Artist artist;

    @ManyToOne
    @JoinColumn(name = "album_id")
    private Album album;

    @Column(name = "deleted_yn", columnDefinition = "boolean default false")
    private Boolean deleted = false;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Song) {
            Song other = (Song) obj;
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