package com.universidad.inventario.repository;

import com.universidad.inventario.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad Producto.
 */
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    /**
     * Busca un producto por su código SKU único.
     */
    Optional<Producto> findByCodigoSku(String codigoSku);

    /**
     * Verifica si ya existe un SKU (para validaciones de unicidad en creación/edición).
     */
    boolean existsByCodigoSku(String codigoSku);

    /**
     * Obtiene todos los productos con su categoría cargada (evita N+1).
     */
    @Query("SELECT p FROM Producto p JOIN FETCH p.categoria")
    List<Producto> findAllWithCategoria();
}
