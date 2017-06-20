/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.org.util;

import com.linuxense.javadbf.DBFException;
import com.linuxense.javadbf.DBFReader;
import com.linuxense.javadbf.DBFWriter;
import com.org.bd.DBFUtil;
import com.org.beans.Completitude;
import com.org.facade.SessionFacadeImpl;
import com.org.model.classes.Municipio;
import com.org.model.classes.agravos.oportunidade.OportunidadeAgravoCOAP;
import com.org.model.classes.agravos.oportunidade.OportunidadeAgravoPQAVS;
import com.org.negocio.Configuracao;
import com.org.negocio.FiltroArquivo;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import org.apache.commons.beanutils.BeanComparator;

/**
 *
 * @author Taidson
 */
public class SinanUtil {
    
    /**
	 * Retorna a descrição do período formatada de acordo com o padrão, para
	 * ser colocado em relatórios.
	 * 
	 * @param dtInicio
	 * @param dtFim
	 * @return
	 */
	public  String getDescricaoPeriodo(Date dtInicio, Date dtFim) {
		String periodo = null;
		
		if (dtInicio != null && dtFim != null) {
			periodo = SinanDateUtil.toString(dtInicio) + " à " + SinanDateUtil.toString(dtFim);
		} else if (dtInicio != null) {
			periodo = "A partir de " + SinanDateUtil.toString(dtInicio);
		} else if (dtFim != null) {
			periodo = "Até " + SinanDateUtil.toString(dtFim);
		}
		
		return periodo;
	}
    
        
        /**
	 * Método para verificar se um lista está vazia.
	 * <pre>
	 * 	list == null returns true
	 * 	list.size() == 0 returns true
	 * 	list.size() > 0 returns false
	 * </pre>
	 * 
	 * @see #isListNotEmpty(Collection)
	 * @param list
	 * @return
	 */
	public static boolean isListEmpty(Collection<?> list){
		return !SinanUtil.isListNotEmpty(list);
	}
	
	/**
	 * Método para verificar se um lista <b>não</b> está vazia.
	 * <pre>
	 * 	list == null returns false
	 * 	list.size() > 0 returns true
	 * 	list.size() == 0 returns false
	 * </pre>
	 * 
	 * @param list
	 * @return
	 */
	public static boolean isListNotEmpty(Collection<?> list){
		return list != null && list.size() > 0;
	}


        public  Boolean isPrazoOportuno(Date dt_notificacao, Date dt_investigacao, String codAgravo){
            Long dias = SinanDateUtil.calculaDiferencaDias(dt_notificacao, dt_investigacao);
            if(dias <= this.prazoOportuno(codAgravo)){
                return true;
            }
            return false;
        }
        
        private Integer prazoOportuno(String codAgravo){
            if(codAgravo.equals("A051") || codAgravo.equals("A009") || codAgravo.equals("A379") || codAgravo.equals("A90") ||
                codAgravo.equals("A369") || codAgravo.equals("B571") || codAgravo.equals("A959") || codAgravo.equals("A923") ||
                codAgravo.equals("A779") || codAgravo.equals("A010") || codAgravo.equals("A988") || codAgravo.equals("B550") ||
                codAgravo.equals("A279") || codAgravo.equals("B54") || codAgravo.equals("G039") || codAgravo.equals("A809") ||
                codAgravo.equals("A209") || codAgravo.equals("A829") || codAgravo.equals("P350") || codAgravo.equals("A35") || 
                codAgravo.equals("A33")){
                return 60;
            }else if(codAgravo.equals("B551") || codAgravo.equals("B19") || codAgravo.equals("P350")){
                return 180;
            }else{
                this.mensagem("Erro ao calcular o prazo oportuno", "Código do agravo não encontrado.");
                return 0;
            }                    
        }
        
        /**
         * Imprime mensagem de alerta passando como parâmetro 
         * a descrição da mensagem e o conteúdo a ser exibido ao usuários.
         * @param description
         * @param message
         * @autor Taidson
         */
        public static void mensagem(String description, String message) {
            JOptionPane.showMessageDialog(new JFrame(), message, description, JOptionPane.INFORMATION_MESSAGE);
        }
        
        /**
         * Imprime mensagem de alerta passando como parâmetro 
         * o conteúdo a ser exibido ao usuários.
         * @param description
         * @param message
         * @autor Taidson
         */
        public static void mensagem(String message) {
            JOptionPane.showMessageDialog(new JFrame(), message);
        }
        
        /**
         * Método responsável por retornar a versão definida do aplicativo
         * @return 
         */
        public static String getVersaoSinanRelatorios(){
            return "4.8";
        }
        
