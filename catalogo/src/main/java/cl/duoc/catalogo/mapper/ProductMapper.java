package cl.duoc.catalogo.mapper;

import cl.duoc.catalogo.dto.request.ProductRequestDto;
import cl.duoc.catalogo.dto.response.ProductResponseDto;
import cl.duoc.catalogo.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring",  uses = CategoryMapper.class)
public interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    Product toEntity(ProductRequestDto dto);

    ProductResponseDto toDto(Product product);

    List<ProductResponseDto> toDtoList(List<Product> products);

}
