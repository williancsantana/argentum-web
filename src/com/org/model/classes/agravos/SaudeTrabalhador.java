/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.org.model.classes.agravos;

import com.linuxense.javadbf.DBFException;
import com.linuxense.javadbf.DBFReader;
import com.linuxense.javadbf.DBFWriter;
import com.org.bd.DBFUtil;
import com.org.model.classes.Agravo;
import com.org.model.classes.ColunasDbf;
import com.org.model.classes.Municipio;
import com.org.negocio.Configuracao;
import com.org.negocio.Util;
import com.org.view.Master;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.beanutils.BeanComparator;

/**
 *
 * @author geraldo
 */
public class SaudeTrabalhador extends Agravo {

    private List<String> agravosValidos = new ArrayList<String>();
    private List<Municipio> municipios = new ArrayList<Municipio>();
    private String sqlAgravosGeraisNumerador;
    private String sqlIntoxicacaoNumerador;
    private String sqlAgravosGeraisDenominador;
    private String sqlIntoxicacaoDenominador;
    static String ANO;
    private Connection conexao;
    private boolean porAgravo;

    public SaudeTrabalhador(boolean isDbf) {
        this.setDBF(isDbf);
        setPeriodo("de Notificação");
        setTipoAgregacao("de Notificação");
        init("postgres");
    }

    public SaudeTrabalhador() {
    }

    @Override
    public void init(String tipoBanco) {
        this.setMultiplicador(1);
        this.setTitulo1("Saúde Trabalhador");
        this.setTextoCompletitude("");
        this.setTituloColuna("Incremento(%)");
        this.setRodape("Incremento: (Nº notificações no período selecionado – nº de notificações em 2008) / Nº de notificações em 2008 *100");
        this.setTipo("trabalhador");
        this.setSqlNumeradorCompletitude("");
        if (!isDBF()) {
            setSqlIntoxicacaoNumerador("select count(*) from dbsinan.tb_investiga_intoxica_exogena where st_acidente_trabalho=1 " + "and (dt_notificacao BETWEEN ?  " + "AND ?) and " + "co_municipio_notificacao = ?");
            setSqlAgravosGeraisNumerador("SELECT  count(*) FROM  dbsinan.tb_notificacao  where " + "co_cid in('Z20.9','Y96','C80','L98.9','Z57.9','H83.3','J64','F99') " + "and (dt_notificacao BETWEEN ?  " + "AND ?) and " + "co_uf_notificacao= ? and " + "co_municipio_notificacao = ?");
            setSqlIntoxicacaoDenominador("select count(*) from dbsinan.tb_investiga_intoxica_exogena where st_acidente_trabalho=1 and " + "(dt_notificacao BETWEEN '2008-01-01' AND '2008-12-31') and " + "co_municipio_notificacao = ?");
            setSqlAgravosGeraisDenominador("SELECT  count(*) FROM  dbsinan.tb_notificacao  where " + "co_cid in('Z20.9','Y96','C80','L98.9','Z57.9','H83.3','J64','F99') " + "and (dt_notificacao BETWEEN '2008-01-01' AND '2008-12-31') and " + "co_uf_notificacao= ? and " + "co_municipio_notificacao = ?");
            this.setSqlNumeradorMunicipioEspecifico("SELECT  count(*) + (select count(*) from dbsinan.tb_investiga_intoxica_exogena where st_acidente_trabalho=1 and (dt_notificacao BETWEEN ?  " + "AND ?) and " + "co_municipio_notificacao = ?)  as numerador FROM  dbsinan.tb_notificacao  where co_cid in('Z20.9','Y96','C80','L98.9','Z57.9','H83.3','J64','F99') " + "and (dt_diagnostico_sintoma BETWEEN ?  " + "AND ?) and " + "co_uf_notificacao= ? and " + "co_municipio_notificacao = ?");
            this.setSqlDenominadorMunicipioEspecifico(
                    "SELECT  count(*) + (select count(*) from dbsinan.tb_investiga_intoxica_exogena where st_acidente_trabalho=1 and " + "(dt_notificacao BETWEEN '2007-01-01' AND '2007-12-31') and " + "co_municipio_notificacao = ?)  as denominador FROM  dbsinan.tb_notificacao  where co_cid in('Z20.9','Y96','C80','L98.9','Z57.9','H83.3','J64','F99') " + "and (dt_diagnostico_sintoma BETWEEN '2007-01-01' AND '2007-12-31') and " + "co_uf_notificacao= ? and " + "co_municipio_notificacao = ?");
            this.setSqlNumeradorEstado(
                    "SELECT  count(*) + (select count(*) from dbsinan.tb_investiga_intoxica_exogena where st_acidente_trabalho=1 and " + "(dt_notificacao BETWEEN ?  " + "AND ?) and " + "co_municipio_notificacao like ?)  as numerador FROM  dbsinan.tb_notificacao" + "  where co_cid in('Z20.9','Y96','C80','L98.9','Z57.9','H83.3','J64','F99') and (dt_diagnostico_sintoma BETWEEN ?  " + "AND ?)" + " and " + "co_uf_notificacao= ?");
            this.setSqlDenominandorEstado(
                    "SELECT  count(*) + (select count(*) from dbsinan.tb_investiga_intoxica_exogena where st_acidente_trabalho=1" + " and (dt_notificacao BETWEEN '2007-01-01' AND '2007-12-31') and co_municipio_notificacao like ?)  as denominador FROM  " + "dbsinan.tb_notificacao  where co_cid in('Z20.9','Y96','C80','L98.9','Z57.9','H83.3','J64','F99') and " + "(dt_diagnostico_sintoma BETWEEN '2007-01-01' AND '2007-12-31') and " + "co_uf_notificacao= ?");
            this.setSqlNumeradorCompletitude("");
            this.setSqlNumeradorBeanMunicipios(this.getSqlNumeradorMunicipioEspecifico());
            this.setSqlDenominadorBeanMunicipios(this.getSqlDenominadorMunicipioEspecifico());
        }
    }

