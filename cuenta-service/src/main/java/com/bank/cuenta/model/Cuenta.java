package com.bank.cuenta.model;

import com.bank.cuenta.model.enums.TipoCuenta;
import lombok.Data;
import org.springframework.data.annotation.Id;
import java.util.ArrayList;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;

@Data
@Document
public class Cuenta {
    @Id
    private String id;
    private String numeroCuenta;
    private TipoCuenta tipoCuenta;
    private BigDecimal saldo;
    private List<Movimiento> movimientos = new ArrayList<>();
    private int maxTransaccionesSinComision;
    private double comisionPorTransaccion;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public void setNumeroCuenta(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    public TipoCuenta getTipoCuenta() {
        return tipoCuenta;
    }

    public void setTipoCuenta(TipoCuenta tipoCuenta) {
        this.tipoCuenta = tipoCuenta;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public int getMaxTransaccionesSinComision() {
        return maxTransaccionesSinComision;
    }

    public void setMaxTransaccionesSinComision(int maxTransaccionesSinComision) {
        this.maxTransaccionesSinComision = maxTransaccionesSinComision;
    }

    public double getComisionPorTransaccion() {
        return comisionPorTransaccion;
    }

    public void setComisionPorTransaccion(double comisionPorTransaccion) {
        this.comisionPorTransaccion = comisionPorTransaccion;
    }

    public List<Movimiento> getMovimientos() {
        return movimientos;
    }

    public void setMovimientos(List<Movimiento> movimientos) {
        this.movimientos = movimientos;
    }
}