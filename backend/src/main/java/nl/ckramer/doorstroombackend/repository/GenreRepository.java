package nl.ckramer.doorstroombackend.repository;

import nl.ckramer.doorstroombackend.entity.Genre;
import nl.ckramer.doorstroombackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {

    Optional<Genre> findByIdAndUser(Long id, User user);

    List<Genre> findAllByUserAndDeletedFalse(User user);

}