    public boolean verificaAgravo(String CID) {
        List<String> cids = new ArrayList<String>();
        cids.add("Z20.9");
        cids.add("Y96");
        cids.add("C80");
        cids.add("L98.9");
        cids.add("Z57.9");
        cids.add("H83.3");
        cids.add("J64");
        cids.add("F99");
        if (cids.contains(CID)) {
            return true;
        }
        return false;
    }

    private void iniciaAgravosValidos() {

        getAgravosValidos().add("Z209");
        getAgravosValidos().add("Y96");
        getAgravosValidos().add("C80");
        getAgravosValidos().add("L989");
        getAgravosValidos().add("Z579");
        getAgravosValidos().add("H833");
        getAgravosValidos().add("J64");
        getAgravosValidos().add("T659");
        getAgravosValidos().add("F99");
    }

    public String buscaNomeAgravo(String codAgravo) {
        String retorno = "";
        if (codAgravo.equals("Z209")) {
            retorno = "ACIDENTE DE TRABALHO COM EXPOSICAO A MATERIAL BIOLOGICO";
        }
        if (codAgravo.equals("Y96")) {
            retorno = "ACIDENTE DE TRABALHO GRAVE";
        }
        if (codAgravo.equals("C80")) {
            retorno = "CANCER RELACIONADO AO TRABALHO";
        }
        if (codAgravo.equals("L989")) {
            retorno = "DERMATOSES OCUPACIONAIS";
        }
        if (codAgravo.equals("Z579")) {
            retorno = "LER DORT";
        }
        if (codAgravo.equals("H833")) {
            retorno = "PAIR";
        }
        if (codAgravo.equals("J64")) {
            retorno = "PNEUMOCONIOSE";
        }
        if (codAgravo.equals("F99")) {
            retorno = "TRANSTORNO MENTAL";
        }
        if (codAgravo.equals("T659")) {
            retorno = "INTOXICACAO EXOGENA";
        }
        return retorno;
    }

    public HashMap<String, Agravo> populaAgravosBeans(String agravoSelecionado) {

        HashMap<String, String> hashAgravos = new HashMap<String, String>();
        HashMap<String, Agravo> agravosBeans = new HashMap<String, Agravo>();
        //se codRegional estiver preenchida, deve buscar somente os municipios pertencentes a ela

        //busca agravos
        List<String> agravos = null;
        agravos = getAgravosValidos();
        for (int i = 0; i < agravos.size(); i++) {

            Agravo agravoDbf = new Agravo();
            agravoDbf.setCodMunicipio(agravos.get(i));
            agravoDbf.setNomeMunicipio(buscaNomeAgravo(agravos.get(i)));
            agravoDbf.setDenominador("0");
            agravoDbf.setNumerador("0");

            hashAgravos.put(agravos.get(i), agravoDbf.getNomeMunicipio());
            agravosBeans.put(agravoDbf.getCodMunicipio(), agravoDbf);
        }

        hashAgravos = this.sortHashMapByValues(hashAgravos, false);
        Set<String> agravoKeys = hashAgravos.keySet();
        HashMap<String, Agravo> agravosBeansRetorno = new HashMap<String, Agravo>();
        Iterator valueIt = agravoKeys.iterator();
        while (valueIt.hasNext()) {
            String key = (String) valueIt.next();
            agravosBeansRetorno.put(key, agravosBeans.get(key));
        }
        return agravosBeansRetorno;
    }

    public String formataData(String data) {
        return data.split("-")[2] + "/" + data.split("-")[1] + "/" + data.split("-")[0];
    }

    @Override
    public String getTaxaEstado(Connection con, Map parametros) throws SQLException {
        this.setConexao(con);
        return "";
    }

    @Override
    public String getCompletitude(Connection con, Map parametros) throws SQLException {
        return "";
    }

    public boolean verificaPeriodo(Map parametros, DBFUtil utilDbf, Object[] rowObjects) throws ParseException {
        return isBetweenDates(utilDbf.getDate(rowObjects, "DT_NOTIFIC"), (String) parametros.get("parDataInicio"), (String) parametros.get("parDataFim"));
    }

    public void incrementaDenominador(Agravo localNotificacao) {
        if (localNotificacao.getDenominador().equals("")) {
            localNotificacao.setDenominador("0");
        }
        int denominador = Integer.parseInt(localNotificacao.getDenominador());
        denominador++;
        localNotificacao.setDenominador(String.valueOf(denominador));
    }

