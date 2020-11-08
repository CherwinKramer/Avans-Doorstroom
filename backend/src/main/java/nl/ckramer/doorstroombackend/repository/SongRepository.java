package nl.ckramer.doorstroombackend.repository;

import nl.ckramer.doorstroombackend.entity.Album;
import nl.ckramer.doorstroombackend.entity.Artist;
import nl.ckramer.doorstroombackend.entity.Song;
import nl.ckramer.doorstroombackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SongRepository extends JpaRepository<Song, Long> {

    List<Song> findAllByAlbumAndDeletedFalse(Album album);

    List<Song> findAllByArtistAndDeletedFalse(Artist artist);

    List<Song> findAllByUserAndDeletedFalse(User user);

    List<Album> findByDeletedFalse();

}