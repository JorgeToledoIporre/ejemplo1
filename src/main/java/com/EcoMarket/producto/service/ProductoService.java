package com.EcoMarket.producto.service;

import com.EcoMarket.producto.model.Producto;
import com.EcoMarket.producto.repository.ProductoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * @Service: Marca la clase como un componente de servicio de Spring
 * @Transactional: Todas las operaciones del servicio se ejecutan en transacciones
 */

@Transactional
@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    public Optional<Producto> obtenerPorId(Long id) {
        return productoRepository.findById(id);
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
    public void setProductoRepository(ProductoRepository productoRepository){
        this.productoRepository = productoRepository;
    }
}
