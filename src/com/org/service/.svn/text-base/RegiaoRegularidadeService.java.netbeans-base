/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.org.service;

import com.linuxense.javadbf.DBFField;
import com.linuxense.javadbf.DBFWriter;
import com.org.beans.CampoDBF;
import com.org.beans.RegiaoRegularidade;
import com.org.model.classes.Municipio;
import com.org.model.classes.UF;
import com.org.util.Report;
import com.org.util.SinanDateUtil;
import com.org.util.SinanUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 *
 * @author Taidson
 */
public class RegiaoRegularidadeService {
    
    /**
     * Método responsável por gerar relatório de regularidade na alimentação do Sinan.
     * @param listaMunicipio
     * @param parametros
     * @param countFields
     * @param isDBF
     * @throws IOException 
     * @autor Taidson
     */
    public void gerarRelatorioRegularidade(List<Municipio> listaMunicipio, Map parametros, int countFields, String isDBF) throws IOException{
        List<RegiaoRegularidade> listaRegiaoRegularidade = agruparPorRegiao(listaMunicipio);
        Report report = new Report();
        JRDataSource jrds = new JRBeanCollectionDataSource(listaRegiaoRegularidade);
        parametros.put("TITULO", "Municípios Irregulares na Alimentação do Sinan");
        report.adionarParametrosPadrao(parametros);
        report.gerarRelatorio(jrds, parametros, "regularidade.jasper", Boolean.TRUE);
        if(Boolean.parseBoolean(isDBF))
            this.gerarDBFRegularidade(listaRegiaoRegularidade, parametros, countFields);
        
    }
    

    /**
     * Método responsável por gerar relatório de regularidade na alimentação do Sinan de forma sintética.
     * Apresenta por região e estado os dados dos municípios irregulares.
     * @param listaMunicipio
     * @param parametros
     * @param countFields
     * @param isDBF
     * @param calculaBrasil
     * @throws IOException
     * @autor Taidson
     */
    public void gerarRelatorioPropMunicIrreg(List<Municipio> listaMunicipio, Map parametros, int countFields, String isDBF, boolean calculaBrasil) throws IOException{
        List<RegiaoRegularidade> listaRegiaoRegularidade = agruparPorRegiaoUF(listaMunicipio);
        calculaMunicipio(listaRegiaoRegularidade);
        calculaBrasil(listaRegiaoRegularidade, parametros, calculaBrasil);
        calculaRegiao(listaRegiaoRegularidade);
        Report report = new Report();
        JRDataSource jrds = new JRBeanCollectionDataSource(listaRegiaoRegularidade);
        parametros.put("TITULO", "Municípios Irregulares na Alimentação do Sinan");
        report.adionarParametrosPadrao(parametros);
        report.gerarRelatorio(jrds, parametros, "regularidade2.jasper", Boolean.TRUE);
        
    }
    
    /**
     * Método reponsável por realizar o cálculo do percentual de municípios irregulares por estado.
     * @param listaRegiaoRegularidade 
     * @autor Taidson
     */
    private void calculaMunicipio(List<RegiaoRegularidade> listaRegiaoRegularidade){
        
        for (RegiaoRegularidade regiaoRegularidade : listaRegiaoRegularidade) {
            for (UF uf : regiaoRegularidade.getListaUF()) {
                int qtdeMunic = 0;
                double qtdeIrregular = 0.0;

                for (Municipio municipio : regiaoRegularidade.getListaMunicipio()) {
                    
                    if(uf.getNmUF().equals(municipio.getUf().getNmUF())){
                        if(municipio.isIrregular()){
                            uf.setQtdMunicipiosSemNotificacao(uf.getQtdMunicipiosSemNotificacao()+1);
                            qtdeIrregular++;
                        }
                        uf.setQtdMunicipios(uf.getQtdMunicipios()+1);
                        qtdeMunic++;
                    }
                }
                if(qtdeMunic != 0){
                    uf.setPercentualIrregular(SinanUtil.converterDoubleDuasDecimais((qtdeIrregular/qtdeMunic)*100));
                }
                uf.setNmUF(SinanUtil.siglaUFToNomeUF(uf.getNmUF()));
            }
        }
    }
    
