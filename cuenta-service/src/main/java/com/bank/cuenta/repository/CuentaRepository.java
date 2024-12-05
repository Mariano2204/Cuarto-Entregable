package com.bank.cuenta.repository;

import com.bank.cuenta.model.Cuenta;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CuentaRepository extends ReactiveMongoRepository<Cuenta, String> {
}
