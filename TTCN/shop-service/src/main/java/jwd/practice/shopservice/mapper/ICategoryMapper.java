package jwd.practice.shopservice.mapper;


import jwd.practice.shopservice.dto.response.CategoryResponse;
import jwd.practice.shopservice.entity.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ICategoryMapper {
    CategoryResponse toCategoryResponse(Category category);
}
