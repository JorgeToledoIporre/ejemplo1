package com.EcoMarket.producto.controller;

import com.EcoMarket.producto.Assemblers.ProductoModelAssembler;
import com.EcoMarket.producto.model.Producto;
import com.EcoMarket.producto.service.ProductoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import java.util.Optional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @RestController: Combina @Controller + @ResponseBody (respuestas JSON automáticas)
 * @RequestMapping: Prefijo común para todas las rutas (/api/v1/productos)
 * @Tag: Documentación de Swagger - agrupa endpoints bajo "Productos"
 */

@RestController
@RequestMapping("/api/v2/productos")
@Tag(name = "Productos", description = "Operaciones relacionadas con productos")
public class ProductoControllerV2 {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private ProductoModelAssembler assembler;

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
    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<CollectionModel<EntityModel<Producto>>> Listar(){
        List<Producto> productos = productoService.listarTodos();
        if (productos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        List<EntityModel<Producto>> productosModel = productos.stream()
        .map(assembler::toModel)
        .collect(Collectors.toList());
        CollectionModel<EntityModel<Producto>> collectionModel =
        CollectionModel.of(productosModel,
        linkTo(methodOn(ProductoControllerV2.class).Listar()).withSelfRel());
        return ResponseEntity.ok(collectionModel);
    }
    @Operation(
        summary = "Obtener producto por ID",
        description = "Devuelve un producto específico si existe"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto encontrado"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<Producto>> obtenerPorId(@PathVariable Long id) {
    return productoService.obtenerPorId(id)
        .map(producto -> ResponseEntity.ok(assembler.toModel(producto)))
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
    @PostMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<Producto>> crearProducto(@RequestBody Producto producto){
        Producto nuevoProducto = productoService.guardar(producto);
        return ResponseEntity
        .created(linkTo(methodOn(ProductoControllerV2.class).obtenerPorId(nuevoProducto.getId())).toUri())
        .body(assembler.toModel(nuevoProducto));  
    }
    @Operation(
        summary = "Actualizar un producto",
        description = "Modifica los datos de un producto existente dado su ID"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto actualizado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @PutMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<Producto>> actualizar(@PathVariable Long id, @RequestBody Producto producto){
        Optional<Producto> actualizado = productoService.actualizar(id, producto);
        if (actualizado.isPresent()){
            Producto producto2 = actualizado.get();
            EntityModel<Producto> resource = EntityModel.of(producto2,
            linkTo(methodOn(ProductoControllerV2.class).actualizar(id, producto)).withSelfRel(),
                linkTo(methodOn(ProductoControllerV2.class).obtenerPorId(id)).withRel("mensaje"),
                linkTo(methodOn(ProductoControllerV2.class).Listar()).withRel("mensajes"),
                linkTo(methodOn(ProductoControllerV2.class).eliminar(id)).withRel("eliminar"));
            return ResponseEntity.ok(resource);
        }
        return ResponseEntity.status(404).body(null);
    }
    @Operation(
        summary = "Eliminar un producto",
        description = "Elimina un producto existente por su ID"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Producto eliminado correctamente"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @DeleteMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<?> eliminar(@PathVariable Long id){
        boolean eliminado = productoService.eliminar(id);
        if (eliminado) {
                return ResponseEntity.noContent().build();
        } else{
            return ResponseEntity
            .status(404).body("Producto no fue eliminado");
        }
    }
    public void setProductoService(ProductoService productoService2) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setProductoService'");
    }
}