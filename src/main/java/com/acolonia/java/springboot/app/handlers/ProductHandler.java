package com.acolonia.java.springboot.app.handlers;

import com.acolonia.java.springboot.app.models.Product;
import com.acolonia.java.springboot.app.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.LocalDateTime;

@Component
public class ProductHandler {

    private final ProductService service;
    private final Validator validator

    @Autowired
    public ProductHandler(ProductService service, Validator validator) {
        this.service = service;
        this.validator = validator;
    }

    public Mono<ServerResponse> list(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.findAll(), Product.class);
    }

    public Mono<ServerResponse> details(ServerRequest request){
        String id = request.pathVariable("id");
        return service.findById(id).flatMap(product ->
            ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(product)
                    .switchIfEmpty(ServerResponse.notFound().build())
        );
    }

    public Mono<ServerResponse> create (ServerRequest request) {
        Mono<Product> productMono = request.bodyToMono(Product.class);
        return productMono.flatMap(product -> {

            //Manejo de errores y validaciones
            Errors errors = new BeanPropertyBindingResult(product, Product.class.getName());
            validator.validate(product,errors);
            if (errors.hasErrors()){
                return Flux.fromIterable(errors.getFieldErrors())
                        .map(fieldError -> "El campo ".concat(fieldError.getField().concat(" ").concat(fieldError.getDefaultMessage())))
                        .collectList()
                        .flatMap(list -> ServerResponse.badRequest().bodyValue(list));
            }

            product.setCreateAt(LocalDateTime.now());
            return service.save(product).flatMap(productDb ->
                    ServerResponse.created(URI.create("/products/".concat(productDb.getId())))
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(productDb));

        });
    }

    public Mono<ServerResponse> update (ServerRequest request) {

        Mono<Product> productMono = request.bodyToMono(Product.class);
        String id = request.pathVariable("id");
        Mono<Product> productDb = service.findById(id);

        return productDb.zipWith(productMono, (db, req) -> {
            db.setName(req.getName());
            db.setPrice(req.getPrice());
            db.setCategory(req.getCategory());
            return db;
        })
                .flatMap(product -> ServerResponse.created(URI.create("/products/".concat(product.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(service.save(product), Product.class))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> delete (ServerRequest request) {

        String id = request.pathVariable("id");
        Mono<Product> productMono = service.findById(id);

        return productMono.flatMap(productdb -> ServerResponse.status(HttpStatus.NO_CONTENT)
                .body(service.delete(productdb), Void.class))
                .switchIfEmpty(ServerResponse.notFound().build());
    }
}
