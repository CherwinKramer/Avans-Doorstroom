package nl.ckramer.doorstroombackend.repository;

import nl.ckramer.doorstroombackend.entity.Album;
import nl.ckramer.doorstroombackend.entity.Artist;
import nl.ckramer.doorstroombackend.entity.Song;
import nl.ckramer.doorstroombackend.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SongRepository extends JpaRepository<Song, Long> {

    @EntityGraph(value = "song-get-all")
    Optional<Song> findByIdAndUser(Long id, User user);

    @EntityGraph(value = "song-get-all")
    List<Song> findAllByAlbumAndDeletedFalse(Album album);

    @EntityGraph(value = "song-get-all")
    List<Song> findAllByArtistAndDeletedFalse(Artist artist);

    @EntityGraph(value = "song-get-all")
    List<Song> findAllByUserAndDeletedFalse(User user);

    @EntityGraph(value = "artist-delete")
    List<Song> findAllByFeaturedArtistsContains(Artist artist);

}