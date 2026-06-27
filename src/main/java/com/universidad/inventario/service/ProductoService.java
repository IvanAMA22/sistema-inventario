package com.universidad.inventario.service;

import com.universidad.inventario.dto.ProductoDTO;
import com.universidad.inventario.dto.TransaccionDTO;

import java.util.List;

/**
 * Interfaz del servicio para la gestión de Productos.
 * Separar la interfaz de la implementación facilita el testing con Mockito.
 */
public interface ProductoService {

    List<ProductoDTO> listarTodos();

    ProductoDTO buscarPorId(Long id);

    ProductoDTO guardar(ProductoDTO dto);

    ProductoDTO actualizar(Long id, ProductoDTO dto);

    void eliminar(Long id);
}
