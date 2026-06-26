package cl.duoc.catalogo.service.impl;

import cl.duoc.catalogo.dto.request.CategoryRequestDto;
import cl.duoc.catalogo.dto.response.CategoryResponseDto;
import cl.duoc.catalogo.mapper.CategoryMapper;
import cl.duoc.catalogo.model.Category;
import cl.duoc.catalogo.repository.CategoryRepository;
import cl.duoc.catalogo.service.CategoryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryImpl implements CategoryService {

    private final CategoryRepository repository;
    private final CategoryMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public CategoryResponseDto getById(Long id) {
        log.info("Buscando categoria con id: {}", id);
        Category category = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoria no encontrada con id: " + id));

        return mapper.toDto(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponseDto> getAll() {
        log.info("Obteniendo todas las categorias registradas");
        return mapper.toDtoList(repository.findAll());
    }

    @Override
    @Transactional
    public CategoryResponseDto create(CategoryRequestDto dto) {
        if (repository.existsByNameIgnoreCase(dto.getName())) {
            throw new IllegalArgumentException("Ya existe una categoria con el nombre: " + dto.getName());
        }

        log.info("Creando categoria con nombre: {}", dto.getName());
        Category category = mapper.toEntity(dto);
        return mapper.toDto(repository.save(category));
    }

    @Override
    @Transactional
    public CategoryResponseDto update(Long id, CategoryRequestDto dto) {
        Category category = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoria no encontrada con id: " + id));

        if (repository.existsByNameIgnoreCaseAndIdNot(dto.getName(), id)) {
            throw new IllegalArgumentException("Ya existe una categoria con el nombre: " + dto.getName());
        }

        log.info("Actualizando categoria con id: {}", id);
        category.setName(dto.getName());

        return mapper.toDto(repository.save(category));
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        if (!repository.existsById(id)) {
            log.warn("Categoria no encontrada para eliminar con id: {}", id);
            return false;
        }

        log.info("Eliminando categoria con id: {}", id);
        repository.deleteById(id);
        return true;
    }


}
