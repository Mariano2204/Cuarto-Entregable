package com.bank.cliente.controller;

import com.bank.cliente.model.Cliente;
import com.bank.cliente.model.ReporteComisionesRequest;
import com.bank.cliente.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.Map;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @GetMapping
    public Flux<Cliente> getAllClientes() {
        return clienteService.findAll();
    }

    @GetMapping("/{id}")
    public Mono<Cliente> getClienteById(@PathVariable String id) {
        return clienteService.findById(id);
    }

    @PostMapping
    public Mono<Cliente> createCliente(@RequestBody Cliente cliente) {
        return clienteService.save(cliente);
    }

    @PutMapping("/{id}")
    public Mono<Cliente> updateCliente(@PathVariable String id, @RequestBody Cliente cliente) {
        return clienteService.update(id, cliente);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteCliente(@PathVariable String id) {
        return clienteService.delete(id);
    }

    @PostMapping("/{clienteId}/cuentas")
    public Mono<ResponseEntity<Cliente>> assignCuentaToCliente(@PathVariable String clienteId, @RequestBody Map<String, String> request) {
        String cuentaId = request.get("cuentaId");
        return clienteService.assignCuentaToCliente(clienteId, cuentaId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @PostMapping("/{clienteId}/creditos")
    public Mono<Cliente> assignCreditoToCliente(@PathVariable String clienteId, @RequestBody Map<String, String> request) {
        String creditoId = request.get("creditoId");
        return clienteService.assignCreditoToCliente(clienteId, creditoId);
    }

    @GetMapping("/{clienteId}/resumen-saldos")
    public Mono<String> obtenerResumenSaldosPromedioDiarios(@PathVariable String clienteId) {
        return clienteService.obtenerResumenSaldosPromedioDiarios(clienteId);
    }


    @PostMapping("/{clienteId}/reporte-comisiones")
    public Mono<String> generarReporteComisiones(@PathVariable String clienteId, @RequestBody ReporteComisionesRequest request) {
        return clienteService.generarReporteComisiones(clienteId, request.getFechaInicio(), request.getFechaFin());
    }

    @GetMapping("/{clienteId}/resumen-consolidado")
    public Mono<ResponseEntity<String>> obtenerResumenConsolidado(@PathVariable String clienteId) {
        return clienteService.obtenerResumenConsolidado(clienteId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}