package cl.duoc.catalogo.service.impl;

import cl.duoc.catalogo.dto.request.ProductRequestDto;
import cl.duoc.catalogo.dto.response.ProductResponseDto;
import cl.duoc.catalogo.mapper.ProductMapper;
import cl.duoc.catalogo.model.Category;
import cl.duoc.catalogo.model.Product;
import cl.duoc.catalogo.repository.CategoryRepository;
import cl.duoc.catalogo.repository.ProductRepository;
import cl.duoc.catalogo.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;


    @Override
    @Transactional(readOnly = true)
    public ProductResponseDto getById(Long id) {
        log.info("Buscando producto con id: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado con id: " + id));

        return productMapper.toDto(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDto> getByIds(List<Long> ids){
        log.info("Buscando productos con ids: {}", ids);

        List<Product> products = productRepository.findByIdIn(ids);

        if (products.size() != ids.size()){
            List<Long> foundIds = products.stream()
                    .map(Product::getId)
                    .toList();

            List<Long> missingIds = ids.stream()
                    .filter(id -> !foundIds.contains(id))
                    .toList();

            throw new EntityNotFoundException("No se encontraron los productos con los IDs: " + missingIds);
        }

        return productMapper.toDtoList(products);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDto> getAll() {
        log.info("Obteniendo todos los productos registrados");
        return productMapper.toDtoList(productRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDto> getByNameContainingIgnoreCase(String name) {
        log.info("Buscando productos por nombre que contenga: {}", name);
        return productMapper.toDtoList(productRepository.findByNameContainingIgnoreCase(name));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDto> getByCategoryId(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)){
            throw  new EntityNotFoundException("Categoria no encontrada con id: " + categoryId);
        }

        log.info("Buscando producto por categoria de id: {}", categoryId);
        return productMapper.toDtoList(productRepository.findByCategoryId(categoryId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDto> getByCategoryIdAndPriceBetween(Long categoryId, Integer minPrice, Integer maxPrice) {
        if (minPrice > maxPrice){
            throw new IllegalArgumentException("El precio minimo no puede ser mayor al precio maximo");
        }

        if (minPrice < 0){
            throw new IllegalArgumentException("Los precios no pueden ser negativos");
        }

        if (!categoryRepository.existsById(categoryId)){
            throw  new EntityNotFoundException("Categoria no encontrada con id: " + categoryId);
        }

        log.info("Buscando productos por categoria de id: {} entre precios {} y {}", categoryId, minPrice, maxPrice);
        return productMapper.toDtoList(
                productRepository.findByCategoryIdAndPriceBetween(categoryId, minPrice, maxPrice)
        );
    }

    @Override
    @Transactional
    public ProductResponseDto create(ProductRequestDto dto) {
        if (productRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new IllegalArgumentException("Ya existe un producto con el nombre: " + dto.getName());
        }

        log.info("Creando producto con nombre: {}", dto.getName());
        Category category = findCategoryById(dto.getCategoryId());
        Product product = productMapper.toEntity(dto);
        product.setCategory(category);

        return productMapper.toDto(productRepository.save(product));
    }

    @Override
    @Transactional
    public ProductResponseDto update(Long id, ProductRequestDto dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado con id: " + id));

        if (productRepository.existsByNameIgnoreCaseAndIdNot(dto.getName(), id)) {
            throw new IllegalArgumentException("Ya existe un producto con el nombre: " + dto.getName());
        }

        log.info("Actualizando producto con id: {}", id);
        Category category = findCategoryById(dto.getCategoryId());

        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setCategory(category);

        return productMapper.toDto(productRepository.save(product));
    }

    private Category findCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada con id: " + categoryId));
    }


    @Override
    public boolean delete(Long id) {
        if (!productRepository.existsById(id)) {
            log.warn("Producto no encontrado para eliminar con id: {}", id);
            return false;
        }

        log.info("Eliminando producto con id: {}", id);
        productRepository.deleteById(id);
        return true;
    }

}