    /**
     * Realiza o cálculo do percentual de municípios irregulares a nível Região.
     * @param listaRegiaoRegularidade 
     * @autor Taidson
     */
    private void calculaRegiao(List<RegiaoRegularidade> listaRegiaoRegularidade){
        for (RegiaoRegularidade regiaoRegularidade : listaRegiaoRegularidade) {
            int qtdeMunic = 0;
            double qtdeIrregular = 0.0;
            for (UF uf : regiaoRegularidade.getListaUF()) {
                qtdeMunic =  qtdeMunic + uf.getQtdMunicipios();
                qtdeIrregular = qtdeIrregular + uf.getQtdMunicipiosSemNotificacao();
            }
            regiaoRegularidade.setQtdeMunicipio(qtdeMunic);
            regiaoRegularidade.setQtdeMunicIrregular((int) qtdeIrregular);
            regiaoRegularidade.setPercetualIrregular(SinanUtil.converterDoubleDuasDecimais((qtdeIrregular/qtdeMunic)*100));
        }
        
    }

    
    /**
     * Realiza o cálculo do percentual de municípios irregulares a nível Brasil.
     * @param listaRegiaoRegularidade
     * @param parametros
     * @param calculaBrasil 
     * @autor Taidson
     */
    private void calculaBrasil(List<RegiaoRegularidade> listaRegiaoRegularidade, Map<String, String> parametros, boolean calculaBrasil){
        
        int qtdeMunic = 0;
        double qtdeIrregular = 0.0;
        double percentual = 0.0;
        
        for (RegiaoRegularidade regiaoRegularidade : listaRegiaoRegularidade) {
            for (UF uf : regiaoRegularidade.getListaUF()) {
                qtdeMunic =  qtdeMunic + uf.getQtdMunicipios();
                qtdeIrregular = qtdeIrregular + uf.getQtdMunicipiosSemNotificacao();
            }
            SinanUtil.ordenaLista(regiaoRegularidade.getListaUF(), "nmUF");
        }
        
        percentual = SinanUtil.converterDoubleDuasDecimais((qtdeIrregular/qtdeMunic)*100);
        
        if(calculaBrasil){
            parametros.put("BRASIL1", qtdeMunic+"");
            parametros.put("BRASIL2",(((int) qtdeIrregular)+""));
            parametros.put("BRASIL3", percentual+"");
        }else{
            parametros.put("BRASIL1", "");
            parametros.put("BRASIL2", "");
            parametros.put("BRASIL3", "");
        }
        
    }
    
    /**
     * Método responsável por agrupar os municípios por estado por região.
     * @param listaMunicipio
     * @return 
     * @autor Taidson
     */
    private List<RegiaoRegularidade> agruparPorRegiaoUF(List<Municipio> listaMunicipio){
        List<RegiaoRegularidade> listaRegiaoRegularidade = agruparPorRegiao(listaMunicipio);
        Map<String, String> mapaUF = new HashMap<String, String>();
        
        if(!SinanUtil.isListEmpty(listaRegiaoRegularidade)){
            for (RegiaoRegularidade regiao : listaRegiaoRegularidade) {
                if(!SinanUtil.isListEmpty(regiao.getListaMunicipio())){
                    mapaUF = mapearUF(regiao.getListaMunicipio());
                    Iterator entries = mapaUF.entrySet().iterator();
                    List<UF> listaUF = new ArrayList<UF>();
                    while (entries.hasNext()) {
                        UF uf = new UF();
                        Entry thisEntry = (Entry) entries.next();
                        uf.setNmUF(thisEntry.getValue().toString());
                        listaUF.add(uf);
                    }
                    regiao.setListaUF(listaUF);
                }
            }
        }
        return listaRegiaoRegularidade;
    }
    
    /**
     * Cria um mapa com todas as ufs dos municípios informados no parâmetro.
     * @param listaMunicipio
     * @return 
     * @autor Taidson
     */
    private Map<String, String> mapearUF(List<Municipio> listaMunicipio){
        Map<String, String> mapaUF = new HashMap<String, String>();
        
        for (Municipio municipio : listaMunicipio) {
            if(!mapaUF.containsKey(municipio.getUf().getNmUF())){
                mapaUF.put(municipio.getUf().getNmUF(), municipio.getUf().getNmUF());
            }
        }
        return mapaUF;
    }
    
