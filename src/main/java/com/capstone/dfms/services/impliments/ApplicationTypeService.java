package com.capstone.dfms.services.impliments;

import com.capstone.dfms.components.exceptions.AppException;
import com.capstone.dfms.components.utils.StringUtils;
import com.capstone.dfms.mappers.IApplicationTypeMapper;
import com.capstone.dfms.models.ApplicationTypeEntity;
import com.capstone.dfms.repositories.IApplicationTypeRepository;
import com.capstone.dfms.requests.ApplicationTypeRequest;
import com.capstone.dfms.services.IApplicationTypeService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ApplicationTypeService implements IApplicationTypeService {
    private final IApplicationTypeRepository repository;
    private final IApplicationTypeMapper mapper;

    @Override
    public ApplicationTypeEntity createApplicationType(ApplicationTypeRequest request) {
        request.setName(StringUtils.NameStandardlizing(request.getName()));
        Optional<ApplicationTypeEntity> entityOptional = repository.findByName(request.getName());

        if(entityOptional.isPresent())
            throw new AppException(HttpStatus.BAD_REQUEST, "Duplicated name!!");

        ApplicationTypeEntity entity = mapper.toModel(request);
        return repository.save(entity);
    }

    @Override
    public List<ApplicationTypeEntity> getAllApplicationTypes() {
        return repository.findAll();
    }

    @Override
    public ApplicationTypeEntity getApplicationTypeById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Application Type not found"));
    }

    @Override
    public ApplicationTypeEntity updateApplicationType(Long id, ApplicationTypeRequest request) {
        request.setName(StringUtils.NameStandardlizing(request.getName()));
        Optional<ApplicationTypeEntity> entityOptional = repository.findByName(request.getName());

        if(entityOptional.isPresent())
            throw new AppException(HttpStatus.BAD_REQUEST, "Duplicated name!!");

        ApplicationTypeEntity applicationType = repository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Application Type not found"));

        mapper.updateEntityFromDto(request, applicationType);
        return repository.save(applicationType);
    }

    @Override
    public void deleteApplicationType(Long id) {
        if (!repository.existsById(id)) {
            throw new AppException(HttpStatus.NOT_FOUND, "Application Type not found");
        }
        repository.deleteById(id);
    }
}
