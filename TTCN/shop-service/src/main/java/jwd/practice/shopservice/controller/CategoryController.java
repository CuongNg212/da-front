package jwd.practice.shopservice.controller;


import jwd.practice.shopservice.dto.request.CategoryCreateRequest;
import jwd.practice.shopservice.dto.response.CategoryResponse;
import jwd.practice.shopservice.entity.Category;
import jwd.practice.shopservice.exception.IdException;
import jwd.practice.shopservice.service.Service.CategoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController

public class CategoryController {
    CategoryService categoryService;
    @PostMapping(value = "/category", consumes = {"application/json"})
    public ResponseEntity<CategoryResponse> createCategory(@RequestBody CategoryCreateRequest categoryCreateRequest)
    {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.categoryService.createCategory(categoryCreateRequest));
    }

    @PutMapping("/category/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(@RequestBody CategoryCreateRequest category, @PathVariable int id)
    {
        return ResponseEntity.ok(this.categoryService.updateCategory(category,id));
    }

    @DeleteMapping("/category/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable int id)
    {
        if(this.categoryService.deleteCategory(id))
            return ResponseEntity.ok("Xoa thanh cong");
        else
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("khong tim thay danh muc can xoa");
    }

    @GetMapping("/category/{id}")
    public ResponseEntity<Category> getOneCategory(@PathVariable int id) throws IdException
    {
        if(!this.categoryService.exisById(id))
        {
            throw new IdException("id danh mục sản phẩm không tồn tại");
        }
        return ResponseEntity.ok(this.categoryService.getOneCategory(id));
    }

    @GetMapping("/category")
    public ResponseEntity<List<Category>> getAllCategory()
    {
        return ResponseEntity.ok(this.categoryService.getAllCategory());
    }
}