        /**
         * Gera arquivo DBF
         * @param writer
         * @throws IOException 
         * @autor Taidson
         */
        public static void gerarDBF(DBFWriter writer) throws IOException {
            try {
                FileOutputStream fos = new FileOutputStream(Configuracao.getPropriedade("arquivo"));
                writer.write(fos);
                fos.close();
                System.out.println("ok");
            } catch (Exception DBFException) {
                System.out.print(DBFException);
            }
        }
        
        
        /**
         * Abre janela de opções para escolher o nome e o diretório
         * do arquivo DBF  a ser salvo.
         * @return 
         * @autor Taidson
         */
        private static File arquivoDbfSalvo;
        public static boolean setNomeArquivoDBF() {
            JFileChooser filesave = new JFileChooser();
            FiltroArquivo filtro = new FiltroArquivo();
            filtro.addExtension("dbf");
            filtro.setDescription("Arquivo DBF");
            filtro.addInicioNome(SessionFacadeImpl.getNomeDbf());
            filesave.setSelectedFile(new File("output.dbf"));
            filesave.setDialogTitle("Informe o local e nome do arquivo DBF a ser salvo");
            filesave.addChoosableFileFilter(filtro);
            Configuracao conf = new Configuracao();
            File file2 = new File(conf.getCaminho());
            filesave.setCurrentDirectory(file2);
            int ret = filesave.showSaveDialog(filesave);
            if (ret == JFileChooser.APPROVE_OPTION) {
                arquivoDbfSalvo = filesave.getSelectedFile();
                System.out.println(arquivoDbfSalvo.getName());
    //                conf.setCaminhoArquivo(file.toString());
                conf.setPropriedade("arquivo", arquivoDbfSalvo.getPath());
                System.out.println(arquivoDbfSalvo);
                return true;
            }
            return false;
    }
   

   /**
    * Imprimi, no consele, a String passada como parâmetro
    * @param conteudo 
    */     
   public static void imprimirConsole(String conteudo){
       System.out.println(conteudo);
   }
 
   /**
    * Retorna o intervalo entre as semanas informadas como parâmetro
    * @param anoSemanaDe
    * @param anoSemanaAte
    * @return 
    * @autor Taidson
    */ 
   public static Integer intervaloSemanas(String anoSemanaDe, String anoSemanaAte){
       
       Integer anoDe = Integer.parseInt(anoSemanaDe.substring(0,4));
       Integer semanaDe = Integer.parseInt(anoSemanaDe.substring(4,6));
       Integer anoAte = Integer.parseInt(anoSemanaAte.substring(0,4));
       Integer semanaAte = Integer.parseInt(anoSemanaAte.substring(4,6));
        
       if(anoDe.equals(anoAte)){
           return semanaAte - semanaDe;
       }else if((anoAte - anoDe) > 1){
           return (((anoAte - anoDe) - 1)*53) + semanaAte + (53 - semanaDe);
       }else{
           return 53 - semanaDe + semanaAte;
       }
   }
   
   /**
    * Formata um valor double em duas casas decimais.
    * @param valorDouble
    * @return 
    * @autor Taidson
    */
    public static double converterDoubleDuasDecimais(double valorDouble) {  
        DecimalFormat fmt = new DecimalFormat("0.00");        
        String string = fmt.format(valorDouble);  
        String[] part = string.split("[,]");  
        String string2 = part[0]+"."+part[1];  
        double valor = Double.parseDouble(string2);  
        return valor;  
    }  
    
    /**
     * Retorna o nome da sigla da UF informada como parâmetro
     * @param siglaUF
     * @return 
     * @autor Taidson
     */
    public static String siglaUFToNomeUF(String siglaUF){
        if(siglaUF.equals("TO")) return "TOCANTINS";
        if(siglaUF.equals("AC")) return "ACRE";
        if(siglaUF.equals("AL")) return "ALAGOAS";
        if(siglaUF.equals("AM")) return "AMAZONAS";
        if(siglaUF.equals("AP")) return "AMAPA";
        if(siglaUF.equals("BA")) return "BAHIA";
        if(siglaUF.equals("CE")) return "CEARA";
        if(siglaUF.equals("DF")) return "DISTRITO FEDERAL";
        if(siglaUF.equals("ES")) return "ESPIRITO SANTO";
        if(siglaUF.equals("GO")) return "GOIAS";
        if(siglaUF.equals("MA")) return "MARANHAO";
        if(siglaUF.equals("MG")) return "MINAS GERAIS";
        if(siglaUF.equals("MS")) return "MATO GROSSO DO SUL";
        if(siglaUF.equals("MT")) return "MATO GROSSO";
        if(siglaUF.equals("PA")) return "PARA";
        if(siglaUF.equals("PB")) return "PARAIBA";
        if(siglaUF.equals("PE")) return "PERNAMBUCO";
        if(siglaUF.equals("PI")) return "PIAUI";
        if(siglaUF.equals("PR")) return "PARANA";
        if(siglaUF.equals("RJ")) return "RIO DE JANEIRO";
        if(siglaUF.equals("RN")) return "RIO GRANDE DO NORTE";
        if(siglaUF.equals("RO")) return "RONDONIA";
        if(siglaUF.equals("RR")) return "RORAIMA";
        if(siglaUF.equals("RS")) return "RIO GRANDE DO SUL";
        if(siglaUF.equals("SC")) return "SANTA CATARINA";
        if(siglaUF.equals("SE")) return "SERGIPE";
        if(siglaUF.equals("SP")) return "SAO PAULO";
        return "O nome da UF " +siglaUF+ "não foi encontrado";
    }
    
