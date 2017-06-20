/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.org.service;

import com.linuxense.javadbf.DBFException;
import com.linuxense.javadbf.DBFReader;
import com.lowagie.text.pdf.codec.postscript.ParseException;
import com.org.bd.DBFUtil;
import com.org.beans.Arquivo;
import com.org.beans.Completitude;
import com.org.beans.Duplicidade;
import com.org.dao.ConectionODBC;
import com.org.dao.ConexaoHSQL;
import com.org.dao.ConexaoODBC;
import com.org.dao.DuplicidadeDAO;
import com.org.dao.TesteDAO;
import com.org.negocio.Util;
import com.org.util.SinanDateUtil;
import com.org.util.SinanUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.spi.DirStateFactory.Result;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.JTextArea;


/**
 *
 * @author taidson.santos
 */
public class DuplicidadeService {
    Completitude bean;
    
    String diretorio = "";
    String arquivo;
    Completitude testeResultado;
    
    String agravo = "";
    String id_agravo;
    String ano;
    DBFUtil dbfUtil = new DBFUtil();
    Object[] rowObjects;
    Map<String, String> parametros;
    
    Map<String, Duplicidade> mapaDuplicidade = new HashMap<String, Duplicidade>();
    List<Duplicidade> itensDuplicidade = new ArrayList<Duplicidade>();
    
    Map<String, List<String>> mapaUFMunicipio;// = new HashMap<String, List<String>>();
    Map<String, Duplicidade> mapaGeral = new HashMap<String, Duplicidade>();
    Map<String, Integer> mapaMunicipio = new HashMap<String, Integer>();
    
    
     public void executar(Map<String, String> parametros, List<String> arquivos, JProgressBar jProgressBar) throws SQLException, DBFException, java.text.ParseException{
        List<Duplicidade> listaDuplicidade;
         
        int count = 0;
        DuplicidadeDAO duplicidadeDAO = new DuplicidadeDAO();
        
        int totalRegistros = duplicidadeDAO.getCount(arquivos.get(0));
        
        Duplicidade bean;
        
        ResultSet rs = duplicidadeDAO.findAllDuplicidades(arquivos.get(0), parametros.get("dataInicio"), parametros.get("dataFim"));
        
        List<String> listaMunicipios;
        Connection con2 = null;
        
        while(rs.next()){
            con2 = ConexaoODBC.getConnection2();
            
            listaDuplicidade =  new ArrayList<Duplicidade>();
            
            ResultSet rs2 = duplicidadeDAO.findDuplicidade(con2, rs.getString("FONETICA_N"), rs.getString("NU_IDADE_N"), rs.getString("CS_SEXO"), arquivos.get(0));
            
    //        listaDuplicidade = this.findDuplicidade(parametros.get("diretorio"), arquivos.get(0), rs.getString("FONETICA_N"), rs.getString("NU_IDADE_N"), rs.getString("CS_SEXO"), rs.getInt("VALOR"));
            
            
            mapaUFMunicipio = new HashMap<String, List<String>>();
            
            
            
            String uf;
            String municipio;
            while(rs2.next()){
                listaMunicipios = new ArrayList<String>();
                uf = rs2.getString("SG_UF_NOT");
                municipio = rs2.getString("ID_MUNICIP");
                if(mapaUFMunicipio.containsKey(uf)){
                    listaMunicipios = mapaUFMunicipio.get(uf);
                }
                listaMunicipios.add(municipio);
                mapaUFMunicipio.put(uf, listaMunicipios);
                
                
            }
            ConexaoODBC teste = new ConexaoODBC(null);
            teste.close(null);
            
//            for (Duplicidade item : listaDuplicidade) {
//                listaMunicipios = new ArrayList<String>();
//                uf = item.getUf();
//                municipio = item.getMunicipio();
//                if(mapaUFMunicipio.containsKey(uf)){
//                    listaMunicipios = mapaUFMunicipio.get(uf);
//                }
//                listaMunicipios.add(municipio);
//                mapaUFMunicipio.put(uf, listaMunicipios);
//            }
            
            //identificar quais ufs estão no mapa e setar +1 ao campo UF DIFERENTE
            Set entries = mapaUFMunicipio.entrySet();
            Iterator it = entries.iterator();
            while (it.hasNext()) {
                Map.Entry item = (Map.Entry) it.next();
                bean = new Duplicidade();
                if(mapaGeral.containsKey(item.getKey())){
                    bean = mapaGeral.get(item.getKey());
                }
                bean.setUfDiferente(bean.getUfDiferente()+1);
                mapaGeral.put(item.getKey().toString(), bean);
            }

            //resolver agora os municípios --> punk
            
            
            Iterator entries2 = mapaUFMunicipio.entrySet().iterator();
            List<String> lista;
            
            while (entries2.hasNext()) {
             ///   Map.Entry i = (Map.Entry) entries2.next();
                boolean municIgual = true;
                mapaMunicipio = new HashMap<String, Integer>();
                lista = new ArrayList<String>();
                Entry thisEntry3 = (Entry) entries2.next();
                lista = (List<String>) thisEntry3.getValue();
                int countMunic = 0;
                for (String item : lista) {
                    if(mapaMunicipio.containsKey(item)){
                        countMunic = mapaMunicipio.get(item) + 1;
                    }else{
                        countMunic++;
                    }
                    mapaMunicipio.put(item, countMunic);
                    countMunic = 0;
                }
                SinanUtil.imprimirConsole(thisEntry3.getKey().toString());
                Set entries4 = mapaMunicipio.entrySet();
                Iterator it2 = entries4.iterator();
                while (it2.hasNext()) {
                    Map.Entry item2 = (Map.Entry) it2.next();
                    bean = new Duplicidade();
                    if(mapaGeral.containsKey(thisEntry3.getKey())){
                        bean = mapaGeral.get(thisEntry3.getKey());
                    }
                    if(mapaMunicipio.size() > 1 || mapaMunicipio.get(item2.getKey().toString()) > 1){
                        if(mapaMunicipio.get(item2.getKey().toString()) == 1 && municIgual){
                            bean.setMunicipioDiferente(bean.getMunicipioDiferente()+mapaMunicipio.size());
                            municIgual = false; 
                        }else if(municIgual){
                            bean.setMunicipioIgual(bean.getMunicipioIgual() + mapaMunicipio.get(item2.getKey().toString()));//confirmar aqui
                        }
                        mapaGeral.put(thisEntry3.getKey().toString(), bean);
                    }
                    
                }
                
                    
            
            
            
            }
            
            
         //   this.matrizDuplicidade(mapaDuplicidade, itensDuplicidade);
            
                SinanUtil.refreshProgressBarSimple(jProgressBar, count, totalRegistros);
                count++;
         }
                
        SinanUtil.mensagem("Acabou");
     }
    
