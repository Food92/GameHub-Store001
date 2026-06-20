package com.gamehubstore.category_mscv.controllers;

import com.gamehubstore.category_mscv.models.Category;
import com.gamehubstore.category_mscv.models.dtos.CategoryDTO;
import com.gamehubstore.category_mscv.services.CategoryService;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Controlador Producto", description = "Metodo CRUD para producto")
@RestController
@Validated
@RequestMapping("/api/v1/categories")
public class ControllerCategory {

    @Autowired
    private CategoryService categoryService;

    // 1. Listar todas las categorías
    @Operation(
            summary = "Obtener todas las categorías",
            description = "Retorna una lista completa con todas las categorías de la tienda. Útil para cargar menús de navegación o filtros globales."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Lista de categorías obtenida correctamente.",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CategoryDTO.class)
            )
    )
    @GetMapping
    public ResponseEntity<List<Category>> findAll() {
        return ResponseEntity.ok(categoryService.findAll());
    }

    // 2. Buscar por ID (ORDENADO: 200 ÉXITO -> 404 ERROR CON JSON INLINE Y @PARAMETER FORMATEADO)
    @Operation(
            summary = "Buscar categoría por ID",
            description = "Busca en el sistema una categoría específica utilizando su identificador único numérico."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Categoría encontrada con éxito.",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CategoryDTO.class)
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "El ID de la categoría buscada no existe en el sistema.",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(type = "object", example = "{\"codigo\": \"NOT_FOUND\", \"mensaje\": \"La categoría solicitada no existe en el catálogo.\", \"detalles\": \"uri=/api/v1/categories/\"}")
            )
    )
    @GetMapping("/{id}")
    public ResponseEntity<Category> findById(
            @Parameter(
                    name = "id",
                    description = "Identificador único y numérico de la categoría en el catálogo",
                    example = "1",
                    required = true
            )
            @PathVariable Long id) {
        return ResponseEntity.ok(categoryService.findById(id));
    }

    // 3. Crear una categoría
    @Operation(
            summary = "Registrar una nueva categoría",
            description = "Recibe los datos de una clasificación del catálogo (ej: Notebooks, Consolas) y la guarda en la base de datos H2 con sus respectivas validaciones."
    )
    @ApiResponse(
            responseCode = "201",
            description = "Categoría creada exitosamente en el catálogo.",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CategoryDTO.class)
            )
    )
    @ApiResponse(responseCode = "400", description = "Error de validación en los datos enviados (Bad Request).")
    @PostMapping
    public ResponseEntity<Category> create(@Valid @RequestBody Category category) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.save(category));
    }

    // 4. Actualizar categoría (ORDENADO: 200 ÉXITO -> 400 MAL FORMATO -> 404 ERROR CON JSON INLINE Y @PARAMETER FORMATEADO)
    @Operation(
            summary = "Actualizar una categoría existente",
            description = "Modifica los datos de una categoría almacenada en H2 localizando su registro a través del ID and actualizando sus campos específicos."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Categoría actualizada correctamente.",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CategoryDTO.class)
            )
    )
    @ApiResponse(responseCode = "400", description = "Datos de actualización inválidos.")
    @ApiResponse(
            responseCode = "404",
            description = "No se encontró ninguna categoría con el ID especificado.",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(type = "object", example = "{\"codigo\": \"NOT_FOUND\", \"mensaje\": \"La categoría a modificar no existe en el catálogo.\", \"detalles\": \"uri=/api/v1/categories/\"}")
            )
    )
    @PutMapping("/{id}")
    public ResponseEntity<Category> update(
            @Parameter(
                    name = "id",
                    description = "ID de la categoría que se desea modificar",
                    example = "1",
                    required = true
            )
            @PathVariable Long id,
            @Valid @RequestBody Category category) {
        Category updated = categoryService.update(id, category.getNombreCategory(), category.getDescripcionCategory());
        return ResponseEntity.ok(updated);
    }

    // 5. Eliminar/Desactivar categoría (ORDENADO: 204 ÉXITO -> 404 ERROR CON JSON INLINE Y @PARAMETER FORMATEADO)
    @Operation(
            summary = "Desactivar una categoría del catálogo",
            description = "Aplica un borrado lógico o desactivación del registro de la categoría utilizando su ID mediante una petición DELETE."
    )
    @ApiResponse(
            responseCode = "204",
            description = "Categoría desactivada exitosamente (No Content).",
            content = @Content
    )
    @ApiResponse(
            responseCode = "404",
            description = "La categoría que se intenta desactivar no existe en el sistema.",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(type = "object", example = "{\"codigo\": \"NOT_FOUND\", \"mensaje\": \"La categoría a desactivar no fue encontrada.\", \"detalles\": \"uri=/api/v1/categories/\"}")
            )
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(
            @Parameter(
                    name = "id",
                    description = "ID de la categoría a la que se le aplicará la desactivación o baja lógica",
                    example = "1",
                    required = true
            )
            @PathVariable Long id) {
        categoryService.desactivar(id);
        return ResponseEntity.noContent().build();
    }

}
