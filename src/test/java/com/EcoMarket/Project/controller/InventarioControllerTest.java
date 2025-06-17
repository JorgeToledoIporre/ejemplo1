package com.EcoMarket.producto.controller;

import com.EcoMarket.producto.model.Inventario;
import com.EcoMarket.producto.service.InventarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;


import java.util.List;

@RestController
@RequestMapping("/api/v1/inventario")
@Tag(name = "Inventario", description = "Gestión del stock de productos")
public class InventarioController {

    @Autowired
    private InventarioService inventarioService;
    @Operation(
        summary = "Listar todo el inventario",
        description = "Devuelve todos los registros de inventario"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Lista de inventario",
        content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(value = """
                [
                  {
                    "id": 1,
                    "producto": {
                      "id": 1,
                      "nombre": "Manzana"
                    },
                    "cantidad": 100,
                    "cantidadMinima": 20,
                    "ubicacion": "Almacén A",
                    "estado": "DISPONIBLE"
                  }
                ]
            """)
        )
    )

    @GetMapping
    public ResponseEntity<List<Inventario>> listarTodos() {
        return ResponseEntity.ok(inventarioService.listarTodos());
    }
    @Operation(
        summary = "Crear un registro de inventario",
        description = "Asocia un producto con stock en una ubicación determinada"
    )
    @ApiResponse(
        responseCode = "201",
        description = "Inventario creado exitosamente",
        content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(value = """
                {
                  "producto": { "id": 1 },
                  "cantidad": 50,
                  "cantidadMinima": 10,
                  "ubicacion": "Depósito B"
                }
            """)
        )
    )

    @GetMapping("/{id}")
    public ResponseEntity<Inventario> obtenerPorId(@PathVariable Long id) {
        return inventarioService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Inventario> crear(@RequestBody Inventario inventario) {
        Inventario creado = inventarioService.guardar(inventario);
        return ResponseEntity.status(201).body(creado);
    }
    @Operation(
        summary = "Actualizar un registro de inventario",
        description = "Modifica los datos de un inventario existente dado su ID"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Inventario actualizado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Inventario no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Inventario> actualizar(@PathVariable Long id, @RequestBody Inventario inventario) {
        return inventarioService.actualizar(id, inventario)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @Operation(
        summary = "Eliminar un registro de inventario",
        description = "Elimina un inventario por su ID si existe"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Inventario eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Inventario no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (inventarioService.eliminar(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
