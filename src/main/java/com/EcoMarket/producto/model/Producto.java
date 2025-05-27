package com.EcoMarket.producto.model;
import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



/**
 * @Entity: Marca la clase como una entidad JPA que se mapea a una tabla de BD
 * @Table: Especifica el nombre de la tabla en la base de datos
 * @Data: Lombok - Genera automáticamente getters, setters, toString, equals y hashCode
 * @NoArgsConstructor: Lombok - Genera constructor sin parámetros (requerido por JPA)
 * @AllArgsConstructor: Lombok - Genera constructor con todos los parámetros
 */

@Entity
@Table(name = "Producto")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String codigo;
    
    @Column(nullable = false)
    private String nombre;
    
    @Column(length = 1000)
    private String descripcion;
    
    @Column(nullable = false)
    private BigDecimal precio;
    
    @Column(name = "categoria")
    private String categoria;
       
    @Column(name = "fecha_creacion")
    private java.time.LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_actualizacion")
    private java.time.LocalDateTime fechaActualizacion;
    
    @Column(name = "activo", nullable = false)
    private boolean activo;

        @PrePersist
    public void prePersist() {
        this.fechaCreacion = java.time.LocalDateTime.now();
        this.fechaActualizacion = java.time.LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.fechaActualizacion = java.time.LocalDateTime.now();
    }
}
