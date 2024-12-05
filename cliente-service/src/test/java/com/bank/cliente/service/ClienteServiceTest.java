package com.bank.cliente.service;

import com.bank.cliente.model.Cliente;
import com.bank.cliente.model.CuentaBancaria;
import com.bank.cliente.model.Credito;
import com.bank.cliente.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
public class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private WebClient cuentaWebClient;

    @Mock
    private WebClient creditoWebClient;

    @Mock
    private WebClient.Builder cuentaWebClientBuilder;

    @Mock
    private WebClient.Builder creditoWebClientBuilder;

    private ClienteService clienteService;

    @BeforeEach
    void setUp() {
        // WebClient.Builder mocks
        when(cuentaWebClientBuilder.baseUrl(anyString())).thenReturn(cuentaWebClientBuilder);
        when(cuentaWebClientBuilder.build()).thenReturn(cuentaWebClient);
        when(creditoWebClientBuilder.baseUrl(anyString())).thenReturn(creditoWebClientBuilder);
        when(creditoWebClientBuilder.build()).thenReturn(creditoWebClient);

        // Instancia de ClienteService con los mocks configurados
        clienteService = new ClienteService(clienteRepository, cuentaWebClientBuilder, creditoWebClientBuilder);
    }

    @Test
    void testFindAll() {
        Cliente cliente = new Cliente();
        when(clienteRepository.findAll()).thenReturn(Flux.just(cliente));

        Flux<Cliente> result = clienteService.findAll();

        assertThat(result).isNotNull();
    }

    @Test
    void testFindById() {
        String clienteId = "123";
        Cliente cliente = new Cliente();
        when(clienteRepository.findById(clienteId)).thenReturn(Mono.just(cliente));

        Mono<Cliente> result = clienteService.findById(clienteId);

        assertThat(result).isNotNull();
        assertThat(result.block()).isEqualTo(cliente);
    }

    @Test
    void testSave() {
        Cliente cliente = new Cliente();
        when(clienteRepository.save(cliente)).thenReturn(Mono.just(cliente));

        Mono<Cliente> result = clienteService.save(cliente);

        assertThat(result).isNotNull();
        assertThat(result.block()).isEqualTo(cliente);
    }

    @Test
    void testUpdate() {
        String clienteId = "123";
        Cliente existingCliente = new Cliente();
        existingCliente.setNombre("Old Name");
        Cliente cliente = new Cliente();
        cliente.setNombre("New Name");

        when(clienteRepository.findById(clienteId)).thenReturn(Mono.just(existingCliente));
        when(clienteRepository.save(existingCliente)).thenReturn(Mono.just(existingCliente));

        Mono<Cliente> result = clienteService.update(clienteId, cliente);

        assertThat(result).isNotNull();
        assertThat(result.block().getNombre()).isEqualTo("New Name");
    }

    @Test
    void testDelete() {
        String clienteId = "123";
        when(clienteRepository.deleteById(clienteId)).thenReturn(Mono.empty());

        Mono<Void> result = clienteService.delete(clienteId);

        assertThat(result).isNotNull();
        verify(clienteRepository, times(1)).deleteById(clienteId);
    }

}