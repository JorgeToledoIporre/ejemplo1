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

public class InventarioControllerTestv2 {

    @Mock
    private InventarioService inventarioService;

    @Mock
    private InventarioAssembler assembler;

    private InventarioControllerV2 inventarioController;

    @BeforeEach
    void setUp() {
        inventarioService = mock(InventarioService.class);
        assembler = mock(InventarioAssembler.class);
        inventarioController = new InventarioControllerV2();
        inventarioController.setInventarioService(inventarioService);

        // Inyectamos el assembler usando reflexión (ya que no hay setter explícito)
        try {
            var field = InventarioControllerV2.class.getDeclaredField("assembler");
            field.setAccessible(true);
            field.set(inventarioController, assembler);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testListarTodos() {
        Inventario i1 = new Inventario();
        Inventario i2 = new Inventario();
        EntityModel<Inventario> em1 = EntityModel.of(i1);
        EntityModel<Inventario> em2 = EntityModel.of(i2);

        when(inventarioService.listarTodos()).thenReturn(List.of(i1, i2));
        when(assembler.toModel(i1)).thenReturn(em1);
        when(assembler.toModel(i2)).thenReturn(em2);

        ResponseEntity<CollectionModel<EntityModel<Inventario>>> respuesta = inventarioController.Listar();
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(2, respuesta.getBody().getContent().size());
    }

    @Test
    void testListarTodos_vacio() {
        when(inventarioService.listarTodos()).thenReturn(List.of());

        ResponseEntity<CollectionModel<EntityModel<Inventario>>> respuesta = inventarioController.Listar();
        assertEquals(HttpStatus.NO_CONTENT, respuesta.getStatusCode());
    }

    @Test
    void testObtenerPorId_existente() {
        Inventario inventario = new Inventario();
        inventario.setId(1L);

        when(inventarioService.obtenerPorId(1L)).thenReturn(Optional.of(inventario));

        ResponseEntity<EntityModel<Inventario>> respuesta = inventarioController.obtenerPorId(1L);
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(1L, respuesta.getBody().getContent().getId());
    }

    @Test
    void testObtenerPorId_noExistente() {
        when(inventarioService.obtenerPorId(1L)).thenReturn(Optional.empty());

        ResponseEntity<EntityModel<Inventario>> respuesta = inventarioController.obtenerPorId(1L);
        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
    }

    @Test
    void testCrearInventario() {
        Inventario inventario = new Inventario();
        Producto producto = new Producto();
        producto.setId(1L);
        inventario.setProducto(producto);
        inventario.setCantidad(10);

        EntityModel<Inventario> model = EntityModel.of(inventario);

        when(inventarioService.guardar(any(Inventario.class))).thenReturn(inventario);
        when(assembler.toModel(any(Inventario.class))).thenReturn(model);

        ResponseEntity<EntityModel<Inventario>> respuesta = inventarioController.crearInventario(inventario);
        assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());
        assertEquals(10, respuesta.getBody().getContent().getCantidad());
    }

    @Test
    void testActualizar_existente() {
        Inventario inventario = new Inventario();
        inventario.setId(1L);
        inventario.setCantidad(20);

        when(inventarioService.actualizar(eq(1L), any(Inventario.class))).thenReturn(Optional.of(inventario));

        ResponseEntity<EntityModel<Inventario>> respuesta = inventarioController.actualizar(1L, inventario);
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(20, respuesta.getBody().getContent().getCantidad());
    }

    @Test
    void testActualizar_noExistente() {
        Inventario inventario = new Inventario();
        when(inventarioService.actualizar(eq(1L), any(Inventario.class))).thenReturn(Optional.empty());

        ResponseEntity<EntityModel<Inventario>> respuesta = inventarioController.actualizar(1L, inventario);
        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
    }

    @Test
    void testEliminar_existente() {
        when(inventarioService.eliminar(1L)).thenReturn(true);

        ResponseEntity<String> respuesta = inventarioController.eliminar(1L);
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals("Inventario eliminado correctamente", respuesta.getBody());
    }

    @Test
    void testEliminar_noExistente() {
        when(inventarioService.eliminar(1L)).thenReturn(false);

        ResponseEntity<String> respuesta = inventarioController.eliminar(1L);
        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        assertEquals("Inventario no fue eliminado", respuesta.getBody());
    }
}
