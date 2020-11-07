package nl.ckramer.doorstroombackend.service;

import nl.ckramer.doorstroombackend.entity.Role;
import nl.ckramer.doorstroombackend.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public Role getDefaultRole() {
        Optional<Role> defaultRoleOptional = roleRepository.findByStandardTrue();
        Role defaultRole = null;
        if (defaultRoleOptional.isPresent()) {
            return defaultRoleOptional.get();
        }
        defaultRole = new Role(Role.USER, Role.USER);
        defaultRole.setStandard(true);
        return roleRepository.save(defaultRole);
    }

}
