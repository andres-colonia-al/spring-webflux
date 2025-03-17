package com.acolonia.java.springboot.app.services;

import com.acolonia.java.springboot.app.models.Category;
import com.acolonia.java.springboot.app.models.Product;
import com.acolonia.java.springboot.app.repositories.CategoryRepository;
import com.acolonia.java.springboot.app.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ProductServiceImpl implements ProductService{

    private final ProductRepository repository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository repository, CategoryRepository categoryRepository) {
        this.repository = repository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Flux<Product> findAll() {
        return repository.findAll();
    }

    @Override
    public Mono<Product> findById(String id) {
        return repository.findById(id);
    }

    @Override
    public Mono<Product> save(Product product) {
        return repository.save(product);
    }

    @Override
    public Mono<Void> delete(Product product) {
        return repository.delete(product);
    }

    @Override
    public Mono<Category> saveCategory(Category category) {
        return categoryRepository.save(category);
    }
}
