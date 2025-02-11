package com.capstone.dfms.controllers;

import com.capstone.dfms.components.apis.CoreApiResponse;
import com.capstone.dfms.models.CategoryEntity;
import com.capstone.dfms.models.RoleEntity;
import com.capstone.dfms.requests.CategoryRequest;
import com.capstone.dfms.requests.RoleRequest;
import com.capstone.dfms.services.IRoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${app.api.version.v1}/roles")
@RequiredArgsConstructor
public class RoleController {
    private final IRoleService roleService;

    @PostMapping("/create")
    public CoreApiResponse<?> createRole(
            @Valid @RequestBody RoleRequest request
    ){
        roleService.createRole(request.getRoleName());
        return CoreApiResponse.success("Create role successfully.");
    }

    @GetMapping
    public CoreApiResponse<List<RoleEntity>> getAll() {
        return CoreApiResponse.success(roleService.getAllRoles());
    }

    @GetMapping("/{id}")
    public CoreApiResponse<RoleEntity> getById(@PathVariable Long id) {
        return CoreApiResponse.success(roleService.getRoleById(id));
    }

    @DeleteMapping("/{id}")
    public CoreApiResponse<?> deleteCategory(
            @PathVariable Long id
    ){
        roleService.deleteRole(id);
        return CoreApiResponse.success("Delete role successfully");
    }

}
