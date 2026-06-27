package com.universidad.inventario.service.impl;

import com.universidad.inventario.dto.TransaccionDTO;
import com.universidad.inventario.entity.Producto;
import com.universidad.inventario.entity.Transaccion;
import com.universidad.inventario.entity.Transaccion.TipoMovimiento;
import com.universidad.inventario.exception.RecursoNoEncontradoException;
import com.universidad.inventario.exception.StockInsuficienteException;
import com.universidad.inventario.repository.ProductoRepository;
import com.universidad.inventario.repository.TransaccionRepository;
import com.universidad.inventario.service.TransaccionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de Transacciones.
 *
 * Reglas de negocio implementadas:
 *  1. Si el tipo es SALIDA y stockActual < cantidad → lanzar StockInsuficienteException.
 *  2. Actualizar stock_actual del producto al persistir la transacción.
 */
@Service
@RequiredArgsConstructor
public class TransaccionServiceImpl implements TransaccionService {

    private final TransaccionRepository transaccionRepository;
    private final ProductoRepository productoRepository;

    // -------------------------------------------------------
    // REGISTRAR TRANSACCIÓN — lógica de negocio principal
    // -------------------------------------------------------
    @Override
    @Transactional
    public TransaccionDTO registrarTransaccion(TransaccionDTO dto) {

        // 1. Obtener el producto o lanzar 404
        Producto producto = productoRepository.findById(dto.getIdProducto())
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto", dto.getIdProducto()));

        // 2. Validar stock si es una SALIDA
        if (TipoMovimiento.SALIDA.equals(dto.getTipoMovimiento())) {
            if (producto.getStockActual() < dto.getCantidad()) {
                throw new StockInsuficienteException(
                    producto.getCodigoSku(),
                    producto.getStockActual(),
                    dto.getCantidad()
                );
            }
            // Descontar stock
            producto.setStockActual(producto.getStockActual() - dto.getCantidad());
        } else {
            // ENTRADA: sumar al stock
            producto.setStockActual(producto.getStockActual() + dto.getCantidad());
        }

        // 3. Persistir el producto con el stock actualizado
        productoRepository.save(producto);

        // 4. Crear y persistir la transacción (@PrePersist asignará la fecha)
        Transaccion transaccion = new Transaccion();
        transaccion.setProducto(producto);
        transaccion.setTipoMovimiento(dto.getTipoMovimiento());
        transaccion.setCantidad(dto.getCantidad());

        Transaccion guardada = transaccionRepository.save(transaccion);
        return toDTO(guardada);
    }

    // -------------------------------------------------------
    // LISTAR TODAS
    // -------------------------------------------------------
    @Override
    @Transactional(readOnly = true)
    public List<TransaccionDTO> listarTodas() {
        return transaccionRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // -------------------------------------------------------
    // LISTAR POR PRODUCTO
    // -------------------------------------------------------
    @Override
    @Transactional(readOnly = true)
    public List<TransaccionDTO> listarPorProducto(Long idProducto) {
        return transaccionRepository
                .findByProducto_IdProductoOrderByFechaMovimientoDesc(idProducto)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // -------------------------------------------------------
    // MAPPER interno
    // -------------------------------------------------------
    private TransaccionDTO toDTO(Transaccion t) {
        TransaccionDTO dto = new TransaccionDTO();
        dto.setIdTransaccion(t.getIdTransaccion());
        dto.setIdProducto(t.getProducto().getIdProducto());
        dto.setNombreProducto(t.getProducto().getNombre());
        dto.setTipoMovimiento(t.getTipoMovimiento());
        dto.setCantidad(t.getCantidad());
        dto.setFechaMovimiento(t.getFechaMovimiento());
        return dto;
    }
}
