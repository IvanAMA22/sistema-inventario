package com.universidad.inventario.service.impl;

import com.universidad.inventario.dto.TransaccionDTO;
import com.universidad.inventario.entity.Producto;
import com.universidad.inventario.entity.Transaccion;
import com.universidad.inventario.entity.Transaccion.TipoMovimiento;
import com.universidad.inventario.exception.RecursoNoEncontradoException;
import com.universidad.inventario.exception.StockInsuficienteException;
import com.universidad.inventario.repository.ProductoRepository;
import com.universidad.inventario.repository.TransaccionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas Unitarias para {@link TransaccionServiceImpl}.
 *
 * Se utiliza JUnit 5 + Mockito para aislar la capa Service
 * sin levantar el contexto de Spring (pruebas rápidas).
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TransaccionServiceImpl — Pruebas Unitarias")
class TransaccionServiceImplTest {

    // -------------------------------------------------------
    // Mocks: simulan repositorios sin tocar la BD real
    // -------------------------------------------------------
    @Mock
    private TransaccionRepository transaccionRepository;

    @Mock
    private ProductoRepository productoRepository;

    // La implementación bajo prueba, con los mocks inyectados
    @InjectMocks
    private TransaccionServiceImpl transaccionService;

    // -------------------------------------------------------
    // Objetos de prueba reutilizables
    // -------------------------------------------------------
    private Producto productoConStock;
    private Producto productoSinStock;

    @BeforeEach
    void setUp() {
        // Producto con stock suficiente (50 unidades)
        productoConStock = new Producto();
        productoConStock.setIdProducto(1L);
        productoConStock.setCodigoSku("LAPTOP-001");
        productoConStock.setNombre("Laptop Dell Inspiron");
        productoConStock.setPrecioUnitario(new BigDecimal("12500.00"));
        productoConStock.setStockActual(50);

        // Producto con stock muy bajo (2 unidades)
        productoSinStock = new Producto();
        productoSinStock.setIdProducto(2L);
        productoSinStock.setCodigoSku("MOUSE-002");
        productoSinStock.setNombre("Mouse Inalámbrico");
        productoSinStock.setPrecioUnitario(new BigDecimal("350.00"));
        productoSinStock.setStockActual(2);
    }

