# Manual de Usuario — Sistema de Inventario Transaccional

**Versión:** 1.0.0  
**URL de acceso:** `http://localhost:8080/inventario`

---

## Introducción

El **Sistema de Inventario Transaccional** es una aplicación web que permite gestionar el stock de productos de una organización. Con este sistema puedes:

- Registrar y mantener un catálogo de productos.
- Controlar entradas y salidas de mercancía en tiempo real.
- Consultar el historial completo de movimientos.
- Acceder a la información en español o inglés.

---

## 1. Acceso al Sistema

1. Abre tu navegador (Chrome, Firefox o Edge).
2. Navega a la dirección: **`http://localhost:8080/inventario`**
3. Verás el panel principal con la lista de productos.

> **Nota:** La aplicación debe estar en ejecución. Si ves un error de conexión, contacta al administrador del sistema.

---

## 2. Interfaz Principal — Lista de Productos

Al ingresar al sistema verás la pantalla principal con:

| Sección | Descripción |
|---|---|
| **Barra de navegación** | Contiene el nombre del sistema, acceso a Transacciones y selector de idioma. |
| **Botón "Nuevo Producto"** | Abre el formulario para agregar un nuevo producto al inventario. |
| **Tabla de productos** | Lista todos los productos con sus datos: ID, SKU, Nombre, Categoría, Precio, Stock. |
| **Panel de resumen** | Muestra el total de productos, productos con stock bajo y acceso rápido a transacciones. |

### Indicadores de stock

| Indicador | Significado |
|---|---|
| Número en **verde** | El producto tiene stock suficiente (más de 5 unidades). |
| Número en **rojo** + badge "LOW" | ⚠️ El producto tiene 5 o menos unidades disponibles. Reabastecer pronto. |

---

## 3. Gestión de Productos

### 3.1 Crear un Nuevo Producto

1. En la pantalla principal, haz clic en el botón **"+ Nuevo Producto"** (esquina superior derecha).
2. Se abre el formulario. Completa los siguientes campos:

| Campo | Descripción | Ejemplo |
|---|---|---|
| **Código SKU** | Código único de identificación del producto. | `LAP-001` |
| **Nombre del Producto** | Nombre descriptivo del producto. | `Laptop Dell Inspiron 15` |
| **Categoría** | Selecciona la categoría del producto del menú desplegable. | `Electrónica` |
| **Precio Unitario** | Precio de venta unitario (mayor a $0.00). | `12500.00` |
| **Stock Actual** | Cantidad inicial de unidades en inventario (mínimo 0). | `25` |

3. Haz clic en **"Guardar"**.
4. Verás un mensaje de confirmación: *"Producto creado exitosamente"*.

> **⚠️ Errores comunes:**
> - Si el **SKU ya existe** para otro producto, verás el mensaje: *"Ya existe un producto con el SKU: XXX"*.
> - Si dejas un campo obligatorio vacío, aparecerán mensajes de validación en rojo junto a cada campo.

---

### 3.2 Editar un Producto Existente

1. En la tabla de productos, localiza el producto que deseas modificar.
2. Haz clic en el botón ✏️ (**Editar**) en la columna "Acciones".
3. El formulario se abrirá con los datos actuales del producto.
4. Modifica los campos que deseas cambiar.
5. Haz clic en **"Guardar"**.
6. Verás el mensaje: *"Producto actualizado exitosamente"*.

---

### 3.3 Eliminar un Producto

> **⚠️ Advertencia:** Al eliminar un producto, también se eliminan todas sus transacciones asociadas. Esta acción **no se puede deshacer**.

1. En la tabla de productos, haz clic en el botón 🗑️ (**Eliminar**) del producto.
2. Aparecerá un cuadro de confirmación: *"¿Estás seguro de que deseas eliminar este producto?"*
3. Haz clic en **"Aceptar"** para confirmar la eliminación.
4. Verás el mensaje: *"Producto eliminado exitosamente"*.

---

## 4. Registro de Transacciones (Movimientos de Stock)

Las transacciones registran los movimientos de entrada y salida de mercancía.

### 4.1 Registrar una Nueva Transacción

1. En la barra de navegación, haz clic en **"Transacciones"** → **"Nueva Transacción"**.  
   *O también puedes hacer clic en el botón "Transacciones" del panel de resumen.*
2. Se abre el formulario. Completa los campos:

| Campo | Descripción |
|---|---|
| **Producto** | Selecciona el producto del menú desplegable. |
| **Tipo de Movimiento** | Elige **ENTRADA** (recibes mercancía) o **SALIDA** (despachas mercancía). |
| **Cantidad** | Número de unidades involucradas en el movimiento (mínimo 1). |

3. Haz clic en **"Registrar"**.

#### ✅ Transacción exitosa
- El stock del producto se actualiza automáticamente.
- Aparece el mensaje de confirmación.

#### ❌ Error por stock insuficiente (solo en SALIDA)
Si intentas registrar una SALIDA y la cantidad supera el stock disponible, verás el mensaje:

> *"Stock insuficiente para [SKU]: disponible=[N] unidades, solicitado=[M] unidades."*

En este caso, el stock **no se modifica** y la transacción **no se registra**. Reduce la cantidad o registra primero una ENTRADA.

---

### 4.2 Ver el Historial de Transacciones

1. En la barra de navegación, haz clic en **"Transacciones"**.
2. Verás la tabla completa con todas las transacciones registradas:

| Columna | Descripción |
|---|---|
| **ID** | Número único de la transacción. |
| **Producto** | Nombre del producto involucrado. |
| **Tipo** | ENTRADA (en verde) o SALIDA (en rojo). |
| **Cantidad** | Unidades del movimiento. |
| **Fecha** | Fecha y hora exacta del registro. |

---

## 5. Cambio de Idioma

El sistema está disponible en **Español** e **Inglés**.

Para cambiar el idioma:
1. En la barra de navegación, localiza los botones de idioma (generalmente en la parte superior derecha).
2. Haz clic en **"ES"** para cambiar a Español.
3. Haz clic en **"EN"** para cambiar a Inglés.

La interfaz se actualizará inmediatamente al idioma seleccionado.

---

## 6. Preguntas Frecuentes (FAQ)

**¿Puedo agregar categorías desde la interfaz web?**  
Actualmente las categorías se crean mediante el inicializador de datos automático al arrancar la aplicación. En versiones futuras se añadirá gestión de categorías desde la UI.

**¿Qué pasa si cierro la ventana del navegador?**  
Tus datos están guardados en la base de datos. Al volver a abrir el sistema, toda la información estará disponible.

**¿Puedo acceder desde otro dispositivo en la misma red?**  
Sí. Si el servidor está en ejecución, puedes acceder desde cualquier equipo en la misma red usando la IP del servidor: `http://[IP-DEL-SERVIDOR]:8080/inventario`.

**¿Dónde veo la API REST?**  
La API está disponible en:
- Listar productos: `http://localhost:8080/api/inventario/productos`
- Listar transacciones: `http://localhost:8080/api/inventario/transacciones`

**¿Qué hacer si el sistema muestra un error 500?**  
Verifica que la base de datos MariaDB/MySQL esté corriendo y que los datos de conexión en `application.properties` sean correctos.
