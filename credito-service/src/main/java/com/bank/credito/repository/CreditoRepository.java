package com.bank.credito.repository;

import com.bank.credito.model.Credito;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CreditoRepository extends ReactiveMongoRepository<Credito, String> {
}