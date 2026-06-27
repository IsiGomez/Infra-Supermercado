package cl.duoc.catalogo.service;

import cl.duoc.catalogo.dto.request.CategoryRequestDto;
import cl.duoc.catalogo.dto.response.CategoryResponseDto;

import java.util.List;

public interface CategoryService {

    CategoryResponseDto getById(Long id);
    List<CategoryResponseDto> getAll();

    CategoryResponseDto create(CategoryRequestDto dto);
    CategoryResponseDto update(Long id, CategoryRequestDto dto);
    boolean delete(Long id);

}
