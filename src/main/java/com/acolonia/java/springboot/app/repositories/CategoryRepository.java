package com.acolonia.java.springboot.app.repositories;

import com.acolonia.java.springboot.app.models.Category;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface CategoryRepository extends ReactiveMongoRepository<Category,String> {
}
