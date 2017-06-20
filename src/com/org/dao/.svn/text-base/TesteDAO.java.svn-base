/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.org.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author Taidson
 */
public class TesteDAO extends GenericDAO{

    public int totalRegistros(String diretorio, List<String> bancos) throws SQLException {
        int totalRegistros = 0;
        int count =0;
        
     //   String query = "Select count(*) as valor from "+banco+" where id_agravo = '"+ id_agravo +"' ";

        String query = "select NM_PACIENT, NU_IDADE_N, CS_SEXO, SG_UF_NOT, ID_MUNICIP, count(*) as valor from DENGN10.DBF where NDUPLIC_N is null or NDUPLIC_N = '0' group by NM_PACIENT, NU_IDADE_N, CS_SEXO, SG_UF_NOT, ID_MUNICIP";
     //   String query = "Select count(*) as valor from "+banco;
        ResultSet rs = super.readDBF(diretorio, query);
        
        while (rs.next()) {
            //totalRegistros = rs.getInt("valor"); 
            count++;
        }
        return totalRegistros;
    }
    
//    public String montaQuery(List<String> bancos){
//        String query;
//        
//        for (String banco : bancos) {
//            query += "Select count(*)"
//        }
//        
//        
//        return query;
//    }

    public int totalRegistros(String diretorio, String banco, String id_agravo) throws SQLException {
        int totalRegistros = 0;
        
        String query = "Select count(*) as valor from "+banco+" where id_agravo = '"+ id_agravo +"' ";

//        String query = "Select count(*) as valor from "+banco+" where id_agravo = 'A539' ";
//        String query = "Select count(*) as valor from "+banco+" where id_agravo = 'J06' ";
//          String query = "Select count(*) as valor from "+banco+" where id_agravo = 'B24' ";
 //       String query = "Select count(*) as valor from "+banco+" where id_agravo = 'A229' ";
//        String query = "Select count(*) as valor from "+banco+" where id_agravo = 'A810' ";
//        String query = "Select count(*) as valor from "+banco+" where id_agravo = 'Y59' ";
 //       String query = "Select count(*) as valor from "+banco+" where id_agravo = 'A219' ";
//        String query = "Select count(*) as valor from "+banco+" where id_agravo = 'B019' ";
//        String query = "Select count(*) as valor from "+banco+" where id_agravo = 'B03' ";
 //       String query = "Select count(*) as valor from "+banco+" where id_agravo = 'A719' ";
 //       String query = "Select count(*) as valor from "+banco+" where id_agravo = 'A08' ";
 //       String query = "Select count(*) as valor from "+banco+" where id_agravo = 'D699' ";
 //       String query = "Select count(*) as valor from "+banco+" where id_agravo = 'G043' ";
 //       String query = "Select count(*) as valor from "+banco+" where id_agravo = 'J07' ";
 //       String query = "Select count(*) as valor from "+banco+" where id_agravo = 'N199' ";
 //       String query = "Select count(*) as valor from "+banco+" where id_agravo = 'R17' ";
 //       String query = "Select count(*) as valor from "+banco+" where id_agravo = 'R699' ";
 //       String query = "Select count(*) as valor from "+banco+" where id_agravo = 'B749' ";
        //String query = "Select count(*) as valor from "+banco+" where id_agravo = 'Z206' ";
//        String query = "Select count(*) as valor from "+banco+" where id_agravo = 'O986' ";
//        String query = "Select count(*) as valor from "+banco+" where id_agravo = 'P371' ";
   //     String query = "Select count(*) as valor from "+banco+" where id_agravo = 'R36' ";
        
        
     //   String query = "Select count(*) as valor from "+banco;
        ResultSet rs = super.readDBF(diretorio, query);
        
        while (rs.next()) {
            totalRegistros = rs.getInt("valor"); 
        }
        return totalRegistros;
    }
    
//    public Teste resultado(String diretorio, String banco, String campo, int qtdeRegistros, String id_agravo) throws SQLException {
//        Teste teste = new Teste();
//          String query = "Select count(*) as valor from "+banco+" where "+campo+" is not null and id_agravo = '"+ id_agravo +"' ";     
 //       String query = "Select count(*) as valor from "+banco+" where "+campo+" is not null and id_agravo = 'A539' ";
//        String query = "Select count(*) as valor from "+banco+" where "+campo+" is not null and id_agravo = 'J06' "; //não possui registros em 01/2009
 //       String query = "Select count(*) as valor from "+banco+" where "+campo+" is not null and id_agravo = 'B24' ";
        //String query = "Select count(*) as valor from "+banco+" where "+campo+" is not null and id_agravo = 'A229' ";
 //       String query = "Select count(*) as valor from "+banco+" where "+campo+" is not null and id_agravo = 'A810' ";
 //       String query = "Select count(*) as valor from "+banco+" where "+campo+" is not null and id_agravo = 'Y59' ";
 //       String query = "Select count(*) as valor from "+banco+" where "+campo+" is not null and id_agravo = 'A219' ";
 //       String query = "Select count(*) as valor from "+banco+" where "+campo+" is not null and id_agravo = 'B019' ";
//        String query = "Select count(*) as valor from "+banco+" where "+campo+" is not null and id_agravo = 'B03' ";
//        String query = "Select count(*) as valor from "+banco+" where "+campo+" is not null and id_agravo = 'A719' ";
 //       String query = "Select count(*) as valor from "+banco+" where "+campo+" is not null and id_agravo = 'A08' ";
  //     String query = "Select count(*) as valor from "+banco+" where "+campo+" is not null and id_agravo = 'D699' ";
  //      String query = "Select count(*) as valor from "+banco+" where "+campo+" is not null and id_agravo = 'G043' ";
 //       String query = "Select count(*) as valor from "+banco+" where "+campo+" is not null and id_agravo = 'J07' ";
 //       String query = "Select count(*) as valor from "+banco+" where "+campo+" is not null and id_agravo = 'N199' ";
 //       String query = "Select count(*) as valor from "+banco+" where "+campo+" is not null and id_agravo = 'R17' ";
 //       String query = "Select count(*) as valor from "+banco+" where "+campo+" is not null and id_agravo = 'R699' ";
 //       String query = "Select count(*) as valor from "+banco+" where "+campo+" is not null and id_agravo = 'B749' ";
 //       String query = "Select count(*) as valor from "+banco+" where "+campo+" is not null and id_agravo = 'Z206' ";
//        String query = "Select count(*) as valor from "+banco+" where "+campo+" is not null and id_agravo = 'O986' ";
//        String query = "Select count(*) as valor from "+banco+" where "+campo+" is not null and id_agravo = 'P371' ";
//        String query = "Select count(*) as valor from "+banco+" where "+campo+" is not null and id_agravo = 'R36' ";
        
        
//        String query = "Select count(*) as valor from "+banco+" where "+campo+" is not null";
  //      ResultSet rs = super.readDBF(diretorio, query);
        
 //       while (rs.next()) {
//            teste.setNome(campo);
//            teste.setResultado((rs.getDouble("valor")/qtdeRegistros)*100); 
//        }
//        return teste;
 //   }
    
    
    
 /*   Conexao conexao = getConnection();

    
    public void montaQuery() throws SQLException{
        String query = "SELECT  * FROM  dbsinan.tb_agravo where co_cid = 'A90'";
        ResultSet rs = super.executeQuery(conexao, query);
        
        String nomeMunicipio = "";
         while (rs.next()) {
             nomeMunicipio = rs.getString("no_agravo");
         }
         SinanUtil.mensagem("teste DAO", nomeMunicipio);
         super.disconnect(conexao, rs);
    }
    */
    
}
