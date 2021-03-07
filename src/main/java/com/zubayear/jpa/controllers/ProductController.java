package com.zubayear.jpa.controllers;

import com.zubayear.jpa.assemblers.ProductAssembler;
import com.zubayear.jpa.entity.Product;
import com.zubayear.jpa.exceptions.ProductNotFoundException;
import com.zubayear.jpa.repository.ProductRepository;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class ProductController {

    private final ProductRepository productRepository;
    private final ProductAssembler productAssembler;

    public ProductController(ProductRepository productRepository, ProductAssembler productAssembler) {
        this.productRepository = productRepository;
        this.productAssembler = productAssembler;
    }

    @RequestMapping(path = "/products", method = RequestMethod.GET)
    public CollectionModel<EntityModel<Product>> allProducts() {
        List<EntityModel<Product>> products = productRepository.findAll()
                .stream().map(productAssembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(products,
                linkTo(methodOn(ProductController.class).allProducts()).withSelfRel());
    }

    @RequestMapping(path = "/products", method = RequestMethod.POST)
    public ResponseEntity<?> addProduct(@RequestBody Product product) {
        EntityModel<Product> productEntityModel =
                productAssembler.toModel(productRepository.save(product));
        return ResponseEntity
                .created(productEntityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(productEntityModel);
    }

    @RequestMapping(path = "/products/{id}", method = RequestMethod.GET)
    public EntityModel<Product> getProduct(@PathVariable Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        return productAssembler.toModel(product);
    }

    @RequestMapping(path = "/products/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> editProduct(@PathVariable Long id, @RequestBody Product newProduct) {
        Product updatedProduct = productRepository.findById(id)
                .map(product -> {
                    product.setProductName(newProduct.getProductName());
                    product.setProductPrice(newProduct.getProductPrice());
                    return product;
                })
                .orElseGet(() -> {
                    newProduct.setId(id);
                    return productRepository.save(newProduct);
                });
        EntityModel<Product> productEntityModel = productAssembler.toModel(updatedProduct);
        return ResponseEntity
                .created(productEntityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(productEntityModel);
    }

    @RequestMapping(path = "/products/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        productRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
