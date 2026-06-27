package cl.duoc.catalogo.repository;

import cl.duoc.catalogo.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    List<Product> findByIdIn(List<Long> ids);

    List<Product> findByNameContainingIgnoreCase(String name);

    List<Product> findByCategoryId(Long categoryId);

    List<Product> findByCategoryIdAndPriceBetween(Long categoryId,
                                                  Integer minPrice,
                                                  Integer maxPrice);

}
