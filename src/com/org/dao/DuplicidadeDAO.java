/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.org.dao;

import com.org.beans.Duplicidade;
import com.org.util.SinanUtil;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author taidson.santos
 */
public class DuplicidadeDAO {
    
    
    public ResultSet findAllDuplicidades(String banco, String dataInicio, String dataFim){
        try {   
                 //Registrando o driver:  
  //               Class.forName("sun.jdbc.odbc.JdbcOdbcDriver").newInstance();   
                 //Estabelecendo a conexão através do ODBC criado no Painel de Controle:  
   //              Connection con = DriverManager.getConnection("jdbc:odbc:sinan_relatorios","","");  
                Connection con = ConexaoODBC.getConnection();
                 //Criando um objeto Statement para enviar requisições SQL para o Banco de Dados   
                 Statement stmt = con.createStatement();  
                 //Executando SQL:  
                stmt.execute("select FONETICA_N, NU_IDADE_N, CS_SEXO, count(*) as VALOR from "+banco+
                         " where FONETICA_N is not null and NU_IDADE_N is not null and CS_SEXO is not null"+
                         " and (NDUPLIC_N is null or NDUPLIC_N = '0')"+
                         " group by FONETICA_N, NU_IDADE_N, CS_SEXO having count(*) > 1");
                                 
                 //Adquirindo através de um objeto ResulSet, os registros retornados pela SQL:  
                 ResultSet rs = stmt.getResultSet();  
                 
 //                while(rs.next()){
  //                    SinanUtil.imprimirConsole("valor: "+rs.getString("valor"));

 //                 }
                 //Fechando a conexão:  
   //              con.close();  
                 return rs;
              } catch(Exception e) {   
                 System.out.println(e);   
                 return null;
              }  
    }
    public ResultSet findDuplicidade(Connection con, String fonetica_n, String nu_idade_n, String cs_sexo, String banco){
        try {   
            
   //         String sql = "select SG_UF_NOT, ID_MUNICIP from "+banco+" where FONETICA_N = '"+fonetica_n+"' and NU_IDADE_N = "+nu_idade_n+" and CS_SEXO = '"+cs_sexo+"'";
            
   //         SinanUtil.imprimirConsole(sql);
                 //Registrando o driver:  
  //               Class.forName("sun.jdbc.odbc.JdbcOdbcDriver").newInstance();   
                 //Estabelecendo a conexão através do ODBC criado no Painel de Controle:  
   //              Connection con = DriverManager.getConnection("jdbc:odbc:sinan_relatorios","","");  
  //-              Connection con = ConexaoODBC.getConnection();
                 //Criando um objeto Statement para enviar requisições SQL para o Banco de Dados   
                 Statement stmt = con.createStatement(); 
                 //Executando SQL:  
                stmt.execute("select SG_UF_NOT, ID_MUNICIP from "+banco+" where FONETICA_N = '"+fonetica_n+"' and NU_IDADE_N = "+nu_idade_n+" and CS_SEXO = '"+cs_sexo+"'");// and NU_IDADE_N = '"+nu_idade_n+"' and CS_SEXO = '"+cs_sexo+"'");
                
                                 
                 //Adquirindo através de um objeto ResulSet, os registros retornados pela SQL:  
                 ResultSet rs = stmt.getResultSet();  
                 
//                 while(rs.next()){
//                      SinanUtil.imprimirConsole("valor: "+rs.getString("valor"));
//
//                  }
                 //Fechando a conexão:  
   //-              con.close();  
                 return rs;
              } catch(Exception e) {   
                 System.out.println(e);   
                 return null;
              }  
    }
    
    
    
    public Integer getCount(String banco){
        try {   
            int count = 0;
                 //Registrando o driver:  
  //               Class.forName("sun.jdbc.odbc.JdbcOdbcDriver").newInstance();   
                 //Estabelecendo a conexão através do ODBC criado no Painel de Controle:  
   //              Connection con = DriverManager.getConnection("jdbc:odbc:sinan_relatorios","","");  
                Connection con = ConexaoODBC.getConnection();
                 //Criando um objeto Statement para enviar requisições SQL para o Banco de Dados   
                 Statement stmt = con.createStatement();  
                 //Executando SQL:  
                stmt.execute("select FONETICA_N, NU_IDADE_N, CS_SEXO, count(*) as valor from "+banco+
                         " where FONETICA_N is not null and NU_IDADE_N is not null and CS_SEXO is not null"+
                         " and (NDUPLIC_N is null or NDUPLIC_N = '0')"+
                         " group by FONETICA_N, NU_IDADE_N, CS_SEXO having count(*) > 1");
                                 
                 //Adquirindo através de um objeto ResulSet, os registros retornados pela SQL:  
                 ResultSet rs = stmt.getResultSet();  
                 
                 while(rs.next()){
                     count++;
                }
                 //Fechando a conexão:  
           //      con.close();  
                 return count;
              } catch(Exception e) {   
                 System.out.println(e);   
                 return null;
              }  
    }
    
    
     public ResultSet getDuplicidade(String banco, String dataInicio, String dataFim){
        try {   
                Connection con = ConexaoODBC.getConnection();
                 //Criando um objeto Statement para enviar requisições SQL para o Banco de Dados   
                 Statement stmt = con.createStatement();  
                 //Executando SQL:  
                 stmt.execute("SELECT count(*) as valor FROM BOTUN10.DBF");  
                 //Adquirindo através de um objeto ResulSet, os registros retornados pela SQL:  

                 //Fechando a conexão:  
                 con.close();  
                 return stmt.getResultSet();
              } catch(Exception e) {   
                 System.out.println(e);   
                 return null;
              }  
    }
     
    public ResultSet listaDuplicidade(){
        try{
            Connection conn = ConexaoHSQL.getConnection();  
            
            Statement stm = conn.createStatement();   
            ResultSet rs = stm.executeQuery("select uf, munic, nome, idade, sexo from public.duplicidade");
            
            

                  while(rs.next()){
                      SinanUtil.imprimirConsole(rs.getString("uf"));

                  }
                  return rs;

       }catch (SQLException e){   
           e.printStackTrace();  
           System.exit(1);  

       }              
          
          return null;
    }
    
    public void insert(Duplicidade bean){
        try{
            Connection conn = ConexaoHSQL.getConnection();  
            Statement stm = conn.createStatement();
            
//            stm.executeUpdate("insert into public.duplicidade values ('"+bean.getUF()+"', '"+bean.getMunicipio()+"', "
 //                   + "'"+bean.getNome()+"'," + "'"+bean.getIdade()+"', '"+bean.getSexo()+"')");
            
       }catch (SQLException e){   
           e.printStackTrace();  
           System.exit(1);  

       }              
          
    }
    
    
    
    
}
