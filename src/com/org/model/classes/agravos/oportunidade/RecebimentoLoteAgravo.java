/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.org.model.classes.agravos.oportunidade;

import java.util.Date;

/**
 *
 * @author geraldo
 */
public class RecebimentoLoteAgravo {//CRIAR AQUI O BEAN PARA RECEBIMENTO
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
    private String codRegiaoSaude;
    
    private String nu_lote;
    private Date dt_trans;
    private Date dt_receb;
    private Integer not_neg;
    private Integer neg_excl;
    private Integer notsurto;
    private Integer investsur;
    private Integer acompsur;
    private Integer acompagr;
    private String config;
    private String remetent;
    
    

    public RecebimentoLoteAgravo(){
        this.setQtdDataInvalida(0);
        this.setQtdInoportuno(0);
        this.setQtdNaoEncerrado(0);
        this.setQtdInoportunoOutras(0);
        this.setQtdOportuno(0);
        this.setTotal(0);
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

    public Integer getAcompagr() {
        return acompagr;
    }

    public void setAcompagr(Integer acompagr) {
        this.acompagr = acompagr;
    }

    public Integer getAcompsur() {
        return acompsur;
    }

    public void setAcompsur(Integer acompsur) {
        this.acompsur = acompsur;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public Date getDt_receb() {
        return dt_receb;
    }

    public void setDt_receb(Date dt_receb) {
        this.dt_receb = dt_receb;
    }

    public Date getDt_trans() {
        return dt_trans;
    }

    public void setDt_trans(Date dt_trans) {
        this.dt_trans = dt_trans;
    }

    public Integer getInvestsur() {
        return investsur;
    }

    public void setInvestsur(Integer investsur) {
        this.investsur = investsur;
    }

    public Integer getNeg_excl() {
        return neg_excl;
    }

    public void setNeg_excl(Integer neg_excl) {
        this.neg_excl = neg_excl;
    }

    public Integer getNot_neg() {
        return not_neg;
    }

    public void setNot_neg(Integer not_neg) {
        this.not_neg = not_neg;
    }

    public Integer getNotsurto() {
        return notsurto;
    }

    public void setNotsurto(Integer notsurto) {
        this.notsurto = notsurto;
    }

    public String getNu_lote() {
        return nu_lote;
    }

    public void setNu_lote(String nu_lote) {
        this.nu_lote = nu_lote;
    }

    public String getRemetent() {
        return remetent;
    }

    public void setRemetent(String remetent) {
        this.remetent = remetent;
    }

    
    
    
    
}
