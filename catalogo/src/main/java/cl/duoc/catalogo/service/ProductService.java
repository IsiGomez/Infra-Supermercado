package cl.duoc.catalogo.service;

import cl.duoc.catalogo.dto.request.ProductRequestDto;
import cl.duoc.catalogo.dto.response.ProductResponseDto;

import java.util.List;

public interface ProductService {

    ProductResponseDto getById(Long id);
    List<ProductResponseDto> getByIds(List<Long> ids);
    List<ProductResponseDto> getAll();
    List<ProductResponseDto> getByNameContainingIgnoreCase(String name);
    List<ProductResponseDto> getByCategoryId(Long categoryId);
    List<ProductResponseDto> getByCategoryIdAndPriceBetween(Long categoryId,
                                                            Integer minPrice,
                                                            Integer maxPrice);

    ProductResponseDto create(ProductRequestDto dto);
    ProductResponseDto update(Long id, ProductRequestDto dto);
    boolean delete(Long id);

}
