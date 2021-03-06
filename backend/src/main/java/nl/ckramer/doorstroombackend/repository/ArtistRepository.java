package nl.ckramer.doorstroombackend.repository;

import nl.ckramer.doorstroombackend.entity.Artist;
import nl.ckramer.doorstroombackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {

    Optional<Artist> findByIdAndUser(Long id, User user);

    List<Artist> findAllByDeletedFalseAndUser(User user);

}
