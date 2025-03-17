package com.acolonia.java.springboot.app;

import com.acolonia.java.springboot.app.models.Category;
import com.acolonia.java.springboot.app.models.Product;
import com.acolonia.java.springboot.app.services.ProductService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

@SpringBootApplication
public class SpringbootWebfluxApplication implements CommandLineRunner {

	private final ProductService service;
	private final ReactiveMongoTemplate mongoTemplate;

    public SpringbootWebfluxApplication(ProductService service, ReactiveMongoTemplate mongoTemplate) {
        this.service = service;
        this.mongoTemplate = mongoTemplate;
    }

    public static void main(String[] args) {
		SpringApplication.run(SpringbootWebfluxApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		//eliminación de tablas al iniciar la aplicación
		this.mongoTemplate.dropCollection("products");
		this.mongoTemplate.dropCollection("categories");

		Category electronics = new Category("Electronico");
		Category sports = new Category("Deporte");
		Category computing = new Category("Computación");
		Category funiture = new Category("Muebles");

		Flux.just(electronics, sports, computing, funiture)
				.flatMap(service::saveCategory)
				.doOnNext(category ->
						System.out.println("Categoria creada. ".concat(category.getName().concat(" ".concat(category.getId())))))
				.thenMany(
						Flux.just(
								new Product("Tv panasonic", 342.67, electronics),
								new Product("Sonic Camara", 500.99, electronics),
								new Product("Apple ipd", 245.89, electronics),
								new Product("NoteBook Sony", 2000.67, computing),
								new Product("HP Multifuncional Impresora", 600.55, computing),
								new Product("Bicicleta", 1589.00, sports),
								new Product("Mueble mica 5 cajones", 350.00, funiture)
						).flatMap(product -> {
							product.setCreateAt(LocalDateTime.now());
							return service.save(product);
						})
				).subscribe(product -> System.out.println("Insert: ".concat(product.getId()).concat(" ".concat(product.getName()))));
	}
}
