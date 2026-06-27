# Diagrama Entidad-Relación y Diccionario de Datos

**Proyecto:** Sistema de Inventario Transaccional  
**Base de Datos:** MariaDB / MySQL  
**Versión:** 1.0.0

---

## 1. Diagrama Entidad-Relación (ER)

```mermaid
erDiagram
    CATEGORIA {
        BIGINT id_categoria PK "AUTO_INCREMENT"
        VARCHAR(100) nombre "NOT NULL, UNIQUE"
        VARCHAR(255) descripcion "NULL"
    }

    PRODUCTO {
        BIGINT id_producto PK "AUTO_INCREMENT"
        BIGINT id_categoria FK "NOT NULL → CATEGORIA"
        VARCHAR(50) codigo_sku "NOT NULL, UNIQUE"
        VARCHAR(150) nombre "NOT NULL"
        DECIMAL(10,2) precio_unitario "NOT NULL, > 0"
        INT stock_actual "NOT NULL, >= 0"
    }

    TRANSACCION {
        BIGINT id_transaccion PK "AUTO_INCREMENT"
        BIGINT id_producto FK "NOT NULL → PRODUCTO"
        ENUM tipo_movimiento "ENTRADA | SALIDA, NOT NULL"
        INT cantidad "NOT NULL, > 0"
        DATETIME fecha_movimiento "NOT NULL, @PrePersist"
    }

    CATEGORIA ||--o{ PRODUCTO : "clasifica"
    PRODUCTO ||--o{ TRANSACCION : "registra"
```

---

## 2. Descripción de Relaciones

| Relación | Tipo | Descripción |
|---|---|---|
| CATEGORIA → PRODUCTO | Uno a Muchos (1:N) | Una categoría puede tener muchos productos. Un producto pertenece a exactamente una categoría. |
| PRODUCTO → TRANSACCION | Uno a Muchos (1:N) | Un producto puede tener muchas transacciones. Una transacción pertenece a un único producto. |

---

## 3. Diccionario de Datos

### 3.1 Tabla: `categoria`

| Columna | Tipo | Restricciones | Descripción |
|---|---|---|---|
| `id_categoria` | `BIGINT` | PK, AUTO_INCREMENT, NOT NULL | Identificador único de la categoría. Clave primaria. |
| `nombre` | `VARCHAR(100)` | NOT NULL, UNIQUE | Nombre descriptivo de la categoría (ej. "Electrónica", "Papelería"). No puede repetirse. |
| `descripcion` | `VARCHAR(255)` | NULL | Descripción opcional de la categoría. Puede estar vacía. |

**Índices:**
- `PRIMARY KEY (id_categoria)`
- `UNIQUE KEY uk_categoria_nombre (nombre)`

---

### 3.2 Tabla: `producto`

| Columna | Tipo | Restricciones | Descripción |
|---|---|---|---|
| `id_producto` | `BIGINT` | PK, AUTO_INCREMENT, NOT NULL | Identificador único del producto. Clave primaria. |
| `id_categoria` | `BIGINT` | FK → `categoria.id_categoria`, NOT NULL | Referencia a la categoría a la que pertenece el producto. |
| `codigo_sku` | `VARCHAR(50)` | NOT NULL, UNIQUE | Código único de identificación del producto (Stock Keeping Unit). Ej: "LAP-001". |
| `nombre` | `VARCHAR(150)` | NOT NULL | Nombre comercial del producto. Ej: "Laptop Dell Inspiron 15". |
| `precio_unitario` | `DECIMAL(10,2)` | NOT NULL, > 0.00 | Precio de venta unitario del producto. Tiene 2 decimales. |
| `stock_actual` | `INT` | NOT NULL, >= 0 | Cantidad actual de unidades disponibles en inventario. No puede ser negativo. |

**Índices:**
- `PRIMARY KEY (id_producto)`
- `UNIQUE KEY uk_producto_sku (codigo_sku)`
- `INDEX idx_producto_categoria (id_categoria)`

