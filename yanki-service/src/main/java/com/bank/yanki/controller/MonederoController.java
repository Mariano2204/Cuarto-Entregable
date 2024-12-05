package com.bank.yanki.controller;

import com.bank.yanki.model.Monedero;
import com.bank.yanki.service.MonederoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/monederos")
public class MonederoController {

    @Autowired
    private MonederoService monederoService;

    @PostMapping
    public Mono<Monedero> crearMonedero(@RequestBody Monedero monedero) {
        return monederoService.crearMonedero(monedero);
    }

    @PostMapping("/enviar")
    public Mono<Monedero> enviarPago(@RequestBody PagoRequest pagoRequest) {
        return monederoService.enviarPago(pagoRequest.getNumeroCelular(), pagoRequest.getMonto());
    }

    @PostMapping("/recibir")
    public Mono<Monedero> recibirPago(@RequestBody PagoRequest pagoRequest) {
        return monederoService.recibirPago(pagoRequest.getNumeroCelular(), pagoRequest.getMonto());
    }

    @PostMapping("/asociar")
    public Mono<Monedero> asociarTarjeta(@RequestBody AsociarTarjetaRequest asociarTarjetaRequest) {
        return monederoService.asociarTarjeta(asociarTarjetaRequest.getNumeroCelular(), asociarTarjetaRequest.getTarjetaDebitoId());
    }

    @GetMapping
    public Flux<Monedero> listarTodos() {
        return monederoService.listarTodos();
    }

    @GetMapping("/{id}")
    public Mono<Monedero> buscarPorId(@PathVariable String id) {
        return monederoService.buscarPorId(id);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> eliminarPorId(@PathVariable String id) {
        return monederoService.eliminarPorId(id);
    }
}

class PagoRequest {
    private String numeroCelular;
    private double monto;

    // Getters y Setters
    public String getNumeroCelular() {
        return numeroCelular;
    }

    public void setNumeroCelular(String numeroCelular) {
        this.numeroCelular = numeroCelular;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }
}

class AsociarTarjetaRequest {
    private String numeroCelular;
    private String tarjetaDebitoId;

    // Getters y Setters
    public String getNumeroCelular() {
        return numeroCelular;
    }

    public void setNumeroCelular(String numeroCelular) {
        this.numeroCelular = numeroCelular;
    }

    public String getTarjetaDebitoId() {
        return tarjetaDebitoId;
    }

    public void setTarjetaDebitoId(String tarjetaDebitoId) {
        this.tarjetaDebitoId = tarjetaDebitoId;
    }
}
