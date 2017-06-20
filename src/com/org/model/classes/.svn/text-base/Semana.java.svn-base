/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.org.model.classes;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author geraldo
 */
public class Semana {

    private String semanaInical;
    private String semanaFinal;
    private String periodo;

    public String getDtAvaliacao() {
        return dtAvaliacao;
    }

    public void setDtAvaliacao(String dtAvaliacao) {
        this.dtAvaliacao = dtAvaliacao;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public boolean isSituacao() {
        return situacao;
    }

    public void setSituacao(boolean situacao) {
        this.situacao = situacao;
    }
    private boolean situacao;
    private String dtAvaliacao;

    public Semana() {
    }

    public Semana(String s1, String s2, String periodo) {
        this.semanaFinal = s2;
        this.semanaInical = s1;
        this.periodo = periodo;
        setSituacao(false);
        Date data = new Date();
        SimpleDateFormat formatador = new SimpleDateFormat("dd/MM/yyyy");
        setDtAvaliacao(formatador.format(data));
    }

    public String getSemanaFinal() {
        return semanaFinal;
    }

    public void setSemanaFinal(String semanaFinal) {
        this.semanaFinal = semanaFinal;
    }

    public String getSemanaInical() {
        return semanaInical;
    }

    public void setSemanaInical(String semanaInical) {
        this.semanaInical = semanaInical;
    }
}
