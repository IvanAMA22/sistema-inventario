package com.universidad.inventario;

import com.universidad.inventario.entity.Categoria;
import com.universidad.inventario.entity.Producto;
import com.universidad.inventario.repository.CategoriaRepository;
import com.universidad.inventario.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Inicializa la base de datos H2 con datos de prueba cuando se activa
 * el perfil "dev". Se ejecuta DESPUÉS de que Hibernate crea el esquema.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;

    @Override
    public void run(String... args) {
        if (categoriaRepository.count() > 0) {
            log.info("[DataInitializer] La BD ya tiene datos, se omite la carga inicial.");
            return;
        }

        log.info("[DataInitializer] Cargando datos de prueba...");

        // --- Categorías ---
        Categoria electronica    = save(categoria("Electronica",    "Equipos y dispositivos electronicos"));
        Categoria perifericos    = save(categoria("Perifericos",    "Accesorios y perifericos de computadora"));
        Categoria redes          = save(categoria("Redes",          "Equipos de conectividad y redes"));
        Categoria almacenamiento = save(categoria("Almacenamiento", "Dispositivos de almacenamiento digital"));

        // --- Productos ---
        productoRepository.save(producto(electronica,    "LAPTOP-001",  "Laptop Dell Inspiron 15",       "12500.00", 25));
        productoRepository.save(producto(electronica,    "LAPTOP-002",  "Laptop HP Pavilion 14",          "9800.00", 18));
        productoRepository.save(producto(electronica,    "MONITOR-001", "Monitor LG 27 4K",               "6200.00", 12));
        productoRepository.save(producto(perifericos,    "MOUSE-001",   "Mouse Logitech MX Master 3",      "850.00", 45));
        productoRepository.save(producto(perifericos,    "TECLADO-001", "Teclado Mecanico Redragon K552",  "650.00",  8));
        productoRepository.save(producto(perifericos,    "AURIF-001",   "Audifonos Sony WH-1000XM5",      "4500.00", 10));
        productoRepository.save(producto(redes,          "ROUTER-001",  "Router TP-Link AX3000",          "1200.00", 30));
        productoRepository.save(producto(almacenamiento, "SSD-001",     "SSD Samsung 1TB NVMe",           "1800.00",  3));
        productoRepository.save(producto(almacenamiento, "USB-001",     "USB Kingston 64GB",                "85.00", 60));

        log.info("[DataInitializer] ✓ {} categorías y {} productos cargados.",
                categoriaRepository.count(), productoRepository.count());
    }

    private Categoria save(Categoria c) {
        return categoriaRepository.save(c);
    }

    private Categoria categoria(String nombre, String descripcion) {
        Categoria c = new Categoria();
        c.setNombre(nombre);
        c.setDescripcion(descripcion);
        return c;
    }

    private Producto producto(Categoria cat, String sku, String nombre, String precio, int stock) {
        Producto p = new Producto();
        p.setCategoria(cat);
        p.setCodigoSku(sku);
        p.setNombre(nombre);
        p.setPrecioUnitario(new BigDecimal(precio));
        p.setStockActual(stock);
        return p;
    }
}
