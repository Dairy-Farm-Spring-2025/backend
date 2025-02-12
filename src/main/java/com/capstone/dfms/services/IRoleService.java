package com.capstone.dfms.services;

import com.capstone.dfms.models.RoleEntity;

import java.util.List;

public interface IRoleService {
    RoleEntity createRole(String role);

    RoleEntity getRoleById(long id);

    List<RoleEntity> getAllRoles();

    void deleteRole(long id);
}