     public List<Duplicidade> findDuplicidade(String diretorio, String banco, String fonetica_n,  String nu_idade_n, String cs_sexo, int valor) throws DBFException{
         
         String[] bd = banco.split(".DBF");
         
         Duplicidade duplicidade;
         List<Duplicidade> listaDuplicidade = new ArrayList<Duplicidade>();
         
         DBFReader campos = Util.retornaObjetoDbfCaminhoArquivo(bd[0], diretorio);
         dbfUtil.mapearPosicoes(campos);
            
         while ((rowObjects = campos.nextRecord()) != null) {
             duplicidade = new Duplicidade();
             
             String fonetica_n_dbf = dbfUtil.getString(rowObjects, "FONETICA_N");
             int nu_idade_n_dbf =  dbfUtil.getInt(rowObjects, "NU_IDADE_N");
             String cs_sexo_dbf = dbfUtil.getString(rowObjects, "CS_SEXO");
             String idadeString;
             if(nu_idade_n != null){
                 idadeString = String.valueOf(nu_idade_n_dbf);
                 
                 if((fonetica_n_dbf != null && fonetica_n.equals(fonetica_n_dbf)) &&
                     (nu_idade_n.equals(idadeString)) &&
                     cs_sexo_dbf != null && cs_sexo.equals(cs_sexo_dbf)){
                
                    duplicidade.setUf(dbfUtil.getString(rowObjects, "SG_UF_NOT"));
                    duplicidade.setMunicipio(dbfUtil.getString(rowObjects, "ID_MUNICIP"));

                    listaDuplicidade.add(duplicidade);
                    if(listaDuplicidade.size() == valor){
                        break;
                    }
                }
                 
             }
             
             
             
         }
         
         return listaDuplicidade;
     }
 
     
     
     
    public void mapearResultado(ResultSet rs) throws SQLException{
        
        //uf e sua lista de municípios
        Map<String, List<String>> mapa = new HashMap<String, List<String>>();
        
        Map<String, Integer> mapaUF = new HashMap<String, Integer>();
        
        Map<String, Integer> mapaMunicipio = new HashMap<String, Integer>();
        
        while(rs.next()){
            List<String> listaMunicTemp = new ArrayList<String>();
            if(mapa.containsKey(rs.getString("SG_UF_NOT"))){
                listaMunicTemp = mapa.get(rs.getString("SG_UF_NOT"));
            }
            listaMunicTemp.add(rs.getString("ID_MUNICIP"));
            mapa.put(rs.getString("SG_UF_NOT"), listaMunicTemp);
        }
        
        
    }
    
            
    public void executar2(Map<String, String> parametros, List<String> arquivos, JProgressBar jProgressBar) throws SQLException, DBFException, java.text.ParseException{
        
        TesteDAO testeDAO = new TesteDAO();
        
       // int tot = testeDAO.totalRegistros(parametros.get("diretorio"), arquivos);
        
        int totalRegistros = this.contarRegistros(parametros.get("diretorio"), arquivos);
        int count = 0;
        
        Date dataNotificacao;
        String numNotificacao;
        String camposChave;
        Integer idade;
        String sexo;
        String nomeSobrenome;
        String nomeSobrenomeIdadeSexo;
        String ufNotificacao;
        String municipioNotificacao;
        String identificaDuplicidade;
        String agravo;
        Duplicidade bean;
        List<Duplicidade> lista;
        List<String> blackList = new ArrayList<String>();
        Map<String, List<Duplicidade>> mapa = new HashMap<String, List<Duplicidade>>();
        int totalCampos;
        DuplicidadeDAO duplicidadeDAO = new DuplicidadeDAO();
        
        for (String item : arquivos) {
            DBFReader campos = Util.retornaObjetoDbfCaminhoArquivo(item, parametros.get("diretorio"));
            
            dbfUtil.mapearPosicoes(campos);
            
            while ((rowObjects = campos.nextRecord()) != null) {
//                try {
//                    identificaDuplicidade = dbfUtil.getString(rowObjects, "NDUPLIC_N");
//                    SinanUtil.imprimirConsole(dbfUtil.getString(rowObjects, "NU_NOTIFIC"));
//                   // SinanUtil.imprimirConsole(item + " OK");
//                } catch (Exception e) {
//                    SinanUtil.imprimirConsole(item);
//                }
               
                numNotificacao = dbfUtil.getString(rowObjects, "NU_NOTIFIC");
                dataNotificacao = dbfUtil.getDate(rowObjects, "DT_NOTIFIC");
                idade = dbfUtil.getInt(rowObjects, "NU_IDADE_N");
                sexo = dbfUtil.getString(rowObjects, "CS_SEXO");
                //nomeSobrenome = SinanUtil.extrairNomeSobrenome(dbfUtil.getString(rowObjects, "NM_PACIENT"));
                nomeSobrenome = dbfUtil.getString(rowObjects, "FONETICA_N");
                agravo = dbfUtil.getString(rowObjects, "ID_AGRAVO");
                if(!agravo.equals("Y09")){
                    identificaDuplicidade = dbfUtil.getString(rowObjects, "NDUPLIC_N");
                }else{
                    identificaDuplicidade = dbfUtil.getString(rowObjects, "NDUPLIC");
                }
                
                ufNotificacao = dbfUtil.getString(rowObjects, "SG_UF_NOT");
                municipioNotificacao = dbfUtil.getString(rowObjects, "ID_MUNICIP");
               
                camposChave = numNotificacao + dataNotificacao.toString() + agravo + municipioNotificacao;
                
                if(SinanDateUtil.isBetweenDates(dataNotificacao, parametros.get("dataInicio"), parametros.get("dataFim")) 
                        && (identificaDuplicidade == null || identificaDuplicidade.equals("0"))){
                    if(nomeSobrenome != null && idade != null && sexo != null){
                        if(numNotificacao.equals("5165595"))
                            SinanUtil.imprimirConsole("XXXXX");
                        
                        bean = new Duplicidade();
  //                      bean.setUF(ufNotificacao);
                        bean.setMunicipio(municipioNotificacao);
   //                     bean.setNome(nomeSobrenome);
 //                       bean.setIdade(idade.toString());
  //                      bean.setSexo(sexo);
                        duplicidadeDAO.insert(bean);
                        
                        SinanUtil.imprimirConsole(numNotificacao);
                        
                        
                 //       nomeSobrenomeIdadeSexo = nomeSobrenome + idade + sexo;
                //        blackList.add(camposChave);
//                        lista = new ArrayList<Duplicidade>();
//                        if(mapa.containsKey(nomeSobrenome)){
//                            lista = mapa.get(nomeSobrenome);
//                        }
//                        bean.setNomeSobrenomeIdadeSexo(nomeSobrenome);
//                        bean.setUF(ufNotificacao);
//                        bean.setMunicipio(municipioNotificacao);
//                        lista.add(bean);
//                        mapa.put(nomeSobrenome, lista);
                       // this.verificaCorrespondencia(arquivos, parametros, nomeSobrenomeIdadeSexo, blackList, mapa, camposChave);
                    }
                }
                SinanUtil.refreshProgressBar(item, jProgressBar, count, totalRegistros);
                count++;
            }
                
       }
     //  SinanUtil.imprimirConsole("teste");             
    }