      /**
     * Retorna o nome ID da UF informada como parâmetro
     * @param siglaUF
     * @return 
     * @autor Taidson
     */
    public static String siglaUFToIDUF(String siglaUF){
        if(siglaUF.equals("TO")) return "17";
        if(siglaUF.equals("AC")) return "12";
        if(siglaUF.equals("AL")) return "27";
        if(siglaUF.equals("AM")) return "13";
        if(siglaUF.equals("AP")) return "16";
        if(siglaUF.equals("BA")) return "29";
        if(siglaUF.equals("CE")) return "23";
        if(siglaUF.equals("DF")) return "53";
        if(siglaUF.equals("ES")) return "32";
        if(siglaUF.equals("GO")) return "52";
        if(siglaUF.equals("MA")) return "21";
        if(siglaUF.equals("MG")) return "31";
        if(siglaUF.equals("MS")) return "50";
        if(siglaUF.equals("MT")) return "51";
        if(siglaUF.equals("PA")) return "15";
        if(siglaUF.equals("PB")) return "25";
        if(siglaUF.equals("PE")) return "26";
        if(siglaUF.equals("PI")) return "22";
        if(siglaUF.equals("PR")) return "41";
        if(siglaUF.equals("RJ")) return "33";
        if(siglaUF.equals("RN")) return "24";
        if(siglaUF.equals("RO")) return "11";
        if(siglaUF.equals("RR")) return "14";
        if(siglaUF.equals("RS")) return "43";
        if(siglaUF.equals("SC")) return "42";
        if(siglaUF.equals("SE")) return "28";
        if(siglaUF.equals("SP")) return "35";
        return "O ID da UF " +siglaUF+ "não foi encontrado";
    }
    
     /**
     * Retorna a sigla da UF conforme id informado como parametro
     * @param siglaUF
     * @return 
     * @autor Taidson
     */
    public static String idUFToSiglaUF(String idUF){
        if(idUF.equals("17")) return "TO";
        if(idUF.equals("12")) return "AC";
        if(idUF.equals("27")) return "AL";
        if(idUF.equals("13")) return "AM";
        if(idUF.equals("16")) return "AP";
        if(idUF.equals("29")) return "BA";
        if(idUF.equals("23")) return "CE";
        if(idUF.equals("53")) return "DF";
        if(idUF.equals("32")) return "ES";
        if(idUF.equals("52")) return "GO";
        if(idUF.equals("21")) return "MA";
        if(idUF.equals("31")) return "MG";
        if(idUF.equals("50")) return "MS";
        if(idUF.equals("51")) return "MT";
        if(idUF.equals("15")) return "PA";
        if(idUF.equals("25")) return "PB";
        if(idUF.equals("26")) return "PE";
        if(idUF.equals("22")) return "PI";
        if(idUF.equals("41")) return "PR";
        if(idUF.equals("33")) return "RJ";
        if(idUF.equals("24")) return "RN";
        if(idUF.equals("11")) return "RO";
        if(idUF.equals("14")) return "RR";
        if(idUF.equals("43")) return "RS";
        if(idUF.equals("42")) return "SC";
        if(idUF.equals("28")) return "SE";
        if(idUF.equals("35")) return "SP";
        return "A sigla da UF " +idUF+ "não foi encontrada";
    }
        
    /**
     * Ordena a um lista conforme o campo passado como parâmetro
     * @param lista
     * @param campo 
     * @autor Taidson
     */
    public static void ordenaLista(List lista, String campo){
        Collections.sort(lista, new BeanComparator(campo));
    }
    
    /**
     * Verifica se um dos arquivos selecionados corresponde à DENGON.
     * @param arquivos
     * @return 
     */
    public static boolean verificaDBDengueOnLine(String[] arquivos){
        for(int i = 0; i < arquivos.length; i++){
            if(arquivos[i].substring(0, 6).equals("DENGON")){
                return true;
            }
        }
        return false;
    }
    
      public static void gerarTabela(File[] files, JTable tabela){
        List<String> listaArquivos = new ArrayList<String>();
        
        for(int i=0; i<files.length; i++){
            listaArquivos.add(files[i].getName());
        }
        
        int count=1;
        String arquivo = "Arquivo";
        Vector colunas = new Vector();
        Vector linhas = new Vector();
        Vector registros = new Vector();
        
        for (String item : listaArquivos) {
            arquivo += ""+ count++;
            colunas.addElement((Object) arquivo);
            registros.add(item);
            arquivo = "Arquivo";
        }
        
        linhas.addElement((Object) registros);
        
        DefaultTableModel modelo = new DefaultTableModel(linhas, colunas);
        modelo.setColumnCount(files.length);
        modelo.setRowCount(1);
        tabela.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
//      tabela.setAutoResizeMode(30);
        tabela.setModel(modelo);
    }
      
