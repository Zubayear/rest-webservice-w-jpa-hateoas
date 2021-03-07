package com.zubayear.jpa.exceptions;

public class OrderNotFoundException extends RuntimeException{
    public OrderNotFoundException(Long id) {
        super("Did not find " + id + " order");
    }
}
