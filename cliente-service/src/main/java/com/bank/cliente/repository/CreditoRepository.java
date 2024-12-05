package com.bank.cliente.repository;

import com.bank.cliente.model.Credito;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface CreditoRepository extends ReactiveMongoRepository<Credito, String> {
}
