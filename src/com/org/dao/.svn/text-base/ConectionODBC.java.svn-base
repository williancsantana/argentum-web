/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.org.dao;

import com.org.util.SinanUtil;
import java.sql.*;  
/**
 *
 * @author taidson.santos
 */
public class ConectionODBC {
    
    public ResultSet getListagem(){
        try {   
                 //Registrando o driver:  
  //               Class.forName("sun.jdbc.odbc.JdbcOdbcDriver").newInstance();   
                 //Estabelecendo a conexão através do ODBC criado no Painel de Controle:  
   //              Connection con = DriverManager.getConnection("jdbc:odbc:sinan_relatorios","","");  
                Connection con = ConexaoODBC.getConnection();
                 //Criando um objeto Statement para enviar requisições SQL para o Banco de Dados   
                 Statement stmt = con.createStatement();  
                 //Executando SQL:  
                 stmt.execute("SELECT count(*) as valor FROM BOTUN10.DBF");  
                 //Adquirindo através de um objeto ResulSet, os registros retornados pela SQL:  
                 ResultSet rs = stmt.getResultSet();  
                 
                 while(rs.next()){
                      SinanUtil.imprimirConsole("valor: "+rs.getString("valor"));

                  }
                 //Fechando a conexão:  
                 con.close();  
                 return rs;
              } catch(Exception e) {   
                 System.out.println(e);   
                 return null;
              }  
        }
    
}
