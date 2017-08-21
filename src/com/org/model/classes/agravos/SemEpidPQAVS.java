/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.org.model.classes.agravos;

import com.linuxense.javadbf.DBFException;
import com.linuxense.javadbf.DBFReader;
import com.linuxense.javadbf.DBFWriter;
import com.org.bd.Conexao;
import com.org.bd.DBFUtil;
import com.org.negocio.Configuracao;
import com.org.model.classes.Agravo;
import com.org.model.classes.ColunasDbf;
import com.org.model.classes.agravos.oportunidade.CasoOportunidadePQAVS;
import com.org.model.classes.agravos.oportunidade.OportunidadeAgravoPQAVS;
import com.org.negocio.Municipio;
import com.org.negocio.Util;
import com.org.util.SinanDateUtil;
import com.org.util.SinanUtil;
import com.org.view.Master;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.beanutils.BeanComparator;

/**
 *
 * @author geraldo
 */
public class SemEpidPQAVS extends Agravo {

    private boolean periodoValido180dias = true;
    private boolean periodoValido60dias = true;
    private boolean porAgravo;
    private String dataAvaliacao;
    private String anoAvaliado;
    private String dtInicioAvaliacao;
    private String dtFimAvaliacao;
    private String nomeAgravo;
    private String uf;
    private String municipio;
    private String sqlCalculoAgravos180dias;
    private String sqlCalculoAgravos60dias;
    private String sqlCalculoAgravosMalaria60dias;
    private List<String> agravosValidos = new ArrayList<String>();
    private String sqlCalculoAgravoEspecifico;
    private List<Municipio> municipios = new ArrayList<Municipio>();
    private List<CasoOportunidadePQAVS> listExportacao = new ArrayList<CasoOportunidadePQAVS>();

    public List<CasoOportunidadePQAVS> getListExportacao() {
        return listExportacao;
    }

    public void setListExportacao(List<CasoOportunidadePQAVS> listExportacao) {
        this.listExportacao = listExportacao;
    }

    public SemEpidPQAVS(boolean isDbf) {
        this.setDBF(isDbf);
        setPeriodo("de avaliação");
        setTipoAgregacao("de Residência");
        init("postgres");
    }

