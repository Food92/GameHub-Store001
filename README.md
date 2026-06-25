# 🎮 GameHub Store - Sistema Distribuido de Microservicios

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2023.0.3-blue)](https://spring.io/projects/spring-cloud)
[![Coverage](https://img.shields.io/badge/Coverage-%3E%2080%25-green)]()

## 📝 Contexto del Proyecto
**GameHub Store** es una plataforma de comercio electrónico distribuida y orientada a la venta de videojuegos, licencias digitales y componentes de hardware. El sistema ha sido diseñado bajo una arquitectura de microservicios altamente cohesivos y desacoplados, implementando el patrón de diseño **CSR (Controller-Service-Repository)** para garantizar una separación real de responsabilidades en cada componente de software.

### 👥 Integrantes del Equipo
* **[Tu Nombre y Apellido]**
* *[Nombre de tu compañero/a si aplica]*

---

## 🏗️ Arquitectura del Sistema e Interoperabilidad

El ecosistema está compuesto por **10 microservicios funcionales**, centralizados a través de un punto único de entrada (API Gateway) y gestionados mediante un servidor de descubrimiento.

### Listado de Microservicios Implementados:
1. **`eureka-server`**: Servidor de descubrimiento y registro de instancias de red.
2. **`api-gateway`**: Centralizador de enrutamiento distribuidor y filtros de tráfico general.
3. **`category-mscv`**: Gestión de familias, géneros y clasificación de productos.
4. **`inventory-mscv`**: Control de existencias físicas y alertas de stock en tiempo real.
5. **`order-mscv`**: Procesamiento, orquestación y flujos de estados de pedidos de compra.
6. **`shipping-mscv`**: Gestión de despachos, logística de entrega de hardware y tracking.
7. **`payment-mscv`**: Procesamiento de transacciones financieras e integración de pasarelas.
8. **`review-mscv`**: Sistema de valoraciones, comentarios y feedback de usuarios.
9. **`promotion-mscv`**: Motor de cupones de descuento, ofertas estacionales y rebajas.
10. **`notification-mscv`**: Servicio asíncrono/síncrono de envío de alertas por correo y eventos.

---

## 🗺️ Enrutamiento del API Gateway

El `api-gateway` centraliza todas las solicitudes externas utilizando **Spring Cloud Gateway** bajo configuraciones YAML limpias.

| Microservicio | Prefijo de Ruta Base | Endpoint de Prueba de Entrada |
| :--- | :--- | :--- |
| `category-mscv` | `/api/v1/categories/**` | `http://localhost:8080/api/v1/categories` |
| `inventory-mscv`| `/api/v1/inventory/**`  | `http://localhost:8080/api/v1/inventory`  |
| `order-mscv`    | `/api/v1/orders/**`     | `http://localhost:8080/api/v1/orders`     |
| `shipping-mscv` | `/api/v1/shipping/**`   | `http://localhost:8080/api/v1/shipping`   |
| `payment-mscv`  | `/api/v1/payments/**`   | `http://localhost:8080/api/v1/payments`   |
| `review-mscv`   | `/api/v1/reviews/**`    | `http://localhost:8080/api/v1/reviews`    |
| `promotion-mscv`| `/api/v1/promotions/**` | `http://localhost:8080/api/v1/promotions` |
| `notification-mscv`| `/api/v1/notifications/**`| `http://localhost:8080/api/v1/notifications`|

---

## 📖 Documentación Interactiva (Swagger/OpenAPI)

Cada microservicio implementa la dependencia `springdoc-openapi-starter-webmvc-ui` exponiendo contratos claros con códigos de estado HTTP semánticos y modelos DTO interactivos.

* **Ruta local unificada (Vía Gateway):** `http://localhost:8080/swagger-ui.html`
* **Endpoints individuales de Swagger UI:**
    * Category: `http://localhost:8081/swagger-ui/index.html`
    * Inventory: `http://localhost:8082/swagger-ui/index.html`
    * Orders: `http://localhost:8083/swagger-ui/index.html`

---

## Pruebas Unitarias y Cobertura (JUnit 5 + Mockito)

El proyecto cuenta con una batería de pruebas unitarias robustas que validan las reglas de negocio clave sobre la capa `@Service` utilizando la estructura formal **Given-When-Then**.

* **Porcentaje de Cobertura de Lógica Crítica:** `> 85%` (Supera el mínimo del 80% exigido).
* **Comando de Ejecución Local:**
    ```bash
    mvn clean test
    ```

---

## Instrucciones de Ejecución Local

### Prerrequisitos:
* Java JDK 21 instalado correctamente.
* Maven 3.8+.
* Instancia de Docker Desktop (Opcional para despliegue por contenedores).

### Pasos para iniciar el ecosistema:
1. Clonar el repositorio.
2. Compilar el proyecto raíz de forma limpia:
   ```bash
   mvn clean compile