    @Deprecated
    public void verificaCorrespondencia(List<String> arquivos, Map<String, String> parametros, String nomeSobrenomeIdadeSexo, List<String> blackList, Map<String, List<Duplicidade>> mapa, String camposChave) throws DBFException, java.text.ParseException{
//        DBFReader campos2 = Util.retornaObjetoDbfCaminhoArquivo(item, parametros.get("diretorio"));
        Object[] rowObjects;
        Integer idade;
        String sexo;
        String nomeSobrenome;
        String numNotificacao;
        Date dataNotificacao;
        String campChave;
        String agravo;
        String municipioNotificacao;
        String nomSobrIdadSexo;
        String identificaDuplicidade;
        Duplicidade bean;
        List<Duplicidade> lista;
        String ufNotificacao;
        int count=0;
        List<String> blackList2 = new ArrayList<String>();
        String diretorio = parametros.get("diretorio");
        String dataInicio = parametros.get("dataInicio");
        String dataFim = parametros.get("dataFim");
               
        for (String item : arquivos) {
            
            DBFReader campos = Util.retornaObjetoDbfCaminhoArquivo(item, diretorio);

            dbfUtil.mapearPosicoes(campos);

             while ((rowObjects = campos.nextRecord()) != null) {
                numNotificacao = dbfUtil.getString(rowObjects, "NU_NOTIFIC");
                dataNotificacao = dbfUtil.getDate(rowObjects, "DT_NOTIFIC");
                idade = dbfUtil.getInt(rowObjects, "NU_IDADE_N");
                sexo = dbfUtil.getString(rowObjects, "CS_SEXO");
                agravo = dbfUtil.getString(rowObjects, "ID_AGRAVO");
                municipioNotificacao = dbfUtil.getString(rowObjects, "ID_MUNICIP");
                ufNotificacao = dbfUtil.getString(rowObjects, "SG_UF_NOT");
                nomeSobrenome = SinanUtil.extrairNomeSobrenome(dbfUtil.getString(rowObjects, "NM_PACIENT"));
                campChave = numNotificacao + dataNotificacao.toString() + agravo + municipioNotificacao;

                if(!agravo.equals("Y09")){
                    identificaDuplicidade = dbfUtil.getString(rowObjects, "NDUPLIC_N");
                }else{
                    identificaDuplicidade = dbfUtil.getString(rowObjects, "NDUPLIC");
                }

                 if(SinanDateUtil.isBetweenDates(dataNotificacao, dataInicio, dataFim) 
                            && (identificaDuplicidade == null || identificaDuplicidade.equals("0"))){

                     if(nomeSobrenome != null && idade != null && sexo != null){
                         nomSobrIdadSexo = nomeSobrenome + idade + sexo;
                         for (String blk : blackList) {
                             //incluir o item a ser verificado na blacklist e remover caso não encontre correspondência
                             if(nomeSobrenomeIdadeSexo.equals(nomSobrIdadSexo) && !campChave.equals(blk)){
                                lista = new ArrayList<Duplicidade>();
                                bean = new Duplicidade();
                                if(mapa.containsKey(nomeSobrenomeIdadeSexo)){
                                    lista = mapa.get(nomeSobrenomeIdadeSexo);
                                }
//                                bean.setNomeSobrenomeIdadeSexo(nomSobrIdadSexo);
//                                bean.setUF(ufNotificacao);
                                bean.setMunicipio(municipioNotificacao);
                                lista.add(bean);
                                mapa.put(nomeSobrenomeIdadeSexo, lista);
                                blackList2.add(campChave);
                                count++;
                             }
                        }
                        //verifica se o registro verificado já consta na lista negra caso contrário ele adiciona
                         //para não verificar contar com esse registro novamente
                        if(count > 0){
                            blackList.addAll(blackList2);
                        }else if (blackList != null && blackList.contains(campChave)) {
                            blackList.remove(camposChave);
                        }
                     }
                 }
             }
        }
    }
    // a blackList adiciona o registro a ser verificado, caso não tenha ocorrencia no restante dos registros este item deve ser
    // excluído da blackList por meio do camposChave.
    // a blackList2 foi criada para complementar a primeira, ou seja adicionar todas as correspondencias encontradas, quando houver.
    
