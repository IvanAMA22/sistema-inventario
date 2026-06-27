package com.universidad.inventario.service.impl;

import com.universidad.inventario.dto.ProductoDTO;
import com.universidad.inventario.entity.Categoria;
import com.universidad.inventario.entity.Producto;
import com.universidad.inventario.exception.RecursoNoEncontradoException;
import com.universidad.inventario.repository.CategoriaRepository;
import com.universidad.inventario.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para ProductoServiceImpl usando JUnit 5 y Mockito.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ProductoServiceImpl — Pruebas Unitarias")
class ProductoServiceImplTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private ProductoServiceImpl productoService;

    private Categoria categoria;
    private Producto producto;
    private ProductoDTO productoDTO;

    @Test
    @Disabled("Prueba de infraestructura: requiere BD MySQL/MariaDB local. " +
              "No ejecutar en el pipeline CI/CD (Jenkins Stage 2).")
    void testDbConnectionLocally() {
        String[] urls = {
            "jdbc:mysql://localhost:3306/?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true",
            "jdbc:mariadb://localhost:3306/?disableSspi=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
        };
        String[] users = {"root"};
        String[] passwords = {"root", "", "admin", "123456", "mysql"};

        System.out.println("=== INICIANDO PRUEBA DE CONEXIÓN A BASE DE DATOS ===");
        for (String url : urls) {
            for (String user : users) {
                for (String password : passwords) {
                    try (java.sql.Connection conn = java.sql.DriverManager.getConnection(url, user, password)) {
                        System.out.println("CONEXIÓN EXITOSA: url=" + url + " user=" + user + " pass=" + password);
                        System.out.println("Motor: " + conn.getMetaData().getDatabaseProductName());
                        System.out.println("Versión: " + conn.getMetaData().getDatabaseProductVersion());
                        return;
                    } catch (Exception e) {
                        System.out.println("FALLÓ: url=" + url + " user=" + user + " pass=" + password + " -> " + e.getMessage());
                    }
                }
            }
        }
        System.out.println("=== FIN DE PRUEBA DE CONEXIÓN ===");
    }

    @BeforeEach
    void setUp() {
        categoria = new Categoria();
        categoria.setIdCategoria(1L);
        categoria.setNombre("Electronica");
        categoria.setDescripcion("Equipos Electronicos");

        producto = new Producto();
        producto.setIdProducto(1L);
        producto.setCategoria(categoria);
        producto.setCodigoSku("LAP-001");
        producto.setNombre("Laptop Dell");
        producto.setPrecioUnitario(new BigDecimal("12500.00"));
        producto.setStockActual(15);

        productoDTO = new ProductoDTO();
        productoDTO.setIdProducto(1L);
        productoDTO.setIdCategoria(1L);
        productoDTO.setCodigoSku("LAP-001");
        productoDTO.setNombre("Laptop Dell");
        productoDTO.setPrecioUnitario(new BigDecimal("12500.00"));
        productoDTO.setStockActual(15);
    }

    @Test
    @DisplayName("Listar todos los productos retorna DTOs")
    void listarTodos_debeRetornarListaDeDTOs() {
        when(productoRepository.findAllWithCategoria()).thenReturn(Collections.singletonList(producto));

        List<ProductoDTO> resultado = productoService.listarTodos();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getCodigoSku()).isEqualTo("LAP-001");
        assertThat(resultado.get(0).getNombreCategoria()).isEqualTo("Electronica");
        verify(productoRepository).findAllWithCategoria();
    }

    @Test
    @DisplayName("Buscar producto por ID existente retorna DTO")
    void buscarPorId_existente_debeRetornarDTO() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        ProductoDTO resultado = productoService.buscarPorId(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombre()).isEqualTo("Laptop Dell");
        verify(productoRepository).findById(1L);
    }

    @Test
    @DisplayName("Buscar producto por ID no existente lanza excepcion")
    void buscarPorId_inexistente_debeLanzarExcepcion() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> productoService.buscarPorId(99L));
        verify(productoRepository).findById(99L);
    }

    @Test
    @DisplayName("Guardar producto nuevo exitosamente")
    void guardar_exitoso_debeRetornarDTO() {
        when(productoRepository.existsByCodigoSku("LAP-001")).thenReturn(false);
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);

        ProductoDTO resultado = productoService.guardar(productoDTO);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getCodigoSku()).isEqualTo("LAP-001");
        verify(productoRepository).save(any(Producto.class));
    }

    @Test
    @DisplayName("Guardar producto con SKU duplicado lanza excepcion")
    void guardar_skuDuplicado_debeLanzarExcepcion() {
        when(productoRepository.existsByCodigoSku("LAP-001")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> productoService.guardar(productoDTO));
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    @DisplayName("Actualizar producto exitosamente sin cambiar SKU")
    void actualizar_sinCambiarSku_debeGuardarCorrectamente() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);

        ProductoDTO resultado = productoService.actualizar(1L, productoDTO);

        assertThat(resultado).isNotNull();
        verify(productoRepository).save(any(Producto.class));
    }

    @Test
    @DisplayName("Actualizar producto cambiando a SKU no existente")
    void actualizar_cambiandoSkuInexistente_debeGuardarCorrectamente() {
        productoDTO.setCodigoSku("LAP-002");
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.existsByCodigoSku("LAP-002")).thenReturn(false);
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);

        ProductoDTO resultado = productoService.actualizar(1L, productoDTO);

        assertThat(resultado).isNotNull();
        verify(productoRepository).save(any(Producto.class));
    }

    @Test
    @DisplayName("Actualizar producto cambiando a SKU duplicado lanza excepcion")
    void actualizar_cambiandoSkuDuplicado_debeLanzarExcepcion() {
        productoDTO.setCodigoSku("LAP-002");
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.existsByCodigoSku("LAP-002")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> productoService.actualizar(1L, productoDTO));
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    @DisplayName("Eliminar producto existente exitosamente")
    void eliminar_existente_debeProceder() {
        when(productoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productoRepository).deleteById(1L);

        productoService.eliminar(1L);

        verify(productoRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Eliminar producto inexistente lanza excepcion")
    void eliminar_inexistente_debeLanzarExcepcion() {
        when(productoRepository.existsById(99L)).thenReturn(false);

        assertThrows(RecursoNoEncontradoException.class, () -> productoService.eliminar(99L));
        verify(productoRepository, never()).deleteById(anyLong());
    }
}
