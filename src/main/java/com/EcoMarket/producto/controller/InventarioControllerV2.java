package com.EcoMarket.producto.controller;

import com.EcoMarket.producto.Assemblers.InventarioAssembler;
import com.EcoMarket.producto.model.Inventario;
import com.EcoMarket.producto.service.InventarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v2/inventario")
@Tag(name = "Inventario", description = "Gestión del stock de productos")
public class InventarioControllerV2 {

    @Autowired
    private InventarioService inventarioService;

    @Autowired
    private InventarioAssembler assembler;
    
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

    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<CollectionModel<EntityModel<Inventario>>> Listar(){
        List<Inventario> inventarios = inventarioService.listarTodos();
        if (inventarios.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        List<EntityModel<Inventario>> inventariosModel = inventarios.stream()
        .map(assembler::toModel)
        .collect(Collectors.toList());
        CollectionModel<EntityModel<Inventario>> collectionModel =
        CollectionModel.of(inventariosModel,
        linkTo(methodOn(InventarioControllerV2.class).Listar()).withSelfRel());
        return ResponseEntity.ok(collectionModel);
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

    @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<Inventario>> obtenerPorId(@PathVariable Long id) {
        Optional<Inventario> inventario = inventarioService.obtenerPorId(id);
        return inventario
        .<EntityModel<Inventario>>map(inv -> EntityModel.of(inv,
        linkTo(methodOn(InventarioControllerV2.class).obtenerPorId(id)).withSelfRel(),
        linkTo(methodOn(InventarioControllerV2.class).Listar()).withRel("inventarios")
    ))
    .map(ResponseEntity::ok)
    .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<Inventario>> crearInventario(@RequestBody Inventario inventario){
        Inventario nuevoInventario = inventarioService.guardar(inventario);
        return ResponseEntity
        .created(linkTo(methodOn(InventarioControllerV2.class).obtenerPorId(nuevoInventario.getId())).toUri())
        .body(assembler.toModel(nuevoInventario));  
    }
    
    @Operation(
        summary = "Actualizar un registro de inventario",
        description = "Modifica los datos de un inventario existente dado su ID"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Inventario actualizado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Inventario no encontrado")
    })
    @PutMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<Inventario>> actualizar(@PathVariable Long id, @RequestBody Inventario inventario){
        Optional<Inventario> actualizado = inventarioService.actualizar(id, inventario);
        if (actualizado.isPresent()){
            Inventario inventario2 = actualizado.get();
            EntityModel<Inventario> resource = EntityModel.of(inventario2,
            linkTo(methodOn(InventarioControllerV2.class).actualizar(id, inventario)).withSelfRel(),
                linkTo(methodOn(InventarioControllerV2.class).obtenerPorId(id)).withRel("Inventario"),
                linkTo(methodOn(InventarioControllerV2.class).Listar()).withRel("Inventarios"),
                linkTo(methodOn(InventarioControllerV2.class).eliminar(id)).withRel("eliminar"));
            return ResponseEntity.ok(resource);
        }
        return ResponseEntity.status(404).body(null);
    }
    @Operation(
        summary = "Eliminar un registro de inventario",
        description = "Elimina un inventario por su ID si existe"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Inventario eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Inventario no encontrado")
    })
    @DeleteMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<String> eliminar(@PathVariable Long id){
        boolean eliminado = inventarioService.eliminar(id);
        if (eliminado) {
                return ResponseEntity
                .ok("Inventario eliminado correctamente");
        } else{
            return ResponseEntity
            .status(404).body("Inventario no fue eliminado");
        }
    }
    public void setInventarioService(InventarioService inventarioService) {
    this.inventarioService = inventarioService;
    }
    
}