    @Override
    public void calcula(DBFReader reader, Map parametros) {

        if (uf.equals("Brasil")) {
            try {
                this.setBeans(getCalculaResultado(reader, parametros));
            } catch (Exception e) {
                System.out.println(e);
            }
        } else {
            //verifica se é dbf
            try {
                if (isDBF()) {
                    this.setBeans(getCalculaResultado(reader, parametros));
                } else {
                    Conexao con = new Util().conectarSiceb();
                    con.conect();
                    this.setBeans(getCalculaResultado(con.getC(), parametros));
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    @Override
    public void init(String tipoBanco) {
        this.setSqlNumeradorCompletitude("");
        this.setTextoCompletitude("");
        this.setMultiplicador(100);
        this.setTipo("Oportunidade");
        this.setTitulo1("Proporção de doenças exantemáticas investigados oportunamente");
        this.setTituloColuna("Proporção");
        this.setRodape("Numerador: Total de casos suspeitos de sarampo e rubéola investigados em até 48 horas após a notificação, residentes em determinado local e notificados em determinado período \n" + "Denominador: Total de casos suspeitos de sarampo e rubéola,  residentes em determinado local e notificados em determinado período ");
        if (!isDBF()) {
            this.setSqlCalculoAgravoEspecifico(
                    "select (dt_encerramento-dt_notificacao) as  diff,dt_encerramento,tp_classificacao_final,dt_notificacao,tp_classificacao_final," + "no_agravo as co_cid,nu_notificacao,co_cnes as co_unidade_notificacao,co_municipio_notificacao,co_municipio_notificacao,no_municipio,co_uf_residencia,co_municipio_residencia,co_uf_notificacao  " + "from dbsinan.tb_notificacao as t1 inner join dbsinan.tb_agravo t2 on t1.co_cid=t2.co_cid inner join dbgeral.tb_municipio t3 on t1.co_municipio_residencia=t3.co_municipio_ibge " + " left join dblocalidade.tb_estabelecimento_saude us on t1.co_unidade_notificacao = us.co_estabelecimento  where " + "(dt_notificacao BETWEEN ?  AND ?) and co_uf_residencia= ? and t1.co_cid = ? and" + "(tp_duplicidade <> 2 or tp_duplicidade is null) ");
            this.setSqlCalculoAgravos180dias(
                    "select (dt_encerramento-dt_notificacao) as  diff,dt_encerramento,tp_classificacao_final,dt_notificacao,tp_classificacao_final," + "no_agravo as co_cid,nu_notificacao,co_cnes as co_unidade_notificacao,co_municipio_notificacao,co_municipio_notificacao,no_municipio,co_uf_residencia,co_municipio_residencia,co_uf_notificacao " + "from dbsinan.tb_notificacao as t1 inner join dbsinan.tb_agravo t2 on t1.co_cid=t2.co_cid inner join dbgeral.tb_municipio t3 on t1.co_municipio_residencia=t3.co_municipio_ibge " + " left join dblocalidade.tb_estabelecimento_saude us on t1.co_unidade_notificacao = us.co_estabelecimento  where  " + "t1.co_cid in( 'B19','B55.1','P35.0' ) and  " + "(dt_notificacao BETWEEN ?  AND ?) and co_uf_residencia= ? and " + "(tp_duplicidade <> 2 or tp_duplicidade is null) ");
            this.setSqlCalculoAgravos60dias(
                    "select (dt_encerramento-dt_notificacao) as  diff,dt_encerramento,tp_classificacao_final,tp_suspeita,dt_notificacao,tp_classificacao_final," + "no_agravo as co_cid,nu_notificacao,co_cnes as co_unidade_notificacao,co_municipio_notificacao,co_municipio_notificacao,no_municipio,co_uf_residencia,co_municipio_residencia,co_uf_notificacao  " + "from dbsinan.tb_notificacao as t1 inner join dbsinan.tb_agravo t2 on t1.co_cid=t2.co_cid inner join dbgeral.tb_municipio t3 on t1.co_municipio_residencia=t3.co_municipio_ibge  " + " left join dblocalidade.tb_estabelecimento_saude us on t1.co_unidade_notificacao = us.co_estabelecimento  where  " + "(t1.co_cid in('A35','B09','B57.1','A00.9','A37.9','A36.9','A95.9','A01.0','A27.9','A80.9','A20.9','A82.9','A33','B55.0'," + "'A98.8','A77.9','G03.9','A05.1','A77.9','A92.3') or (t1.co_cid='A90' and tp_classificacao_final in(2,3,4))) and  " + "(dt_notificacao BETWEEN ?  AND ?) and co_uf_residencia= ? and (tp_duplicidade <> 2 or tp_duplicidade is null)");
            this.setSqlCalculoAgravosMalaria60dias(
                    "select (dt_encerramento-dt_notificacao) as  diff,dt_encerramento,tp_classificacao_final,tp_suspeita,dt_notificacao,tp_classificacao_final," + "no_agravo as co_cid,nu_notificacao,co_cnes as co_unidade_notificacao,co_municipio_notificacao,co_municipio_notificacao,no_municipio,co_uf_residencia,co_municipio_residencia,co_uf_notificacao  " + "from dbsinan.tb_notificacao as t1 inner join dbsinan.tb_agravo t2 on t1.co_cid=t2.co_cid inner join dbgeral.tb_municipio t3 on t1.co_municipio_residencia=t3.co_municipio_ibge  " + " left join dblocalidade.tb_estabelecimento_saude us on t1.co_unidade_notificacao = us.co_estabelecimento  where  " + "(t1.co_cid in('A35','B09','B57.1','A00.9','A37.9','A36.9','A95.9','A01.0','A27.9','A80.9','A20.9','A82.9','A33','B55.0'," + "'A98.8','A77.9','G03.9','A05.1','A77.9','A92.3','B54') or (t1.co_cid='A90' and tp_classificacao_final in(2,3,4))) and  " + "(dt_notificacao BETWEEN ?  AND ?) and co_uf_residencia= ? and (tp_duplicidade <> 2 or tp_duplicidade is null)");

            this.setSqlNumeradorBeanMunicipios(this.getSqlNumeradorMunicipioEspecifico());
            this.setSqlDenominadorBeanMunicipios(this.getSqlDenominadorMunicipioEspecifico());
        }
    }

    private void iniciaAgravosValidos() {
        getAgravosValidos().add("A229");  //ANTRAZ PNEUMONICO
        getAgravosValidos().add("A969");  //ARENAVIRUS
        getAgravosValidos().add("A051");  //BOTULISMO
        getAgravosValidos().add("A009");  //COLERA
        getAgravosValidos().add("A90");  //DENGUE (OBITOS)
        getAgravosValidos().add("A984");  //EBOLA
        getAgravosValidos().add("Y59");   //EVENTOS ADVERSOS GRAVES OU OBITOS POS-VACINACAO
        getAgravosValidos().add("A959");  //FEBRE AMARELA
        getAgravosValidos().add("A920");  //FEBRE DE CHIKUNGUNYA
        getAgravosValidos().add("A923");  //FEBRE DO NILO OCIDENTAL
        getAgravosValidos().add("A779");  //FEBRE MACULOSA E OUTRAS RIQUETISIOSES
        getAgravosValidos().add("A484");  //FEBRE PURPURICA BRASILEIRA
        getAgravosValidos().add("J11");   //INFLUENZA HUMANA PRODUZIDA POR NOVO SUBTIPO VIRAL
        getAgravosValidos().add("A962");  //LASSA
        getAgravosValidos().add("B54");   //MALARIA NA REGIAO EXTRA AMAZONICA
        getAgravosValidos().add("A983");  //MARBURG
        getAgravosValidos().add("A809");  //PARALISIA FLACIDA AGUDA
        getAgravosValidos().add("A209");  //PESTE
        getAgravosValidos().add("A829");  //RAIVA HUMANA
        getAgravosValidos().add("B092");  //RUBEOLA
        getAgravosValidos().add("B091");  //SARAMPO
        getAgravosValidos().add("P350");  //SINDROME DA RUBEOLA CONGENITA
        getAgravosValidos().add("U049");  //SINDROME RESPIRATORIA AGUDA GRAVE ASSOCIADA A CORONAVIRUS
        getAgravosValidos().add("A219");  //TULAREMIA
        getAgravosValidos().add("B03");   //VARIOLA

    }

    private String buscaRegiao(String uf) {
        if (uf.equals("TO")) {
            return "NORTE";
        }
        if (uf.equals("AC")) {
            return "NORTE";
        }
        if (uf.equals("AL")) {
            return "NORDESTE";
        }
        if (uf.equals("AM")) {
            return "NORTE";
        }
        if (uf.equals("AP")) {
            return "NORTE";
        }
        if (uf.equals("BA")) {
            return "NORDESTE";
        }
        if (uf.equals("CE")) {
            return "NORDESTE";
        }
        if (uf.equals("DF")) {
            return "CENTRO-OESTE";
        }
        if (uf.equals("ES")) {
            return "SUDESTE";
        }
        if (uf.equals("GO")) {
            return "CENTRO-OESTE";
        }
        if (uf.equals("MA")) {
            return "NORDESTE";
        }
        if (uf.equals("MG")) {
            return "SUDESTE";
        }
        if (uf.equals("MS")) {
            return "CENTRO-OESTE";
        }
        if (uf.equals("MT")) {
            return "CENTRO-OESTE";
        }
        if (uf.equals("PA")) {
            return "NORTE";
        }
        if (uf.equals("PB")) {
            return "NORDESTE";
        }
        if (uf.equals("PE")) {
            return "NORDESTE";
        }
        if (uf.equals("PI")) {
            return "NORDESTE";
        }
        if (uf.equals("PR")) {
            return "SUL";
        }
        if (uf.equals("RJ")) {
            return "SUDESTE";
        }
        if (uf.equals("RN")) {
            return "NORDESTE";
        }
        if (uf.equals("RO")) {
            return "NORTE";
        }
        if (uf.equals("RR")) {
            return "NORTE";
        }
        if (uf.equals("RS")) {
            return "SUL";
        }
        if (uf.equals("RO")) {
            return "NORTE";
        }
        if (uf.equals("SC")) {
            return "SUL";
        }
        if (uf.equals("SE")) {
            return "NORDESTE";
        }
        if (uf.equals("SP")) {
            return "SUDESTE";
        }

        return null;
    }

    /**
     * Busca na base DBF de Região de Saúde o nome da região a partir do código.
     *
     * @param idRegiao
     * @return
     * @throws SQLException
     * @data 15fev2013
     * @autor Taidson
     */
    public String buscaRegiaoSaude(String idRegiao) throws SQLException {
        if (idRegiao == null) {
            return "";
        }

        DBFReader reader = SinanUtil.retornaObjetoDbfCaminhoArquivo("REGIAO", "dbf\\");
        Object[] rowObjects;
        DBFUtil utilDbf = new DBFUtil();
        try {
            utilDbf.mapearPosicoes(reader);
            while ((rowObjects = reader.nextRecord()) != null) {
                if (idRegiao.equals(utilDbf.getString(rowObjects, "ID_REGIAO"))) {
                    return utilDbf.getString(rowObjects, "NM_REGIAO");
                }
            }
        } catch (DBFException e) {
            Master.mensagem("Erro: regional nao encontrada.Verifique se existe a pasta DBF e se os arquivo REGIONET.DBF está lá:\n" + e);
        }
        return "";
    }

    private HashMap<String, OportunidadeAgravoPQAVS> populaMunicipiosBeansOportuno(String uf, String idMunicipio, String codRegional) {
        DBFUtil utilDbf = new DBFUtil();
        HashMap<String, String> municipios = new HashMap<String, String>();
        HashMap<String, OportunidadeAgravoPQAVS> municipiosBeans = new HashMap<String, OportunidadeAgravoPQAVS>();
        //se codRegional estiver preenchida, deve buscar somente os municipios pertencentes a ela
        int i = 1;
        if (codRegional.length() > 0 || ("".equals(idMunicipio) && !"BR".equals(uf))) {

            //busca municipios dessa regional
            DBFReader readerMunicipio = Util.retornaObjetoDbfCaminhoArquivo("MUNICNET", "dbf\\");
            Object[] rowObjects1;
//ALTERAR ESSE CÓDIGO PARA BUSCAR OS MUNICÍPIOS DA REGIÃO DE SAÚDE - 14FEV2013
            try {
                utilDbf.mapearPosicoes(readerMunicipio);
                while ((rowObjects1 = readerMunicipio.nextRecord()) != null) {

                    int TotalRegistrosInt = readerMunicipio.getRecordCount();
                    if (codRegional.length() > 0 && codRegional.equals(utilDbf.getString(rowObjects1, "ID_REGIAO"))) {
                        if (!utilDbf.getString(rowObjects1, "NM_MUNICIP").startsWith("IGNORADO") && utilDbf.getString(rowObjects1, "NM_MUNICIP").lastIndexOf("TRANSF.") == -1 && utilDbf.getString(rowObjects1, "NM_MUNICIP").lastIndexOf("ATUAL BENTO GONCALVES") == -1) {
                            if ((utilDbf.getString(rowObjects1, "SG_UF").equals("DF") && utilDbf.getString(rowObjects1, "NM_MUNICIP").equals("BRASILIA")) || !utilDbf.getString(rowObjects1, "SG_UF").equals("DF")) {
                                OportunidadeAgravoPQAVS agravoDbf = new OportunidadeAgravoPQAVS();
                                agravoDbf.setNmAgravo(utilDbf.getString(rowObjects1, "NM_MUNICIP"));
                                agravoDbf.setCodAgravo(utilDbf.getString(rowObjects1, "ID_MUNICIP"));
                                agravoDbf.setRegiao(buscaRegiao(agravoDbf.getNmAgravo()));
                                //          agravoDbf.setNmAgravo(utilDbf.getString(rowObjects1, "SG_UF")); //ALTEREI PARA MUNICIPIO 21mar2013
                                agravoDbf.setUf(utilDbf.getString(rowObjects1, "SG_UF"));
                                agravoDbf.setCodRegiaoSaude(utilDbf.getString(rowObjects1, "ID_REGIAO"));
                                try {
                                    agravoDbf.setRegiaoSaude(buscaRegiaoSaude(utilDbf.getString(rowObjects1, "ID_REGIAO")));
                                } catch (SQLException ex) {
                                    Logger.getLogger(SemEpidPQAVS.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                agravoDbf.setQtdDataInvalida(0);
                                agravoDbf.setQtdInoportuno(0);
                                agravoDbf.setQtdNaoEncerrado(0);
                                agravoDbf.setQtdOportuno(0);
                                agravoDbf.setTotal(0);
                                municipios.put(utilDbf.getString(rowObjects1, "ID_MUNICIP"), utilDbf.getString(rowObjects1, "NM_MUNICIP"));
                                municipiosBeans.put(agravoDbf.getCodAgravo(), agravoDbf);
                            }
                        }
                    } else if (uf.equals(utilDbf.getString(rowObjects1, "SG_UF")) && codRegional.length() == 0) {
                        if (!utilDbf.getString(rowObjects1, "NM_MUNICIP").startsWith("IGNORADO") && utilDbf.getString(rowObjects1, "NM_MUNICIP").lastIndexOf("TRANSF.") == -1 && utilDbf.getString(rowObjects1, "NM_MUNICIP").lastIndexOf("ATUAL BENTO GONCALVES") == -1) {
                            if ((utilDbf.getString(rowObjects1, "SG_UF").equals("DF") && utilDbf.getString(rowObjects1, "NM_MUNICIP").equals("BRASILIA")) || !utilDbf.getString(rowObjects1, "SG_UF").equals("DF")) {
                                OportunidadeAgravoPQAVS agravoDbf = new OportunidadeAgravoPQAVS();
                                agravoDbf.setNmAgravo(utilDbf.getString(rowObjects1, "NM_MUNICIP"));
                                agravoDbf.setCodAgravo(utilDbf.getString(rowObjects1, "ID_MUNICIP"));
                                agravoDbf.setRegiao(buscaRegiao(agravoDbf.getNmAgravo()));
                                //          agravoDbf.setNmAgravo(utilDbf.getString(rowObjects1, "SG_UF")); //ALTEREI PARA MUNICIPIO 21mar2013
                                agravoDbf.setUf(utilDbf.getString(rowObjects1, "SG_UF"));
                                agravoDbf.setCodRegiaoSaude(utilDbf.getString(rowObjects1, "ID_REGIAO"));
                                try {
                                    agravoDbf.setRegiaoSaude(buscaRegiaoSaude(utilDbf.getString(rowObjects1, "ID_REGIAO")));
                                } catch (SQLException ex) {
                                    Logger.getLogger(SemEpidPQAVS.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                agravoDbf.setQtdDataInvalida(0);
                                agravoDbf.setQtdInoportuno(0);
                                agravoDbf.setQtdNaoEncerrado(0);
                                agravoDbf.setQtdOportuno(0);
                                agravoDbf.setTotal(0);
                                municipios.put(utilDbf.getString(rowObjects1, "ID_MUNICIP"), utilDbf.getString(rowObjects1, "NM_MUNICIP"));
                                municipiosBeans.put(agravoDbf.getCodAgravo(), agravoDbf);
                            }
                        }
                    }
                    float percentual = Float.parseFloat(String.valueOf(i)) / Float.parseFloat(String.valueOf(TotalRegistrosInt)) * 100;
                    getBarraStatus().setString("Preparando arquivos... ");
                    getBarraStatus().setValue((int) percentual);
                    i++;
                }
                getBarraStatus().setValue(0);
            } catch (DBFException e) {
                Master.mensagem("Erro ao carregar municipios:\n" + e);
            }

        } else {
            //busca municipios dessa regional
            DBFReader readerMunicipio = Util.retornaObjetoDbfCaminhoArquivo("MUNICNET", "dbf\\");
            Object[] rowObjects1;
            try {
                utilDbf.mapearPosicoes(readerMunicipio);
//VERIFICAR ESSE TRECHO DE CÓDIGO
                int TotalRegistrosInt = readerMunicipio.getRecordCount();
                while ((rowObjects1 = readerMunicipio.nextRecord()) != null) {
                    if (utilDbf.getString(rowObjects1, "NM_MUNICIP").equals("BRASILIA")) {
                        SinanUtil.imprimirConsole("TESTE");
                    }
                    if ((utilDbf.getString(rowObjects1, "SG_UF").equals("DF") && utilDbf.getString(rowObjects1, "ID_MUNICIP").equals("530010")) || !utilDbf.getString(rowObjects1, "SG_UF").equals("DF")) {
                        if (idMunicipio.equals("") || idMunicipio.equals(utilDbf.getString(rowObjects1, "ID_MUNICIP"))) {
                            OportunidadeAgravoPQAVS agravoDbf = new OportunidadeAgravoPQAVS();
                            agravoDbf.setNmAgravo(utilDbf.getString(rowObjects1, "NM_MUNICIP"));
                            agravoDbf.setCodAgravo(utilDbf.getString(rowObjects1, "ID_MUNICIP"));
                            agravoDbf.setRegiao(buscaRegiao(agravoDbf.getNmAgravo()));
                            agravoDbf.setUf(utilDbf.getString(rowObjects1, "SG_UF"));
                            agravoDbf.setCodRegiaoSaude(utilDbf.getString(rowObjects1, "ID_REGIAO"));
                            try {
                                agravoDbf.setRegiaoSaude(buscaRegiaoSaude(utilDbf.getString(rowObjects1, "ID_REGIAO")));
                            } catch (SQLException ex) {
                                Logger.getLogger(SemEpidPQAVS.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            agravoDbf.setQtdDataInvalida(0);
                            agravoDbf.setQtdInoportuno(0);
                            agravoDbf.setQtdNaoEncerrado(0);
                            agravoDbf.setQtdOportuno(0);
                            agravoDbf.setTotal(0);
                            municipios.put(utilDbf.getString(rowObjects1, "ID_MUNICIP"), utilDbf.getString(rowObjects1, "NM_MUNICIP"));
                            municipiosBeans.put(agravoDbf.getCodAgravo(), agravoDbf);
                        }
                        //           }
                        //       }
                    }
                    float percentual = Float.parseFloat(String.valueOf(i)) / Float.parseFloat(String.valueOf(TotalRegistrosInt)) * 100;
                    getBarraStatus().setString("Preparando arquivos... ");
                    getBarraStatus().setValue((int) percentual);
                    i++;
                }
                getBarraStatus().setValue(0);
            } catch (DBFException e) {
                Master.mensagem("Erro ao carregar municipios:\n" + e);
            }
        }
        municipios = new Agravo().sortHashMapByValues(municipios, false);
        Set<String> municipiosKeys = municipios.keySet();
        HashMap<String, OportunidadeAgravoPQAVS> municipiosBeansRetorno = new HashMap<String, OportunidadeAgravoPQAVS>();
        Iterator valueIt = municipiosKeys.iterator();
        while (valueIt.hasNext()) {
            String key = (String) valueIt.next();
            municipiosBeansRetorno.put(key, municipiosBeans.get(key));
        }
        return municipiosBeans;
    }

    public String buscaNomeAgravo(String codAgravo) {
        String retorno = "";

        if (codAgravo.equals("A229")) {
            retorno = "ANTRAZ PNEUMONICO";
        }
        if (codAgravo.equals("A969")) {
            retorno = "ARENAVIRUS";
        }
        if (codAgravo.equals("A051")) {
            retorno = "BOTULISMO";
        }
        if (codAgravo.equals("A009")) {
            retorno = "COLERA";
        }
        if (codAgravo.equals("A90")) {
            retorno = "DENGUE (OBITOS)";
        }
        if (codAgravo.equals("A984")) {
            retorno = "EBOLA";
        }
        if (codAgravo.equals("Y59")) {
            retorno = "EVENTOS ADVERSOS GRAVES OU OBITOS POS-VACINACAO";
        }
        if (codAgravo.equals("A959")) {
            retorno = "FEBRE AMARELA";
        }
        if (codAgravo.equals("A920")) {
            retorno = "FEBRE DE CHIKUNGUNYA";
        }
        if (codAgravo.equals("A923")) {
            retorno = "FEBRE DO NILO OCIDENTAL";
        }
        if (codAgravo.equals("A779")) {
            retorno = "FEBRE MACULOSA E OUTRAS RIQUETISIOSES";
        }
        if (codAgravo.equals("A484")) {
            retorno = "FEBRE PURPURICA BRASILEIRA";
        }
        if (codAgravo.equals("J11")) {
            retorno = "INFLUENZA HUMANA PRODUZIDA POR NOVO SUBTIPO VIRAL";
        }
        if (codAgravo.equals("A962")) {
            retorno = "LASSA";
        }
        if (codAgravo.equals("B54")) {
            retorno = "MALARIA NA REGIAO EXTRA AMAZONICA";
        }
        if (codAgravo.equals("A983")) {
            retorno = "MARBURG";
        }
        if (codAgravo.equals("A809")) {
            retorno = "PARALISIA FLACIDA AGUDA";
        }
        if (codAgravo.equals("A209")) {
            retorno = "PESTE";
        }
        if (codAgravo.equals("A829")) {
            retorno = "RAIVA HUMANA";
        }
        if (codAgravo.equals("B092")) {
            retorno = "RUBEOLA";
        }
        if (codAgravo.equals("B091")) {
            retorno = "SARAMPO";
        }
        if (codAgravo.equals("P350")) {
            retorno = "SINDROME DA RUBEOLA CONGENITA";
        }
        if (codAgravo.equals("U049")) {
            retorno = "SINDROME RESPIRATORIA AGUDA GRAVE ASSOCIADA A CORONAVIRUS";
        }
        if (codAgravo.equals("A219")) {
            retorno = "TULAREMIA";
        }
        if (codAgravo.equals("B03")) {
            retorno = "VARIOLA";
        }

        return retorno;
    }

    public HashMap<String, OportunidadeAgravoPQAVS> populaAgravosBeans(String agravoSelecionado) {
        DBFUtil utilDbf = new DBFUtil();
        HashMap<String, String> hashAgravos = new HashMap<String, String>();
        HashMap<String, OportunidadeAgravoPQAVS> agravosBeans = new HashMap<String, OportunidadeAgravoPQAVS>();
        //se codRegional estiver preenchida, deve buscar somente os municipios pertencentes a ela

        //variavel para controlar exantematica
        int exantematica = 1;
        //busca agravos
        List<String> agravos = null;
        if (agravoSelecionado.equals("TODOS") || agravoSelecionado.isEmpty()) {
            agravos = getAgravosValidos();
        } else {
            agravos = new ArrayList<String>();
            agravos.add(agravoSelecionado);
        }
        for (int i = 0; i < agravos.size(); i++) {

            OportunidadeAgravoPQAVS agravoDbf = new OportunidadeAgravoPQAVS();
            if (agravos.get(i).equals("B09")) {
                agravoDbf.setCodAgravo("B09" + exantematica);
                if (exantematica == 1) {
                    agravoDbf.setNmAgravo("SARAMPO");
                    exantematica++;
                    i--;
                } else {
                    agravoDbf.setNmAgravo("RUBEOLA");
                }
            } else {
                agravoDbf.setCodAgravo(agravos.get(i));
                agravoDbf.setNmAgravo(buscaNomeAgravo(agravos.get(i)));
            }
            agravoDbf.setQtdDataInvalida(0);
            agravoDbf.setQtdInoportuno(0);
            agravoDbf.setQtdNaoEncerrado(0);
            agravoDbf.setQtdOportuno(0);
            agravoDbf.setTotal(0);
            hashAgravos.put(agravoDbf.getCodAgravo(), agravoDbf.getNmAgravo());
            agravosBeans.put(agravoDbf.getCodAgravo(), agravoDbf);
        }

        hashAgravos = this.sortHashMapByValues(hashAgravos, false);
        Set<String> agravoKeys = hashAgravos.keySet();
        HashMap<String, OportunidadeAgravoPQAVS> agravosBeansRetorno = new HashMap<String, OportunidadeAgravoPQAVS>();
        Iterator valueIt = agravoKeys.iterator();
        while (valueIt.hasNext()) {
            String key = (String) valueIt.next();
            agravosBeansRetorno.put(key, agravosBeans.get(key));
        }
        return agravosBeansRetorno;
    }

    private boolean verificaAgravoOportuno(String agravo) {
        if (this.getAgravosValidos().contains(agravo)) {
            return true;
        }
        return false;
    }

    public String formataData(String data) {
        return data.split("-")[2] + "/" + data.split("-")[1] + "/" + data.split("-")[0];
    }

    private String retornaNome(String codMunicipio) {
        String nomeMunicipio = "";
        for (int i = 0; i < getMunicipios().size(); i++) {
            if (getMunicipios().get(i).getCodMunicipio().equals(codMunicipio)) {
                nomeMunicipio = getMunicipios().get(i).getNmMunicipio();
            }
        }
        return nomeMunicipio;
    }

    private int retornaDiferenca(String agravo) {
        if (agravo.equals("B19") || agravo.equals("B551") || agravo.equals("P350")) {
            return 180;
        }
        return 60;
    }

    private String classificaSituacao(String situacao) {
        if (situacao.equals("Oportuno")) {
            return "1";
        }
        if (situacao.equals("Inoportuno com outras categorias")) {
            return "2";
        }
        if (situacao.equals("Inconclusivo")) {
            return "2";
        }
        if (situacao.equals("Não Encerrado")) {
            return "3";
        }
        if (situacao.equals("Data inválida")) {
            return "4";
        }
        return "";

    }

    /**
     * POPULA O BEAN PARA EXPORTAR PARA DBF
     *
     * @param return null
     */
    private void populaBeanExportacaoDBF(String situacao, String agravo, String idMunicipioNotificacao,
            String idMunicipioResidencia, String dtNotificacao,
            String nmNotificacao, String idUnidade, String dtEncerramento) {
        CasoOportunidadePQAVS caso = new CasoOportunidadePQAVS();
        caso.setAgravo(agravo);
        caso.setSituacao(classificaSituacao(situacao));
        caso.setIdMunicipio(idMunicipioNotificacao);
        caso.setIdMunicipioResidencia(idMunicipioResidencia);
        caso.setDtNotificacao(dtNotificacao);
        caso.setNumNotificacao(nmNotificacao);
        caso.setIdUnidade(idUnidade);
        caso.setAno(dtNotificacao.substring(6, 10));
        caso.setSgUF(idMunicipioNotificacao.substring(0, 2));
        if (idMunicipioResidencia != null) {
            caso.setSgUFResidencia(idMunicipioResidencia.substring(0, 2));
        }
        caso.setDtEncerramento(dtEncerramento);
        getListExportacao().add(caso);
    }

    /**
     * classifica a oportunidade da notificacao
     *
     * @param diferencaDatas
     * @param diferencaValida indica se eh um agravo de 180 ou 60 dias
     * @param agravoBean retorna qual situacao foi classificada
     */
    private String classificaNotificacao(int diferencaDatas, int diferencaValida, OportunidadeAgravoPQAVS agravoBean, int classificaoFinal) {
        String retorno = "";
        if (diferencaDatas < 0) {
            agravoBean.setQtdDataInvalida(Integer.valueOf(agravoBean.getQtdDataInvalida().intValue() + 1));
            retorno = "Data Inválida";
        } else {
            if (diferencaDatas > diferencaValida) {
                if (classificaoFinal == 8) {
                    agravoBean.setQtdInoportuno(Integer.valueOf(agravoBean.getQtdInoportuno().intValue() + 1));
                    retorno = "Inconclusivo";
                } else {
                    agravoBean.setQtdInoportunoOutras(Integer.valueOf(agravoBean.getQtdInoportunoOutras().intValue() + 1));
                    retorno = "Inoportuno com outras categorias";
                }
            } else {
                if (diferencaDatas <= diferencaValida) {
                    agravoBean.setQtdOportuno(Integer.valueOf(agravoBean.getQtdOportuno().intValue() + 1));
                    retorno = "Oportuno";
                }
            }
        }
        agravoBean.setTotal(Integer.valueOf(agravoBean.getTotal().intValue() + 1));
        return retorno;
    }

    /**
     *
     * @param reader
     * @param parametros
     * @return uma lista dos resultados com beans
     * @throws ParseException
     * @throws DBFException CALCULO A PARTIR DA BASE DBF - POR ESTADO OU
     * MUNICIPIO
     */
//    ALTERAR ESSA MÉTODO --> INCLUIR ALGORITMO NOVO
    @SuppressWarnings("SizeReplaceableByIsEmpty")
    private List calculaEstadoMunicipio(DBFReader reader, Map parametros) throws ParseException, DBFException {
        //buscar os municipios que vao para o resultado
        HashMap<String, OportunidadeAgravoPQAVS> municipiosBeans = new HashMap<String, OportunidadeAgravoPQAVS>();
        DBFUtil utilDbf = new DBFUtil();
        List<String> municipiosRegionais = new ArrayList<String>();
        CasoOportunidadePQAVS casoListado = null;
        boolean todosMunicipio = true;
        //busca o parametro agravo
        String agravoSelecionado = buscaCodigoAgravo(parametros.get("parAgravo").toString());
        Agravo agravodbf = new Agravo();
        
        if (agravoSelecionado.equals("")) {
            agravoSelecionado = "TODOS";
        }
        //inicia variavel com agravos válidos
        iniciaAgravosValidos();
        //verificar se a calculo é para ser listado por agravo ou por municipio
        if (parametros.get("parNomeMunicipio") == null) {
            parametros.put("parNomeMunicipio", "");
            todosMunicipio = false;
        }
        if (parametros.get("parCodRegional") == null) {
            parametros.put("parCodRegional", "");
        }
        if (parametros.get("parNomeMunicipio").equals("TODOS")) {
            todosMunicipio = true;
        }
        if (parametros.get("parNomeMunicipio").equals("-- Selecione --") && parametros.get("parCodRegional").toString().length() > 0) {
            todosMunicipio = true;
            parametros.put("parNomeMunicipio", "TODOS");
        }
        boolean porRegional = false;
        if (todosMunicipio && parametros.get("parCodRegiaoSaude").toString().length() > 0) {
            municipiosRegionais = verificaMunicipioRegiaoSaude(parametros.get("parCodRegiaoSaude").toString());
            porRegional = true;
        }
        //selecionou um municipio
        if (!parametros.get("parNomeMunicipio").equals("") && !parametros.get("parNomeMunicipio").equals("TODOS")) {
            //popular somente com o municipio selecionado
            if (parametros.get("parDiscriminarPorAgravo").equals(false) || parametros.get("parDiscriminarPorAgravo").equals("")) {
                municipiosBeans = populaMunicipiosBeansOportuno(parametros.get("parSgUf").toString(), parametros.get("parMunicipio").toString(), parametros.get("parCodRegional").toString());
                setPorAgravo(false);
            } else {
                municipiosBeans = populaAgravosBeans(agravoSelecionado);
                setPorAgravo(true);
            }
        } else {
            if (parametros.get("parDiscriminarPorAgravo").equals(false)) {
                parametros.put("parNomeMunicipio", "Todos Municípios");
            }
            if (parametros.get("parNomeMunicipio").toString().equals("TODOS")) {

                municipiosBeans = populaMunicipiosBeansOportuno(parametros.get("parSgUf").toString(), "", parametros.get("parCodRegiaoSaude").toString());
                setPorAgravo(false);
            } else {
                municipiosBeans = populaAgravosBeans(agravoSelecionado);
                setPorAgravo(true);
            }
        }

        //inicia o calculo
        Object[] rowObjects;
        java.sql.Date dtNotificacao, dtEncerramento;
        String agravo;
        OportunidadeAgravoPQAVS municipioResidencia;
        String dataInicio = (String) parametros.get("parDataInicio");
        String dataFim60 = (String) parametros.get("parDataFim60");
        String dataFim180 = (String) parametros.get("parDataFim180");
        String ufResidencia = (String) parametros.get("parUf");
        String ufResidenciaDbf = "";
        String parMunicipioResidencia = (String) parametros.get("parMunicipio");
        String parMunicipioResidenciaDbf = "";
        String semEpidemilogica;
        String codMunicipio;
        String anoEdpid = "";
        Map<String, Integer> mapaSemEpidemiologica;
        int i = 1;
        String[] arquivos = parametros.get("parArquivos").toString().split("\\|\\|");
        boolean denqueOnLine = SinanUtil.verificaDBDengueOnLine(arquivos);
        
        for (int k = 0; k < arquivos.length; k++) {
            i = 1;
            String arquivo = arquivos[k].substring(0, 5);
            try {
                reader = Util.retornaObjetoDbfCaminhoArquivo(arquivos[k].substring(0, arquivos[k].length() - 4), Configuracao.getPropriedade("caminho"));
                utilDbf.mapearPosicoes(reader);
                double TotalRegistros = Double.parseDouble(String.valueOf(reader.getRecordCount()));
                while ((rowObjects = reader.nextRecord()) != null) {
                    //cálculo da taxa estadual
                    //verifica a uf de residencia
                    if (utilDbf.getString(rowObjects, "SG_UF_NOT") != null) {

                        codMunicipio = utilDbf.getString(rowObjects, "ID_MUNICIP");
                        semEpidemilogica = utilDbf.getString(rowObjects, "SEM_NOT");
                        dtNotificacao = utilDbf.getDate(rowObjects, "DT_NOTIFIC");
                        ufResidenciaDbf = utilDbf.getString(rowObjects, "SG_UF_NOT");

                        if (semEpidemilogica != null && semEpidemilogica.length() >= 4) {
                            anoEdpid = semEpidemilogica.substring(0, 4);
                        } else {
                            anoEdpid = "0";
                        }

                        if (utilDbf.getString(rowObjects, "NU_NOTIFIC").equals("5039435") 
                                || utilDbf.getString(rowObjects, "NU_NOTIFIC").equals("9948375")) {
                            SinanUtil.imprimirConsole(utilDbf.getString(rowObjects, "NU_NOTIFIC"));
                        }
                        if (municipiosBeans.containsKey(codMunicipio)
                                && anoEdpid.length() == 4
                                && parametros.get("parAnoAvaliacao").equals(anoEdpid)
                                && semEpidemilogica.length() == 6) {
                            if (municipiosBeans.get(codMunicipio) != null
                                    && municipiosBeans.get(codMunicipio).getMapaSemEpidemiologica().size() == 0) {
                                mapaSemEpidemiologica = new HashMap<String, Integer>();
                                mapaSemEpidemiologica.put(semEpidemilogica, 1);
                                municipiosBeans.get(codMunicipio).setMapaSemEpidemiológica(mapaSemEpidemiologica);
                                municipiosBeans.get(codMunicipio).setTotal(1);
                            } else {
                                if (municipiosBeans.get(codMunicipio).getMapaSemEpidemiologica().get(semEpidemilogica) == null) {
                                    municipiosBeans.get(codMunicipio).getMapaSemEpidemiologica().put(semEpidemilogica, 1);
                                    municipiosBeans.get(codMunicipio).setTotal(municipiosBeans.get(codMunicipio).getTotal() + 1);
                                }
                            }

                        }
                        float percentual = Float.parseFloat(String.valueOf(i)) / Float.parseFloat(String.valueOf(TotalRegistros)) * 100;
                        getBarraStatus().setString((int) percentual + "%");
                        getBarraStatus().setValue((int) percentual);
                        i++;
                    }
                }

            } catch (DBFException ex) {
                Master.mensagem("Erro:\n" + ex);
            }
        }
        //CALCULA A TAXA PARA TODOS OS MUNICIPIOS
        List<OportunidadeAgravoPQAVS> beans = new ArrayList();
        Collection<OportunidadeAgravoPQAVS> municipioBean = municipiosBeans.values();

        for (Iterator<OportunidadeAgravoPQAVS> it = municipioBean.iterator(); it.hasNext();) {
            OportunidadeAgravoPQAVS agravoDBF = it.next();
//            if (agravoDBF.getTotal().intValue() > 0) {
            beans.add(agravoDBF);
//            }
        }
        Collections.sort(beans, new BeanComparator("nmAgravo"));

        Collections.sort(this.getListagemCasos(), new BeanComparator("situacao"));
        Collections.sort(this.getListagemCasos(), new BeanComparator("nmMunicipio"));

        //calcular o total
        OportunidadeAgravoPQAVS agravoBean = new OportunidadeAgravoPQAVS();
        agravoBean.setNmAgravo("TOTAL");
        for (i = 0; i < beans.size(); i++) {
            agravoBean.setQtdOportuno(Integer.valueOf(beans.get(i).getQtdOportuno().intValue() + agravoBean.getQtdOportuno().intValue()));
            agravoBean.setQtdInoportuno(Integer.valueOf(beans.get(i).getQtdInoportuno().intValue() + agravoBean.getQtdInoportuno().intValue()));
            agravoBean.setQtdInoportunoOutras(Integer.valueOf(beans.get(i).getQtdInoportunoOutras().intValue() + agravoBean.getQtdInoportunoOutras().intValue()));
            agravoBean.setQtdNaoEncerrado(Integer.valueOf(beans.get(i).getQtdNaoEncerrado().intValue() + agravoBean.getQtdNaoEncerrado().intValue()));
            agravoBean.setQtdDataInvalida(Integer.valueOf(beans.get(i).getQtdDataInvalida().intValue() + agravoBean.getQtdDataInvalida().intValue()));
            agravoBean.setTotal(Integer.valueOf(beans.get(i).getTotal().intValue() + agravoBean.getTotal().intValue()));
        }
        //se nao tiver selecionado "todos municipios" limpar beans
        if (!isPorAgravo() && !todosMunicipio) {
            beans = new ArrayList<OportunidadeAgravoPQAVS>();
            agravoBean.setNmAgravo(parametros.get("parSgUf").toString());
        }
        beans.add(agravoBean);
        return beans;
    }

    public HashMap<String, OportunidadeAgravoPQAVS> populaUfsBeansOportuno() {
        DBFUtil utilDbf = new DBFUtil();
        HashMap<String, String> uf = new HashMap<String, String>();
        HashMap<String, OportunidadeAgravoPQAVS> ufsBeans = new HashMap<String, OportunidadeAgravoPQAVS>();
        //se codRegional estiver preenchida, deve buscar somente os municipios pertencentes a ela

        //busca municipios dessa regional
        DBFReader readerMunicipio = Util.retornaObjetoDbfCaminhoArquivo("UF", "dbf\\");
        Object[] rowObjects1;
        try {
            utilDbf.mapearPosicoes(readerMunicipio);

            while ((rowObjects1 = readerMunicipio.nextRecord()) != null) {

                OportunidadeAgravoPQAVS agravoDbf = new OportunidadeAgravoPQAVS();
                agravoDbf.setNmAgravo(utilDbf.getString(rowObjects1, "SG_UF"));
                agravoDbf.setUf(utilDbf.getString(rowObjects1, "SG_UF"));
                agravoDbf.setCodAgravo(utilDbf.getString(rowObjects1, "ID_UF"));
                agravoDbf.setRegiao(buscaRegiao(agravoDbf.getNmAgravo())); //CRIAR MÉTODO PARA BUSCAR A REGIÃO DE SAÚDE A PARTIR DO MUNICÍPIO
                agravoDbf.setCodRegiaoSaude(utilDbf.getString(rowObjects1, "ID_REGIAO"));
                try {
                    agravoDbf.setRegiaoSaude(buscaRegiaoSaude(utilDbf.getString(rowObjects1, "ID_REGIAO")));
                } catch (SQLException ex) {
                    Logger.getLogger(SemEpidPQAVS.class.getName()).log(Level.SEVERE, null, ex);
                }
                agravoDbf.setQtdDataInvalida(0);
                agravoDbf.setQtdInoportuno(0);
                agravoDbf.setQtdNaoEncerrado(0);
                agravoDbf.setQtdOportuno(0);
                agravoDbf.setTotal(0);
                uf.put(utilDbf.getString(rowObjects1, "ID_UF"), utilDbf.getString(rowObjects1, "SG_UF"));
                ufsBeans.put(agravoDbf.getCodAgravo(), agravoDbf);
            }
        } catch (DBFException e) {
            Master.mensagem("Erro ao carregar municipios:\n" + e);
        }
        uf = this.sortHashMapByValues(uf, false);
        Set<String> ufKeys = uf.keySet();
        HashMap<String, OportunidadeAgravoPQAVS> ufsBeansRetorno = new HashMap<String, OportunidadeAgravoPQAVS>();
        Iterator valueIt = ufKeys.iterator();
        while (valueIt.hasNext()) {
            String key = (String) valueIt.next();
            ufsBeansRetorno.put(key, ufsBeans.get(key));
        }
        return ufsBeansRetorno;
    }

    /**
     *
     * @param reader
     * @param parametros
     * @return uma lista dos resultados com beans
     * @throws ParseException
     * @throws DBFException CALCULO A PARTIR DA BASE DBF - BRASIL
     */
    //ALTERAR O ALGORITMO AQUI
    // *********************************************
    public List getCalculaResultado(DBFReader reader, Map parametros) throws ParseException, DBFException {

        //verifica se o calculo é o brasil
        if (!parametros.get("parUf").equals("brasil")) {
            return calculaEstadoMunicipio(reader, parametros);
        }
        CasoOportunidadePQAVS casoListado = null;
        HashMap<String, OportunidadeAgravoPQAVS> municipiosBeans = new HashMap<String, OportunidadeAgravoPQAVS>();
        Map<String, Integer> mapaSemEpidemiologica;
        DBFUtil utilDbf = new DBFUtil();
        Agravo agravodbf = new Agravo();
        Object[] rowObjects;
        java.sql.Date dtNotificacao, dtEncerramento;
        String agravo;
        String codMunicipio;
        String semEpidemilogica;
        OportunidadeAgravoPQAVS municipioResidencia;
        String dataInicio = (String) parametros.get("parDataInicio");
        String dataFim60 = (String) parametros.get("parDataFim60");
        String dataFim180 = (String) parametros.get("parDataFim180");
        int i = 1;
        String anoEdpid = "";
        //busca o parametro agravo
        String agravoSelecionado = buscaCodigoAgravo(parametros.get("parAgravo").toString());

        //buscar os municipios que vao para o resultado
        String coluna;
        if (parametros.get("municipios").toString().equals("sim")) {
            municipiosBeans = populaMunicipiosBeansOportuno("BR", "", "");
            coluna = "ID_MN_RESI";
        } else {
            municipiosBeans = populaUfsBeansOportuno();// PREENCHE AS COLUNAS COM AS UFs. CRIAR RECURSO PARA AS REGIÕES
            coluna = "SG_UF";
        }

        //AQUI JÁ TEM O MAPA POPULADO COM TODOS OS MUNICÍPIOS (municipiosBeans)
        //inicia variavel com agravos válidos
        iniciaAgravosValidos();
        if (parametros.get("parDiscriminarPorAgravo").equals(true)) {
            municipiosBeans = populaAgravosBeans(agravoSelecionado);
            coluna = "ID_AGRAVO";
        }

        //inicia o calculo
        String[] arquivos = parametros.get("parArquivos").toString().split("\\|\\|");
        boolean denqueOnLine = SinanUtil.verificaDBDengueOnLine(arquivos);
        for (int k = 0; k < arquivos.length; k++) {
            i = 1;
            String arquivo = arquivos[k].substring(0, 5);
            try {
                reader = Util.retornaObjetoDbfCaminhoArquivo(arquivos[k].substring(0, arquivos[k].length() - 4), Configuracao.getPropriedade("caminho"));
                utilDbf.mapearPosicoes(reader);
                int TotalRegistrosInt = reader.getRecordCount();
                while ((rowObjects = reader.nextRecord()) != null) {

                    codMunicipio = utilDbf.getString(rowObjects, "ID_MUNICIP");
                    semEpidemilogica = utilDbf.getString(rowObjects, "SEM_NOT");
                    dtNotificacao = utilDbf.getDate(rowObjects, "DT_NOTIFIC");

                    //   String ano = dtNotificacao.toString().substring(0, 4);
                    //     anoEdpid = semEpidemilogica.substring(0, 4);
                    if (semEpidemilogica != null && semEpidemilogica.length() >= 4) {
                        anoEdpid = semEpidemilogica.substring(0, 4);
                    } else {
                        anoEdpid = "0";
                    }

                    //         SinanUtil.imprimirConsole(anoEdpid + ' '+ anoEdpid.length()  + ' '+ semEpidemilogica.length());
                    if (municipiosBeans.containsKey(codMunicipio) && anoEdpid.length() == 4 && parametros.get("parAnoAvaliacao").equals(anoEdpid) && semEpidemilogica.length() == 6) {
                        if (municipiosBeans.get(codMunicipio) != null && municipiosBeans.get(codMunicipio).getMapaSemEpidemiologica().size() == 0) {
                            mapaSemEpidemiologica = new HashMap<String, Integer>();
                            mapaSemEpidemiologica.put(semEpidemilogica, 1);
                            municipiosBeans.get(codMunicipio).setMapaSemEpidemiológica(mapaSemEpidemiologica);
                            municipiosBeans.get(codMunicipio).setTotal(1);
                        } else {
                            if (municipiosBeans.get(codMunicipio).getMapaSemEpidemiologica().get(semEpidemilogica) == null) {
                                municipiosBeans.get(codMunicipio).getMapaSemEpidemiologica().put(semEpidemilogica, 1);
                                municipiosBeans.get(codMunicipio).setTotal(municipiosBeans.get(codMunicipio).getTotal() + 1);
                            }
                        }

                    }
//                    
//                    if(anoEdpid.length() == 4 && parametros.get("parAnoAvaliacao").equals(anoEdpid) && semEpidemilogica.length() == 6){
//                        if(municipiosBeans.get(codMunicipio) != null && municipiosBeans.get(codMunicipio).getMapaSemEpidemiologica().size() == 0){
//                            mapaSemEpidemiologica = new HashMap<String, Integer>();
//                            mapaSemEpidemiologica.put(semEpidemilogica, 1);
//                            municipiosBeans.get(codMunicipio).setMapaSemEpidemiológica(mapaSemEpidemiologica);
//                            municipiosBeans.get(codMunicipio).setTotal(1);
//                       }else{
//                            if(municipiosBeans.get(codMunicipio).getMapaSemEpidemiologica().get(semEpidemilogica) == null){
//                                municipiosBeans.get(codMunicipio).getMapaSemEpidemiologica().put(semEpidemilogica, 1);
//                                municipiosBeans.get(codMunicipio).setTotal(municipiosBeans.get(codMunicipio).getTotal() + 1);
//                            }
//                        }
//                        
//                    }
                    //TAIDSON    
                    if (utilDbf.getString(rowObjects, "NU_NOTIFIC").equals("7675226")) {
                        SinanUtil.imprimirConsole(utilDbf.getString(rowObjects, "NU_NOTIFIC"));
                    }

                    /*

                    //verifica a uf de residencia é diferente de null
                    if (utilDbf.getString(rowObjects, coluna) != null) {
                        agravo = utilDbf.getString(rowObjects, "ID_AGRAVO");
                        
                        //teste para desconsiderar do cálculo dos os registros de dengue do arquivo nindi, quando selecionado arquivo de dengue online
                        if(arquivo.equals("DENGO") || (arquivo.equals("NINDI") && denqueOnLine && !agravo.equals("A90")) || (arquivo.equals("NINDI") && !denqueOnLine)){
                        
                            if (agravo.equals("B09")) {
                                int suspeita = utilDbf.getInt(rowObjects, "CS_SUSPEIT");
                                if (suspeita == 1) {
                                    agravo = "B091";
                                } else {
                                    agravo = "B092";
                                }
                            }
                            if (coluna.equals("ID_AGRAVO")) {
                                municipioResidencia = municipiosBeans.get(agravo);
                            } else {
                                municipioResidencia = municipiosBeans.get(utilDbf.getString(rowObjects, coluna));
                            }
                            //verifica se existe a referencia do municipio no bean


                            //busca data de notificacao e encerramento
                            dtNotificacao = utilDbf.getDate(rowObjects, "DT_NOTIFIC");
                            dtEncerramento = utilDbf.getDate(rowObjects, "DT_ENCERRA");
                            boolean continuaCalculo = true;



                            //se o agravo for dengue, verifica classificacao final
                            /*
                            if (agravo.equals("A90")) {
                                String classificacaoFinal = utilDbf.getString(rowObjects, "CLASSI_FIN", 1);
                                if (classificacaoFinal == null) {
                                    continuaCalculo = false;
                                } else {
                                    int cf = Integer.parseInt(classificacaoFinal);
                                    if (cf < 2 || cf > 4) {
                                        continuaCalculo = false;
                                    }
                                }
                            }*/
                    //se o agravo for dengue, verifica se é óbito por Dengue ou óbito em investigação
                    /*
                            if (agravo.equals("A90")) {
                                String evolucao = utilDbf.getString(rowObjects, "EVOLUCAO", 1);
                                if (evolucao == null) {
                                    continuaCalculo = false;
                                } else {
                                    int cf = Integer.parseInt(evolucao);
                                    if (cf < 2 || cf > 4) {
                                        continuaCalculo = false;
                                    }
                                }
                            }
                            

                            //verifica se foi selecionado um agravo especifico
                            if (agravoSelecionado.length() > 0) {
                                if (!agravoSelecionado.equals(agravo)) {
                                    continuaCalculo = false;
                                }
                            }

//                            int anoAvaliadoMalaria = Integer.parseInt(parametros.get("parDataInicio").toString().split("-")[0]);
//                            if (agravoSelecionado.length() == 0 && anoAvaliadoMalaria == 2008 && agravo.equals("B54")) {
//                                continuaCalculo = false;
//                            }
//                            if (anoAvaliadoMalaria == 2008 && agravoSelecionado.equals("B54")) {
//                                Master.mensagem("Malária está disponível a partir do ano de 2009");
//                                return null;
//                            }
                            if (municipioResidencia != null && verificaAgravoOportuno(agravo) && continuaCalculo) {
                                //verifica qual agravo para designar a dataFinal de avaliacao
                                String dataFim = dataFim60;
                                String colunaClassificacao = "CLASSI_FIN";
                                if (agravo.equals("B19") || agravo.equals("P350") || agravo.equals("B551")) {
                                    dataFim = dataFim180;
                                    if (agravo.equals("B551")) {
                                        colunaClassificacao = "CLASSI_FIN";
                                    }
                                }
                                //PAREI AQUI
                                
                               
                                
                                if (agravodbf.isBetweenDates(dtNotificacao, dataInicio, dataFim)) {
                                    casoListado = new CasoOportunidadePQAVS();
                                    String situacao = "";
                                    //verifica se a data de encerramento é nula
                                    if (dtEncerramento == null) {
                                        //se o usuario marcou que quer a listagem, armazenar o caso no array listagemCasos
                                        if (this.isTemListagem()) {
                                            casoListado.setAgravo(buscaNomeAgravo(agravo));
                                            casoListado.setDtNotificacao(formataData(dtNotificacao.toString()));
                                            casoListado.setIdUnidade(utilDbf.getString(rowObjects, "ID_UNIDADE"));
                                            casoListado.setNumNotificacao(utilDbf.getString(rowObjects, "NU_NOTIFIC"));
                                            casoListado.setIdMunicipio(utilDbf.getString(rowObjects, "ID_MUNICIP"));
                                            String nomeMunicipio = retornaNome(utilDbf.getString(rowObjects, "ID_MN_RESI"));
                                            if (nomeMunicipio.equals("")) {
                                                Municipio municipioNovo = new Municipio();
                                                municipioNovo.setCodMunicipio(utilDbf.getString(rowObjects, "ID_MN_RESI"));
                                                municipioNovo.setNmMunicipio(agravodbf.getNomeMunicipio(utilDbf.getString(rowObjects, "ID_MN_RESI")));
                                                casoListado.setNmMunicipio(municipioNovo.getNmMunicipio());
                                                getMunicipios().add(municipioNovo);
                                            } else {
                                                casoListado.setNmMunicipio(nomeMunicipio);
                                            }
                                            casoListado.setSituacao("Não Encerrado");
                                            this.getListagemCasosPQAVS().add(casoListado);
                                        }
                                        situacao = "Não Encerrado";
                                        municipioResidencia.setQtdNaoEncerrado(Integer.valueOf(municipioResidencia.getQtdNaoEncerrado().intValue() + 1));
                                        municipioResidencia.setTotal(Integer.valueOf(municipioResidencia.getTotal().intValue() + 1));
                                    } else {
                                        int diferenca = this.dataDiff(dtNotificacao, dtEncerramento);
                                        int diferencaOportuna = retornaDiferenca(agravo);
                                        situacao = classificaNotificacao(diferenca, diferencaOportuna, municipioResidencia, utilDbf.getInt(rowObjects, colunaClassificacao));

                                        if (situacao.equals("Data Inválida")) {
                                            System.out.println("dt_notificacao:" + formataData(dtNotificacao.toString()) + "\nnum notificacao:" + utilDbf.getString(rowObjects, "NU_NOTIFIC") + "\nmunicipio:" + utilDbf.getString(rowObjects, "ID_MUNICIP"));
                                        }
                                        if (this.isTemListagem() && !situacao.equals("Oportuno") && !situacao.equals("Inoportuno com outras categorias")) {
                                            casoListado.setAgravo(buscaNomeAgravo(agravo));
                                            casoListado.setDtNotificacao(formataData(dtNotificacao.toString()));
                                            casoListado.setIdUnidade(utilDbf.getString(rowObjects, "ID_UNIDADE"));
                                            casoListado.setNumNotificacao(utilDbf.getString(rowObjects, "NU_NOTIFIC"));
                                            casoListado.setIdMunicipio(utilDbf.getString(rowObjects, "ID_MUNICIP"));
                                            String nomeMunicipio = retornaNome(utilDbf.getString(rowObjects, "ID_MN_RESI"));
                                            if (nomeMunicipio.equals("")) {
                                                Municipio municipioNovo = new Municipio();
                                                municipioNovo.setCodMunicipio(utilDbf.getString(rowObjects, "ID_MN_RESI"));
                                                municipioNovo.setNmMunicipio(agravodbf.getNomeMunicipio(utilDbf.getString(rowObjects, "ID_MN_RESI")));
                                                casoListado.setNmMunicipio(municipioNovo.getNmMunicipio());
                                                getMunicipios().add(municipioNovo);
                                            } else {
                                                casoListado.setNmMunicipio(nomeMunicipio);
                                            }
                                            casoListado.setSituacao(situacao);
                                            this.getListagemCasosPQAVS().add(casoListado);
                                        }
                                    }
                                    //ALIMENTAR PARA EXPORTAR PARA DBF
                                    String dtEncerramentoExportacao;
                                    if (dtEncerramento == null) {
                                        dtEncerramentoExportacao = "";
                                    } else {
                                        dtEncerramentoExportacao = formataData(dtEncerramento.toString());
                                    }
    //                            populaBeanExportacaoDBF(situacao, buscaNomeAgravo(agravo), utilDbf.getString(rowObjects, "ID_MUNICIP"),
    //                                    utilDbf.getString(rowObjects, "ID_MN_RESI"), formataData(dtNotificacao.toString()),
    //                                    utilDbf.getString(rowObjects, "NU_NOTIFIC"), utilDbf.getString(rowObjects, "ID_UNIDADE"),
    //                                    dtEncerramentoExportacao);
                                }

                            }
                        }   
                    }*/
                    float percentual = Float.parseFloat(String.valueOf(i)) / Float.parseFloat(String.valueOf(TotalRegistrosInt)) * 100;
                    //getBarraStatus().setValue((int) percentual);
                    getBarraStatus().setString((int) percentual + "%");
                    getBarraStatus().setValue((int) percentual);
                    i++;
                    //System.out.println(i);
                }

            } catch (DBFException ex) {
                Master.mensagem("Erro:\n" + ex);
            }
        }
        //CALCULA A TAXA PARA TODOS OS MUNICIPIOS
        List<OportunidadeAgravoPQAVS> beans = new ArrayList();
        Collection<OportunidadeAgravoPQAVS> municipioBean = municipiosBeans.values();

        for (Iterator<OportunidadeAgravoPQAVS> it = municipioBean.iterator(); it.hasNext();) {
            OportunidadeAgravoPQAVS agravoDBF = it.next();
            beans.add(agravoDBF);
        }
        Collections.sort(beans, new BeanComparator("nmAgravo"));
        if (!parametros.get("municipios").toString().equals("sim") && parametros.get("parDiscriminarPorAgravo").equals(false)) {
            Collections.sort(beans, new BeanComparator("regiao"));
        }

        Collections.sort(this.getListagemCasos(), new BeanComparator("situacao"));
        Collections.sort(this.getListagemCasos(), new BeanComparator("nmMunicipio"));
        //calcular o total
        OportunidadeAgravoPQAVS agravoBean = new OportunidadeAgravoPQAVS();
        agravoBean.setNmAgravo("TOTAL");
        for (i = 0; i < beans.size(); i++) {
            agravoBean.setQtdOportuno(Integer.valueOf(beans.get(i).getQtdOportuno().intValue() + agravoBean.getQtdOportuno().intValue()));
            agravoBean.setQtdInoportuno(Integer.valueOf(beans.get(i).getQtdInoportuno().intValue() + agravoBean.getQtdInoportuno().intValue()));
            agravoBean.setQtdInoportunoOutras(Integer.valueOf(beans.get(i).getQtdInoportunoOutras().intValue() + agravoBean.getQtdInoportunoOutras().intValue()));
            agravoBean.setQtdNaoEncerrado(Integer.valueOf(beans.get(i).getQtdNaoEncerrado().intValue() + agravoBean.getQtdNaoEncerrado().intValue()));
            agravoBean.setQtdDataInvalida(Integer.valueOf(beans.get(i).getQtdDataInvalida().intValue() + agravoBean.getQtdDataInvalida().intValue()));
            agravoBean.setTotal(Integer.valueOf(beans.get(i).getTotal().intValue() + agravoBean.getTotal().intValue()));
        }
        beans.add(agravoBean);
        return beans;
    }

    private String converteCodigoParaCompleto(String agravoSelecionado) {
        if (agravoSelecionado.length() > 3) {
            return agravoSelecionado.substring(0, 3) + "." + agravoSelecionado.substring(3);
        }
        return agravoSelecionado;
    }

    /**
     *
     * @param con
     * @param parametros
     * @return uma lista dos resultados com beans
     * @throws SQLException CALCULO A PARTIR DA BASE POSTGRES - USADO PARA
     * CALCULAR TODOS OS MUNICIPIOS
     */
    private List getCalculaResultadoPorMunicipio(Connection con, Map parametros) throws SQLException {
        String sql;
        String municipio, noMunicipio = null;
        String situacao = "", codCid = "", rsIdMunicipio = "", rsIdMunicipioResidencia = "", rsNumeroNotificacao = "", rsIdUnidade = "";
        java.sql.Statement stm = con.createStatement();
        PreparedStatement stm2;
        ResultSet rs, rs2;
        OportunidadeAgravoPQAVS agravoBean = null;
        List<OportunidadeAgravoPQAVS> beans = new ArrayList();
        CasoOportunidadePQAVS casoListado = null;
        boolean prosseguir = true;
        if (parametros.get("parNomeRegional").equals("-- Selecione --") || parametros.get("parNomeRegional").equals("TODAS") || parametros.get("parNomeRegional").equals("")) {
            sql = "select co_municipio_ibge,no_municipio from dbgeral.tb_municipio where sg_uf = '" + parametros.get("parSgUf") + "' order by no_municipio";
        } else {
            sql = "select t1.co_municipio_ibge,no_municipio from dbgeral.tb_municipio as t1, dblocalidade.rl_regional_municipio_svs as t2 where t2.co_uf_ibge=" + parametros.get("parUf") + " and t1.co_municipio_ibge=t2.co_municipio_ibge and co_regional = '" + parametros.get("parCodRegional") + "' and no_municipio not like '%Ignorado%'  order by no_municipio";
        }
        try {
            rs = stm.executeQuery(sql);
        } catch (Exception exception) {
            if (parametros.get("parNomeRegional").equals("TODAS") || parametros.get("parNomeRegional").equals("-- Selecione --")) {
                sql = "select co_municipio_ibge,no_municipio from dbgeral.tb_municipio where sg_uf = '" + parametros.get("parSgUf") + "' order by no_municipio";
            } else {
                sql = "select t1.co_municipio_ibge,no_municipio from dbgeral.tb_municipio as t1, dblocalidade.rl_regional_municipio_svs as t2 where t2.co_uf_ibge=" + parametros.get("parUf") + " and t1.co_municipio_ibge=t2.co_municipio_ibge and co_regional = '" + parametros.get("parCodRegional") + "' and no_municipio not like '%Ignorado%'  order by no_municipio";
            }
            rs = stm.executeQuery(sql);
        }
        //calcula para cada municipio
        while (rs.next()) {

            municipio = rs.getString("co_municipio_ibge");
            noMunicipio = rs.getString("no_municipio");
            getBarraStatus().setString("Aguarde, calculado do município de " + noMunicipio);
            //adicona no list o bean do municipio
            agravoBean = new OportunidadeAgravoPQAVS();
            agravoBean.setNmAgravo(noMunicipio);
            beans.add(agravoBean);
            //busca o total de encerrados, nao encerrados, data inválida e inoportunos

            //primeira busca é dos agravos de 180 dias
            //busca o parametro agravo
            String agravoSelecionado = parametros.get("parAgravo").toString();
            if (agravoSelecionado.equals("TODOS") || agravoSelecionado.equals("B19") || agravoSelecionado.equals("B551") || agravoSelecionado.equals("P350")) {
                if (agravoSelecionado.equals("TODOS")) {
                    sql = this.getSqlCalculoAgravos180dias() + " and co_municipio_residencia = ? " + " order by co_cid,tp_suspeita";
                } else {
                    sql = this.getSqlCalculoAgravoEspecifico() + " and co_municipio_residencia = ? " + " order by co_cid,tp_suspeita";
                    agravoSelecionado = converteCodigoParaCompleto(agravoSelecionado);
                    prosseguir = false;
                }
                stm2 = con.prepareStatement(sql);
                //passa data inicial
                stm2.setDate(1, this.transformaDate(parametros.get("parDataInicio").toString()));
                //passa data final
                stm2.setDate(2, this.transformaDate(parametros.get("parDataFim180").toString()));
                //parametro uf
                stm2.setString(3, parametros.get("parUf").toString());
                //paremetro municipio

                if (!agravoSelecionado.equals("TODOS")) {
                    stm2.setString(4, agravoSelecionado);
                    stm2.setString(5, municipio);
                } else {
                    stm2.setString(4, municipio);
                }
                rs2 = stm2.executeQuery();

                //looping para somar a quantidade de cada agravo e adicionar no municipio
                while (rs2.next()) {
                    //verifica se tem data de encerramento
                    casoListado = new CasoOportunidadePQAVS();
                    Date dtEncerramento = rs2.getDate("dt_encerramento");
                    Date dtNotificacao = rs2.getDate("dt_notificacao");
                    codCid = rs2.getString("co_cid");
                    rsIdMunicipio = rs2.getString("co_municipio_notificacao");
                    rsIdMunicipioResidencia = rs2.getString("co_municipio_residencia");
                    rsNumeroNotificacao = rs2.getString("nu_notificacao");
                    rsIdUnidade = rs2.getString("co_unidade_notificacao");

                    if (dtEncerramento == null) {
                        //se o usuario marcou que quer a listagem, armazenar o caso no array listagemCasos

                        if (this.isTemListagem()) {
                            casoListado.setAgravo(codCid);
                            casoListado.setDtNotificacao(formataData(dtNotificacao.toString()));
                            casoListado.setIdUnidade(rsIdUnidade);
                            casoListado.setNumNotificacao(rsNumeroNotificacao);
                            casoListado.setIdMunicipio(rsIdMunicipio);
                            casoListado.setNmMunicipio(rs2.getString("no_municipio"));
                            casoListado.setSituacao("Não Encerrado");
                            casoListado.setIdMunicipioResidencia(rsIdMunicipioResidencia);
                            casoListado.setSgUF(rs2.getString("co_uf_notificacao"));
                            casoListado.setSgUFResidencia(rs2.getString("co_uf_residencia"));
                            casoListado.setAno(casoListado.getDtNotificacao().substring(6, 10));
                            this.getListagemCasosPQAVS().add(casoListado);
                        }
                        situacao = "Não encerrado";
                        agravoBean.setQtdNaoEncerrado(Integer.valueOf(agravoBean.getQtdNaoEncerrado().intValue() + 1));
                        agravoBean.setTotal(Integer.valueOf(agravoBean.getTotal().intValue() + 1));

                    } else {
                        situacao = this.classificaNotificacao(rs2.getInt("diff"), 180, agravoBean, rs2.getInt("tp_classificacao_final"));
                        if (this.isTemListagem() && !situacao.equals("Oportuno") && !situacao.equals("Inoportuno com outras categorias")) {
                            casoListado.setAgravo(codCid);
                            casoListado.setDtNotificacao(formataData(dtNotificacao.toString()));
                            casoListado.setIdUnidade(rsIdUnidade);
                            casoListado.setNumNotificacao(rsNumeroNotificacao);
                            casoListado.setIdMunicipio(rsIdMunicipio);
                            casoListado.setNmMunicipio(rs2.getString("no_municipio"));
                            casoListado.setSituacao(situacao);
                            casoListado.setIdMunicipioResidencia(rsIdMunicipioResidencia);
                            casoListado.setSgUF(rs2.getString("co_uf_notificacao"));
                            casoListado.setSgUFResidencia(rs2.getString("co_uf_residencia"));
                            casoListado.setAno(casoListado.getDtNotificacao().substring(6, 10));
                            this.getListagemCasosPQAVS().add(casoListado);
                        }
                    }
                    //ALIMENTAR PARA EXPORTAR PARA DBF
                    String dtEncerramentoExportacao;
                    if (dtEncerramento == null) {
                        dtEncerramentoExportacao = "";
                    } else {
                        dtEncerramentoExportacao = formataData(dtEncerramento.toString());
                    }
                    populaBeanExportacaoDBF(situacao, codCid, rsIdMunicipio,
                            rsIdMunicipioResidencia, formataData(dtNotificacao.toString()),
                            rsNumeroNotificacao, rsIdUnidade, dtEncerramentoExportacao);
                }
            }
            //-----------------------------------------------------------------------------------------
            //calcula para os agravos de 60 dias
            //-----------------------------------------------------------------------------------------
            if (prosseguir) {
                //pega ano avaliado
                int anoAvaliadoMalaria = Integer.parseInt(parametros.get("parDataInicio").toString().split("-")[0]);
                if (agravoSelecionado.equals("TODOS")) {
                    //se o ano selecionado for maior que 2008, pegar sql com malaria
                    if (anoAvaliadoMalaria > 2008) {
                        stm2 = con.prepareStatement(this.getSqlCalculoAgravosMalaria60dias() + " and co_municipio_residencia = ? " + " order by co_cid,tp_suspeita");
                    } else //calcular os agravos de 60 dias
                    {
                        stm2 = con.prepareStatement(this.getSqlCalculoAgravos60dias() + " and co_municipio_residencia = ? " + " order by co_cid,tp_suspeita");

                    }
                } else {
                    if (anoAvaliadoMalaria == 2008 && agravoSelecionado.equals("B54")) {
                        Master.mensagem("Malária está disponível a partir do ano de 2009");
                        return null;
                    }
                    agravoSelecionado = converteCodigoParaCompleto(agravoSelecionado);
                    if (agravoSelecionado.equals("A90")) {
                        stm2 = con.prepareStatement(this.getSqlCalculoAgravoEspecifico() + " and tp_classificacao_final in(2,3,4) and co_municipio_residencia = ? " + " order by co_cid,tp_suspeita");
                    } else {
                        stm2 = con.prepareStatement(this.getSqlCalculoAgravoEspecifico() + " and co_municipio_residencia = ? " + " order by co_cid,tp_suspeita");
                    }
                }

                //passa data inicial
                stm2.setDate(1, this.transformaDate(parametros.get("parDataInicio").toString()));
                //passa data final
                stm2.setDate(2, this.transformaDate(parametros.get("parDataFim60").toString()));
                //parametro uf
                stm2.setString(3, parametros.get("parUf").toString());
                //paremetro municipio
                if (!agravoSelecionado.equals("TODOS")) {
                    stm2.setString(4, agravoSelecionado);
                    stm2.setString(5, municipio);
                } else {
                    stm2.setString(4, municipio);
                }

                rs2 = stm2.executeQuery();
                //looping para somar a quantidade de cada agravo
                while (rs2.next()) {
                    codCid = rs2.getString("co_cid");
                    //se o agravo for diferente, comecar a calcular
                    //se for exantemtica, verificar tp_suspeita. Se for 1 = sarampo || 2 = rubéola
                    if (codCid.equals("DOENCAS EXANTEMATICAS")) {
                        if (rs2.getInt("tp_suspeita") == 1) {
                            codCid = "SARAMPO";
                        } else {
                            codCid = "RUBEOLA";
                        }
                    }
                    casoListado = new CasoOportunidadePQAVS();
                    Date dtEncerramento = rs2.getDate("dt_encerramento");
                    Date dtNotificacao = rs2.getDate("dt_notificacao");
                    codCid = rs2.getString("co_cid");
                    rsIdMunicipio = rs2.getString("co_municipio_notificacao");
                    rsIdMunicipioResidencia = rs2.getString("co_municipio_residencia");
                    rsNumeroNotificacao = rs2.getString("nu_notificacao");
                    rsIdUnidade = rs2.getString("co_unidade_notificacao");

                    //verifica se tem data de encerramento
                    if (dtEncerramento == null) {
                        //se o usuario marcou que quer a listagem, armazenar o caso no array listagemCasos
                        if (this.isTemListagem()) {
                            casoListado.setAgravo(codCid);
                            casoListado.setDtNotificacao(formataData(dtNotificacao.toString()));
                            casoListado.setIdUnidade(rsIdUnidade);
                            casoListado.setNumNotificacao(rsNumeroNotificacao);
                            casoListado.setIdMunicipio(rsIdMunicipio);
                            casoListado.setNmMunicipio(rs2.getString("no_municipio"));
                            casoListado.setSituacao("Não Encerrado");
                            casoListado.setIdMunicipioResidencia(rsIdMunicipioResidencia);
                            casoListado.setSgUF(rs2.getString("co_uf_notificacao"));
                            casoListado.setSgUFResidencia(rs2.getString("co_uf_residencia"));
                            casoListado.setAno(casoListado.getDtNotificacao().substring(6, 10));
                            this.getListagemCasosPQAVS().add(casoListado);
                            situacao = "Não Encerrado";
                        }
                        agravoBean.setQtdNaoEncerrado(Integer.valueOf(agravoBean.getQtdNaoEncerrado().intValue() + 1));
                        agravoBean.setTotal(Integer.valueOf(agravoBean.getTotal().intValue() + 1));
                    } else {
                        situacao = this.classificaNotificacao(rs2.getInt("diff"), 60, agravoBean, rs2.getInt("tp_classificacao_final"));
                        if (this.isTemListagem() && !situacao.equals("Oportuno") && !situacao.equals("Inoportuno com outras categorias")) {
                            casoListado.setAgravo(codCid);
                            casoListado.setDtNotificacao(formataData(dtNotificacao.toString()));
                            casoListado.setIdUnidade(rsIdUnidade);
                            casoListado.setNumNotificacao(rsNumeroNotificacao);
                            casoListado.setIdMunicipio(rsIdMunicipio);
                            casoListado.setNmMunicipio(rs2.getString("no_municipio"));
                            casoListado.setSituacao(situacao);
                            casoListado.setIdMunicipioResidencia(rsIdMunicipioResidencia);
                            casoListado.setSgUF(rs2.getString("co_uf_notificacao"));
                            casoListado.setSgUFResidencia(rs2.getString("co_uf_residencia"));
                            casoListado.setAno(casoListado.getDtNotificacao().substring(6, 10));
                            this.getListagemCasosPQAVS().add(casoListado);
                        }
                    }
                    //ALIMENTAR PARA EXPORTAR PARA DBF
                    String dtEncerramentoExportacao;
                    if (dtEncerramento == null) {
                        dtEncerramentoExportacao = "";
                    } else {
                        dtEncerramentoExportacao = formataData(dtEncerramento.toString());
                    }
                    populaBeanExportacaoDBF(situacao, codCid, rsIdMunicipio,
                            rsIdMunicipioResidencia, formataData(dtNotificacao.toString()),
                            rsNumeroNotificacao, rsIdUnidade, dtEncerramentoExportacao);
                }
            }
        }
        getBarraStatus().setString(null);
        Collections.sort(beans, new BeanComparator("nmAgravo"));
        Collections.sort(this.getListagemCasos(), new BeanComparator("situacao"));
        Collections.sort(this.getListagemCasos(), new BeanComparator("nmMunicipio"));
        //calcular o total
        agravoBean = new OportunidadeAgravoPQAVS();
        agravoBean.setNmAgravo("TOTAL");
        for (int i = 0; i < beans.size(); i++) {
            agravoBean.setQtdOportuno(Integer.valueOf(beans.get(i).getQtdOportuno().intValue() + agravoBean.getQtdOportuno().intValue()));
            agravoBean.setQtdInoportuno(Integer.valueOf(beans.get(i).getQtdInoportuno().intValue() + agravoBean.getQtdInoportuno().intValue()));
            agravoBean.setQtdInoportunoOutras(Integer.valueOf(beans.get(i).getQtdInoportunoOutras().intValue() + agravoBean.getQtdInoportunoOutras().intValue()));
            agravoBean.setQtdNaoEncerrado(Integer.valueOf(beans.get(i).getQtdNaoEncerrado().intValue() + agravoBean.getQtdNaoEncerrado().intValue()));
            agravoBean.setQtdDataInvalida(Integer.valueOf(beans.get(i).getQtdDataInvalida().intValue() + agravoBean.getQtdDataInvalida().intValue()));
            agravoBean.setTotal(Integer.valueOf(beans.get(i).getTotal().intValue() + agravoBean.getTotal().intValue()));
        }
        beans.add(agravoBean);
        return beans;
    }

    /**
     *
     * @param con
     * @param parametros
     * @return uma lista dos resultados com beans
     * @throws SQLException CALCULO A PARTIR DA BASE POSTGRES
     */
    public List getCalculaResultado(Connection con, Map parametros) throws SQLException, ParseException {
        ResultSet rs2;
        PreparedStatement stm2;
        String situacao = "", codCid = "", rsIdMunicipio = "", rsIdMunicipioResidencia = "", rsNumeroNotificacao = "", rsIdUnidade = "";
        String agravo = "";
        String sql;
        OportunidadeAgravoPQAVS agravoBean = null;
        List<OportunidadeAgravoPQAVS> beans = new ArrayList();
        CasoOportunidadePQAVS casoListado = null;
        boolean prosseguir = true;
        //verifica se vai calcular por municipio
        if (parametros.get("parNomeMunicipio") == null) {
            parametros.put("parNomeMunicipio", "");
        }
        if (parametros.get("parNomeMunicipio").equals("TODOS")) {
            parametros.put("parNomeMunicipio", "Todos Municípios");
        }
        if (parametros.get("parNomeMunicipio").toString().equals("Todos Municípios")) {
            return getCalculaResultadoPorMunicipio(con, parametros);
        }
        //calcular os agravos de 180 dias
        //veririca se tem o municipio como parametro
        String municipio = "";
        if (parametros.get("parMunicipio") != null) {
            municipio = " and co_municipio_residencia = ? ";
        }

        //busca o parametro agravo
        String agravoSelecionado = buscaCodigoAgravo(parametros.get("parAgravo").toString());
        if (agravoSelecionado.equals("")) {
            agravoSelecionado = "TODOS";
        }
        if (agravoSelecionado.equals("TODOS") || agravoSelecionado.equals("B19") || agravoSelecionado.equals("B551") || agravoSelecionado.equals("P350")) {
            if (agravoSelecionado.equals("TODOS")) {
                sql = this.getSqlCalculoAgravos180dias() + municipio + " order by co_cid,tp_suspeita";
            } else {
                sql = this.getSqlCalculoAgravoEspecifico() + municipio + " order by co_cid,tp_suspeita";
                agravoSelecionado = converteCodigoParaCompleto(agravoSelecionado);
                prosseguir = false;
            }
            stm2 = con.prepareStatement(sql);
            //passa data inicial
            stm2.setDate(1, this.transformaDate(parametros.get("parDataInicio").toString()));
            //passa data final
            stm2.setDate(2, this.transformaDate(parametros.get("parDataFim180").toString()));
            //parametro uf
            stm2.setString(3, parametros.get("parUf").toString());
            //paremetro municipio
            if (!municipio.equals("")) {
                if (!agravoSelecionado.equals("TODOS")) {
                    stm2.setString(4, agravoSelecionado);
                    stm2.setString(5, parametros.get("parMunicipio").toString());
                } else {
                    stm2.setString(4, parametros.get("parMunicipio").toString());
                }

            } else if (!agravoSelecionado.equals("TODOS")) {
                stm2.setString(4, agravoSelecionado);
            }
            rs2 = stm2.executeQuery();

            //looping para somar a quantidade de cada agravo
            while (rs2.next()) {
                codCid = rs2.getString("co_cid");
                //se o agravo for diferente, comecar a calcular
                if (!agravo.equals(codCid)) {
                    //se agravo for diferente do inicio, inserir na lista para retornar no final
                    agravoBean = new OportunidadeAgravoPQAVS();
                    agravoBean.setNmAgravo(codCid);
                    beans.add(agravoBean);
                    agravo = codCid;
                }
                //verifica se tem data de encerramento
                casoListado = new CasoOportunidadePQAVS();
                Date dtEncerramento = rs2.getDate("dt_encerramento");
                Date dtNotificacao = rs2.getDate("dt_notificacao");
                codCid = rs2.getString("co_cid");
                rsIdMunicipio = rs2.getString("co_municipio_notificacao");
                rsIdMunicipioResidencia = rs2.getString("co_municipio_residencia");
                rsNumeroNotificacao = rs2.getString("nu_notificacao");
                rsIdUnidade = rs2.getString("co_unidade_notificacao");
                if (dtEncerramento == null) {
                    //se o usuario marcou que quer a listagem, armazenar o caso no array listagemCasos
                    if (this.isTemListagem()) {
                        casoListado.setAgravo(codCid);
                        casoListado.setDtNotificacao(formataData(dtNotificacao.toString()));
                        casoListado.setIdUnidade(rsIdUnidade);
                        casoListado.setNumNotificacao(rsNumeroNotificacao);
                        casoListado.setIdMunicipio(rsIdMunicipio);
                        casoListado.setNmMunicipio(rs2.getString("no_municipio"));
                        casoListado.setSituacao("Não Encerrado");
                        casoListado.setIdMunicipioResidencia(rsIdMunicipioResidencia);
                        casoListado.setSgUF(rs2.getString("co_uf_notificacao"));
                        casoListado.setSgUFResidencia(rs2.getString("co_uf_residencia"));
                        casoListado.setAno(casoListado.getDtNotificacao().substring(6, 10));
                        this.getListagemCasosPQAVS().add(casoListado);
                    }
                    agravoBean.setQtdNaoEncerrado(Integer.valueOf(agravoBean.getQtdNaoEncerrado().intValue() + 1));
                    agravoBean.setTotal(Integer.valueOf(agravoBean.getTotal().intValue() + 1));
                    //popula o bean para exportacao

                } else {
                    situacao = this.classificaNotificacao(rs2.getInt("diff"), 180, agravoBean, rs2.getInt("tp_classificacao_final"));
                    if (this.isTemListagem() && !situacao.equals("Oportuno") && !situacao.equals("Inoportuno com outras categorias")) {
                        casoListado.setAgravo(codCid);
                        casoListado.setDtNotificacao(formataData(dtNotificacao.toString()));
                        casoListado.setIdUnidade(rsIdUnidade);
                        casoListado.setNumNotificacao(rsNumeroNotificacao);
                        casoListado.setIdMunicipio(rsIdMunicipio);
                        casoListado.setNmMunicipio(rs2.getString("no_municipio"));
                        casoListado.setSituacao(situacao);
                        casoListado.setIdMunicipioResidencia(rsIdMunicipioResidencia);
                        casoListado.setSgUF(rs2.getString("co_uf_notificacao"));
                        casoListado.setSgUFResidencia(rs2.getString("co_uf_residencia"));
                        casoListado.setAno(casoListado.getDtNotificacao().substring(6, 10));
                        this.getListagemCasosPQAVS().add(casoListado);
                    }
                }

                //ALIMENTAR PARA EXPORTAR PARA DBF
                String dtEncerramentoExportacao;
                if (dtEncerramento == null) {
                    dtEncerramentoExportacao = "";
                } else {
                    dtEncerramentoExportacao = formataData(dtEncerramento.toString());
                }
                populaBeanExportacaoDBF(situacao, codCid, rsIdMunicipio,
                        rsIdMunicipioResidencia, formataData(dtNotificacao.toString()),
                        rsNumeroNotificacao, rsIdUnidade, dtEncerramentoExportacao);
            }
        }

        if (prosseguir) {
            //pega ano avaliado
            int anoAvaliadoMalaria = Integer.parseInt(parametros.get("parDataInicio").toString().split("-")[0]);
            if (agravoSelecionado.equals("TODOS")) {
                //se o ano selecionado for maior que 2008, pegar sql com malaria
                if (anoAvaliadoMalaria > 2008) {
                    stm2 = con.prepareStatement(this.getSqlCalculoAgravosMalaria60dias() + municipio + " order by co_cid,tp_suspeita");
                } else //calcular os agravos de 60 dias
                {
                    stm2 = con.prepareStatement(this.getSqlCalculoAgravos60dias() + municipio + " order by co_cid,tp_suspeita");

                }
            } else {
                if (anoAvaliadoMalaria == 2008 && agravoSelecionado.equals("B54")) {
                    Master.mensagem("Malária está disponível a partir do ano de 2009");
                    return null;
                }
                agravoSelecionado = converteCodigoParaCompleto(agravoSelecionado);
                if (agravoSelecionado.equals("A90")) {
                    stm2 = con.prepareStatement(this.getSqlCalculoAgravoEspecifico() + " and tp_classificacao_final in(2,3,4) order by co_cid,tp_suspeita");
                } else {
                    stm2 = con.prepareStatement(this.getSqlCalculoAgravoEspecifico() + municipio + " order by co_cid,tp_suspeita");
                }

            }

            //passa data inicial
            stm2.setDate(1, this.transformaDate(parametros.get("parDataInicio").toString()));
            //passa data final
            stm2.setDate(2, this.transformaDate(parametros.get("parDataFim60").toString()));
            //parametro uf
            stm2.setString(3, parametros.get("parUf").toString());
            //paremetro municipio
            if (!municipio.equals("")) {
                if (!agravoSelecionado.equals("TODOS")) {
                    stm2.setString(4, agravoSelecionado);
                    stm2.setString(5, parametros.get("parMunicipio").toString());
                } else {
                    stm2.setString(4, parametros.get("parMunicipio").toString());
                }
            } else if (!agravoSelecionado.equals("TODOS")) {
                stm2.setString(4, agravoSelecionado);
            }
            rs2 = stm2.executeQuery();
            agravo = "";
            agravoBean = null;
            //looping para somar a quantidade de cada agravo
            while (rs2.next()) {
                codCid = rs2.getString("co_cid");
                //se o agravo for diferente, comecar a calcular
                //se for exantemtica, verificar tp_suspeita. Se for 1 = sarampo || 2 = rubéola
                if (codCid.equals("DOENCAS EXANTEMATICAS")) {
                    if (rs2.getInt("tp_suspeita") == 1) {
                        codCid = "SARAMPO";
                    } else {
                        codCid = "RUBEOLA";
                    }
                }
                if (!agravo.equals(codCid)) {
                    //se agravo for diferente do inicio, inserir na lista para retornar no final
                    agravoBean = new OportunidadeAgravoPQAVS();
                    agravoBean.setNmAgravo(codCid);
                    beans.add(agravoBean);
                    agravo = codCid;
                }
                //verifica se tem data de encerramento
                casoListado = new CasoOportunidadePQAVS();
                Date dtEncerramento = rs2.getDate("dt_encerramento");
                Date dtNotificacao = rs2.getDate("dt_notificacao");
                codCid = rs2.getString("co_cid");
                rsIdMunicipio = rs2.getString("co_municipio_notificacao");
                rsIdMunicipioResidencia = rs2.getString("co_municipio_residencia");
                rsNumeroNotificacao = rs2.getString("nu_notificacao");
                rsIdUnidade = rs2.getString("co_unidade_notificacao");

                if (dtEncerramento == null) {
                    //se o usuario marcou que quer a listagem, armazenar o caso no array listagemCasos
                    if (this.isTemListagem()) {
                        casoListado.setAgravo(codCid);
                        casoListado.setDtNotificacao(formataData(dtNotificacao.toString()));
                        casoListado.setIdUnidade(rsIdUnidade);
                        casoListado.setNumNotificacao(rsNumeroNotificacao);
                        casoListado.setIdMunicipio(rsIdMunicipio);
                        casoListado.setNmMunicipio(rs2.getString("no_municipio"));
                        casoListado.setSituacao("Não Encerrado");
                        casoListado.setIdMunicipioResidencia(rsIdMunicipioResidencia);
                        casoListado.setSgUF(rs2.getString("co_uf_notificacao"));
                        casoListado.setSgUFResidencia(rs2.getString("co_uf_residencia"));
                        casoListado.setAno(casoListado.getDtNotificacao().substring(6, 10));
                        this.getListagemCasosPQAVS().add(casoListado);
                    }
                    agravoBean.setQtdNaoEncerrado(Integer.valueOf(agravoBean.getQtdNaoEncerrado().intValue() + 1));
                    agravoBean.setTotal(Integer.valueOf(agravoBean.getTotal().intValue() + 1));
                } else {
                    situacao = this.classificaNotificacao(rs2.getInt("diff"), 60, agravoBean, rs2.getInt("tp_classificacao_final"));
                    if (this.isTemListagem() && !situacao.equals("Oportuno") && !situacao.equals("Inoportuno com outras categorias")) {
                        casoListado.setAgravo(codCid);
                        casoListado.setDtNotificacao(formataData(dtNotificacao.toString()));
                        casoListado.setIdUnidade(rsIdUnidade);
                        casoListado.setNumNotificacao(rsNumeroNotificacao);
                        casoListado.setIdMunicipio(rsIdMunicipio);
                        casoListado.setNmMunicipio(rs2.getString("no_municipio"));
                        casoListado.setSituacao(situacao);
                        casoListado.setIdMunicipioResidencia(rsIdMunicipioResidencia);
                        casoListado.setAno(casoListado.getDtNotificacao().substring(6, 10));
                        casoListado.setSgUF(rs2.getString("co_uf_notificacao"));
                        casoListado.setSgUFResidencia(rs2.getString("co_uf_residencia"));
                        this.getListagemCasosPQAVS().add(casoListado);
                    }
                }

                //ALIMENTAR PARA EXPORTAR PARA DBF
                String dtEncerramentoExportacao;
                if (dtEncerramento == null) {
                    dtEncerramentoExportacao = "";
                } else {
                    dtEncerramentoExportacao = formataData(dtEncerramento.toString());
                }
                populaBeanExportacaoDBF(situacao, codCid, rsIdMunicipio,
                        rsIdMunicipioResidencia, formataData(dtNotificacao.toString()),
                        rsNumeroNotificacao, rsIdUnidade, dtEncerramentoExportacao);
            }
        }
        if (beans.size() == 0) {
            agravoBean = new OportunidadeAgravoPQAVS();
            agravoBean.setNmAgravo(buscaNomeAgravo(agravoSelecionado));
            beans.add(agravoBean);
        }

        //calcular o total
        agravoBean = new OportunidadeAgravoPQAVS();
        agravoBean.setNmAgravo("TOTAL");
        for (int i = 0; i < beans.size(); i++) {
            agravoBean.setQtdOportuno(Integer.valueOf(beans.get(i).getQtdOportuno().intValue() + agravoBean.getQtdOportuno().intValue()));
            agravoBean.setQtdInoportuno(Integer.valueOf(beans.get(i).getQtdInoportuno().intValue() + agravoBean.getQtdInoportuno().intValue()));
            agravoBean.setQtdInoportunoOutras(Integer.valueOf(beans.get(i).getQtdInoportunoOutras().intValue() + agravoBean.getQtdInoportunoOutras().intValue()));
            agravoBean.setQtdNaoEncerrado(Integer.valueOf(beans.get(i).getQtdNaoEncerrado().intValue() + agravoBean.getQtdNaoEncerrado().intValue()));
            agravoBean.setQtdDataInvalida(Integer.valueOf(beans.get(i).getQtdDataInvalida().intValue() + agravoBean.getQtdDataInvalida().intValue()));
            agravoBean.setTotal(Integer.valueOf(beans.get(i).getTotal().intValue() + agravoBean.getTotal().intValue()));
        }
        beans.add(agravoBean);
        Collections.sort(beans, new BeanComparator("nmAgravo"));
        Collections.sort(this.getListagemCasos(), new BeanComparator("situacao"));
        Collections.sort(this.getListagemCasos(), new BeanComparator("nmMunicipio"));
        return beans;
    }

    @Override
    public List getBeansMunicipioEspecifico(Connection con, Map parametros) throws SQLException {
        return this.getBeanMunicipioEspecifico(con, parametros);
    }

    @Override
    public List getBeanMunicipioEspecifico(Connection con, Map parametros) throws SQLException {
        return getBeans();
    }

    @Override
    public List getBeansEstadoEspecifico(Connection con, Map parametros) throws SQLException {
        //        return this.getBeanEstadoEspecifico(con, parametros);
        return getBeans();
    }

    @Override
    public Map getParametros() {
        String dataInicio = "";
        String dataFim60 = "";
        String dataFim180 = "";
        try {
            if (this.dtInicioAvaliacao != null) {
                dataInicio = retornaDataInicioNotificacao(this.dtInicioAvaliacao, this.dataAvaliacao);
                dataFim60 = retornaDataFimNotificacao(this.dtFimAvaliacao, this.dataAvaliacao, 60);
                dataFim180 = retornaDataFimNotificacao(this.dtFimAvaliacao, this.dataAvaliacao, 180);
            } else {
                dataInicio = retornaDataInicio(this.anoAvaliado, this.dataAvaliacao);
                dataFim60 = retornaDataFim(this.anoAvaliado, this.dataAvaliacao, 60);
                dataFim180 = retornaDataFim(this.anoAvaliado, this.dataAvaliacao, 180);
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        Map parametros = new HashMap();
        parametros.put("parVersao", "");
        if (this.nomeAgravo.toString().equals("TODOS")) {
            parametros.put("parAgravo", "TODOS");
        } else {
            parametros.put("parAgravo", buscaCodigoAgravo(this.nomeAgravo.toString()));
        }
        if (this.dtInicioAvaliacao != null) {
            parametros.put("parAnoAvaliacao", this.dtInicioAvaliacao.toString() + " a " + this.dtFimAvaliacao.toString());
        } else {
            parametros.put("parAnoAvaliacao", this.anoAvaliado.toString());
        }
        parametros.put("parAnoAvaliacao", this.anoAvaliado.toString());
        parametros.put("parDataAvaliacao", "");
        parametros.put("parDataInicio", "");
        parametros.put("parDataFim60", "");
        parametros.put("parDataFim180", "");
        /*
        parametros.put("parDataAvaliacao", this.dataAvaliacao);
        parametros.put("parDataInicio", Util.formataData(dataInicio));
        parametros.put("parDataFim60", Util.formataData(dataFim60));
        parametros.put("parDataFim180", Util.formataData(dataFim180));
         */

        if (isPeriodoValido180dias() && isPeriodoValido60dias()) {
            parametros.put("parPeriodo", "\n    SRC, LTA e HEPATITES: de " + dataInicio + " a " + dataFim180 + "\n    Demais agravos de " + dataInicio + " a " + dataFim60);
            parametros.put("parPeriodo60", dataInicio + " a " + dataFim60);
        } else {
            if (!isPeriodoValido180dias()) {
                parametros.put("parPeriodo", "\n    SRC, LTA e HEPATITES: intervalo insuficiente " + "\n    Demais agravos de " + dataInicio + " a " + dataFim60);
                parametros.put("parPeriodo60", dataInicio + " a " + dataFim60);
            } else {
                if (!isPeriodoValido60dias()) {
                    parametros.put("parPeriodo", "\n    SRC, LTA e HEPATITES: intervalo insuficiente " + "\n    Demais agravos: intervalo insuficiente ");
                    parametros.put("parPeriodo60", " Intervalo insuficiente");
                }
            }
        }

        parametros.put("parTitulo1", "Proporção de Notificações Segundo Oportunidade do Encerramento da Investigação");
        parametros.put("parTituloColuna", "Coluna");
        parametros.put("parRodape", "Para a realização da avaliação da oportunidade do encerramento dos casos é verificado o percentual de casos notificados que foram encerrados oportunamente, isto é, as fichas de investigação que contém informações do " + "diagnóstico final e data do encerramento preenchidas, no prazo estabelecido para cada agravo.");
//        parametros.put("parAno", Util.getAno(dataFim60));
        if (getUf().equals("Brasil")) {
            parametros.put("parUf", "brasil");
            parametros.put("parSgUf", "Todas UFs");
        }
        if (uf.equals("Brasil")) {
            parametros.put("parTituloColuna", "Agravo");
        } else {
            if (municipio.isEmpty()) {
                parametros.put("parTituloColuna", "Agravo");
            } else {
                if (municipio.equals("Todos Municípios")) {
                    parametros.put("parTituloColuna", "Município");
                } else {
                    parametros.put("parTituloColuna", "Agravo");
                }
            }

        }
        parametros.put("parAgravo", this.nomeAgravo);
        return parametros;
    }

    private String retornaDataInicio(String anoAvaliado, String dataAvaliacao) throws ParseException {
        String anoAvaliacao = dataAvaliacao.split("/")[2];

        DateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
        Date dataInicio = new java.sql.Date(fmt.parse("01/01/" + anoAvaliacao).getTime());
        Date dataFim = new java.sql.Date(fmt.parse(dataAvaliacao).getTime());

        //se anos forem iguais
        if (anoAvaliacao.equals(anoAvaliado)) {
            //verificar dataAvaliacao-1/1/anoavaliado > 60 dias

            if (Agravo.dataDiff(dataInicio, dataFim) > 60) {
                return fmt.format(dataInicio).toString();
            } else {
                Master.mensagem("O intervalo entre data de avaliação e ano/período avaliado é insuficiente para avaliar agravos cujo prazo de encerramento oportuno é de 60 dias");
                setPeriodoValido60dias(false);
            }
        } else {
            return "01/01/" + anoAvaliado;
        }
        return null;
    }

    private String retornaDataFim(String ano, String dataAvaliacao, int intervalo) throws ParseException {
        DateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
        Calendar calendarData = Calendar.getInstance();
        Date dataFim = new java.sql.Date(fmt.parse(dataAvaliacao).getTime());
        calendarData.setTime(dataFim);
        calendarData.add(Calendar.DATE, -intervalo);
        java.util.Date dataFim2 = calendarData.getTime();
        //se anos forem iguais
        if (ano.equals(dataAvaliacao.split("/")[2])) {
            return fmt.format(dataFim2).toString();
        } else {
            //verifica se subtrair a data de avaliacao pelo intervalo vai ser menor que 31/12/anoAvaliado
            //se for menor a data final vai ser esta subtraçao
            //se for maior, a data final vai ser 31/12/anoAvaliado
            Date dataParametro = new java.sql.Date(fmt.parse("31/12/" + ano).getTime());
            if (dataFim2.before(dataParametro)) {
                return fmt.format(dataFim2).toString();
            } else {
                return "31/12/" + ano;
            }
        }
    }

    private String retornaDataInicioNotificacao(String dtInicioNotificacao, String dataAvaliacao) throws ParseException {
        //String anoAvaliacao = dataAvaliacao.split("/")[2];

        DateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
//      Date dataInicio = new java.sql.Date(fmt.parse("01/01/" + anoAvaliacao).getTime());
        Date dataInicio = new java.sql.Date(fmt.parse(dtInicioNotificacao).getTime());
        Date dataFim = new java.sql.Date(fmt.parse(dataAvaliacao).getTime());

        //verificar dataAvaliacao-1/1/anoavaliado > 60 dias
        if (Agravo.dataDiff(dataInicio, dataFim) > 60) {
            return fmt.format(dataInicio).toString();
        } else {
            Master.mensagem("O intervalo entre data de avaliação e ano/período avaliado é insuficiente para avaliar agravos cujo prazo de encerramento oportuno é de 60 dias");
            setPeriodoValido60dias(false);
        }

        return null;
    }

    private String retornaDataFimNotificacao(String dtFimNotificacao, String dataAvaliacao, int intervalo) throws ParseException {
        DateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
        Calendar calendarData = Calendar.getInstance();
        Date dataFim = new java.sql.Date(fmt.parse(dataAvaliacao).getTime());
        calendarData.setTime(dataFim);
        calendarData.add(Calendar.DATE, -intervalo);
        java.util.Date dataFim2 = calendarData.getTime();
        //se anos forem iguais

        //verifica se subtrair a data de avaliacao pelo intervalo vai ser menor que 31/12/anoAvaliado
        //se for menor a data final vai ser esta subtraçao
        //se for maior, a data final vai ser 31/12/anoAvaliado
        Date dataParametro = new java.sql.Date(fmt.parse(dtFimNotificacao).getTime());
        if (dataFim2.before(dataParametro)) {
            return fmt.format(dataFim2).toString();
        } else {
            return dtFimNotificacao;
        }
    }

    public String buscaCodigoAgravo(String nomeAgravo) {
        String retorno = "";

        if (nomeAgravo.equals("ANTRAZ PNEUMONICO")) {
            retorno = "A229";
        }
        if (nomeAgravo.equals("ARENAVIRUS")) {
            retorno = "A969";
        }
        if (nomeAgravo.equals("BOTULISMO")) {
            retorno = "A051";
        }
        if (nomeAgravo.equals("COLERA")) {
            retorno = "A009";
        }
        if (nomeAgravo.equals("DENGUE (OBITOS)")) {
            retorno = "A90";
        }
        if (nomeAgravo.equals("EBOLA")) {
            retorno = "A984";
        }
        if (nomeAgravo.equals("EVENTOS ADVERSOS GRAVES OU OBITOS POS-VACINACAO")) {
            retorno = "Y59";
        }
        if (nomeAgravo.equals("FEBRE AMARELA")) {
            retorno = "A959";
        }
        if (nomeAgravo.equals("FEBRE DE CHIKUNGUNYA")) {
            retorno = "A920";
        }
        if (nomeAgravo.equals("FEBRE DO NILO OCIDENTAL")) {
            retorno = "A923";
        }
        if (nomeAgravo.equals("FEBRE MACULOSA E OUTRAS RIQUETISIOSES")) {
            retorno = "A779";
        }
        if (nomeAgravo.equals("FEBRE PURPURICA BRASILEIRA")) {
            retorno = "A484";
        }
        if (nomeAgravo.equals("INFLUENZA HUMANA PRODUZIDA POR NOVO SUBTIPO VIRAL")) {
            retorno = "J11";
        }
        if (nomeAgravo.equals("LASSA")) {
            retorno = "A962";
        }
        if (nomeAgravo.equals("MALARIA NA REGIAO EXTRA AMAZONICA")) {
            retorno = "B54";
        }
        if (nomeAgravo.equals("MARBURG")) {
            retorno = "A983";
        }
        if (nomeAgravo.equals("PARALISIA FLACIDA AGUDA")) {
            retorno = "A809";
        }
        if (nomeAgravo.equals("PESTE")) {
            retorno = "A209";
        }
        if (nomeAgravo.equals("RAIVA HUMANA")) {
            retorno = "A829";
        }
        if (nomeAgravo.equals("RUBEOLA")) {
            retorno = "B092";
        }
        if (nomeAgravo.equals("SARAMPO")) {
            retorno = "B091";
        }
        if (nomeAgravo.equals("SINDROME DA RUBEOLA CONGENITA")) {
            retorno = "P350";
        }
        if (nomeAgravo.equals("SINDROME RESPIRATORIA AGUDA GRAVE ASSOCIADA A CORONAVIRUS")) {
            retorno = "U049";
        }
        if (nomeAgravo.equals("TULAREMIA")) {
            retorno = "A219";
        }
        if (nomeAgravo.equals("VARIOLA")) {
            retorno = "B03";
        }

        return retorno;
    }

    /**
     * @return the periodoValido180dias
     */
    public boolean isPeriodoValido180dias() {
        return periodoValido180dias;
    }

    /**
     * @param periodoValido180dias the periodoValido180dias to set
     */
    public void setPeriodoValido180dias(boolean periodoValido180dias) {
        this.periodoValido180dias = periodoValido180dias;
    }

    /**
     * @return the periodoValido60dias
     */
    public boolean isPeriodoValido60dias() {
        return periodoValido60dias;
    }

    /**
     * @param periodoValido60dias the periodoValido60dias to set
     */
    public void setPeriodoValido60dias(boolean periodoValido60dias) {
        this.periodoValido60dias = periodoValido60dias;
    }

    /**
     * @return the dataAvaliacao
     */
    public String getDataAvaliacao() {
        return dataAvaliacao;
    }

    /**
     * @param dataAvaliacao the dataAvaliacao to set
     */
    public void setDataAvaliacao(String dataAvaliacao) {
        this.dataAvaliacao = dataAvaliacao;
    }

    /**
     * @return the anoAvaliado
     */
    public String getAnoAvaliado() {
        return anoAvaliado;
    }

    /**
     * @param anoAvaliado the anoAvaliado to set
     */
    public void setAnoAvaliado(String anoAvaliado) {
        this.anoAvaliado = anoAvaliado;
    }

    public String getDtInicioAvaliacao() {
        return dtInicioAvaliacao;
    }

    public void setDtInicioAvaliacao(String dtInicioAvaliacao) {
        this.dtInicioAvaliacao = dtInicioAvaliacao;
    }

    public String getDtFimAvaliacao() {
        return dtFimAvaliacao;
    }

    public void setDtFimAvaliacao(String dtFimAvaliacao) {
        this.dtFimAvaliacao = dtFimAvaliacao;
    }

    /**
     * @return the nomeAgravo
     */
    public String getNomeAgravo() {
        return nomeAgravo;
    }

    /**
     * @param nomeAgravo the nomeAgravo to set
     */
    public void setNomeAgravo(String nomeAgravo) {
        this.nomeAgravo = nomeAgravo;
    }

    /**
     * @return the uf
     */
    public String getUf() {
        return uf;
    }

    /**
     * @param uf the uf to set
     */
    public void setUf(String uf) {
        this.uf = uf;
    }

    /**
     * @return the municipio
     */
    public String getMunicipio() {
        return municipio;
    }

    /**
     * @param municipio the municipio to set
     */
    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    @Override
    public HashMap<String, ColunasDbf> getColunas() {
        HashMap<String, ColunasDbf> hashColunas = new HashMap<String, ColunasDbf>();
        if (isPorAgravo()) {
            hashColunas.put("ID_AGRAVO", new ColunasDbf(5));
            hashColunas.put("DS_AGRAVO", new ColunasDbf(30));
        } else {
            hashColunas.put("ID_LOCRES", new ColunasDbf(7));
            hashColunas.put("DS_LOCRES", new ColunasDbf(30));
            hashColunas.put("ID_UFRES", new ColunasDbf(2));
        }
        hashColunas.put("NUM_NENC", new ColunasDbf(10, 0));
        hashColunas.put("PER_NENC", new ColunasDbf(5, 2));
        hashColunas.put("NUM_INOIN", new ColunasDbf(10, 0));
        hashColunas.put("PER_INOIN", new ColunasDbf(5, 2));
        hashColunas.put("NUM_INOOT", new ColunasDbf(10, 0));
        hashColunas.put("PER_INOOT", new ColunasDbf(5, 2));
        hashColunas.put("N_ENCOPOR", new ColunasDbf(10, 0));
        hashColunas.put("I_ENCOPOR", new ColunasDbf(5, 2));
        hashColunas.put("NUM_DTINV", new ColunasDbf(10, 0));
        hashColunas.put("PER_DTINV", new ColunasDbf(5, 2));
        hashColunas.put("D_ENCOPOR", new ColunasDbf(10, 0));
        hashColunas.put("ANO_NOTIF", new ColunasDbf(4, 0));
        hashColunas.put("DT_AVALIA", new ColunasDbf(10));
        hashColunas.put("ORIGEM", new ColunasDbf(30));
//        hashColunas.put("AGRAVO", new ColunasDbf(30));
//        hashColunas.put("ID_MUNIC", new ColunasDbf(6));
//        hashColunas.put("ID_MN_RES", new ColunasDbf(6));
//        hashColunas.put("DT_NOTIFIC", new ColunasDbf(10));
//        hashColunas.put("NU_NOTIFIC", new ColunasDbf(10));
//        hashColunas.put("ID_UNIDADE", new ColunasDbf(8));
//        hashColunas.put("SG_UF", new ColunasDbf(2));
//        hashColunas.put("NU_ANO", new ColunasDbf(4, 0));
//        hashColunas.put("UFRES", new ColunasDbf(2));
//        hashColunas.put("OPORTU", new ColunasDbf(2, 0));
//        hashColunas.put("ORIGEM", new ColunasDbf(30));
        this.setColunas(hashColunas);
        return hashColunas;
    }

    @Override
    public String[] getOrdemColunas() {
        // return new String[]{"AGRAVO", "ID_MUNIC", "ID_MN_RES", "DT_NOTIFIC", "NU_NOTIFIC", "ID_UNIDADE", "SG_UF", "NU_ANO", "UFRES", "OPORTU", "ORIGEM"};
        if (isPorAgravo()) {
            return new String[]{"ID_AGRAVO", "DS_AGRAVO", "NUM_NENC", "PER_NENC", "NUM_INOIN", "PER_INOIN",
                "NUM_INOOT", "PER_INOOT", "N_ENCOPOR", "I_ENCOPOR", "NUM_DTINV", "PER_DTINV", "D_ENCOPOR", "ANO_NOTIF",
                "DT_AVALIA", "ORIGEM"};
        } else {
            return new String[]{"ID_LOCRES", "DS_LOCRES", "ID_UFRES", "NUM_NENC", "PER_NENC", "NUM_INOIN", "PER_INOIN",
                "NUM_INOOT", "PER_INOOT", "N_ENCOPOR", "I_ENCOPOR", "NUM_DTINV", "PER_DTINV", "D_ENCOPOR", "ANO_NOTIF",
                "DT_AVALIA", "ORIGEM"};
        }
    }

    @Override
    public DBFWriter getLinhas(HashMap<String, ColunasDbf> colunas, List bean, DBFWriter writer) throws DBFException, IOException {
        int k;
        for (int i = 0; i < bean.size(); i++) {
            Object rowData[] = new Object[colunas.size()];
            OportunidadeAgravoPQAVS agravo = (OportunidadeAgravoPQAVS) bean.get(i);
            if (isPorAgravo()) {
                if (agravo.getCodAgravo() == null) {
                    rowData[0] = null;
                } else {
                    rowData[0] = agravo.getCodAgravo();
                }
                k = 2;
            } else {
                if (agravo.getCodAgravo() == null) {
                    rowData[0] = SinanUtil.siglaUFToIDUF(agravo.getNmAgravo());
                    rowData[2] = SinanUtil.siglaUFToIDUF(agravo.getNmAgravo());
                } else {
                    rowData[0] = agravo.getCodAgravo();
                    rowData[2] = agravo.getCodAgravo().substring(0, 2);
                }
                k = 3;
            }
            rowData[1] = agravo.getNmAgravo();

            rowData[k] = Double.parseDouble(agravo.getQtdNaoEncerrado().toString());
            k++;
            rowData[k] = Double.parseDouble(calculaPercentual(agravo.getQtdNaoEncerrado().toString(), agravo.getTotal().toString()));
            k++;
            rowData[k] = Double.parseDouble(agravo.getQtdInoportuno().toString());
            k++;
            rowData[k] = Double.parseDouble(calculaPercentual(agravo.getQtdInoportuno().toString(), agravo.getTotal().toString()));
            k++;
            rowData[k] = Double.parseDouble(agravo.getQtdInoportunoOutras().toString());
            k++;
            rowData[k] = Double.parseDouble(calculaPercentual(agravo.getQtdInoportunoOutras().toString(), agravo.getTotal().toString()));
            k++;
            rowData[k] = Double.parseDouble(agravo.getQtdOportuno().toString());
            k++;
            rowData[k] = Double.parseDouble(calculaPercentual(agravo.getQtdOportuno().toString(), agravo.getTotal().toString()));
            k++;
            rowData[k] = Double.parseDouble(agravo.getQtdDataInvalida().toString());
            k++;
            rowData[k] = Double.parseDouble(calculaPercentual(agravo.getQtdDataInvalida().toString(), agravo.getTotal().toString()));
            k++;
            if (agravo.getTotal() != null) {
                rowData[k] = Double.parseDouble(agravo.getTotal().toString());
                k++;
            } else {
                rowData[k] = Double.parseDouble("0");
                k++;
            }
            rowData[k] = Double.parseDouble(getAnoAvaliado());
            k++;
            rowData[k] = getDataAvaliacao();
            k++;
            rowData[k] = "ENCOPORTUNO-SINANNET";
//            CasoOportunidade agravo = (CasoOportunidade) bean.get(i);
//            rowData[0] = agravo.getAgravo();
//            rowData[1] = agravo.getIdMunicipio();
//            rowData[2] = agravo.getIdMunicipioResidencia();
//            rowData[3] = agravo.getDtNotificacao();
//            rowData[4] = agravo.getNumNotificacao();
//            rowData[5] = agravo.getIdUnidade();
//            rowData[6] = agravo.getSgUF();
//            rowData[7] = Double.parseDouble(agravo.getAno());
//            rowData[8] = agravo.getSgUFResidencia();
//            rowData[9] = Double.parseDouble(agravo.getSituacao());
//            rowData[10] = "OPORTUNIDADE-SINANNET";
            writer.addRecord(rowData);
        }
        return writer;
    }

    public String calculaPercentual(String num, String den) {
        double numd = Double.parseDouble(num);
        double dend = Double.parseDouble(den);
        DecimalFormat df = new DecimalFormat("0.00");
        if (dend > 0) {
            return df.format(numd / dend * 100).replace(",", ".");
        } else {
            return "0.0";
        }
    }

    @Override
    public String getCaminhoJasper() {
        return "/com/org/relatorios/oportunidadePQAVS.jasper";
    }

    @Override
    public String getTaxaEstado(Connection con, Map parametros) throws SQLException {
        if (isDBF()) {
            //ler o arquivo dbf
            DBFReader reader = null;// Util.retornaObjetoDbf(Configuracao.getPropriedade("caminho"));
//            if (reader == null) {
//                abreJanelaEscolherCaminho();
//            }
            calcula(reader, parametros);
            return getTaxaEstadual();
        } else {
            try {
                setBeans(getCalculaResultado(con, parametros));
            } catch (ParseException ex) {
                System.out.println(ex);
            }
            return getTaxaEstadual();
        }
    }

    /**
     * @return the sqlCalculoAgravos180dias
     */
    public String getSqlCalculoAgravos180dias() {
        return sqlCalculoAgravos180dias;
    }

    /**
     * @param sqlCalculoAgravos180dias the sqlCalculoAgravos180dias to set
     */
    public void setSqlCalculoAgravos180dias(String sqlCalculoAgravos180dias) {
        this.sqlCalculoAgravos180dias = sqlCalculoAgravos180dias;
    }

    /**
     * @return the sqlCalculoAgravos60dias
     */
    public String getSqlCalculoAgravos60dias() {
        return sqlCalculoAgravos60dias;
    }

    /**
     * @param sqlCalculoAgravos60dias the sqlCalculoAgravos60dias to set
     */
    public void setSqlCalculoAgravos60dias(String sqlCalculoAgravos60dias) {
        this.sqlCalculoAgravos60dias = sqlCalculoAgravos60dias;
    }

    /**
     * @return the sqlCalculoAgravosMalaria60dias
     */
    public String getSqlCalculoAgravosMalaria60dias() {
        return sqlCalculoAgravosMalaria60dias;
    }

    /**
     * @param sqlCalculoAgravosMalaria60dias the sqlCalculoAgravosMalaria60dias
     * to set
     */
    public void setSqlCalculoAgravosMalaria60dias(String sqlCalculoAgravosMalaria60dias) {
        this.sqlCalculoAgravosMalaria60dias = sqlCalculoAgravosMalaria60dias;
    }

    /**
     * @return the sqlCalculoAgravoEspecifico
     */
    public String getSqlCalculoAgravoEspecifico() {
        return sqlCalculoAgravoEspecifico;
    }

    /**
     * @param sqlCalculoAgravoEspecifico the sqlCalculoAgravoEspecifico to set
     */
    public void setSqlCalculoAgravoEspecifico(String sqlCalculoAgravoEspecifico) {
        this.sqlCalculoAgravoEspecifico = sqlCalculoAgravoEspecifico;
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
