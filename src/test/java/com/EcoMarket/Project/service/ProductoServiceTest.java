package com.EcoMarket.Project.service;

import com.EcoMarket.producto.model.Producto;
import com.EcoMarket.producto.repository.ProductoRepository;
import com.EcoMarket.producto.service.ProductoService;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
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
    void obtenerPorId(){
        Producto producto = new Producto();
        producto.setId(1L);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        Optional<Producto> resultado =productoService.obtenerPorId(1L); 

        assertTrue(resultado.isPresent() );
        assertEquals(1L, resultado.get().getId());
    }

    public Optional<Producto> obtenerPorCodigo(String codigo) {
        return productoRepository.findByCodigo(codigo);
    }

    public List<Producto> buscarPorNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre);
    }

    public List<Producto> listarActivos() {
        return productoRepository.findByActivoTrue();
    }

    public List<Producto> buscarPorCategoria(String categoria) {
        return productoRepository.findByCategoria(categoria);
    }

    public Producto guardar(Producto producto) {
        //verificación de codigo
        if (productoRepository.findByCodigo(producto.getCodigo()).isPresent()) {
            throw new RuntimeException("El código ya existe");
    }
        return productoRepository.save(producto);
    }

    public boolean eliminar(Long id) {
        if (productoRepository.existsById(id)) {
            productoRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    public Optional<Producto> actualizar(Long id, Producto productoActualizado) {
    return productoRepository.findById(id).map(producto -> {
        // Si se cambia el código, verificar que no esté duplicado
        if (!producto.getCodigo().equals(productoActualizado.getCodigo())) {
            if (productoRepository.findByCodigo(productoActualizado.getCodigo()).isPresent()) {
                throw new RuntimeException("El código ya existe");
            }
            producto.setCodigo(productoActualizado.getCodigo());
            }
        producto.setCodigo(productoActualizado.getCodigo());
        producto.setNombre(productoActualizado.getNombre());
        producto.setDescripcion(productoActualizado.getDescripcion());
        producto.setPrecio(productoActualizado.getPrecio());
        producto.setCategoria(productoActualizado.getCategoria());
        producto.setActivo(productoActualizado.isActivo());
        producto.setFechaActualizacion(LocalDateTime.now());
        return productoRepository.save(producto);
    });
    }
}
