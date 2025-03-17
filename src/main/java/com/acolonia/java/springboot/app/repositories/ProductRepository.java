package com.acolonia.java.springboot.app.repositories;

import com.acolonia.java.springboot.app.models.Product;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ProductRepository extends ReactiveMongoRepository<Product,String> {
}
