/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.org.model.classes.agravos;

import com.linuxense.javadbf.DBFException;
import com.linuxense.javadbf.DBFReader;
import com.linuxense.javadbf.DBFWriter;
import com.org.bd.DBFUtil;
import com.org.model.classes.Faltoso;
import com.org.model.classes.Agravo;
import com.org.model.classes.ColunasDbf;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.beanutils.BeanComparator;

/**
 *
 * @author geraldo
 */
public class Hanseniase extends Agravo {

    public Hanseniase(boolean isDbf){
        this.setDBF(isDbf);
        setPeriodo("de Diagnóstico");
        setTipoAgregacao("de Notificação Atual");
    }
     @Override
    public List getBeansMunicipioEspecifico(Connection con, Map parametros) throws SQLException {
         return this.getListaMunicipioEspecifico(con, parametros);
     }
     @Override
     //nao usa completitude
     public String getCompletitude(Connection con, Map parametros) throws SQLException {
        return "";
     }
     @Override
     //nao usa este metodo
     public String getTaxaEstado(Connection con, Map parametros) throws SQLException {
         return "";
     }
    @Override
    public List getListaMunicipioEspecifico(Connection con, Map parametros) throws SQLException {
        List beans = new ArrayList();
        if (isDBF()) {
            DBFReader reader = getReader();
            DBFUtil utilDbf = new DBFUtil();
            try {
                utilDbf.mapearPosicoes(reader);
            } catch (DBFException ex) {
                System.out.println(ex);
            }
            String tpAlta;
            String municipio, codMunicipioAtual, noMunicipio = null;

            municipio = parametros.get("parMunicipio").toString();
            noMunicipio = parametros.get("parNomeMunicipio").toString();

            SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
            Object[] rowObjects;
            Date dtUltimoComparecimento;
            Date dtAtual = new Date(new java.util.Date().getTime());

            try {
                double TotalRegistros = Double.parseDouble(String.valueOf(reader.getRecordCount()));
                int i = 1;
                while ((rowObjects = reader.nextRecord()) != null) {
                    tpAlta = utilDbf.getString(rowObjects, "TPALTA_N");
                    if (tpAlta == null) {
                        tpAlta = "";
                    }
                    codMunicipioAtual = utilDbf.getString(rowObjects, "ID_MUNI_AT");
                    if (codMunicipioAtual == null) {
                        codMunicipioAtual = "";
                    }
                    if (codMunicipioAtual.equals(municipio) && tpAlta.isEmpty()) {
                        dtUltimoComparecimento = utilDbf.getDate(rowObjects, "DTULTCOMP");
                        int diferenca = Agravo.dataDiff(dtUltimoComparecimento, dtAtual);
                        if (diferenca > 91) {
                            Faltoso f = new Faltoso();
                            f.setNome(utilDbf.getString(rowObjects, "NM_PACIENT"));
                            f.setNuMesesSemInformacao(String.valueOf(diferenca / 30));
                            f.setNumero(utilDbf.getString(rowObjects, "NU_NOT_AT"));
                            f.setClassificacao(utilDbf.getString(rowObjects, "CLASSATUAL"));
                            f.setDtDiagnostico(formato.format(utilDbf.getDate(rowObjects, "DT_DIAG")));
                            f.setDtInicioTratamento(formato.format(utilDbf.getDate(rowObjects, "DTINICTRAT")));
                            f.setDtNotificacao(formato.format(utilDbf.getDate(rowObjects, "DT_NOTIFIC")));
                            f.setProntuario(utilDbf.getString(rowObjects, "NU_PRONTUA"));
                            f.setUnidade(utilDbf.getString(rowObjects, "ID_UNID_AT"));
                            f.setNoUnidade("");
                            f.setNomeMunicipio(noMunicipio);
                            f.setCodMunicipio(municipio);
                            f.setDtUltimoComparecimento(formato.format(dtUltimoComparecimento));
                            f.setDtHoje(formato.format(dtAtual));
                            beans.add(f);
                        }
                    }
                    float percentual = Float.parseFloat(String.valueOf(i)) / Float.parseFloat(String.valueOf(TotalRegistros)) * 100;
                    getBarraStatus().setValue((int) percentual);
//                getLabel().setText("Registros: " + i + " de " + TotalRegistros);
                    i++;
                }
            } catch (NumberFormatException ex) {
                System.out.println(ex);
            } catch (ParseException ex) {
                System.out.println(ex);
            } catch (DBFException ex) {
                System.out.println(ex);
            }
            System.out.println("terminou");
            Collections.sort(beans, new BeanComparator("unidade"));
        } else {
            ResultSet rs2;
            String municipio, noMunicipio = null;

            municipio = parametros.get("parMunicipio").toString();
            noMunicipio = parametros.get("parNomeMunicipio").toString();
            String sql1 = "select nu_notificacao_atual as notificacao,"
                    + "ds_prontuario as prontuario,"
                    + "co_cnes as unidade,"
                    + "ds_estabelecimento as noUnidade,"
                    + "t1.dt_notificacao as dt_notificacao,"
                    + "no_nome_paciente as nome,"
                    + "tp_classific_operacao_atual as classificacao,"
                    + "dt_diagnostico_sintoma as dt_diagnostico,"
                    + "dt_ini_tratamento as dt_inicio,"
                    + "dt_ultimo_comparecimento as dt_limite,"
                    + "trunc(DATE_PART('days',now()-dt_ultimo_comparecimento)/30) as meses "
                    + "from dbsinan.tb_investiga_hanseniase t1 inner join dbsinan.tb_notificacao t2 on "
                    + "(t1.nu_notificacao=t2.nu_notificacao and t1.dt_notificacao=t2.dt_notificacao  and t1.co_municipio_notificacao=t2.co_municipio_notificacao)  "
                    + "inner join dblocalidade.tb_estabelecimento_saude t3 on t1.co_unidade_atual = t3.co_estabelecimento "
                    + " where t1.co_municipio_atual= ?  " + parametros.get("parCasosNovos")
                    + " and (tp_alta is null or tp_alta = '') and "
                    + "((now()-dt_ultimo_comparecimento) > age('08/12/2006','05/12/2006'))  order by ds_estabelecimento,t1.dt_notificacao desc";
            PreparedStatement stm2;
            try {
                stm2 = con.prepareStatement(sql1);
                stm2.setString(1, municipio);
                rs2 = stm2.executeQuery();
            } catch (Exception exception) {
                SimpleDateFormat formato2 = new SimpleDateFormat("yyyy-MM-dd");
                String dataAtual = formato2.format(new Date());
                sql1 = "select nu_notificacao_atual as notificacao,"
                        + " ds_prontuario as prontuario,"
                        + " co_cnes as unidade,"
                        + " ds_estabelecimento as noUnidade,"
                        + " t1.dt_notificacao as dt_notificacao,"
                        + " no_nome_paciente as nome,"
                        + " tp_classific_operacao_atual as classificacao,"
                        + " dt_diagnostico_sintoma as dt_diagnostico,"
                        + " dt_ini_tratamento as dt_inicio,"
                        + " dt_ultimo_comparecimento as dt_limite,"
                        + " (cast('today' as date)-cast (dt_ultimo_comparecimento as date))/30 meses"
                        + " from tb_investiga_hanseniase t1 inner join tb_notificacao t2 on"
                        + " (t1.nu_notificacao=t2.nu_notificacao and t1.dt_notificacao=t2.dt_notificacao  and t1.co_municipio_notificacao=t2.co_municipio_notificacao)"
                        + "  inner join tb_estabelecimento_saude t3 on t1.co_unidade_atual = t3.co_estabelecimento"
                        + " where t1.co_municipio_atual= " + municipio
                        + " and (tp_alta is null or tp_alta = '') and"
                        + " cast('today' as date)-cast(dt_ultimo_comparecimento as date) > 90"
                        + "  order by ds_estabelecimento,t1.dt_notificacao desc";

                System.out.println(sql1);
                java.sql.Statement stm = con.createStatement();
                rs2 = stm.executeQuery(sql1);

            }
            SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");

            String nome;
            while (rs2.next()) {
                Faltoso f = new Faltoso();
//            nome = decodificaNome(rs2.getString("nome"));
//            f.setNome(nome);
                f.setNome("");
                f.setNuMesesSemInformacao(rs2.getString("meses"));
                f.setNumero(rs2.getString("notificacao"));
                f.setClassificacao(rs2.getString("classificacao"));
                f.setDtDiagnostico(formato.format(rs2.getDate("dt_diagnostico")));
                f.setDtInicioTratamento(formato.format(rs2.getDate("dt_inicio")));
                f.setDtNotificacao(formato.format(rs2.getDate("dt_notificacao")));
                f.setProntuario(rs2.getString("prontuario"));
                f.setUnidade(rs2.getString("unidade"));
                f.setNoUnidade(rs2.getString("noUnidade"));
                f.setNomeMunicipio(noMunicipio);
                f.setCodMunicipio(municipio);
                f.setDtUltimoComparecimento(formato.format(rs2.getDate("dt_limite")));
                beans.add(f);
            }
            System.out.println("terminou");
        }
        return beans;

    }

