package com.EcoMarket.producto.service;

// Importaciones necesarias para las pruebas
import com.EcoMarket.producto.model.Producto;
import com.EcoMarket.producto.repository.ProductoRepository;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

// Importaciones para assertions (verificaciones) en las pruebas
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Importaciones para mocking (simulación de objetos)
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

// Importaciones para JUnit (framework de pruebas)
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

/**
 * CLASE DE PRUEBAS PARA PRODUCTOSERVICE
 * 
 * Esta clase contiene pruebas unitarias para verificar que el servicio
 * ProductoService funciona correctamente. Utiliza:
 * 
 * - JUnit: Framework para crear y ejecutar pruebas
 * - Mockito: Framework para simular dependencias (objetos mock)
 * 
 * @Service: Marca la clase como un componente de servicio de Spring
 * @Transactional: Todas las operaciones se ejecutan en transacciones
 */
@Transactional // Cada prueba se ejecuta en una transacción que se revierte al final
@Service // Aunque es una clase de pruebas, se marca como servicio por alguna configuración específica
public class ProductoServiceTest {

    // SIMULACIÓN DE DEPENDENCIAS (MOCKS)
    @Mock
    private ProductoRepository productoRepository; // Repositorio simulado
    
    private ProductoService productoService; // Servicio real que vamos a probar
    
    /**
     * CONFIGURACIÓN INICIAL ANTES DE CADA PRUEBA
     * 
     * Este método se ejecuta antes de cada prueba individual
     * para preparar el entorno de testing.
     */
    @BeforeEach
    void setUp() {
        // Crea un mock (simulación) del repositorio
        productoRepository = mock(ProductoRepository.class);
        
        // Crea una instancia real del servicio que vamos a probar
        productoService = new ProductoService();
        
        // Inyecta el repositorio mock en el servicio
        productoService.setProductoRepository(productoRepository);
    }

    /**
     * PRUEBA: LISTAR TODOS LOS PRODUCTOS
     * 
     * Verifica que el método listarTodos() del servicio
     * devuelve correctamente una lista de productos.
     */
    @Test
    void TestListar() {
        // PREPARACIÓN: Creamos datos de prueba
        List<Producto> lista = List.of(new Producto(), new Producto());
        
        // CONFIGURACIÓN DEL MOCK: Le decimos qué debe devolver cuando se llame a findAll()
        when(productoRepository.findAll()).thenReturn(lista);

        // EJECUCIÓN: Llamamos al método que estamos probando
        List<Producto> resultado = productoService.listarTodos();
        
        // VERIFICACIÓN: Comprobamos que el resultado es correcto
        assertEquals(2, resultado.size()); // Debe devolver 2 productos
        verify(productoRepository, times(1)).findAll(); // findAll() debe haberse llamado exactamente 1 vez
    }

    /**
     * PRUEBA: OBTENER PRODUCTO POR ID
     * 
     * Verifica que podemos obtener un producto específico por su ID.
     */
    @Test
    void TestObtenerPorId() {
        // PREPARACIÓN: Creamos un producto de prueba
        Producto producto = new Producto();
        producto.setId(1L);
        
        // CONFIGURACIÓN DEL MOCK: Simulamos que el repositorio encuentra el producto
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        // EJECUCIÓN: Buscamos el producto por ID
        Optional<Producto> resultado = productoService.obtenerPorId(1L);

        // VERIFICACIÓN: Comprobamos que se encontró el producto correcto
        assertTrue(resultado.isPresent()); // El Optional debe contener un producto
        assertEquals(1L, resultado.get().getId()); // El ID debe coincidir
    }

    /**
     * PRUEBA: OBTENER PRODUCTO POR CÓDIGO
     * 
     * Verifica que podemos buscar un producto por su código único.
     */
    @Test
    void testObtenerPorCodigo() {
        // PREPARACIÓN: Producto con código específico
        Producto producto = new Producto();
        producto.setCodigo("ABC123");
        
        // CONFIGURACIÓN DEL MOCK
        when(productoRepository.findByCodigo("ABC123")).thenReturn(Optional.of(producto));

        // EJECUCIÓN
        Optional<Producto> resultado = productoService.obtenerPorCodigo("ABC123");

        // VERIFICACIÓN
        assertTrue(resultado.isPresent());
        assertEquals("ABC123", resultado.get().getCodigo());
    }

    /**
     * PRUEBA: BUSCAR PRODUCTOS POR NOMBRE (BÚSQUEDA PARCIAL)
     * 
     * Verifica que podemos buscar productos por parte de su nombre,
     * ignorando mayúsculas y minúsculas.
     */
    @Test
    void testBuscarPorNombre() {
        // PREPARACIÓN: Producto que contenga "arro" en su nombre
        Producto producto = new Producto();
        producto.setNombre("Arroz");
        
        // CONFIGURACIÓN: Buscar productos que contengan "arro" (sin importar mayúsculas)
        when(productoRepository.findByNombreContainingIgnoreCase("arro")).thenReturn(List.of(producto));

        // EJECUCIÓN: Búsqueda parcial
        List<Producto> resultado = productoService.buscarPorNombre("arro");

        // VERIFICACIÓN: Debe encontrar el producto "Arroz"
        assertEquals(1, resultado.size());
        assertEquals("Arroz", resultado.get(0).getNombre());
    }