**Foreign Keys:**
- `FK_producto_categoria`: `id_categoria` → `categoria.id_categoria` (ON DELETE RESTRICT)

---

### 3.3 Tabla: `transaccion`

| Columna | Tipo | Restricciones | Descripción |
|---|---|---|---|
| `id_transaccion` | `BIGINT` | PK, AUTO_INCREMENT, NOT NULL | Identificador único de la transacción. Clave primaria. |
| `id_producto` | `BIGINT` | FK → `producto.id_producto`, NOT NULL | Referencia al producto involucrado en el movimiento. |
| `tipo_movimiento` | `ENUM('ENTRADA','SALIDA')` | NOT NULL | Tipo de movimiento: ENTRADA (recepción) o SALIDA (despacho). |
| `cantidad` | `INT` | NOT NULL, > 0 | Número de unidades involucradas en el movimiento. |
| `fecha_movimiento` | `DATETIME` | NOT NULL | Fecha y hora exacta en que se registró el movimiento. Se asigna automáticamente (`@PrePersist`). |

**Índices:**
- `PRIMARY KEY (id_transaccion)`
- `INDEX idx_transaccion_producto (id_producto)`
- `INDEX idx_transaccion_fecha (fecha_movimiento)`

**Foreign Keys:**
- `FK_transaccion_producto`: `id_producto` → `producto.id_producto` (ON DELETE CASCADE)

---

## 4. Reglas de Integridad de Datos

| Regla | Descripción |
|---|---|
| **Stock no negativo** | El campo `stock_actual` en `producto` nunca puede ser negativo. La lógica de negocio en `TransaccionServiceImpl` verifica esto antes de registrar una SALIDA. |
| **SKU único** | Cada producto debe tener un `codigo_sku` diferente. Se valida a nivel de BD (UNIQUE) y a nivel de servicio. |
| **Cascada en eliminación** | Al eliminar un producto, todas sus transacciones asociadas se eliminan en cascada (`@OneToMany(cascade = CascadeType.ALL)`). |
| **Fecha automática** | La `fecha_movimiento` se asigna automáticamente mediante el callback `@PrePersist` de JPA, garantizando consistencia. |
| **Categoría obligatoria** | Todo producto debe tener una categoría asignada. La FK es NOT NULL. |

---

## 5. Script DDL de Referencia (MySQL/MariaDB)

```sql
-- ============================================================
-- SISTEMA DE INVENTARIO — DDL de referencia
-- Nota: Hibernate genera y actualiza este esquema automáticamente
--       con spring.jpa.hibernate.ddl-auto=update
-- ============================================================

CREATE DATABASE IF NOT EXISTS inventario_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE inventario_db;

CREATE TABLE IF NOT EXISTS categoria (
    id_categoria BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre       VARCHAR(100) NOT NULL,
    descripcion  VARCHAR(255),
    CONSTRAINT uk_categoria_nombre UNIQUE (nombre)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS producto (
    id_producto    BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_categoria   BIGINT         NOT NULL,
    codigo_sku     VARCHAR(50)    NOT NULL,
    nombre         VARCHAR(150)   NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    stock_actual   INT            NOT NULL DEFAULT 0,
    CONSTRAINT uk_producto_sku UNIQUE (codigo_sku),
    CONSTRAINT fk_producto_categoria
        FOREIGN KEY (id_categoria) REFERENCES categoria(id_categoria)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS transaccion (
    id_transaccion  BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_producto     BIGINT                 NOT NULL,
    tipo_movimiento ENUM('ENTRADA','SALIDA') NOT NULL,
    cantidad        INT                    NOT NULL,
    fecha_movimiento DATETIME              NOT NULL,
    CONSTRAINT fk_transaccion_producto
        FOREIGN KEY (id_producto) REFERENCES producto(id_producto)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;
```