    /**
     * Método responsável por agrupar os municípios por região
     * @param listaMunicipio
     * @return 
     */
    private List<RegiaoRegularidade> agruparPorRegiao(List<Municipio> listaMunicipio){
        List<RegiaoRegularidade> listaRegiaoRegularidade = new ArrayList<RegiaoRegularidade>();
        RegiaoRegularidade regiaoNorte, regiaoNordeste, regiaoCentroOeste, regiaoSudeste, regiaoSul;
        
        List<Municipio> listaNorte = new ArrayList<Municipio>();
        regiaoNorte = new RegiaoRegularidade();
        regiaoNorte.setNome("Norte");
        List<Municipio> listaNordeste = new ArrayList<Municipio>();
        regiaoNordeste = new RegiaoRegularidade();
        regiaoNordeste.setNome("Nordeste");
        List<Municipio> listaCentroOeste = new ArrayList<Municipio>();
        regiaoCentroOeste = new RegiaoRegularidade();
        regiaoCentroOeste.setNome("Centro-oeste");
        List<Municipio> listaSudeste = new ArrayList<Municipio>();
        regiaoSudeste = new RegiaoRegularidade();
        regiaoSudeste.setNome("Sudeste");
        List<Municipio> listaSul = new ArrayList<Municipio>();
        regiaoSul = new RegiaoRegularidade();
        regiaoSul.setNome("Sul");
        
        if(!SinanUtil.isListEmpty(listaMunicipio)){
            for (Municipio item : listaMunicipio) {
                if(item.getNmRegiao().equals("NORTE")){
                    listaNorte.add(item);
                }else if(item.getNmRegiao().equals("NORDESTE")){
                    listaNordeste.add(item);
                }else if(item.getNmRegiao().equals("CENTRO-OESTE")){
                    listaCentroOeste.add(item);
                }else if(item.getNmRegiao().equals("SUDESTE")){
                    listaSudeste.add(item);
                }else if(item.getNmRegiao().equals("SUL")){
                    listaSul.add(item);
                }
            }
            regiaoNorte.setListaMunicipio(listaNorte);
            regiaoNordeste.setListaMunicipio(listaNordeste);
            regiaoCentroOeste.setListaMunicipio(listaCentroOeste);
            regiaoSudeste.setListaMunicipio(listaSudeste);
            regiaoSul.setListaMunicipio(listaSul);
        }
        
        if(!SinanUtil.isListEmpty(regiaoNorte.getListaMunicipio()))
            listaRegiaoRegularidade.add(regiaoNorte);
        if(!SinanUtil.isListEmpty(regiaoNordeste.getListaMunicipio()))
            listaRegiaoRegularidade.add(regiaoNordeste);
        if(!SinanUtil.isListEmpty(regiaoCentroOeste.getListaMunicipio()))
            listaRegiaoRegularidade.add(regiaoCentroOeste);
        if(!SinanUtil.isListEmpty(regiaoSudeste.getListaMunicipio()))
            listaRegiaoRegularidade.add(regiaoSudeste);
        if(!SinanUtil.isListEmpty(regiaoSul.getListaMunicipio()))
            listaRegiaoRegularidade.add(regiaoSul);
        
        return listaRegiaoRegularidade;
   }
    
    
   /**
     * Método responsável por gerar arquivo DBF para o relatório de regularidade no sinan.
     * @param lista
     * @param periodos
     * @param countFields
     * @throws IOException 
     * @autor Taidson
     */ 
   public void gerarDBFRegularidade(List<RegiaoRegularidade> lista, Map periodos, int countFields) throws IOException{
        CampoDBF field;
        DBFField fields[] = new DBFField[countFields];

        field = new CampoDBF("ID_MUNIC", "String", 7, 0);
        fields[0] = field;
        
        field = new CampoDBF("NM_MUNIC", "String", 30, 0);
        fields[1] = field;
        
        field = new CampoDBF("SG_UF", "String", 2, 0);
        fields[2] = field;
        
        field = new CampoDBF("DT_AVALIA", "String", 10, 0);
        fields[3] = field;
        
        if(periodos.get("periodo1") != null){
            field = new CampoDBF("PERIODO01","String", 2, 0);
            fields[4] = field;
        }
        if(periodos.get("periodo2") != null){
            field = new CampoDBF("PERIODO02","String", 2, 0);
            fields[5] = field;
        }
        if(periodos.get("periodo3") != null){
            field = new CampoDBF("PERIODO03","String", 2, 0);
            fields[6] = field;
        }
        if(periodos.get("periodo4") != null){
            field = new CampoDBF("PERIODO04","String", 2, 0);
            fields[7] = field;
        }
        if(periodos.get("periodo5") != null){
            field = new CampoDBF("PERIODO05","String", 2, 0);
            fields[8] = field;
        }
        if(periodos.get("periodo6") != null){
            field = new CampoDBF("PERIODO06","String", 2, 0);
            fields[9] = field;
        }
        if(periodos.get("periodo7") != null){
            field = new CampoDBF("PERIODO07","String", 2, 0);
            fields[10] = field;
        }
        if(periodos.get("periodo8") != null){
            field = new CampoDBF("PERIODO08","String", 2, 0);
            fields[11] = field;
        }
        if(periodos.get("periodo9") != null){
            field = new CampoDBF("PERIODO09","String", 2, 0);
            fields[12] = field;
        }
        if(periodos.get("periodo10") != null){
            field = new CampoDBF("PERIODO10","String", 2, 0);
            fields[13] = field;
        }
        if(periodos.get("periodo11") != null){
            field = new CampoDBF("PERIODO11","String", 2, 0);
            fields[14] = field;
        }
        if(periodos.get("periodo12") != null){
            field = new CampoDBF("PERIODO12","String", 2, 0);
            fields[15] = field;
        }
        if(periodos.get("periodo13") != null){
            field = new CampoDBF("PERIODO13","String", 2, 0);
            fields[16] = field;
        }
        if(periodos.get("periodo14") != null){
            field = new CampoDBF("PERIODO14","String", 2, 0);
            fields[17] = field;
        }
        if(periodos.get("periodo15") != null){
            field = new CampoDBF("PERIODO15","String", 2, 0);
            fields[18] = field;
        }
        if(periodos.get("periodo16") != null){
            field = new CampoDBF("PERIODO16","String", 2, 0);
            fields[19] = field;
        }
        if(periodos.get("periodo17") != null){
            field = new CampoDBF("PERIODO17","String", 2, 0);
            fields[20] = field;
        }
        if(periodos.get("periodo18") != null){
            field = new CampoDBF("PERIODO18","String", 2, 0);
            fields[21] = field;
        }
        this.prepareDataToDBF(lista, fields, countFields);
    }
   
