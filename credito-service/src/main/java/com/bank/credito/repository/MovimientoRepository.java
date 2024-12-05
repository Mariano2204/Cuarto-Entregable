package com.bank.credito.repository;

import com.bank.credito.model.Movimiento;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovimientoRepository extends ReactiveMongoRepository<Movimiento, String> {
}
