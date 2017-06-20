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
import com.org.dao.TesteDAO;
import com.org.negocio.Util;
import com.org.util.SinanUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.JTextArea;


/**
 *
 * @author taidson.santos
 */
public class CompletitudeService {
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
    
    @Deprecated
    TesteDAO testeDAO = new TesteDAO();
    @Deprecated
    int qtdeRegistros = 0;
    
    @Deprecated
    public int getTotalRegistros(String diretorio, String banco) throws SQLException{
        if(qtdeRegistros > 0){
            return qtdeRegistros;
        }else{
 //           qtdeRegistros = testeDAO.totalRegistros(diretorio, banco);
        }
       return qtdeRegistros;
    }

    public boolean campoSistema(String campo){
        if(campo.equals("NDUPLIC_N") || campo.equals("DT_DIGITA") || campo.equals("DT_TRANSUS") || 
           campo.equals("DT_TRANSDM") || campo.equals("DT_TRANSSM") || campo.equals("DT_TRANSRM") || 
           campo.equals("DT_TRANSRS") || campo.equals("DT_TRANSSE") || campo.equals("NU_LOTE_V") || 
           campo.equals("NU_LOTE_H") || campo.equals("CS_FLXRET") || campo.equals("FLXRECEBI") || 
           campo.equals("IDENT_MICR") || campo.equals("MIGRADO_W")){
            return true;
        }
        return false;
    }
        