    @Override
    public List getListaEstadoEspecifico(Connection con, Map parametros) throws SQLException {

        ResultSet rs2;
        List beans = new ArrayList();
        String uf = parametros.get("parUf").toString();
        String noUF = parametros.get("parSgUf").toString();
        String sql1 = "select nu_notificacao_atual as notificacao,"
                + "ds_prontuario as prontuario,"
                + "no_nome_paciente as nome,"
                + "tp_classific_operacao_atual as classificacao,"
                + "to_char(dt_diagnostico_sintoma,'DD/MM/YYYY') as dt_diagnostico,"
                + "to_char(dt_ini_tratamento,'DD/MM/YYYY') as dt_inicio,"
                + "to_char(dt_ultimo_comparecimento,'DD/MM/YYYY') as dt_limite,"
                + "to_char(now(),'DD/MM/YYYY') as dt_atual,"
                + "trunc(DATE_PART('days',now()-dt_ultimo_comparecimento)/30) as meses "
                + "from dbsinan.tb_investiga_hanseniase t1 inner join dbsinan.tb_notificacao t2 on "
                + "(t1.nu_notificacao=t2.nu_notificacao and t1.dt_notificacao=t2.dt_notificacao  and t1.co_municipio_notificacao=t2.co_municipio_notificacao)   "
                + " where t1.co_uf_atual= ?  " + parametros.get("parCasosNovos")
                + " and (tp_alta is null or tp_alta = '') and "
                + "((now()-dt_ultimo_comparecimento) > 90)";

        PreparedStatement stm2 = con.prepareStatement(sql1);
        stm2.setString(1, uf);
        rs2 = stm2.executeQuery();
        while (rs2.next()) {
            Faltoso f = new Faltoso();
            f.setNome(rs2.getString("nome"));
            f.setNuMesesSemInformacao(rs2.getString("meses"));
            f.setNumero(rs2.getString("notificacao"));
            f.setClassificacao(rs2.getString("classificacao"));
            f.setDtDiagnostico(rs2.getString("dt_diagnostico"));
            f.setDtHoje(rs2.getString("dt_atual"));
            f.setDtInicioTratamento(rs2.getString("dt_inicio"));
            f.setProntuario(rs2.getString("prontuario"));
            f.setNomeMunicipio(noUF);
            f.setCodMunicipio(uf);
            f.setDtUltimoComparecimento(rs2.getString("dt_limite"));
            beans.add(f);
        }
        System.out.println("terminou");
        return beans;
    }
@Override
    public String[] getOrdemColunas() {
        return new String[]{"NUM_NOTIF", "NUM_PRONT", "DT_NOTIFI", "NM_PACIEN", "CLA_OPATU", "DT_DIAGN", "DT_INITRAT", "DT_ULTCOM",
        "DT_RELATO", "MES_SINFO","ORIGEM"};
    }
    @Override
    public HashMap<String, ColunasDbf> getColunas() {
        HashMap<String, ColunasDbf> hashColunas = new HashMap<String, ColunasDbf>();
        hashColunas.put("NUM_NOTIF", new ColunasDbf(7));
        hashColunas.put("NUM_PRONT", new ColunasDbf(7));
        hashColunas.put("DT_NOTIFI", new ColunasDbf(10));
        hashColunas.put("NM_PACIEN", new ColunasDbf(30));
        hashColunas.put("CLA_OPATU", new ColunasDbf(1));
        hashColunas.put("DT_DIAGN", new ColunasDbf(10));
        hashColunas.put("DT_INITRAT", new ColunasDbf(10));
        hashColunas.put("DT_ULTCOM", new ColunasDbf(10));
        hashColunas.put("DT_RELATO", new ColunasDbf(10));
        hashColunas.put("MES_SINFO", new ColunasDbf(4,0));
        hashColunas.put("ORIGEM", new ColunasDbf(30));
        this.setColunas(hashColunas);
        return hashColunas;
    }

