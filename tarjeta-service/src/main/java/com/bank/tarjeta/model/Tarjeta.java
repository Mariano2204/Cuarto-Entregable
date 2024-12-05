package com.bank.tarjeta.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "tarjetas")
public class Tarjeta {
    @Id
    private String id;
    private String numeroTarjeta;
    private String clienteId;
    private String cuentaPrincipalId;
    private List<String> cuentasAsociadas;
    private List<Movimiento> movimientos;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumeroTarjeta() {
        return numeroTarjeta;
    }

    public void setNumeroTarjeta(String numeroTarjeta) {
        this.numeroTarjeta = numeroTarjeta;
    }

    public String getClienteId() {
        return clienteId;
    }

    public void setClienteId(String clienteId) {
        this.clienteId = clienteId;
    }

    public String getCuentaPrincipalId() {
        return cuentaPrincipalId;
    }

    public void setCuentaPrincipalId(String cuentaPrincipalId) {
        this.cuentaPrincipalId = cuentaPrincipalId;
    }

    public List<String> getCuentasAsociadas() {
        return cuentasAsociadas;
    }

    public void setCuentasAsociadas(List<String> cuentasAsociadas) {
        this.cuentasAsociadas = cuentasAsociadas;
    }

    public List<Movimiento> getMovimientos() {
        return movimientos;
    }

    public void setMovimientos(List<Movimiento> movimientos) {
        this.movimientos = movimientos;
    }
}