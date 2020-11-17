package nl.ckramer.doorstroombackend.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import nl.ckramer.doorstroombackend.entity.Genre;

@Data
@NoArgsConstructor
public class GenreDto {

    public GenreDto(Genre genre) {
        this.id = genre.getId();
        this.name = genre.getName();
    }

    private Long id;
    private String name;

}
