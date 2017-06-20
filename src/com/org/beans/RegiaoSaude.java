/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.org.beans;

import com.org.model.classes.Municipio;
import com.org.model.classes.UF;
import com.org.model.classes.agravos.oportunidade.OportunidadeAgravoCOAP;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Taidson
 */
public class RegiaoSaude {
    private String nmAgravo;
    private List<OportunidadeAgravoCOAP> lista = new ArrayList<OportunidadeAgravoCOAP>();
    private Integer qtdOportuno;
    private Integer total;
    private String uf;
    private String codRegiaoSaude;

    public RegiaoSaude() {
    }

    public RegiaoSaude(String codRegiaoSaude, String nmAgravo, List<OportunidadeAgravoCOAP> lista) {
        this.codRegiaoSaude = codRegiaoSaude;
        this.nmAgravo = nmAgravo;
        this.lista = lista;
    }

    public RegiaoSaude(String nmAgravo, List<OportunidadeAgravoCOAP> lista) {
        this.nmAgravo = nmAgravo;
        this.lista = lista;
    }

    public List<OportunidadeAgravoCOAP> getLista() {
        return lista;
    }

    public void setLista(List<OportunidadeAgravoCOAP> lista) {
        this.lista = lista;
    }

    public String getNmAgravo() {
        return nmAgravo;
    }

    public void setNmAgravo(String nmAgravo) {
        this.nmAgravo = nmAgravo;
    }

    public Integer getQtdOportuno() {
        return qtdOportuno;
    }

    public void setQtdOportuno(Integer qtdOportuno) {
        this.qtdOportuno = qtdOportuno;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getCodRegiaoSaude() {
        return codRegiaoSaude;
    }

    public void setCodRegiaoSaude(String codRegiaoSaude) {
        this.codRegiaoSaude = codRegiaoSaude;
    }

    
}
