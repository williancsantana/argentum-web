/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.org.service;

import com.linuxense.javadbf.DBFException;
import com.linuxense.javadbf.DBFField;
import com.linuxense.javadbf.DBFReader;
import com.linuxense.javadbf.DBFWriter;
import com.org.bd.DBFUtil;
import com.org.beans.CampoDBF;
import com.org.beans.RegiaoRegularidade;
import com.org.beans.RegiaoSaude;
import com.org.beans.RegiaoSaudePQAVS;
import com.org.beans.UFPQAVS;
import com.org.facade.SessionFacadeImpl;
import com.org.model.classes.Municipio;
import com.org.model.classes.UF;
import com.org.model.classes.agravos.oportunidade.OportunidadeAgravoPQAVS;
import com.org.util.Report;
import com.org.util.SinanDateUtil;
import com.org.util.SinanUtil;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;


/**
 *
 * @author Taidson
 */
public class AutoctoneMalariaService {
    
    
    
    public void gerarRelatorioPQAVS(List<RegiaoSaudePQAVS> listaRegiaoSaude, Map parametros, int countFields) throws IOException{
        Report report = new Report();
        JRDataSource jrds = new JRBeanCollectionDataSource(listaRegiaoSaude);
        parametros.put("TITULO", "Encerramento Oportuno");
        
        report.adionarParametrosPadrao(parametros);
        report.gerarRelatorio(jrds, parametros, "oportunidadePQAVS.jasper", Boolean.TRUE);
        
//        if(Boolean.parseBoolean(isDBF))
//            this.gerarDBFRegularidade(listaRegiaoRegularidade, parametros, countFields);
        
    }
    
    public void gerarRelatorioPQAVSUFRegiaoSaude(List<UFPQAVS> lista, Map parametros, int countFields, String isDBF) throws IOException{
        Report report = new Report();
        JRDataSource jrds = new JRBeanCollectionDataSource(lista);
        parametros.put("TITULO", "Encerramento Oportuno");
        
        report.adionarParametrosPadrao(parametros);
        report.gerarRelatorio(jrds, parametros, "oportunidadePQAVS.jasper", Boolean.TRUE);
        
//        if(Boolean.parseBoolean(isDBF))
//            this.gerarDBFRegularidade(listaRegiaoRegularidade, parametros, countFields);
        
    }

      public void somatorio(List<RegiaoSaudePQAVS> listaRegiaoSaude, Map parametros){
        int countOport;
        int countNotif;
        int countTotalMunic = 0;
        int countTotalOport = 0;
        int countTotalNot = 0;
        int countRegiao;
        for (RegiaoSaudePQAVS regiaoSaude : listaRegiaoSaude) {
            countOport = 0;
            countNotif = 0;
            countTotalMunic += regiaoSaude.getLista().size();
            for (OportunidadeAgravoPQAVS municipio : regiaoSaude.getLista()) {
                countOport += municipio.getQtdOportuno();
                countNotif += municipio.getTotal();
                regiaoSaude.setUf(municipio.getUf());
            }
            regiaoSaude.setQtdOportuno(countOport);
            regiaoSaude.setTotal(countNotif);
            countTotalOport += countOport;
            countTotalNot += countNotif;
        }
        parametros.put("TOTAL1", ""+countTotalMunic);
        parametros.put("TOTAL2", ""+countTotalOport);
        parametros.put("TOTAL3", ""+countTotalNot);
    }
      
