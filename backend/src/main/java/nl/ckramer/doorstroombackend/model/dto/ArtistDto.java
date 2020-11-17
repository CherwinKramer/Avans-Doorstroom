package nl.ckramer.doorstroombackend.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.ckramer.doorstroombackend.entity.Artist;

@Data
@NoArgsConstructor
public class ArtistDto {

    public ArtistDto(Artist artist) {
        this.id = artist.getId();
        this.name = artist.getName();
        this.surname = artist.getSurname();
        this.place = artist.getPlace();
    }

    private Long id;
    private String name;
    private String surname;
    private String place;

    @JsonProperty("fullName")
    String getFullName() {
        return name + " " + surname;
    }
}
