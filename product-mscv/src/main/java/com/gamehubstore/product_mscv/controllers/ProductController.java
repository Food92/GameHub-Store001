package com.gamehubstore.product_mscv.controllers;

import com.gamehubstore.product_mscv.models.Product;
import com.gamehubstore.product_mscv.models.dtos.ProductDTO;
import com.gamehubstore.product_mscv.repositories.ProductRepository;
import com.gamehubstore.product_mscv.services.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Controlador Producto", description = "Metodo CRUD para producto")
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    // 1. Crear un producto
    @Operation(
            summary = "Registrar un nuevo producto",
            description = "Recibe los datos de un artículo gamer (notebook, GPU, consola) and lo guarda en la base de datos H2 con las validaciones correspondientes."
    )
    @ApiResponse(
            responseCode = "201",
            description = "Producto creado exitosamente en el catálogo.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductDTO.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Error de validación en los datos enviados (Bad Request).",
            content = @Content(mediaType = "application/json")
    )
    @PostMapping
    public ResponseEntity<Product> save(@Valid @RequestBody Product product) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.save(product));
    }

    // 2. Listar los productos
    @Operation(
            summary = "Obtener todo el catálogo",
            description = "Retorna una lista completa con todos los productos registrados en la tienda en formato ProductDTO."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Lista de productos obtenida correctamente.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductDTO.class))
    )
    @GetMapping
    public ResponseEntity<List<ProductDTO>> findAll() {
        return ResponseEntity.ok(productService.findAll());
    }

    // 3. Buscar por ID
    @Operation(
            summary = "Buscar producto por ID",
            description = "Busca en el inventario un producto específico utilizando su identificador único numérico."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Producto encontrado con éxito.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductDTO.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "El ID del producto buscado no existe en el sistema.",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(type = "object", example = "{\"codigo\": \"NOT_FOUND\", \"mensaje\": \"El producto solicitado no existe en el sistema.\", \"detalles\": \"uri=/api/v1/products/\"}")
            )
    )
    @GetMapping("/{id}")
    public ResponseEntity<Product> findById(
            @Parameter(
                    name = "id",
                    description = "Identificador único y numérico del producto registrado en la tienda",
                    example = "1",
                    required = true
            )
            @PathVariable Long id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    // 4. Listar por categoría
    @Operation(
            summary = "Filtrar productos por Categoría",
            description = "Busca todos los productos asociados a un ID de categoría específico."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Lista filtrada por categoría devuelta con éxito.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductDTO.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "El ID de la categoría buscada no existe en el sistema.",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(type = "object", example = "{\"codigo\": \"NOT_FOUND\", \"mensaje\": \"La categoría solicitada o sus productos no existen en el sistema.\", \"detalles\": \"uri=/api/v1/products/category/\"}")
            )
    )
    @GetMapping("/category/{idCategory}")
    public ResponseEntity<List<Product>> findByCategory(
            @Parameter(
                    name = "idCategory",
                    description = "ID numérico de la categoría a filtrar en el catálogo",
                    example = "2",
                    required = true
            )
            @PathVariable Long idCategory) {
        return ResponseEntity.ok(productService.findByIdCategory(idCategory));
    }

    // 5. Listar por marca
    @Operation(
            summary = "Filtrar productos por Marca/Fabricante",
            description = "Busca artículos filtrando por su fabricante comercial (ej: Sony, ASUS, Nintendo)."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Lista filtrada por marca devuelta con éxito.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductDTO.class))
    )
    @GetMapping("/marca/{marca}")
    public ResponseEntity<List<Product>> findByMarca(
            @Parameter(
                    name = "marca",
                    description = "Nombre o marca comercial del fabricante del producto",
                    example = "ASUS",
                    required = true
            )
            @PathVariable String marca) {
        return ResponseEntity.ok(productService.findByMarca(marca));
    }

    // 6. Listar por estado
    @Operation(
            summary = "Filtrar productos por Estado (Disponibilidad)",
            description = "Retorna los productos filtrándolos por su estado de disponibilidad usando valores lógicos (true/false)."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Lista filtrada por estado devuelta con éxito.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductDTO.class))
    )
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Product>> findByEstado(
            @Parameter(
                    name = "estado",
                    description = "Estado lógico de disponibilidad (true: Disponible / false: Inactivo)",
                    example = "true",
                    required = true
            )
            @PathVariable Boolean estado) {
        return ResponseEntity.ok(productService.findByEstado(estado));
    }

    // 7. Actualizar producto
    @Operation(
            summary = "Actualizar un producto existente",
            description = "Reemplaza por completo los datos de un producto almacenado utilizando su ID y un nuevo cuerpo JSON validado."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Producto actualizado correctamente.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductDTO.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Datos de actualización inválidos.",
            content = @Content(mediaType = "application/json")
    )
    @ApiResponse(
            responseCode = "404",
            description = "No se encontró el producto con el ID especificado para ser modificado.",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(type = "object", example = "{\"codigo\": \"NOT_FOUND\", \"mensaje\": \"El producto a modificar no existe en el sistema.\", \"detalles\": \"uri=/api/v1/products/\"}")
            )
    )
    @PutMapping("/{id}")
    public ResponseEntity<Product> update(
            @Parameter(
                    name = "id",
                    description = "ID del producto que se desea modificar en el inventario",
                    example = "1",
                    required = true
            )
            @PathVariable Long id,
            @Valid @RequestBody Product product) {
        return ResponseEntity.ok(productService.update(product, id));
    }

    // 8. Eliminar producto
    @Operation(
            summary = "Eliminar un producto del inventario",
            description = "Borra físicamente y de forma permanente el registro del producto de la base de datos local H2."
    )
    @ApiResponse(
            responseCode = "204",
            description = "Producto eliminado exitosamente (No Content).",
            content = @Content
    )
    @ApiResponse(
            responseCode = "404",
            description = "El producto que se intenta eliminar no existe en el sistema.",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(type = "object", example = "{\"codigo\": \"NOT_FOUND\", \"mensaje\": \"El producto a eliminar no fue encontrado.\", \"detalles\": \"uri=/api/v1/products/\"}")
            )
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(
                    name = "id",
                    description = "ID del producto que se eliminará permanentemente de la base de datos",
                    example = "1",
                    required = true
            )
            @PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // 9. Desactivar producto
    @Operation(
            summary = "Desactivar un producto (Borrado Lógico)",
            description = "Cambia parcialmente el estado del producto a inactivo (false) mediante una petición PATCH."
    )
    @ApiResponse(
            responseCode = "204",
            description = "Producto desactivado con éxito (No Content).",
            content = @Content
    )
    @ApiResponse(
            responseCode = "404",
            description = "El producto que se intenta desactivar no fue encontrado.",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(type = "object", example = "{\"codigo\": \"NOT_FOUND\", \"mensaje\": \"El producto a desactivar no existe.\", \"detalles\": \"uri=/api/v1/products/\"}")
            )
    )
    @PatchMapping("/{id}")
    public ResponseEntity<Void> desactivar(
            @Parameter(
                    name = "id",
                    description = "ID del producto al que se le aplicará la baja lógica de visualización",
                    example = "1",
                    required = true
            )
            @PathVariable Long id){
        productService.desactivar(id);
        return ResponseEntity.noContent().build();
    }
}