/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.org.util;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author Taidson
 */
public class Report {
    
    public void gerarRelatorio(JRDataSource jrds, Map parametros, String jasper, Boolean subRelatorio){
        if(!subRelatorio){
            try {
                
                this.adionarParametrosPadrao(parametros);
                
                URL arquivo = getClass().getResource("/com/org/relatorios/"+ jasper);
                JasperReport jasperReport = (JasperReport) JRLoader.loadObject(arquivo);  
                //aqui, como nÃ£o vais passar parÃ¢metros para dentro do relatÃ³rio, e porque estou a assumir que nÃ£o tenhas ligaÃ§Ã£o com base de dados, os dois ultimos parametros sÃ£o nulos  
                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, jrds);  
                //isto mostra.te o viewer, penso que Ã© a melhor maneira, pois assim a pessoa escolhe o formato em que quer gravar, e o sitio onde gravar  
                JasperViewer jrviewer = new JasperViewer(jasperPrint, false);  
                jrviewer.setVisible(true);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(new JFrame(), "Erro ao gerar relatório.", 
                        "Relatório", JOptionPane.INFORMATION_MESSAGE);
                e.printStackTrace();
            }
        }else{
            this.gerarRelatorio(jrds, parametros, jasper);
        }
    }
    
    private void gerarRelatorio(JRDataSource jrds, Map parametros, String jasper){
        try {
            String diretorio = "/com/org/relatorios/";
            URL relatorio = getClass().getResource(diretorio + jasper);
            URL sub_relatorio = getClass().getResource(diretorio);
            
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(relatorio);
            parametros.put("SUBREPORT_DIR", sub_relatorio.toString());  
            
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, jrds);  
            JasperViewer jrviewer = new JasperViewer(jasperPrint, false);  
            jrviewer.setVisible(true);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(new JFrame(), "Erro ao gerar relatório.", 
                "Relatório", JOptionPane.INFORMATION_MESSAGE);
            e.printStackTrace();
        }
        
    }

    /*
    public void adicionarParametros(Map parametros, String arqSelect, 
            String titulo, String ufNotif, String ufRes,
            String regionalNotif, String regionalRes, 
            String muniNotif, String muniRes, String nivelAgregacao){
        
        if(arqSelect == null) arqSelect = "";
        if(ufRes == null) ufRes = "";
        if(regionalRes == null) regionalRes = "";
        if(muniRes == null) muniRes = "";
        if(ufNotif == null) ufNotif = "";
        if(regionalNotif == null) regionalNotif = "";
        if(muniNotif == null) muniNotif = "";
        if(nivelAgregacao == null) nivelAgregacao = "";
        
        parametros.put("TITULO", titulo);
        parametros.put("AGRECAO", "Nível de Agregação: "+nivelAgregacao);
        parametros.put("UFRES", "UF de Residência: "+ufRes);
        parametros.put("REGRES", "Regional de Residência: "+regionalRes);
        parametros.put("MUNRES", "Município de Residência: "+muniRes);
        parametros.put("UFNOTIF", "UF de Notificação: "+ufNotif);
        parametros.put("REGNOTIF", "Regional de Notificação: "+regionalNotif);
        parametros.put("MUNNOTIF", "Município de Notificação: "+muniNotif);
        parametros.put("ARQSELECT", "Arquivos selecionados: "+arqSelect);
        
        this.adionarParametrosPadrao(parametros);
    }*/
    
    public void adionarParametrosPadrao(Map parametros){
        parametros.put("CABECALHO1", "República Federativa do Brasil - Ministério da Saúde");
        parametros.put("CABECALHO2", "Sistema de Informação de Agravos de Notificação - Sinan");
        parametros.put("RODAPE1", "SINAN Relatórios - Versão "+ SinanUtil.getVersaoSinanRelatorios());
    }
}
