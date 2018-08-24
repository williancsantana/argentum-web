/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.org.model.classes.agravos.oportunidade;

import com.org.model.classes.Agravo;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author geraldo
 */
public class OportunidadeAgravoPQAVS  extends Agravo{
    private String nmAgravo;
    private Integer qtdNaoEncerrado;
    private Integer qtdInoportuno;
    private Integer qtdOportuno;
    private Integer qtdDataInvalida;
    private Integer qtdInoportunoOutras;
    private Integer total;
    private String codAgravo;
    private String regiao;
    private String regiaoSaude;
    private String uf;
    private String codRegionalSaude;
    private String regionalSaude;
    private String codRegiaoSaude;
    private Map<String, Integer> mapaSemEpidemiologica = new HashMap<String, Integer>();
    private Double resultado;

    public OportunidadeAgravoPQAVS(){
        this.setQtdDataInvalida(0);
        this.setQtdInoportuno(0);
        this.setQtdNaoEncerrado(0);
        this.setQtdInoportunoOutras(0);
        this.setQtdOportuno(0);
        this.setTotal(0);
    }

    public String getCodRegionalSaude() {
        return codRegionalSaude;
    }

    public void setCodRegionalSaude(String codRegionalSaude) {
        this.codRegionalSaude = codRegionalSaude;
    }

    public String getRegionalSaude() {
        return regionalSaude;
    }

    public void setRegionalSaude(String regionalSaude) {
        this.regionalSaude = (regionalSaude != null)?regionalSaude:"";
    }
    
    
    
    
    /**
     * @return the nmAgravo
     */
    public String getNmAgravo() {
        return nmAgravo;
    }

    /**
     * @param nmAgravo the nmAgravo to set
     */
    public void setNmAgravo(String nmAgravo) {
        this.nmAgravo = nmAgravo;
    }

    /**
     * @return the qtdNaoEncerrado
     */
    public Integer getQtdNaoEncerrado() {
        return qtdNaoEncerrado;
    }

    /**
     * @param qtdNaoEncerrado the qtdNaoEncerrado to set
     */
    public void setQtdNaoEncerrado(Integer qtdNaoEncerrado) {
        this.qtdNaoEncerrado = qtdNaoEncerrado;
    }

    /**
     * @return the qtdInoportuno
     */
    public Integer getQtdInoportuno() {
        return qtdInoportuno;
    }

    /**
     * @param qtdInoportuno the qtdInoportuno to set
     */
    public void setQtdInoportuno(Integer qtdInoportuno) {
        this.qtdInoportuno = qtdInoportuno;
    }

    /**
     * @return the qtdOportuno
     */
    public Integer getQtdOportuno() {
        return qtdOportuno;
    }

    /**
     * @param qtdOportuno the qtdOportuno to set
     */
    public void setQtdOportuno(Integer qtdOportuno) {
        this.qtdOportuno = qtdOportuno;
    }

    /**
     * @return the qtdDataInvalida
     */
    public Integer getQtdDataInvalida() {
        return qtdDataInvalida;
    }

    /**
     * @param qtdDataInvalida the qtdDataInvalida to set
     */
    public void setQtdDataInvalida(Integer qtdDataInvalida) {
        this.qtdDataInvalida = qtdDataInvalida;
    }

    /**
     * @return the total
     */
    public Integer getTotal() {
        return total;
    }

    /**
     * @param total the total to set
     */
    public void setTotal(Integer total) {
        this.total = total;
    }

    /**
     * @return the codAgravo
     */
    public String getCodAgravo() {
        return codAgravo;
    }

    /**
     * @param codAgravo the codAgravo to set
     */
    public void setCodAgravo(String codAgravo) {
        this.codAgravo = codAgravo;
    }

    /**
     * @return the regiao
     */
    public String getRegiao() {
        return regiao;
    }

    /**
     * @param regiao the regiao to set
     */
    public void setRegiao(String regiao) {
        this.regiao = regiao;
    }

    /**
     * @return the qtdInoportunoOutras
     */
    public Integer getQtdInoportunoOutras() {
        return qtdInoportunoOutras;
    }

    /**
     * @param qtdInoportunoOutras the qtdInoportunoOutras to set
     */
    public void setQtdInoportunoOutras(Integer qtdInoportunoOutras) {
        this.qtdInoportunoOutras = qtdInoportunoOutras;
    }

    public String getRegiaoSaude() {
        return regiaoSaude;
    }

    public void setRegiaoSaude(String regiaoSaude) {
        this.regiaoSaude = regiaoSaude;
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

    public Map<String, Integer> getMapaSemEpidemiologica() {
        return mapaSemEpidemiologica;
    }

    public void setMapaSemEpidemiológica(Map<String, Integer> mapaSemEpidemiologica) {
        this.mapaSemEpidemiologica = mapaSemEpidemiologica;
    }

    public Double getResultado() {
        return resultado;
    }

    public void setResultado(Double resultado) {
        this.resultado = resultado;
    }

    public Boolean temRegionalSaude(){
//        return (this.codRegionalSaude != null) || (!this.codRegionalSaude.equals(""));
        return (this.codRegionalSaude != null);
    }
    
}
