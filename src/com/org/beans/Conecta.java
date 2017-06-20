/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.org.beans;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Taidson
 */
public class Conecta {
       private Connection con = null;  
     
   private String url=null;  
   private String user=null;  
   private String senha=null;     
   private String driver=null;  
     
   private String caminho=null;  
     
   public Conecta(String url){  
      this.url = url;        
      this.driver="com.hxtt.sql.dbf.DBFDriver";     
        
   }  
     
   public  boolean Conectar(){  
        
      try {  
         Class.forName(driver).newInstance();  
         this.con = java.sql.DriverManager.getConnection(this.url);           
         return(true);           
           
      } catch (SQLException e) {   
         e.printStackTrace();  
         return(false);  
           
      } catch (Exception e) {  
         e.printStackTrace();  
         return(false);           
      }                     
   }  
     
   public Connection getConnection(){  
      return(this.con);  
   }     
/*     
   public static void main(String args[]){  
      Conecta c = new Conecta("jdbc:DBF:/"+"E:\\DADOS\\BD\\DBF\\");  
      if( c.Conectar()){  
         System.out.println("Conexão OK");     
         c.getSQL();  
      }        
   }  
*/     
   public void getSQL(){  
      try{  
         Statement st=this.getConnection().createStatement();        
         String SQL = "SELECT * FROM output where ID_MUNIC '310020'";  
         ResultSet rs = st.executeQuery(SQL);  
           
           
      }catch(SQLException exc){  
         exc.printStackTrace();  
      }  
        
   }  

}
