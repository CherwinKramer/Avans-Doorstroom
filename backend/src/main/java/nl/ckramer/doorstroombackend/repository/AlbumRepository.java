package nl.ckramer.doorstroombackend.repository;

import nl.ckramer.doorstroombackend.entity.Album;
import nl.ckramer.doorstroombackend.entity.Artist;
import nl.ckramer.doorstroombackend.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {

    @EntityGraph(value = "album-get-all")
    List<Album> findAllByArtistAndDeletedFalse(Artist artist);

    @EntityGraph(value = "album-view")
    List<Album> findAllByUserAndDeletedFalse(User user);

    @EntityGraph(value = "album-view")
    Optional<Album> findByIdAndUser(Long id, User user);

}