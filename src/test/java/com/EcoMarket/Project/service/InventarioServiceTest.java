package com.EcoMarket.Project.service;

import com.EcoMarket.producto.model.Inventario;
import com.EcoMarket.producto.repository.InventarioRepository;
import com.EcoMarket.producto.repository.ProductoRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class InventarioServiceTest {

    @Autowired
    private InventarioRepository inventarioRepository;

    @Autowired
    private ProductoRepository productoRepository;

    public List<Inventario> listarTodos() {
        return inventarioRepository.findAll();
    }

    public Optional<Inventario> obtenerPorId(Long id) {
        return inventarioRepository.findById(id);
    }

    public Inventario guardar(Inventario inventario) {
        if (inventario.getProducto() == null || inventario.getProducto().getId() == null) {
            throw new IllegalArgumentException("Debe proporcionar un producto válido");
        }
        if (!productoRepository.existsById(inventario.getProducto().getId())) {
            throw new RuntimeException("Producto no encontrado");
        }
        inventario.setFechaActualizacion(LocalDateTime.now());
        inventario.actualizarEstado(); // lógica de estado
        return inventarioRepository.save(inventario);
    }

    public Optional<Inventario> actualizar(Long id, Inventario inventarioActualizado) {
        return inventarioRepository.findById(id).map(inventario -> {
            if (inventarioActualizado.getProducto() == null || inventarioActualizado.getProducto().getId() == null) {
                throw new IllegalArgumentException("Debe proporcionar un producto válido");
            }
            if (!productoRepository.existsById(inventarioActualizado.getProducto().getId())) {
                throw new RuntimeException("Producto no encontrado");
            }
            inventario.setProducto(inventarioActualizado.getProducto());
            inventario.setCantidad(inventarioActualizado.getCantidad());
            inventario.setCantidadMinima(inventarioActualizado.getCantidadMinima());
            inventario.setUbicacion(inventarioActualizado.getUbicacion());
            inventario.setFechaActualizacion(LocalDateTime.now());
            inventario.actualizarEstado();
            return inventarioRepository.save(inventario);
        });
    }

    public boolean eliminar(Long id) {
        if (inventarioRepository.existsById(id)) {
            inventarioRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
