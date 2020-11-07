package nl.ckramer.doorstroombackend.repository;

import nl.ckramer.doorstroombackend.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String roleName);
    Optional<Role> findByStandardTrue();
    List<Role> findAllByDeletedFalse();
}
