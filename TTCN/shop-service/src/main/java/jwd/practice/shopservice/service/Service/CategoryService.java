package jwd.practice.shopservice.service.Service;


import jwd.practice.shopservice.dto.request.CategoryCreateRequest;
import jwd.practice.shopservice.dto.response.CategoryResponse;
import jwd.practice.shopservice.entity.Category;
import jwd.practice.shopservice.mapper.ICategoryMapper;
import jwd.practice.shopservice.repository.Category_Repository;
import jwd.practice.shopservice.service.IService.ICategoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryService implements ICategoryService {
    final Category_Repository categoryRepository;
    ICategoryMapper categoryMapper;
    @Override
    public CategoryResponse createCategory(CategoryCreateRequest category) {
        Category newCategory = new Category();
        newCategory.setCategoryName(category.getCategoryName());
        newCategory.setDescription(category.getDescription());
        return this.categoryMapper.toCategoryResponse(this.categoryRepository.save(newCategory)) ;
    }

    @Override
    public CategoryResponse updateCategory(CategoryCreateRequest category, int id) {
        Category updateCategory = this.categoryRepository.findById(id).get();
        updateCategory.setDescription(category.getDescription());
        updateCategory.setCategoryName(category.getCategoryName());
        return this.categoryMapper.toCategoryResponse(this.categoryRepository.save(updateCategory)) ;
    }

    @Override
    public Boolean deleteCategory(int id) {
        if(this.categoryRepository.findById(id).get() == null)
            return false;
        else
        {
            this.categoryRepository.deleteById(id);
            return true;
        }
    }

    @Override
    public Category getOneCategory(int id) {
        return this.categoryRepository.findById(id).get();
    }

    @Override
    public Boolean exisById(int id) {
        return this.categoryRepository.existsById(id);
    }

    @Override
    public List<Category> getAllCategory() {
        return this.categoryRepository.findAll();
    }
}
