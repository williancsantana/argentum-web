/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.org.model.classes.agravos;

import com.linuxense.javadbf.DBFException;
import com.linuxense.javadbf.DBFReader;
import com.linuxense.javadbf.DBFWriter;
import com.org.bd.DBFUtil;
import com.org.model.classes.ColunasDbf;
import com.org.negocio.Configuracao;
import com.org.negocio.Util;
import com.org.util.SinanUtil;
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
import java.util.Date;
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
public class TuberculoseCoorte extends com.org.model.classes.Agravo {

    private String cura;
    private String abandono;
    private String transfMesmoMunicipio;
    private String transfOutroMunicipio;
    private String transfOutroUf;
    private String naoPreenchido;
    private String erroDiagnostico;
    private String perNaoPreenchido;
    private String subTotal;
    private String total;
    private String perAbandono;
    private String perCura;
    private String transfOutroPais;
    private String obitoComTuberculose;
    private String obitoPorTuberculose;
    private String outraCategoria;
    static String dtInicial;
    static String dtFinal;
    boolean situacaoEncerramento;

    public TuberculoseCoorte(boolean isDbf, boolean situacaoEncerramento) {
        this.setDBF(isDbf);
        setPeriodo("de Diagnóstico");
        setTipoAgregacao("de Residência Atual");
        init("postgres");
        setSituacaoEncerramento(situacaoEncerramento);
        if(!situacaoEncerramento){
            this.setRodape("NOTA DE RODAPÉ: \n1) Mudança de esquema por intolerância, falência, " + "continua em tratamento e tb multiressistente estão somadas em OUTRAS CATEGORIAS e " + "podem ser avaliadas em separado utilizando o Tabwin. \n2) Os casos de tuberculose " + "meningoencefálica estão excluídos da análise da situação até o 9° mês. ");
        }else{
            this.setRodape("");
        }
    }

    public TuberculoseCoorte() {
    }

    public void init(String tipoBanco) {

        this.setMultiplicador(100000);
        this.setTipo("tube");
//        this.setRodape("NOTA DE RODAPÉ: \n1) Mudança de esquema por intolerância, falência, " + "continua em tratamento e tb multiressistente estão somadas em OUTRAS CATEGORIAS e " + "podem ser avaliadas em separado utilizando o Tabwin. \n2) Os casos de tuberculose " + "meningoencefálica estão excluídos da análise da situação até o 9° mês. ");
        this.setSqlNumeradorCompletitude("");
        this.setCura("0");
        this.setAbandono("0");
        this.setTransfMesmoMunicipio("0");
        this.setTransfOutroMunicipio("0");
        this.setTransfOutroPais("0");
        this.setTransfOutroUf("0");
        this.setErroDiagnostico("0");
        this.setObitoPorTuberculose("0");
        this.setObitoComTuberculose("0");
        this.setNaoPreenchido("0");
        this.setSubTotal("0");
        this.setTransfNaoEspecificada("0");
        this.setOutraCategoria("0");
    }
    public static String formataData(String data) {
        String[] d = data.split("-");
        return d[2] + "/" + d[1] + "/" + d[0];
    }
    public void populaDatas(Map parametros) {
        dtFinal = formataData(parametros.get("parDataFim1").toString());
        dtInicial = formataData(parametros.get("parDataInicio1").toString());
        
    }

