package com.bank.yanki.repository;

import com.bank.yanki.model.Monedero;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface MonederoRepository extends ReactiveMongoRepository<Monedero, String> {
    Mono<Monedero> findByNumeroCelular(String numeroCelular);
}