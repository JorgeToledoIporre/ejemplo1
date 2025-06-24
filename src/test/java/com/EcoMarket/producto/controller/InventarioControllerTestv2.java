package com.EcoMarket.producto.controller;

import com.EcoMarket.producto.Assemblers.InventarioAssembler;
import com.EcoMarket.producto.model.Inventario;
import com.EcoMarket.producto.model.Producto;
import com.EcoMarket.producto.service.InventarioService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Clase de pruebas unitarias para InventarioControllerV2
 * 
 * Esta versión V2 del controlador implementa HATEOAS (Hypermedia As The Engine Of Application State)
 * HATEOAS es un principio de REST que permite que las respuestas incluyan enlaces a recursos relacionados,
 * facilitando la navegación por la API sin conocimiento previo de las URLs.
 * 
 * Por ejemplo, una respuesta puede incluir enlaces como:
 * - "self": enlace al propio recurso
 * - "edit": enlace para editar el recurso  
 * - "delete": enlace para eliminar el recurso
 */
public class InventarioControllerTestv2 {

    // Mock del servicio de inventario
    @Mock
    private InventarioService inventarioService;

    // Mock del assembler que convierte entidades en EntityModel (para HATEOAS)
    @Mock
    private InventarioAssembler assembler;

    // El controlador V2 que vamos a probar
    private InventarioControllerV2 inventarioController;

