package com.universidad.inventario.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para la creación y actualización de un Producto.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoDTO {

    private Long idProducto;

    @NotNull(message = "{producto.categoria.notnull}")
    private Long idCategoria;

    @NotBlank(message = "{producto.sku.notblank}")
    @Size(max = 50, message = "{producto.sku.size}")
    private String codigoSku;

    @NotBlank(message = "{producto.nombre.notblank}")
    @Size(max = 150, message = "{producto.nombre.size}")
    private String nombre;

    @NotNull(message = "{producto.precio.notnull}")
    @DecimalMin(value = "0.01", message = "{producto.precio.min}")
    private BigDecimal precioUnitario;

    @NotNull(message = "{producto.stock.notnull}")
    @Min(value = 0, message = "{producto.stock.min}")
    private Integer stockActual;

    // Campo de solo lectura para mostrar el nombre de la categoría en respuestas
    private String nombreCategoria;
}
