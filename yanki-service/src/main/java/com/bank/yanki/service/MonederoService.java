package com.bank.yanki.service;

import com.bank.yanki.model.Monedero;
import com.bank.yanki.repository.MonederoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class MonederoService {

    @Autowired
    private MonederoRepository monederoRepository;

    private final WebClient tarjetaWebClient;

    @Autowired
    public MonederoService(MonederoRepository monederoRepository, WebClient.Builder tarjetaWebClientBuilder) {
        this.monederoRepository = monederoRepository;
        this.tarjetaWebClient = tarjetaWebClientBuilder.baseUrl("http://localhost:8085").build();
    }

    public Mono<Monedero> crearMonedero(Monedero monedero) {
        return monederoRepository.save(monedero);
    }

    public Mono<Monedero> enviarPago(String numeroCelular, double monto) {
        return monederoRepository.findByNumeroCelular(numeroCelular)
                .flatMap(monedero -> {
                    if (monedero.getSaldo() >= monto) {
                        monedero.setSaldo(monedero.getSaldo() - monto);
                        return realizarPagoConTarjeta(monedero.getTarjetaDebitoId(), monto)
                                .then(monederoRepository.save(monedero));
                    } else {
                        return Mono.error(new IllegalArgumentException("Saldo insuficiente"));
                    }
                });
    }

    public Mono<Monedero> recibirPago(String numeroCelular, double monto) {
        return monederoRepository.findByNumeroCelular(numeroCelular)
                .flatMap(monedero -> {
                    monedero.setSaldo(monedero.getSaldo() + monto);
                    return realizarPagoConTarjeta(monedero.getTarjetaDebitoId(), -monto)
                            .then(monederoRepository.save(monedero));
                });
    }

    public Mono<Monedero> asociarTarjeta(String numeroCelular, String tarjetaDebitoId) {
        return monederoRepository.findByNumeroCelular(numeroCelular)
                .flatMap(monedero -> {
                    monedero.setTarjetaDebitoId(tarjetaDebitoId);
                    return monederoRepository.save(monedero);
                });
    }

    public Flux<Monedero> listarTodos() {
        return monederoRepository.findAll();
    }

    public Mono<Monedero> buscarPorId(String id) {
        return monederoRepository.findById(id);
    }

    public Mono<Void> eliminarPorId(String id) {
        return monederoRepository.deleteById(id);
    }

    private Mono<Void> realizarPagoConTarjeta(String tarjetaDebitoId, double monto) {
        if (tarjetaDebitoId == null || tarjetaDebitoId.isEmpty()) {
            return Mono.empty();
        }
        return tarjetaWebClient.post()
                .uri("/tarjetas/{tarjetaDebitoId}/pago", tarjetaDebitoId)
                .bodyValue(new RealizarPagoRequest(monto))
                .retrieve()
                .bodyToMono(Void.class);
    }

    private static class RealizarPagoRequest {
        private double monto;

        public RealizarPagoRequest(double monto) {
            this.monto = monto;
        }

        public double getMonto() {
            return monto;
        }

        public void setMonto(double monto) {
            this.monto = monto;
        }
    }
}