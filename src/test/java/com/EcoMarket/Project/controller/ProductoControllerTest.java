package com.EcoMarket.Project.controller;

import com.EcoMarket.producto.controller.ProductoController;
import com.EcoMarket.producto.model.Producto;
import com.EcoMarket.producto.service.ProductoService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class ProductoControllerTest {

    @Mock
    private ProductoService productoService;
    private ProductoController productoController;
    @BeforeEach

    void setUp() {
        productoService = mock(ProductoService.class);
        productoController = new ProductoController();
        productoController.setProductoService(productoService); // Debes agregar este setter en la clase real ProductoController
    }

    @Test
    void testListar() {
        Producto p1 = new Producto();
        Producto p2 = new Producto();
        when(productoService.listarTodos()).thenReturn(List.of(p1, p2));

        ResponseEntity<List<Producto>> respuesta = productoController.listarTodos();
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(2, respuesta.getBody().size());
    }


    @Test
    void testObtenerPorId_existente() {
        Producto producto = new Producto();
        producto.setId(1L);
        when(productoService.obtenerPorId(1L)).thenReturn(Optional.of(producto));

        ResponseEntity<Producto> respuesta = productoController.obtenerPorId(1L);
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(1L, respuesta.getBody().getId());
    }
    @Test
    void testObtenerPorId_noExistente() {
        when(productoService.obtenerPorId(1L)).thenReturn(Optional.empty());

        ResponseEntity<Producto> respuesta = productoController.obtenerPorId(1L);
        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
    }
    @Test
    void testCrearProducto() {
        Producto producto = new Producto();
        producto.setCodigo("P001");
        producto.setNombre("Producto Test");
        producto.setDescripcion("Descripción");
        producto.setPrecio(BigDecimal.valueOf(10));
        producto.setCategoria("Prueba");
        producto.setActivo(true);

        when(productoService.guardar(any(Producto.class))).thenReturn(producto);

        ResponseEntity<Producto> respuesta = productoController.crearProducto(producto);
        assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());;
        assertEquals("P001", respuesta.getBody().getCodigo());
    }

    @Test
    void testActualizar_existente() {
        Producto producto = new Producto();
        producto.setNombre("Actualizado");
        when(productoService.actualizar(eq(1L), any(Producto.class))).thenReturn(Optional.of(producto));

        ResponseEntity<Producto> respuesta = productoController.actualizar(1L, producto);
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals("Actualizado", respuesta.getBody().getNombre());
    }
    @Test
    void testActualizar_noExistente() {
        Producto producto = new Producto();
        when(productoService.actualizar(eq(1L), any(Producto.class))).thenReturn(Optional.empty());

        ResponseEntity<Producto> respuesta = productoController.actualizar(1L, producto);
        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
    }
    @Test
    void testEliminar_existente() {
        when(productoService.eliminar(1L)).thenReturn(true);

        ResponseEntity<Void> respuesta = productoController.eliminar(1L);
        assertEquals(HttpStatus.NO_CONTENT, respuesta.getStatusCode());
    }
   @Test
    void testEliminar_noExistente() {
        when(productoService.eliminar(1L)).thenReturn(false);

        ResponseEntity<Void> respuesta = productoController.eliminar(1L);
        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
    }
}