    public void listaCampos(DBFReader campos, JProgressBar jProgressBar, JCheckBox checkBox, List<Completitude> listaTabela, JCheckBox checkBox2) throws DBFException, NumberFormatException, java.text.ParseException{
        int totalRegistros = campos.getRecordCount();
        int totalCampos = campos.getFieldCount();
        dbfUtil.mapearPosicoes(campos); 
        int i=0;
        
        SinanUtil.initProgressBar(jProgressBar);
        
        DBFReader camposII = Util.retornaObjetoDbfCaminhoArquivo(arquivo, diretorio);
        dbfUtil.mapearPosicoes(camposII);

        Arquivo arquivo;
        
        Map<String, Double> mapa = new HashMap<String, Double>();
        while ((rowObjects = camposII.nextRecord()) != null) {

            for(int x=0; x < totalCampos; x++){
                arquivo = new Arquivo();
                arquivo.setNome(camposII.getField(x).getName());
                arquivo.setValor(dbfUtil.getString(rowObjects, arquivo.getNome()));
                agravo = this.getNomeAgravo(dbfUtil.getString(rowObjects, "ID_AGRAVO"));
               
                bean = new Completitude();
                id_agravo = dbfUtil.getString(rowObjects, "ID_AGRAVO");
                try {
                    ano = SinanUtil.dateToString(dbfUtil.getDate(rowObjects, "DT_NOTIFIC"),"yyyy");
                } catch (Exception ex) {
                    Logger.getLogger(CompletitudeService.class.getName()).log(Level.SEVERE, null, ex);
                }
                bean.setAgravo(agravo);
                bean.setNome(camposII.getField(x).getName());
                
                
                if(mapa.containsKey(bean.getNome())){
                    Double aux = mapa.get(bean.getNome());
                    if(arquivo.getValor() != null){
                        mapa.put(bean.getNome(), aux+1.0);
                    }
                    
                }else{
                    if(arquivo.getValor() != null){
                        mapa.put(bean.getNome(), 1.0);
                    }else{
                        mapa.put(bean.getNome(), 0.0);
                    }
                }
                
                if(arquivo.getValor() != null && arquivo.getValor().equals("9")){
                    if(mapa.containsKey(bean.getNome()+"9")){
                        Double aux9 = mapa.get(bean.getNome()+"9");
                        mapa.put(bean.getNome()+"9", aux9+1.0);
                    }else{
                        mapa.put(bean.getNome()+"9", 1.0);
                    } 
                }
            
                if(arquivo.getValor() != null && arquivo.getValor().equals("99")){
                    if(mapa.containsKey(bean.getNome()+"99")){
                        Double aux99 = mapa.get(bean.getNome()+"99");
                        mapa.put(bean.getNome()+"99", aux99+1+0);
                    }else{
                        mapa.put(bean.getNome()+"99", 1.0);
                    } 
                }
                
            }
            SinanUtil.refreshProgressBar(this.arquivo, jProgressBar, i, totalRegistros);
            i++;
        }
        
//        this.setArquivo(this.arquivo);
        
        Double ultimo = new Double(0.0);
        Double ultimo9 = new Double(0.0);
        Double ultimo99 = new Double(0.0);
        Double aux999 = new Double(0.0);
        
        for(int l=0; l < totalCampos; l++){
            bean = new Completitude();
            
            ultimo = mapa.get(campos.getField(l).getName());
            ultimo9 = mapa.get(campos.getField(l).getName()+"9");
            ultimo99 = mapa.get(campos.getField(l).getName()+"99");
            
            bean.setAgravo(agravo);
            bean.setNome(campos.getField(l).getName());
            if(bean.getNome().equals("ID_DISTRIT")){
                SinanUtil.imprimirConsole(bean.getNome());
            }
            if(ultimo == null){
                ultimo = new Double(0.0);
            }
            bean.setResultado(SinanUtil.converterDoubleUmaCasaDecimal((ultimo/totalRegistros)*100));
            if(ultimo99 == null){
                ultimo99 = new Double(0.0);
            }
            if(ultimo9 == null){
                ultimo9 = new Double(0.0);
            }          
            
            if(ultimo99 > 0){
               aux999 =  ultimo - ultimo99;
            }else{
               aux999 =  ultimo - ultimo9; 
            }
            bean.setResultado_9_99(SinanUtil.converterDoubleUmaCasaDecimal((aux999/totalRegistros)*100));
            
            bean.setFaixa(getFaixa(bean.getResultado()));
            bean.setFaixa_9_9(getFaixa(bean.getResultado_9_99()));
            bean.setAno(ano);
            if(bean.getResultado()!=null && !campoSistema(bean.getNome()) && possuiCampo(id_agravo, bean.getNome(), checkBox)){
/*                teste.append(bean.getAgravo()+";"+
                bean.getNome()+";" +
                bean.getResultado()+";" +
                bean.getFaixa()+";" +
                bean.getResultado_9_99()+";" +
                bean.getFaixa_9_9()+"\n");
                */
                if(checkBox2.isSelected())
                    this.setArquivo(bean.getAgravo()+";"+
                    bean.getAno()+";"+
                    bean.getNome()+";" +
                    bean.getResultado()+";" +
                    bean.getFaixa()+";" +
                    bean.getResultado_9_99()+";" +
                    bean.getFaixa_9_9());
//                teste.repaint();   
//                teste.update(teste.getGraphics());
                listaTabela.add(bean);
            }
            
        }
//        this.setArquivo("-------------------------------------------------------------");
//        this.setArquivo("");
    }
    
    public void executar(Map<String, String> parametros, List<String> arquivos, JProgressBar jProgressBar, JCheckBox checkBox, JTable jTable, JCheckBox checkBox2) throws SQLException, DBFException, java.text.ParseException{
        List<Completitude> listaTabela = new ArrayList<Completitude>();
        for (String item : arquivos) {
            DBFReader campos = Util.retornaObjetoDbfCaminhoArquivo(item, parametros.get("diretorio"));
            this.arquivo = item;
            this.diretorio = parametros.get("diretorio");
            this.parametros = parametros;
       
            listaCampos(campos, jProgressBar, checkBox, listaTabela, checkBox2);           
        }
        SinanUtil.gerarTabela(listaTabela, jTable);
    }

    public int getFaixa(Double valor){
        int faixa = 0;
        
        if(valor <= 25.0){
            faixa = 1;
        }else if(valor >= 25.1 && valor <= 50.0){
            faixa = 2;
        }else if(valor >= 50.1 && valor <= 75.0){
            faixa = 3;
        }else{
            faixa = 4;
        }
        
        return faixa;
    }
    
