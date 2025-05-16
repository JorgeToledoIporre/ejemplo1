package com.EcoMarket.producto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.EcoMarket.producto.model.Inventario;
import com.EcoMarket.producto.model.Producto;

import java.util.Optional;
import java.util.List;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Long> {
    
    Optional<Inventario> findByProducto(Producto producto);
    
    Optional<Inventario> findByProductoId(Long productoId);
    
    List<Inventario> findByEstado(String estado);
    
    List<Inventario> findByCantidadLessThanEqual(Integer cantidad);
}