package com.bank.tarjeta.repository;

import com.bank.tarjeta.model.Tarjeta;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TarjetaRepository extends ReactiveCrudRepository<Tarjeta, String> {
}