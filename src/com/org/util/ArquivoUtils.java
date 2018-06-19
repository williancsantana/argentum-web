/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.org.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Date;

/**
 *
 * @author taidson.santos
 */
public class ArquivoUtils {
    public final static String dbf = "dbf";

    /*
     * Get the extension of a file.
     */  
    public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }
    
    public static void gerarLogErro(Exception e){
        String data = SinanDateUtil.dateToString(new Date(), "dd/MM/YYYY HH:mm:ss");
        String dia = data.substring(0,2);
        String mes = data.substring(3,5);
        String ano = data.substring(6,10);
        String separador = File.separator;
        data = data.replace("/","-").replace(":", "-");
        File logDir = new File("");
        File log = new File(logDir.getAbsolutePath()+separador+"logs"+separador+ano+separador+mes+separador+dia+separador+"log "+data+".txt");
        log.getParentFile().mkdirs();//Método para criar a estrutura de diretórios e evitar o FileNotFoundException
        try{
            //PrintStream ps = new PrintStream(log);         
            PrintWriter pw = new PrintWriter(new FileWriter(log));
            e.printStackTrace(pw);
        }
        catch(Exception exception){
            exception.printStackTrace();
        }
    }
    
    public static void gerarArquivo(String content){
        String separador = File.separator;
        String data = SinanDateUtil.dateToString(new Date(), "dd/MM/YYYY HH:mm:ss");
        data = data.replace("/","-").replace(":", "-");
        File pastaAtual = new File("");
        File arquivoDestino = new File(pastaAtual.getAbsolutePath()+separador+"Arquivos Gerados"+separador+"Arquivo "+data+".txt");
        arquivoDestino.getParentFile().mkdirs();
        try{
            BufferedWriter bf = new BufferedWriter(new FileWriter(arquivoDestino));
            bf.write(content);
            bf.close();
        }catch(Exception e){
            gerarLogErro(e);        
        }
    }
}
