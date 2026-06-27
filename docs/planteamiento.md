# Planteamiento del Problema — Sistema de Inventario Transaccional

**Unidad de Aprendizaje:** Diseño de Sistemas  
**Institución:** Universidad  
**Docente:** Ing. Julio Cesar Sarandingua Quintero  
**Tecnología:** Java 17 + Spring Boot 3.3 + Thymeleaf + JPA/Hibernate + MariaDB

---

## 1. Contexto y Antecedentes

Muchas organizaciones, especialmente pequeñas y medianas empresas (PYMES), gestionan su inventario de manera manual o con hojas de cálculo. Esta práctica genera problemas como:

- **Errores humanos** en el registro de entradas y salidas de productos.
- **Falta de trazabilidad**: no existe un historial de movimientos que permita auditar el stock.
- **Desactualización del inventario en tiempo real**, provocando sobre-stock o desabasto.
- **Dificultad para generar reportes** de manera ágil y confiable.
- **Acceso limitado**: la información queda atrapada en archivos locales sin posibilidad de acceso multi-usuario.

---

## 2. Definición del Problema

> **¿Cómo diseñar e implementar un sistema de información que permita gestionar de forma confiable el inventario de productos de una organización, registrando entradas y salidas de stock con validación de reglas de negocio, y accesible desde un navegador web?**

La ausencia de un sistema automatizado de gestión de inventario ocasiona:

1. **Pérdidas económicas** por no detectar a tiempo el agotamiento de productos críticos.
2. **Ineficiencia operativa** al no poder consultar el estado del inventario en tiempo real.
3. **Falta de control** sobre los movimientos de mercancía (quién hizo qué y cuándo).
4. **Incapacidad de integración** con otros sistemas (e-commerce, contabilidad, etc.) al no existir una API estandarizada.

---

## 3. Justificación

El desarrollo de un **Sistema de Inventario Transaccional** basado en arquitectura **MVC monolítica** con Spring Boot justifica su existencia por:

- **Aplicación de conocimientos académicos**: integra los conceptos de Diseño de Sistemas vistos en clase (patrones de diseño, capas de arquitectura, ORM, REST).
- **Solución real y funcional**: el sistema puede ser desplegado en un entorno local o en la nube para uso inmediato.
- **Base extensible**: la arquitectura modular permite añadir módulos futuros (reportes, autenticación, roles) sin reescribir el núcleo.
- **Buenas prácticas DevOps**: la integración con Jenkins, SonarQube y JaCoCo demuestra madurez en el ciclo de desarrollo profesional.

---

## 4. Objetivos

### 4.1 Objetivo General

Desarrollar un Sistema de Inventario Transaccional web utilizando la arquitectura MVC monolítica con Spring Boot, que permita gestionar productos y registrar movimientos de stock con validación de reglas de negocio.

### 4.2 Objetivos Específicos

1. Diseñar e implementar el modelo de datos relacional para productos, categorías y transacciones de inventario.
2. Desarrollar la capa de negocio que valide las reglas de stock (verificar disponibilidad antes de registrar salidas).
3. Implementar una interfaz de usuario web con Thymeleaf que soporte internacionalización (español/inglés).
4. Exponer una API REST bajo `/api/inventario` para permitir integración con sistemas externos.
5. Configurar un pipeline CI/CD con Jenkins que automatice las fases de build, pruebas, análisis de calidad y despliegue.
6. Alcanzar una cobertura de pruebas unitarias mínima del 80% mediante JUnit 5, Mockito y JaCoCo.

---

## 5. Alcance del Sistema

### Incluido en el alcance:

| Funcionalidad | Descripción |
|---|---|
| Gestión de Productos | CRUD completo (Crear, Leer, Actualizar, Eliminar) |
| Gestión de Categorías | Clasificación de productos por categoría |
| Registro de Transacciones | Entradas y salidas de stock con validación |
| Control de Stock | Verificación automática de stock disponible |
| Alertas visuales | Indicador de stock bajo (≤5 unidades) |
| API REST | Endpoints JSON para integración externa |
| Internacionalización | Interfaz en Español e Inglés |
| Pipeline CI/CD | Automatización con Jenkins (5 stages) |

### Fuera del alcance:

- Autenticación y autorización de usuarios (login/roles).
- Módulo de reportes y estadísticas avanzadas.
- Integración con sistemas de punto de venta (POS).
- Notificaciones por correo electrónico o SMS.

---

## 6. Tecnologías Utilizadas

| Capa | Tecnología | Versión |
|---|---|---|
| Lenguaje | Java | 17 LTS |
| Framework Backend | Spring Boot | 3.3.0 |
| Motor de Plantillas | Thymeleaf | 3.x |
| Persistencia | Spring Data JPA + Hibernate | 6.x |
| Base de Datos | MariaDB / MySQL | 10.x / 8.x |
| Base de Datos (pruebas) | H2 (en memoria) | 2.x |
| Validaciones | Jakarta Validation | 3.x |
| Reducción de código | Lombok | 1.18.x |
| Pruebas Unitarias | JUnit 5 + Mockito | 5.x / 5.x |
| Cobertura de código | JaCoCo | 0.8.12 |
| CI/CD | Jenkins (pipeline declarativo) | LTS |
| Análisis de calidad | SonarQube | 10.x |
| Análisis de seguridad | Checkmarx | CLI |
| Gestor de dependencias | Maven | 3.x |
