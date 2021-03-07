package com.zubayear.jpa.assemblers;

import com.zubayear.jpa.controllers.ProductController;
import com.zubayear.jpa.entity.Product;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ProductAssembler implements RepresentationModelAssembler<Product, EntityModel<Product>> {

    @Override
    public EntityModel<Product> toModel(Product product) {
        return EntityModel.of(product,
                linkTo(methodOn(ProductController.class).getProduct(product.getId())).withSelfRel(),
                linkTo(methodOn(ProductController.class).allProducts()).withRel("all-products"));
    }
}
