package nl.ckramer.doorstroombackend.repository;

import nl.ckramer.doorstroombackend.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    Optional<Permission> findByName(String permissionName);

    List<Permission> findByAdminOnlyFalse();
    List<Permission> findAllByAdminOnlyFalseAndDeletedFalse();

    List<Permission> findByAdminOnlyTrue();
    List<Permission> findAllByAdminOnlyTrueAndDeletedFalse();

    List<Permission> findAllByDeletedFalse();

    @Query(value = "SELECT p FROM Permission p JOIN p.roles r WHERE r.id = ?1")
    List<Permission> findByRoleId(Long roleId);

    @Query(value = "SELECT p FROM Permission p WHERE p NOT IN(SELECT pr FROM Permission pr JOIN pr.roles ro WHERE ro.id = ?1 AND pr.deleted = false)")
    List<Permission> findAvailableForRole(Long roleId);

}
