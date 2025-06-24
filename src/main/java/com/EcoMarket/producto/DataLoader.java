package com.EcoMarket.producto;

// Importaciones necesarias para el funcionamiento de la clase
import com.EcoMarket.producto.model.Producto;
import com.EcoMarket.producto.model.Inventario;
import com.EcoMarket.producto.repository.ProductoRepository;
import com.EcoMarket.producto.repository.InventarioRepository;
import net.datafaker.Faker; // Librería para generar datos falsos de prueba
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner; // Interfaz que ejecuta código al iniciar la aplicación
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

/**
 * CLASE DATALOADER - CARGADOR DE DATOS DE PRUEBA
 * 
 * Esta clase se encarga de cargar datos de prueba en la base de datos
 * cuando la aplicación se ejecuta en el perfil "dev" (desarrollo).
 * 
 * @Profile("dev") - Solo se ejecuta cuando el perfil activo es "dev"
 * @Component - Spring la reconoce como un componente y la gestiona
 * CommandLineRunner - Interfaz que permite ejecutar código al iniciar la app
 */
@Profile("dev") // Solo funciona en perfil de desarrollo
@Component // Spring gestiona esta clase como un bean
public class DataLoader implements CommandLineRunner {

    // INYECCIÓN DE DEPENDENCIAS
    // Spring automáticamente inyecta estas dependencias
    @Autowired
    private ProductoRepository productoRepo; // Repositorio para operaciones con productos

    @Autowired
    private InventarioRepository inventarioRepo; // Repositorio para operaciones con inventario

    /**
     * MÉTODO PRINCIPAL QUE SE EJECUTA AL INICIAR LA APLICACIÓN
     * 
     * Este método se ejecuta automáticamente cuando Spring Boot inicia
     * y el perfil "dev" está activo.
     */
    @Override
    public void run(String... args) {
        // INICIALIZACIÓN DE HERRAMIENTAS PARA GENERAR DATOS FALSOS
        Faker faker = new Faker(); // Genera datos realistas pero falsos
        Random rand = new Random(); // Genera números aleatorios

        // BUCLE PARA GENERAR 10 PRODUCTOS DE PRUEBA
        for (int i = 0; i < 10; i++) {
            
            // CREACIÓN Y CONFIGURACIÓN DE UN PRODUCTO
            Producto p = new Producto();
            
            // Genera un código único: "P" + 8 caracteres aleatorios en mayúsculas
            p.setCodigo("P" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            
            // Genera un nombre de producto realista (ej: "Smartphone", "Laptop")
            p.setNombre(faker.commerce().productName());
            
            // Genera una descripción usando texto Lorem Ipsum
            p.setDescripcion(faker.lorem().sentence());
            
            // Genera un precio aleatorio entre 1 y 100 con 2 decimales
            p.setPrecio(BigDecimal.valueOf(faker.number().randomDouble(2, 1, 100)));
            
            // Genera una categoría de producto (ej: "Electronics", "Books")
            p.setCategoria(faker.commerce().department());
            
            // Establece las fechas de creación y actualización al momento actual
            p.setFechaCreacion(LocalDateTime.now());
            p.setFechaActualizacion(LocalDateTime.now());
            
            // Asigna aleatoriamente si el producto está activo o no
            p.setActivo(faker.bool().bool());
            
            // GUARDA EL PRODUCTO EN LA BASE DE DATOS
            productoRepo.save(p);

            // CREACIÓN DE INVENTARIO ASOCIADO AL PRODUCTO
            Inventario inv = new Inventario();
            
            // Vincula el inventario con el producto recién creado
            inv.setProducto(p);
            
            // Genera una cantidad aleatoria entre 0 y 99
            int cantidad = rand.nextInt(100);
            inv.setCantidad(cantidad);
            
            // Establece cantidad mínima aleatoria entre 0 y 9
            inv.setCantidadMinima(rand.nextInt(10));
            
            // Genera una ubicación falsa (nombre de ciudad)
            inv.setUbicacion(faker.address().cityName());
            
            // Establece la fecha de actualización
            inv.setFechaActualizacion(LocalDateTime.now());
            
            // Actualiza automáticamente el estado según la cantidad
            // (AGOTADO si cantidad = 0, BAJO_STOCK si cantidad < mínima, DISPONIBLE en otros casos)
            inv.actualizarEstado();
            
            // GUARDA EL INVENTARIO EN LA BASE DE DATOS
            inventarioRepo.save(inv);
        }

        // MENSAJE DE CONFIRMACIÓN EN LA CONSOLA
        System.out.println("Datos de prueba cargados (perfil dev)");
    }
}