    // =======================================================
    // TEST CASE 1: SALIDA exitosa — descuenta stock
    // =======================================================
    @Test
    @DisplayName("TC-01: Registrar SALIDA exitosa → stock descontado correctamente")
    void registrarSalida_conStockSuficiente_debeDescontarStock() {

        // ARRANGE — preparar el escenario de prueba
        TransaccionDTO dto = new TransaccionDTO();
        dto.setIdProducto(1L);
        dto.setTipoMovimiento(TipoMovimiento.SALIDA);
        dto.setCantidad(10); // solicitar 10 unidades (stock = 50, ok)

        // Simular que el repositorio devuelve el producto con stock 50
        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoConStock));

        // Simular el guardado de la transacción (devuelve una entidad con ID)
        Transaccion transaccionGuardada = new Transaccion();
        transaccionGuardada.setIdTransaccion(100L);
        transaccionGuardada.setProducto(productoConStock);
        transaccionGuardada.setTipoMovimiento(TipoMovimiento.SALIDA);
        transaccionGuardada.setCantidad(10);
        transaccionGuardada.setFechaMovimiento(LocalDateTime.now());
        when(transaccionRepository.save(any(Transaccion.class))).thenReturn(transaccionGuardada);

        // ACT — ejecutar el método bajo prueba
        TransaccionDTO resultado = transaccionService.registrarTransaccion(dto);

        // ASSERT — verificar resultados
        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdTransaccion()).isEqualTo(100L);
        assertThat(resultado.getTipoMovimiento()).isEqualTo(TipoMovimiento.SALIDA);
        assertThat(resultado.getCantidad()).isEqualTo(10);

        // Verificar que el stock fue DESCONTADO: 50 - 10 = 40
        assertThat(productoConStock.getStockActual()).isEqualTo(40);

        // Verificar que se guardó el producto con el nuevo stock
        ArgumentCaptor<Producto> productoCaptor = ArgumentCaptor.forClass(Producto.class);
        verify(productoRepository).save(productoCaptor.capture());
        assertThat(productoCaptor.getValue().getStockActual()).isEqualTo(40);

        // Verificar que la transacción también fue guardada
        verify(transaccionRepository).save(any(Transaccion.class));
    }

    // =======================================================
    // TEST CASE 2: SALIDA fallida — StockInsuficienteException
    // =======================================================
    @Test
    @DisplayName("TC-02: Registrar SALIDA con stock insuficiente → lanza StockInsuficienteException")
    void registrarSalida_conStockInsuficiente_debeLanzarExcepcion() {

        // ARRANGE — solicitar más de lo disponible (2 unidades, pedir 10)
        TransaccionDTO dto = new TransaccionDTO();
        dto.setIdProducto(2L);
        dto.setTipoMovimiento(TipoMovimiento.SALIDA);
        dto.setCantidad(10); // pedir 10, pero solo hay 2

        when(productoRepository.findById(2L)).thenReturn(Optional.of(productoSinStock));

        // ACT & ASSERT — verificar que se lanza la excepción correcta
        StockInsuficienteException excepcion = assertThrows(
            StockInsuficienteException.class,
            () -> transaccionService.registrarTransaccion(dto),
            "Debería lanzar StockInsuficienteException cuando stock < cantidad"
        );

        // Verificar los datos de la excepción
        assertThat(excepcion.getCodigoSku()).isEqualTo("MOUSE-002");
        assertThat(excepcion.getStockDisponible()).isEqualTo(2);
        assertThat(excepcion.getCantidadSolicitada()).isEqualTo(10);
        assertThat(excepcion.getMessage()).contains("MOUSE-002");

        // Verificar que NO se guardó ninguna transacción (rollback implícito)
        verify(transaccionRepository, never()).save(any(Transaccion.class));

        // Verificar que el stock NO fue modificado
        assertThat(productoSinStock.getStockActual()).isEqualTo(2);
    }

    // =======================================================
    // TEST CASE 3: ENTRADA exitosa — suma al stock
    // =======================================================
    @Test
    @DisplayName("TC-03: Registrar ENTRADA exitosa → stock incrementado correctamente")
    void registrarEntrada_debeIncrementarStock() {

        // ARRANGE
        TransaccionDTO dto = new TransaccionDTO();
        dto.setIdProducto(1L);
        dto.setTipoMovimiento(TipoMovimiento.ENTRADA);
        dto.setCantidad(20); // recibir 20 unidades (stock actual = 50)

        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoConStock));

        Transaccion transaccionGuardada = new Transaccion();
        transaccionGuardada.setIdTransaccion(101L);
        transaccionGuardada.setProducto(productoConStock);
        transaccionGuardada.setTipoMovimiento(TipoMovimiento.ENTRADA);
        transaccionGuardada.setCantidad(20);
        transaccionGuardada.setFechaMovimiento(LocalDateTime.now());
        when(transaccionRepository.save(any(Transaccion.class))).thenReturn(transaccionGuardada);

        // ACT
        TransaccionDTO resultado = transaccionService.registrarTransaccion(dto);

        // ASSERT
        assertThat(resultado).isNotNull();
        assertThat(resultado.getTipoMovimiento()).isEqualTo(TipoMovimiento.ENTRADA);

        // Stock: 50 + 20 = 70
        assertThat(productoConStock.getStockActual()).isEqualTo(70);

        verify(productoRepository).save(any(Producto.class));
        verify(transaccionRepository).save(any(Transaccion.class));
    }

    // =======================================================
    // TEST CASE 4: Producto no encontrado → RecursoNoEncontradoException
    // =======================================================
    @Test
    @DisplayName("TC-04: Registrar transacción con ID de producto inexistente → lanza RecursoNoEncontradoException")
    void registrarTransaccion_productoInexistente_debeLanzarExcepcion() {

        // ARRANGE
        TransaccionDTO dto = new TransaccionDTO();
        dto.setIdProducto(999L); // ID que no existe
        dto.setTipoMovimiento(TipoMovimiento.ENTRADA);
        dto.setCantidad(5);

        when(productoRepository.findById(999L)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(
            RecursoNoEncontradoException.class,
            () -> transaccionService.registrarTransaccion(dto),
            "Debería lanzar RecursoNoEncontradoException para producto inexistente"
        );

        // Verificar que no se intentó guardar nada
        verify(transaccionRepository, never()).save(any(Transaccion.class));
        verify(productoRepository, never()).save(any(Producto.class));
    }

    // =======================================================
    // TEST CASE 5: SALIDA exacta al límite del stock
    // =======================================================
    @Test
    @DisplayName("TC-05: Registrar SALIDA exacta al límite del stock → exitosa, stock en 0")
    void registrarSalida_cantidadExactaAlStock_debeExitarYDejarStockEnCero() {

        // ARRANGE — solicitar exactamente las 2 unidades disponibles
        TransaccionDTO dto = new TransaccionDTO();
        dto.setIdProducto(2L);
        dto.setTipoMovimiento(TipoMovimiento.SALIDA);
        dto.setCantidad(2); // pedir exactamente 2 (stock = 2)

        when(productoRepository.findById(2L)).thenReturn(Optional.of(productoSinStock));

        Transaccion transaccionGuardada = new Transaccion();
        transaccionGuardada.setIdTransaccion(102L);
        transaccionGuardada.setProducto(productoSinStock);
        transaccionGuardada.setTipoMovimiento(TipoMovimiento.SALIDA);
        transaccionGuardada.setCantidad(2);
        transaccionGuardada.setFechaMovimiento(LocalDateTime.now());
        when(transaccionRepository.save(any(Transaccion.class))).thenReturn(transaccionGuardada);

        // ACT
        TransaccionDTO resultado = transaccionService.registrarTransaccion(dto);

        // ASSERT — debe ser exitoso y el stock queda en 0
        assertThat(resultado).isNotNull();
        assertThat(productoSinStock.getStockActual()).isEqualTo(0);
        verify(productoRepository).save(any(Producto.class));
        verify(transaccionRepository).save(any(Transaccion.class));
    }
}
