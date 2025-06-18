package com.EcoMarket.Project.service;

import com.EcoMarket.producto.model.Producto;
import com.EcoMarket.producto.repository.ProductoRepository;
import com.EcoMarket.producto.service.ProductoService;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
/**
 * @Service: Marca la clase como un componente de servicio de Spring
 * @Transactional: Todas las operaciones del servicio se ejecutan en transacciones
 */

@Transactional
@Service

public class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;
    private ProductoService productoService;

    @BeforeEach
    //clase de pruebas para preparar el entorno 
    void setUp (){
        productoRepository = mock(ProductoRepository.class);
        productoService = new ProductoService();
        productoService.setProductoRepository(productoRepository);

    }
    @Test
    void TestListar(){
        List<Producto> lista =List.of(new Producto(), new Producto());
        when(productoRepository.findAll()).thenReturn(lista);

        List<Producto> resultado = productoService.listarTodos();
        assertEquals(2, resultado);
        verify(productoRepository,times(1)).findAll();
    }
    @Test
    void TestObtenerPorId(){
        Producto producto = new Producto();
        producto.setId(1L);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        Optional<Producto> resultado =productoService.obtenerPorId(1L); 

        assertTrue(resultado.isPresent() );
        assertEquals(1L, resultado.get().getId());
    }

    @Test
    void testObtenerPorCodigo() {
        Producto producto = new Producto();
        producto.setCodigo("ABC123");
        when(productoRepository.findByCodigo("ABC123")).thenReturn(Optional.of(producto));

        Optional<Producto> resultado = productoService.obtenerPorCodigo("ABC123");

        assertTrue(resultado.isPresent());
        assertEquals("ABC123", resultado.get().getCodigo());
    }
    @Test
    void testBuscarPorNombre() {
        Producto producto = new Producto();
        producto.setNombre("Arroz");
        when(productoRepository.findByNombreContainingIgnoreCase("arro")).thenReturn(List.of(producto));

        List<Producto> resultado = productoService.buscarPorNombre("arro");

        assertEquals(1, resultado.size());
        assertEquals("Arroz", resultado.get(0).getNombre());
    }


    @Test
    void testListarActivos() {
        Producto producto = new Producto();
        producto.setActivo(true);
        when(productoRepository.findByActivoTrue()).thenReturn(List.of(producto));

        List<Producto> resultado = productoService.listarActivos();

        assertEquals(1, resultado.size());
        assertTrue(resultado.get(0).isActivo());
    }
    void testBuscarPorCategoria() {
        Producto producto = new Producto();
        producto.setCategoria("Bebidas");
        when(productoRepository.findByCategoria("Bebidas")).thenReturn(List.of(producto));

        List<Producto> resultado = productoService.buscarPorCategoria("Bebidas");

        assertEquals(1, resultado.size());
        assertEquals("Bebidas", resultado.get(0).getCategoria());
    }

    @Test
    void testGuardar() {
        Producto producto = new Producto();
        producto.setCodigo("P001");
        producto.setPrecio(BigDecimal.TEN);
        when(productoRepository.findByCodigo("P001")).thenReturn(Optional.empty());
        when(productoRepository.save(producto)).thenReturn(producto);

        Producto guardado = productoService.guardar(producto);

        assertEquals("P001", guardado.getCodigo());
    }

    @Test
    void testEliminarExistente() {
        when(productoRepository.existsById(1L)).thenReturn(true);

        boolean eliminado = productoService.eliminar(1L);

        assertTrue(eliminado);
        verify(productoRepository).deleteById(1L);
    }

    @Test
    void testEliminarNoExistente() {
        when(productoRepository.existsById(99L)).thenReturn(false);

        boolean eliminado = productoService.eliminar(99L);

        assertFalse(eliminado);
        verify(productoRepository, never()).deleteById(anyLong());
    }
    
   @Test
    void testActualizarProducto() {
        Producto existente = new Producto();
        existente.setId(1L);
        existente.setCodigo("OLD123");

        Producto actualizado = new Producto();
        actualizado.setCodigo("NEW123");
        actualizado.setNombre("Producto actualizado");
        actualizado.setDescripcion("Nuevo");
        actualizado.setPrecio(BigDecimal.valueOf(25));
        actualizado.setCategoria("Lácteos");
        actualizado.setActivo(true);

        when(productoRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(productoRepository.findByCodigo("NEW123")).thenReturn(Optional.empty());
        when(productoRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        Optional<Producto> result = productoService.actualizar(1L, actualizado);

        assertTrue(result.isPresent());
        assertEquals("NEW123", result.get().getCodigo());
        assertEquals("Producto actualizado", result.get().getNombre());
    }
    }

