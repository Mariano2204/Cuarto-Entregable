package com.bank.cliente.model;

import com.bank.cliente.model.enums.TipoCredito;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Document
public class Credito {
    @Id
    private String id;
    private TipoCredito tipoCredito;
    private BigDecimal limiteCredito;
    private BigDecimal saldo;
    private String clienteId;
    private List<Movimiento> movimientos = new ArrayList<>();
    private double deuda;
    private boolean deudaVencida;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TipoCredito getTipoCredito() {
        return tipoCredito;
    }

    public void setTipoCredito(TipoCredito tipoCredito) {
        this.tipoCredito = tipoCredito;
    }

    public BigDecimal getLimiteCredito() {
        return limiteCredito;
    }

    public void setLimiteCredito(BigDecimal limiteCredito) {
        this.limiteCredito = limiteCredito;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public String getClienteId() {
        return clienteId;
    }

    public void setClienteId(String clienteId) {
        this.clienteId = clienteId;
    }

    public List<Movimiento> getMovimientos() {
        return movimientos;
    }

    public void setMovimientos(List<Movimiento> movimientos) {
        this.movimientos = movimientos;
    }

    public double getDeuda() {
        return deuda;
    }

    public void setDeuda(double deuda) {
        this.deuda = deuda;
    }

    public boolean isDeudaVencida() {
        return deudaVencida;
    }

    public void setDeudaVencida(boolean deudaVencida) {
        this.deudaVencida = deudaVencida;
    }
}