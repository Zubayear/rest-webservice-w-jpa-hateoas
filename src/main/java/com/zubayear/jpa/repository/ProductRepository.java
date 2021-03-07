package com.zubayear.jpa.repository;

import com.zubayear.jpa.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findProductByCustomer_Id(Long id);
}