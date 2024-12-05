package com.bank.yanki.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "monederos")
public class Monedero {

    @Id
    private String id;
    private String numeroDocumento;
    private String tipoDocumento;
    private String numeroCelular;
    private String imeiCelular;
    private String correoElectronico;
    private double saldo;
    private String tarjetaDebitoId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNumeroCelular() {
        return numeroCelular;
    }

    public void setNumeroCelular(String numeroCelular) {
        this.numeroCelular = numeroCelular;
    }

    public String getImeiCelular() {
        return imeiCelular;
    }

    public void setImeiCelular(String imeiCelular) {
        this.imeiCelular = imeiCelular;
    }

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public String getTarjetaDebitoId() {
        return tarjetaDebitoId;
    }

    public void setTarjetaDebitoId(String tarjetaDebitoId) {
        this.tarjetaDebitoId = tarjetaDebitoId;
    }
}