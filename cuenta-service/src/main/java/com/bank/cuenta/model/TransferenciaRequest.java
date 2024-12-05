package com.bank.cuenta.model;

import lombok.Data;

@Data
public class TransferenciaRequest {
    private String cuentaOrigenId;
    private String cuentaDestinoId;
    private double monto;

    public String getCuentaOrigenId() {
        return cuentaOrigenId;
    }

    public void setCuentaOrigenId(String cuentaOrigenId) {
        this.cuentaOrigenId = cuentaOrigenId;
    }

    public String getCuentaDestinoId() {
        return cuentaDestinoId;
    }

    public void setCuentaDestinoId(String cuentaDestinoId) {
        this.cuentaDestinoId = cuentaDestinoId;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }
}