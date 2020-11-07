package nl.ckramer.doorstroombackend.repository;

import nl.ckramer.doorstroombackend.entity.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {

    List<Artist> findAllByDeletedFalse();
    Optional<Artist> findByName(String name);
    Optional<Artist> findByNameAndDeletedFalse(String name);

}