         public void somatorioUFRegiaoSaude(List<UFPQAVS> listaUF, Map parametros){
        int countOport;
        int countNotif;
        int countTotalRegiao = 0;
        int countTotalOport = 0;
        int countTotalNot = 0;
        for (UFPQAVS uf : listaUF) {
            countOport = 0;
            countNotif = 0;
            countTotalRegiao += uf.getLista().size();
            for (RegiaoSaudePQAVS regiaoSaude : uf.getLista()) {
                countOport += regiaoSaude.getQtdOportuno();
                countNotif += regiaoSaude.getTotal();
            }
            uf.setQtdOportuno(countOport);
            uf.setTotal(countNotif);
            countTotalOport += countOport;
            countTotalNot += countNotif;
        }
        parametros.put("TOTAL1", ""+countTotalRegiao);
        parametros.put("TOTAL2", ""+countTotalOport);
        parametros.put("TOTAL3", ""+countTotalNot);
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
    @Deprecated
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
     * Cria um mapa com todas as ufs dos municípios informados no parâmetro.
     * @param listaMunicipio
     * @return 
     * @autor Taidson
     */
    public Map<String, List<OportunidadeAgravoPQAVS>> agruparRegiaoSaude(List<OportunidadeAgravoPQAVS> listaBean){
        Map<String, List<OportunidadeAgravoPQAVS>> mapaRegiaoSaude = new HashMap<String, List<OportunidadeAgravoPQAVS>>();
        List<OportunidadeAgravoPQAVS> listaMunicipios;
        
        listaBean = SinanUtil.removeMunicipiosIgnoradosPQAVS(listaBean);
        
        for (OportunidadeAgravoPQAVS bean : listaBean) {
            if(!bean.getNmAgravo().equals("TOTAL")){
                listaMunicipios = new ArrayList<OportunidadeAgravoPQAVS>();
                if(bean.getUf() != null && bean.getUf().equals("DF")){
                    bean.setNmAgravo("BRASILIA");
                }
                if(mapaRegiaoSaude.containsKey(bean.getUf() +"       "+ bean.getRegiaoSaude())){
                    listaMunicipios = mapaRegiaoSaude.get(bean.getUf() +"       "+ bean.getRegiaoSaude());
                }
                listaMunicipios.add(bean);
                SinanUtil.ordenaLista(listaMunicipios, "nmAgravo");
                mapaRegiaoSaude.put(bean.getUf() +"       "+ bean.getRegiaoSaude(), listaMunicipios);
            }
        }
        return mapaRegiaoSaude;
    }
    
    public Map<String, List<OportunidadeAgravoPQAVS>> agruparRegiaoSaude2(List<OportunidadeAgravoPQAVS> listaBean){
        Map<String, List<OportunidadeAgravoPQAVS>> mapaRegiaoSaude = new HashMap<String, List<OportunidadeAgravoPQAVS>>();
        List<OportunidadeAgravoPQAVS> listaMunicipios;
        
        listaBean = SinanUtil.removeMunicipiosIgnoradosPQAVS(listaBean);
        
        for (OportunidadeAgravoPQAVS bean : listaBean) {
            if(!bean.getNmAgravo().equals("TOTAL")){
                listaMunicipios = new ArrayList<OportunidadeAgravoPQAVS>();
                if(bean.getUf() != null && bean.getUf().equals("DF")){
                    bean.setNmAgravo("BRASILIA");
                }
                if(mapaRegiaoSaude.containsKey(bean.getCodRegiaoSaude())){
                    listaMunicipios = mapaRegiaoSaude.get(bean.getCodRegiaoSaude());
                }
                listaMunicipios.add(bean);
                SinanUtil.ordenaLista(listaMunicipios, "nmAgravo");
                mapaRegiaoSaude.put(bean.getCodRegiaoSaude(), listaMunicipios); //concatenando com o parâmetro +bean.getUf()
            }
        }
        return mapaRegiaoSaude;
    }
    //ANALISANDO O MÉTODO agruparRegiaoSaude2 em 18fev2014
     
    ///PAREI AQUI /// PAREI AQUI
    public Map<String, List<RegiaoSaudePQAVS>> agruparUFRegiaoSaude2(List<RegiaoSaudePQAVS> listaBean){
        Map<String, List<RegiaoSaudePQAVS>> mapaRegiaoSaude = new HashMap<String, List<RegiaoSaudePQAVS>>();
        List<RegiaoSaudePQAVS> listaRegiaoSaude;
        
        for (RegiaoSaudePQAVS bean : listaBean) {
            listaRegiaoSaude = new ArrayList<RegiaoSaudePQAVS>();
            if(bean.getUf() != null && bean.getUf().equals("DF")){
                bean.setNmAgravo("BRASILIA");
            }
            if(mapaRegiaoSaude.containsKey(bean.getUf())){
                listaRegiaoSaude = mapaRegiaoSaude.get(bean.getUf());
            }
            listaRegiaoSaude.add(bean);
 //           SinanUtil.ordenaLista(listaRegiaoSaude, "nome");
            mapaRegiaoSaude.put(bean.getUf(), listaRegiaoSaude);
        }
        return mapaRegiaoSaude;
    }
    
    //ALTERAR ESSE MÉTODO PARA ADEQUAR AO AGRUPAMENTO POR UF
    public List<UFPQAVS> converterMapaUFRegiaoSaudeEmLista(Map<String, List<RegiaoSaudePQAVS>> mapaUF, Map parametros){
        List<UFPQAVS> listaUF = new ArrayList<UFPQAVS>();
        UFPQAVS uf;
        int count=0;
        Iterator entries = mapaUF.entrySet().iterator();
            while (entries.hasNext()) {
                count++;
                Entry thisEntry = (Entry) entries.next();
                SinanUtil.ordenaLista(mapaUF.get(thisEntry.getKey()), "nmAgravo");
                uf = new UFPQAVS(thisEntry.getKey().toString(), mapaUF.get(thisEntry.getKey())); //CRIAR MÉTODO CONSTRUTOR NA CLASSE UF
                uf.setNmAgravo(SinanUtil.siglaUFToNomeUF(uf.getNmAgravo()));
                listaUF.add(uf);
            }

        SinanUtil.ordenaLista(listaUF, "nmAgravo");
        this.somatorioUFRegiaoSaude(listaUF, parametros);
        return listaUF;
    }
    
    //DEBUGAR ESSE MÉTODO
    public List<RegiaoSaudePQAVS> converterMapaRegiaoSaudeEmLista(Map<String, List<OportunidadeAgravoPQAVS>> mapaRegiaoSaude, Map parametros){
        List<RegiaoSaudePQAVS> listaRegiaoSaude = new ArrayList<RegiaoSaudePQAVS>();
        RegiaoSaudePQAVS regiaoSaude;
        int count=0;
        Iterator entries = mapaRegiaoSaude.entrySet().iterator();
            while (entries.hasNext()) {
                count++;
                Entry thisEntry = (Entry) entries.next();
                SinanUtil.ordenaLista(mapaRegiaoSaude.get(thisEntry.getKey()), "nmAgravo");
                regiaoSaude = new RegiaoSaudePQAVS(thisEntry.getKey().toString(), this.regiaoSaudeNome(thisEntry.getKey().toString()), mapaRegiaoSaude.get(thisEntry.getKey())); //colocar um método para retornar o nome da regiao de acordo com o parâmetro thisEntry.getKey().toString() que será modificado para código da regiao
                listaRegiaoSaude.add(regiaoSaude);
//                if(count >= 9) break;
            }

        SinanUtil.ordenaLista(listaRegiaoSaude, "nmAgravo");
        //colocar uj if perguntando se a adesagregação é por uf/regiao
        this.somatorio(listaRegiaoSaude, parametros);
        return listaRegiaoSaude;
    }
    
    /**
     * Retorna o nome de um região de saúde a partir de seu respectivo código
     * @param idRegiaoSaude
     * @return
     * @author Taidson
     */
    public static String regiaoSaudeNome(String idRegiaoSaude){
        DBFReader reader = retornaObjetoDbfCaminhoArquivo("REGIAO", "dbf\\");
        Object[] rowObjects;
        DBFUtil utilDbf = new DBFUtil();
        try {
            utilDbf.mapearPosicoes(reader);
            while ((rowObjects = reader.nextRecord()) != null) {
                if (idRegiaoSaude.equals(utilDbf.getString(rowObjects, "ID_REGIAO"))) {
                    return utilDbf.getString(rowObjects, "NM_REGIAO");
                }
            }
        } catch (DBFException e) {
            Logger.getLogger(SessionFacadeImpl.class.getName()).log(Level.SEVERE, null, "Erro ao carregar região de saúde:\n" + e);
        }
        return idRegiaoSaude;
  }
    
     

  /**
     * Retorna o objeto arquivo dbf
     * @param arquivo
     * @param caminho
     * @return 
     */
  public static DBFReader retornaObjetoDbfCaminhoArquivo(String arquivo, String caminho) {
        DBFReader reader = null;
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(caminho + arquivo + ".DBF"); // take dbf file as program argument

        } catch (FileNotFoundException e) {
            Logger.getLogger(SessionFacadeImpl.class.getName()).log(Level.SEVERE, null, "Erro: tabela " + arquivo + ".dbf nao encontrada.\n" + e);
            return reader;
        }
        try {
            reader = new DBFReader(inputStream);
        } catch (DBFException e) {
            Logger.getLogger(SessionFacadeImpl.class.getName()).log(Level.SEVERE, null, "Erro: tabela " + arquivo + ".dbf nao encontrada.\n" + e);
        }
        return reader;
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
   public void gerarDBFPQAVSDefineCampos(List<OportunidadeAgravoPQAVS> lista) throws IOException{
       
        CampoDBF field;
        DBFField fields[] = new DBFField[9];

        field = new CampoDBF("UF", "String", 2, 0);
        fields[0] = field;
        
        field = new CampoDBF("COD_CIR", "String", 5, 0);
        fields[1] = field;

        field = new CampoDBF("REGIAO", "String", 80, 0);
        fields[2] = field;
        
        field = new CampoDBF("COD_IBGE", "String", 6, 0);
        fields[3] = field;

        field = new CampoDBF("MUNICIPIO", "String", 80, 0);
        fields[4] = field;
        
        field = new CampoDBF("AUSEN_CASO", "String", 1, 0);
        fields[5] = field;        
        
        field = new CampoDBF("NUMERADOR", "String", 12, 0);
        fields[6] = field;
        
        field = new CampoDBF("DENOMINAD", "String", 12, 0);
        fields[7] = field;
        
        field = new CampoDBF("RESULTADO", "String", 12, 0);
        fields[8] = field;
        
        this.prepareDataToDBFPQAVS(lista, fields, 9);
    }
   
      private void prepareDataToDBFPQAVS(List<OportunidadeAgravoPQAVS> lista, DBFField fields[], int countFields) throws IOException{
          
        DBFWriter writer = new DBFWriter();
        Double oportuno;
        Double total;
        writer.setFields(fields);
        lista = SinanUtil.removeMunicipiosIgnoradosPQAVS(lista);
        for (OportunidadeAgravoPQAVS item : lista) {
            
            Object rowData[] =  new Object[countFields];
            rowData[0] = item.getUf();
            rowData[1] = item.getCodRegiaoSaude();
            rowData[2] = item.getRegiaoSaude();
            rowData[3] = item.getCodAgravo();
            rowData[4] = item.getNmAgravo();
            
            if(item.getTotal() == 0){
                rowData[5] = "X";
            }else{
                 rowData[5] = "";
            }
            
            rowData[6] = item.getQtdOportuno().toString();
            
            rowData[7] = item.getTotal().toString();
            
            if(item.getQtdOportuno() > 0 && item.getTotal() > 0){
                oportuno = new Double(item.getQtdOportuno()).doubleValue();
                total = new Double(item.getTotal()).doubleValue();
                
                rowData[8] = String.valueOf(SinanUtil.converterDoubleUmaCasaDecimal((oportuno/total)*100));
                SinanUtil.imprimirConsole("Resultado sem converter"+(oportuno/total)*100);
            }
                /*
            if(item.getQtdOportuno() > 0)
                rowData[6] = String.valueOf(SinanUtil.converterDoubleUmaCasaDecimal(new Double((item.getQtdOportuno()/ item.getTotal())*100).doubleValue())) ;
            else
                rowData[6] = 0;
             * 
             */
            writer.addRecord(rowData); 
        }
        SinanUtil.setNomeArquivoDBF();
        SinanUtil.gerarDBF(writer);
   }

   @Deprecated
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
   @Deprecated
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