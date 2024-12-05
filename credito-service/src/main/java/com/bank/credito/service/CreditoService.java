package com.bank.credito.service;

import com.bank.credito.model.Credito;
import com.bank.credito.model.Movimiento;
import com.bank.credito.model.enums.TipoMovimiento;
import com.bank.credito.repository.CreditoRepository;
import com.bank.credito.repository.MovimientoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CreditoService {

    @Autowired
    private CreditoRepository creditoRepository;

    @Autowired
    private MovimientoRepository movimientoRepository;

    public Flux<Credito> findAll() {
        return creditoRepository.findAll();
    }

    public Mono<Credito> findById(String id) {
        return creditoRepository.findById(id);
    }

    public Mono<Credito> save(Credito credito) {
        return creditoRepository.save(credito);
    }

    public Mono<Credito> update(String id, Credito credito) {
        return creditoRepository.findById(id)
                .flatMap(existingCredito -> {
                    existingCredito.setTipoCredito(credito.getTipoCredito());
                    existingCredito.setLimiteCredito(credito.getLimiteCredito());
                    existingCredito.setSaldo(credito.getSaldo());
                    existingCredito.setDeuda(credito.getDeuda());
                    existingCredito.setDeudaVencida(credito.isDeudaVencida());
                    return creditoRepository.save(existingCredito);
                });
    }

    public Mono<Void> delete(String id) {
        return creditoRepository.deleteById(id);
    }

    public Mono<Credito> addMovimientoToCredito(String creditoId, Movimiento movimiento) {
        return creditoRepository.findById(creditoId)
                .flatMap(credito -> {
                    if (movimiento.getTipoMovimiento() == TipoMovimiento.PAGO) {
                        credito.setSaldo(credito.getSaldo().subtract(movimiento.getMonto()));
                    }
                    movimiento.setCreditoId(creditoId);
                    return movimientoRepository.save(movimiento)
                            .flatMap(savedMovimiento -> {
                                credito.getMovimientos().add(savedMovimiento);
                                return creditoRepository.save(credito);
                            });
                });
    }
}