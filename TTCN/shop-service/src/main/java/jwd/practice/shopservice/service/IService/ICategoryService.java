package jwd.practice.shopservice.service.IService;



import jwd.practice.shopservice.dto.request.CategoryCreateRequest;
import jwd.practice.shopservice.dto.response.CategoryResponse;
import jwd.practice.shopservice.entity.Category;

import java.util.List;

public interface ICategoryService {
    CategoryResponse createCategory(CategoryCreateRequest categoryCreateRequest);
    CategoryResponse updateCategory(CategoryCreateRequest category, int id);
    Boolean deleteCategory(int id);
    Category getOneCategory(int id);
    Boolean exisById(int id);
    List<Category> getAllCategory();
}
