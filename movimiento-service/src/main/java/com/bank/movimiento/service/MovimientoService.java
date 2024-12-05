package com.bank.movimiento.service;

import com.bank.movimiento.model.Movimiento;
import com.bank.movimiento.repository.MovimientoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class MovimientoService {

    @Autowired
    private MovimientoRepository movimientoRepository;

    private final WebClient creditoWebClient;
    private final WebClient cuentaWebClient;

    public MovimientoService(WebClient.Builder webClientBuilder) {
        this.creditoWebClient = webClientBuilder.baseUrl("http://localhost:8083").build();
        this.cuentaWebClient = webClientBuilder.baseUrl("http://localhost:8082").build();
    }

    public Mono<Movimiento> save(Movimiento movimiento) {
        return movimientoRepository.save(movimiento);
    }

    public Mono<Movimiento> assignMovimientoToCredito(Movimiento movimiento) {
        return creditoWebClient.post()
                .uri("/creditos/{creditoId}/movimientos", movimiento.getCreditoId())
                .bodyValue(movimiento)
                .retrieve()
                .bodyToMono(Movimiento.class)
                .thenReturn(movimiento);
    }

    public Mono<Movimiento> assignMovimientoToCuenta(Movimiento movimiento) {
        return cuentaWebClient.post()
                .uri("/cuentas/{cuentaId}/movimientos", movimiento.getCuentaId())
                .bodyValue(movimiento)
                .retrieve()
                .bodyToMono(Movimiento.class)
                .thenReturn(movimiento);
    }

    public Flux<Movimiento> findAll() {
        return movimientoRepository.findAll();
    }

    public Mono<Movimiento> findById(String id) {
        return movimientoRepository.findById(id);
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