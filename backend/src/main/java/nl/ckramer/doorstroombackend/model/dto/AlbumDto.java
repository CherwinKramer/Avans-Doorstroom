package nl.ckramer.doorstroombackend.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.ckramer.doorstroombackend.entity.Album;
import nl.ckramer.doorstroombackend.entity.Artist;
import nl.ckramer.doorstroombackend.entity.Song;

import javax.persistence.Persistence;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class AlbumDto {

    public AlbumDto(Album album) {
        this.id = album.getId();
        this.name = album.getName();
        if (album.getArtist() != null && Persistence.getPersistenceUtil().isLoaded(album.getArtist())) this.artistDto = new ArtistDto(album.getArtist());
        if (album.getSongs() != null && Persistence.getPersistenceUtil().isLoaded(album.getSongs())) {
            for (Song song : album.getSongs()) {
                songDtoList.add(new SongDto(song));
            }
        }
    }

    private Long id;
    private String name;

    @JsonProperty("artist")
    private ArtistDto artistDto = new ArtistDto();

    @JsonProperty("songs")
    private List<SongDto> songDtoList = new ArrayList<>();

}
