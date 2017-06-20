/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.org.dao;
import java.sql.*;  
import java.io.*;  
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author taidson.santos
 */
public class ConexaoODBC {
    private static ConexaoODBC conexao = null;  
    private static ConexaoODBC conexao2 = null;  
    private Connection con; 
    private Connection con2; 
    /** Creates a new instance of Conexao */  
    public ConexaoODBC() {  
            try {
                //Registrando o driver:  
                Class.forName("sun.jdbc.odbc.JdbcOdbcDriver").newInstance();
                con = DriverManager.getConnection("jdbc:odbc:sinan_relatorios","","");
            } catch (InstantiationException ex) {
                Logger.getLogger(ConexaoODBC.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(ConexaoODBC.class.getName()).log(Level.SEVERE, null, ex);
            }
            //Estabelecendo a conexão através do ODBC criado no Painel de Controle:  
             
        catch(SQLException e)  
        {  System.out.println('\n' + "Erro na conexão com o banco");  
           e.printStackTrace();  
           System.exit(1);              
        }  
        catch(ClassNotFoundException e)  
        {   System.out.println('\n' + "Classe do driver do banco de dados não encontrada");  
        }  
    }  
    public ConexaoODBC(Connection connection) {  
            try {
                //Registrando o driver:  
                Class.forName("sun.jdbc.odbc.JdbcOdbcDriver").newInstance();
                con2 = DriverManager.getConnection("jdbc:odbc:sinan_relatorios","","");
            } catch (InstantiationException ex) {
                Logger.getLogger(ConexaoODBC.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(ConexaoODBC.class.getName()).log(Level.SEVERE, null, ex);
            }
            //Estabelecendo a conexão através do ODBC criado no Painel de Controle:  
             
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
            conexao = new ConexaoODBC();  
        }  
        return conexao.con;  
    }
    
    public static Connection getConnection2(){  
        if(conexao2 == null){  
            conexao2 = new ConexaoODBC(null);  
        }  
        return conexao2.con2;  
    }
    
       
    
      
   public void close(){  
        try{  
            con.close();  
        }  
        catch(SQLException e){  
              
        }  
   }   
   public void close(Connection connection){  
        try{  
            con2.close();  
        }  
        catch(SQLException e){  
              
        }  
   }   
}
