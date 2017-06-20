/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.org.model.classes;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

/**
 *
 * @author geraldo
 */
public class Municipio {

    private String nmMunicipio;
    private String periodo1;
    private String periodo2;
    private String periodo3;
    private String periodo4;
    private String periodo5;
    private String periodo6;
    private String periodo7;
    private String periodo8;
    private String periodo9;
    private String periodo10;
    private String periodo11;
    private String periodo12;
    private String periodo13;
    private String periodo14;
    private String periodo15;
    private String periodo16;
    private String periodo17;
    private String periodo18;
    private boolean irregular = false;

    public HashMap<String,Semana> getSemanas() {
        return semanas;
    }

    public void setSemanas(HashMap<String,Semana> semanas) {
        this.semanas = semanas;
    }
    private String codMunicipio;
    private String sgUF;
    private UF uf;
    private String nmRegiao;
    private HashMap<String,Semana> semanas;
    

    public Municipio(String nome, String codigo, String siglaUf) {
        this.nmMunicipio = nome;
        this.codMunicipio = codigo;
        this.uf = new UF();
        this.uf.setNmUF(siglaUf);
        this.uf.setRegiao(new Regiao());
        this.uf.getRegiao().setNmRegiao(buscaRegiao(siglaUf));
        this.nmRegiao = this.uf.getRegiao().getNmRegiao();
        this.sgUF = siglaUf;
        this.semanas = new HashMap<String,Semana>();
    }

    public Municipio() {
    }

    public String buscaRegiao(String uf) {
        if (uf.equals("TO")) {
            return "NORTE";
        }
        if (uf.equals("AC")) {
            return "NORTE";
        }
        if (uf.equals("AL")) {
            return "NORDESTE";
        }
        if (uf.equals("AM")) {
            return "NORTE";
        }
        if (uf.equals("AP")) {
            return "NORTE";
        }
        if (uf.equals("BA")) {
            return "NORDESTE";
        }
        if (uf.equals("CE")) {
            return "NORDESTE";
        }
        if (uf.equals("DF")) {
            return "CENTRO-OESTE";
        }
        if (uf.equals("ES")) {
            return "SUDESTE";
        }
        if (uf.equals("GO")) {
            return "CENTRO-OESTE";
        }
        if (uf.equals("MA")) {
            return "NORDESTE";
        }
        if (uf.equals("MG")) {
            return "SUDESTE";
        }
        if (uf.equals("MS")) {
            return "CENTRO-OESTE";
        }
        if (uf.equals("MT")) {
            return "CENTRO-OESTE";
        }
        if (uf.equals("PA")) {
            return "NORTE";
        }
        if (uf.equals("PB")) {
            return "NORDESTE";
        }
        if (uf.equals("PE")) {
            return "NORDESTE";
        }
        if (uf.equals("PI")) {
            return "NORDESTE";
        }
        if (uf.equals("PR")) {
            return "SUL";
        }
        if (uf.equals("RJ")) {
            return "SUDESTE";
        }
        if (uf.equals("RN")) {
            return "NORDESTE";
        }
        if (uf.equals("RO")) {
            return "NORTE";
        }
        if (uf.equals("RR")) {
            return "NORTE";
        }
        if (uf.equals("RS")) {
            return "SUL";
        }
        if (uf.equals("RO")) {
            return "NORTE";
        }
        if (uf.equals("SC")) {
            return "SUL";
        }
        if (uf.equals("SE")) {
            return "NORDESTE";
        }
        if (uf.equals("SP")) {
            return "SUDESTE";
        }

        return null;
    }

    /**
     * @return the nmMunicipio
     */
    public String getNmMunicipio() {
        return nmMunicipio;
    }

    /**
     * @param nmMunicipio the nmMunicipio to set
     */
    public void setNmMunicipio(String nmMunicipio) {
        this.nmMunicipio = nmMunicipio;
    }

