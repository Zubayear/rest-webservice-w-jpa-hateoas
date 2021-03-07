package com.zubayear.jpa.assemblers;

import com.zubayear.jpa.controllers.OrderController;
import com.zubayear.jpa.entity.Order;
import com.zubayear.jpa.entity.Status;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class OrderAssembler implements RepresentationModelAssembler<Order, EntityModel<Order>> {
    @Override
    public EntityModel<Order> toModel(Order order) {
        EntityModel<Order> orderEntityModel =
                EntityModel.of(order,
                        linkTo(methodOn(OrderController.class).allOrders()).withRel("all-orders"),
                        linkTo(methodOn(OrderController.class).getOrder(order.getId())).withSelfRel());
        if (order.getStatus() == Status.IN_PROGRESS) {
            orderEntityModel.add(linkTo(methodOn(OrderController.class).cancel(order.getId())).withRel("cancel"));
            orderEntityModel.add(linkTo(methodOn(OrderController.class).complete(order.getId())).withRel("complete"));
        }
        return orderEntityModel;
    }
}
