package com.capstone.dfms.services;

import com.capstone.dfms.models.CategoryEntity;


import java.util.List;

public interface ICategoryServices {
    CategoryEntity createCategory(String category);

    CategoryEntity getCategoryById(long id);

    List<CategoryEntity> getAllCategorys();

    CategoryEntity updateCategory(Long id, String name );

    void deleteCategory(long id);
}