    public static void gerarTabela(List<Completitude>listaTabela, JTable tabela){
        
        Vector colunas = new Vector();
        Vector linhas = new Vector();
        Vector registros = new Vector();
        
        colunas.addElement((Object) "Agravo");
        colunas.addElement((Object) "Ano");
        colunas.addElement((Object) "Campo");
        colunas.addElement((Object) "Completitude (%)");
        colunas.addElement((Object) "* Faixa");
        colunas.addElement((Object) "Completitude (%) S/Ign.");
        colunas.addElement((Object) "* Faixa  S/Ign.");
        
        for (Completitude item : listaTabela) {
            registros.addElement((Object) item.getAgravo());
            registros.addElement((Object) item.getAno());
            registros.addElement((Object) item.getNome());
            registros.addElement((Object) item.getResultado());
            registros.addElement((Object) item.getFaixa());
            registros.addElement((Object) item.getResultado_9_99());
            registros.addElement((Object) item.getFaixa_9_9());
            linhas.addElement((Object) registros);
            registros = new Vector();
        }
        
        DefaultTableModel modelo = new DefaultTableModel(linhas, colunas);
        modelo.setColumnCount(7);
        modelo.setRowCount(listaTabela.size());
//        tabela.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tabela.setAutoResizeMode(30);
        tabela.setModel(modelo);
    }
    
