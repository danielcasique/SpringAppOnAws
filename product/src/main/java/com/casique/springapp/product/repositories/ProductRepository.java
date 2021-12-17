package com.casique.springapp.product.repositories;

import com.casique.springapp.product.entities.Product;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface ProductRepository<P> extends CrudRepository<Product, Long> {
    List<Product> findByName(String name);
}
