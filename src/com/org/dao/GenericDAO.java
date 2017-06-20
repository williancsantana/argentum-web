/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.org.dao;

import com.org.bd.Conexao;
import com.org.negocio.Util;
import com.org.util.SinanUtil;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;

/**
 *
 * @author Taidson
 */
public class GenericDAO {
    
   protected ResultSet readDBF(String diretorio, String query){
        ResultSet rs =  null;
        try{
            String conexao = "com.hxtt.sql.dbf.DBFDriver";  

            // Carrega o Driver a ser utilizado  
            Class.forName(conexao).newInstance();  

            // String de conexao para o DBF  
            String url = "jdbc:DBF:/" + diretorio;  

            // Abre uma conexão com o arquivo  
            Connection conn = DriverManager.getConnection(url);  

            // recuperar a classe Stamtemant a partir da conexao criada  
            Statement stmt = conn.createStatement();  

            // Retorna o resultado da Query  
            rs = stmt.executeQuery(query); 

        } catch(Exception e) {  
            System.out.println("Erro ao tentar ler DBF: " + e);
        }

        return rs;
    }
   
    protected Conexao getConnection(){
        Util util = new Util();
        Conexao conexao = util.conectarSiceb();
        try{
            conexao.conect();
        }catch(Exception e){
            SinanUtil.mensagem("Erro ao conectar com o Banco de dados", "Não foi possível conectar ao banco de dados. Verifique se esta máquina possui o Sinan NET instalado.\nCaso não possua, utilize a base DBF.\n" + e);
        }
        return conexao;
    }
    
    protected ResultSet executeQuery(Conexao conexao, String query){
         ResultSet rs = null;
        try{
            Statement stm = conexao.getC().createStatement();
            rs = stm.executeQuery(query);
        }catch(Exception e){
            SinanUtil.mensagem("Erro", "Erro ao realizar consulta no banco de dados.\n" + e);
        }
        return rs;
    }
    
    protected void disconnect(Conexao conexao, ResultSet rs) throws SQLException{
        try{
            rs.close();
            conexao.disconect();
        }catch(Exception e){
            System.out.println("Erro ao tentar fechar a conexão com o banco: " + e);
        }
    }
            
            
}
