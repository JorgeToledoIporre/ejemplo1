package com.EcoMarket.producto.controller;

import com.EcoMarket.producto.Assemblers.ProductoModelAssembler;
import com.EcoMarket.producto.model.Producto;
import com.EcoMarket.producto.service.ProductoService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Clase de pruebas unitarias para ProductoControllerV2
 * 
 * Esta clase verifica que el controlador REST funcione correctamente
 * sin depender de la base de datos real (usa mocks).
 */
public class ProductoControllertestv2 {

    // @Mock: Crea objetos falsos (mocks) para simular dependencias
    @Mock
    private ProductoService productoService;  // Simula el servicio de productos

    @Mock
    private ProductoModelAssembler assembler;  // Simula el ensamblador HATEOAS

    // El controlador que vamos a probar
    private ProductoControllerV2 productoController;

    /**
     * @BeforeEach: Este método se ejecuta antes de cada prueba
     * Configura el entorno de prueba creando mocks y el controlador
     */
    @BeforeEach
    void setUp() {
        // Creamos mocks manualmente (objetos falsos para las dependencias)
        productoService = mock(ProductoService.class);
        assembler = mock(ProductoModelAssembler.class);
        productoController = new ProductoControllerV2();

        // Inyectamos las dependencias mockeadas al controlador
        productoController.setProductoService(productoService);
        
        // Usamos reflection para inyectar el assembler (campo privado)
        try {
            var field = ProductoControllerV2.class.getDeclaredField("assembler");
            field.setAccessible(true);  // Permite acceso a campo privado
            field.set(productoController, assembler);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Prueba el método Listar() cuando hay productos
     * Verifica que retorne HTTP 200 OK con la lista de productos
     */
    @Test
    void testListar() {
        // ARRANGE: Preparamos los datos de prueba
        Producto p1 = new Producto();
        Producto p2 = new Producto();
        EntityModel<Producto> em1 = EntityModel.of(p1);  // Wrapper HATEOAS
        EntityModel<Producto> em2 = EntityModel.of(p2);

        // Configuramos el comportamiento de los mocks
        when(productoService.listarTodos()).thenReturn(List.of(p1, p2));
        when(assembler.toModel(p1)).thenReturn(em1);
        when(assembler.toModel(p2)).thenReturn(em2);

        // ACT: Ejecutamos el método que queremos probar
        ResponseEntity<CollectionModel<EntityModel<Producto>>> respuesta = productoController.Listar();
        
        // ASSERT: Verificamos que el resultado sea el esperado
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(2, respuesta.getBody().getContent().size());
    }

    /**
     * Prueba el método Listar() cuando no hay productos
     * Verifica que retorne HTTP 204 NO_CONTENT
     */
    @Test
    void testListar_vacio() {
        // ARRANGE: Configuramos el servicio para retornar lista vacía
        when(productoService.listarTodos()).thenReturn(List.of());

        // ACT: Ejecutamos el método
        ResponseEntity<CollectionModel<EntityModel<Producto>>> respuesta = productoController.Listar();
        
        // ASSERT: Verificamos que retorne NO_CONTENT
        assertEquals(HttpStatus.NO_CONTENT, respuesta.getStatusCode());
    }

    /**
     * Prueba obtener un producto por ID cuando existe
     * Verifica que retorne HTTP 200 OK con el producto
     */
    @Test
    void testObtenerPorId_existente() {
        // ARRANGE: Creamos un producto con ID
        Producto producto = new Producto();
        producto.setId(1L);
        EntityModel<Producto> model = EntityModel.of(producto);

        // Configuramos los mocks para simular que el producto existe
        when(productoService.obtenerPorId(1L)).thenReturn(Optional.of(producto));
        when(assembler.toModel(producto)).thenReturn(model);

        // ACT: Ejecutamos el método
        ResponseEntity<EntityModel<Producto>> respuesta = productoController.obtenerPorId(1L);
        
        // ASSERT: Verificamos el resultado
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(1L, respuesta.getBody().getContent().getId());
    }

    /**
     * Prueba obtener un producto por ID cuando NO existe
     * Verifica que retorne HTTP 404 NOT_FOUND
     */
    @Test
    void testObtenerPorId_noExistente() {
        // ARRANGE: Configuramos el servicio para retornar Optional vacío
        when(productoService.obtenerPorId(1L)).thenReturn(Optional.empty());

        // ACT: Ejecutamos el método
        ResponseEntity<EntityModel<Producto>> respuesta = productoController.obtenerPorId(1L);
        
        // ASSERT: Verificamos que retorne NOT_FOUND
        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
    }

    /**
     * Prueba la creación de un nuevo producto
     * Verifica que retorne HTTP 201 CREATED con el producto creado
     */
    @Test
    void testCrearProducto() {
        // ARRANGE: Creamos un producto con todos los datos necesarios
        Producto producto = new Producto();
        producto.setCodigo("P001");
        producto.setNombre("Producto Test");
        producto.setDescripcion("Descripción");
        producto.setPrecio(BigDecimal.valueOf(10));
        producto.setCategoria("Prueba");
        producto.setActivo(true);

        EntityModel<Producto> model = EntityModel.of(producto);

        // Configuramos los mocks
        when(productoService.guardar(any(Producto.class))).thenReturn(producto);
        when(assembler.toModel(any(Producto.class))).thenReturn(model);

        // ACT: Ejecutamos el método de creación
        ResponseEntity<EntityModel<Producto>> respuesta = productoController.crearProducto(producto);
        
        // ASSERT: Verificamos que se haya creado correctamente
        assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());
        assertEquals("P001", respuesta.getBody().getContent().getCodigo());
    }

    /**
     * Prueba la actualización de un producto existente
     * Verifica que retorne HTTP 200 OK con el producto actualizado
     */
    @Test
    void testActualizar_existente() {
        // ARRANGE: Creamos un producto con cambios
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Actualizado");

        // Configuramos el servicio para simular actualización exitosa
        when(productoService.actualizar(eq(1L), any(Producto.class))).thenReturn(Optional.of(producto));

        // ACT: Ejecutamos la actualización
        ResponseEntity<EntityModel<Producto>> respuesta = productoController.actualizar(1L, producto);
        
        // ASSERT: Verificamos el resultado
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals("Actualizado", respuesta.getBody().getContent().getNombre());
    }

    /**
     * Prueba la actualización de un producto que NO existe
     * Verifica que retorne HTTP 404 NOT_FOUND
     */
    @Test
    void testActualizar_noExistente() {
        // ARRANGE: Configuramos el servicio para retornar Optional vacío
        Producto producto = new Producto();
        when(productoService.actualizar(eq(1L), any(Producto.class))).thenReturn(Optional.empty());

        // ACT: Intentamos actualizar
        ResponseEntity<EntityModel<Producto>> respuesta = productoController.actualizar(1L, producto);
        
        // ASSERT: Verificamos que retorne NOT_FOUND
        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
    }

    /**
     * Prueba la eliminación de un producto existente
     * Verifica que retorne HTTP 204 NO_CONTENT
     */
    @Test
    void testEliminar_existente() {
        // ARRANGE: Configuramos el servicio para simular eliminación exitosa
        when(productoService.eliminar(1L)).thenReturn(true);

        // ACT: Ejecutamos la eliminación
        ResponseEntity<?> respuesta = productoController.eliminar(1L);
        
        // ASSERT: Verificamos que retorne NO_CONTENT (eliminación exitosa)
        assertEquals(HttpStatus.NO_CONTENT, respuesta.getStatusCode());
    }

    /**
     * Prueba la eliminación de un producto que NO existe
     * Verifica que retorne HTTP 404 NOT_FOUND
     */
    @Test
    void testEliminar_noExistente() {
        // ARRANGE: Configuramos el servicio para simular que no existe
        when(productoService.eliminar(1L)).thenReturn(false);

        // ACT: Intentamos eliminar
        ResponseEntity<?> respuesta = productoController.eliminar(1L);
        
        // ASSERT: Verificamos que retorne NOT_FOUND
        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
    }
}