package com.bank.credito.controller;

import com.bank.credito.model.Credito;
import com.bank.credito.model.Movimiento;
import com.bank.credito.service.CreditoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/creditos")
public class CreditoController {

    @Autowired
    private CreditoService creditoService;

    @GetMapping
    public Flux<Credito> getAllCreditos() {
        return creditoService.findAll();
    }

    @GetMapping("/{id}")
    public Mono<Credito> getCreditoById(@PathVariable String id) {
        return creditoService.findById(id);
    }

    @PostMapping
    public Mono<Credito> createCredito(@RequestBody Credito credito) {
        return creditoService.save(credito);
    }

    @PutMapping("/{id}")
    public Mono<Credito> updateCredito(@PathVariable String id, @RequestBody Credito credito) {
        return creditoService.update(id, credito);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteCredito(@PathVariable String id) {
        return creditoService.delete(id);
    }

    @PostMapping("/{creditoId}/movimientos")
    public Mono<Credito> addMovimientoToCredito(@PathVariable String creditoId, @RequestBody Movimiento movimiento) {
        return creditoService.addMovimientoToCredito(creditoId, movimiento);
    }
}