package com.EcoMarket.producto.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "Inventario")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Inventario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;
    
    @Column(nullable = false)
    private Integer cantidad;
    
    @Column(name = "cantidad_minima")
    private Integer cantidadMinima;
    
    @Column(name = "ubicacion")
    private String ubicacion;
    
    @Column(name = "fecha_actualizacion")
    private java.time.LocalDateTime fechaActualizacion;
    
    // Estado del inventario (disponible, agotado, bajo_stock)
    @Column(name = "estado")
    private String estado;
    
    // Método auxiliar para calcular el estado según la cantidad y cantidadMinima
    public void actualizarEstado() {
        if (cantidad <= 0) {
            estado = "AGOTADO";
        } else if (cantidadMinima != null && cantidad <= cantidadMinima) {
            estado = "BAJO_STOCK";
        } else {
            estado = "DISPONIBLE";
        }
    }
}