    /**
     * Configuración que se ejecuta antes de cada prueba
     */
    @BeforeEach
    void setUp() {
        // Creamos los mocks
        inventarioService = mock(InventarioService.class);
        assembler = mock(InventarioAssembler.class);
        inventarioController = new InventarioControllerV2();
        
        // Inyectamos el servicio
        inventarioController.setInventarioService(inventarioService);

        // Inyectamos el assembler usando reflexión (acceso a campos privados)
        // Esto es necesario cuando no hay un setter público para la dependencia
        try {
            var field = InventarioControllerV2.class.getDeclaredField("assembler");
            field.setAccessible(true); // Permite acceder a campos privados
            field.set(inventarioController, assembler);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Prueba que verifica listar todos los inventarios (con HATEOAS)
     */
    @Test
    void testListarTodos() {
        // ARRANGE: Preparamos inventarios de prueba
        Inventario i1 = new Inventario();
        Inventario i2 = new Inventario();
        
        // EntityModel es una envoltura HATEOAS que contiene la entidad + enlaces
        EntityModel<Inventario> em1 = EntityModel.of(i1);
        EntityModel<Inventario> em2 = EntityModel.of(i2);

        // Configuramos los mocks
        when(inventarioService.listarTodos()).thenReturn(List.of(i1, i2));
        when(assembler.toModel(i1)).thenReturn(em1); // Convierte i1 a EntityModel
        when(assembler.toModel(i2)).thenReturn(em2); // Convierte i2 a EntityModel

        // ACT: Llamamos al método Listar() del controlador V2
        ResponseEntity<CollectionModel<EntityModel<Inventario>>> respuesta = inventarioController.Listar();
        
        // ASSERT: Verificamos la respuesta HATEOAS
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(2, respuesta.getBody().getContent().size()); // 2 EntityModels en la colección
    }

    /**
     * Prueba que verifica el comportamiento cuando no hay inventarios (lista vacía)
     */
    @Test
    void testListarTodos_vacio() {
        // ARRANGE: Configuramos el servicio para devolver lista vacía
        when(inventarioService.listarTodos()).thenReturn(List.of());

        // ACT: Listamos inventarios
        ResponseEntity<CollectionModel<EntityModel<Inventario>>> respuesta = inventarioController.Listar();
        
        // ASSERT: Debe devolver HTTP 204 (No Content) cuando no hay datos
        assertEquals(HttpStatus.NO_CONTENT, respuesta.getStatusCode());
    }

    /**
     * Prueba obtener inventario por ID (existente) con HATEOAS
     */
    @Test
    void testObtenerPorId_existente() {
        // ARRANGE: Preparamos un inventario
        Inventario inventario = new Inventario();
        inventario.setId(1L);

        // Configuramos los mocks
        when(inventarioService.obtenerPorId(1L)).thenReturn(Optional.of(inventario));
        // Nota: Este test tiene un bug - no configura el assembler.toModel()

        // ACT: Obtenemos el inventario por ID
        ResponseEntity<EntityModel<Inventario>> respuesta = inventarioController.obtenerPorId(1L);
        
        // ASSERT: Verificamos que devuelva el inventario correcto
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(1L, respuesta.getBody().getContent().getId());
    }

    /**
     * Prueba obtener inventario por ID cuando no existe
     */
    @Test
    void testObtenerPorId_noExistente() {
        // ARRANGE: Configuramos para que no encuentre el inventario
        when(inventarioService.obtenerPorId(1L)).thenReturn(Optional.empty());

        // ACT: Intentamos obtener un inventario inexistente
        ResponseEntity<EntityModel<Inventario>> respuesta = inventarioController.obtenerPorId(1L);
        
        // ASSERT: Debe devolver 404
        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
    }

    /**
     * Prueba crear un nuevo inventario con HATEOAS
     */
    @Test
    void testCrearInventario() {
        // ARRANGE: Preparamos un inventario para crear
        Inventario inventario = new Inventario();
        Producto producto = new Producto();
        producto.setId(1L);
        inventario.setProducto(producto);
        inventario.setCantidad(10);

        // Preparamos el EntityModel para la respuesta HATEOAS
        EntityModel<Inventario> model = EntityModel.of(inventario);

        // Configuramos los mocks
        when(inventarioService.guardar(any(Inventario.class))).thenReturn(inventario);
        when(assembler.toModel(any(Inventario.class))).thenReturn(model);

        // ACT: Creamos el inventario
        ResponseEntity<EntityModel<Inventario>> respuesta = inventarioController.crearInventario(inventario);
        
        // ASSERT: Verificamos la creación exitosa
        assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());
        assertEquals(10, respuesta.getBody().getContent().getCantidad());
    }

    /**
     * Prueba actualizar un inventario existente
     */
    @Test
    void testActualizar_existente() {
        // ARRANGE: Preparamos datos de actualización
        Inventario inventario = new Inventario();
        inventario.setId(1L);
        inventario.setCantidad(20);

        // Configuramos el mock del servicio
        when(inventarioService.actualizar(eq(1L), any(Inventario.class))).thenReturn(Optional.of(inventario));
        // Nota: Este test también tiene el bug de no configurar el assembler

        // ACT: Actualizamos el inventario
        ResponseEntity<EntityModel<Inventario>> respuesta = inventarioController.actualizar(1L, inventario);
        
        // ASSERT: Verificamos la actualización
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(20, respuesta.getBody().getContent().getCantidad());
    }

    /**
     * Prueba actualizar un inventario que no existe
     */
    @Test
    void testActualizar_noExistente() {
        // ARRANGE: Configuramos para simular inventario no encontrado
        Inventario inventario = new Inventario();
        when(inventarioService.actualizar(eq(1L), any(Inventario.class))).thenReturn(Optional.empty());

        // ACT: Intentamos actualizar inventario inexistente
        ResponseEntity<EntityModel<Inventario>> respuesta = inventarioController.actualizar(1L, inventario);
        
        // ASSERT: Debe devolver 404
        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
    }

    /**
     * Prueba eliminar un inventario existente
     * Nota: En V2, el método eliminar devuelve un mensaje String en lugar de Void
     */
    @Test
    void testEliminar_existente() {
        // ARRANGE: Configuramos para simular eliminación exitosa
        when(inventarioService.eliminar(1L)).thenReturn(true);

        // ACT: Eliminamos el inventario
        ResponseEntity<String> respuesta = inventarioController.eliminar(1L);
        
        // ASSERT: Verificamos eliminación exitosa con mensaje
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals("Inventario eliminado correctamente", respuesta.getBody());
    }

    /**
     * Prueba eliminar un inventario que no existe
     */
    @Test
    void testEliminar_noExistente() {
        // ARRANGE: Configuramos para simular que no se pudo eliminar
        when(inventarioService.eliminar(1L)).thenReturn(false);

        // ACT: Intentamos eliminar inventario inexistente
        ResponseEntity<String> respuesta = inventarioController.eliminar(1L);
        
        // ASSERT: Verificamos el mensaje de error
        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        assertEquals("Inventario no fue eliminado", respuesta.getBody());
    }
}