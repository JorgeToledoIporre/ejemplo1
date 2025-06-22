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

public class ProductoControllertestv2 {

    @Mock
    private ProductoService productoService;

    @Mock
    private ProductoModelAssembler assembler;

    private ProductoControllerV2 productoController;

    @BeforeEach
    void setUp() {
        productoService = mock(ProductoService.class);
        assembler = mock(ProductoModelAssembler.class);
        productoController = new ProductoControllerV2();

        // Inyectar mocks manualmente
        productoController.setProductoService(productoService);
        try {
            var field = ProductoControllerV2.class.getDeclaredField("assembler");
            field.setAccessible(true);
            field.set(productoController, assembler);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testListar() {
        Producto p1 = new Producto();
        Producto p2 = new Producto();
        EntityModel<Producto> em1 = EntityModel.of(p1);
        EntityModel<Producto> em2 = EntityModel.of(p2);

        when(productoService.listarTodos()).thenReturn(List.of(p1, p2));
        when(assembler.toModel(p1)).thenReturn(em1);
        when(assembler.toModel(p2)).thenReturn(em2);

        ResponseEntity<CollectionModel<EntityModel<Producto>>> respuesta = productoController.Listar();
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(2, respuesta.getBody().getContent().size());
    }

    @Test
    void testListar_vacio() {
        when(productoService.listarTodos()).thenReturn(List.of());

        ResponseEntity<CollectionModel<EntityModel<Producto>>> respuesta = productoController.Listar();
        assertEquals(HttpStatus.NO_CONTENT, respuesta.getStatusCode());
    }

    @Test
    void testObtenerPorId_existente() {
        Producto producto = new Producto();
        producto.setId(1L);
        EntityModel<Producto> model = EntityModel.of(producto);

        when(productoService.obtenerPorId(1L)).thenReturn(Optional.of(producto));
        when(assembler.toModel(producto)).thenReturn(model);

        ResponseEntity<EntityModel<Producto>> respuesta = productoController.obtenerPorId(1L);
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(1L, respuesta.getBody().getContent().getId());
    }

    @Test
    void testObtenerPorId_noExistente() {
        when(productoService.obtenerPorId(1L)).thenReturn(Optional.empty());

        ResponseEntity<EntityModel<Producto>> respuesta = productoController.obtenerPorId(1L);
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

        EntityModel<Producto> model = EntityModel.of(producto);

        when(productoService.guardar(any(Producto.class))).thenReturn(producto);
        when(assembler.toModel(any(Producto.class))).thenReturn(model);

        ResponseEntity<EntityModel<Producto>> respuesta = productoController.crearProducto(producto);
        assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());
        assertEquals("P001", respuesta.getBody().getContent().getCodigo());
    }

    @Test
    void testActualizar_existente() {
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Actualizado");

        when(productoService.actualizar(eq(1L), any(Producto.class))).thenReturn(Optional.of(producto));

        ResponseEntity<EntityModel<Producto>> respuesta = productoController.actualizar(1L, producto);
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals("Actualizado", respuesta.getBody().getContent().getNombre());
    }

    @Test
    void testActualizar_noExistente() {
        Producto producto = new Producto();
        when(productoService.actualizar(eq(1L), any(Producto.class))).thenReturn(Optional.empty());

        ResponseEntity<EntityModel<Producto>> respuesta = productoController.actualizar(1L, producto);
        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
    }

    @Test
    void testEliminar_existente() {
        when(productoService.eliminar(1L)).thenReturn(true);

        ResponseEntity<?> respuesta = productoController.eliminar(1L);
        assertEquals(HttpStatus.NO_CONTENT, respuesta.getStatusCode());
    }

    @Test
    void testEliminar_noExistente() {
        when(productoService.eliminar(1L)).thenReturn(false);

        ResponseEntity<?> respuesta = productoController.eliminar(1L);
        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
    }
}