    /**
     * PRUEBA: LISTAR SOLO PRODUCTOS ACTIVOS
     * 
     * Verifica que podemos obtener únicamente los productos
     * que están marcados como activos.
     */
    @Test
    void testListarActivos() {
        // PREPARACIÓN: Producto activo
        Producto producto = new Producto();
        producto.setActivo(true);
        
        // CONFIGURACIÓN: El repositorio devuelve solo productos activos
        when(productoRepository.findByActivoTrue()).thenReturn(List.of(producto));

        // EJECUCIÓN
        List<Producto> resultado = productoService.listarActivos();

        // VERIFICACIÓN
        assertEquals(1, resultado.size());
        assertTrue(resultado.get(0).isActivo()); // Debe estar activo
    }

    /**
     * PRUEBA: BUSCAR PRODUCTOS POR CATEGORÍA
     * 
     * Verifica que podemos filtrar productos por su categoría.
     * 
     * NOTA: Falta la anotación @Test, es un error en el código original
     */
    @Test // Esta anotación faltaba en el código original
    void testBuscarPorCategoria() {
        // PREPARACIÓN: Producto de la categoría "Bebidas"
        Producto producto = new Producto();
        producto.setCategoria("Bebidas");
        
        // CONFIGURACIÓN
        when(productoRepository.findByCategoria("Bebidas")).thenReturn(List.of(producto));

        // EJECUCIÓN
        List<Producto> resultado = productoService.buscarPorCategoria("Bebidas");

        // VERIFICACIÓN
        assertEquals(1, resultado.size());
        assertEquals("Bebidas", resultado.get(0).getCategoria());
    }

    /**
     * PRUEBA: GUARDAR UN NUEVO PRODUCTO
     * 
     * Verifica que podemos guardar un producto nuevo correctamente.
     * Incluye validación de que el código no esté duplicado.
     */
    @Test
    void testGuardar() {
        // PREPARACIÓN: Nuevo producto con código único
        Producto producto = new Producto();
        producto.setCodigo("P001");
        producto.setPrecio(BigDecimal.TEN);
        
        // CONFIGURACIÓN: El código no existe previamente
        when(productoRepository.findByCodigo("P001")).thenReturn(Optional.empty());
        // El repositorio devuelve el mismo producto cuando se guarda
        when(productoRepository.save(producto)).thenReturn(producto);

        // EJECUCIÓN: Intentamos guardar el producto
        Producto guardado = productoService.guardar(producto);

        // VERIFICACIÓN: El producto se guardó con el código correcto
        assertEquals("P001", guardado.getCodigo());
    }

    /**
     * PRUEBA: ELIMINAR UN PRODUCTO EXISTENTE
     * 
     * Verifica que podemos eliminar un producto que existe en la base de datos.
     */
    @Test
    void testEliminarExistente() {
        // CONFIGURACIÓN: El producto con ID 1 existe
        when(productoRepository.existsById(1L)).thenReturn(true);

        // EJECUCIÓN: Intentamos eliminar el producto
        boolean eliminado = productoService.eliminar(1L);

        // VERIFICACIÓN:
        assertTrue(eliminado); // La operación fue exitosa
        verify(productoRepository).deleteById(1L); // Se llamó al método de eliminación
    }

    /**
     * PRUEBA: INTENTAR ELIMINAR UN PRODUCTO QUE NO EXISTE
     * 
     * Verifica el comportamiento cuando tratamos de eliminar
     * un producto que no existe en la base de datos.
     */
    @Test
    void testEliminarNoExistente() {
        // CONFIGURACIÓN: El producto con ID 99 NO existe
        when(productoRepository.existsById(99L)).thenReturn(false);

        // EJECUCIÓN: Intentamos eliminar un producto inexistente
        boolean eliminado = productoService.eliminar(99L);

        // VERIFICACIÓN:
        assertFalse(eliminado); // La operación falló (correcto)
        verify(productoRepository, never()).deleteById(anyLong()); // NO se intentó eliminar nada
    }

    /**
     * PRUEBA: ACTUALIZAR UN PRODUCTO EXISTENTE
     * 
     * Verifica que podemos actualizar los datos de un producto existente,
     * incluyendo validación de código único para el nuevo código.
     */
    @Test
    void testActualizarProducto() {
        // PREPARACIÓN: Producto existente en la base de datos
        Producto existente = new Producto();
        existente.setId(1L);
        existente.setCodigo("OLD123");

        // Nuevos datos para actualizar el producto
        Producto actualizado = new Producto();
        actualizado.setCodigo("NEW123");
        actualizado.setNombre("Producto actualizado");
        actualizado.setDescripcion("Nuevo");
        actualizado.setPrecio(BigDecimal.valueOf(25));
        actualizado.setCategoria("Lácteos");
        actualizado.setActivo(true);

        // CONFIGURACIÓN DE MOCKS:
        // El producto con ID 1 existe
        when(productoRepository.findById(1L)).thenReturn(Optional.of(existente));
        // El nuevo código no está en uso
        when(productoRepository.findByCodigo("NEW123")).thenReturn(Optional.empty());
        // El repositorio devuelve el objeto que se le pasa para guardar
        when(productoRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        // EJECUCIÓN: Actualizamos el producto
        Optional<Producto> result = productoService.actualizar(1L, actualizado);

        // VERIFICACIÓN: Los datos se actualizaron correctamente
        assertTrue(result.isPresent()); // La actualización fue exitosa
        assertEquals("NEW123", result.get().getCodigo()); // Código actualizado
        assertEquals("Producto actualizado", result.get().getNombre()); // Nombre actualizado
    }
}