    public static List<String> lerArquivo(String nomeArquivo){
        List<String> listaCampos = new ArrayList<String>();
        String diretorio = "campos";
        File file = new File(diretorio+"//"+nomeArquivo);
        Properties props = new Properties();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            //le os dados que estao no arquivo
            props.load(fis);
            
            for(Object key : props.keySet()) {
                listaCampos.add(key.toString());
            }
            fis.close();
            return listaCampos;
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
            return null;
        }
        
    }
    
    @Deprecated
    public static List<String> lerArquivo1(String nomeArquivo){
        List<String> listaCampos = new ArrayList<String>();
        try {  
           // Gravando no arquivo  
           File arquivo;  
           String diretorio = "campos";
           StringBuilder campo = new StringBuilder();
           char quebra = 0;
           
           // Lendo do arquivo  
           arquivo = new File(diretorio+"//"+nomeArquivo);  
           FileInputStream fis = new FileInputStream(arquivo);  

           int ln;  
           while ( (ln = fis.read()) != -1 ) {  
               if(ln != 13 && ln != 10){
                   System.out.print((char)ln);
                  // System.out.print(ln);
                   campo.append((char)ln);
               }else{
                   listaCampos.add(campo.toString());
                   campo = new StringBuilder();                   
               }
           }  
           listaCampos.add(campo.toString());
           fis.close();  
           return listaCampos;
        }  
        catch (Exception ee) {  
           ee.printStackTrace();  
           return null;
        }  
    }
    
    public static Boolean verificaCampos(String arquivo, String campo){
        List<String> listaCampos = lerArquivo(arquivo);
        if(isListNotEmpty(listaCampos)){
            for (String item : listaCampos) {
                if(campo.equals(item)){
                    return true;
                }
            }
        }
        return false;
    }
    
    public static String dateToString(Date date, String format) {
        return new java.text.SimpleDateFormat(format).format(date);
    }
    
     public static void initProgressBar(JProgressBar jProgressBar){
        jProgressBar.setStringPainted(true);
        jProgressBar.setValue(0);
    }
    
    /**
     * Método responsável por atualizar os valores de um progressBar.
     * @param jProgressBar
     * @param count
     * @param totalRegistros 
     */
    public static void refreshProgressBar(JProgressBar jProgressBar, int count, int totalRegistros){
        float percentualGeral = Float.parseFloat(String.valueOf(count+1)) / totalRegistros * 100;
        jProgressBar.setValue((int) percentualGeral);
        jProgressBar.setStringPainted(true);
        jProgressBar.setString((int) percentualGeral + "%");
    }
    
    /**
     * Método responsável por atualizar os valores de um progressBar.
     * @param arquivo
     * @param jProgressBar
     * @param count
     * @param totalRegistros 
     */
    public static void refreshProgressBarSimple(JProgressBar jProgressBar, int count, int totalRegistros){
        float percentualGeral = Float.parseFloat(String.valueOf(count+1)) / totalRegistros * 100;
        jProgressBar.setValue((int) percentualGeral);
        jProgressBar.setStringPainted(true);
        jProgressBar.setString((int) percentualGeral + "%");
    }
    /**
     * Método responsável por atualizar os valores de um progressBar.
     * @param arquivo
     * @param jProgressBar
     * @param count
     * @param totalRegistros 
     */
    
    public static void refreshProgressBar(String arquivo, JProgressBar jProgressBar, int count, int totalRegistros){
        float percentualGeral = Float.parseFloat(String.valueOf(count+1)) / totalRegistros * 100;
        jProgressBar.setValue((int) percentualGeral);
        jProgressBar.setStringPainted(true);
        jProgressBar.setString(arquivo + " ("+(int) percentualGeral + "%)");
    }
    
     public static double converterDoubleDuasCasasDecimais(double valorDouble) {  
        DecimalFormat fmt = new DecimalFormat("0.00");        
        String string = fmt.format(valorDouble);  
        String[] part = string.split("[,]");  
        String string2 = part[0]+"."+part[1];  
        double valor = Double.parseDouble(string2);  
        return valor;  
    }  
    
    public static double converterDoubleUmaCasaDecimal(double valorDouble) {  
        DecimalFormat fmt = new DecimalFormat("0.0");        
        String string = fmt.format(valorDouble);  
        String[] part = string.split("[,]");  
        String string2 = part[0]+"."+part[1];  
        double valor = Double.parseDouble(string2);  
        return valor;  
    }  
    
    @Deprecated
    public static void populaComboAgravo1(JComboBox cbAgravo){
        cbAgravo.removeAllItems();
        cbAgravo.addItem("Selecione um Agravo");
        cbAgravo.addItem("ACIDENTE DE TRABALHO COM EXPOSIÇÃO À MATERIAL BIOLÓGICO");
        cbAgravo.addItem("ACIDENTE DE TRABALHO GRAVE");
        cbAgravo.addItem("ACIDENTE POR ANIMAIS PEÇONHENTOS");
        cbAgravo.addItem("AIDS ADULTO");
        cbAgravo.addItem("AIDS CRIANÇA");
        cbAgravo.addItem("ATENDIMENTO ANTI-RÁBICO HUMANO");
        cbAgravo.addItem("BOTULISMO");
        cbAgravo.addItem("CÂNCER RELACIONADO AO TRABALHO");
        cbAgravo.addItem("CÓLERA");
        cbAgravo.addItem("COQUELUCHE");
        cbAgravo.addItem("DENGUE");
        cbAgravo.addItem("DERMATOSES OCUPACIONAIS");
        cbAgravo.addItem("DIFTERIA");
        cbAgravo.addItem("DOENÇAS DE CHAGAS AGUDA");
        cbAgravo.addItem("DOENÇAS EXANTEMÁTICAS");
        cbAgravo.addItem("ESQUISTOSSOMOSE");
        cbAgravo.addItem("FEBRE AMARELA");
        cbAgravo.addItem("FEBRE DO NILO");
        cbAgravo.addItem("FEBRE MACULOSA");
        cbAgravo.addItem("FEBRE TIFÓIDE");
        cbAgravo.addItem("GESTANTES HIV +");
        cbAgravo.addItem("HANSENÍASE");
        cbAgravo.addItem("HANTAVIROSES");
        cbAgravo.addItem("HEPATITES VIRAIS");
        cbAgravo.addItem("INFLUENZA");
        cbAgravo.addItem("INTOXICAÇÕES EXÓGENAS");
        cbAgravo.addItem("LEISHMANIOSE TEGUMENTAR AMERICANA");
        cbAgravo.addItem("LEISHMANIOSE VISCERAL");
        cbAgravo.addItem("LEPTOSPIROSE");
        cbAgravo.addItem("LER/DORT");
        cbAgravo.addItem("MALÁRIA");
        cbAgravo.addItem("MENINGITE");
        cbAgravo.addItem("PAIR");
        cbAgravo.addItem("PARALISIA FLÁCIDA AGUDA/POLIOMIELITE");
        cbAgravo.addItem("PESTE");
        cbAgravo.addItem("PNEUMOCONIOSES");
        cbAgravo.addItem("RAIVA HUMANA");
        cbAgravo.addItem("ROTAVIRUS");
        cbAgravo.addItem("SÍFILIS CONGÊNITA");
        cbAgravo.addItem("SÍFILIS EM GESTANTE");
        cbAgravo.addItem("SÍNDROME DA RUBÉOLA CONGÊNITA");
        cbAgravo.addItem("TÉTANO ACIDENTAL");
        cbAgravo.addItem("TÉTANO NEONATAL");
        cbAgravo.addItem("TRACOMA");
        cbAgravo.addItem("TRANSTORNOS MENTAIS");
        cbAgravo.addItem("TUBERCULOSE");
        cbAgravo.addItem("VIOLÊNCIA DOMÉSTICA, SEXUAL E/OU OUTRAS VIOLÊNCIAS");
    }
    
    public static void populaComboAgravo(JComboBox cbAgravo){
        cbAgravo.removeAllItems();
        cbAgravo.addItem("Selecione um Agravo");
        cbAgravo.addItem("AIDS ADULTO");
        cbAgravo.addItem("AIDS CRIANÇA");
        cbAgravo.addItem("COQUELUCHE");
        cbAgravo.addItem("DENGUE");
        cbAgravo.addItem("DIFTERIA");
        cbAgravo.addItem("DOENÇAS DE CHAGAS AGUDA");
        cbAgravo.addItem("DOENÇAS EXANTEMÁTICAS");
        cbAgravo.addItem("FEBRE AMARELA");
        cbAgravo.addItem("GESTANTES HIV +");
        cbAgravo.addItem("HANSENÍASE");
        cbAgravo.addItem("HEPATITES VIRAIS");
        cbAgravo.addItem("LEISHMANIOSE TEGUMENTAR AMERICANA");
        cbAgravo.addItem("LEISHMANIOSE VISCERAL");
        cbAgravo.addItem("LEPTOSPIROSE");
        cbAgravo.addItem("MENINGITE");
        cbAgravo.addItem("PARALISIA FLÁCIDA AGUDA/POLIOMIELITE");
        cbAgravo.addItem("RAIVA HUMANA");
        cbAgravo.addItem("SÍFILIS CONGÊNITA");
        cbAgravo.addItem("SÍFILIS EM GESTANTE");
        cbAgravo.addItem("TUBERCULOSE");
        cbAgravo.addItem("VIOLÊNCIA DOMÉSTICA, SEXUAL E/OU OUTRAS VIOLÊNCIAS");
    }
    
    public static String bancoAgravo(String agravo){
        if(agravo.equals("AIDS ADULTO")) return "AIDSA";
        if(agravo.equals("AIDS CRIANÇA")) return "AIDSC";
        if(agravo.equals("COQUELUCHE")) return "COQUE";
        if(agravo.equals("DENGUE")) return "DENGN";
        if(agravo.equals("DIFTERIA")) return "DIFTE";
        if(agravo.equals("DOENÇAS DE CHAGAS AGUDA")) return "CHAGA";
        if(agravo.equals("DOENÇAS EXANTEMÁTICAS")) return "EXANT";
        if(agravo.equals("FEBRE AMARELA")) return "FAMAR";
        if(agravo.equals("GESTANTES HIV +")) return "HIVGE";
        if(agravo.equals("HANSENÍASE")) return "HANSN";
        if(agravo.equals("HEPATITES VIRAIS")) return "HEPAN";
        if(agravo.equals("LEISHMANIOSE TEGUMENTAR AMERICANA")) return "LTAN"; //4
        if(agravo.equals("LEISHMANIOSE VISCERAL")) return "LEISH";
        if(agravo.equals("LEPTOSPIROSE")) return "LEPTO";
        if(agravo.equals("MENINGITE")) return "MENIN";
        if(agravo.equals("PARALISIA FLÁCIDA AGUDA/POLIOMIELITE")) return "PFAN"; //4
        if(agravo.equals("RAIVA HUMANA")) return "RAIVA";
        if(agravo.equals("SÍFILIS CONGÊNITA")) return "SIFIC";
        if(agravo.equals("SÍFILIS EM GESTANTE")) return "SIFGE";
        if(agravo.equals("TUBERCULOSE")) return "TUBEN";
        if(agravo.equals("VIOLÊNCIA DOMÉSTICA, SEXUAL E/OU OUTRAS VIOLÊNCIAS")) return "VIOLE";
        
        return "";
        
    }
    
    @Deprecated
    public static String bancoAgravo1(String agravo){
        if(agravo.equals("ACIDENTE DE TRABALHO COM EXPOSIÇÃO À MATERIAL BIOLÓGICO")) return "ACBIO";
        if(agravo.equals("ACIDENTE DE TRABALHO GRAVE")) return "ACGRA";
        if(agravo.equals("ACIDENTE POR ANIMAIS PEÇONHENTOS")) return "ANIMP";
        if(agravo.equals("AIDS ADULTO")) return "AIDSA";
        if(agravo.equals("AIDS CRIANÇA")) return "AIDSC";
        if(agravo.equals("ATENDIMENTO ANTI-RÁBICO HUMANO")) return "ANTRA";
        if(agravo.equals("BOTULISMO")) return "BOTUN";
        if(agravo.equals("CÂNCER RELACIONADO AO TRABALHO")) return "CANCE";
        if(agravo.equals("CÓLERA")) return "COLEN";
        if(agravo.equals("COQUELUCHE")) return "COQUE";
        if(agravo.equals("DENGUE")) return "DENGN";
        if(agravo.equals("DERMATOSES OCUPACIONAIS")) return "DERMA";
        if(agravo.equals("DIFTERIA")) return "DIFTE";
        if(agravo.equals("DOENÇAS DE CHAGAS AGUDA")) return "CHAGA";
        if(agravo.equals("DOENÇAS EXANTEMÁTICAS")) return "EXANT";
        if(agravo.equals("ESQUISTOSSOMOSE")) return "ESQUI";
        if(agravo.equals("FEBRE AMARELA")) return "FAMAR";
        if(agravo.equals("FEBRE DO NILO")) return "NILON";
        if(agravo.equals("FEBRE MACULOSA")) return "FMACU";
        if(agravo.equals("FEBRE TIFÓIDE")) return "FTIFO";
        if(agravo.equals("GESTANTES HIV +")) return "HIVGE";
        if(agravo.equals("HANSENÍASE")) return "HANSN";
        if(agravo.equals("HANTAVIROSES")) return "HANTA";
        if(agravo.equals("HEPATITES VIRAIS")) return "HEPAN";
        if(agravo.equals("INFLUENZA")) return "INFLU";
        if(agravo.equals("INTOXICAÇÕES EXÓGENAS")) return "IEXOG";
        if(agravo.equals("LEISHMANIOSE TEGUMENTAR AMERICANA")) return "LTAN"; //4
        if(agravo.equals("LEISHMANIOSE VISCERAL")) return "LEISH";
        if(agravo.equals("LEPTOSPIROSE")) return "LEPTO";
        if(agravo.equals("LER/DORT")) return "LERN"; //4
        if(agravo.equals("MALÁRIA")) return "MALAN";
        if(agravo.equals("MENINGITE")) return "MENIN";
        if(agravo.equals("PAIR")) return "PAIRN";
        if(agravo.equals("PARALISIA FLÁCIDA AGUDA/POLIOMIELITE")) return "PFAN"; //4
        if(agravo.equals("PESTE")) return "PESTE";
        if(agravo.equals("PNEUMOCONIOSES")) return "PNEUM";
        if(agravo.equals("RAIVA HUMANA")) return "RAIVA";
        if(agravo.equals("ROTAVIRUS")) return "ROTAN";
        if(agravo.equals("SÍFILIS CONGÊNITA")) return "SIFIC";
        if(agravo.equals("SÍFILIS EM GESTANTE")) return "SIFGE";
        if(agravo.equals("SÍNDROME DA RUBÉOLA CONGÊNITA")) return "SRCN"; //4
        if(agravo.equals("TÉTANO ACIDENTAL")) return "TETAC";
        if(agravo.equals("TÉTANO NEONATAL")) return "TETAN";
        if(agravo.equals("TRACOMA")) return "TRACO";
        if(agravo.equals("TRANSTORNOS MENTAIS")) return "MENTA";
        if(agravo.equals("TUBERCULOSE")) return "TUBEN";
        if(agravo.equals("VIOLÊNCIA DOMÉSTICA, SEXUAL E/OU OUTRAS VIOLÊNCIAS")) return "VIOLE";
        
        return "";
        
    }

    
    public static String extrairNomeSobrenome(String nomeCompleto){
        String[] nome = nomeCompleto.split(" ");
        
        return nome[0] + nome[nome.length - 1];
    }
    
    /**
     * Realiza leitura dos DBFs na pasta do Sinan Relatórios.
     * @param arquivo
     * @param caminho
     * @return 
     * @autor Taidson
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
     * Realiza a leitura do DBF de municípios e retorna o nome do município a partir do código do município.
     * @param idMunicipio
     * @return
     * @throws SQLException 
     * @autor Taidson
     */
    public static String municipioCodigoToNome(String idMunicipio) throws SQLException {
        if (idMunicipio == null) {
            return "";
        }
 
        DBFReader reader = SinanUtil.retornaObjetoDbfCaminhoArquivo("MUNICNET", "dbf\\");
        Object[] rowObjects;
        DBFUtil utilDbf = new DBFUtil();
        try {
            utilDbf.mapearPosicoes(reader);
            while ((rowObjects = reader.nextRecord()) != null) {
                if (idMunicipio.equals(utilDbf.getString(rowObjects, "ID_MUNICIP"))) {
                    return utilDbf.getString(rowObjects, "NM_MUNICIP");
                }
            }
        }catch (DBFException e) {
            mensagem("Erro: regional nao encontrada.Verifique se existe a pasta DBF e se os arquivo REGIONET.DBF está lá:\n" + e);
        }
        return "";
    }
    
    /**
     * Método responsável por excluir municípios ignorados
     * @param lista
     * @return 
     */
    public static List<OportunidadeAgravoCOAP>removeMunicipiosIgnorados(List<OportunidadeAgravoCOAP> lista){
        List<OportunidadeAgravoCOAP> listaMunicipio = new ArrayList<OportunidadeAgravoCOAP>();
        
        for (OportunidadeAgravoCOAP municipio : lista) {
            if(!verificaMunicipioIgnorado(municipio)){
                listaMunicipio.add(municipio);
            }
        }
        return listaMunicipio;
    }
    
    /**
     * Verifica se o municípo informado com parâmetro é ignorado.
     * @param municipio
     * @return 
     */
    public static Boolean verificaMunicipioIgnorado(OportunidadeAgravoCOAP municipio){
        if(municipio.getNmAgravo().equals("MUNICIPIO IGNORADO - RO") ||
        municipio.getNmAgravo().equals("MUNICIPIO IGNORADO - RN") ||
        municipio.getNmAgravo().equals("MUNICIPIO IGNORADO - AL") ||
        municipio.getNmAgravo().equals("MUNICIPIO IGNORADO - SE") ||
        municipio.getNmAgravo().equals("MUNICIPIO IGNORADO - MG") ||
        municipio.getNmAgravo().equals("MUNICIPIO IGNORADO - ES") ||
        municipio.getNmAgravo().equals("MUNICIPIO IGNORADO - RJ") ||
        municipio.getNmAgravo().equals("MUNICIPIO IGNORADO - SP") ||
        municipio.getNmAgravo().equals("MUNICIPIO IGNORADO - RS") ||
        municipio.getNmAgravo().equals("MUNICIPIO IGNORADO - MS") ||
        municipio.getNmAgravo().equals("MUNICIPIO IGNORADO - MT") ||
        municipio.getNmAgravo().equals("MUNICIPIO IGNORADO - GO") ||
        municipio.getNmAgravo().equals("MUNICIPIO IGNORADO - DF") ||
        municipio.getNmAgravo().equals("MUNICIPIO IGNORADO - RR") ||
        municipio.getNmAgravo().equals("MUNICIPIO IGNORADO - PA") ||
        municipio.getNmAgravo().equals("MUNICIPIO IGNORADO - AP") ||
        municipio.getNmAgravo().equals("MUNICIPIO IGNORADO - TO") ||
        municipio.getNmAgravo().equals("MUNICIPIO IGNORADO - MA") ||
        municipio.getNmAgravo().equals("MUNICIPIO IGNORADO - PI") ||
        municipio.getNmAgravo().equals("MUNICIPIO IGNORADO - CE") ||
        municipio.getNmAgravo().equals("MUNICIPIO IGNORADO - PB") ||
        municipio.getNmAgravo().equals("MUNICIPIO IGNORADO - PE") ||
        municipio.getNmAgravo().equals("MUNICIPIO IGNORADO - BA") ||
        municipio.getNmAgravo().equals("MUNICIPIO IGNORADO - PR") ||
        municipio.getNmAgravo().equals("MUNICIPIO IGNORADO - SC") ||
        municipio.getNmAgravo().equals("MUNICIPIO IGNORADO - AC") ||
        municipio.getNmAgravo().equals("MUNICIPIO IGNORADO - AM"))
            return true;
        return false;     
    }
    
    /**
     * Método responsável por excluir municípios ignorados
     * @param lista
     * @return 
     */
    public static List<OportunidadeAgravoPQAVS>removeMunicipiosIgnoradosPQAVS(List<OportunidadeAgravoPQAVS> lista){
        List<OportunidadeAgravoPQAVS> listaMunicipio = new ArrayList<OportunidadeAgravoPQAVS>();
        
        for (OportunidadeAgravoPQAVS municipio : lista) {
            if(!verificaMunicipioIgnorado(municipio)){
                listaMunicipio.add(municipio);
            }
        }
        return listaMunicipio;
    }
    
    /**
     * Verifica se o municípo informado com parâmetro é ignorado.
     * @param municipio
     * @return 
     */
    public static Boolean verificaMunicipioIgnorado(OportunidadeAgravoPQAVS municipio){
        if(municipio.getNmAgravo().equals("MUNICIPIO IGNORADO - RO") ||
        municipio.getNmAgravo().equals("MUNICIPIO IGNORADO - RN") ||
        municipio.getNmAgravo().equals("MUNICIPIO IGNORADO - AL") ||
        municipio.getNmAgravo().equals("MUNICIPIO IGNORADO - SE") ||
        municipio.getNmAgravo().equals("MUNICIPIO IGNORADO - MG") ||
        municipio.getNmAgravo().equals("MUNICIPIO IGNORADO - ES") ||
        municipio.getNmAgravo().equals("MUNICIPIO IGNORADO - RJ") ||
        municipio.getNmAgravo().equals("MUNICIPIO IGNORADO - SP") ||
        municipio.getNmAgravo().equals("MUNICIPIO IGNORADO - RS") ||
        municipio.getNmAgravo().equals("MUNICIPIO IGNORADO - MS") ||
        municipio.getNmAgravo().equals("MUNICIPIO IGNORADO - MT") ||
        municipio.getNmAgravo().equals("MUNICIPIO IGNORADO - GO") ||
        municipio.getNmAgravo().equals("MUNICIPIO IGNORADO - DF") ||
        municipio.getNmAgravo().equals("MUNICIPIO IGNORADO - RR") ||
        municipio.getNmAgravo().equals("MUNICIPIO IGNORADO - PA") ||
        municipio.getNmAgravo().equals("MUNICIPIO IGNORADO - AP") ||
        municipio.getNmAgravo().equals("MUNICIPIO IGNORADO - TO") ||
        municipio.getNmAgravo().equals("MUNICIPIO IGNORADO - MA") ||
        municipio.getNmAgravo().equals("MUNICIPIO IGNORADO - PI") ||
        municipio.getNmAgravo().equals("MUNICIPIO IGNORADO - CE") ||
        municipio.getNmAgravo().equals("MUNICIPIO IGNORADO - PB") ||
        municipio.getNmAgravo().equals("MUNICIPIO IGNORADO - PE") ||
        municipio.getNmAgravo().equals("MUNICIPIO IGNORADO - BA") ||
        municipio.getNmAgravo().equals("MUNICIPIO IGNORADO - PR") ||
        municipio.getNmAgravo().equals("MUNICIPIO IGNORADO - SC") ||
        municipio.getNmAgravo().equals("MUNICIPIO IGNORADO - AC") ||
        municipio.getNmAgravo().equals("MUNICIPIO IGNORADO - AM"))
            return true;
        return false;     
    }
}
