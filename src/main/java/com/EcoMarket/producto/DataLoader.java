package com.EcoMarket.producto;

import com.EcoMarket.producto.model.Producto;
import com.EcoMarket.producto.model.Inventario;
import com.EcoMarket.producto.repository.ProductoRepository;
import com.EcoMarket.producto.repository.InventarioRepository;
import net.datafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;

@Profile("dev")
@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private ProductoRepository productoRepo;

    @Autowired
    private InventarioRepository inventarioRepo;

    @Override
    public void run(String... args) {
        Faker faker = new Faker();
        Random rand = new Random();

        // Generar productos
        for (int i = 0; i < 10; i++) {
            Producto p = new Producto();
            p.setCodigo("P" + faker.number().digits(4));
            p.setNombre(faker.commerce().productName());
            p.setDescripcion(faker.lorem().sentence());
            p.setPrecio(BigDecimal.valueOf(faker.number().randomDouble(2, 1, 100)));
            p.setCategoria(faker.commerce().department());
            p.setFechaCreacion(LocalDateTime.now());
            p.setFechaActualizacion(LocalDateTime.now());
            p.setActivo(faker.bool().bool());
            productoRepo.save(p);

            // Generar inventario asociado
            Inventario inv = new Inventario();
            inv.setProducto(p);
            int cantidad = rand.nextInt(100);
            inv.setCantidad(cantidad);
            inv.setCantidadMinima(rand.nextInt(10));
            inv.setUbicacion(faker.address().cityName());
            inv.setFechaActualizacion(LocalDateTime.now());
            inv.actualizarEstado(); // Establece el estado (AGOTADO, BAJO_STOCK, DISPONIBLE)
            inventarioRepo.save(inv);
        }

        System.out.println("Datos de prueba cargados (perfil dev)");
    }
}
