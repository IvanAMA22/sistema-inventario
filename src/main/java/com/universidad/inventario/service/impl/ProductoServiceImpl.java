package com.universidad.inventario.service.impl;

import com.universidad.inventario.dto.ProductoDTO;
import com.universidad.inventario.entity.Categoria;
import com.universidad.inventario.entity.Producto;
import com.universidad.inventario.exception.RecursoNoEncontradoException;
import com.universidad.inventario.repository.CategoriaRepository;
import com.universidad.inventario.repository.ProductoRepository;
import com.universidad.inventario.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación de la lógica de negocio para Productos.
 */
@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    // -------------------------------------------------------
    // LISTAR TODOS (con JOIN FETCH para evitar N+1)
    // -------------------------------------------------------
    @Override
    @Transactional(readOnly = true)
    public List<ProductoDTO> listarTodos() {
        return productoRepository.findAllWithCategoria()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // -------------------------------------------------------
    // BUSCAR POR ID
    // -------------------------------------------------------
    @Override
    @Transactional(readOnly = true)
    public ProductoDTO buscarPorId(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto", id));
        return toDTO(producto);
    }

    // -------------------------------------------------------
    // GUARDAR (CREAR)
    // -------------------------------------------------------
    @Override
    @Transactional
    public ProductoDTO guardar(ProductoDTO dto) {
        // Verificar que el SKU no esté duplicado
        if (productoRepository.existsByCodigoSku(dto.getCodigoSku())) {
            throw new IllegalArgumentException(
                "Ya existe un producto con el SKU: " + dto.getCodigoSku());
        }
        Categoria categoria = categoriaRepository.findById(dto.getIdCategoria())
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría", dto.getIdCategoria()));

        Producto producto = toEntity(dto, categoria);
        return toDTO(productoRepository.save(producto));
    }

    // -------------------------------------------------------
    // ACTUALIZAR
    // -------------------------------------------------------
    @Override
    @Transactional
    public ProductoDTO actualizar(Long id, ProductoDTO dto) {
        Producto existente = productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto", id));

        // Verificar unicidad de SKU solo si cambió
        if (!existente.getCodigoSku().equalsIgnoreCase(dto.getCodigoSku())
                && productoRepository.existsByCodigoSku(dto.getCodigoSku())) {
            throw new IllegalArgumentException(
                "Ya existe un producto con el SKU: " + dto.getCodigoSku());
        }

        Categoria categoria = categoriaRepository.findById(dto.getIdCategoria())
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría", dto.getIdCategoria()));

        existente.setNombre(dto.getNombre());
        existente.setCodigoSku(dto.getCodigoSku());
        existente.setPrecioUnitario(dto.getPrecioUnitario());
        existente.setStockActual(dto.getStockActual());
        existente.setCategoria(categoria);

        return toDTO(productoRepository.save(existente));
    }

    // -------------------------------------------------------
    // ELIMINAR
    // -------------------------------------------------------
    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Producto", id);
        }
        productoRepository.deleteById(id);
    }

    // -------------------------------------------------------
    // MAPPERS internos (Entidad <-> DTO)
    // -------------------------------------------------------
    public ProductoDTO toDTO(Producto p) {
        ProductoDTO dto = new ProductoDTO();
        dto.setIdProducto(p.getIdProducto());
        dto.setIdCategoria(p.getCategoria().getIdCategoria());
        dto.setNombreCategoria(p.getCategoria().getNombre());
        dto.setCodigoSku(p.getCodigoSku());
        dto.setNombre(p.getNombre());
        dto.setPrecioUnitario(p.getPrecioUnitario());
        dto.setStockActual(p.getStockActual());
        return dto;
    }

    private Producto toEntity(ProductoDTO dto, Categoria categoria) {
        Producto p = new Producto();
        p.setCategoria(categoria);
        p.setCodigoSku(dto.getCodigoSku());
        p.setNombre(dto.getNombre());
        p.setPrecioUnitario(dto.getPrecioUnitario());
        p.setStockActual(dto.getStockActual());
        return p;
    }
}