    public void incrementaNumerador(Agravo localNotificacao) {
        if (localNotificacao.getNumerador().equals("")) {
            localNotificacao.setNumerador("0");
        }
        int numerador = Integer.parseInt(localNotificacao.getNumerador());
        numerador++;
        localNotificacao.setNumerador(String.valueOf(numerador));
    }

    public Agravo calculoPostgreInterno(Map parametros, String parametro1, String nmLocalNotificacao, String condicaoMunicipio) {
        try {
            String sql = "select count(*) as numerador from dbsinan.tb_investiga_intoxica_exogena where st_acidente_trabalho=1 " + "and (dt_notificacao BETWEEN ? AND ?) and co_municipio_notificacao like ?";

            PreparedStatement stm2 = this.conexao.prepareStatement(sql);
            stm2.setDate(1, this.transformaDate(parametros.get("parDataInicio").toString()));
            stm2.setDate(2, this.transformaDate(parametros.get("parDataFim").toString()));
            stm2.setString(3, parametro1 + "%");
            if (!condicaoMunicipio.equals("")) {
                stm2.setString(3, parametro1);
            }
            ResultSet rs2;
            rs2 = stm2.executeQuery();
            rs2.next();
            double numerador = rs2.getDouble("numerador");
            //calcula para os outros agravos
            sql = "SELECT  count(*) as numerador FROM  dbsinan.tb_notificacao  where " + "co_cid in('Z20.9','Y96','C80','L98.9','Z57.9','H83.3','J64','F99') " + "and (dt_notificacao BETWEEN ?  " + "AND ?) and co_uf_notificacao = ? " + condicaoMunicipio;

            stm2 = this.conexao.prepareStatement(sql);
            stm2.setDate(1, this.transformaDate(parametros.get("parDataInicio").toString()));
            stm2.setDate(2, this.transformaDate(parametros.get("parDataFim").toString()));
            stm2.setString(3, parametro1.substring(0, 2));
            if (!condicaoMunicipio.equals("")) {
                stm2.setString(4, parametro1);
            }
            rs2 = stm2.executeQuery();
            rs2.next();
            numerador = numerador + rs2.getDouble("numerador");

            //calcula denominador
            sql = "select count(*) as denominador from dbsinan.tb_investiga_intoxica_exogena where st_acidente_trabalho=1 " + "and (dt_notificacao BETWEEN '2008-01-01' AND '2008-12-31') and co_municipio_notificacao like ?";
            stm2 = this.conexao.prepareStatement(sql);
            stm2.setString(1, parametro1 + "%");
            if (!condicaoMunicipio.equals("")) {
                stm2.setString(1, parametro1);
            }
            rs2 = stm2.executeQuery();
            rs2.next();
            double denominador = rs2.getDouble("denominador");
            //calcula para os outros agravos
            sql = "SELECT  count(*) as denominador FROM  dbsinan.tb_notificacao  where " + "co_cid in('Z20.9','Y96','C80','L98.9','Z57.9','H83.3','J64','F99') " + "and (dt_notificacao BETWEEN '2008-01-01' AND '2008-12-31') and co_uf_notificacao = ? " + condicaoMunicipio;
            stm2 = this.conexao.prepareStatement(sql);
            stm2.setString(1, parametro1.substring(0, 2));
            if (!condicaoMunicipio.equals("")) {
                stm2.setString(2, parametro1);
            }
            rs2 = stm2.executeQuery();
            rs2.next();
            denominador = denominador + rs2.getDouble("denominador");

            Agravo agravoDbf = new Agravo();
            agravoDbf.init("");
            agravoDbf.setCodMunicipio(parametro1);
            agravoDbf.setNomeMunicipio(nmLocalNotificacao);
            agravoDbf.setDenominador(String.valueOf(numerador));
            agravoDbf.setNumerador(String.valueOf(denominador));
            return agravoDbf;
        } catch (SQLException e) {
            Master.mensagem("Erro na conexão do banco de dados: " + e);
        }
        return null;
    }

