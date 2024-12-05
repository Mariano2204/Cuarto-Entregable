package com.bank.cuenta.service;

import com.bank.cuenta.model.Movimiento;
import com.bank.cuenta.repository.MovimientoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class MovimientoService {

    @Autowired
    private MovimientoRepository movimientoRepository;

    public Flux<Movimiento> findAll() {
        return movimientoRepository.findAll();
    }

    public Mono<Movimiento> findById(String id) {
        return movimientoRepository.findById(id);
    }

    public Mono<Movimiento> save(Movimiento movimiento) {
        return movimientoRepository.save(movimiento);
    }

    public Mono<Movimiento> update(String id, Movimiento movimiento) {
        return movimientoRepository.findById(id)
                .flatMap(existingMovimiento -> {
                    existingMovimiento.setFecha(movimiento.getFecha());
                    existingMovimiento.setTipoMovimiento(movimiento.getTipoMovimiento());
                    existingMovimiento.setMonto(movimiento.getMonto());
                    return movimientoRepository.save(existingMovimiento);
                });
    }

    public Mono<Void> delete(String id) {
        return movimientoRepository.deleteById(id);
    }
}