    @Override
    public Map getParametros() {
        Map parametros = new HashMap();
        parametros.put("parCasosNovos", "");
        this.setTitulo1("Listagem de notificações de prováveis faltosos e abandono do tratamento de hanseníase");
        this.setRodape("1 -Este relatório lista as notificações com tipo de saída não preenchido e cujo " + "intervalo entre data de emissão do relatório e data do último comparecimento ultrapasse 91 " + "dias. Esses casos podem ser conseqüente da falta de digitação dos dados de acompanhamento, " + "da falta de comparecimento do paciente à US ou ao acompanhamento da PSF (faltoso = 03 meses " + "e mais a 11 meses; abandono de tratamento =12 meses e mais) e falta de atualização do campo " + "tipo de saída com categoria 7-abandono. Tem como finalidade alertar as SMS  e  US sobre a " + "necessidade de fazer buscas ativas dos faltosos, confirmar possíveis abandonos e fazer as " + "devidas atualizações na base de dados.\n\n" + "2- os dados das notificações dessa lista devem ser atualizados por meio do preenchimento do Boletim de Acompanhamento de Hanseníase emitido e enviado mensalmente pela SMS.");
        parametros.put("parTitulo1", this.getTitulo1());
        parametros.put("parRodape", this.getRodape());
        parametros.put("parConfig", "");
        return parametros;
    }