    public HashMap<String, Agravo> retornaCalculoAgravos(Map parametros, String parametro1, String condicaoMunicipio) {
        HashMap<String, Agravo> municipiosBeans = new HashMap<String, Agravo>();
        Agravo agravoDbf;
        try {
            String sql = "select count(*) as numerador from dbsinan.tb_investiga_intoxica_exogena where st_acidente_trabalho=1 " + "and (dt_notificacao BETWEEN ? AND ?) and co_municipio_notificacao like ?";

            PreparedStatement stm2 = this.conexao.prepareStatement(sql);
            stm2.setDate(1, this.transformaDate(parametros.get("parDataInicio").toString()));
            stm2.setDate(2, this.transformaDate(parametros.get("parDataFim").toString()));
            stm2.setString(3, parametro1 + "%");
            if (!condicaoMunicipio.equals("")) {
                stm2.setString(3, parametro1);
            }
            ResultSet rs2;
            rs2 = stm2.executeQuery();
            rs2.next();
            agravoDbf = new Agravo();
            agravoDbf.init("");
            agravoDbf.setCodMunicipio("T659");
            agravoDbf.setNomeMunicipio("INTOXICACAO EXOGENA");
            agravoDbf.setDenominador(String.valueOf(rs2.getDouble("numerador")));
            agravoDbf.setNumerador("0");
            municipiosBeans.put(agravoDbf.getCodMunicipio(), agravoDbf);

            //calcula para os outros agravos
            sql = "SELECT  t1.co_cid as co_cid,t2.no_agravo as no_agravo,count(*) as numerador FROM  dbsinan.tb_notificacao as t1 inner join dbsinan.tb_agravo as t2 on t1.co_cid=t2.co_cid  where t1.co_cid in('Z20.9','Y96','C80','L98.9','Z57.9','H83.3','J64','F99') and (dt_notificacao BETWEEN ?  " + "AND ?) and co_uf_notificacao = ? " + condicaoMunicipio + " group by t1.co_cid,t2.no_agravo";

            stm2 = this.conexao.prepareStatement(sql);
            stm2.setDate(1, this.transformaDate(parametros.get("parDataInicio").toString()));
            stm2.setDate(2, this.transformaDate(parametros.get("parDataFim").toString()));
            stm2.setString(3, parametro1.substring(0, 2));
            if (!condicaoMunicipio.equals("")) {
                stm2.setString(4, parametro1);
            }
            rs2 = stm2.executeQuery();
            while (rs2.next()) {
                agravoDbf = new Agravo();
                agravoDbf.init("");
                agravoDbf.setCodMunicipio(rs2.getString("co_cid"));
                agravoDbf.setNomeMunicipio(rs2.getString("no_agravo"));
                agravoDbf.setDenominador(String.valueOf(rs2.getDouble("numerador")));
                agravoDbf.setNumerador("0");
                municipiosBeans.put(agravoDbf.getCodMunicipio(), agravoDbf);
            }

            //calcula numerador
            sql = "select count(*) as numerador from dbsinan.tb_investiga_intoxica_exogena where st_acidente_trabalho=1 " + "and (dt_notificacao BETWEEN '2008-01-01' AND '2008-12-31') and co_municipio_notificacao like ?";
            stm2 = this.conexao.prepareStatement(sql);
            stm2.setString(1, parametro1 + "%");
            if (!condicaoMunicipio.equals("")) {
                stm2.setString(1, parametro1);
            }
            rs2 = stm2.executeQuery();
            rs2.next();
            municipiosBeans.get("T659").setNumerador(String.valueOf(rs2.getDouble("numerador")));
            //calcula para os outros agravos
            sql = "SELECT  t1.co_cid as co_cid,t2.no_agravo as no_agravo,count(*) as numerador FROM  dbsinan.tb_notificacao as t1 inner join dbsinan.tb_agravo as t2 on t1.co_cid=t2.co_cid  where " + "t1.co_cid in('Z20.9','Y96','C80','L98.9','Z57.9','H83.3','J64','F99') " + "and (dt_notificacao BETWEEN '2008-01-01' AND '2008-12-31') and co_uf_notificacao = ? " + condicaoMunicipio + " group by t1.co_cid,t2.no_agravo";
            stm2 = this.conexao.prepareStatement(sql);
            stm2.setString(1, parametro1.substring(0, 2));
            if (!condicaoMunicipio.equals("")) {
                stm2.setString(2, parametro1);
            }
            rs2 = stm2.executeQuery();
            while (rs2.next()) {
                if (municipiosBeans.get(rs2.getString("co_cid")) == null) {
                    agravoDbf = new Agravo();
                    agravoDbf.init("");
                    agravoDbf.setCodMunicipio(rs2.getString("co_cid"));
                    agravoDbf.setNomeMunicipio(rs2.getString("no_agravo"));
                    agravoDbf.setNumerador(String.valueOf(rs2.getDouble("numerador")));
                    agravoDbf.setDenominador("0");
                    municipiosBeans.put(agravoDbf.getCodMunicipio(), agravoDbf);
                } else {
                    municipiosBeans.get(rs2.getString("co_cid")).setNumerador(String.valueOf(rs2.getDouble("numerador")));
                }
            }

            return municipiosBeans;
        } catch (SQLException e) {
            Master.mensagem("Erro na conexão do banco de dados: " + e);
        }
        return null;
    }

