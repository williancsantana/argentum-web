/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.org.dao;
import java.sql.*;  
import java.io.*;  

/**
 *
 * @author taidson.santos
 */
public class ConexaoHSQL {
    private static ConexaoHSQL conexao = null;  
    private Connection conn;  
    /** Creates a new instance of Conexao */  
    public ConexaoHSQL() {  
        try{  
            Class.forName("org.hsqldb.jdbcDriver");  
            conn = DriverManager.getConnection ("jdbc:hsqldb:file:/sinan_relatorios/bdtmp/","SA","");  
        }  
        catch(SQLException e)  
        {  System.out.println('\n' + "Erro na conexão com o banco");  
           e.printStackTrace();  
           System.exit(1);              
        }  
        catch(ClassNotFoundException e)  
        {   System.out.println('\n' + "Classe do driver do banco de dados não encontrada");  
        }  
    }  
     
    public static Connection getConnection(){  
        if(conexao == null){  
            conexao = new ConexaoHSQL();  
        }  
        return conexao.conn;  
    }  
      
   public void close(){  
        try{  
            conn.close();  
        }  
        catch(SQLException e){  
              
        }  
   }   
}