    @Override
    public DBFWriter getLinhas(HashMap<String, ColunasDbf> colunas, List bean, DBFWriter writer) throws DBFException, IOException {
        for (int i = 0; i < bean.size(); i++) {
            Object rowData[] = new Object[colunas.size()];
            Faltoso faltoso = (Faltoso) bean.get(i);
            rowData[0] = faltoso.getNumero();
            if (faltoso.getProntuario() != null) {
                rowData[1] = faltoso.getProntuario();
            }
            rowData[2] = faltoso.getDtNotificacao();
            rowData[3] = faltoso.getNome();
            rowData[4] = faltoso.getClassificacao();
            rowData[5] = faltoso.getDtDiagnostico();
            rowData[6] = faltoso.getDtInicioTratamento();
            rowData[7] = faltoso.getDtUltimoComparecimento();
            Date data = new Date();
            SimpleDateFormat formatador = new SimpleDateFormat("dd/MM/yyyy");            
            rowData[8] = formatador.format( data );
            rowData[9] = Double.parseDouble(faltoso.getNuMesesSemInformacao());
            rowData[10] = "HANSENIASE-SINANNET";
            writer.addRecord(rowData);
        }
        return writer;
    }

    @Override
    public String getCaminhoJasper() {
        return "/com/org/relatorios/hanseniase_listagem.jasper";
    }
    
}
