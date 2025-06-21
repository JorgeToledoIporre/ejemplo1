package com.EcoMarket.producto.Assemblers;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.EcoMarket.producto.controller.InventarioControllerV2;
import com.EcoMarket.producto.model.Inventario;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;


@Component
public class InventarioAssembler implements RepresentationModelAssembler<Inventario, EntityModel<Inventario>> {

    @Override
    public EntityModel<Inventario> toModel(Inventario inventario){
        return EntityModel.of(inventario,
        linkTo(methodOn(InventarioControllerV2.class).Listar()).withRel("listar"),
        linkTo(methodOn(InventarioControllerV2.class).obtenerPorId(inventario.getId())).withSelfRel(),
        linkTo(methodOn(InventarioControllerV2.class).actualizar(inventario.getId(), inventario)).withRel("actualizar")
        );
    }   
}
