package com.universidad.inventario.controller;

import com.universidad.inventario.dto.ProductoDTO;
import com.universidad.inventario.dto.TransaccionDTO;
import com.universidad.inventario.service.ProductoService;
import com.universidad.inventario.service.TransaccionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller bajo /api/inventario.
 * Expone los recursos del inventario en formato JSON.
 */
@RestController
@RequestMapping("/api/inventario")
@RequiredArgsConstructor
public class InventarioRestController {

    private final ProductoService productoService;
    private final TransaccionService transaccionService;

    // -------------------------------------------------------
    // GET /api/inventario/productos — listar todos los productos
    // -------------------------------------------------------
    @GetMapping("/productos")
    public ResponseEntity<List<ProductoDTO>> listarProductos() {
        return ResponseEntity.ok(productoService.listarTodos());
    }

    // -------------------------------------------------------
    // GET /api/inventario/productos/{id}
    // -------------------------------------------------------
    @GetMapping("/productos/{id}")
    public ResponseEntity<ProductoDTO> obtenerProducto(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.buscarPorId(id));
    }

    // -------------------------------------------------------
    // POST /api/inventario/transacciones — registrar movimiento via REST
    // -------------------------------------------------------
    @PostMapping("/transacciones")
    public ResponseEntity<TransaccionDTO> registrarTransaccion(
            @Valid @RequestBody TransaccionDTO dto) {
        TransaccionDTO resultado = transaccionService.registrarTransaccion(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
    }

    // -------------------------------------------------------
    // GET /api/inventario/transacciones — listar todas
    // -------------------------------------------------------
    @GetMapping("/transacciones")
    public ResponseEntity<List<TransaccionDTO>> listarTransacciones() {
        return ResponseEntity.ok(transaccionService.listarTodas());
    }
}