    @Deprecated
    public Integer contarRegistros(String diretorio, List<String> arquivos) throws DBFException{
        int totalRegistros = 0;
        
        for (String item : arquivos) {
            DBFReader campos = Util.retornaObjetoDbfCaminhoArquivo(item, diretorio);
            totalRegistros += campos.getRecordCount();
        }
        return totalRegistros;
    }
    
    /*
    public void matrizDuplicidade(Map<String, Duplicidade> mapaDuplicidade, List<Duplicidade> itensDuplicidade){
        Map<String, Integer> mapaUf = new HashMap<String, Integer>();
        Map<String, List<String>> mapaUfMunicipio = new HashMap<String, List<String>>();
        Map<String, Integer> mapaMunicipio;
        List<String> listaMunicipios;
        Duplicidade bean;
        Integer municipioCount;
        
        //UF
        for (Duplicidade item : itensDuplicidade) {
            if(!mapaUf.containsKey(item.getUf())){
                mapaUf.put(item.getUf(), null);
            }
        }
        
        //UF
        if(mapaUf.size() > 1){
            for (Duplicidade item : itensDuplicidade) {
                bean = new Duplicidade();
                if(mapaDuplicidade.containsKey(item.getUf())){
                    bean = mapaDuplicidade.get(item.getUf());
                    bean.setUfDiferente(bean.getUfDiferente() + 1);
                }else{
                    bean.setUf(item.getUf());
                    bean.setUfDiferente(1);
                }
                mapaDuplicidade.put(item.getUf(), bean);
            }
        }
        
        
        for (Duplicidade item : itensDuplicidade) {
            listaMunicipios = new ArrayList<String>();
            
            if(mapaUfMunicipio.containsKey(item.getUf())){
                listaMunicipios = mapaUfMunicipio.get(item.getUf());
            }
            
            listaMunicipios.add(item.getMunicipio());
            mapaUfMunicipio.put(item.getUf(), listaMunicipios);
        }
        
        Iterator entries = mapaUfMunicipio.entrySet().iterator();
        List<String> lista;
        while (entries.hasNext()) {
            lista = new ArrayList<String>();
            Entry thisEntry = (Entry) entries.next();
            lista = (List<String>) thisEntry.getValue();
            municipioCount = 0;
            mapaMunicipio = new HashMap<String, Integer>();
            for (String item : lista) {
                if(mapaMunicipio.containsKey(item)){
                    municipioCount = mapaMunicipio.get(item);
                }
                mapaMunicipio.put(item, municipioCount + 1);
            }
            
            bean = new Duplicidade();
            bean = mapaDuplicidade.get(thisEntry.getKey().toString());
            bean.setMunicipioDiferente(bean.getMunicipioDiferente() + mapaMunicipio.size());
            mapaDuplicidade.put(thisEntry.getKey().toString(), bean);
            
            while (entries.hasNext()) {
                Entry thisEntry2 = (Entry) entries.next();
                if(mapaMunicipio.containsKey(thisEntry2.getKey()) && mapaMunicipio.get(thisEntry2.getKey().toString()) >= 2){
                    bean = new Duplicidade();
                    bean = mapaDuplicidade.get(thisEntry.getKey().toString());
                    bean.setMunicipioIgual(bean.getMunicipioIgual() + mapaMunicipio.get(thisEntry.getKey().toString()));
                    mapaDuplicidade.put(thisEntry.getKey().toString(), bean);
                }
            }
            
        }
    }
    
    */
}
