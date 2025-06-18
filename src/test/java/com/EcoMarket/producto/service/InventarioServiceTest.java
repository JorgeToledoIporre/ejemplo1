package com.EcoMarket.Project.service;

import com.EcoMarket.producto.model.Inventario;
import com.EcoMarket.producto.model.Producto;
import com.EcoMarket.producto.repository.InventarioRepository;
import com.EcoMarket.producto.repository.ProductoRepository;
import com.EcoMarket.producto.service.InventarioService;

import jakarta.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import org.springframework.stereotype.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class InventarioServiceTest {

    @Mock
    private InventarioRepository inventarioRepository;
    private ProductoRepository productoRepository;
    private InventarioService inventarioService;

    @BeforeEach
    void setUp() {
        inventarioRepository = mock(InventarioRepository.class);
        productoRepository = mock(ProductoRepository.class);
        inventarioService = new InventarioService();
        inventarioService.setInventarioRepository(inventarioRepository); 
        inventarioService.setProductoRepository(productoRepository);     
    }

    @Test
    void testListarTodos() {
        when(inventarioRepository.findAll()).thenReturn(List.of(new Inventario(), new Inventario()));
        List<Inventario> lista = inventarioService.listarTodos();
        assertEquals(2, lista.size());
    }

    @Test
    void testObtenerPorId() {
        Inventario inventario = new Inventario();
        inventario.setId(1L);
        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(inventario));
        Optional<Inventario> resultado = inventarioService.obtenerPorId(1L);
        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getId());
    }


    @Test
    void testGuardarValido() {
        Producto producto = new Producto();
        producto.setId(1L);

        Inventario inventario = new Inventario();
        inventario.setProducto(producto);
        inventario.setCantidad(50);
        inventario.setCantidadMinima(10);
        inventario.setUbicacion("Bodega 1");

        when(productoRepository.existsById(1L)).thenReturn(true);
        when(inventarioRepository.save(any())).thenReturn(inventario);

        Inventario guardado = inventarioService.guardar(inventario);
        assertEquals(50, guardado.getCantidad());
        assertEquals("Bodega 1", guardado.getUbicacion());
    }

    @Test
    void testGuardaroNulo() {
        Inventario inventario = new Inventario();
        assertThrows(IllegalArgumentException.class, () -> inventarioService.guardar(inventario));
    }

    @Test
    void testGuardarNoExiste() {
        Producto producto = new Producto();
        producto.setId(1L);
        Inventario inventario = new Inventario();
        inventario.setProducto(producto);

        when(productoRepository.existsById(1L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> inventarioService.guardar(inventario));
    }
    @Test
    void testActualizar() {
        Producto producto = new Producto();
        producto.setId(1L);

        Inventario existente = new Inventario();
        existente.setId(1L);

        Inventario actualizado = new Inventario();
        actualizado.setProducto(producto);
        actualizado.setCantidad(30);
        actualizado.setCantidadMinima(5);
        actualizado.setUbicacion("Estante A");

        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(productoRepository.existsById(1L)).thenReturn(true);
        when(inventarioRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        Optional<Inventario> resultado = inventarioService.actualizar(1L, actualizado);
        assertTrue(resultado.isPresent());
        assertEquals(30, resultado.get().getCantidad());
        assertEquals("Estante A", resultado.get().getUbicacion());
    }

    @Test
    void testEliminarExistente() {
        when(inventarioRepository.existsById(1L)).thenReturn(true);
        boolean eliminado = inventarioService.eliminar(1L);
        assertTrue(eliminado);
        verify(inventarioRepository).deleteById(1L);
    }
    @Test
    void testEliminarNoExistente() {
        when(inventarioRepository.existsById(1L)).thenReturn(false);
        boolean eliminado = inventarioService.eliminar(1L);
        assertFalse(eliminado);
        verify(inventarioRepository, never()).deleteById(anyLong());
    }
}
