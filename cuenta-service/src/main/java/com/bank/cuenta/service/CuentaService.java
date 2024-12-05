package com.bank.cuenta.service;

import com.bank.cuenta.model.Cuenta;
import com.bank.cuenta.model.Movimiento;
import com.bank.cuenta.model.enums.TipoMovimiento;
import com.bank.cuenta.repository.CuentaRepository;
import com.bank.cuenta.repository.MovimientoRepository ;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;
import java.util.ArrayList;

@Service
public class CuentaService {

    @Autowired
    private CuentaRepository cuentaRepository;

    @Autowired
    private MovimientoRepository movimientoRepository;

    public Flux<Cuenta> findAll() {
        return cuentaRepository.findAll();
    }

    public Mono<Cuenta> findById(String id) {
        return cuentaRepository.findById(id);
    }

    public Mono<Cuenta> save(Cuenta cuenta) {
        return cuentaRepository.save(cuenta);
    }

    public Mono<Cuenta> update(String id, Cuenta cuenta) {
        return cuentaRepository.findById(id)
                .flatMap(existingCuenta -> {
                    existingCuenta.setNumeroCuenta(cuenta.getNumeroCuenta());
                    existingCuenta.setTipoCuenta(cuenta.getTipoCuenta());
                    existingCuenta.setSaldo(cuenta.getSaldo());
                    existingCuenta.setMovimientos(cuenta.getMovimientos());
                    existingCuenta.setMaxTransaccionesSinComision(cuenta.getMaxTransaccionesSinComision());
                    existingCuenta.setComisionPorTransaccion(cuenta.getComisionPorTransaccion());
                    return cuentaRepository.save(existingCuenta);
                });
    }

    public Mono<Void> delete(String id) {
        return cuentaRepository.deleteById(id);
    }

    public Mono<Void> transferirEntreCuentas(String cuentaOrigenId, String cuentaDestinoId, double monto) {
        return cuentaRepository.findById(cuentaOrigenId)
                .flatMap(cuentaOrigen -> {
                    if (cuentaOrigen.getSaldo().doubleValue() < monto) {
                        return Mono.error(new RuntimeException("Saldo insuficiente en la cuenta de origen."));
                    }
                    return cuentaRepository.findById(cuentaDestinoId)
                            .flatMap(cuentaDestino -> {
                                cuentaOrigen.setSaldo(cuentaOrigen.getSaldo().subtract(BigDecimal.valueOf(monto)));
                                cuentaDestino.setSaldo(cuentaDestino.getSaldo().add(BigDecimal.valueOf(monto)));
                                return cuentaRepository.save(cuentaOrigen)
                                        .then(cuentaRepository.save(cuentaDestino))
                                        .then();
                            });
                });
    }

    public Mono<Void> transferirATerceros(String cuentaOrigenId, String cuentaDestinoId, double monto) {
        return transferirEntreCuentas(cuentaOrigenId, cuentaDestinoId, monto);
    }

    public Mono<Cuenta> addMovimientoToCuenta(String cuentaId, Movimiento movimiento) {
        return cuentaRepository.findById(cuentaId)
                .flatMap(cuenta -> {
                    if (movimiento.getTipoMovimiento() == TipoMovimiento.DEPOSITO) {
                        cuenta.setSaldo(cuenta.getSaldo().add(movimiento.getMonto()));
                    } else if (movimiento.getTipoMovimiento() == TipoMovimiento.RETIRO) {
                        cuenta.setSaldo(cuenta.getSaldo().subtract(movimiento.getMonto()));
                    }
                    movimiento.setCuentaId(cuentaId);
                    if (cuenta.getMovimientos() == null) {
                        cuenta.setMovimientos(new ArrayList<>());
                    }
                    return movimientoRepository.save(movimiento)
                            .flatMap(savedMovimiento -> {
                                cuenta.getMovimientos().add(savedMovimiento);
                                return cuentaRepository.save(cuenta);
                            });
                });
    }
}