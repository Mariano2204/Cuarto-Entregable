package com.bank.movimiento.controller;

import com.bank.movimiento.model.Movimiento;
import com.bank.movimiento.service.MovimientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/movimientos")
public class MovimientoController {

    @Autowired
    private MovimientoService movimientoService;

    @GetMapping
    public Flux<Movimiento> getAllMovimientos() {
        return movimientoService.findAll();
    }

    @GetMapping("/{id}")
    public Mono<Movimiento> getMovimientoById(@PathVariable String id) {
        return movimientoService.findById(id);
    }

    @PostMapping
    public Mono<Movimiento> createMovimiento(@RequestBody Movimiento movimiento) {
        return movimientoService.save(movimiento)
                .flatMap(savedMovimiento -> {
                    if (movimiento.getCreditoId() != null) {
                        // Llamar al microservicio de crédito para asignar el movimiento
                        return movimientoService.assignMovimientoToCredito(savedMovimiento);
                    } else if (movimiento.getCuentaId() != null) {
                        // Llamar al microservicio de cuenta para asignar el movimiento
                        return movimientoService.assignMovimientoToCuenta(savedMovimiento);
                    } else {
                        return Mono.error(new IllegalArgumentException("Debe especificar creditoId o cuentaId"));
                    }
                });
    }

    @PutMapping("/{id}")
    public Mono<Movimiento> updateMovimiento(@PathVariable String id, @RequestBody Movimiento movimiento) {
        return movimientoService.update(id, movimiento);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteMovimiento(@PathVariable String id) {
        return movimientoService.delete(id);
    }
}