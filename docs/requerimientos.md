# Requerimientos del Sistema — Inventario Transaccional

**Unidad de Aprendizaje:** Diseño de Sistemas  
**Proyecto:** Sistema de Inventario Transaccional  
**Versión:** 1.0.0

---

## 1. Requerimientos Funcionales (RF)

Los requerimientos funcionales describen **qué debe hacer** el sistema.

### RF-01 — Gestión de Productos

| ID | Requerimiento | Prioridad |
|---|---|---|
| RF-01.1 | El sistema debe permitir **crear** un nuevo producto con: nombre, código SKU (único), categoría, precio unitario y stock inicial. | Alta |
| RF-01.2 | El sistema debe **listar** todos los productos registrados en una tabla con sus datos principales. | Alta |
| RF-01.3 | El sistema debe permitir **editar** los datos de un producto existente. | Alta |
| RF-01.4 | El sistema debe permitir **eliminar** un producto del inventario. | Media |
| RF-01.5 | El sistema debe **validar** que el código SKU sea único. Si ya existe, debe mostrar un mensaje de error descriptivo. | Alta |
| RF-01.6 | El sistema debe mostrar una **alerta visual** (badge rojo "LOW") cuando el stock de un producto sea igual o menor a 5 unidades. | Media |

---

### RF-02 — Gestión de Categorías

| ID | Requerimiento | Prioridad |
|---|---|---|
| RF-02.1 | El sistema debe mantener un catálogo de categorías que permita clasificar los productos. | Alta |
| RF-02.2 | El sistema debe mostrar el nombre de la categoría asociada a cada producto en la lista. | Alta |

---

### RF-03 — Registro de Transacciones (Movimientos de Stock)

| ID | Requerimiento | Prioridad |
|---|---|---|
| RF-03.1 | El sistema debe permitir registrar una **transacción de ENTRADA** (recepción de mercancía), incrementando el stock del producto. | Alta |
| RF-03.2 | El sistema debe permitir registrar una **transacción de SALIDA** (despacho de mercancía), descontando el stock del producto. | Alta |
| RF-03.3 | Antes de registrar una SALIDA, el sistema debe **validar** que el stock disponible sea suficiente. Si no lo es, debe lanzar una excepción y mostrar un mensaje de error. | Alta |
| RF-03.4 | Cada transacción debe registrar automáticamente la **fecha y hora** del movimiento. | Alta |
| RF-03.5 | El sistema debe permitir **listar todas las transacciones** registradas en orden cronológico. | Media |

---

### RF-04 — API REST

| ID | Requerimiento | Prioridad |
|---|---|---|
| RF-04.1 | El sistema debe exponer un endpoint `GET /api/inventario/productos` que devuelva la lista completa de productos en formato JSON. | Alta |
| RF-04.2 | El sistema debe exponer un endpoint `GET /api/inventario/productos/{id}` que devuelva un producto específico por su ID. | Alta |
| RF-04.3 | El sistema debe exponer un endpoint `POST /api/inventario/transacciones` que permita registrar una transacción vía REST (JSON). | Alta |
| RF-04.4 | El sistema debe exponer un endpoint `GET /api/inventario/transacciones` que devuelva la lista de todas las transacciones. | Media |
| RF-04.5 | La API REST debe devolver códigos HTTP estándar: 200 (OK), 201 (Created), 404 (Not Found), 400 (Bad Request). | Alta |

---

### RF-05 — Internacionalización (i18n)

| ID | Requerimiento | Prioridad |
|---|---|---|
| RF-05.1 | El sistema debe soportar la interfaz en **Español (es)** como idioma predeterminado. | Alta |
| RF-05.2 | El sistema debe soportar la interfaz en **Inglés (en)** como idioma alternativo. | Media |
| RF-05.3 | El usuario debe poder **cambiar el idioma** desde la barra de navegación mediante los parámetros `?lang=es` y `?lang=en`. | Media |

---

### RF-06 — Manejo de Errores

| ID | Requerimiento | Prioridad |
|---|---|---|
| RF-06.1 | El sistema debe mostrar mensajes de validación descriptivos cuando un formulario tenga datos inválidos. | Alta |
| RF-06.2 | El sistema debe manejar globalmente las excepciones de negocio (`StockInsuficienteException`, `RecursoNoEncontradoException`) y devolver respuestas HTTP apropiadas. | Alta |
| RF-06.3 | El sistema debe mostrar mensajes de éxito (flash messages) después de operaciones exitosas (crear, editar, eliminar). | Media |

---

## 2. Requerimientos No Funcionales (RNF)

Los requerimientos no funcionales describen **cómo debe comportarse** el sistema.

### RNF-01 — Rendimiento

| ID | Requerimiento | Métrica |
|---|---|---|
| RNF-01.1 | El sistema debe responder a las solicitudes de la interfaz web en menos de **2 segundos** bajo carga normal. | < 2s por request |
| RNF-01.2 | Las consultas a la base de datos deben usar `JOIN FETCH` para evitar el problema N+1. | Verificado en código |

---

### RNF-02 — Disponibilidad y Mantenibilidad

| ID | Requerimiento |
|---|---|
| RNF-02.1 | El código debe seguir las convenciones de nomenclatura estándar de Java (camelCase, PascalCase). |
| RNF-02.2 | Cada clase debe tener documentación JavaDoc en su encabezado. |
| RNF-02.3 | El sistema debe separar claramente las capas: Controller, Service, Repository, Entity, DTO. |

---

### RNF-03 — Calidad del Código

| ID | Requerimiento | Herramienta |
|---|---|---|
| RNF-03.1 | La cobertura de pruebas unitarias debe ser **≥ 80%** en las capas de servicio y controlador. | JaCoCo |
| RNF-03.2 | El código no debe tener vulnerabilidades de seguridad críticas o altas. | Checkmarx / SonarQube |
| RNF-03.3 | El análisis estático de código no debe reportar issues de severidad **Critical** o **Blocker**. | SonarQube |

---

### RNF-04 — Compatibilidad

| ID | Requerimiento |
|---|---|
| RNF-04.1 | El sistema debe ser compatible con los navegadores modernos: Chrome, Firefox, Edge (últimas 2 versiones). |
| RNF-04.2 | La interfaz debe ser **responsiva** y adaptarse correctamente a pantallas de escritorio y tableta. |
| RNF-04.3 | El sistema debe funcionar con **Java 17 LTS** y ser compilado con **Maven 3.x**. |

---

### RNF-05 — Seguridad

| ID | Requerimiento |
|---|---|
| RNF-05.1 | Las credenciales de la base de datos NO deben estar embebidas en el código fuente (usar `application.properties` o variables de entorno). |
| RNF-05.2 | Los formularios HTML deben utilizar el método POST para operaciones de modificación. |
| RNF-05.3 | Los campos de entrada deben validarse tanto en el cliente (HTML5) como en el servidor (Jakarta Validation). |

---

### RNF-06 — Portabilidad y Despliegue

| ID | Requerimiento |
|---|---|
| RNF-06.1 | El sistema debe poder empaquetarse como un JAR ejecutable autónomo (`mvn package`). |
| RNF-06.2 | El despliegue debe automatizarse mediante un pipeline Jenkins con al menos 4 stages: Checkout, Build+Test, Scan, Deploy. |
| RNF-06.3 | El sistema debe poder ejecutarse en cualquier entorno con JDK 17 y MariaDB/MySQL instalados. |
