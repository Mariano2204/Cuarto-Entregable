package com.bank.cliente.service;

import com.bank.cliente.model.Cliente;
import com.bank.cliente.model.CuentaBancaria;
import com.bank.cliente.model.Credito;
import com.bank.cliente.model.enums.TipoCliente;
import com.bank.cliente.model.enums.TipoCredito;
import com.bank.cliente.model.enums.TipoCuenta;
import com.bank.cliente.repository.ClienteRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    private final WebClient cuentaWebClient;
    private final WebClient creditoWebClient;

    public ClienteService(ClienteRepository clienteRepository, WebClient.Builder cuentaWebClientBuilder, WebClient.Builder creditoWebClientBuilder) {
        this.clienteRepository = clienteRepository;
        this.cuentaWebClient = cuentaWebClientBuilder.baseUrl("http://localhost:8082").build();
        this.creditoWebClient = creditoWebClientBuilder.baseUrl("http://localhost:8083").build();
    }

    public Flux<Cliente> findAll() {
        return clienteRepository.findAll();
    }

    public Mono<Cliente> findById(String id) {
        return clienteRepository.findById(id);
    }

    public Mono<Cliente> save(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    public Mono<Cliente> update(String id, Cliente cliente) {
        return clienteRepository.findById(id)
                .flatMap(existingCliente -> {
                    existingCliente.setNombre(cliente.getNombre());
                    existingCliente.setTipo(cliente.getTipo());
                    existingCliente.setProductos(cliente.getProductos());
                    return clienteRepository.save(existingCliente);
                });
    }

    public Mono<Void> delete(String id) {
        return clienteRepository.deleteById(id);
    }

    private Mono<Boolean> tieneDeudaVencida(String clienteId) {
        return clienteRepository.findById(clienteId)
                .flatMap(cliente -> {
                    List<String> productos = cliente.getProductos();
                    return Flux.fromIterable(productos)
                            .flatMap(productoId -> creditoWebClient.get()
                                    .uri("/creditos/{creditoId}", productoId)
                                    .retrieve()
                                    .bodyToMono(Credito.class)
                                    .filter(Credito::isDeudaVencida))
                            .hasElements();
                });
    }

    // Métodos de fallback
    private Mono<Cliente> fallbackCuenta(String clienteId, String cuentaId, Throwable t) {
        return Mono.error(new RuntimeException("El servicio de cuentas no está disponible en este momento. Por favor, inténtelo más tarde."));
    }

    private Mono<Cliente> fallbackCredito(String clienteId, String creditoId, Throwable t) {
        return Mono.error(new RuntimeException("El servicio de créditos no está disponible en este momento. Por favor, inténtelo más tarde."));
    }

    @CircuitBreaker(name = "cuentaService", fallbackMethod = "fallbackCuenta")
    @TimeLimiter(name = "cuentaService")
    public Mono<Cliente> assignCuentaToCliente(String clienteId, String cuentaId) {
        return tieneDeudaVencida(clienteId)
                .flatMap(tieneDeuda -> {
                    if (tieneDeuda) {
                        return Mono.error(new IllegalArgumentException("El cliente tiene una deuda vencida y no puede adquirir nuevos productos"));
                    }
                    return cuentaWebClient.get()
                            .uri("/cuentas/{cuentaId}", cuentaId)
                            .retrieve()
                            .bodyToMono(CuentaBancaria.class)
                            .flatMap(cuenta -> clienteRepository.findById(clienteId)
                                    .flatMap(cliente -> {
                                        // Obtener los detalles de todas las cuentas del cliente
                                        return Flux.fromIterable(cliente.getProductos())
                                                .flatMap(productoId -> cuentaWebClient.get()
                                                        .uri("/cuentas/{cuentaId}", productoId)
                                                        .retrieve()
                                                        .bodyToMono(CuentaBancaria.class)
                                                        .onErrorResume(e -> Mono.empty())) // Ignorar errores si el producto no es una cuenta bancaria
                                                .collectList()
                                                .flatMap(cuentas -> {
                                                    // Verificar restricciones según el tipo de cliente
                                                    if (cliente.getTipo() == TipoCliente.PERSONAL || cliente.getTipo() == TipoCliente.VIP) {
                                                        if (cuentas.stream().anyMatch(c -> c.getTipoCuenta() == cuenta.getTipoCuenta())) {
                                                            return Mono.error(new IllegalArgumentException("El cliente ya tiene una cuenta de tipo " + cuenta.getTipoCuenta().name()));
                                                        }
                                                    } else if (cliente.getTipo() == TipoCliente.EMPRESARIAL || cliente.getTipo() == TipoCliente.PYME) {
                                                        if (cuenta.getTipoCuenta() == TipoCuenta.AHORRO || cuenta.getTipoCuenta() == TipoCuenta.PLAZO_FIJO) {
                                                            return Mono.error(new IllegalArgumentException("El cliente empresarial no puede tener una cuenta de ahorro o a plazo fijo"));
                                                        }
                                                    }

                                                    // Verificar si la cuenta ya está asociada con el cliente
                                                    if (cliente.getProductos().contains(cuentaId)) {
                                                        return Mono.error(new IllegalArgumentException("La cuenta ya está asociada con el cliente"));
                                                    }

                                                    cliente.getProductos().add(cuentaId);
                                                    return clienteRepository.save(cliente);
                                                });
                                    }));
                });
    }

    @CircuitBreaker(name = "creditoService", fallbackMethod = "fallbackCredito")
    @TimeLimiter(name = "creditoService")
    public Mono<Cliente> assignCreditoToCliente(String clienteId, String creditoId) {
        return tieneDeudaVencida(clienteId)
                .flatMap(tieneDeuda -> {
                    if (tieneDeuda) {
                        return Mono.error(new IllegalArgumentException("El cliente tiene una deuda vencida y no puede adquirir nuevos productos"));
                    }
                    return creditoWebClient.get()
                            .uri("/creditos/{creditoId}", creditoId)
                            .retrieve()
                            .bodyToMono(Credito.class)
                            .flatMap(credito -> clienteRepository.findById(clienteId)
                                    .flatMap(cliente -> {
                                        // Obtener los detalles de todos los créditos del cliente
                                        return Flux.fromIterable(cliente.getProductos())
                                                .flatMap(productoId -> creditoWebClient.get()
                                                        .uri("/creditos/{creditoId}", productoId)
                                                        .retrieve()
                                                        .bodyToMono(Credito.class)
                                                        .onErrorResume(e -> Mono.empty())) // Ignorar errores si el producto no es un crédito
                                                .collectList()
                                                .flatMap(creditos -> {
                                                    // Verificar restricciones según el tipo de cliente
                                                    if (cliente.getTipo() == TipoCliente.PERSONAL) {
                                                        if (creditos.stream().anyMatch(c -> c.getTipoCredito() == credito.getTipoCredito())) {
                                                            return Mono.error(new IllegalArgumentException("El cliente ya tiene un crédito de tipo " + credito.getTipoCredito().name()));
                                                        }
                                                        if (credito.getTipoCredito() == TipoCredito.EMPRESARIAL) {
                                                            return Mono.error(new IllegalArgumentException("El cliente personal no puede tener un crédito de tipo EMPRESARIAL"));
                                                        }
                                                    }

                                                    // Verificar si el crédito ya está asociado con el cliente
                                                    if (cliente.getProductos().contains(creditoId)) {
                                                        return Mono.error(new IllegalArgumentException("El crédito ya está asociado con el cliente"));
                                                    }

                                                    cliente.getProductos().add(creditoId);
                                                    return clienteRepository.save(cliente);
                                                });
                                    }));
                });
    }

    public Mono<String> obtenerResumenSaldosPromedioDiarios(String clienteId) {
        return clienteRepository.findById(clienteId)
                .flatMap(cliente -> {
                    List<String> productos = cliente.getProductos();
                    return Flux.fromIterable(productos)
                            .flatMap(productoId -> cuentaWebClient.get()
                                    .uri("/cuentas/{cuentaId}", productoId)
                                    .retrieve()
                                    .bodyToMono(CuentaBancaria.class)
                                    .map(cuenta -> "Cuenta: " + cuenta.getNumeroCuenta() + ", Saldo Promedio Diario: " + calcularSaldoPromedioDiario(cuenta)))
                            .collectList()
                            .map(resumen -> String.join("\n", resumen));
                });
    }

    public double calcularSaldoPromedioDiario(CuentaBancaria cuenta) {
        return cuenta.getSaldo().doubleValue();
    }

    public Mono<String> generarReporteComisiones(String clienteId, String fechaInicio, String fechaFin) {
        return clienteRepository.findById(clienteId)
                .flatMap(cliente -> {
                    List<String> productos = cliente.getProductos();
                    return Flux.fromIterable(productos)
                            .flatMap(productoId -> cuentaWebClient.get()
                                    .uri("/cuentas/{cuentaId}", productoId)
                                    .retrieve()
                                    .bodyToMono(CuentaBancaria.class)
                                    .map(cuenta -> "Cuenta: " + cuenta.getNumeroCuenta() + ", Comisiones: " + calcularComisiones(cuenta, fechaInicio, fechaFin)))
                            .collectList()
                            .map(reporte -> String.join("\n", reporte));
                });
    }

    public double calcularComisiones(CuentaBancaria cuenta, String fechaInicio, String fechaFin) {
          return 0.0;
    }

    public Mono<String> obtenerResumenConsolidado(String clienteId) {
        return clienteRepository.findById(clienteId)
                .flatMap(cliente -> {
                    List<String> productos = cliente.getProductos();
                    return Flux.fromIterable(productos)
                            .flatMap(productoId -> cuentaWebClient.get()
                                    .uri("/cuentas/{cuentaId}", productoId)
                                    .retrieve()
                                    .bodyToMono(CuentaBancaria.class)
                                    .map(cuenta -> "Cuenta: " + cuenta.getNumeroCuenta() + ", Saldo: " + cuenta.getSaldo())
                                    .switchIfEmpty(creditoWebClient.get()
                                            .uri("/creditos/{creditoId}", productoId)
                                            .retrieve()
                                            .bodyToMono(Credito.class)
                                            .map(credito -> "Crédito: " + credito.getId() + ", Deuda: " + credito.getDeuda())))
                            .collectList()
                            .map(resumen -> String.join("\n", resumen));
                });
    }

}