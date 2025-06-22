package com.EcoMarket.producto.controller;

import com.EcoMarket.producto.model.Inventario;
import com.EcoMarket.producto.model.Producto;
import com.EcoMarket.producto.service.InventarioService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;


public class InventarioControllerTest {

    @Mock
    private InventarioService inventarioService;
    private InventarioController inventarioController;

    @BeforeEach
    void setUp() {
        inventarioService = mock(InventarioService.class);
        inventarioController = new InventarioController();
        inventarioController.setInventarioService(inventarioService);
    }
    @Test
    void testListarTodos() {
        Inventario i1 = new Inventario();
        Inventario i2 = new Inventario();
        when(inventarioService.listarTodos()).thenReturn(List.of(i1, i2));

        ResponseEntity<List<Inventario>> respuesta = inventarioController.listarTodos();
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(2, respuesta.getBody().size());
    }

    @Test
    void testObtenerPorId_existente() {
        Inventario inventario = new Inventario();
        inventario.setId(1L);
        when(inventarioService.obtenerPorId(1L)).thenReturn(Optional.of(inventario));

        ResponseEntity<Inventario> respuesta = inventarioController.obtenerPorId(1L);
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(1L, respuesta.getBody().getId());
    }
    @Test
    void testObtenerPorId_noExistente() {
        when(inventarioService.obtenerPorId(1L)).thenReturn(Optional.empty());

        ResponseEntity<Inventario> respuesta = inventarioController.obtenerPorId(1L);
        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
    }
    @Test
    void testCrearInventario() {
        Inventario inventario = new Inventario();
        Producto producto = new Producto();
        producto.setId(1L);
        inventario.setProducto(producto);
        inventario.setCantidad(10);

        when(inventarioService.guardar(any(Inventario.class))).thenReturn(inventario);

        ResponseEntity<Inventario> respuesta = inventarioController.crear(inventario);
        assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());
        assertEquals(10, respuesta.getBody().getCantidad());
    }

    @Test
    void testActualizar_existente() {
        Inventario inventario = new Inventario();
        inventario.setCantidad(20);
        when(inventarioService.actualizar(eq(1L), any(Inventario.class))).thenReturn(Optional.of(inventario));

        ResponseEntity<Inventario> respuesta = inventarioController.actualizar(1L, inventario);
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(20, respuesta.getBody().getCantidad());
    }
    @Test
    void testActualizar_noExistente() {
        Inventario inventario = new Inventario();
        when(inventarioService.actualizar(eq(1L), any(Inventario.class))).thenReturn(Optional.empty());

        ResponseEntity<Inventario> respuesta = inventarioController.actualizar(1L, inventario);
        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
    }
    @Test
    void testEliminar_existente() {
        when(inventarioService.eliminar(1L)).thenReturn(true);

        ResponseEntity<Void> respuesta = inventarioController.eliminar(1L);
        assertEquals(HttpStatus.NO_CONTENT, respuesta.getStatusCode());
    }
    @Test
    void testEliminar_noExistente() {
        when(inventarioService.eliminar(1L)).thenReturn(false);

        ResponseEntity<Void> respuesta = inventarioController.eliminar(1L);
        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
    }
}
