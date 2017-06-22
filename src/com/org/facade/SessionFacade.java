/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.org.facade;

import java.util.Vector;
import javax.swing.JPanel;

/**
 *
 * @author geraldo
 */
public interface SessionFacade {
    public String[] retornaGrupos();
    public String[] retornaAgravos(String grupo);
    public String[] retornaSubgrupos(String grupo); /** SINAN_RELATORIO Nova função implementada para retornar as pactuações anteriores */
    public String[] retornaRelatorios(String grupo,String agravo);
    public JPanel retornaPanel(String relatorio);
    public Vector<String> retornaUFs();
    public Vector<String> retornaRegionais(String UF);
    public Vector<String> retornaMunicipios(String UF);
    public Vector<String> retornaMunicipios(String UF, String regional);
    public int getCodigoUf(String UF);
    public void geraRelatorio(String uf, String municipio);
    public String getCodMunicipio(String municipio, String UF);
}