    public HashMap<String, Agravo> calculoPostgres(Map parametros) {
        HashMap<String, Agravo> municipiosBeans = new HashMap<String, Agravo>();
        //calcular do estado
        String nmParametro1 = "parUf";
        String nmParametro2 = "parSgUf";
        String condicaoMunicipio = "", sql;

        if (parametros.get("municipios").equals("sim")) {
            if (parametros.get("parDiscriminarPorAgravo").toString().equals("true")) {
                Master.mensagem("Selecione uma UF ou um município específico para emitir o relatório por agravo");
                return null;
            }
            condicaoMunicipio = "and co_municipio_notificacao = ?";
            try {
                if (parametros.get("parNomeRegional").equals("-- Selecione --") || parametros.get("parNomeRegional").equals("")) {
                    sql = "select co_municipio_ibge,no_municipio from dbgeral.tb_municipio where sg_uf = '" + parametros.get("parSgUf") + "' order by no_municipio";
                } else {
                    sql = "select t1.co_municipio_ibge,no_municipio from dbgeral.tb_municipio as t1, dblocalidade.rl_regional_municipio_svs as t2 where t2.co_uf_ibge=" + parametros.get("parUf") + " and t1.co_municipio_ibge=t2.co_municipio_ibge and co_regional = '" + parametros.get("parCodRegional") + "' and no_municipio not like '%Ignorado%'  order by no_municipio";
                }
                java.sql.Statement stm = this.getConexao().createStatement();
                ResultSet rs = stm.executeQuery(sql);
                while (rs.next()) {
                    String municipio = rs.getString("co_municipio_ibge");
                    String noMunicipio = rs.getString("no_municipio");
                    getBarraStatus().setString("Calculando município: " + noMunicipio);
                    municipiosBeans.put(municipio,
                            calculoPostgreInterno(parametros, municipio,
                            noMunicipio, condicaoMunicipio));
                }
            } catch (SQLException e) {
                Master.mensagem("Erro na conexão do banco de dados: " + e);
            }
        } else {
            if (parametros.get("parNomeMunicipio") != null) {
                condicaoMunicipio = "and co_municipio_notificacao = ?";
                nmParametro1 = "parMunicipio";
                nmParametro2 = "parNomeMunicipio";
            }
            if (!parametros.get("parDiscriminarPorAgravo").toString().equals("true")) {
                municipiosBeans.put(parametros.get(nmParametro1).toString(),
                        calculoPostgreInterno(parametros, parametros.get(nmParametro1).toString(),
                        parametros.get(nmParametro2).toString(), condicaoMunicipio));
            } else {
                municipiosBeans = retornaCalculoAgravos(parametros, parametros.get(nmParametro1).toString(), condicaoMunicipio);
            }
        }
        return municipiosBeans;
    }

