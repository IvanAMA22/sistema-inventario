package com.universidad.inventario.repository;

import com.universidad.inventario.entity.Transaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio JPA para la entidad Transaccion.
 */
@Repository
public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {

    /**
     * Obtiene el historial de transacciones para un producto específico,
     * ordenadas por fecha descendente.
     */
    List<Transaccion> findByProducto_IdProductoOrderByFechaMovimientoDesc(Long idProducto);
}
