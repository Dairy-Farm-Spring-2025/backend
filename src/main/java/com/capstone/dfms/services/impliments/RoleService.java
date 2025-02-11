package com.capstone.dfms.services.impliments;

import com.capstone.dfms.components.exceptions.AppException;
import com.capstone.dfms.components.exceptions.DataNotFoundException;
import com.capstone.dfms.components.utils.StringUtils;
import com.capstone.dfms.models.CategoryEntity;
import com.capstone.dfms.models.RoleEntity;
import com.capstone.dfms.repositories.IRoleRepository;
import com.capstone.dfms.repositories.IUserRepository;
import com.capstone.dfms.services.IRoleService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@AllArgsConstructor
public class RoleService implements IRoleService {
    private final IRoleRepository roleRepository;

    private final IUserRepository userRepository;


    @Override
    public RoleEntity createRole(String role ) {
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setName(StringUtils.NameStandardlizing(role));
        return roleRepository.save(roleEntity);
    }

    @Override
    public RoleEntity getRoleById(long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "This role is not existed!"));
    }

    @Override
    public List<RoleEntity> getAllRoles() {
        return roleRepository.findAll();
    }



    @Override
    public void deleteRole(long id) {
        RoleEntity roleEntity = roleRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Role", "id", id));
        boolean hasUsers = userRepository.existsByRoleId(roleEntity);
        if (hasUsers) {
            throw new AppException(HttpStatus.BAD_REQUEST,"Cannot delete role because it is assigned to one or more users.");
        }
        roleRepository.delete(roleEntity);
    }
}
