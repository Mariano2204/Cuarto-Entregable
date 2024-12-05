package com.bank.cuenta.repository;

import com.bank.cuenta.model.Movimiento;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovimientoRepository extends ReactiveMongoRepository<Movimiento, String> {
}

