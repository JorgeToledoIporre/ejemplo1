package com.EcoMarket.producto.controller;

import com.EcoMarket.producto.model.Producto;
import com.EcoMarket.producto.service.ProductoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @RestController: Combina @Controller + @ResponseBody (respuestas JSON automáticas)
 * @RequestMapping: Prefijo común para todas las rutas (/api/v1/productos)
 * @Tag: Documentación de Swagger - agrupa endpoints bajo "Productos"
 */

@RestController
@RequestMapping("/api/v1/productos")
@Tag(name = "Productos", description = "Operaciones relacionadas con productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;
    @Operation(
        summary = "Listar todos los productos",
        description = "Obtiene una lista de todos los productos disponibles"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Lista de productos",
        content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(value = """
                [
                  {
                    "id": 1,
                    "codigo": "P001",
                    "nombre": "Producto de prueba",
                    "descripcion": "Un producto para pruebas",
                    "precio": 12.99,
                    "categoria": "Alimentos",
                    "activo": true
                  }
                ]
            """)
        )
    )
    @GetMapping
    public ResponseEntity<List<Producto>> listarTodos() {
        return ResponseEntity.ok(productoService.listarTodos());
    }
    @Operation(
        summary = "Obtener producto por ID",
        description = "Devuelve un producto específico si existe"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto encontrado"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerPorId(@PathVariable Long id) {
        return productoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @Operation(
        summary = "Crear un nuevo producto",
        description = "Guarda un nuevo producto con los datos enviados"
    )
    @ApiResponse(
        responseCode = "201",
        description = "Producto creado exitosamente",
        content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(value = """
                {
                  "codigo": "P002",
                  "nombre": "Naranja",
                  "descripcion": "Fruta cítrica",
                  "precio": 1.5,
                  "categoria": "Frutas",
                  "activo": true
                }
            """)
        )
    )
    @PostMapping
    public ResponseEntity<Producto> crearProducto(@RequestBody Producto producto) {
        Producto creado = productoService.guardar(producto);
        return ResponseEntity.status(201).body(creado);
    }
    @Operation(
        summary = "Actualizar un producto",
        description = "Modifica los datos de un producto existente dado su ID"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto actualizado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizar(@PathVariable Long id, @RequestBody Producto producto) {
        return productoService.actualizar(id, producto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @Operation(
        summary = "Eliminar un producto",
        description = "Elimina un producto existente por su ID"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Producto eliminado correctamente"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (productoService.eliminar(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
