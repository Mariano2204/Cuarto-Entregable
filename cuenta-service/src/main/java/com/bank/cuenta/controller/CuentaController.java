package com.bank.cuenta.controller;

import com.bank.cuenta.model.Cuenta;
import com.bank.cuenta.model.Movimiento;
import com.bank.cuenta.model.TransferenciaRequest;
import com.bank.cuenta.service.CuentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/cuentas")
public class CuentaController {

    @Autowired
    private CuentaService cuentaService;

    @GetMapping
    public Flux<Cuenta> getAllCuentas() {
        return cuentaService.findAll();
    }

    @GetMapping("/{id}")
    public Mono<Cuenta> getCuentaById(@PathVariable String id) {
        return cuentaService.findById(id);
    }

    @PostMapping
    public Mono<Cuenta> createCuenta(@RequestBody Cuenta cuenta) {
        return cuentaService.save(cuenta);
    }

    @PutMapping("/{id}")
    public Mono<Cuenta> updateCuenta(@PathVariable String id, @RequestBody Cuenta cuenta) {
        return cuentaService.update(id, cuenta);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteCuenta(@PathVariable String id) {
        return cuentaService.delete(id);
    }

    @PostMapping("/transferir")
    public Mono<Void> transferirEntreCuentas(@RequestBody TransferenciaRequest request) {
        return cuentaService.transferirEntreCuentas(request.getCuentaOrigenId(), request.getCuentaDestinoId(), request.getMonto());
    }

    @PostMapping("/transferir-terceros")
    public Mono<Void> transferirATerceros(@RequestBody TransferenciaRequest request) {
        return cuentaService.transferirATerceros(request.getCuentaOrigenId(), request.getCuentaDestinoId(), request.getMonto());
    }

    @PostMapping("/{cuentaId}/movimientos")
    public Mono<Cuenta> addMovimientoToCuenta(@PathVariable String cuentaId, @RequestBody Movimiento movimiento) {
        return cuentaService.addMovimientoToCuenta(cuentaId, movimiento);
    }
}