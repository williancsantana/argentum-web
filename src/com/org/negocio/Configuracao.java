/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.org.negocio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author geraldo
 */
public class Configuracao {

    private String versao;
    private boolean Dbf;
    private String caminho;
    private String caminhoArquivo;

    public Configuracao() {
        this.setVersao(Configuracao.getPropriedade("versao"));
        this.setDbf(false);
        this.setDbf(false);
        this.setCaminho(Configuracao.getPropriedade("caminho"));
    }

    public static  void setPropriedade(String key, String value){
        File file = new File("config.properties");
        Properties props = new Properties();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            //le os dados que estao no arquivo
            props.load(fis);
            props.setProperty(key, value);
            props.store(new FileOutputStream("config.properties"), value);
            fis.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }

    }

    public static String getPropriedade(String prop) {
        File file = new File("config.properties");
        Properties props = new Properties();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            //le os dados que estao no arquivo
            props.load(fis);
            fis.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
        return props.getProperty(prop);
    }

    /**
     * @return the versao
     */
    public String getVersao() {
        return versao;
    }

    /**
     * @param versao the versao to set
     */
    public void setVersao(String versao) {
        this.versao = versao;
    }

    /**
     * @return the isDbf
     */
    public boolean isDbf() {
        return Dbf;
    }

    /**
     * @param isDbf the isDbf to set
     */
    public void setDbf(boolean isDbf) {
        this.Dbf = isDbf;
    }

    /**
     * @return the caminho
     */
    public String getCaminho() {
        return caminho;
    }

    /**
     * @param caminho the caminho to set
     */
    public void setCaminho(String caminho) {
        this.caminho = caminho;
    }

    /**
     * @return the caminhoArquivo
     */
    public String getCaminhoArquivo() {
        return caminhoArquivo;
    }

    /**
     * @param caminhoArquivo the caminhoArquivo to set
     */
    public void setCaminhoArquivo(String caminhoArquivo) {
        this.caminhoArquivo = caminhoArquivo;
    }
}