    private void calculaBrasil(DBFReader reader, Map parametros) throws ParseException {
        populaDatas(parametros);
        //buscar os municipios que vao para o resultado
        HashMap<String, TuberculoseCoorte> municipiosBeans = new HashMap<String, TuberculoseCoorte>();
        DBFUtil utilDbf = new DBFUtil();
        String coluna;
        if (parametros.get("municipios").toString().equals("sim")) {
            municipiosBeans = populaMunicipiosBeansTuberc("BR", "");
            coluna = "ID_MUNIC_2";
        } else {
            municipiosBeans = populaUfsBeansTubec();
            coluna = "SG_UF_2";
        }

        //inicia o calculo

        String modoEntrada;
        Date dtDiagnostico;
        String tipoAlta;
        Object[] rowObjects;

        TuberculoseCoorte municipioResidencia;
        String dataInicio1 = (String) parametros.get("parDataInicio1");
        String dataFim1 = (String) parametros.get("parDataFim1");
        String parTpForma1 = (String) parametros.get("parTpForma1");
        String parBaciloscopia = (String) parametros.get("parBaciloscopia");
        String parTpExtrapulmonar1 = (String) parametros.get("parTpExtrapulmonar1");

        String tpForma = "";
        String baciloscopia1 = "";
        String baciloscopia2 = "";
        String tpExtrapulmonar1 = "";
        String tpExtrapulmonar2 = "";
        boolean isOk;
        boolean controleBaciloscopia;

        //loop para ler os arquivos selecionados
        String[] arquivos = parametros.get("parArquivos").toString().split("\\|\\|");
        for (int k = 0; k < arquivos.length; k++) {
            int i = 1;
            try {
                reader = Util.retornaObjetoDbfCaminhoArquivo(arquivos[k].substring(0, arquivos[k].length() - 4), Configuracao.getPropriedade("caminho"));
                utilDbf.mapearPosicoes(reader);
                double TotalRegistros = Double.parseDouble(String.valueOf(reader.getRecordCount()));
                while ((rowObjects = reader.nextRecord()) != null) {
                    isOk = true;
                    controleBaciloscopia = true;
                    //cálculo da taxa estadual
                    //verifica a uf de residencia
                    if (utilDbf.getString(rowObjects, coluna) != null) {
                        //verifica se existe a referencia do municipio no bean
                        municipioResidencia = municipiosBeans.get(utilDbf.getString(rowObjects, coluna));
                        modoEntrada = utilDbf.getString(rowObjects, "TRATAMENTO", 1);
                        dtDiagnostico = utilDbf.getDate(rowObjects, "DT_DIAG");
                        if (municipioResidencia != null) {
                            if (modoEntrada != null) {
                                if (modoEntrada.equals("1") || modoEntrada.equals("4")) {
                                    if (isBetweenDates(dtDiagnostico, dataInicio1, dataFim1)) {
                                        if (parTpForma1 != null) {
                                            tpForma = utilDbf.getString(rowObjects, "FORMA", 1);
                                            if (tpForma == null) {
                                                isOk = false;
                                            } else {
                                                if (!tpForma.equals("1") && !tpForma.equals("3")) {
                                                    isOk = false;
                                                }
                                            }
                                        }
                                        if (parBaciloscopia != null) {
                                            baciloscopia1 = utilDbf.getString(rowObjects, "BACILOSC_E", 1);
                                            baciloscopia2 = utilDbf.getString(rowObjects, "BACILOS_E2", 1);
                                            if (baciloscopia1 == null && baciloscopia2 == null) {
                                                controleBaciloscopia = false;
                                            } else {
                                                if (baciloscopia1 != null && baciloscopia2 == null) {
                                                    if (!baciloscopia1.equals("1")) {
                                                        controleBaciloscopia = false;
                                                    }
                                                } else {
                                                    if (baciloscopia2 != null && baciloscopia1 == null) {
                                                        if (!baciloscopia2.equals("1")) {
                                                            controleBaciloscopia = false;
                                                        }
                                                    } else {
                                                        if (baciloscopia2.equals("1") || baciloscopia1.equals("1")) {
                                                            controleBaciloscopia = true;
                                                        } else {
                                                            controleBaciloscopia = false;
                                                        }
                                                    }
                                                }
                                            }


                                        }
                                        if (parTpExtrapulmonar1 != null) {
                                            tpExtrapulmonar1 = utilDbf.getString(rowObjects, "EXTRAPU1_N", 2);
                                            tpExtrapulmonar2 = utilDbf.getString(rowObjects, "EXTRAPU2_N", 2);

                                            if (tpExtrapulmonar1 != null) {
                                                if (tpExtrapulmonar1.equals("7")) {
                                                    isOk = false;
                                                }
                                            }
                                            if (tpExtrapulmonar2 != null) {
                                                if (tpExtrapulmonar2.equals("7")) {
                                                    isOk = false;
                                                }
                                            }
                                        }
                                        //busca o tipo de alta
                                        if (isOk && controleBaciloscopia) {
                                            if (parametros.get("parGroup").toString().equals("tp_situacao_encerramento")) {
                                                tipoAlta = utilDbf.getString(rowObjects, "SITUA_ENCE", 2);
                                                municipioResidencia = preencheValoresEncerramento(tipoAlta, municipioResidencia);
                                            } else {
                                                tipoAlta = utilDbf.getString(rowObjects, "SITUA_9_M", 2);
                                                municipioResidencia = classificaAlta(tipoAlta, municipioResidencia);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    float percentual = Float.parseFloat(String.valueOf(i)) / Float.parseFloat(String.valueOf(TotalRegistros)) * 100;
                    getBarraStatus().setValue((int) percentual);
                    i++;
                }

            } catch (DBFException ex) {
                Master.mensagem("Erro:\n" + ex);
            }
        }

        //CALCULA A TAXA PARA TODOS OS MUNICIPIOS
        this.setBeans(new ArrayList());
        Collection<TuberculoseCoorte> municipioBean = municipiosBeans.values();

        for (Iterator<TuberculoseCoorte> it = municipioBean.iterator(); it.hasNext();) {
            TuberculoseCoorte agravoDBF = it.next();
            this.getBeans().add(insereNoBean(agravoDBF));
        }
        Collections.sort(this.getBeans(), new BeanComparator("nomeMunicipio"));
    }

    @Override
    public List getBeanMunicipioEspecifico(Connection con, Map parametros) throws SQLException {
        populaDatas(parametros);
        if (isDBF()) {
            return this.getBeans();
        } else {
            this.init("");
            ResultSet rs2;
            String municipio, noMunicipio = null;
            List beans = new ArrayList();
            Hanseniase d1 = null;
            DecimalFormat df = new DecimalFormat("0.00");

            municipio = parametros.get("parMunicipio").toString();
            noMunicipio = parametros.get("parNomeMunicipio").toString();
            String sql1 = "select distinct(" + parametros.get("parGroup").toString() + ") as alta,count(*) as c " + " from dbsinan.tb_investiga_tuberculose t1 inner join dbsinan.tb_notificacao t2 on (t1.nu_notificacao=t2.nu_notificacao and t1.dt_notificacao=t2.dt_notificacao and t1.co_municipio_notificacao=t2.co_municipio_notificacao)  " + " where t2.co_cid='A16.9'  and  t1.co_municipio_residencia_atual= ? and " + "(tp_entrada=1 or tp_entrada=4)  " + parametros.get("parSqlTuberculose").toString() + parametros.get("parSqlTuberculose9mes").toString() + "   dt_diagnostico_sintoma between ? and ? and ( tp_duplicidade is null or tp_duplicidade=1  ) " + " group by " + parametros.get("parGroup").toString() + "";

            PreparedStatement stm2;
            try {
                stm2 = con.prepareStatement(sql1);
                stm2.setString(1, municipio);
                stm2.setString(2, parametros.get("parDataInicio1").toString());
                stm2.setString(3, parametros.get("parDataFim1").toString());
                rs2 = stm2.executeQuery();
            } catch (Exception exception) {
                sql1 = "select distinct(" + parametros.get("parGroup").toString() + ") as alta,count(*) as c " + " from tb_investiga_tuberculose t1 inner join tb_notificacao t2 on (t1.nu_notificacao=t2.nu_notificacao and t1.dt_notificacao=t2.dt_notificacao and t1.co_municipio_notificacao=t2.co_municipio_notificacao)  " + " where t2.co_cid='A16.9' and  t1.co_municipio_residencia_atual= '" + municipio + "' and " + "(tp_entrada=1 or tp_entrada=4)  " + parametros.get("parSqlTuberculose").toString() + parametros.get("parSqlTuberculose9mes").toString() + "   dt_diagnostico_sintoma between '" + parametros.get("parDataInicio1").toString() + "' and '" + parametros.get("parDataFim1").toString() + "' and ( tp_duplicidade is null or tp_duplicidade=1  ) " + " group by " + parametros.get("parGroup").toString() + "";
                System.out.println(sql1);
                stm2 = con.prepareStatement(sql1);
                rs2 = stm2.executeQuery();
            }
            if (parametros.get("parGroup").toString().equals("tp_situacao_encerramento")) {
                this.preencheValoresEncerramento(rs2);
            } else {
                this.preencheValores(rs2);
            }
            beans.add(insereNoBean(municipio, noMunicipio));
            System.out.println("terminou");
            return beans;
        }

    }

    @Override
    public List getBeanEstadoEspecifico(Connection con, Map parametros) throws SQLException {
        populaDatas(parametros);
        if (isDBF()) {
            return getBeans();
        } else {
            this.init("");
            ResultSet rs2;
            String uf, noUF = null;
            List beans = new ArrayList();
            Hanseniase d1 = null;
            DecimalFormat df = new DecimalFormat("0.00");

            uf = parametros.get("parUf").toString();
            noUF = parametros.get("parSgUf").toString();
            String sql1 = "select distinct(" + parametros.get("parGroup").toString() + ") as alta,count(*) as c " + " from dbsinan.tb_investiga_tuberculose t1 inner join dbsinan.tb_notificacao t2 on (t1.nu_notificacao=t2.nu_notificacao and t1.dt_notificacao=t2.dt_notificacao and t1.co_municipio_notificacao=t2.co_municipio_notificacao)  " + " where t2.co_cid='A16.9' and  t1.co_uf_residencia_atual= ? and " + "(tp_entrada=1 or tp_entrada=4)  " + parametros.get("parSqlTuberculose").toString() + parametros.get("parSqlTuberculose9mes").toString() + "   dt_diagnostico_sintoma between ? and ? and ( tp_duplicidade is null or tp_duplicidade=1  ) " + " group by " + parametros.get("parGroup").toString() + "";

            PreparedStatement stm2;
            try {
                stm2 = con.prepareStatement(sql1);
                stm2.setString(1, uf);
//                System.out.println(sql1);
//                System.out.println(uf);
//                System.out.println(parametros.get("parDataInicio1").toString());
//                System.out.println(parametros.get("parDataFim1").toString());
                //calcula para PB
                stm2.setString(2, parametros.get("parDataInicio1").toString());
                stm2.setString(3, parametros.get("parDataFim1").toString());
                rs2 = stm2.executeQuery();
            } catch (Exception exception) {
                sql1 = "select distinct(" + parametros.get("parGroup").toString() + ") as alta,count(*) as c " + " from tb_investiga_tuberculose t1 inner join tb_notificacao t2 on (t1.nu_notificacao=t2.nu_notificacao and t1.dt_notificacao=t2.dt_notificacao and t1.co_municipio_notificacao=t2.co_municipio_notificacao)  " + " where t2.co_cid='A16.9' and  t1.co_uf_residencia_atual= " + uf + "  and " + "(tp_entrada=1 or tp_entrada=4)  " + parametros.get("parSqlTuberculose").toString() + parametros.get("parSqlTuberculose9mes").toString() + "   dt_diagnostico_sintoma between '" + parametros.get("parDataInicio1").toString() + "' and '" + parametros.get("parDataFim1").toString() + "' and ( tp_duplicidade is null or tp_duplicidade=1  ) " + " group by " + parametros.get("parGroup").toString() + "";

                stm2 = con.prepareStatement(sql1);
                rs2 = stm2.executeQuery();
            }
            if (parametros.get("parGroup").toString().equals("tp_situacao_encerramento")) {
                this.preencheValoresEncerramento(rs2);
            } else {
                this.preencheValores(rs2);
            }
            beans.add(insereNoBean(uf, noUF));
            System.out.println("terminou");

            return beans;
        }

    }

    @Override
    public List getBeanMunicipios(Connection con, Map parametros) throws SQLException {
       populaDatas(parametros);
        if (isDBF()) {
            return getBeans();
        } else {
            String sql;
            java.sql.Statement stm = con.createStatement();
            ResultSet rs, rs2;
            String municipio, noMunicipio = null;
            List beans = new ArrayList();
            Hanseniase d1 = null;
            if (parametros.get("parNomeRegional").equals("Todas Regionais") || parametros.get("parNomeRegional").equals("-- Selecione --")) {
                sql = "select co_municipio_ibge,no_municipio from dbgeral.tb_municipio where sg_uf = '" + parametros.get("parSgUf") + "' order by no_municipio";
            } else {
                sql = "select t1.co_municipio_ibge,no_municipio from dbgeral.tb_municipio as t1, dblocalidade.rl_regional_municipio_svs as t2 where t2.co_uf_ibge=" + parametros.get("parUf") + " and t1.co_municipio_ibge=t2.co_municipio_ibge and co_regional = '" + parametros.get("parCodRegional") + "' and no_municipio not like '%Ignorado%'  order by no_municipio";
            }
            try {
                rs = stm.executeQuery(sql);
            } catch (Exception exception) {
                if (parametros.get("parNomeRegional").equals("Todas Regionais") || parametros.get("parNomeRegional").equals("-- Selecione --")) {
                    sql = "select co_municipio_ibge,no_municipio from dbgeral.tb_municipio where sg_uf = '" + parametros.get("parSgUf") + "' order by no_municipio";
                } else {
                    sql = "select t1.co_municipio_ibge,no_municipio from dbgeral.tb_municipio as t1,  dblocalidade.rl_regional_municipio_svs as t2 where t2.co_uf_ibge=" + parametros.get("parUf") + " and t1.co_municipio_ibge=t2.co_municipio_ibge and co_regional = '" + parametros.get("parCodRegional") + "' and no_municipio not like '%Ignorado%'  order by no_municipio";
                }
                rs = stm.executeQuery(sql);
            }
            this.init("");
            while (rs.next()) {
                this.init("");
                municipio = rs.getString("co_municipio_ibge");
                noMunicipio = rs.getString("no_municipio");

                String sql1 = "select distinct(" + parametros.get("parGroup").toString() + ") as alta,count(*) as c " + " from dbsinan.tb_investiga_tuberculose t1 inner join dbsinan.tb_notificacao t2 on (t1.nu_notificacao=t2.nu_notificacao and t1.dt_notificacao=t2.dt_notificacao and t1.co_municipio_notificacao=t2.co_municipio_notificacao)  " + " where t2.co_cid='A16.9' and  t1.co_municipio_residencia_atual= ? and " + "(tp_entrada=1 or tp_entrada=4)  " + parametros.get("parSqlTuberculose").toString() + parametros.get("parSqlTuberculose9mes").toString() + "   dt_diagnostico_sintoma between ? and ? and ( tp_duplicidade is null or tp_duplicidade=1  ) " + " group by " + parametros.get("parGroup").toString() + "";
                PreparedStatement stm2;
                try {
                    stm2 = con.prepareStatement(sql1);
                    stm2.setString(1, municipio);
                    //calcula para PB
                    stm2.setString(2, parametros.get("parDataInicio1").toString());
                    stm2.setString(3, parametros.get("parDataFim1").toString());
                    rs2 = stm2.executeQuery();
                } catch (Exception exception) {
                    sql1 = "select distinct(" + parametros.get("parGroup").toString() + ") as alta,count(*) as c " + " from dbsinan.tb_investiga_tuberculose t1 inner join dbsinan.tb_notificacao t2 on (t1.nu_notificacao=t2.nu_notificacao and t1.dt_notificacao=t2.dt_notificacao and t1.co_municipio_notificacao=t2.co_municipio_notificacao)  " + " where t2.co_cid='A16.9' and  t1.co_municipio_residencia_atual= '" + municipio + "' and " + "(tp_entrada=1 or tp_entrada=4)  " + parametros.get("parSqlTuberculose").toString() + parametros.get("parSqlTuberculose9mes").toString() + "   dt_diagnostico_sintoma between '" + parametros.get("parDataInicio1").toString() + "' and '" + parametros.get("parDataFim1").toString() + "' and ( tp_duplicidade is null or tp_duplicidade=1  ) " + " group by " + parametros.get("parGroup").toString() + "";
                    stm2 = con.prepareStatement(sql1);
                    rs2 = stm2.executeQuery();
                }
                if (parametros.get("parGroup").toString().equals("tp_situacao_encerramento")) {
                    this.preencheValoresEncerramento(rs2);
                } else {
                    this.preencheValores(rs2);
                }
                beans.add(insereNoBean(municipio, noMunicipio));
            }
            System.out.println("terminou");

            return beans;
        }

    }

    private void calculaMunicipios(DBFReader reader, Map parametros) throws ParseException {
        populaDatas(parametros);
        //buscar os municipios que vao para o resultado
        HashMap<String, TuberculoseCoorte> municipiosBeans = new HashMap<String, TuberculoseCoorte>();
        String ufResidencia = (String) parametros.get("parUf");
        String sgUfResidencia = (String) parametros.get("parSgUf");
        String codRegional = (String) parametros.get("parCodRegional");
        DBFUtil utilDbf = new DBFUtil();
        if (codRegional == null) {
            codRegional = "";
        }

        municipiosBeans = populaMunicipiosBeansTuberc(sgUfResidencia, codRegional);

        //inicia o calculo

        String modoEntrada;
        Date dtDiagnostico;
        String classificacaoOperacionalAtual;
        String tipoAlta;
        Object[] rowObjects;
        DecimalFormat df = new DecimalFormat("0.00");

        TuberculoseCoorte municipioResidencia;
        String dataInicio1 = (String) parametros.get("parDataInicio1");
        String dataFim1 = (String) parametros.get("parDataFim1");
        String parTpForma1 = (String) parametros.get("parTpForma1");
        String parTpForma2 = (String) parametros.get("parTpForma2");
        String parBaciloscopia = (String) parametros.get("parBaciloscopia");
        String parBaciloscopia2 = (String) parametros.get("parBaciloscopia2");
        String parTpExtrapulmonar1 = (String) parametros.get("parTpExtrapulmonar1");
        String parTpExtrapulmonar2 = (String) parametros.get("parTpExtrapulmonar2");

        String tpForma = "";
        String baciloscopia1 = "";
        String baciloscopia2 = "";
        String tpExtrapulmonar1 = "";
        String tpExtrapulmonar2 = "";
        boolean isOk;
        boolean controleBaciloscopia;

        //loop para ler os arquivos selecionados
        String[] arquivos = parametros.get("parArquivos").toString().split("\\|\\|");
        for (int k = 0; k < arquivos.length; k++) {
            int i = 1;
            try {
                reader = Util.retornaObjetoDbfCaminhoArquivo(arquivos[k].substring(0, arquivos[k].length() - 4), Configuracao.getPropriedade("caminho"));
                utilDbf.mapearPosicoes(reader);
                double TotalRegistros = Double.parseDouble(String.valueOf(reader.getRecordCount()));
                while ((rowObjects = reader.nextRecord()) != null) {
                    isOk = true;
                    controleBaciloscopia = true;
                    //cálculo da taxa estadual
                    //verifica a uf de residencia
                    if (utilDbf.getString(rowObjects, "SG_UF_2") != null) {
                        //verifica se existe a referencia do municipio no bean
                        municipioResidencia = municipiosBeans.get(utilDbf.getString(rowObjects, "ID_MUNIC_2"));
                        modoEntrada = utilDbf.getString(rowObjects, "TRATAMENTO", 1);
                        dtDiagnostico = utilDbf.getDate(rowObjects, "DT_DIAG");
                        if (municipioResidencia != null) {
                            if (modoEntrada != null) {
                                if (modoEntrada.equals("1") || modoEntrada.equals("4")) {
                                    if (isBetweenDates(dtDiagnostico, dataInicio1, dataFim1)) {
                                        if (parTpForma1 != null) {
                                            tpForma = utilDbf.getString(rowObjects, "FORMA", 1);
                                            if (!tpForma.equals("1") && !tpForma.equals("3")) {
                                                isOk = false;
                                            }
                                        }
                                        if (parBaciloscopia != null) {
                                            baciloscopia1 = utilDbf.getString(rowObjects, "BACILOSC_E", 1);
                                            baciloscopia2 = utilDbf.getString(rowObjects, "BACILOS_E2", 1);
                                            if (baciloscopia1 == null && baciloscopia2 == null) {
                                                controleBaciloscopia = false;
                                            } else {
                                                if (baciloscopia1 != null && baciloscopia2 == null) {
                                                    if (!baciloscopia1.equals("1")) {
                                                        controleBaciloscopia = false;
                                                    }
                                                } else {
                                                    if (baciloscopia2 != null && baciloscopia1 == null) {
                                                        if (!baciloscopia2.equals("1")) {
                                                            controleBaciloscopia = false;
                                                        }
                                                    } else {
                                                        if (baciloscopia2.equals("1") || baciloscopia1.equals("1")) {
                                                            controleBaciloscopia = true;
                                                        } else {
                                                            controleBaciloscopia = false;
                                                        }
                                                    }
                                                }
                                            }


                                        }
                                        if (parTpExtrapulmonar1 != null) {
                                            tpExtrapulmonar1 = utilDbf.getString(rowObjects, "EXTRAPU1_N", 2);
                                            tpExtrapulmonar2 = utilDbf.getString(rowObjects, "EXTRAPU2_N", 2);

                                            if (tpExtrapulmonar1 != null) {
                                                if (tpExtrapulmonar1.equals("7")) {
                                                    isOk = false;
                                                }
                                            }
                                            if (tpExtrapulmonar2 != null) {
                                                if (tpExtrapulmonar2.equals("7")) {
                                                    isOk = false;
                                                }
                                            }
                                        }
                                        //busca o tipo de alta
                                        if (isOk && controleBaciloscopia) {
                                            if (parametros.get("parGroup").toString().equals("tp_situacao_encerramento")) {
                                                tipoAlta = utilDbf.getString(rowObjects, "SITUA_ENCE", 2);
                                                municipioResidencia = preencheValoresEncerramento(tipoAlta, municipioResidencia);
                                            } else {
                                                tipoAlta = utilDbf.getString(rowObjects, "SITUA_9_M", 2);
                                                municipioResidencia = classificaAlta(tipoAlta, municipioResidencia);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    float percentual = Float.parseFloat(String.valueOf(i)) / Float.parseFloat(String.valueOf(TotalRegistros)) * 100;
                    getBarraStatus().setValue((int) percentual);
                    i++;
                }

            } catch (DBFException ex) {
                Master.mensagem("Erro:\n" + ex);
            }
        }

        //CALCULA A TAXA PARA TODOS OS MUNICIPIOS
        this.setBeans(new ArrayList());
        Collection<TuberculoseCoorte> municipioBean = municipiosBeans.values();

        for (Iterator<TuberculoseCoorte> it = municipioBean.iterator(); it.hasNext();) {
            TuberculoseCoorte agravoDBF = it.next();
            this.getBeans().add(insereNoBean(agravoDBF));
        }
        Collections.sort(this.getBeans(), new BeanComparator("nomeMunicipio"));
    }

    @Override
    public void calcula(DBFReader reader, Map parametros) {
        populaDatas(parametros);
        init("");
        String municipios = (String) parametros.get("municipios");

        String brasil = (String) parametros.get("parUf");
        if (municipios.equals("sim") && !brasil.equals("brasil")) {
            try {
                calculaMunicipios(reader, parametros);
            } catch (ParseException ex) {
                System.out.println(ex);
            }
        } else {
            try {
                if (brasil.equals("brasil")) {
                    calculaBrasil(reader, parametros);
                } else {
                    Object[] rowObjects;
                    DBFUtil utilDbf = new DBFUtil();
                    String modoEntrada;
                    Date dtDiagnostico;
                    String tipoAlta;
                    String ufResidencia = (String) parametros.get("parUf");
                    String municipioResidencia = (String) parametros.get("parMunicipio");
                    if (municipioResidencia == null) {
                        municipioResidencia = "";
                    }
                    String dataInicio1 = (String) parametros.get("parDataInicio1");
                    String dataFim1 = (String) parametros.get("parDataFim1");
                    String parTpForma1 = (String) parametros.get("parTpForma1");
                    String parBaciloscopia = (String) parametros.get("parBaciloscopia");
                    String parTpExtrapulmonar1 = (String) parametros.get("parTpExtrapulmonar1");

                    String tpForma = "";
                    String baciloscopia1 = "";
                    String baciloscopia2 = "";
                    String tpExtrapulmonar1 = "";
                    String tpExtrapulmonar2 = "";
                    boolean isOk;
                    boolean controleBaciloscopia;
                    //loop para ler os arquivos selecionados
                    String[] arquivos = parametros.get("parArquivos").toString().split("\\|\\|");
                    for (int k = 0; k < arquivos.length; k++) {
                        int i = 1;
                        reader = Util.retornaObjetoDbfCaminhoArquivo(arquivos[k].substring(0, arquivos[k].length() - 4), Configuracao.getPropriedade("caminho"));
                        utilDbf.mapearPosicoes(reader);
                        double TotalRegistros = Double.parseDouble(String.valueOf(reader.getRecordCount()));
                        while ((rowObjects = reader.nextRecord()) != null) {
                            isOk = true;
                            controleBaciloscopia = true;
                            //cálculo da taxa estadual
                            //verifica a uf de residencia ATUAL
                            if (utilDbf.getString(rowObjects, "SG_UF_2") != null) {
                                if (utilDbf.getString(rowObjects, "SG_UF_2").equals(ufResidencia)) {
                                    //verifica se tem o parametro de municipio de residencia
                                    modoEntrada = utilDbf.getString(rowObjects, "TRATAMENTO", 1);
                                    dtDiagnostico = utilDbf.getDate(rowObjects, "DT_DIAG");
                                    if (verificaMunicipio(municipioResidencia, utilDbf.getString(rowObjects, "ID_MUNIC_2"))) {
                                        //calcula PB
                                        if (modoEntrada != null) {
                                            if (modoEntrada.equals("1") || modoEntrada.equals("4")) {
                                                if (isBetweenDates(dtDiagnostico, dataInicio1, dataFim1)) {
                                                    if (parTpForma1 != null) {
                                                        tpForma = utilDbf.getString(rowObjects, "FORMA", 1);
                                                        if (!tpForma.equals("1") && !tpForma.equals("3")) {
                                                            isOk = false;
                                                        }
                                                    }
                                                    if (parBaciloscopia != null) {
                                                        baciloscopia1 = utilDbf.getString(rowObjects, "BACILOSC_E", 1);
                                                        baciloscopia2 = utilDbf.getString(rowObjects, "BACILOS_E2", 1);
                                                        if (baciloscopia1 == null && baciloscopia2 == null) {
                                                            controleBaciloscopia = false;
                                                        } else {
                                                            if (baciloscopia1 != null && baciloscopia2 == null) {
                                                                if (!baciloscopia1.equals("1")) {
                                                                    controleBaciloscopia = false;
                                                                }
                                                            } else {
                                                                if (baciloscopia2 != null && baciloscopia1 == null) {
                                                                    if (!baciloscopia2.equals("1")) {
                                                                        controleBaciloscopia = false;
                                                                    }
                                                                } else {
                                                                    if (baciloscopia2.equals("1") || baciloscopia1.equals("1")) {
                                                                        controleBaciloscopia = true;
                                                                    } else {
                                                                        controleBaciloscopia = false;
                                                                    }
                                                                }
                                                            }
                                                        }


                                                    }
                                                    if (parTpExtrapulmonar1 != null) {
                                                        tpExtrapulmonar1 = utilDbf.getString(rowObjects, "EXTRAPU1_N", 2);
                                                        tpExtrapulmonar2 = utilDbf.getString(rowObjects, "EXTRAPU2_N", 2);

                                                        if (tpExtrapulmonar1 != null) {
                                                            if (tpExtrapulmonar1.equals("7")) {
                                                                isOk = false;
                                                            }
                                                        }
                                                        if (tpExtrapulmonar2 != null) {
                                                            if (tpExtrapulmonar2.equals("7")) {
                                                                isOk = false;
                                                            }
                                                        }
                                                    }
                                                    //verifica se está vinculado
//                                                if (utilDbf.getString(rowObjects, "IN_VINCULA")!=null) {
//                                                    if (utilDbf.getString(rowObjects, "IN_VINCULA").equals("1")) {
//
//                                                        isOk = true;
//                                                    }
//
//                                                }
                                                    //busca o tipo de alta
                                                    if (isOk && controleBaciloscopia) {
                                                        if (parametros.get("parGroup").toString().equals("tp_situacao_encerramento")) {
                                                            tipoAlta = utilDbf.getString(rowObjects, "SITUA_ENCE", 2);
                                                            preencheValoresEncerramento(tipoAlta);
                                                        } else {
                                                            tipoAlta = utilDbf.getString(rowObjects, "SITUA_9_M", 2);
//                                                        if(tipoAlta == null)
//                                                            //if(tipoAlta.equals("5"))
//                                                            System.out.println(utilDbf.getString(rowObjects, "NU_NOTIFIC"));
//                                                        if(tipoAlta != null )
//                                                            if(tipoAlta.equals(""))
//                                                                System.out.println(utilDbf.getString(rowObjects, "NU_NOTIFIC"));
                                                            classificaAlta(tipoAlta);
                                                        }

                                                    }
                                                }
                                            }
                                        }

                                    }
                                }
                            }
                            float percentual = Float.parseFloat(String.valueOf(i)) / Float.parseFloat(String.valueOf(TotalRegistros)) * 100;
                            getBarraStatus().setValue((int) percentual);
                            i++;
                        }
                    }

                    this.setBeans(new ArrayList());
                    String nomeElemento;
                    if (municipioResidencia.equals("")) {
                        nomeElemento = (String) parametros.get("parSgUf");
                    } else {
                        nomeElemento = (String) parametros.get("parNomeMunicipio");
                    }
                    this.getBeans().add(insereNoBean(municipioResidencia, nomeElemento));
                }

            } catch (NumberFormatException ex) {
                System.out.println(ex);
            } catch (ParseException ex) {
                System.out.println(ex);
            } catch (DBFException ex) {
                System.out.println(ex);
            }
        }
    }

    @Override
    public List getBeansEstadoEspecifico(Connection con, Map parametros) throws SQLException {
        populaDatas(parametros);
        if (isDBF()) {
            return getBeans();
        } else {
            if (parametros.get("municipios").equals("nao")) {
                return this.getBeanEstadoEspecifico(con, parametros);
            } else {
                return this.getBeanMunicipios(con, parametros);
            }
        }

    }

    @Override
    public List getBeansMunicipioEspecifico(Connection con, Map parametros) throws SQLException {
       populaDatas(parametros);
        if (isDBF()) {
            return getBeans();
        } else {
            return this.getBeanMunicipioEspecifico(con, parametros);
        }

    }

    public void preencheValores(ResultSet rs2) throws SQLException {
        int temp = 0;
        String tipoAlta;
        while (rs2.next()) {
            tipoAlta = rs2.getString("alta");
            temp = rs2.getInt("c");
            if (tipoAlta != null && !tipoAlta.equals("")) {
                this.setSubTotal(String.valueOf(temp + Integer.parseInt(getSubTotal())));
                if (tipoAlta.equals("1")) {
                    temp = temp + Integer.parseInt(getCura());
                    this.setCura(String.valueOf(temp));
                }
                if (tipoAlta.equals("5")) {
                    temp = temp + Integer.parseInt(getTransfMesmoMunicipio());
                    this.setTransfMesmoMunicipio(String.valueOf(temp));
                }
                if (tipoAlta.equals("6")) {
                    temp = temp + Integer.parseInt(getTransfOutroMunicipio());
                    this.setTransfOutroMunicipio(String.valueOf(temp));
                }
                if (tipoAlta.equals("7")) {
                    temp = temp + Integer.parseInt(getTransfOutroUf());
                    this.setTransfOutroUf(String.valueOf(temp));
                }
                if(tipoAlta.equals("8")){
                    temp = temp + Integer.parseInt(getTransfOutroPais());
                    this.setTransfOutroPais(String.valueOf(temp));
                }
                if (tipoAlta.equals("9") || tipoAlta.equals("11") || tipoAlta.equals("12") || tipoAlta.equals("13")) {
                    temp = temp + Integer.parseInt(getOutraCategoria());
                    this.setOutraCategoria(String.valueOf(temp));
                }
                //obito por tub
                if (tipoAlta.equals("3")) {
                    temp = temp + Integer.parseInt(getObitoPorTuberculose());
                    this.setObitoPorTuberculose(String.valueOf(temp));
                }
                //obito com tub
                if (tipoAlta.equals("4")) {
                    temp = temp + Integer.parseInt(getObitoComTuberculose());
                    this.setObitoComTuberculose(String.valueOf(temp));
                }
                if (tipoAlta.equals("2")) {
                    temp = temp + Integer.parseInt(getAbandono());
                    this.setAbandono(String.valueOf(temp));
                }
                if (tipoAlta.equals("10")) {
                    this.setSubTotal(String.valueOf(Integer.parseInt(getSubTotal()) - temp));
                    temp = temp + Integer.parseInt(getErroDiagnostico());
                    this.setErroDiagnostico(String.valueOf(temp));
                }
            } else {
                this.setSubTotal(String.valueOf(Integer.parseInt(getSubTotal()) + temp));
                temp = temp + Integer.parseInt(getNaoPreenchido());
                this.setNaoPreenchido(String.valueOf(temp));
            }
        }
    }

    public void preencheValoresEncerramento(ResultSet rs2) throws SQLException {
        int temp = 0;
        String tipoAlta;
        while (rs2.next()) {
            tipoAlta = rs2.getString("alta");
            temp = rs2.getInt("c");
            if (tipoAlta != null && !tipoAlta.equals("")) {
                this.setSubTotal(String.valueOf(temp + Integer.parseInt(getSubTotal())));
                if (tipoAlta.equals("1")) {
                    temp = temp + Integer.parseInt(getCura());
                    this.setCura(String.valueOf(temp));
                }
                if (tipoAlta.equals("5")) {
                    temp = temp + Integer.parseInt(getTransfMesmoMunicipio());
                    this.setTransfMesmoMunicipio(String.valueOf(temp));
                }
                if (tipoAlta.equals("7")) {
                    temp = temp + Integer.parseInt(getTransfOutroUf());
                    this.setTransfOutroUf(String.valueOf(temp));
                }

                //obito por tub
                if (tipoAlta.equals("3")) {
                    temp = temp + Integer.parseInt(getObitoPorTuberculose());
                    this.setObitoPorTuberculose(String.valueOf(temp));
                }
                //obito com tub
                if (tipoAlta.equals("4")) {
                    temp = temp + Integer.parseInt(getObitoComTuberculose());
                    this.setObitoComTuberculose(String.valueOf(temp));
                }
                if (tipoAlta.equals("2")) {
                    temp = temp + Integer.parseInt(getAbandono());
                    this.setAbandono(String.valueOf(temp));
                }
                if (tipoAlta.equals("6")) {
                    this.setSubTotal(String.valueOf(Integer.parseInt(getSubTotal()) - temp));
                    temp = temp + Integer.parseInt(getErroDiagnostico());
                    this.setErroDiagnostico(String.valueOf(temp));
                }
            } else {
                this.setSubTotal(String.valueOf(Integer.parseInt(getSubTotal()) + temp));
                temp = temp + Integer.parseInt(getNaoPreenchido());
                this.setNaoPreenchido(String.valueOf(temp));
            }
        }
    }

    private TuberculoseCoorte preencheValoresEncerramento(String tipoAlta, TuberculoseCoorte municipioResidencia) {
        int temp = 1;
        if (tipoAlta != null && !tipoAlta.equals("")) {
            municipioResidencia.setSubTotal(String.valueOf(temp + Integer.parseInt(municipioResidencia.getSubTotal())));
            if (tipoAlta.equals("1")) {
                temp = temp + Integer.parseInt(municipioResidencia.getCura());
                municipioResidencia.setCura(String.valueOf(temp));
            }
            if (tipoAlta.equals("5")) {
                temp = temp + Integer.parseInt(municipioResidencia.getTransfMesmoMunicipio());
                municipioResidencia.setTransfMesmoMunicipio(String.valueOf(temp));
            }
            if (tipoAlta.equals("7")) {
                temp = temp + Integer.parseInt(municipioResidencia.getTransfOutroUf());
                municipioResidencia.setTransfOutroUf(String.valueOf(temp));
            }

            //obito por tub
            if (tipoAlta.equals("3")) {
                temp = temp + Integer.parseInt(municipioResidencia.getObitoPorTuberculose());
                municipioResidencia.setObitoPorTuberculose(String.valueOf(temp));
            }
            //obito com tub
            if (tipoAlta.equals("4")) {
                temp = temp + Integer.parseInt(municipioResidencia.getObitoComTuberculose());
                municipioResidencia.setObitoComTuberculose(String.valueOf(temp));
            }
            if (tipoAlta.equals("2")) {
                temp = temp + Integer.parseInt(municipioResidencia.getAbandono());
                municipioResidencia.setAbandono(String.valueOf(temp));
            }
            if (tipoAlta.equals("6")) {
                municipioResidencia.setSubTotal(String.valueOf(Integer.parseInt(municipioResidencia.getSubTotal()) - temp));
                temp = temp + Integer.parseInt(municipioResidencia.getErroDiagnostico());
                municipioResidencia.setErroDiagnostico(String.valueOf(temp));
            }
        } else {
            municipioResidencia.setSubTotal(String.valueOf(Integer.parseInt(municipioResidencia.getSubTotal()) + temp));
            temp = temp + Integer.parseInt(municipioResidencia.getNaoPreenchido());
            municipioResidencia.setNaoPreenchido(String.valueOf(temp));
        }
        return municipioResidencia;
    }

    private TuberculoseCoorte classificaAlta(String tipoAlta, TuberculoseCoorte beanMunicipioResidencia) {
        int temp = 1;
        if (tipoAlta != null && !tipoAlta.equals("")) {
            beanMunicipioResidencia.setSubTotal(String.valueOf(temp + Integer.parseInt(beanMunicipioResidencia.getSubTotal())));
            if (tipoAlta.equals("1")) {
                temp = temp + Integer.parseInt(beanMunicipioResidencia.getCura());
                beanMunicipioResidencia.setCura(String.valueOf(temp));
            }
            if (tipoAlta.equals("5")) {
                temp = temp + Integer.parseInt(beanMunicipioResidencia.getTransfMesmoMunicipio());
                beanMunicipioResidencia.setTransfMesmoMunicipio(String.valueOf(temp));
            }
            if (tipoAlta.equals("6")) {
                temp = temp + Integer.parseInt(beanMunicipioResidencia.getTransfOutroMunicipio());
                beanMunicipioResidencia.setTransfOutroMunicipio(String.valueOf(temp));
            }
            if (tipoAlta.equals("7")) {
                temp = temp + Integer.parseInt(beanMunicipioResidencia.getTransfOutroUf());
                beanMunicipioResidencia.setTransfOutroUf(String.valueOf(temp));
            }
            if (tipoAlta.equals("8")) {
                temp = temp + Integer.parseInt(beanMunicipioResidencia.getTransfOutroPais());
                beanMunicipioResidencia.setTransfOutroPais(String.valueOf(temp));
            }
            if (tipoAlta.equals("9") || tipoAlta.equals("11") || tipoAlta.equals("12") || tipoAlta.equals("13")) {
                temp = temp + Integer.parseInt(beanMunicipioResidencia.getOutraCategoria());
                beanMunicipioResidencia.setOutraCategoria(String.valueOf(temp));
            }
            //obito por tub
            if (tipoAlta.equals("3")) {
                temp = temp + Integer.parseInt(beanMunicipioResidencia.getObitoPorTuberculose());
                beanMunicipioResidencia.setObitoPorTuberculose(String.valueOf(temp));
            }
            //obito com tub
            if (tipoAlta.equals("4")) {
                temp = temp + Integer.parseInt(beanMunicipioResidencia.getObitoComTuberculose());
                beanMunicipioResidencia.setObitoComTuberculose(String.valueOf(temp));
            }
            if (tipoAlta.equals("2")) {
                temp = temp + Integer.parseInt(beanMunicipioResidencia.getAbandono());
                beanMunicipioResidencia.setAbandono(String.valueOf(temp));
            }
            if (tipoAlta.equals("10")) {
                beanMunicipioResidencia.setSubTotal(String.valueOf(Integer.parseInt(beanMunicipioResidencia.getSubTotal()) - temp));
                temp = temp + Integer.parseInt(beanMunicipioResidencia.getErroDiagnostico());
                beanMunicipioResidencia.setErroDiagnostico(String.valueOf(temp));
            }
        } else {
            beanMunicipioResidencia.setSubTotal(String.valueOf(Integer.parseInt(beanMunicipioResidencia.getSubTotal()) + temp));
            temp = temp + Integer.parseInt(beanMunicipioResidencia.getNaoPreenchido());
            beanMunicipioResidencia.setNaoPreenchido(String.valueOf(temp));
        }
        beanMunicipioResidencia.setSubTotal(String.valueOf(Integer.parseInt(beanMunicipioResidencia.getSubTotal())));
        return beanMunicipioResidencia;
    }

    public TuberculoseCoorte insereNoBean(TuberculoseCoorte beanHans) {

        DecimalFormat df = new DecimalFormat("0.0");
        if (beanHans.getSubTotal().equals("-1")) {
            beanHans.setSubTotal("0");
        }
        String sub = beanHans.getSubTotal();
        if (beanHans.getSubTotal().equals("0")) {
            sub = "1";
        }
        beanHans.setTotal(String.valueOf(Integer.valueOf(beanHans.getSubTotal()) + Integer.valueOf(beanHans.getErroDiagnostico())));
        beanHans.setPerAbandono(df.format(Float.valueOf(beanHans.getAbandono()) / Float.valueOf(sub) * 100));
        beanHans.setPerCura(df.format(Float.valueOf(beanHans.getCura()) / Float.valueOf(sub) * 100));
        beanHans.setPerNaoPreenchido(df.format(Float.valueOf(beanHans.getNaoPreenchido()) / Float.valueOf(sub) * 100));
        return beanHans;
    }

    public TuberculoseCoorte insereNoBean(String municipio, String noMunicipio) {

        DecimalFormat df = new DecimalFormat("0.00");
        TuberculoseCoorte d1 = new TuberculoseCoorte();
        d1.setCodMunicipio(municipio);
        d1.setNomeMunicipio(noMunicipio);
        getBarraStatus().setString("Calculando município: " + noMunicipio);

        d1.setCura(this.getCura());
        d1.setTransfMesmoMunicipio(this.getTransfMesmoMunicipio());
        d1.setTransfOutroMunicipio(this.getTransfOutroMunicipio());
        d1.setTransfOutroPais(this.getTransfOutroPais());
        d1.setTransfOutroUf(this.getTransfOutroUf());
        d1.setObitoPorTuberculose(this.getObitoPorTuberculose());
        d1.setObitoComTuberculose(this.getObitoComTuberculose());
        d1.setAbandono(this.getAbandono());
        d1.setErroDiagnostico(this.getErroDiagnostico());
        d1.setOutraCategoria(this.getOutraCategoria());
        d1.setNaoPreenchido(this.getNaoPreenchido());
        d1.setTransfNaoEspecificada(this.getTransfNaoEspecificada());
        d1.setSubTotal(this.getSubTotal());
        String sub = this.getSubTotal();
        if (d1.getSubTotal().equals("0")) {
            sub = "1";
        }
        d1.setTotal(String.valueOf(Integer.valueOf(this.getSubTotal()) + Integer.valueOf(this.getErroDiagnostico())));
        d1.setPerAbandono(df.format(Float.valueOf(this.getAbandono()) / Float.valueOf(sub) * 100));
        d1.setPerCura(df.format(Float.valueOf(this.getCura()) / Float.valueOf(sub) * 100));
        d1.setPerNaoPreenchido(df.format(Float.valueOf(this.getNaoPreenchido()) / Float.valueOf(sub) * 100));
        return d1;
    }

    public void reset() {
        this.setCura("0");
        this.setAbandono("0");
        this.setTransfMesmoMunicipio("0");
        this.setTransfOutroMunicipio("0");
        this.setTransfOutroPais("0");
        this.setTransfOutroUf("0");
        this.setErroDiagnostico("0");
        this.setObitoPorTuberculose("0");
        this.setObitoComTuberculose("0");
        this.setNaoPreenchido("0");
        this.setSubTotal("0");
        this.setTransfNaoEspecificada("0");
        this.setOutraCategoria("0");
    }

    @Override
    public String getTaxaEstado(Connection con, Map parametros) throws SQLException {
        if (isDBF()) {
            //ler o arquivo dbf
            DBFReader reader = null;
//            Util.retornaObjetoDbf(Configuracao.getPropriedade("caminho"));
//            if (reader == null) {
//                abreJanelaEscolherCaminho();
//            }
            calcula(reader, parametros);
            return getTaxaEstadual();
        } else {
            return "";
        }
    }

    public HashMap<String, TuberculoseCoorte> populaUfsBeansTubec() {
        DBFUtil utilDbf = new DBFUtil();
        HashMap<String, String> uf = new HashMap<String, String>();
        HashMap<String, TuberculoseCoorte> ufsBeans = new HashMap<String, TuberculoseCoorte>();
        //se codRegional estiver preenchida, deve buscar somente os municipios pertencentes a ela

        //busca municipios dessa regional
        DBFReader readerMunicipio = Util.retornaObjetoDbfCaminhoArquivo("UF", "dbf\\");
        Object[] rowObjects1;
        try {
            utilDbf.mapearPosicoes(readerMunicipio);

            while ((rowObjects1 = readerMunicipio.nextRecord()) != null) {

                TuberculoseCoorte agravoDbf = new TuberculoseCoorte();
                agravoDbf.init("");
                agravoDbf.setCodMunicipio(utilDbf.getString(rowObjects1, "ID_UF"));
                agravoDbf.setNomeMunicipio(utilDbf.getString(rowObjects1, "SG_UF"));
                uf.put(utilDbf.getString(rowObjects1, "ID_UF"), utilDbf.getString(rowObjects1, "SG_UF"));
                ufsBeans.put(agravoDbf.getCodMunicipio(), agravoDbf);

            }
        } catch (DBFException e) {
            Master.mensagem("Erro ao carregar municipios:\n" + e);
        }

        uf = sortHashMapByValues(uf, false);
        Set<String> ufKeys = uf.keySet();
        HashMap<String, TuberculoseCoorte> ufsBeansRetorno = new HashMap<String, TuberculoseCoorte>();
        Iterator valueIt = ufKeys.iterator();
        while (valueIt.hasNext()) {
            String key = (String) valueIt.next();
            ufsBeansRetorno.put(key, ufsBeans.get(key));

        }
        return ufsBeansRetorno;
    }

    public HashMap<String, TuberculoseCoorte> populaMunicipiosBeansTuberc(String sgUfResidencia, String codRegional) {
        DBFUtil utilDbf = new DBFUtil();
        HashMap<String, String> municipios = new HashMap<String, String>();
        HashMap<String, TuberculoseCoorte> municipiosBeans = new HashMap<String, TuberculoseCoorte>();
        //se codRegional estiver preenchida, deve buscar somente os municipios pertencentes a ela
        if (codRegional.length() > 0) {
            //busca municipios dessa regional
            DBFReader readerMunicipio = Util.retornaObjetoDbfCaminhoArquivo("MUNICNET", "dbf\\");
            Object[] rowObjects1;

            try {
                utilDbf.mapearPosicoes(readerMunicipio);
                while ((rowObjects1 = readerMunicipio.nextRecord()) != null) {
                    if (codRegional.equals(utilDbf.getString(rowObjects1, "ID_REGIONA"))) {
                        TuberculoseCoorte agravoDbf = new TuberculoseCoorte();
                        agravoDbf.init("");
                        agravoDbf.setCodMunicipio(utilDbf.getString(rowObjects1, "ID_MUNICIP"));
                        agravoDbf.setNomeMunicipio(utilDbf.getString(rowObjects1, "NM_MUNICIP"));
                        municipios.put(utilDbf.getString(rowObjects1, "ID_MUNICIP"), utilDbf.getString(rowObjects1, "NM_MUNICIP"));
                        municipiosBeans.put(agravoDbf.getCodMunicipio(), agravoDbf);
                    }
                }
            } catch (DBFException e) {
                Master.mensagem("Erro ao carregar municipios:\n" + e);
            }
        } else {
            //busca municipios dessa regional
            DBFReader readerMunicipio = Util.retornaObjetoDbfCaminhoArquivo("MUNICNET", "dbf\\");
            Object[] rowObjects1;
            try {
                utilDbf.mapearPosicoes(readerMunicipio);

                while ((rowObjects1 = readerMunicipio.nextRecord()) != null) {
                    if (sgUfResidencia.equals(utilDbf.getString(rowObjects1, "SG_UF")) || sgUfResidencia.equals("BR")) {
                        if (!utilDbf.getString(rowObjects1, "NM_MUNICIP").startsWith("IGNORADO") && utilDbf.getString(rowObjects1, "NM_MUNICIP").lastIndexOf("TRANSF.") == -1 && utilDbf.getString(rowObjects1, "NM_MUNICIP").lastIndexOf("ATUAL BENTO GONCALVES") == -1) {
                            if ((utilDbf.getString(rowObjects1, "SG_UF").equals("DF") && utilDbf.getString(rowObjects1, "ID_MUNICIP").equals("530010")) || !utilDbf.getString(rowObjects1, "SG_UF").equals("DF")) {
                                TuberculoseCoorte agravoDbf = new TuberculoseCoorte();
                                agravoDbf.init("");
                                agravoDbf.setCodMunicipio(utilDbf.getString(rowObjects1, "ID_MUNICIP"));
                                agravoDbf.setNomeMunicipio(utilDbf.getString(rowObjects1, "NM_MUNICIP"));
                                municipios.put(utilDbf.getString(rowObjects1, "ID_MUNICIP"), utilDbf.getString(rowObjects1, "NM_MUNICIP"));
                                municipiosBeans.put(agravoDbf.getCodMunicipio(), agravoDbf);
                            }
                        }
                    }
                }
            } catch (DBFException e) {
                Master.mensagem("Erro ao carregar municipios:\n" + e);
            }
        }
        municipios = sortHashMapByValues(municipios, false);
        Set<String> municipiosKeys = municipios.keySet();
        HashMap<String, TuberculoseCoorte> municipiosBeansRetorno = new HashMap<String, TuberculoseCoorte>();
        Iterator valueIt = municipiosKeys.iterator();
        while (valueIt.hasNext()) {
            String key = (String) valueIt.next();
            municipiosBeansRetorno.put(key, municipiosBeans.get(key));

        }
        return municipiosBeansRetorno;
    }

    private void preencheValoresEncerramento(String tipoAlta) {
        int temp = 1;
        if (tipoAlta != null && !tipoAlta.equals("")) {
            this.setSubTotal(String.valueOf(temp + Integer.parseInt(this.getSubTotal())));
            if (tipoAlta.equals("1")) {
                temp = temp + Integer.parseInt(this.getCura());
                this.setCura(String.valueOf(temp));
            }
            if (tipoAlta.equals("5")) {
                temp = temp + Integer.parseInt(this.getTransfMesmoMunicipio());
                this.setTransfMesmoMunicipio(String.valueOf(temp));
            }
            if (tipoAlta.equals("7")) {
                temp = temp + Integer.parseInt(this.getTransfOutroUf());
                this.setTransfOutroUf(String.valueOf(temp));
            }

            //obito por tub
            if (tipoAlta.equals("3")) {
                temp = temp + Integer.parseInt(this.getObitoPorTuberculose());
                this.setObitoPorTuberculose(String.valueOf(temp));
            }
            //obito com tub
            if (tipoAlta.equals("4")) {
                temp = temp + Integer.parseInt(this.getObitoComTuberculose());
                this.setObitoComTuberculose(String.valueOf(temp));
            }
            if (tipoAlta.equals("2")) {
                temp = temp + Integer.parseInt(this.getAbandono());
                this.setAbandono(String.valueOf(temp));
            }
            if (tipoAlta.equals("6")) {
                this.setSubTotal(String.valueOf(Integer.parseInt(this.getSubTotal()) - temp));
                temp = temp + Integer.parseInt(this.getErroDiagnostico());
                this.setErroDiagnostico(String.valueOf(temp));
            }
        } else {
            this.setSubTotal(String.valueOf(Integer.parseInt(this.getSubTotal()) + temp));
            temp = temp + Integer.parseInt(this.getNaoPreenchido());
            this.setNaoPreenchido(String.valueOf(temp));
        }
    }

    private void classificaAlta(String tipoAlta) {
        int temp = 1;
        if (tipoAlta != null && !tipoAlta.equals("")) {
            this.setSubTotal(String.valueOf(temp + Integer.parseInt(getSubTotal())));
            if (tipoAlta.equals("1")) {
                temp = temp + Integer.parseInt(getCura());
                this.setCura(String.valueOf(temp));
            }
            if (tipoAlta.equals("5")) {
                temp = temp + Integer.parseInt(getTransfMesmoMunicipio());
                this.setTransfMesmoMunicipio(String.valueOf(temp));
            }
            if (tipoAlta.equals("6")) {
                temp = temp + Integer.parseInt(getTransfOutroMunicipio());
                this.setTransfOutroMunicipio(String.valueOf(temp));
            }
            if (tipoAlta.equals("7")) {
                temp = temp + Integer.parseInt(getTransfOutroUf());
                this.setTransfOutroUf(String.valueOf(temp));
            }
            if (tipoAlta.equals("8")) {
                temp = temp + Integer.parseInt(getTransfOutroPais());
                this.setTransfOutroPais(String.valueOf(temp));
            }
            if (tipoAlta.equals("9") || tipoAlta.equals("11") || tipoAlta.equals("12") || tipoAlta.equals("13")) {
                temp = temp + Integer.parseInt(getOutraCategoria());
                this.setOutraCategoria(String.valueOf(temp));
            }
            //obito por tub
            if (tipoAlta.equals("3")) {
                temp = temp + Integer.parseInt(getObitoPorTuberculose());
                this.setObitoPorTuberculose(String.valueOf(temp));
            }
            //obito com tub
            if (tipoAlta.equals("4")) {
                temp = temp + Integer.parseInt(getObitoComTuberculose());
                this.setObitoComTuberculose(String.valueOf(temp));
            }
            if (tipoAlta.equals("2")) {
                temp = temp + Integer.parseInt(getAbandono());
                this.setAbandono(String.valueOf(temp));
            }
            if (tipoAlta.equals("10")) {
                this.setSubTotal(String.valueOf(Integer.parseInt(getSubTotal()) - temp));
                temp = temp + Integer.parseInt(getErroDiagnostico());
                this.setErroDiagnostico(String.valueOf(temp));
            }
        } else {
            this.setSubTotal(String.valueOf(Integer.parseInt(getSubTotal()) + temp));
            temp = temp + Integer.parseInt(getNaoPreenchido());
            this.setNaoPreenchido(String.valueOf(temp));
        }
        this.setSubTotal(String.valueOf(Integer.parseInt(getSubTotal())));
    }

    @Override
    public HashMap<String, ColunasDbf> getColunas() {
        HashMap<String, ColunasDbf> hashColunas = new HashMap<String, ColunasDbf>();
        
        hashColunas.put("ID_LOCRES", new ColunasDbf(7));
        hashColunas.put("DS_LOCRES", new ColunasDbf(30));
        hashColunas.put("ID_UFRES", new ColunasDbf(2));
        hashColunas.put("N_CURATB", new ColunasDbf(10, 0));
        hashColunas.put("I_CURATB", new ColunasDbf(5, 2));
        hashColunas.put("ST_ABAND", new ColunasDbf(10, 0));
        hashColunas.put("P_STABAND", new ColunasDbf(5, 2));
        hashColunas.put("ST_OBITB", new ColunasDbf(10, 0));
        hashColunas.put("ST_OBITO", new ColunasDbf(10, 0));
        hashColunas.put("ST_IGNOR", new ColunasDbf(10, 0));
        hashColunas.put("P_STIGNOR", new ColunasDbf(5, 2));
        hashColunas.put("D_CURATB", new ColunasDbf(4, 0));
        hashColunas.put("ST_ERRDIA", new ColunasDbf(10, 0));
        hashColunas.put("TOTAL_NOT", new ColunasDbf(4, 0));
        hashColunas.put("ORIGEM", new ColunasDbf(30));
        hashColunas.put("ANO_DIAG", new ColunasDbf(4, 0));
        hashColunas.put("DT_DIAGIN", new ColunasDbf(10));
        hashColunas.put("DT_DIAGFI", new ColunasDbf(10));
        
        if(!isSituacaoEncerramento()){
            hashColunas.put("ST_MESMUN", new ColunasDbf(10, 0));
            hashColunas.put("ST_OUTMUN", new ColunasDbf(10, 0));
            hashColunas.put("ST_OUTUF", new ColunasDbf(10, 0));
            hashColunas.put("ST_OUTPA", new ColunasDbf(10, 0));
            hashColunas.put("ST_OUTRA", new ColunasDbf(10, 0));
        }else{
            hashColunas.put("ST_TRANSF", new ColunasDbf(10, 0));
            hashColunas.put("ST_TBMULT", new ColunasDbf(10, 0));
        }
       
        this.setColunas(hashColunas);
        return hashColunas;
    }

    @Override
    public String[] getOrdemColunas() {
        if(!isSituacaoEncerramento()){
            return new String[]{"ID_LOCRES", "DS_LOCRES", "ID_UFRES", "N_CURATB", "I_CURATB", "ST_ABAND", "P_STABAND", "ST_OBITB", "ST_OBITO",
                "ST_MESMUN", "ST_OUTMUN", "ST_OUTUF", "ST_OUTPA", "ST_IGNOR", "P_STIGNOR", 
                "ST_OUTRA", "D_CURATB", "ST_ERRDIA", "TOTAL_NOT","ANO_DIAG","DT_DIAGIN", 
                "DT_DIAGFI", "ORIGEM"};
        }
        return new String[]{"ID_LOCRES", "DS_LOCRES", "ID_UFRES", "N_CURATB", "I_CURATB", "ST_ABAND", "P_STABAND", "ST_OBITB", "ST_OBITO",
            "ST_TRANSF", "ST_IGNOR", "P_STIGNOR", "ST_TBMULT", "D_CURATB", "ST_ERRDIA",
            "TOTAL_NOT", "ANO_DIAG","DT_DIAGIN","DT_DIAGFI", "ORIGEM"};
    }

    @Override
    public Map getParametros() {
        Util util = new Util();
        Map parametros = new HashMap();
        parametros.put("parRodape", this.getRodape());
        parametros.put("parConfig", "");
        return parametros;
    }

    @Override
    public DBFWriter getLinhas(HashMap<String, ColunasDbf> colunas, List bean, DBFWriter writer) throws DBFException, IOException {
        for (int i = 0; i < bean.size(); i++) {
            Object rowData[] = new Object[colunas.size()];
            TuberculoseCoorte agravo = (TuberculoseCoorte) bean.get(i);
            if (agravo.getNomeMunicipio().equals("BRASIL")) {
                rowData[0] = null;
                rowData[2] = null;
            } else {
                if(agravo.getCodMunicipio() == null || agravo.getCodMunicipio().equals("")){
                    rowData[0] = SinanUtil.siglaUFToIDUF(agravo.getNomeMunicipio());//ID_LOCRES
                    rowData[2] = SinanUtil.siglaUFToIDUF(agravo.getNomeMunicipio());
                }else{
                    rowData[0] = agravo.getCodMunicipio();//ID_LOCRES
                    rowData[2] = agravo.getCodMunicipio().substring(0, 2);
                }
            }
            rowData[1] = agravo.getNomeMunicipio();//DS_LOCRES
            rowData[3] = Double.parseDouble(agravo.getCura());//ID_UFRES
            rowData[4] = Double.parseDouble(agravo.getPerCura().replace(",", "."));//N_CURATB
            rowData[5] = Double.parseDouble(agravo.getAbandono());//I_CURATB
            rowData[6] = Double.parseDouble(agravo.getPerAbandono().replace(",", "."));//ST_ABAND
            rowData[7] = Double.parseDouble(agravo.getObitoPorTuberculose());//ST_OBITB
            rowData[8] = Double.parseDouble(agravo.getObitoComTuberculose());//ST_OBITO

            if(!isSituacaoEncerramento()){
                rowData[9] = Double.parseDouble(agravo.getTransfMesmoMunicipio());//ST_MESMUN
                rowData[10] = Double.parseDouble(agravo.getTransfOutroMunicipio());//ST_OUTMUN
                rowData[11] = Double.parseDouble(agravo.getTransfOutroUf());//ST_OUTUF
                rowData[12] = Double.parseDouble(agravo.getTransfOutroPais());//ST_OUTPA
                rowData[13] = Double.parseDouble(agravo.getNaoPreenchido());//ST_IGNOR
                rowData[14] = Double.parseDouble(agravo.getPerNaoPreenchido().replace(",", "."));//P_STIGNOR
                rowData[15] = Double.parseDouble(agravo.getOutraCategoria());//ST_OUTRA
                rowData[16] = Double.parseDouble(agravo.getSubTotal());//D_CURATB
                rowData[17] = Double.parseDouble(agravo.getErroDiagnostico());//ST_ERRDIA
                rowData[18] = Double.parseDouble(agravo.getTotal());//TOTAL_NOT
                rowData[19] = preencheAno(dtInicial, dtFinal);//ANO_DIAG
                rowData[20] = dtInicial;//DT_DIAGIN
                rowData[21] = dtFinal;//DT_DIAGFI
                rowData[22] = "TUBERCULOSE-SINANNET";//ORIGEM
            }else{
                rowData[9] = Double.parseDouble(agravo.getTransfMesmoMunicipio());//ST_TRANSF
                rowData[10] = Double.parseDouble(agravo.getNaoPreenchido());//ST_IGNOR
                rowData[11] = Double.parseDouble(agravo.getPerNaoPreenchido().replace(",", "."));//P_STIGNOR
                rowData[12] = Double.parseDouble(agravo.getTransfOutroUf());//ST_TBMULT    
                rowData[13] = Double.parseDouble(agravo.getSubTotal());//D_CURATB
                rowData[14] = Double.parseDouble(agravo.getErroDiagnostico());//ST_ERRDIA
                rowData[15] = Double.parseDouble(agravo.getTotal());//TOTAL_NOT
                rowData[16] = preencheAno(dtInicial, dtFinal);//ANO_DIAG
                rowData[17] = dtInicial;//DT_DIAGIN
                rowData[18] = dtFinal;//DT_DIAGFI
                rowData[19] = "TUBERCULOSE-SINANNET";//ORIGEM
            }
           

            writer.addRecord(rowData);
        }
        return writer;
    }

    @Override
    public String getCaminhoJasper() {
        return "/com/org/relatorios/hanseniase.jasper";
    }

    public String getCura() {
        return cura;
    }

    public void setCura(String cura) {
        this.cura = cura;
    }

    public String getAbandono() {
        return abandono;
    }

    public void setAbandono(String abandono) {
        this.abandono = abandono;
    }

    public String getTransfMesmoMunicipio() {
        return transfMesmoMunicipio;
    }

    public void setTransfMesmoMunicipio(String transfMesmoMunicipio) {
        this.transfMesmoMunicipio = transfMesmoMunicipio;
    }

    public String getTransfOutroMunicipio() {
        return transfOutroMunicipio;
    }

    public void setTransfOutroMunicipio(String transfOutroMunicipio) {
        this.transfOutroMunicipio = transfOutroMunicipio;
    }

    public String getTransfOutroUf() {
        return transfOutroUf;
    }

    public void setTransfOutroUf(String transfOutroUf) {
        this.transfOutroUf = transfOutroUf;
    }

    public String getNaoPreenchido() {
        return naoPreenchido;
    }

    public void setNaoPreenchido(String naoPreenchido) {
        this.naoPreenchido = naoPreenchido;
    }

    public String getErroDiagnostico() {
        return erroDiagnostico;
    }

    public void setErroDiagnostico(String erroDiagnostico) {
        this.erroDiagnostico = erroDiagnostico;
    }

    public String getPerNaoPreenchido() {
        return perNaoPreenchido;
    }

    public void setPerNaoPreenchido(String perNaoPreenchido) {
        this.perNaoPreenchido = perNaoPreenchido;
    }

    public String getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(String subTotal) {
        this.subTotal = subTotal;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getPerAbandono() {
        return perAbandono;
    }

    public void setPerAbandono(String perAbandono) {
        this.perAbandono = perAbandono;
    }

    public String getPerCura() {
        return perCura;
    }

    public void setPerCura(String perCura) {
        this.perCura = perCura;
    }

    public String getTransfOutroPais() {
        return transfOutroPais;
    }

    public void setTransfOutroPais(String transfOutroPais) {
        this.transfOutroPais = transfOutroPais;
    }

    public String getObitoPorTuberculose() {
        return obitoPorTuberculose;
    }

    public void setObitoPorTuberculose(String obito) {
        this.obitoPorTuberculose = obito;
    }

    public String getObitoComTuberculose() {
        return obitoComTuberculose;
    }

    public void setObitoComTuberculose(String obito) {
        this.obitoComTuberculose = obito;
    }

    public String getOutraCategoria() {
        return outraCategoria;
    }

    public void setOutraCategoria(String outraCategoria) {
        this.outraCategoria = outraCategoria;
    }

    public boolean isSituacaoEncerramento() {
        return situacaoEncerramento;
    }

    public void setSituacaoEncerramento(boolean situacaoEncerramento) {
        this.situacaoEncerramento = situacaoEncerramento;
    }
    
}