    /**
     * @return the codMunicipio
     */
    public String getCodMunicipio() {
        return codMunicipio;
    }

    /**
     * @param codMunicipio the codMunicipio to set
     */
    public void setCodMunicipio(String codMunicipio) {
        this.codMunicipio = codMunicipio;
    }

    /**
     * @return the sgUF
     */
    public String getSgUF() {
        return sgUF;
    }

    /**
     * @param sgUF the sgUF to set
     */
    public void setSgUF(String sgUF) {
        this.sgUF = sgUF;
    }

    /**
     * @return the uf
     */
    public UF getUf() {
        return uf;
    }

    /**
     * @param uf the uf to set
     */
    public void setUf(UF uf) {
        this.uf = uf;
    }

    /**
     * @return the nmRegiao
     */
    public String getNmRegiao() {
        return nmRegiao;
    }

    /**
     * @param nmRegiao the nmRegiao to set
     */
    public void setNmRegiao(String nmRegiao) {
        this.nmRegiao = nmRegiao;
    }

    
    public String getPeriodo1() {
        return periodo1;
    }

    public String getPeriodo2() {
        return periodo2;
    }

    public String getPeriodo3() {
        return periodo3;
    }

    public String getPeriodo4() {
        return periodo4;
    }

    public String getPeriodo5() {
        return periodo5;
    }

    public String getPeriodo6() {
        return periodo6;
    }

    public String getPeriodo7() {
        return periodo7;
    }

    public String getPeriodo8() {
        return periodo8;
    }

    public String getPeriodo9() {
        return periodo9;
    }

    public String getPeriodo10() {
        return periodo10;
    }

    public String getPeriodo11() {
        return periodo11;
    }

    public String getPeriodo12() {
        return periodo12;
    }

    public String getPeriodo13() {
        return periodo13;
    }

    public String getPeriodo14() {
        return periodo14;
    }

    public String getPeriodo15() {
        return periodo15;
    }

    public String getPeriodo16() {
        return periodo16;
    }

    public String getPeriodo17() {
        return periodo17;
    }

    public String getPeriodo18() {
        return periodo18;
    }

    public void setPeriodo1(String periodo1) {
        this.periodo1 = periodo1;
    }

    public void setPeriodo2(String periodo2) {
        this.periodo2 = periodo2;
    }

    public void setPeriodo3(String periodo3) {
        this.periodo3 = periodo3;
    }

    public void setPeriodo4(String periodo4) {
        this.periodo4 = periodo4;
    }

    public void setPeriodo5(String periodo5) {
        this.periodo5 = periodo5;
    }

    public void setPeriodo6(String periodo6) {
        this.periodo6 = periodo6;
    }

    public void setPeriodo7(String periodo7) {
        this.periodo7 = periodo7;
    }

    public void setPeriodo8(String periodo8) {
        this.periodo8 = periodo8;
    }

    public void setPeriodo9(String periodo9) {
        this.periodo9 = periodo9;
    }

    public void setPeriodo10(String periodo10) {
        this.periodo10 = periodo10;
    }

    public void setPeriodo11(String periodo11) {
        this.periodo11 = periodo11;
    }

    public void setPeriodo12(String periodo12) {
        this.periodo12 = periodo12;
    }

    public void setPeriodo13(String periodo13) {
        this.periodo13 = periodo13;
    }

    public void setPeriodo14(String periodo14) {
        this.periodo14 = periodo14;
    }

    public void setPeriodo15(String periodo15) {
        this.periodo15 = periodo15;
    }

    public void setPeriodo16(String periodo16) {
        this.periodo16 = periodo16;
    }

    public void setPeriodo17(String periodo17) {
        this.periodo17 = periodo17;
    }

    public void setPeriodo18(String periodo18) {
        this.periodo18 = periodo18;
    }

    public boolean isIrregular() {
        return irregular;
    }

    public void setIrregular(boolean irregular) {
        this.irregular = irregular;
    }
    
}
