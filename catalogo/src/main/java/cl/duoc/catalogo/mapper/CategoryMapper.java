package cl.duoc.catalogo.mapper;

import cl.duoc.catalogo.dto.request.CategoryRequestDto;
import cl.duoc.catalogo.dto.response.CategoryResponseDto;
import cl.duoc.catalogo.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "id", ignore = true)
    Category toEntity(CategoryRequestDto dto);

    CategoryResponseDto toDto(Category category);

    List<CategoryResponseDto> toDtoList(List<Category> categories);

}
