package com.universidad.inventario.repository;

import com.universidad.inventario.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio JPA para la entidad Categoria.
 * Hereda operaciones CRUD de JpaRepository.
 */
@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    /**
     * Busca una categoría por su nombre (útil para validar duplicados).
     */
    Optional<Categoria> findByNombreIgnoreCase(String nombre);
}
