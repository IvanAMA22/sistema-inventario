package com.universidad.inventario.dto;

import com.universidad.inventario.entity.Transaccion.TipoMovimiento;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para el registro de una Transacción de inventario.
 * La fecha (fechaMovimiento) es asignada por @PrePersist y no se recibe del cliente.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransaccionDTO {

    private Long idTransaccion;

    @NotNull(message = "{transaccion.producto.notnull}")
    private Long idProducto;

    @NotNull(message = "{transaccion.tipo.notnull}")
    private TipoMovimiento tipoMovimiento;

    @NotNull(message = "{transaccion.cantidad.notnull}")
    @Min(value = 1, message = "{transaccion.cantidad.min}")
    private Integer cantidad;

    // Solo lectura: retornado en respuesta
    private LocalDateTime fechaMovimiento;

    // Solo lectura: nombre del producto para mostrar en vistas
    private String nombreProducto;
}
