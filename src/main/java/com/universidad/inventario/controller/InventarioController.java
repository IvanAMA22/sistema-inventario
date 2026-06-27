package com.universidad.inventario.controller;

import com.universidad.inventario.dto.ProductoDTO;
import com.universidad.inventario.dto.TransaccionDTO;
import com.universidad.inventario.entity.Transaccion.TipoMovimiento;
import com.universidad.inventario.exception.StockInsuficienteException;
import com.universidad.inventario.repository.CategoriaRepository;
import com.universidad.inventario.service.ProductoService;
import com.universidad.inventario.service.TransaccionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador Web MVC para las vistas Thymeleaf del Sistema de Inventario.
 * Procesa todas las rutas orientadas a la interfaz de usuario HTML.
 */
@Controller
@RequestMapping("/inventario")
@RequiredArgsConstructor
public class InventarioController {

    private final ProductoService productoService;
    private final TransaccionService transaccionService;
    private final CategoriaRepository categoriaRepository;

    // ===================================================
    // INVENTARIO - PÁGINA PRINCIPAL (lista de productos)
    // ===================================================
    @GetMapping
    public String listarInventario(Model model) {
        model.addAttribute("productos", productoService.listarTodos());
        model.addAttribute("tituloPagina", "inventario.titulo");
        return "inventario/lista";
    }

    // ===================================================
    // PRODUCTO - FORMULARIO NUEVO
    // ===================================================
    @GetMapping("/producto/nuevo")
    public String formularioNuevoProducto(Model model) {
        model.addAttribute("productoDTO", new ProductoDTO());
        model.addAttribute("categorias", categoriaRepository.findAll());
        return "inventario/producto-form";
    }

    // ===================================================
    // PRODUCTO - GUARDAR (POST)
    // ===================================================
    @PostMapping("/producto/guardar")
    public String guardarProducto(
            @Valid @ModelAttribute("productoDTO") ProductoDTO dto,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("categorias", categoriaRepository.findAll());
            return "inventario/producto-form";
        }
        try {
            if (dto.getIdProducto() == null) {
                productoService.guardar(dto);
                redirectAttributes.addFlashAttribute("mensajeExito", "msg.producto.creado");
            } else {
                productoService.actualizar(dto.getIdProducto(), dto);
                redirectAttributes.addFlashAttribute("mensajeExito", "msg.producto.actualizado");
            }
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorSku", e.getMessage());
            model.addAttribute("categorias", categoriaRepository.findAll());
            return "inventario/producto-form";
        }
        return "redirect:/inventario";
    }

    // ===================================================
    // PRODUCTO - FORMULARIO EDITAR
    // ===================================================
    @GetMapping("/producto/editar/{id}")
    public String formularioEditarProducto(@PathVariable Long id, Model model) {
        model.addAttribute("productoDTO", productoService.buscarPorId(id));
        model.addAttribute("categorias", categoriaRepository.findAll());
        return "inventario/producto-form";
    }

    // ===================================================
    // PRODUCTO - ELIMINAR
    // ===================================================
    @PostMapping("/producto/eliminar/{id}")
    public String eliminarProducto(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        productoService.eliminar(id);
        redirectAttributes.addFlashAttribute("mensajeExito", "msg.producto.eliminado");
        return "redirect:/inventario";
    }

    // ===================================================
    // TRANSACCIÓN - FORMULARIO
    // ===================================================
    @GetMapping("/transaccion/nueva")
    public String formularioNuevaTransaccion(Model model) {
        model.addAttribute("transaccionDTO", new TransaccionDTO());
        model.addAttribute("productos", productoService.listarTodos());
        model.addAttribute("tiposMovimiento", TipoMovimiento.values());
        return "inventario/transaccion-form";
    }

    // ===================================================
    // TRANSACCIÓN - REGISTRAR (POST)
    // ===================================================
    @PostMapping("/transaccion/registrar")
    public String registrarTransaccion(
            @Valid @ModelAttribute("transaccionDTO") TransaccionDTO dto,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("productos", productoService.listarTodos());
            model.addAttribute("tiposMovimiento", TipoMovimiento.values());
            return "inventario/transaccion-form";
        }
        try {
            transaccionService.registrarTransaccion(dto);
            redirectAttributes.addFlashAttribute("mensajeExito", "msg.transaccion.registrada");
        } catch (StockInsuficienteException e) {
            redirectAttributes.addFlashAttribute("mensajeError", e.getMessage());
        }
        return "redirect:/inventario";
    }

    // ===================================================
    // TRANSACCIONES - LISTA COMPLETA
    // ===================================================
    @GetMapping("/transacciones")
    public String listarTransacciones(Model model) {
        model.addAttribute("transacciones", transaccionService.listarTodas());
        return "inventario/transacciones-lista";
    }
}
