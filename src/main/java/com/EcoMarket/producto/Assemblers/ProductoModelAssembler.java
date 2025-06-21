package com.EcoMarket.producto.Assemblers;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import com.EcoMarket.producto.model.Producto;
import com.EcoMarket.producto.controller.ProductoControllerV2;

@Component
public class ProductoModelAssembler implements  RepresentationModelAssembler<Producto, EntityModel<Producto>>{

    @Override
    public EntityModel<Producto> toModel(Producto producto){
        return EntityModel.of(producto,
        linkTo(methodOn(ProductoControllerV2.class).obtenerPorId(producto.getId())).withSelfRel(),
        linkTo(methodOn(ProductoControllerV2.class).Listar()).withRel("usuarios"),
        linkTo(methodOn(ProductoControllerV2.class).actualizar(producto.getId(), producto)).withRel("actualizar"));
    }
}
