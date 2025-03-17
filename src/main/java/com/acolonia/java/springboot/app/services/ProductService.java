package com.acolonia.java.springboot.app.services;

import com.acolonia.java.springboot.app.models.Category;
import com.acolonia.java.springboot.app.models.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductService {
    Flux<Product> findAll();
    Mono<Product> findById(String id);
    Mono<Product> save (Product product);
    Mono<Void> delete(Product product);
    Mono<Category> saveCategory(Category category);
}
