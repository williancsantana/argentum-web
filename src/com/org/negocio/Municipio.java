/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.org.negocio;

/**
 *
 * @author geraldo
 */
public class Municipio {

    private String nmMunicipio;
    private String codMunicipio;
    private String sgUF;
    private UF uf;
    private String nmRegiao;

    public Municipio(String nome, String codigo, String siglaUf) {
        this.nmMunicipio = nome;
        this.codMunicipio = codigo;
        this.uf = new UF();
        this.uf.setNmUF(siglaUf);
        this.uf.setRegiao(new Regiao());
        this.uf.getRegiao().setNmRegiao(buscaRegiao(siglaUf));
        this.nmRegiao = this.uf.getRegiao().getNmRegiao();
        this.sgUF = siglaUf;
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
}
