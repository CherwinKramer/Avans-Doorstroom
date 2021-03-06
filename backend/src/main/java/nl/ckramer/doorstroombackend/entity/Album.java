package nl.ckramer.doorstroombackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.ckramer.doorstroombackend.entity.base.Auditable;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

@NamedEntityGraph(name = "album-get-all",
        attributeNodes = {
                @NamedAttributeNode("artist"), @NamedAttributeNode("songs")
        })

@NamedEntityGraph(name = "album-view",
        attributeNodes = {
                @NamedAttributeNode("artist")
        })

@Entity
@Table(name = "backend_album")
@Data
@NoArgsConstructor
public class Album extends Auditable implements Serializable {

    @Id
    @Column(name = "album_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "name")
    @Size(max = 30)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id")
    private Artist artist;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "album")
    private List<Song> songs;

    @Column(name = "deleted_yn", columnDefinition = "boolean default false")
    private Boolean deleted = false;

    @Override
    @JsonIgnore
    public User getUser() {
        return super.getUser();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Album) {
            Album other = (Album) obj;
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