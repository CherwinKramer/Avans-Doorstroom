package nl.ckramer.doorstroombackend.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.ckramer.doorstroombackend.entity.Artist;
import nl.ckramer.doorstroombackend.entity.Song;

import javax.persistence.Persistence;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
public class SongDto {

    public SongDto(Song song) {
        this.id = song.getId();
        this.name = song.getName();
        this.url = song.getUrl();
        if (song.getArtist() != null && Persistence.getPersistenceUtil().isLoaded(song.getArtist())) this.artistDto = new ArtistDto(song.getArtist());
        if (song.getAlbum() != null && Persistence.getPersistenceUtil().isLoaded(song.getAlbum())) this.albumDto = new AlbumDto(song.getAlbum());
        if (song.getGenre() != null && Persistence.getPersistenceUtil().isLoaded(song.getGenre())) this.genreDto = new GenreDto(song.getGenre());
        if (song.getFeaturedArtists() != null && Persistence.getPersistenceUtil().isLoaded(song.getFeaturedArtists())) {
            this.featuredArtistHashSet = song.getFeaturedArtists();
            for (Artist artist : song.getFeaturedArtists()) {
                featuredArtistIds.add(artist.getId());
            }
        }
    }

    private Long id;
    private String name;
    private String url;

    @JsonIgnore
    private transient Set<Artist> featuredArtistHashSet = new HashSet<>();

    @JsonProperty("artist")
    private ArtistDto artistDto = new ArtistDto();

    @JsonProperty("album")
    private AlbumDto albumDto = new AlbumDto();

    @JsonProperty("genre")
    private GenreDto genreDto = new GenreDto();

    @JsonProperty("featuredArtists")
    private List<Long> featuredArtistIds = new ArrayList<>();

    @JsonProperty("featuredArtistNames")
    String getFeaturedArtistNames() {
        String artistNames = "";
        String prefix = "";
        if (featuredArtistHashSet != null && !featuredArtistHashSet.isEmpty()) {
            for (Artist artist : featuredArtistHashSet) {
                artistNames = artistNames + prefix + artist.getName() + " " + artist.getSurname();
                prefix = " and ";
            }
        }
        return artistNames;
    }

}
