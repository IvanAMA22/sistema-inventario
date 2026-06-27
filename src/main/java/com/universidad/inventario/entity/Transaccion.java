package com.universidad.inventario.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad JPA que representa una Transacción de inventario (ENTRADA o SALIDA).
 */
@Entity
@Table(name = "transaccion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_transaccion")
    private Long idTransaccion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    /**
     * Enumeración para el tipo de movimiento permitido.
     */
    public enum TipoMovimiento {
        ENTRADA, SALIDA
    }

    @NotNull(message = "El tipo de movimiento es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimiento", nullable = false, length = 10)
    private TipoMovimiento tipoMovimiento;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    /**
     * La fecha se asigna automáticamente en el momento de persistir.
     * No debe ser enviada por el cliente.
     */
    @Column(name = "fecha_movimiento", nullable = false, updatable = false)
    private LocalDateTime fechaMovimiento;

    /**
     * Hook de JPA: se ejecuta antes de INSERT para asignar la fecha actual.
     */
    @PrePersist
    protected void onPrePersist() {
        this.fechaMovimiento = LocalDateTime.now();
    }
}