    public String getNomeAgravo(String id_agravo){
        if(id_agravo.equals("X29")) return "Acidente por Animais Peçonhentos";
        if(id_agravo.equals("Z209")) return "Acidente de Trabalho com Exposição a Material Biológico";
        if(id_agravo.equals("Y96")) return "Acidente de Trabalho Grave";
        if(id_agravo.equals("C80")) return "Cancer Relacionado ao Trabalho";
        if(id_agravo.equals("L989")) return "Dermatoses Ocupacionais";
        if(id_agravo.equals("J11")) return "Influênza Humana por Novo Subtipo (Pandêmico)";
        if(id_agravo.equals("Z579")) return "LER DORT";
        if(id_agravo.equals("F99")) return "Transtorno Mental";
        if(id_agravo.equals("H833")) return "PAIR";
        if(id_agravo.equals("J64")) return "Pneumoconiose";
        if(id_agravo.equals("B24")) return "AIDS";
        if(id_agravo.equals("W64")) return "Atendimento Anti-Rábico Humano";
        if(id_agravo.equals("A051")) return "Botulismo";
        if(id_agravo.equals("A229")) return "Carbúnculo ou Antraz";
        if(id_agravo.equals("A009")) return "Cólera";
        if(id_agravo.equals("A379")) return "Coqueluche";
        if(id_agravo.equals("A90")) return "Dengue";
        if(id_agravo.equals("A369")) return "Difteria";
        if(id_agravo.equals("B571")) return "Doenças de Chagas Aguda";
        if(id_agravo.equals("A810")) return "Doença de Creutzfeldt-Jacob";
        if(id_agravo.equals("B09")) return "Doenças Exantemáticas";
        if(id_agravo.equals("EPI")) return "Epizootia";
        if(id_agravo.equals("B659")) return "Esquistossomose";
        if(id_agravo.equals("Y59")) return "Eventos Adversos Pós-vacina";
        if(id_agravo.equals("A959")) return "Febre Amarela";
        if(id_agravo.equals("A923")) return "Febre do Nilo";
        if(id_agravo.equals("A779")) return "Febre Maculosa";
        if(id_agravo.equals("A010")) return "Febre Tifóide";
        if(id_agravo.equals("Z21")) return "Gestantes HIV +";
        if(id_agravo.equals("A309")) return "Hanseníase";
        if(id_agravo.equals("A988")) return "Hantaviroses";
        if(id_agravo.equals("B19")) return "Hepatites Virais";
        if(id_agravo.equals("T659")) return "Intoxicações Exógenas";
        if(id_agravo.equals("B551")) return "Leishmaniose Tegumentar Americana";
        if(id_agravo.equals("B550")) return "Leishmaniose Visceral";
        if(id_agravo.equals("A279")) return "Leptospirose";
        if(id_agravo.equals("B54")) return "Malária";
        if(id_agravo.equals("G039")) return "Meningite";
        if(id_agravo.equals("A809")) return "Paralisia Flácida Aguda/Poliomielite";
        if(id_agravo.equals("A209")) return "Peste";
        if(id_agravo.equals("A829")) return "Raiva Humana";
        if(id_agravo.equals("A080")) return "Rotavirus";
        if(id_agravo.equals("A539")) return "Sífilis Adquirida";
        if(id_agravo.equals("A509")) return "Sífilis Congênita";
        if(id_agravo.equals("O981")) return "Sífilis em Gestante";
        if(id_agravo.equals("D699")) return "Síndrome da Febre hemorrágica Aguda";
        if(id_agravo.equals("N199")) return "Síndrome da Insuficiência Renal Aguda";
        if(id_agravo.equals("P350")) return "Síndrome da Rubéola Congênita";
        if(id_agravo.equals("R36")) return "Síndrome do Corrimento Uretral Masculino";
        if(id_agravo.equals("A08")) return "Síndrome Diarréica Aguda";
        if(id_agravo.equals("R17")) return "Síndrome Ictérica Aguda";
        if(id_agravo.equals("G043")) return "Síndrome Neurológica Aguda";
        if(id_agravo.equals("J07")) return "Síndrome Respiratória Aguda";
        if(id_agravo.equals("R699")) return "Outras Síndromes";
        if(id_agravo.equals("A35")) return "Tétano Acidental";
        if(id_agravo.equals("A33")) return "Tétano Neonatal";
        if(id_agravo.equals("A719")) return "Tracoma";
        if(id_agravo.equals("A169")) return "Tuberculose";
        if(id_agravo.equals("A219")) return "Tularemia";
        if(id_agravo.equals("B03")) return "Varíola";
        if(id_agravo.equals("B019")) return "Varicela";
        if(id_agravo.equals("Y09")) return "Violência doméstica, sexual e/ou outras violências";
        return id_agravo;
    }
    
