package com.capstone.dfms.services.impliments;

import com.capstone.dfms.components.exceptions.AppException;
import com.capstone.dfms.components.exceptions.DataNotFoundException;
import com.capstone.dfms.components.utils.LocalizationUtils;
import com.capstone.dfms.components.utils.StringUtils;
import com.capstone.dfms.models.CategoryEntity;
import com.capstone.dfms.repositories.ICategoryRepository;
import com.capstone.dfms.services.ICategoryServices;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CategoryService implements ICategoryServices {
    private final ICategoryRepository categoryRepository;

    @Override
    public CategoryEntity createCategory(String name ) {
        CategoryEntity category = new CategoryEntity();
        category.setName(StringUtils.NameStandardlizing(name));
        return categoryRepository.save(category);
    }

    @Override
    public CategoryEntity getCategoryById(long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("category.not_exist")));
    }

    @Override
    public List<CategoryEntity> getAllCategorys() {
        return categoryRepository.findAll();
    }

    @Override
    public CategoryEntity updateCategory(Long id, String name) {
        CategoryEntity category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("category.not_exist")));
        category.setName(name);
        return categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(long id) {
        CategoryEntity category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("category.not_exist")));
        categoryRepository.delete(category);
    }
}