    @Override
    public void calcula(DBFReader reader, Map parametros) {
        this.setPorAgravo(false);
        List<String> municipiosRegionais = new ArrayList<String>();
        if (parametros.get("parDiscriminarPorAgravo").toString().equals("true")) {
            this.setPorAgravo(true);
        }
        HashMap<String, Agravo> municipiosBeans = new HashMap<String, Agravo>();
        if (!isDBF()) {
            municipiosBeans = calculoPostgres(parametros);
        } else {
            iniciaAgravosValidos();
            String colunaLocalNotificacao = "";
            String codRegional = (String) parametros.get("parCodRegional");
            if (codRegional == null) {
                codRegional = "";
            }
            //popula de acordo com os parametros de selecao
            String parametroUF = parametros.get("parUf").toString();
            if (parametros.get("parDiscriminarPorAgravo").toString().equals("true")) {
                for (int i = 0; i < agravosValidos.size(); i++) {
                    Agravo agravoDbf = new Agravo();
                    agravoDbf.init("");
                    agravoDbf.setCodMunicipio(agravosValidos.get(i));
                    agravoDbf.setNomeMunicipio(buscaNomeAgravo(agravosValidos.get(i)));
                    agravoDbf.setDenominador("0");
                    agravoDbf.setNumerador("0");
                    municipiosBeans.put(agravosValidos.get(i), agravoDbf);
                    colunaLocalNotificacao = "ID_AGRAVO";
                }
                municipiosRegionais = verificaMunicipio(codRegional);
            } else {
                if (parametroUF.equals("brasil")) {
                    if (!parametros.get("parDiscriminarPorAgravo").toString().equals("true")) {
                        if (parametros.get("municipios").toString().equals("sim")) {
                            municipiosBeans = populaMunicipiosBeans("BR", "");
                            colunaLocalNotificacao = "ID_MUNICIP";
                        } else {
                            municipiosBeans = populaUfsBeans();
                            colunaLocalNotificacao = "SG_UF_NOT";
                        }
                    }
                } else {
                    if (parametros.get("parNomeMunicipio") != null) {
                        if (parametros.get("municipios").equals("sim")) {

                            municipiosBeans = populaMunicipiosBeans(parametros.get("parSgUf").toString(), codRegional);
                            colunaLocalNotificacao = "ID_MUNICIP";
                        } else {
                            //municipio especifico
                            Agravo agravoDbf = new Agravo();
                            agravoDbf.init("");
                            agravoDbf.setCodMunicipio(parametros.get("parMunicipio").toString());
                            agravoDbf.setNomeMunicipio(parametros.get("parNomeMunicipio").toString());
                            agravoDbf.setDenominador("0");
                            agravoDbf.setNumerador("0");
                            municipiosBeans.put(parametros.get("parMunicipio").toString(), agravoDbf);
                            colunaLocalNotificacao = "ID_MUNICIP";
                        }
                    } else {
                        //UF
                        Agravo agravoDbf = new Agravo();
                        agravoDbf.init("");
                        agravoDbf.setCodMunicipio(parametros.get("parUf").toString());
                        agravoDbf.setNomeMunicipio(parametros.get("parSgUf").toString());
                        agravoDbf.setDenominador("0");
                        agravoDbf.setNumerador("0");
                        municipiosBeans.put(parametros.get("parUf").toString(), agravoDbf);
                        colunaLocalNotificacao = "SG_UF_NOT";
                    }
                }
            }
            //loop para ler os arquivos selecionados
            String[] arquivos = parametros.get("parArquivos").toString().split("\\|\\|");
            for (int k = 0; k < arquivos.length; k++) {

                //verificar nos arquivos se existe alguma notificacao no periodo selecionado
//            janela.jLabel20.setText("Lendo arquivo " + arquivos[k]);
                reader = Util.retornaObjetoDbfCaminhoArquivo(arquivos[k].substring(0, arquivos[k].length() - 4), Configuracao.getPropriedade("caminho"));
                Object[] rowObjects = null;
                boolean isBetween, isIntoxicacao, isBetween2008;
                DBFUtil utilDbf = new DBFUtil();
                String codCid;
                DecimalFormat df = new DecimalFormat("0.##");
                Agravo localNotificacao;
                try {
                    utilDbf.mapearPosicoes(reader);
                    int TotalRegistrosInt = reader.getRecordCount();
                    int i = 1;
                    while ((rowObjects = reader.nextRecord()) != null) {
                        if (utilDbf.getString(rowObjects, colunaLocalNotificacao) != null) {
                            //verifica se existe a referencia do municipio OU AGRAVO no bean
                            localNotificacao = municipiosBeans.get(utilDbf.getString(rowObjects, colunaLocalNotificacao));
                            //se estiver selecionado para discriminar por agravo, verificar se o municipio pertence a regional
                            //selecionada
                            boolean continua1 = true;
                            if (isPorAgravo() && codRegional.length() > 0) {
                                if (!municipiosRegionais.contains(utilDbf.getString(rowObjects, "ID_MUNICIP"))) {
                                    continua1 = false;
                                }
                            }
                            if (localNotificacao != null && continua1) {
                                try {
                                    isBetween = verificaPeriodo(parametros, utilDbf, rowObjects);
                                    isBetween2008 = isBetweenDates(utilDbf.getDate(rowObjects, "DT_NOTIFIC"), "2008-01-01", "2008-12-31");
                                } catch (Exception e) {
                                    Master.mensagem("Erro no arquivo dbf: " + arquivos[k]);
                                    return;
                                }
                                //verifica se esta na selecao quando for descriminar por agravo
                                boolean continua = true;
                                if (parametros.get("parDiscriminarPorAgravo").toString().equals("true")) {
                                    if (parametroUF.equals("brasil")) {
                                        continua = true;
                                    } else {
                                        if (parametros.get("nivelAgregacao").equals("UF")) {
                                            if (parametroUF.equals(utilDbf.getString(rowObjects, "SG_UF_NOT"))) {
                                                continua = true;
                                            } else {
                                                continua = false;
                                            }
                                        } else {
                                            if (parametros.get("parMunicipio").equals(utilDbf.getString(rowObjects, "ID_MUNICIP"))) {
                                                continua = true;
                                            } else {
                                                continua = false;
                                            }
                                        }
                                    }
                                }
                                //verifica se esta no periodo
                                if (continua && (isBetween || isBetween2008)) {
                                    //verificar qual base
                                    isIntoxicacao = true;
                                    if (arquivos[k].substring(0, 4).equals("NIND")) {
                                        isIntoxicacao = false;
                                    }
                                    //se for notindi, verifica se é um dos agravos válidos
                                    if (!isIntoxicacao) {
                                        try {
                                            codCid = utilDbf.getString(rowObjects, "ID_AGRAVO");
                                            if (codCid != null) {
                                                if (agravosValidos.contains(codCid) && !codCid.equals("T659")) {
                                                    if (isBetween) {
                                                        incrementaDenominador(localNotificacao);
                                                    }
                                                    if (isBetween2008) {
                                                        incrementaNumerador(localNotificacao);
                                                    }
                                                }
                                            }
                                        } catch (Exception e) {
                                            Master.mensagem("Erro 547: " + e);
                                        }

                                    } else {
                                        String doencaTrabalho = utilDbf.getString(rowObjects, "DOENCA_TRA");
                                        if (doencaTrabalho != null) {
                                            if (doencaTrabalho.equals("1")) {
                                                if (isBetween) {
                                                    incrementaDenominador(localNotificacao);
                                                }
                                                if (isBetween2008) {
                                                    incrementaNumerador(localNotificacao);
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                        }
                        float percentual = Float.parseFloat(String.valueOf(i)) / Float.parseFloat(String.valueOf(TotalRegistrosInt)) * 100;
                        getBarraStatus().setValue((int) percentual);
                        i++;
                    }
                } catch (DBFException e) {
                    Master.mensagem("Erro 557: " + e);
                }
            }
        }
        List<Agravo> beans = new ArrayList();
        Collection<Agravo> municipioBean = municipiosBeans.values();
        DecimalFormat df = new DecimalFormat("0.00");
        DecimalFormat df2 = new DecimalFormat("0");
        for (Iterator<Agravo> it = municipioBean.iterator(); it.hasNext();) {
            Agravo agravoDBF = it.next();
            double num = Double.parseDouble(agravoDBF.getNumerador());
            double den = Double.parseDouble(agravoDBF.getDenominador());
            double divisor;
            if (den == 0) {
                agravoDBF.setTaxa("0.00");
            } else {
                if (num == 0) {
                    divisor = 1;
                } else {
                    divisor = num;
                }
                agravoDBF.setTaxa(String.valueOf(df.format((den - num) * 100 / divisor)));

            }
            try {
                agravoDBF.setNumerador(agravoDBF.getNumerador().substring(0, agravoDBF.getNumerador().lastIndexOf(".")));
                agravoDBF.setDenominador(agravoDBF.getDenominador().substring(0, agravoDBF.getDenominador().lastIndexOf(".")));
            } catch (Exception e) {
            }
            beans.add(agravoDBF);
        }
        Collections.sort(beans, new BeanComparator("nomeMunicipio"));

        //calcular o total
        if (parametros.get("parUf").toString().equals("brasil") || parametros.get("parSgUf").toString().equals("TODAS")) {
            if (parametros.get("parUf").toString().equals("brasil") && !parametros.get("parDiscriminarPorAgravo").toString().equals("true")) {
                if (!parametros.get("municipios").toString().equals("sim") && !parametros.get("parSgUf").toString().equals("TODAS")) {
                    beans = new ArrayList();
                }
            }
            Agravo agravoBean = new Agravo();
            agravoBean.setNomeMunicipio("BRASIL");
            agravoBean.setCodMunicipio("51");
            agravoBean.setNumerador("0");
            agravoBean.setDenominador("0");
            for (Iterator<Agravo> it = municipioBean.iterator(); it.hasNext();) {
                Agravo agravoUF = it.next();
                double divisor;
                agravoBean.setNumerador(String.valueOf(Integer.parseInt(agravoUF.getNumerador()) + Integer.parseInt(agravoBean.getNumerador())));
                agravoBean.setDenominador(String.valueOf(Integer.parseInt(agravoUF.getDenominador()) + Integer.parseInt(agravoBean.getDenominador())));
                double num = Double.parseDouble(agravoBean.getNumerador());
                double den = Double.parseDouble(agravoBean.getDenominador());
                if (agravoBean.getDenominador().equals("0")) {
                    agravoBean.setTaxa("0.00");
                } else {
                    if (num == 0) {
                        divisor = 1;
                    } else {
                        divisor = num;
                    }
                    agravoBean.setTaxa(df.format((den - num) * 100 / divisor));
                }
            }
            beans.add(agravoBean);
        }
        this.setBeans(beans);
    }

    @Override
    public List getBeanMunicipios(Connection con, Map parametros) throws SQLException {
        calcula(null, parametros);
        return this.getBeans();
    }

    @Override
    public List getBeansMunicipioEspecifico(Connection con, Map parametros) throws SQLException {
        calcula(null, parametros);
        return this.getBeans();
    }

    @Override
    public List getBeansEstadoEspecifico(Connection con, Map parametros) throws SQLException {
        calcula(null, parametros);
        return this.getBeans();
    }

    @Override
    public List getBeanMunicipioEspecifico(Connection con, Map parametros) throws SQLException {
        return getBeans();
    }

    @Override
    public Map getParametros() {
        Map parametros = new HashMap();
        parametros.put("parDataInicio", Util.formataData(this.getDataInicio()));
        parametros.put("parDataFim", Util.formataData(this.getDataFim()));
        parametros.put("parPeriodo", "de " + this.getDataInicio() + " a " + this.getDataFim());
        parametros.put("parTituloColuna", this.getTituloColuna());
        parametros.put("parFator", String.valueOf(this.getMultiplicador()));
        parametros.put("parAno", Util.getAno(this.getDataFim()));
        parametros.put("parRodape", this.getRodape());
        parametros.put("parConfig", "");
        parametros.put("parTituloDenominador", "Not período sel.");
        parametros.put("parTituloNumerador", "Not. em 2008");
        parametros.put("parTituloLocal", "Local de Notificação");
        parametros.put("parTitulo1", "Número de notificações dos agravos à saúde do trabalhador");
        //pegar o ano para exportar para dbf
        ANO = "";
        if (Util.getAno(this.getDataFim()).equals(Util.getAno(this.getDataInicio()))) {
            ANO = Util.getAno(this.getDataFim());
        }
        //informa datas selecionadas
        setDataInicio(this.getDataInicio());
        setDataFim(this.getDataFim());
        return parametros;
    }

    @Override
    public DBFWriter getLinhas(HashMap<String, ColunasDbf> colunas, List bean, DBFWriter writer) throws DBFException, IOException {
        for (int i = 0; i < bean.size(); i++) {
            Object rowData[] = new Object[colunas.size()];
            Agravo agravo = (Agravo) bean.get(i);
            if (agravo.getNomeMunicipio().equals("BRASIL")) {
                rowData[0] = null;
            } else {
                rowData[0] = agravo.getCodMunicipio();
            }
            rowData[1] = agravo.getNomeMunicipio();
            if (isPorAgravo()) {
                rowData[2] = Double.parseDouble(agravo.getNumerador());
                rowData[3] = Double.parseDouble(agravo.getDenominador());
                rowData[4] = Double.parseDouble(agravo.getTaxa().replace(",", "."));
                rowData[5] = preencheAno(getDataInicio(), getDataFim());
                rowData[6] = getDataInicio();
                rowData[7] = getDataFim();
                rowData[8] = "SAUDETRABALHADOR-SINANNET";
            } else {
                if (agravo.getNomeMunicipio().equals("BRASIL")) {
                    rowData[2] = null;
                } else {
                    rowData[2] = agravo.getCodMunicipio().substring(0, 2);
                }
                rowData[3] = Double.parseDouble(agravo.getNumerador());
                rowData[4] = Double.parseDouble(agravo.getDenominador());
                rowData[5] = Double.parseDouble(agravo.getTaxa().replace(",", "."));
                rowData[6] = preencheAno(getDataInicio(), getDataFim());
                rowData[7] = getDataInicio();
                rowData[8] = getDataFim();
                rowData[9] = "SAUDETRABALHADOR-SINANNET";
            }
            writer.addRecord(rowData);
        }
        return writer;
    }

    @Override
    public String getCaminhoJasper() {
        return "/com/org/relatorios/agravo1.jasper";
    }

    @Override
    public HashMap<String, ColunasDbf> getColunas() {
        HashMap<String, ColunasDbf> hashColunas = new HashMap<String, ColunasDbf>();
        if (isPorAgravo()) {
            hashColunas.put("ID_AGRAVO", new ColunasDbf(7));
            hashColunas.put("DS_AGRAVO", new ColunasDbf(30));
        } else {
            hashColunas.put("ID_LOCNOT", new ColunasDbf(7));
            hashColunas.put("DS_LOCNOT", new ColunasDbf(30));
            hashColunas.put("ID_UFNOT", new ColunasDbf(2));
        }
        hashColunas.put("NUM_NOT08", new ColunasDbf(10, 0));
        hashColunas.put("I_NOTTRAB", new ColunasDbf(10, 0));
        hashColunas.put("INCREMENT", new ColunasDbf(10, 2));
        hashColunas.put("ANO_NOTIF", new ColunasDbf(4, 0));
        hashColunas.put("DT_NOTINI", new ColunasDbf(10));
        hashColunas.put("DT_NOTFIN", new ColunasDbf(10));
        hashColunas.put("ORIGEM", new ColunasDbf(30));
        this.setColunas(hashColunas);
        return hashColunas;
    }

    @Override
    public String[] getOrdemColunas() {
        if (isPorAgravo()) {
            return new String[]{"ID_AGRAVO", "DS_AGRAVO", "NUM_NOT08", "I_NOTTRAB", "INCREMENT", "ANO_NOTIF", "DT_NOTINI", "DT_NOTFIN", "ORIGEM"};
        } else {
            return new String[]{"ID_LOCNOT", "DS_LOCNOT", "ID_UFNOT", "NUM_NOT08", "I_NOTTRAB", "INCREMENT", "ANO_NOTIF", "DT_NOTINI", "DT_NOTFIN", "ORIGEM"};
        }

    }

    /**
     * @return the agravosValidos
     */
    public List<String> getAgravosValidos() {
        return agravosValidos;
    }

    /**
     * @param agravosValidos the agravosValidos to set
     */
    public void setAgravosValidos(List<String> agravosValidos) {
        this.agravosValidos = agravosValidos;
    }

    /**
     * @return the municipios
     */
    public List<Municipio> getMunicipios() {
        return municipios;
    }

    /**
     * @param municipios the municipios to set
     */
    public void setMunicipios(List<Municipio> municipios) {
        this.municipios = municipios;
    }

    /**
     * @return the sqlAgravosGeraisNumerador
     */
    public String getSqlAgravosGeraisNumerador() {
        return sqlAgravosGeraisNumerador;
    }

    /**
     * @param sqlAgravosGeraisNumerador the sqlAgravosGeraisNumerador to set
     */
    public void setSqlAgravosGeraisNumerador(String sqlAgravosGeraisNumerador) {
        this.sqlAgravosGeraisNumerador = sqlAgravosGeraisNumerador;
    }

    /**
     * @return the sqlIntoxicacaoNumerador
     */
    public String getSqlIntoxicacaoNumerador() {
        return sqlIntoxicacaoNumerador;
    }

    /**
     * @param sqlIntoxicacaoNumerador the sqlIntoxicacaoNumerador to set
     */
    public void setSqlIntoxicacaoNumerador(String sqlIntoxicacaoNumerador) {
        this.sqlIntoxicacaoNumerador = sqlIntoxicacaoNumerador;
    }

    /**
     * @return the sqlAgravosGeraisDenominador
     */
    public String getSqlAgravosGeraisDenominador() {
        return sqlAgravosGeraisDenominador;
    }

    /**
     * @param sqlAgravosGeraisDenominador the sqlAgravosGeraisDenominador to set
     */
    public void setSqlAgravosGeraisDenominador(String sqlAgravosGeraisDenominador) {
        this.sqlAgravosGeraisDenominador = sqlAgravosGeraisDenominador;
    }

    /**
     * @return the sqlIntoxicacaoDenominador
     */
    public String getSqlIntoxicacaoDenominador() {
        return sqlIntoxicacaoDenominador;
    }

    /**
     * @param sqlIntoxicacaoDenominador the sqlIntoxicacaoDenominador to set
     */
    public void setSqlIntoxicacaoDenominador(String sqlIntoxicacaoDenominador) {
        this.sqlIntoxicacaoDenominador = sqlIntoxicacaoDenominador;
    }

    /**
     * @return the conexao
     */
    public Connection getConexao() {
        return conexao;
    }

    /**
     * @param conexao the conexao to set
     */
    public void setConexao(Connection conexao) {
        this.conexao = conexao;
    }

    /**
     * @return the porAgravo
     */
    public boolean isPorAgravo() {
        return porAgravo;
    }

    /**
     * @param porAgravo the porAgravo to set
     */
    public void setPorAgravo(boolean porAgravo) {
        this.porAgravo = porAgravo;
    }
}
