package com.universidad.inventario.service;

import com.universidad.inventario.dto.TransaccionDTO;

import java.util.List;

/**
 * Interfaz del servicio de Transacciones.
 * Contiene la lógica de negocio crítica: control de stock.
 */
public interface TransaccionService {

    /**
     * Registra una transacción (ENTRADA o SALIDA) y actualiza el stock del producto.
     * Lanza {@link com.universidad.inventario.exception.StockInsuficienteException}
     * si el tipo es SALIDA y no hay stock suficiente.
     *
     * @param dto datos de la transacción a registrar
     * @return el DTO de la transacción persistida
     */
    TransaccionDTO registrarTransaccion(TransaccionDTO dto);

    List<TransaccionDTO> listarTodas();

    List<TransaccionDTO> listarPorProducto(Long idProducto);
}