    public Boolean possuiCampo(String id_agravo, String campo, JCheckBox checkBox){
        if(!checkBox.isSelected()) return true;
        if(id_agravo.equals("X29")) return SinanUtil.verificaCampos("acidente_animais_peconhentos.txt", campo);
        if(id_agravo.equals("Z209")) return SinanUtil.verificaCampos("acidente_trabalho_biologico.txt", campo);
        if(id_agravo.equals("Y96")) return SinanUtil.verificaCampos("acidente_trabalho_grave.txt", campo);
        if(id_agravo.equals("C80")) return SinanUtil.verificaCampos("cancer_relacionado_trabalho.txt", campo);
        if(id_agravo.equals("L989")) return SinanUtil.verificaCampos("dermatoses_ocupacionais.txt", campo);
        if(id_agravo.equals("J11")) return SinanUtil.verificaCampos("influenza.txt", campo);
        if(id_agravo.equals("Z579")) return SinanUtil.verificaCampos("ler_dort.txt", campo);
        if(id_agravo.equals("F99")) return SinanUtil.verificaCampos("transtornos_mentais_relacionados_trabalho.txt", campo);
        if(id_agravo.equals("H833")) return SinanUtil.verificaCampos("pair.txt", campo);
        if(id_agravo.equals("J64")) return SinanUtil.verificaCampos("pneumoconioses.txt", campo);
        if(id_agravo.equals("B24")) return SinanUtil.verificaCampos("aids.txt", campo);
        if(id_agravo.equals("W64")) return SinanUtil.verificaCampos("atendimento_anti_rabico.txt", campo);
        if(id_agravo.equals("A051")) return SinanUtil.verificaCampos("botulismo.txt", campo);
        if(id_agravo.equals("A229")) return true;//"Carbúnculo ou Antraz"
        if(id_agravo.equals("A009")) return SinanUtil.verificaCampos("colera.txt", campo);
        if(id_agravo.equals("A379")) return SinanUtil.verificaCampos("coqueluche.txt", campo);
        if(id_agravo.equals("A90")) return SinanUtil.verificaCampos("dengue.txt", campo);
        if(id_agravo.equals("A369")) return SinanUtil.verificaCampos("difiteria.txt", campo);
        if(id_agravo.equals("B571")) return SinanUtil.verificaCampos("chagas.txt", campo);
        if(id_agravo.equals("A810")) return true;//"Doença de Creutzfeldt-Jacob";
        if(id_agravo.equals("EPI")) return SinanUtil.verificaCampos("epizootia.txt", campo);
        if(id_agravo.equals("B09")) return SinanUtil.verificaCampos("exantematica.txt", campo);
        if(id_agravo.equals("B659")) return SinanUtil.verificaCampos("esquistossomose.txt", campo);
        if(id_agravo.equals("Y59")) return true;//"Eventos Adversos Pós-vacina";
        if(id_agravo.equals("A959")) return SinanUtil.verificaCampos("febre_amarela.txt", campo);
        if(id_agravo.equals("A923")) return SinanUtil.verificaCampos("febre_nilo.txt", campo);
        if(id_agravo.equals("A779")) return SinanUtil.verificaCampos("febre_maculosa.txt", campo);
        if(id_agravo.equals("A010")) return SinanUtil.verificaCampos("febre_tifoide.txt", campo);
        if(id_agravo.equals("Z21")) return SinanUtil.verificaCampos("gestante_hiv.txt", campo);
        if(id_agravo.equals("A309")) return SinanUtil.verificaCampos("hanseniase.txt", campo);
        if(id_agravo.equals("A988")) return SinanUtil.verificaCampos("hantavirose.txt", campo);
        if(id_agravo.equals("B19")) return SinanUtil.verificaCampos("hepatites_virais.txt", campo);
        if(id_agravo.equals("T659")) return SinanUtil.verificaCampos("intoxicacao_exogena.txt", campo);
        if(id_agravo.equals("B551")) return SinanUtil.verificaCampos("leishimaniose_tegumentar_americana.txt", campo);
        if(id_agravo.equals("B550")) return SinanUtil.verificaCampos("leishimaniose_visceral.txt", campo);
        if(id_agravo.equals("A279")) return SinanUtil.verificaCampos("leptospirose.txt", campo);
        if(id_agravo.equals("B54")) return SinanUtil.verificaCampos("malaria.txt", campo);
        if(id_agravo.equals("G039")) return SinanUtil.verificaCampos("meningite.txt", campo);
        if(id_agravo.equals("A809")) return SinanUtil.verificaCampos("paralisia_flacida_aguda.txt", campo);
        if(id_agravo.equals("A209")) return SinanUtil.verificaCampos("peste.txt", campo);
        if(id_agravo.equals("A829")) return SinanUtil.verificaCampos("raiva_humana.txt", campo);
        if(id_agravo.equals("A080")) return SinanUtil.verificaCampos("rotavirus.txt", campo);
        if(id_agravo.equals("A539")) return true;//"Sífilis Adquirida";
        if(id_agravo.equals("A509")) return SinanUtil.verificaCampos("sifilis_congenita.txt", campo);
        if(id_agravo.equals("O981")) return SinanUtil.verificaCampos("sifilis_gestante.txt", campo);
        if(id_agravo.equals("D699")) return true;//"Síndrome da Febre hemorrágica Aguda";
        if(id_agravo.equals("N199")) return true;//"Síndrome da Insuficiência Renal Aguda";
        if(id_agravo.equals("P350")) return SinanUtil.verificaCampos("sindrome_rubeola_congenita.txt", campo);
        if(id_agravo.equals("R36")) return true;//"Síndrome do Corrimento Uretral Masculino";
        if(id_agravo.equals("A08")) return true;//"Síndrome Diarréica Aguda";
        if(id_agravo.equals("R17")) return true;//"Síndrome Ictérica Aguda";
        if(id_agravo.equals("G043")) return true;//"Síndrome Neurológica Aguda";
        if(id_agravo.equals("J07")) return true;//"Síndrome Respiratória Aguda";
        if(id_agravo.equals("R699")) return true; //"Outras Síndromes";
        if(id_agravo.equals("A35")) return SinanUtil.verificaCampos("tetano_acidental.txt", campo);
        if(id_agravo.equals("A33")) return SinanUtil.verificaCampos("tetano_neonatal.txt", campo);
        if(id_agravo.equals("A719")) return SinanUtil.verificaCampos("tracoma.txt", campo);
        if(id_agravo.equals("A169")) return SinanUtil.verificaCampos("tuberculose.txt", campo);
        if(id_agravo.equals("A219")) return true;//"Tularemia";
        if(id_agravo.equals("B03")) return true;//"Varíola";
        if(id_agravo.equals("B019")) return true;//"Varicela";
        if(id_agravo.equals("Y09")) return SinanUtil.verificaCampos("violencia.txt", campo);
        return true;
    }
    
    
   public void setArquivo(String conteudo){
       try {  
           // Gravando no arquivo  
           FileWriter arquivo;  

//           arquivo = new FileWriter("arquivo.csv", true);  
           arquivo = new FileWriter(parametros.get("diretorioSalvar") + parametros.get("arquivoSalvar")+".csv", true);  
           
           arquivo.write(conteudo+"\r\n");
          // arquivo.append(conteudo+"\r\n");
           arquivo.close();  

           // Lendo do arquivo  
           /*
           arquivo = new File("arquivo.txt");  
           FileInputStream fis = new FileInputStream(arquivo);  

           int ln;  
           while ( (ln = fis.read()) != -1 ) {  
              System.out.print( (char)ln );  
           }  

           fis.close();  */
        }  
        catch (Exception ee) {  
           ee.printStackTrace();  
        }  
   }
   
}
