package com.bank.tarjeta.controller;

import com.bank.tarjeta.model.Tarjeta;
import com.bank.tarjeta.model.Movimiento;
import com.bank.tarjeta.service.TarjetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/tarjetas")
public class TarjetaController {

    @Autowired
    private TarjetaService tarjetaService;

    @GetMapping
    public Flux<Tarjeta> findAll() {
        return tarjetaService.findAll();
    }

    @GetMapping("/{id}")
    public Mono<Tarjeta> findById(@PathVariable String id) {
        return tarjetaService.findById(id);
    }

    @PostMapping
    public Mono<Tarjeta> save(@RequestBody Tarjeta tarjeta) {
        return tarjetaService.save(tarjeta);
    }

    @PutMapping("/{id}")
    public Mono<Tarjeta> update(@PathVariable String id, @RequestBody Tarjeta tarjeta) {
        return tarjetaService.update(id, tarjeta);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable String id) {
        return tarjetaService.delete(id);
    }

    @GetMapping("/{id}/saldo")
    public Mono<Double> consultarSaldoCuentaPrincipal(@PathVariable String id) {
        return tarjetaService.consultarSaldoCuentaPrincipal(id);
    }

    @GetMapping("/{id}/movimientos")
    public Mono<List<Movimiento>> obtenerUltimosMovimientos(@PathVariable String id) {
        return tarjetaService.obtenerUltimosMovimientos(id);
    }

    @PostMapping("/{id}/pago")
    public Mono<Void> realizarPagoConTarjeta(@PathVariable String id, @RequestBody PagoRequest pagoRequest) {
        return tarjetaService.realizarPagoConTarjeta(id, pagoRequest.getMonto());
    }

    @PostMapping("/{id}/asociar")
    public Mono<Tarjeta> asociarCuenta(@PathVariable String id, @RequestBody CuentaAsociadaRequest request) {
        return tarjetaService.asociarCuenta(id, request.getCuentaId());
    }

    @PostMapping("/{id}/cuenta-principal")
    public Mono<Tarjeta> establecerCuentaPrincipal(@PathVariable String id, @RequestBody CuentaPrincipalRequest request) {
        return tarjetaService.establecerCuentaPrincipal(id, request.getCuentaPrincipalId());
    }
}

class PagoRequest {
    private double monto;

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }
}

class CuentaAsociadaRequest {
    private String cuentaId;

    public String getCuentaId() {
        return cuentaId;
    }

    public void setCuentaId(String cuentaId) {
        this.cuentaId = cuentaId;
    }
}

class CuentaPrincipalRequest {
    private String cuentaPrincipalId;

    public String getCuentaPrincipalId() {
        return cuentaPrincipalId;
    }

    public void setCuentaPrincipalId(String cuentaPrincipalId) {
        this.cuentaPrincipalId = cuentaPrincipalId;
    }
}