package com.capstone.dfms.services.impliments;

import com.capstone.dfms.components.exceptions.AppException;
import com.capstone.dfms.components.utils.LocalizationUtils;
import com.capstone.dfms.models.RoleEntity;
import com.capstone.dfms.models.TaskTypeEntity;
import com.capstone.dfms.repositories.IRoleRepository;
import com.capstone.dfms.repositories.ITaskTypeRepository;
import com.capstone.dfms.services.ITaskTypeService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TaskTypeService implements ITaskTypeService {
    private final ITaskTypeRepository taskTypeRepository;
    private final IRoleRepository roleRepository;

    @Override
    public TaskTypeEntity createTaskType(TaskTypeEntity taskType ) {
        RoleEntity role = roleRepository.findById(taskType.getRoleId().getId()).orElseThrow(()
                -> new AppException(HttpStatus.NOT_FOUND,
                LocalizationUtils.getMessage("user.login.role_not_exist")));
        if (taskTypeRepository.existsByName(taskType.getName())) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Tên loại công việc đã tồn tại.");
        }
        taskType.setRoleId(role);
        return taskTypeRepository.save(taskType);
    }

    @Override
    public TaskTypeEntity getTaskTypeById(long id) {
        return taskTypeRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "This role is not existed!"));
    }

    @Override
    public List<TaskTypeEntity> getAllTaskTypes() {
        return taskTypeRepository.findAll();
    }



    @Override
    public void deleteTaskType(long id) {
        TaskTypeEntity taskType = taskTypeRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST,"Task type is not exist"));

        taskTypeRepository.delete(taskType);
    }
}
