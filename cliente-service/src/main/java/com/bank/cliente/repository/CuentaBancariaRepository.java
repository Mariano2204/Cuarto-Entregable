package com.bank.cliente.repository;

import com.bank.cliente.model.CuentaBancaria;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface CuentaBancariaRepository extends ReactiveMongoRepository<CuentaBancaria, String> {
    Flux<CuentaBancaria> findByClienteId(String clienteId);
}