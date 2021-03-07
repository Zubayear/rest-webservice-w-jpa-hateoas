package com.zubayear.jpa.controllers;

import com.zubayear.jpa.assemblers.CustomerAssembler;
import com.zubayear.jpa.entity.Customer;
import com.zubayear.jpa.entity.Product;
import com.zubayear.jpa.exceptions.CustomerNotFoundException;
import com.zubayear.jpa.repository.CustomerRepository;
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
public class CustomerController {

    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final CustomerAssembler customerAssembler;

    public CustomerController(CustomerRepository customerRepository,
                              ProductRepository productRepository,
                              CustomerAssembler customerAssembler) {
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.customerAssembler = customerAssembler;
    }

    @RequestMapping(value = "/customers", method = RequestMethod.GET)
    public CollectionModel<EntityModel<Customer>> allCustomers() {
        List<EntityModel<Customer>> customers = customerRepository.findAll()
                .stream().map(customerAssembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(customers, linkTo(methodOn(CustomerController.class).allCustomers()).withSelfRel());
    }

    @RequestMapping(value = "/customers/{id}", method = RequestMethod.GET)
    public EntityModel<Customer> getCustomer(@PathVariable Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));
        return customerAssembler.toModel(customer);
    }

    @RequestMapping(value = "/customers", method = RequestMethod.POST)
    ResponseEntity<?> addCustomer(@RequestBody Customer customer) {
        EntityModel<Customer> customerEntityModel =
                customerAssembler.toModel(customerRepository.save(customer));
        return ResponseEntity
                .created(customerEntityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(customerEntityModel);
    }

    @RequestMapping(path = "/customers/{id}", method = RequestMethod.PUT)
    ResponseEntity<?> editCustomer(@PathVariable Long id, @RequestBody Customer newCustomer) {
        Customer updatedCustomer = customerRepository.findById(id)
                .map(customer -> {
                    customer.setFirstName(newCustomer.getFirstName());
                    customer.setLastName(newCustomer.getLastName());
                    return customer;
                })
                .orElseGet(() -> {
                    newCustomer.setId(id);
                    return customerRepository.save(newCustomer);
                });
        EntityModel<Customer> customerEntityModel =
                customerAssembler.toModel(updatedCustomer);
        return ResponseEntity
                .created(customerEntityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(customerEntityModel);
    }

    @RequestMapping(path = "/customers/{id}", method = RequestMethod.DELETE)
    ResponseEntity<?> deleteCustomer(@PathVariable Long id) {
        customerRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(path = "/customers/{id}/products", method = RequestMethod.GET)
    public List<Product> getProductsOfCustomer(@PathVariable Long id) {
        return productRepository.findProductByCustomer_Id(id);
    }
}