/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.org.negocio;

import com.linuxense.javadbf.DBFException;
import com.linuxense.javadbf.DBFReader;
import com.org.bd.Conexao;
import com.org.bd.DBFUtil;
import com.org.model.classes.GrupoRelatorio;
import com.org.model.classes.Relatorio;
import com.org.view.Master;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

/**
 *
 * @author Geraldo
 */
public class Util {

    private String tipoBanco;

    public Util() {
    }

    public Conexao conectarSiceb() {
        Conexao con = null;

        try {
//            FileReader siceb = new FileReader(new File("C:\\SICEB.ini"));  
            FileReader siceb = new FileReader(new File("C:\\SinanNet\\SinanNet.ini"));
            BufferedReader leitor = new BufferedReader(siceb);
            String linha = "";
            String tipoBanco = "";
            String nomeDB = "";
            String host = "";
            String usuario = "postgres";
            String senha = "oiretsinimadeduas1953";
            while (!linha.equals("Drivername")) {
                linha = leitor.readLine();
                String s[] = linha.split("\\=");
                if (s[0].equals("Drivername")) {
                    if (s[1].toUpperCase().equals("FIREBIRD")) {
                        tipoBanco = "INTERBASE";
                    } else {
                        tipoBanco = s[1].toUpperCase();
                    }
                    linha = s[0];
                }
                if (s[0].equals("Database")) {
                    nomeDB = s[1];
                }
                if (s[0].equals("Hostname")) {
                    host = s[1];
                }
                if (s[0].equals("User")) {
                    usuario = s[1];
                }
                if (s[0].equals("Pass")) {
                    senha = s[1];
                }
            }
            leitor.close();
            siceb.close();
            this.setTipoBanco(tipoBanco);
            con = new Conexao(tipoBanco, nomeDB, host, usuario, senha);
        } catch (Exception exception) {
            //Master.mensagem("Erro Util -> conectarSiceb \n" + exception);
        }

        return con;
    }

    public ArrayList<GrupoRelatorio> criaGruposRelatorios() {
        ArrayList<GrupoRelatorio> grupos = new ArrayList<GrupoRelatorio>();
        //criar grupos
        GrupoRelatorio grupo = new GrupoRelatorio("PACTO 2010/2011");
        grupo.setHasAgravos(true);
        //cria relatorios
        grupos.add(grupo);

        grupo = new GrupoRelatorio("PACTO 2009");
        grupo.setHasAgravos(true);
        //cria relatorios
        grupo.getRelatorios().add(new Relatorio("Encerramento Oportuno da Investigação",""));
        grupo.getRelatorios().add(new Relatorio("Taxa de letalidade por Febre Hemorrágica Dengue","Dengue"));
        grupos.add(grupo);

        grupo = new GrupoRelatorio("Regularidade na alimentação do Sinan");
        grupo.setHasAgravos(false);
        grupos.add(grupo);

        grupo = new GrupoRelatorio("Outros relatórios");
        grupo.setHasAgravos(true);
        grupos.add(grupo);

        return grupos;
    }

    public String getTipoBanco() {
        return tipoBanco;
    }

    public void setTipoBanco(String tipoBanco) {
        this.tipoBanco = tipoBanco;
    }

    

    public static DBFReader retornaObjetoDbf(String path) {
        DBFReader reader = null;
        InputStream inputStream = null;
        try {
//            inputStream = new FileInputStream(path + arquivo + ".DBF"); // take dbf file as program argument
            inputStream = new FileInputStream(path); // take dbf file as program argument

        } catch (FileNotFoundException e) {
            Master.mensagem("Erro: tabela " + path + " nao encontrada.\n" + e);
            System.out.println("Erro ao abrir o arquivo DBF " + e);
            return reader;
        }
        try {
            reader = new DBFReader(inputStream);
        } catch (DBFException e) {
            Master.mensagem("Erro: tabela " + path + " nao encontrada.\n" + e);
            System.out.println("Erro ao abrir o arquivo DBF 2 " + e);
        }
        return reader;
    }

    public static String getAno(String data) {
        String[] d = data.split("/");
//        if (d[2].equals("2008")) {
//            d[2] = "2007";
//        }
        return d[2];
    }

    public static String formataData(String data) {
        String[] d = data.split("/");
        return d[2] + "-" + d[1] + "-" + d[0];
    }

   

    public HashMap<String, Municipio> getMunicipios(int UF, String regional, String sgUf) {
        DBFReader reader = retornaObjetoDbfCaminhoArquivo("MUNICNET", "dbf\\");
        Object[] rowObjects;
        DBFUtil utilDbf = new DBFUtil();
        HashMap<String, Municipio> municipios = new HashMap<String, Municipio>();
        try {
            utilDbf.mapearPosicoes(reader);
            while ((rowObjects = reader.nextRecord()) != null) {
                if (sgUf.equals(utilDbf.getString(rowObjects, "SG_UF")) || UF == 0) {
                    if (!utilDbf.getString(rowObjects, "NM_MUNICIP").startsWith("IGNORADO") && utilDbf.getString(rowObjects, "NM_MUNICIP").lastIndexOf("TRANSF.") == -1) {
                        municipios.put(utilDbf.getString(rowObjects, "ID_MUNICIP"), new Municipio(utilDbf.getString(rowObjects, "NM_MUNICIP"), utilDbf.getString(rowObjects, "ID_MUNICIP"), utilDbf.getString(rowObjects, "SG_UF")));
                    }
                }
            }
        } catch (DBFException e) {
            Master.mensagem("Erro ao carregar municipios:\n" + e);
            System.out.println("Erro ao carregar municipios: " + e);
        }
        return municipios;
    }   

    public static DBFReader retornaObjetoDbfCaminhoArquivo(String arquivo, String caminho) {
        DBFReader reader = null;
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(caminho + arquivo + ".DBF"); // take dbf file as program argument

        } catch (FileNotFoundException e) {
            Master.mensagem("Erro: tabela " + arquivo + ".dbf nao encontrada.\n" + e);
            System.out.println("Erro ao abrir o arquivo DBF " + e);
            return reader;
        }
        try {
            reader = new DBFReader(inputStream);
        } catch (DBFException e) {
            Master.mensagem("Erro: tabela " + arquivo + ".dbf nao encontrada.\n" + e);
            System.out.println("Erro ao abrir o arquivo DBF 2 " + e);
        }
        return reader;
    }
}

