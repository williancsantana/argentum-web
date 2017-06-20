/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.org.beans;

/**
 *
 * @author taidson.santos
 */
public class Duplicidade {
    private String uf;
    private String municipio;
    private Integer ufDiferente;
    private Integer municipioDiferente;
    private Integer municipioIgual;

    public Duplicidade() {
        setUfDiferente(0);
        setMunicipioDiferente(0);
        setMunicipioIgual(0);
    }

    
    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public Integer getUfDiferente() {
        return ufDiferente;
    }

    public void setUfDiferente(Integer ufDiferente) {
        this.ufDiferente = ufDiferente;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public Integer getMunicipioDiferente() {
        return municipioDiferente;
    }

    public void setMunicipioDiferente(Integer municipioDiferente) {
        this.municipioDiferente = municipioDiferente;
    }

    public Integer getMunicipioIgual() {
        return municipioIgual;
    }

    public void setMunicipioIgual(Integer municipioIgual) {
        this.municipioIgual = municipioIgual;
    }

}