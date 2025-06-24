package com.EcoMarket.producto.service;

import com.EcoMarket.producto.model.Inventario;
import com.EcoMarket.producto.model.Producto;
import com.EcoMarket.producto.repository.InventarioRepository;
import com.EcoMarket.producto.repository.ProductoRepository;

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

/**
 * Clase de pruebas unitarias para el servicio de Inventario
 * 
 * @Transactional: Asegura que cada prueba se ejecute en su propia transacción
 * @Service: Indica que esta es una clase de servicio de Spring
 * 
 * Esta clase verifica la lógica de negocio del manejo de inventarios
 * sin conectarse a la base de datos real (usa mocks)
 */
@Transactional
@Service
public class InventarioServiceTest {

    // Mocks: Objetos falsos que simulan las dependencias
    @Mock
    private InventarioRepository inventarioRepository;  // Simula acceso a datos de inventario
    
    @Mock
    private ProductoRepository productoRepository;      // Simula acceso a datos de productos

    // El servicio que vamos a probar
    private InventarioService inventarioService;

    /**
     * Configuración que se ejecuta antes de cada prueba
     * Inicializa los mocks y el servicio con sus dependencias
     */
    @BeforeEach
    void setUp() {
        // Creamos mocks manualmente
        inventarioRepository = mock(InventarioRepository.class);
        productoRepository = mock(ProductoRepository.class);
        
        // Creamos el servicio y le inyectamos las dependencias mockeadas
        inventarioService = new InventarioService();
        inventarioService.setInventarioRepository(inventarioRepository); 
        inventarioService.setProductoRepository(productoRepository);     
    }

    /**
     * Prueba el método listarTodos()
     * Verifica que retorne la lista completa de inventarios
     */
    @Test
    void testListarTodos() {
        // ARRANGE: Preparamos datos de prueba
        // Configuramos el mock para retornar una lista con 2 inventarios
        when(inventarioRepository.findAll()).thenReturn(List.of(new Inventario(), new Inventario()));
        
        // ACT: Ejecutamos el método que queremos probar
        List<Inventario> lista = inventarioService.listarTodos();
        
        // ASSERT: Verificamos que el resultado sea correcto
        assertEquals(2, lista.size());
    }

    /**
     * Prueba el método obtenerPorId() con un ID existente
     * Verifica que retorne el inventario correcto
     */
    @Test
    void testObtenerPorId() {
        // ARRANGE: Creamos un inventario con ID específico
        Inventario inventario = new Inventario();
        inventario.setId(1L);
        
        // Configuramos el mock para simular que encuentra el inventario
        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(inventario));
        
        // ACT: Buscamos el inventario por ID
        Optional<Inventario> resultado = inventarioService.obtenerPorId(1L);
        
        // ASSERT: Verificamos que lo encontró y tiene el ID correcto
        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getId());
    }

    /**
     * Prueba el método guardar() con datos válidos
     * Verifica que se guarde correctamente un inventario válido
     */
    @Test
    void testGuardarValido() {
        // ARRANGE: Preparamos un producto y un inventario válidos
        Producto producto = new Producto();
        producto.setId(1L);

        Inventario inventario = new Inventario();
        inventario.setProducto(producto);
        inventario.setCantidad(50);
        inventario.setCantidadMinima(10);
        inventario.setUbicacion("Bodega 1");

        // Configuramos los mocks para simular que el producto existe
        when(productoRepository.existsById(1L)).thenReturn(true);
        when(inventarioRepository.save(any())).thenReturn(inventario);

        // ACT: Intentamos guardar el inventario
        Inventario guardado = inventarioService.guardar(inventario);
        
        // ASSERT: Verificamos que se guardó con los datos correctos
        assertEquals(50, guardado.getCantidad());
        assertEquals("Bodega 1", guardado.getUbicacion());
    }

    /**
     * Prueba el método guardar() con datos nulos
     * Verifica que lance una excepción cuando el inventario es nulo
     */
    @Test
    void testGuardaroNulo() {
        // ARRANGE: Creamos un inventario sin producto (inválido)
        Inventario inventario = new Inventario();
        
        // ACT & ASSERT: Verificamos que lance la excepción esperada
        assertThrows(IllegalArgumentException.class, () -> inventarioService.guardar(inventario));
    }

    /**
     * Prueba el método guardar() cuando el producto no existe
     * Verifica que lance una excepción si el producto asociado no existe
     */
    @Test
    void testGuardarNoExiste() {
        // ARRANGE: Creamos un inventario con producto que no existe
        Producto producto = new Producto();
        producto.setId(1L);
        Inventario inventario = new Inventario();
        inventario.setProducto(producto);

        // Configuramos el mock para simular que el producto NO existe
        when(productoRepository.existsById(1L)).thenReturn(false);

        // ACT & ASSERT: Verificamos que lance RuntimeException
        assertThrows(RuntimeException.class, () -> inventarioService.guardar(inventario));
    }

    /**
     * Prueba el método actualizar() con datos válidos
     * Verifica que se actualice correctamente un inventario existente
     */
    @Test
    void testActualizar() {
        // ARRANGE: Preparamos los datos para la actualización
        Producto producto = new Producto();
        producto.setId(1L);

        // Inventario existente en la base de datos
        Inventario existente = new Inventario();
        existente.setId(1L);

        // Datos actualizados
        Inventario actualizado = new Inventario();
        actualizado.setProducto(producto);
        actualizado.setCantidad(30);
        actualizado.setCantidadMinima(5);
        actualizado.setUbicacion("Estante A");

        // Configuramos los mocks
        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(productoRepository.existsById(1L)).thenReturn(true);
        when(inventarioRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        // ACT: Ejecutamos la actualización
        Optional<Inventario> resultado = inventarioService.actualizar(1L, actualizado);
        
        // ASSERT: Verificamos que se actualizó correctamente
        assertTrue(resultado.isPresent());
        assertEquals(30, resultado.get().getCantidad());
        assertEquals("Estante A", resultado.get().getUbicacion());
    }

    /**
     * Prueba el método eliminar() con un inventario existente
     * Verifica que se elimine correctamente y retorne true
     */
    @Test
    void testEliminarExistente() {
        // ARRANGE: Configuramos el mock para simular que el inventario existe
        when(inventarioRepository.existsById(1L)).thenReturn(true);
        
        // ACT: Intentamos eliminar el inventario
        boolean eliminado = inventarioService.eliminar(1L);
        
        // ASSERT: Verificamos que se eliminó correctamente
        assertTrue(eliminado);
        // Verificamos que se llamó al método deleteById del repositorio
        verify(inventarioRepository).deleteById(1L);
    }

    /**
     * Prueba el método eliminar() con un inventario que no existe
     * Verifica que retorne false y no intente eliminar nada
     */
    @Test
    void testEliminarNoExistente() {
        // ARRANGE: Configuramos el mock para simular que el inventario NO existe
        when(inventarioRepository.existsById(1L)).thenReturn(false);
        
        // ACT: Intentamos eliminar un inventario inexistente
        boolean eliminado = inventarioService.eliminar(1L);
        
        // ASSERT: Verificamos que retorne false
        assertFalse(eliminado);
        
        // Verificamos que NUNCA se llamó al método deleteById
        // (no debe intentar eliminar algo que no existe)
        verify(inventarioRepository, never()).deleteById(anyLong());
    }
}