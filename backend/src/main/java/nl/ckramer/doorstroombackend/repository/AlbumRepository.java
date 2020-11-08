package nl.ckramer.doorstroombackend.repository;

import nl.ckramer.doorstroombackend.entity.Album;
import nl.ckramer.doorstroombackend.entity.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {

    List<Album> findAllByArtistAndDeletedFalse(Artist artist);

    List<Album> findByDeletedFalse();

}