package com.bank.movimiento.repository;

import com.bank.movimiento.model.Movimiento;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovimientoRepository extends ReactiveMongoRepository<Movimiento, String> {
}

