package com.bank.tarjeta.service;

import com.bank.tarjeta.model.CuentaBancaria;
import com.bank.tarjeta.model.Tarjeta;
import com.bank.tarjeta.model.Movimiento;
import com.bank.tarjeta.repository.TarjetaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TarjetaService {

    @Autowired
    private TarjetaRepository tarjetaRepository;

    private final WebClient cuentaWebClient;

    @Autowired
    public TarjetaService(TarjetaRepository tarjetaRepository, WebClient.Builder cuentaWebClientBuilder) {
        this.tarjetaRepository = tarjetaRepository;
        this.cuentaWebClient = cuentaWebClientBuilder.baseUrl("http://localhost:8082").build();
    }

    private static final int NUMERO_MAXIMO_MOVIMIENTOS = 10;

    public Flux<Tarjeta> findAll() {
        return tarjetaRepository.findAll();
    }

    public Mono<Tarjeta> findById(String id) {
        return tarjetaRepository.findById(id);
    }

    public Mono<Tarjeta> save(Tarjeta tarjeta) {
        return tarjetaRepository.save(tarjeta);
    }

    public Mono<Tarjeta> update(String id, Tarjeta tarjeta) {
        return tarjetaRepository.findById(id)
                .flatMap(existingTarjeta -> {
                    existingTarjeta.setNumeroTarjeta(tarjeta.getNumeroTarjeta());
                    existingTarjeta.setClienteId(tarjeta.getClienteId());
                    existingTarjeta.setCuentaPrincipalId(tarjeta.getCuentaPrincipalId());
                    existingTarjeta.setCuentasAsociadas(tarjeta.getCuentasAsociadas());
                    existingTarjeta.setMovimientos(tarjeta.getMovimientos());
                    return tarjetaRepository.save(existingTarjeta);
                });
    }

    public Mono<Void> delete(String id) {
        return tarjetaRepository.deleteById(id);
    }

    public Mono<Double> consultarSaldoCuentaPrincipal(String tarjetaId) {
        return tarjetaRepository.findById(tarjetaId)
                .flatMap(tarjeta -> {
                    // Lógica para consultar el saldo de la cuenta principal
                    return cuentaWebClient.get()
                            .uri("/cuentas/{cuentaId}", tarjeta.getCuentaPrincipalId())
                            .retrieve()
                            .bodyToMono(CuentaBancaria.class)
                            .map(cuenta -> cuenta.getSaldo().doubleValue());
                });
    }

    public Mono<List<Movimiento>> obtenerUltimosMovimientos(String tarjetaId) {
        return tarjetaRepository.findById(tarjetaId)
                .map(tarjeta -> {
                    List<Movimiento> movimientos = tarjeta.getMovimientos();
                    return movimientos.stream()
                            .sorted((m1, m2) -> m2.getFecha().compareTo(m1.getFecha()))
                            .limit(NUMERO_MAXIMO_MOVIMIENTOS)
                            .collect(Collectors.toList());
                });
    }

    public Mono<Void> realizarPagoConTarjeta(String tarjetaId, double monto) {
        return tarjetaRepository.findById(tarjetaId)
                .flatMap(tarjeta -> {
                    return realizarPago(tarjeta, monto);
                });
    }

    private Mono<Void> realizarPago(Tarjeta tarjeta, double monto) {
        // Logica para verificar el saldo y realizar el pago
        return cuentaWebClient.get()
                .uri("/cuentas/{cuentaId}", tarjeta.getCuentaPrincipalId())
                .retrieve()
                .bodyToMono(CuentaBancaria.class)
                .flatMap(cuentaPrincipal -> {
                    if (cuentaPrincipal.getSaldo().doubleValue() >= monto) {
                        // Realizar el pago desde la cuenta principal
                        return debitarMonto(cuentaPrincipal, monto)
                                .then(agregarMovimiento(tarjeta, "pago", monto));
                    } else {
                        // Verificar saldo en cuentas asociadas
                        return Flux.fromIterable(tarjeta.getCuentasAsociadas())
                                .flatMap(cuentaId -> cuentaWebClient.get()
                                        .uri("/cuentas/{cuentaId}", cuentaId)
                                        .retrieve()
                                        .bodyToMono(CuentaBancaria.class))
                                .filter(cuenta -> cuenta.getSaldo().doubleValue() > 0)
                                .collectList()
                                .flatMap(cuentas -> {
                                    double montoRestante = monto;
                                    for (CuentaBancaria cuenta : cuentas) {
                                        if (cuenta.getSaldo().doubleValue() >= montoRestante) {
                                            return debitarMonto(cuenta, montoRestante)
                                                    .then(agregarMovimiento(tarjeta, "pago", monto));
                                        } else {
                                            montoRestante -= cuenta.getSaldo().doubleValue();
                                            debitarMonto(cuenta, cuenta.getSaldo().doubleValue()).subscribe();
                                        }
                                    }
                                    return Mono.error(new IllegalArgumentException("Saldo insuficiente en todas las cuentas asociadas"));
                                });
                    }
                });
    }

    private Mono<Void> debitarMonto(CuentaBancaria cuenta, double monto) {
        // Logica para debitar el monto de la cuenta
        cuenta.setSaldo(cuenta.getSaldo().subtract(BigDecimal.valueOf(monto)));
        return cuentaWebClient.put()
                .uri("/cuentas/{cuentaId}", cuenta.getId())
                .bodyValue(cuenta)
                .retrieve()
                .bodyToMono(Void.class);
    }

    private Mono<Void> agregarMovimiento(Tarjeta tarjeta, String tipo, double monto) {
        Movimiento movimiento = new Movimiento();
        movimiento.setTipo(tipo);
        movimiento.setMonto(monto);
        movimiento.setFecha(LocalDateTime.now());
        tarjeta.getMovimientos().add(movimiento);
        return tarjetaRepository.save(tarjeta).then();
    }

    public Mono<Tarjeta> asociarCuenta(String tarjetaId, String cuentaId) {
        return tarjetaRepository.findById(tarjetaId)
                .flatMap(tarjeta -> {
                    tarjeta.getCuentasAsociadas().add(cuentaId);
                    return tarjetaRepository.save(tarjeta);
                });
    }

    public Mono<Tarjeta> establecerCuentaPrincipal(String tarjetaId, String cuentaPrincipalId) {
        return tarjetaRepository.findById(tarjetaId)
                .flatMap(tarjeta -> {
                    tarjeta.setCuentaPrincipalId(cuentaPrincipalId);
                    return tarjetaRepository.save(tarjeta);
                });
    }
}