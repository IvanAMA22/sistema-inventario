package com.universidad.inventario.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la creación y actualización de una Categoría.
 * Evita exponer directamente la entidad JPA a las capas superiores.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaDTO {

    private Long idCategoria;

    @NotBlank(message = "{categoria.nombre.notblank}")
    @Size(max = 100, message = "{categoria.nombre.size}")
    private String nombre;

    @Size(max = 255, message = "{categoria.descripcion.size}")
    private String descripcion;
}