   /**
    * Método de auxílio ao método gerarDBFRegularidade na geração do arquivo DBF
    * para o relatório de regularidade no Sinan.
    * @param lista
    * @param fields
    * @param countFields
    * @throws IOException 
    */
   private void prepareDataToDBF(List<RegiaoRegularidade> lista, DBFField fields[], int countFields) throws IOException{
        
        DBFWriter writer = new DBFWriter();
        writer.setFields(fields);
        for (RegiaoRegularidade regiao : lista) {
            for (Municipio item : regiao.getListaMunicipio()) {
                Object rowData[] =  new Object[countFields];
                rowData[0] = item.getCodMunicipio();
                rowData[1] = item.getNmMunicipio();
                rowData[2] = item.getSgUF();
                rowData[3] = SinanDateUtil.currentDateString();
                if(countFields > 4){
                    rowData[4] = item.getPeriodo1();
                }
                if(countFields > 5){
                    rowData[5] = item.getPeriodo2();
                }
                if(countFields > 6){
                    rowData[6] = item.getPeriodo3();
                }
                if(countFields > 7){
                    rowData[7] = item.getPeriodo4();
                }
                if(countFields > 8){
                    rowData[8] = item.getPeriodo5();
                }
                if(countFields > 9){
                    rowData[9] = item.getPeriodo6();
                }
                if(countFields > 10){
                    rowData[10] = item.getPeriodo7();
                }
                if(countFields > 11){
                    rowData[11] = item.getPeriodo8();
                }
                if(countFields > 12){
                    rowData[12] = item.getPeriodo9();
                }
                if(countFields > 13){
                    rowData[13] = item.getPeriodo10();
                }
                if(countFields > 14){
                    rowData[14] = item.getPeriodo11();
                }
                if(countFields > 15){
                    rowData[15] = item.getPeriodo12();
                }
                if(countFields > 16){
                    rowData[16] = item.getPeriodo13();
                }
                if(countFields > 17){
                    rowData[17] = item.getPeriodo14();
                }
                if(countFields > 18){
                    rowData[18] = item.getPeriodo15();
                }
                if(countFields > 19){
                    rowData[19] = item.getPeriodo16();
                }
                if(countFields > 20){
                    rowData[20] = item.getPeriodo17();
                }
                if(countFields > 21){
                    rowData[21] = item.getPeriodo18();
                }
                
               writer.addRecord(rowData); 
            }
        }
        SinanUtil.setNomeArquivoDBF();
        SinanUtil.gerarDBF(writer);
   }
   
}