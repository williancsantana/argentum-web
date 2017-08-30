/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.k
 */
package com.org.model.classes;
//teste

import com.linuxense.javadbf.DBFException;
import com.linuxense.javadbf.DBFReader;
import com.linuxense.javadbf.DBFWriter;
import com.org.bd.DBFUtil;
import com.org.model.classes.agravos.TuberculoseCoorte;
import com.org.negocio.Configuracao;
import com.org.model.classes.agravos.DengueLetalidade;
import com.org.model.classes.agravos.HanseniaseCoorte;
import com.org.negocio.Util;
import com.org.util.SinanUtil;
import com.org.view.Master;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Geraldo
 */
public class Agravo {

    private String nomeMunicipio;
    private String codMunicipio;
    private String numerador;
    private Integer numeradorInt;
    private Integer denominadorInt;
    private String denominador;
    private String periodo;
    private String taxa;
    private String tipoAgregacao;
    private String sqlNumeradorMunicipioEspecifico;
    private String sqlDenominadorMunicipioEspecifico;
    private String sqlNumeradorBeanMunicipios;
    private String sqlDenominadorBeanMunicipios;
    private String sqlNumeradorEstado;
    private String sqlDenominandorEstado;
    private String sqlNumeradorCompletitude;
    private String Titulo1;
    private String TextoCompletitude;
    private String TituloColuna;
    private String Rodape;
    private String Tipo;
    private int Multiplicador;
    private JLabel label;
    private String transfNaoEspecificada;
    private JProgressBar barraStatus;
    private JProgressBar barraStatusGeral;
    private HashMap<String, ColunasDbf> colunas;
    private Map parametros;
    private String caminhoJasper;
    private String[] ordemColunas;
    private Util util = new Util();
    private Configuracao conf = new Configuracao();
    private String arquivo;
    private String taxaEstadual;
    private List beans;
    private String percentualCompletitude;
    private boolean DBF;
    private String dataInicio;
    private String dataFim;
    //propriedade para oportunidade
    private String dataAvaliacao;
    private List<com.org.model.classes.agravos.oportunidade.CasoOportunidade> listagemCasos = new ArrayList<com.org.model.classes.agravos.oportunidade.CasoOportunidade>();
    private List<com.org.model.classes.agravos.oportunidade.CasoOportunidadeCOAP> listagemCasosCOAP = new ArrayList<com.org.model.classes.agravos.oportunidade.CasoOportunidadeCOAP>();
    private List<com.org.model.classes.agravos.oportunidade.CasoOportunidadePQAVS> listagemCasosPQAVS = new ArrayList<com.org.model.classes.agravos.oportunidade.CasoOportunidadePQAVS>();
    private String anoAvaliado;
    private String dtInicioAvaliacao;
    private String dtFimAvaliacao;
    private String dtInicioTransf;
    private String dtFimTransf;
    private String dtInicioReceb;
    private String dtFimReceb;
    private String nomeAgravo;
    private boolean temListagem;
    private String uf;
    private String municipio;
    private String regional;
    private String codRegional;
    private String regiaoSaude;
    private String codRegiaoSaude;

    public Agravo() {
    }

    public Agravo(boolean isDbf) {
        setDBF(isDbf);
    }

    public void init(String tipo) {
    }

    public Agravo adicionaBrasil(Collection<Agravo> municipioBean) {
        DecimalFormat df = new DecimalFormat("0.00");
        Agravo agravoBean = new Agravo();
        agravoBean.setNomeMunicipio("BRASIL");
        agravoBean.setCodMunicipio("51");
        agravoBean.setNumerador("0");
        agravoBean.setDenominador("0");
        for (Iterator<Agravo> it = municipioBean.iterator(); it.hasNext();) {
            Agravo agravoUF = it.next();
            agravoBean.setNumerador(String.valueOf(Integer.parseInt(agravoUF.getNumerador()) + Integer.parseInt(agravoBean.getNumerador())));
            agravoBean.setDenominador(String.valueOf(Integer.parseInt(agravoUF.getDenominador()) + Integer.parseInt(agravoBean.getDenominador())));

            if (agravoBean.getDenominador().equals("0")) {
                agravoBean.setTaxa("0.00");
            } else {
                agravoBean.setTaxa(df.format(Double.parseDouble(agravoBean.getNumerador()) / Double.parseDouble(agravoBean.getDenominador()) * this.getMultiplicador()));
            }

        }
        return agravoBean;
    }

    public Agravo adicionaRegional(Collection<Agravo> municipioBean, String nomeRegional, String codRegional) {
        DecimalFormat df = new DecimalFormat("0.00");
        Agravo agravoBean = new Agravo();
        agravoBean.setNomeMunicipio(nomeRegional);
        agravoBean.setCodMunicipio(codRegional);
        agravoBean.setNumerador("0");
        agravoBean.setDenominador("0");
        for (Iterator<Agravo> it = municipioBean.iterator(); it.hasNext();) {
            Agravo agravoUF = it.next();
            agravoBean.setNumerador(String.valueOf(Integer.parseInt(agravoUF.getNumerador()) + Integer.parseInt(agravoBean.getNumerador())));
            agravoBean.setDenominador(String.valueOf(Integer.parseInt(agravoUF.getDenominador()) + Integer.parseInt(agravoBean.getDenominador())));
            if (agravoBean.getDenominador().equals("0")) {
                agravoBean.setTaxa("0.00");
            } else {
                agravoBean.setTaxa(df.format(Double.parseDouble(agravoBean.getNumerador()) / Double.parseDouble(agravoBean.getDenominador()) * this.getMultiplicador()));
            }
        }
        return agravoBean;
    }

    public Agravo adicionaTotal(Collection<Agravo> municipioBean, String codRegiao) {
        DecimalFormat df = new DecimalFormat("0");
        Agravo agravoBean = new Agravo();
        agravoBean.setNomeMunicipio("TOTAL");
        agravoBean.setCodMunicipio(codRegiao);
        agravoBean.setNumerador("0");
        agravoBean.setDenominador("0");
        for (Iterator<Agravo> it = municipioBean.iterator(); it.hasNext();) {
            Agravo agravoUF = it.next();
            agravoBean.setNumerador(String.valueOf(Integer.parseInt(agravoUF.getNumerador()) + Integer.parseInt(agravoBean.getNumerador())));
            agravoBean.setDenominador(String.valueOf(Integer.parseInt(agravoUF.getDenominador()) + Integer.parseInt(agravoBean.getDenominador())));
            if (agravoBean.getTaxa() == null || agravoUF.getTaxa() == null) {
                agravoBean.setTaxa("0");
            } else {
                // agravoBean.setTaxa(df.format(Double.parseDouble(agravoBean.getNumerador()) / Double.parseDouble(agravoBean.getDenominador()) * this.getMultiplicador()));
                if (agravoUF.getTaxa().split(",").length > 1) {
                    agravoUF.setTaxa(agravoUF.getTaxa().split(",")[0] + "." + agravoUF.getTaxa().split(",")[1]);
                }
                agravoBean.setTaxa(df.format(Double.parseDouble(agravoUF.getTaxa()) + Double.parseDouble(agravoBean.getTaxa())));
            }
        }
        return agravoBean;
    }

    public String getNomeMunicipio(String codMunicipio) {
        DBFUtil utilDbf = new DBFUtil();
        String retorno = "";
        DBFReader readerMunicipio = Util.retornaObjetoDbfCaminhoArquivo("MUNICNET", "dbf\\");
        Object[] rowObjects1;
        try {
            utilDbf.mapearPosicoes(readerMunicipio);

            while ((rowObjects1 = readerMunicipio.nextRecord()) != null) {
                if (codMunicipio.equals(utilDbf.getString(rowObjects1, "ID_MUNICIP"))) {
                    retorno = utilDbf.getString(rowObjects1, "SG_UF") + " - " + utilDbf.getString(rowObjects1, "NM_MUNICIP");
                }
            }
        } catch (DBFException e) {
            Master.mensagem("Erro ao carregar municipios:\n" + e);
        }
        return retorno;
    }

    public String getNomeMunicipioNormal(String codMunicipio) {
        DBFUtil utilDbf = new DBFUtil();
        String retorno = "";
        DBFReader readerMunicipio = Util.retornaObjetoDbfCaminhoArquivo("MUNICNET", "dbf\\");
        Object[] rowObjects1;
        try {
            utilDbf.mapearPosicoes(readerMunicipio);

            while ((rowObjects1 = readerMunicipio.nextRecord()) != null) {
                if (codMunicipio.equals(utilDbf.getString(rowObjects1, "ID_MUNICIP"))) {
                    retorno = utilDbf.getString(rowObjects1, "NM_MUNICIP");
                }
            }
        } catch (DBFException e) {
            Master.mensagem("Erro ao carregar municipios:\n" + e);
        }
        return retorno;
    }

    public void initAdequadamente(String tipo) {
    }

    public String decodificaNome(String texto) {
        int i, j;
        String s = "";
        for (i = 0; i < texto.length(); i++) {
            int chr = texto.charAt(i);
            j = i;
            if (j == 0) {
                j++;
            }
            int temp = (chr + (j % 24));
            char c = (char) temp;
            s = s + c;
        }
        return s;
    }

    public List getBeanMunicipioEspecifico(Connection con, Map parametros) throws SQLException {
        if (isDBF()) {
            return getBeans();
        } else {
            getBarraStatus().setString("Aguarde... calculando município: ");
            String sql;
            java.sql.Statement stm = con.createStatement();
            ResultSet rs, rs2;
            int multiplicador;
            double numerador, denominador;
            String municipio, noMunicipio = null;
            List beans = new ArrayList();
            Agravo d1 = null;
            DecimalFormat df = new DecimalFormat("0.00");
            DecimalFormat df2 = new DecimalFormat(",###,##");
            municipio = parametros.get("parMunicipio").toString();
            PreparedStatement stm2;
            if (getTipo().equals("dengueGrave")) {
                rs2 = con.createStatement().executeQuery("SELECT  count(*) as numerador FROM  dbsinan.tb_notificacao " + "where co_cid = 'A90' and ds_semana_sintoma in (" + parametros.get("parDataInicio") + ") and " + "co_uf_residencia= " + parametros.get("parUf").toString() + " and co_municipio_residencia = " + municipio + " and " + "tp_evolucao_caso = '2' and tp_classificacao_final in (2,3,4)");
            } else {
                stm2 = con.prepareStatement(this.getSqlNumeradorMunicipioEspecifico());
                stm2.setDate(1, this.transformaDate(parametros.get("parDataInicio").toString()));
                stm2.setDate(2, this.transformaDate(parametros.get("parDataFim").toString()));
                if (getTipo().equals("trabalhador")) {
                    stm2.setString(3, municipio);
                    stm2.setDate(4, this.transformaDate(parametros.get("parDataInicio").toString()));
                    stm2.setDate(5, this.transformaDate(parametros.get("parDataFim").toString()));
                    stm2.setString(6, parametros.get("parUf").toString());
                    stm2.setString(7, municipio);

                } else {
                    stm2.setString(3, parametros.get("parUf").toString());
                    stm2.setString(4, municipio);
                }

                rs2 = stm2.executeQuery();
            }
            rs2.next();

            numerador = rs2.getDouble("numerador");
            if (getTipo().equals("dengueGrave")) {
                rs = con.createStatement().executeQuery("SELECT  count(*) as denominador FROM  dbsinan.tb_notificacao " + "where co_cid = 'A90' and ds_semana_sintoma in (" + parametros.get("parDataInicio") + ") and " + "co_uf_residencia= " + parametros.get("parUf").toString() + " and co_municipio_residencia = " + municipio + " and " + " tp_classificacao_final in (2,3,4)");
            } else {
                stm2 = con.prepareStatement(this.getSqlDenominadorMunicipioEspecifico());
                if (this.getTipo().equals("populacao")) {
                    stm2.setString(1, municipio);
                    stm2.setString(2, parametros.get("parAno").toString());
                    multiplicador = 100000;
                } else {
                    multiplicador = 100;
                    if (!getTipo().equals("trabalhador")) {
                        stm2.setDate(1, this.transformaDate(parametros.get("parDataInicio").toString()));
                        stm2.setDate(2, this.transformaDate(parametros.get("parDataFim").toString()));
                        stm2.setString(3, parametros.get("parUf").toString());
                        stm2.setString(4, municipio);
                    } else {
                        stm2.setString(1, municipio);
                        stm2.setString(2, parametros.get("parUf").toString());
                        stm2.setString(3, municipio);
                    }
                }

                rs = stm2.executeQuery();
            }
            rs.next();
            denominador = 0;
            if (this.getTipo().equals("populacao")) {
                try {
                    //buscar denominador do dbf
                    if (this.getTitulo1().equals("Taxa de incidência de aids em menores de 5 anos de idade ")) {
                        denominador = this.getPopulacao(municipio, 5, parametros.get("parAno").toString());
                    } else {
                        denominador = this.getPopulacao(municipio, 15, parametros.get("parAno").toString());
                    }
                } catch (DBFException ex) {
                    Logger.getLogger(Agravo.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                denominador = rs.getInt("denominador");
            }
            d1 = new Agravo();
            d1.setCodMunicipio(municipio);
            d1.setNomeMunicipio(parametros.get("parNomeMunicipio").toString());
            if (this.getTipo().equals("sifilis") || this.getTipo().equals("trabalhador")) {
                if (this.getTipo().equals("trabalhador")) {
                    d1.setNumerador(String.valueOf(NumberFormat.getNumberInstance().format(denominador)));
                    d1.setDenominador(String.valueOf(NumberFormat.getNumberInstance().format(numerador)));
                    if (denominador > 0) {
                        d1.setTaxa(String.valueOf(df.format((numerador - denominador) * 100 / denominador)));
                    } else {
                        d1.setTaxa("0");
                    }
                } else {
                    //sifilis
                    d1.setNumerador(String.valueOf(NumberFormat.getNumberInstance().format(numerador)));
                    d1.setDenominador(String.valueOf(NumberFormat.getNumberInstance().format(denominador)));
                    d1.setTaxa(this.getTaxaSililis(con, parametros, null));
                }
            } else {
                if (!String.valueOf(denominador).equals("0.0")) {
                    d1.setNumerador(String.valueOf(df2.format(numerador)));
                    d1.setDenominador(String.valueOf(NumberFormat.getNumberInstance().format(denominador)));
                    d1.setTaxa(String.valueOf(df.format(numerador / denominador * this.getMultiplicador())));
                } else {
                    d1.setNumerador("0");
                    d1.setDenominador("0");
                    d1.setTaxa("0.00");
                }
            }
            beans.add(d1);
            getBarraStatus().setString(null);
            return beans;
        }

    }

    public String getTaxaSililis(Connection con, Map parametros, String municipio) throws SQLException {
        String sql = "SELECT  count(*) as taxa FROM  dbsinan.tb_notificacao " + "where co_cid = 'A50.9' and (dt_diagnostico_sintoma BETWEEN ?  " + "AND ?) and " + "co_uf_residencia= ? ";
        if (parametros.get("nivelAgregacao").equals("Municipio") || municipio != null) {
            sql = sql + " and co_municipio_residencia = ? ";
        }
        PreparedStatement stm2 = con.prepareStatement(sql);
        stm2.setDate(1, this.transformaDate(parametros.get("parDataInicio").toString()));
        stm2.setDate(2, this.transformaDate(parametros.get("parDataFim").toString()));
        stm2.setString(3, parametros.get("parUf").toString());
        if (parametros.get("nivelAgregacao").equals("Municipio") || municipio != null) {
            if (municipio != null) {
                stm2.setString(4, municipio);
            } else {
                stm2.setString(4, parametros.get("parMunicipio").toString());
            }
        }
        ResultSet rs = stm2.executeQuery();
        rs.next();
        return rs.getString("taxa");
    }

    public List getBeanMunicipios(Connection con, Map parametros) throws SQLException {
        if (isDBF()) {
            return getBeans();
        } else {
            String sql;
            java.sql.Statement stm = con.createStatement();
            ResultSet rs, rs2;
            int multiplicador;
            double numerador, denominador;
            String municipio, noMunicipio = null;
            List beans = new ArrayList();
            DengueLetalidade d1 = null;
            DecimalFormat df = new DecimalFormat("0.00");
            DecimalFormat df2 = new DecimalFormat(",###,##");
            if (parametros.get("parNomeRegional").equals("-- Selecione --") || parametros.get("parNomeRegional").equals("")) {
                sql = "select co_municipio_ibge,no_municipio from dbgeral.tb_municipio where sg_uf = '" + parametros.get("parSgUf") + "' order by no_municipio";
            } else {
//                sql = "select t1.co_municipio_ibge,no_municipio from dbgeral.tb_municipio as t1, dblocalidade.rl_regional_municipio_svs as t2 where t2.co_uf_ibge=" + parametros.get("parUf") + " and t1.co_municipio_ibge=t2.co_municipio_ibge and co_regional = '" + parametros.get("parCodRegional") + "' and no_municipio not like '%Ignorado%'  order by no_municipio";
                sql = "select t1.co_municipio_ibge,no_municipio from dbgeral.tb_municipio as t1, dblocalidade.rl_regional_municipio_svs as t2 where t2.co_uf_ibge=" + parametros.get("parUf") + " and t1.co_municipio_ibge=t2.co_municipio_ibge and co_regional is null and no_municipio not like '%Ignorado%'  order by no_municipio";
            }
            try {
                rs = stm.executeQuery(sql);
            } catch (Exception exception) {
                if (parametros.get("parNomeRegional").equals("Todas Regionais") || parametros.get("parNomeRegional").equals("")) {
                    sql = "select co_municipio_ibge,no_municipio from dbgeral.tb_municipio where sg_uf = '" + parametros.get("parSgUf") + "' order by no_municipio";
                } else {
                    sql = "select t1.co_municipio_ibge,no_municipio from dbgeral.tb_municipio as t1, dblocalidade.rl_regional_municipio_svs as t2 where t2.co_uf_ibge=" + parametros.get("parUf") + " and t1.co_municipio_ibge=t2.co_municipio_ibge and co_regional = '" + parametros.get("parCodRegional") + "' and no_municipio not like '%Ignorado%'  order by no_municipio";
                }
                rs = stm.executeQuery(sql);

            }
            while (rs.next()) {
                municipio = rs.getString("co_municipio_ibge");
                noMunicipio = rs.getString("no_municipio");
                getBarraStatus().setString("Calculando município: " + noMunicipio);
                PreparedStatement stm2 = con.prepareStatement(this.getSqlNumeradorBeanMunicipios());
                if (getTipo().equals("dengueGrave")) {
                    rs2 = con.createStatement().executeQuery("SELECT  count(*) as numerador FROM  dbsinan.tb_notificacao " + "where co_cid = 'A90' and ds_semana_sintoma in (" + parametros.get("parDataInicio") + ") and " + "co_uf_residencia= " + parametros.get("parUf").toString() + " and co_municipio_residencia = " + municipio + " and " + "tp_evolucao_caso = '2' and tp_classificacao_final in (2,3,4)");
                } else {
                    stm2.setDate(1, this.transformaDate(parametros.get("parDataInicio").toString()));
                    stm2.setDate(2, this.transformaDate(parametros.get("parDataFim").toString()));
                    if (getTipo().equals("trabalhador")) {
                        stm2.setString(3, municipio);
                        stm2.setDate(4, this.transformaDate(parametros.get("parDataInicio").toString()));
                        stm2.setDate(5, this.transformaDate(parametros.get("parDataFim").toString()));
                        stm2.setString(6, parametros.get("parUf").toString());
                        stm2.setString(7, municipio);

                    } else {
                        stm2.setString(3, parametros.get("parUf").toString());
                        stm2.setString(4, municipio);
                    }
                    rs2 = stm2.executeQuery();
                }
                rs2.next();

                numerador = rs2.getDouble("numerador");
                if (getTipo().equals("dengueGrave")) {
                    rs2 = con.createStatement().executeQuery("SELECT  count(*) as denominador FROM  dbsinan.tb_notificacao " + "where co_cid = 'A90' and ds_semana_sintoma in (" + parametros.get("parDataInicio") + ") and " + "co_uf_residencia= " + parametros.get("parUf").toString() + " and co_municipio_residencia = " + municipio + " and " + " tp_classificacao_final in (2,3,4)");
                } else {
                    stm2 = con.prepareStatement(this.getSqlDenominadorBeanMunicipios());
                    if (this.getTipo().equals("populacao")) {
                        stm2.setString(1, municipio);
                        stm2.setString(2, parametros.get("parAno").toString());
                        multiplicador = 100000;
                    } else {
                        multiplicador = 100;
                        if (!this.getTipo().equals("trabalhador")) {
                            stm2.setDate(1, this.transformaDate(parametros.get("parDataInicio").toString()));
                            stm2.setDate(2, this.transformaDate(parametros.get("parDataFim").toString()));
                            stm2.setString(3, parametros.get("parUf").toString());
                            stm2.setString(4, municipio);
                        } else {
                            stm2.setString(1, municipio);
                            stm2.setString(2, parametros.get("parUf").toString());
                            stm2.setString(3, municipio);
                        }
                    }
                    rs2 = stm2.executeQuery();
                }
                rs2.next();
                try {
                    denominador = 0;
                    if (this.getTipo().equals("populacao")) {
                        try {
                            //buscar denominador do dbf
                            if (this.getTitulo1().equals("Taxa de incidência de aids em menores de 5 anos de idade ")) {
                                denominador = this.getPopulacao(municipio, 5, parametros.get("parAno").toString());
                            } else {
                                denominador = this.getPopulacao(municipio, 15, parametros.get("parAno").toString());
                            }
                        } catch (DBFException ex) {
                            Logger.getLogger(Agravo.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else {
                        denominador = rs2.getDouble("denominador");
                    }
//                denominador = rs2.getDouble("denominador");
                    d1 = new DengueLetalidade();
                    d1.setCodMunicipio(municipio);
                    d1.setNomeMunicipio(noMunicipio);

                    if (this.getTipo().equals("sifilis") || this.getTipo().equals("trabalhador")) {
                        if (this.getTipo().equals("trabalhador")) {
                            d1.setNumerador(String.valueOf(NumberFormat.getNumberInstance().format(denominador)));
                            d1.setDenominador(String.valueOf(NumberFormat.getNumberInstance().format(numerador)));
                            if (denominador > 0) {
                                d1.setTaxa(String.valueOf(df.format((numerador - denominador) * 100 / denominador)));
                            } else {
                                d1.setTaxa("0");
                            }
                        } else {
                            d1.setNumerador(String.valueOf(NumberFormat.getNumberInstance().format(numerador)));
                            d1.setDenominador(String.valueOf(NumberFormat.getNumberInstance().format(denominador)));
                            d1.setTaxa(this.getTaxaSililis(con, parametros, municipio));
                        }
                    } else {
                        if (!String.valueOf(denominador).equals("0.0")) {
                            d1.setNumerador(String.valueOf(df2.format(numerador)));
                            d1.setDenominador(String.valueOf(NumberFormat.getNumberInstance().format(denominador)));
                            d1.setTaxa(String.valueOf(df.format(numerador / denominador * this.getMultiplicador())));

                        } else {
                            d1.setNumerador("0");
                            d1.setDenominador("0");
                            d1.setTaxa("0.00");
                        }
                    }
                    beans.add(d1);

                } catch (Exception exception) {
                    System.out.println(municipio + "\n" + exception);
                }

            }
            getBarraStatus().setString(null);

            return beans;
        }
    }

    public List getBeanEstadoEspecifico(Connection con, Map parametros) throws SQLException {
        if (isDBF()) {
            return getBeans();
        } else {
            getBarraStatus().setString("Aguarde... calculando município: ");
            ResultSet rs = null, rs2 = null;
            int multiplicador;
            double numerador, denominador;
            String municipio = null;
            List beans = new ArrayList();
            Agravo d1 = null;
            DecimalFormat df = new DecimalFormat("0.00");
            PreparedStatement stm2 = con.prepareStatement(this.getSqlNumeradorEstado());
            System.out.println("Numerador: " + this.getSqlNumeradorEstado());
            if (getTipo().equals("dengueGrave")) {
                rs2 = con.createStatement().executeQuery("SELECT  count(*) as numerador FROM  dbsinan.tb_notificacao " + "where co_cid = 'A90' and ds_semana_sintoma in (" + parametros.get("parDataInicio") + ") and " + "co_uf_residencia= " + parametros.get("parUf").toString() + " and " + "tp_evolucao_caso = '2' and tp_classificacao_final in (2,3,4)");
                rs2.next();
            } else {
                stm2.setDate(1, this.transformaDate(parametros.get("parDataInicio").toString()));
                stm2.setDate(2, this.transformaDate(parametros.get("parDataFim").toString()));
            }

            if (getTipo().equals("trabalhador")) {
                stm2.setString(3, parametros.get("parUf").toString() + '%');
                stm2.setDate(4, this.transformaDate(parametros.get("parDataInicio").toString()));
                stm2.setDate(5, this.transformaDate(parametros.get("parDataFim").toString()));
                stm2.setString(6, parametros.get("parUf").toString());

            } else {
                stm2.setString(3, parametros.get("parUf").toString());
            }
            if (!getTipo().equals("dengueGrave")) {
                rs2 = stm2.executeQuery();
                rs2.next();
            }
            numerador = rs2.getDouble("numerador");
            stm2 = con.prepareStatement(this.getSqlDenominandorEstado());
            System.out.println("Denominador: " + this.getSqlDenominandorEstado());
            if (this.getTipo().equals("populacao")) {
                stm2.setString(1, parametros.get("parUf").toString());
                stm2.setString(2, parametros.get("parAno").toString());
                multiplicador = 100000;
            } else {
                multiplicador = 100;
                if (!getTipo().equals("trabalhador")) {
                    if (getTipo().equals("dengueGrave")) {

                        rs = con.createStatement().executeQuery("SELECT  count(*) as denominador FROM  dbsinan.tb_notificacao " + "where co_cid = 'A90' and ds_semana_sintoma in (" + parametros.get("parDataInicio") + ") and " + "co_uf_residencia= " + parametros.get("parUf").toString() + " and " + " tp_classificacao_final in (2,3,4)");
                        rs.next();
                    } else {
                        stm2.setDate(1, this.transformaDate(parametros.get("parDataInicio").toString()));
                        stm2.setDate(2, this.transformaDate(parametros.get("parDataFim").toString()));
                    }
                    stm2.setString(3, parametros.get("parUf").toString());
                } else {
                    stm2.setString(1, parametros.get("parUf").toString() + '%');
                    stm2.setString(2, parametros.get("parUf").toString());
                }
            }
            if (!getTipo().equals("dengueGrave")) {
                rs = stm2.executeQuery();
                rs.next();
            }
            //  System.out.println(rs.getInt("denominador"));
            denominador = 0;
            if (this.getTipo().equals("populacao")) {
                try {
                    //buscar denominador do dbf
                    if (this.getTitulo1().equals("Taxa de incidência de aids em menores de 5 anos de idade ")) {
                        denominador = this.getPopulacao(parametros.get("parUf").toString(), 5, parametros.get("parAno").toString());
                    } else {
                        denominador = this.getPopulacao(parametros.get("parUf").toString(), 15, parametros.get("parAno").toString());
                    }
                } catch (DBFException ex) {
                    Logger.getLogger(Agravo.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                denominador = rs.getDouble("denominador");
            }
            d1 = new Agravo();
            d1.setCodMunicipio(parametros.get("parUf").toString());
            d1.setNomeMunicipio(parametros.get("parSgUf").toString());
            if (this.getTipo().equals("sifilis") || this.getTipo().equals("trabalhador")) {
                if (this.getTipo().equals("trabalhador")) {
                    d1.setNumerador(String.valueOf(NumberFormat.getNumberInstance().format(denominador)));
                    d1.setDenominador(String.valueOf(NumberFormat.getNumberInstance().format(numerador)));
                    if (denominador > 0) {
                        d1.setTaxa(String.valueOf(df.format((numerador - denominador) * 100 / denominador)));
                    } else {
                        d1.setTaxa("0");
                    }
                } else {
                    d1.setNumerador(String.valueOf(NumberFormat.getNumberInstance().format(numerador)));
                    d1.setDenominador(String.valueOf(NumberFormat.getNumberInstance().format(denominador)));
                    d1.setTaxa(this.getTaxaSililis(con, parametros, null));
                }
            } else {
                if (!String.valueOf(denominador).equals("0.0")) {
                    d1.setNumerador(String.valueOf(NumberFormat.getNumberInstance().format(numerador)));
                    d1.setDenominador(String.valueOf(NumberFormat.getNumberInstance().format(denominador)));
                    d1.setTaxa(String.valueOf(df.format(numerador / denominador * this.getMultiplicador())));
                } else {
                    d1.setNumerador("0");
                    d1.setDenominador("0");
                    d1.setTaxa("0.00");
                }
            }
            beans.add(d1);
            getBarraStatus().setString(null);
            return beans;
        }
    }

    public List getBeansEstadoEspecifico(Connection con, Map parametros) throws SQLException {
        if (parametros.get("municipios").equals("sim")) {
            return this.getBeanMunicipios(con, parametros);
        } else {
            return this.getBeanEstadoEspecifico(con, parametros);
        }
    }

    public List getListaEstadoEspecifico(Connection con, Map parametros) throws SQLException {
        List beans = new ArrayList();
        return beans;
    }

    public List getListaMunicipioEspecifico(Connection con, Map parametros) throws SQLException {
        List beans = new ArrayList();
        return beans;
    }

    public List getListaMunicipioEspecifico(Map parametros) {
        if (isDBF()) {
            return getListaHanseniase(parametros);
        } else {
            List beans = new ArrayList();
            return beans;
        }

    }

    public static File abreJanelaEscolherCaminho() {
        JFileChooser fileopen = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Arquivo DBF", "dbf");
        fileopen.addChoosableFileFilter(filter);

        int ret = fileopen.showDialog(null, "Abrir DBF");

        if (ret == JFileChooser.APPROVE_OPTION) {
            File file = fileopen.getSelectedFile();
            System.out.println(file);
            return file;
        }
        return null;
    }

    public void calcula(DBFReader reader, Map parametros) {
    }

    public DBFReader getReader() {
        DBFReader reader = Util.retornaObjetoDbf(getConf().getCaminho());
        if (reader == null) {
            abreJanelaEscolherCaminho();
        }
        return reader;
    }

    public String getTaxaEstado(Connection con, Map parametros) throws SQLException {
        if (isDBF()) {
            DBFReader reader = null;
            //ler o arquivo dbf
            if (parametros.get("parVariosArquivos") == null) {
                reader = Util.retornaObjetoDbf(Configuracao.getPropriedade("caminho"));
                if (reader == null) {
                    abreJanelaEscolherCaminho();
                }
            }
            calcula(reader, parametros);
            return getTaxaEstadual();
        } else {
            String sql;
            java.sql.Statement stm = con.createStatement();
            ResultSet rs, rs2 = null;
            String total;
            int multiplicador;
            double numerador, denominador;
            DecimalFormat df = new DecimalFormat("0.00");
            PreparedStatement stm2 = con.prepareStatement(this.getSqlNumeradorEstado());
            System.out.println(this.getSqlNumeradorEstado());
            if (getTipo().equals("dengueGrave")) {
                rs2 = con.createStatement().executeQuery("SELECT  count(*) as numerador FROM  dbsinan.tb_notificacao " + "where co_cid = 'A90' and ds_semana_sintoma in (" + parametros.get("parDataInicio") + ") and " + "co_uf_residencia= " + parametros.get("parUf").toString() + " and " + "tp_evolucao_caso = '2' and tp_classificacao_final in (2,3,4)");
                rs2.next();
            } else {
                stm2.setDate(1, this.transformaDate(parametros.get("parDataInicio").toString()));
                stm2.setDate(2, this.transformaDate(parametros.get("parDataFim").toString()));
            }
            if (getTipo().equals("trabalhador")) {
                stm2.setString(3, parametros.get("parUf").toString() + '%');
                stm2.setDate(4, this.transformaDate(parametros.get("parDataInicio").toString()));
                stm2.setDate(5, this.transformaDate(parametros.get("parDataFim").toString()));
                stm2.setString(6, parametros.get("parUf").toString());

            } else {
                stm2.setString(3, parametros.get("parUf").toString());
            }
            if (!getTipo().equals("dengueGrave")) {
                rs2 = stm2.executeQuery();
                rs2.next();
            }
            numerador = rs2.getDouble("numerador");
            stm2 = con.prepareStatement(this.getSqlDenominandorEstado());
            if (this.getTipo().equals("populacao")) {
                stm2.setString(1, parametros.get("parUf").toString());
                stm2.setString(2, parametros.get("parAno").toString());
                multiplicador = 100000;
            } else {
                multiplicador = 100;
                if (!getTipo().equals("trabalhador")) {
                    if (getTipo().equals("dengueGrave")) {
                        rs2 = con.createStatement().executeQuery("SELECT  count(*) as denominador FROM  dbsinan.tb_notificacao " + "where co_cid = 'A90' and ds_semana_sintoma in (" + parametros.get("parDataInicio") + ") and " + "co_uf_residencia= " + parametros.get("parUf").toString() + " and " + " tp_classificacao_final in (2,3,4)");
                        rs2.next();
                    } else {
                        stm2.setDate(1, this.transformaDate(parametros.get("parDataInicio").toString()));
                        stm2.setDate(2, this.transformaDate(parametros.get("parDataFim").toString()));
                    }
                    stm2.setString(3, parametros.get("parUf").toString());
                } else {
                    stm2.setString(1, parametros.get("parUf").toString() + '%');
                    stm2.setString(2, parametros.get("parUf").toString());
                }
            }
            if (!getTipo().equals("dengueGrave")) {
                rs2 = stm2.executeQuery();
                rs2.next();
            }
            denominador = 0;
            if (this.getTipo().equals("populacao")) {
                try {
                    //buscar denominador do dbf
                    denominador = this.getPopulacao(parametros.get("parUf").toString(), 5, parametros.get("parAno").toString());
                } catch (DBFException ex) {
                    Logger.getLogger(Agravo.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                denominador = rs2.getDouble("denominador");
            }
//        denominador = rs2.getDouble("denominador");
            if (!String.valueOf(denominador).equals("0.0")) {
                total = df.format(numerador / denominador * this.getMultiplicador());
            } else {
                total = "0";
            }
            if (this.getTipo().equals("sifilis")) {
                return NumberFormat.getNumberInstance().format(numerador + denominador) + " (Menor que 1 ano:" + NumberFormat.getNumberInstance().format(numerador) + " | Sem informação de idade: " + NumberFormat.getNumberInstance().format(denominador) + ")";
            } else {
                if (this.getTipo().equals("trabalhador")) {
                    return df.format((numerador - denominador) * 100 / denominador);
                }
                return total + " (Numerador:" + numerador + " / Denominador: " + denominador + ")";
            }
        }

    }

    public java.sql.Date transformaDate(String data) {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        java.sql.Date date = null;
        try {
            date = new java.sql.Date(((java.util.Date) formatter.parse(data)).getTime());
        } catch (Exception exception) {
            System.out.println("erro: parametro data no calculo do getTaxaEstado:" + exception);
        }
        return date;
    }

    public String getCompletitude(Connection con, Map parametros) throws SQLException {
        if (isDBF()) {
            String sql, total = "";
            if (this.getSqlNumeradorCompletitude().isEmpty()) {
                total = "";
            } else {
                total = this.getPercentualCompletitude();
            }
            return this.getTextoCompletitude() + total;
        } else {
            String sql, total;
            if (this.getSqlNumeradorCompletitude().isEmpty()) {
                total = "";
            } else {
                java.sql.Statement stm = con.createStatement();
                ResultSet rs = null;
                PreparedStatement stm2 = null;
                double numerador, denominador = 0;
                DecimalFormat df = new DecimalFormat("0.00");
                String complemento = "";
                if (parametros.get("nivelAgregacao").toString().equals("Municipio")) {
                    complemento = " and t1.co_municipio_residencia = ?";
                }
                if (getTipo().equals("dengueGrave")) {
                    String query = "SELECT  count(*) as numerador FROM  dbsinan.tb_notificacao " + "where co_cid = 'A90' and ds_semana_sintoma in (" + parametros.get("parDataInicio") + ") and " + "co_uf_residencia= " + parametros.get("parUf").toString() + " and tp_evolucao_caso is null and tp_classificacao_final in (2,3,4)";

                    if (parametros.get("nivelAgregacao").toString().equals("Municipio")) {
                        query = query + " and co_municipio_residencia = " + parametros.get("parMunicipio").toString();
                    }
                    rs = con.createStatement().executeQuery(query);
                    rs.next();
                    numerador = rs.getDouble("numerador");

                    query = "SELECT  count(*) as denominador FROM  dbsinan.tb_notificacao as t1 " + "where co_cid = 'A90' and ds_semana_sintoma in (" + parametros.get("parDataInicio") + ") and " + "co_uf_residencia= " + parametros.get("parUf").toString() + " and " + "tp_classificacao_final in (2,3,4)";

                    if (parametros.get("nivelAgregacao").toString().equals("Municipio")) {
                        query = query + " and co_municipio_residencia = " + parametros.get("parMunicipio").toString();
                    }
                    rs = con.createStatement().executeQuery(query);
                    rs.next();
                    denominador = rs.getDouble("denominador");
                } else {
                    stm2 = con.prepareStatement(this.getSqlNumeradorCompletitude() + complemento);
                    stm2.setDate(1, this.transformaDate(parametros.get("parDataInicio").toString()));
                    stm2.setDate(2, this.transformaDate(parametros.get("parDataFim").toString()));
                    stm2.setString(3, parametros.get("parUf").toString());
                    if (parametros.get("nivelAgregacao").toString().equals("Municipio")) {
                        stm2.setString(4, parametros.get("parMunicipio").toString());
                    }
                    rs = stm2.executeQuery();
                    rs.next();
                    numerador = rs.getDouble("numerador");
                    PreparedStatement stm3 = con.prepareStatement(this.getSqlDenominandorEstado() + complemento);
                    stm3.setDate(1, this.transformaDate(parametros.get("parDataInicio").toString()));
                    stm3.setDate(2, this.transformaDate(parametros.get("parDataFim").toString()));
                    stm3.setString(3, parametros.get("parUf").toString());
                    if (parametros.get("nivelAgregacao").toString().equals("Municipio")) {
                        stm3.setString(4, parametros.get("parMunicipio").toString());
                    }
                    rs = stm3.executeQuery();
                    rs.next();
                    denominador = rs.getDouble("denominador");
                }

                if (!String.valueOf(denominador).equals("0.0")) {
                    total = df.format(numerador / denominador * 100);
                    // System.out.println(numerador.divide(denominador,2).multiply(new java.math.BigDecimal(100)));
                } else {
                    total = "0";
                }
            }

            return this.getTextoCompletitude() + total;
        }

    }

    public DBFWriter getLinhas(HashMap<String, ColunasDbf> colunas, List bean, DBFWriter writer) throws DBFException, IOException {

        return null;
    }

    public boolean isBetweenDates(Date dataParametro, String dataInicio, String dataFim) throws ParseException {
        Date dtInicio = convertToDate(dataInicio);
        Date dtFim = convertToDate(dataFim);
        if (dataParametro != null) {
            if (dataParametro.after(dtInicio) || dataParametro.equals(dtInicio)) {
                if (dataParametro.before(dtFim) || dataParametro.equals(dtFim)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static Date converterParaData(String data) throws ParseException {
        try {
            DateFormat dt = DateFormat.getDateInstance(DateFormat.SHORT, Locale.US);
            java.util.Date date = dt.parse(data);
            return new Date(date.getTime());
        } catch (NumberFormatException ex) {
            Master.mensagem("Erro:\n" + ex);
        } catch (ParseException ex) {
            Master.mensagem("Erro:\n" + ex);
        }
        return null;

    }

    private Date convertToDate(String data) throws ParseException {
        try {
            String mes = data.substring(5, 7);
            String dia = data.substring(8, 10);
            String ano = data.substring(2, 4);

            String str = mes + "/" + dia + "/" + ano;
            DateFormat dt = DateFormat.getDateInstance(DateFormat.SHORT, Locale.US);
            java.util.Date date = dt.parse(str);
            return new Date(date.getTime());
        } catch (NumberFormatException ex) {
            Master.mensagem("Erro:\n" + ex);
        } catch (ParseException ex) {
            Master.mensagem("Erro:\n" + ex);
        }
        return null;

    }

    //ESSE MÉTODO É UTILIZADO QUANDO SELECIONA UMA REGIÃO DE SAÚDE ESPECÍFICA - TAIDSON 15FEV2013
    public List<String> verificaMunicipio(String codRegional) {
        List<String> municipiosRegionais = new ArrayList<String>();

        if (codRegional.length() > 0) {
            DBFUtil utilDbf = new DBFUtil();
            //busca municipios dessa regional
            DBFReader readerMunicipio = Util.retornaObjetoDbfCaminhoArquivo("MUNICNET", "dbf\\");
            Object[] rowObjects1;

            try {
                utilDbf.mapearPosicoes(readerMunicipio);
                while ((rowObjects1 = readerMunicipio.nextRecord()) != null) {
                    if (codRegional.equals(utilDbf.getString(rowObjects1, "ID_REGIONA"))) {
                        municipiosRegionais.add(utilDbf.getString(rowObjects1, "ID_MUNICIP"));

                    }
                }
            } catch (DBFException e) {
                Master.mensagem("Erro ao carregar municipios:\n" + e);
            }
        }
        return municipiosRegionais;

    }

    /**
     * Retorna os muncípio de uma região de saúde específica
     *
     * @param codRegiaoSaude
     * @return
     * @data 15fev2013
     * @autor Taidson
     */
    public List<String> verificaMunicipioRegiaoSaude(String codRegiaoSaude) {
        List<String> municipiosRegiaoSaude = new ArrayList<String>();

        if (codRegiaoSaude.length() > 0) {
            DBFUtil utilDbf = new DBFUtil();
            //busca municipios dessa região de saúde
            DBFReader readerMunicipio = Util.retornaObjetoDbfCaminhoArquivo("MUNICNET", "dbf\\");
            Object[] rowObjects1;

            try {
                utilDbf.mapearPosicoes(readerMunicipio);
                while ((rowObjects1 = readerMunicipio.nextRecord()) != null) {
                    if (codRegiaoSaude.equals(utilDbf.getString(rowObjects1, "ID_REGIAO"))) {
                        municipiosRegiaoSaude.add(utilDbf.getString(rowObjects1, "ID_MUNICIP"));

                    }
                }
            } catch (DBFException e) {
                Master.mensagem("Erro ao carregar municipios:\n" + e);
            }
        }
        return municipiosRegiaoSaude;

    }

    public HashMap<String, Agravo> populaMunicipiosBeans(String sgUfResidencia, String codRegional) {
        DBFUtil utilDbf = new DBFUtil();
        HashMap<String, String> municipios = new HashMap<String, String>();
        HashMap<String, Agravo> municipiosBeans = new HashMap<String, Agravo>();
        //se codRegional estiver preenchida, deve buscar somente os municipios pertencentes a ela
        if (codRegional.length() > 0) {
            //busca municipios dessa regional
            DBFReader readerMunicipio = Util.retornaObjetoDbfCaminhoArquivo("MUNICNET", "dbf\\");
            Object[] rowObjects1;

            try {
                utilDbf.mapearPosicoes(readerMunicipio);
                while ((rowObjects1 = readerMunicipio.nextRecord()) != null) {
                    if (codRegional.equals(utilDbf.getString(rowObjects1, "ID_REGIONA"))) {
                        if (!utilDbf.getString(rowObjects1, "NM_MUNICIP").startsWith("IGNORADO") && utilDbf.getString(rowObjects1, "NM_MUNICIP").lastIndexOf("TRANSF.") == -1 && utilDbf.getString(rowObjects1, "NM_MUNICIP").lastIndexOf("ATUAL BENTO GONCALVES") == -1) {
                            if ((utilDbf.getString(rowObjects1, "SG_UF").equals("DF") && utilDbf.getString(rowObjects1, "NM_MUNICIP").equals("BRASILIA")) || !utilDbf.getString(rowObjects1, "SG_UF").equals("DF")) {
                                Agravo agravoDbf = new Agravo();
                                agravoDbf.setCodMunicipio(utilDbf.getString(rowObjects1, "ID_MUNICIP"));
                                agravoDbf.setNomeMunicipio(utilDbf.getString(rowObjects1, "NM_MUNICIP"));
                                agravoDbf.setCodRegional(utilDbf.getString(rowObjects1, "ID_REGIONA"));
                                try {
                                    agravoDbf.setRegional(buscaRegionalSaude(utilDbf.getString(rowObjects1, "ID_REGIONA")));
                                } catch (SQLException ex) {
                                    Logger.getLogger(Agravo.class.getName()).log(Level.SEVERE, null, ex);
                                }

                                agravoDbf.setDenominador("0");
                                agravoDbf.setNumerador("0");
                                agravoDbf.setDenominadorInt(0);
                                agravoDbf.setNumeradorInt(0);
                                municipios.put(utilDbf.getString(rowObjects1, "ID_MUNICIP"), utilDbf.getString(rowObjects1, "NM_MUNICIP"));
                                municipiosBeans.put(agravoDbf.getCodMunicipio(), agravoDbf);
                            }
                        }
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
                            if ((utilDbf.getString(rowObjects1, "SG_UF").equals("DF") && utilDbf.getString(rowObjects1, "NM_MUNICIP").equals("BRASILIA")) || !utilDbf.getString(rowObjects1, "SG_UF").equals("DF")) {
                                Agravo agravoDbf = new Agravo();
                                agravoDbf.setCodMunicipio(utilDbf.getString(rowObjects1, "ID_MUNICIP"));
                                agravoDbf.setNomeMunicipio(utilDbf.getString(rowObjects1, "NM_MUNICIP"));
                                agravoDbf.setCodRegional(utilDbf.getString(rowObjects1, "ID_REGIONA"));
                                try {
                                    agravoDbf.setRegional(buscaRegionalSaude(utilDbf.getString(rowObjects1, "ID_REGIONA")));
                                } catch (SQLException ex) {
                                    Logger.getLogger(Agravo.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                agravoDbf.setDenominador("0");
                                agravoDbf.setNumerador("0");
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
        HashMap<String, Agravo> municipiosBeansRetorno = new HashMap<String, Agravo>();
        Iterator valueIt = municipiosKeys.iterator();
        while (valueIt.hasNext()) {
            String key = (String) valueIt.next();
            municipiosBeansRetorno.put(key, municipiosBeans.get(key));

        }
        return municipiosBeansRetorno;

    }

    public HashMap<String, Agravo> populaMunicipiosBeansMAL(String sgUfResidencia, String codRegiao, String regiao) {
        DBFUtil utilDbf = new DBFUtil();
        HashMap<String, String> municipios = new HashMap<String, String>();
        HashMap<String, Agravo> municipiosBeans = new HashMap<String, Agravo>();
        //se codRegional estiver preenchida, deve buscar somente os municipios pertencentes a ela
        //busca municipios dessa regional
        DBFReader readerMunicipio = Util.retornaObjetoDbfCaminhoArquivo("MUNICNET", "dbf\\");
        Object[] rowObjects1;

        Boolean sgUF = false;
        Boolean reg = false;
        Boolean temReg = false;
        int i = 1;

        try {
            utilDbf.mapearPosicoes(readerMunicipio);
            double TotalRegistros = Double.parseDouble(String.valueOf(readerMunicipio.getRecordCount()));
            while ((rowObjects1 = readerMunicipio.nextRecord()) != null) {
                if (utilDbf.getString(rowObjects1, "SG_UF") != null && sgUfResidencia != "TODAS") {
                    sgUF = (utilDbf.getString(rowObjects1, "SG_UF").equals(sgUfResidencia));
                }
                if (utilDbf.getString(rowObjects1, "SG_UF").equals("TO")) {
                    System.out.println("");
                }
                if (regiao.equals("true")) {
                    if (utilDbf.getString(rowObjects1, "ID_REGIAO") != null) {
                        reg = utilDbf.getString(rowObjects1, "ID_REGIAO").equals(codRegiao);
                    }
                } else {
                    if (utilDbf.getString(rowObjects1, "ID_REGIONA") != null) {
                        reg = utilDbf.getString(rowObjects1, "ID_REGIONA").equals(codRegiao);
                    }
                }
                if (sgUfResidencia.equals("TODAS")) {
                    if (regiao.equals("true")) {
                        if (utilDbf.getString(rowObjects1, "ID_REGIAO") != null) {
                            temReg = true;
                        }
                    } else {
                        if (utilDbf.getString(rowObjects1, "ID_REGIONA") != null) {
                            temReg = true;
                        }
                    }
                }

                if (temReg || (sgUF && codRegiao.length() == 0) || (reg && !(codRegiao.length() == 0))) {
                    if (!utilDbf.getString(rowObjects1, "NM_MUNICIP").startsWith("IGNORADO") && utilDbf.getString(rowObjects1, "NM_MUNICIP").lastIndexOf("TRANSF.") == -1 && utilDbf.getString(rowObjects1, "NM_MUNICIP").lastIndexOf("ATUAL BENTO GONCALVES") == -1) {
                        Agravo agravoDbf = new Agravo();
                        agravoDbf.setCodMunicipio(utilDbf.getString(rowObjects1, "ID_MUNICIP"));
                        agravoDbf.setNomeMunicipio(utilDbf.getString(rowObjects1, "NM_MUNICIP"));
                        agravoDbf.setUf(utilDbf.getString(rowObjects1, "SG_UF"));
                        if (regiao.equals("true")) {
                            agravoDbf.setCodRegiaoSaude(utilDbf.getString(rowObjects1, "ID_REGIAO"));
                        } else {
                            agravoDbf.setCodRegional(utilDbf.getString(rowObjects1, "ID_REGIONA"));
                        }
                        try {
                            if (regiao.equals("true")) {
                                agravoDbf.setRegiaoSaude(buscaRegiaoSaude(agravoDbf.getCodRegiaoSaude()));
                            } else {
                                agravoDbf.setRegional(buscaRegionalSaude(agravoDbf.getCodRegional()));
                            }

                            if (agravoDbf.getRegiaoSaude() == null) {
                                agravoDbf.setRegiaoSaude("");
                            }
                            if (agravoDbf.getRegional() == null) {
                                agravoDbf.setRegional("");
                            }

                        } catch (SQLException ex) {
                            Logger.getLogger(Agravo.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        agravoDbf.setDenominador("0");
                        agravoDbf.setNumerador("0");
                        agravoDbf.setNumeradorInt(0);
                        agravoDbf.setDenominadorInt(0);
                        municipios.put(utilDbf.getString(rowObjects1, "ID_MUNICIP"), utilDbf.getString(rowObjects1, "NM_MUNICIP"));
                        municipiosBeans.put(agravoDbf.getCodMunicipio(), agravoDbf);
                    }
                }
                temReg = false;
                float percentual = Float.parseFloat(String.valueOf(i)) / Float.parseFloat(String.valueOf(TotalRegistros)) * 100;
                getBarraStatus().setValue((int) percentual);
                i++;

            }
        } catch (DBFException e) {
            Master.mensagem("Erro ao carregar municipios:\n" + e);
        }

        municipios = sortHashMapByValues(municipios, false);
        Set<String> municipiosKeys = municipios.keySet();
        HashMap<String, Agravo> municipiosBeansRetorno = new HashMap<String, Agravo>();
        Iterator valueIt = municipiosKeys.iterator();
        while (valueIt.hasNext()) {
            String key = (String) valueIt.next();
            municipiosBeansRetorno.put(key, municipiosBeans.get(key));

        }
        return municipiosBeansRetorno;

    }

    public HashMap<String, Agravo> populaMunicipiosBeansPactuacao(String sgUfResidencia, String codRegiao) {
        DBFUtil utilDbf = new DBFUtil();
        HashMap<String, String> municipios = new HashMap<String, String>();
        HashMap<String, Agravo> municipiosBeans = new HashMap<String, Agravo>();
        //se codRegional estiver preenchida, deve buscar somente os municipios pertencentes a ela
        if (codRegiao.length() > 0) {
            //busca municipios dessa regional
            DBFReader readerMunicipio = Util.retornaObjetoDbfCaminhoArquivo("MUNICNET", "dbf\\");
            Object[] rowObjects1;
            try {
                utilDbf.mapearPosicoes(readerMunicipio);
                while ((rowObjects1 = readerMunicipio.nextRecord()) != null) {
                    if (codRegiao.equals(utilDbf.getString(rowObjects1, "ID_REGIAO"))) {
                        if (!utilDbf.getString(rowObjects1, "NM_MUNICIP").startsWith("IGNORADO") && utilDbf.getString(rowObjects1, "NM_MUNICIP").lastIndexOf("TRANSF.") == -1 && utilDbf.getString(rowObjects1, "NM_MUNICIP").lastIndexOf("ATUAL BENTO GONCALVES") == -1) {
                            if ((utilDbf.getString(rowObjects1, "SG_UF").equals("DF") && utilDbf.getString(rowObjects1, "NM_MUNICIP").equals("BRASILIA")) || !utilDbf.getString(rowObjects1, "SG_UF").equals("DF")) {
                                Agravo agravoDbf = new Agravo();
                                agravoDbf.setCodMunicipio(utilDbf.getString(rowObjects1, "ID_MUNICIP"));
                                agravoDbf.setNomeMunicipio(utilDbf.getString(rowObjects1, "NM_MUNICIP"));
                                agravoDbf.setUf(utilDbf.getString(rowObjects1, "SG_UF"));
                                agravoDbf.setCodRegiaoSaude(utilDbf.getString(rowObjects1, "ID_REGIAO"));
                                agravoDbf.setDenominador("0");
                                agravoDbf.setNumerador("0");
                                municipios.put(utilDbf.getString(rowObjects1, "ID_MUNICIP"), utilDbf.getString(rowObjects1, "NM_MUNICIP"));
                                municipiosBeans.put(agravoDbf.getCodMunicipio(), agravoDbf);
                            }
                        }
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
                            if ((utilDbf.getString(rowObjects1, "SG_UF").equals("DF") && utilDbf.getString(rowObjects1, "NM_MUNICIP").equals("BRASILIA")) || !utilDbf.getString(rowObjects1, "SG_UF").equals("DF")) {
                                Agravo agravoDbf = new Agravo();
                                agravoDbf.setCodMunicipio(utilDbf.getString(rowObjects1, "ID_MUNICIP"));
                                agravoDbf.setNomeMunicipio(utilDbf.getString(rowObjects1, "NM_MUNICIP"));
                                agravoDbf.setUf(utilDbf.getString(rowObjects1, "SG_UF"));
                                agravoDbf.setCodRegiaoSaude(utilDbf.getString(rowObjects1, "ID_REGIAO"));
                                agravoDbf.setDenominador("0");
                                agravoDbf.setNumerador("0");
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
        HashMap<String, Agravo> municipiosBeansRetorno = new HashMap<String, Agravo>();
        Iterator valueIt = municipiosKeys.iterator();
        while (valueIt.hasNext()) {
            String key = (String) valueIt.next();
            municipiosBeansRetorno.put(key, municipiosBeans.get(key));

        }
        return municipiosBeansRetorno;

    }

    public HashMap<String, TuberculoseCoorte> populaMunicipiosBeansTube(String sgUfResidencia, String codRegional) {
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
                        if (!utilDbf.getString(rowObjects1, "NM_MUNICIP").startsWith("IGNORADO") && utilDbf.getString(rowObjects1, "NM_MUNICIP").lastIndexOf("TRANSF.") == -1 && utilDbf.getString(rowObjects1, "NM_MUNICIP").lastIndexOf("ATUAL BENTO GONCALVES") == -1) {
                            if ((utilDbf.getString(rowObjects1, "SG_UF").equals("DF") && utilDbf.getString(rowObjects1, "NM_MUNICIP").equals("BRASILIA")) || !utilDbf.getString(rowObjects1, "SG_UF").equals("DF")) {

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
        } else {
            //busca municipios dessa regional
            DBFReader readerMunicipio = Util.retornaObjetoDbfCaminhoArquivo("MUNICNET", "dbf\\");
            Object[] rowObjects1;
            try {
                utilDbf.mapearPosicoes(readerMunicipio);

                while ((rowObjects1 = readerMunicipio.nextRecord()) != null) {
                    if (sgUfResidencia.equals(utilDbf.getString(rowObjects1, "SG_UF")) || sgUfResidencia.equals("BR")) {
                        if (!utilDbf.getString(rowObjects1, "NM_MUNICIP").startsWith("IGNORADO") && utilDbf.getString(rowObjects1, "NM_MUNICIP").lastIndexOf("TRANSF.") == -1 && utilDbf.getString(rowObjects1, "NM_MUNICIP").lastIndexOf("ATUAL BENTO GONCALVES") == -1) {
                            if ((utilDbf.getString(rowObjects1, "SG_UF").equals("DF") && utilDbf.getString(rowObjects1, "NM_MUNICIP").equals("BRASILIA")) || !utilDbf.getString(rowObjects1, "SG_UF").equals("DF")) {
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

    public HashMap<String, Agravo> populaUfsBeans() {
        DBFUtil utilDbf = new DBFUtil();
        HashMap<String, String> uf = new HashMap<String, String>();
        HashMap<String, Agravo> ufsBeans = new HashMap<String, Agravo>();
        //se codRegional estiver preenchida, deve buscar somente os municipios pertencentes a ela

        //busca municipios dessa regional
        DBFReader readerMunicipio = Util.retornaObjetoDbfCaminhoArquivo("UF", "dbf\\");
        Object[] rowObjects1;
        try {
            utilDbf.mapearPosicoes(readerMunicipio);

            while ((rowObjects1 = readerMunicipio.nextRecord()) != null) {

                Agravo agravoDbf = new Agravo();
                agravoDbf.init("");
                agravoDbf.setCodMunicipio(utilDbf.getString(rowObjects1, "ID_UF"));
                agravoDbf.setNomeMunicipio(utilDbf.getString(rowObjects1, "SG_UF"));
                agravoDbf.setDenominador("0");
                agravoDbf.setNumerador("0");
                uf.put(utilDbf.getString(rowObjects1, "ID_UF"), utilDbf.getString(rowObjects1, "SG_UF"));
                ufsBeans.put(agravoDbf.getCodMunicipio(), agravoDbf);
            }
        } catch (DBFException e) {
            Master.mensagem("Erro ao carregar municipios:\n" + e);
        }
        uf = sortHashMapByValues(uf, false);
        Set<String> ufKeys = uf.keySet();
        HashMap<String, Agravo> ufsBeansRetorno = new HashMap<String, Agravo>();
        Iterator valueIt = ufKeys.iterator();
        while (valueIt.hasNext()) {
            String key = (String) valueIt.next();
            ufsBeansRetorno.put(key, ufsBeans.get(key));
        }
        return ufsBeansRetorno;
    }

    public HashMap<String, Agravo> populaRegiaoBeans(String SG_UF, String id_Regiao) {
        DBFUtil utilDbf = new DBFUtil();
        HashMap<String, String> regiao = new HashMap<String, String>();
        HashMap<String, Agravo> RegBeans = new HashMap<String, Agravo>();
        Boolean sgUF = false;
        Boolean reg = false;
        Boolean temReg = false;
        //se codRegional estiver preenchida, deve buscar somente os municipios pertencentes a ela
        //busca municipios dessa regional
        DBFReader readerMunicipio = Util.retornaObjetoDbfCaminhoArquivo("REGIAO", "dbf\\");
        Object[] rowObjects1;

        try {
            utilDbf.mapearPosicoes(readerMunicipio);
            while ((rowObjects1 = readerMunicipio.nextRecord()) != null) {

                if (utilDbf.getString(rowObjects1, "SG_UF") != null) {
                    sgUF = (utilDbf.getString(rowObjects1, "SG_UF").equals(SG_UF));
                }
                if (utilDbf.getString(rowObjects1, "ID_REGIAO") != null) {
                    reg = utilDbf.getString(rowObjects1, "ID_REGIAO").equals(id_Regiao);
                }

                if (SG_UF.equals("TODAS")) {
                    if (utilDbf.getString(rowObjects1, "ID_REGIAO") != null) {
                        temReg = true;
                    }
                }

                if (temReg || (sgUF && id_Regiao.isEmpty()) || (reg && !id_Regiao.isEmpty())) {
                    Agravo agravoDbf = new Agravo();
                    agravoDbf.init("");
                    agravoDbf.setUf(utilDbf.getString(rowObjects1, "SG_UF"));
                    agravoDbf.setCodMunicipio(utilDbf.getString(rowObjects1, "ID_REGIAO"));
                    agravoDbf.setNomeMunicipio(utilDbf.getString(rowObjects1, "NM_REGIAO"));
                    agravoDbf.setDenominador("0");
                    agravoDbf.setNumerador("0");
                    agravoDbf.setNumeradorInt(0);
                    agravoDbf.setDenominadorInt(0);
                    regiao.put(utilDbf.getString(rowObjects1, "ID_REGIAO"), utilDbf.getString(rowObjects1, "NM_REGIAO"));
                    RegBeans.put(agravoDbf.getCodMunicipio(), agravoDbf);
                }
                temReg = false;
            }
        } catch (DBFException e) {
            Master.mensagem("Erro ao carregar municipios:\n" + e);
        }
        regiao = sortHashMapByValues(regiao, false);
        Set<String> RegKeys = regiao.keySet();
        HashMap<String, Agravo> regBeansRetorno = new HashMap<String, Agravo>();
        Iterator valueIt = RegKeys.iterator();
        while (valueIt.hasNext()) {
            String key = (String) valueIt.next();
            regBeansRetorno.put(key, RegBeans.get(key));
        }
        return regBeansRetorno;
    }

    public HashMap<String, Agravo> populaRegionalBeans(String SG_UF, String id_Regiao) {
        DBFUtil utilDbf = new DBFUtil();
        HashMap<String, String> regiao = new HashMap<String, String>();
        HashMap<String, Agravo> RegBeans = new HashMap<String, Agravo>();
        Boolean temReg = false;
        //se codRegional estiver preenchida, deve buscar somente os municipios pertencentes a ela
        //busca municipios dessa regional
        DBFReader readerMunicipio = Util.retornaObjetoDbfCaminhoArquivo("REGIONET", "dbf\\");
        Object[] rowObjects1;

        try {
            utilDbf.mapearPosicoes(readerMunicipio);
            while ((rowObjects1 = readerMunicipio.nextRecord()) != null) {
                if (SG_UF.equals("TODAS")) {
                    if (utilDbf.getString(rowObjects1, "ID_REGIONA") != null) {
                        temReg = true;
                    }
                }

                if (temReg || (utilDbf.getString(rowObjects1, "SG_UF").equals(SG_UF) && id_Regiao.isEmpty())
                        || (!id_Regiao.isEmpty() && utilDbf.getString(rowObjects1, "ID_REGIONA").equals(id_Regiao))) {
                    Agravo agravoDbf = new Agravo();
                    agravoDbf.init("");
                    agravoDbf.setUf(utilDbf.getString(rowObjects1, "SG_UF"));
                    agravoDbf.setCodMunicipio(utilDbf.getString(rowObjects1, "ID_REGIONA"));
                    agravoDbf.setNomeMunicipio(utilDbf.getString(rowObjects1, "NM_REGIONA"));
                    agravoDbf.setDenominador("0");
                    agravoDbf.setNumerador("0");
                    agravoDbf.setNumeradorInt(0);
                    agravoDbf.setDenominadorInt(0);
                    regiao.put(utilDbf.getString(rowObjects1, "ID_REGIONA"), utilDbf.getString(rowObjects1, "NM_REGIONA"));
                    RegBeans.put(agravoDbf.getCodMunicipio(), agravoDbf);
                }
                temReg = false;
            }
        } catch (DBFException e) {
            Master.mensagem("Erro ao carregar municipios:\n" + e);
        }
        regiao = sortHashMapByValues(regiao, false);
        Set<String> RegKeys = regiao.keySet();
        HashMap<String, Agravo> regBeansRetorno = new HashMap<String, Agravo>();
        Iterator valueIt = RegKeys.iterator();
        while (valueIt.hasNext()) {
            String key = (String) valueIt.next();
            regBeansRetorno.put(key, RegBeans.get(key));
        }
        return regBeansRetorno;
    }

    public HashMap<String, TuberculoseCoorte> populaUfsBeansTube() {
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

    public HashMap<String, HanseniaseCoorte> populaUfsBeansHans() {
        DBFUtil utilDbf = new DBFUtil();
        HashMap<String, String> uf = new HashMap<String, String>();
        HashMap<String, HanseniaseCoorte> ufsBeans = new HashMap<String, HanseniaseCoorte>();
        //se codRegional estiver preenchida, deve buscar somente os municipios pertencentes a ela

        //busca municipios dessa regional
        DBFReader readerMunicipio = Util.retornaObjetoDbfCaminhoArquivo("UF", "dbf\\");
        Object[] rowObjects1;
        try {
            utilDbf.mapearPosicoes(readerMunicipio);

            while ((rowObjects1 = readerMunicipio.nextRecord()) != null) {

                HanseniaseCoorte agravoDbf = new HanseniaseCoorte();
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
        HashMap<String, HanseniaseCoorte> ufsBeansRetorno = new HashMap<String, HanseniaseCoorte>();
        Iterator valueIt = ufKeys.iterator();
        while (valueIt.hasNext()) {
            String key = (String) valueIt.next();
            ufsBeansRetorno.put(key, ufsBeans.get(key));

        }
        return ufsBeansRetorno;
    }

    public HashMap<String, HanseniaseCoorte> populaMunicipiosBeansHans(String sgUfResidencia, String codRegional) {
        DBFUtil utilDbf = new DBFUtil();
        HashMap<String, String> municipios = new HashMap<String, String>();
        HashMap<String, HanseniaseCoorte> municipiosBeans = new HashMap<String, HanseniaseCoorte>();
        //se codRegional estiver preenchida, deve buscar somente os municipios pertencentes a ela
        if (codRegional.length() > 0) {
            //busca municipios dessa regional
            DBFReader readerMunicipio = Util.retornaObjetoDbfCaminhoArquivo("MUNICNET", "dbf\\");
            Object[] rowObjects1;

            try {
                utilDbf.mapearPosicoes(readerMunicipio);
                while ((rowObjects1 = readerMunicipio.nextRecord()) != null) {
                    if (codRegional.equals(utilDbf.getString(rowObjects1, "ID_REGIONA"))) {
                        if (!utilDbf.getString(rowObjects1, "NM_MUNICIP").startsWith("IGNORADO") && utilDbf.getString(rowObjects1, "NM_MUNICIP").lastIndexOf("TRANSF.") == -1 && utilDbf.getString(rowObjects1, "NM_MUNICIP").lastIndexOf("ATUAL BENTO GONCALVES") == -1) {
                            if ((utilDbf.getString(rowObjects1, "SG_UF").equals("DF") && utilDbf.getString(rowObjects1, "NM_MUNICIP").equals("BRASILIA")) || !utilDbf.getString(rowObjects1, "SG_UF").equals("DF")) {
                                HanseniaseCoorte agravoDbf = new HanseniaseCoorte();
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
        } else {
            //busca municipios dessa regional
            DBFReader readerMunicipio = Util.retornaObjetoDbfCaminhoArquivo("MUNICNET", "dbf\\");
            Object[] rowObjects1;
            try {
                utilDbf.mapearPosicoes(readerMunicipio);

                while ((rowObjects1 = readerMunicipio.nextRecord()) != null) {
                    if (sgUfResidencia.equals(utilDbf.getString(rowObjects1, "SG_UF")) || sgUfResidencia.equals("BR")) {
                        if (!utilDbf.getString(rowObjects1, "NM_MUNICIP").startsWith("IGNORADO") && utilDbf.getString(rowObjects1, "NM_MUNICIP").lastIndexOf("TRANSF.") == -1 && utilDbf.getString(rowObjects1, "NM_MUNICIP").lastIndexOf("ATUAL BENTO GONCALVES") == -1) {
                            if ((utilDbf.getString(rowObjects1, "SG_UF").equals("DF") && utilDbf.getString(rowObjects1, "NM_MUNICIP").equals("BRASILIA")) || !utilDbf.getString(rowObjects1, "SG_UF").equals("DF")) {
                                HanseniaseCoorte agravoDbf = new HanseniaseCoorte();
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
        HashMap<String, HanseniaseCoorte> municipiosBeansRetorno = new HashMap<String, HanseniaseCoorte>();
        Iterator valueIt = municipiosKeys.iterator();
        while (valueIt.hasNext()) {
            String key = (String) valueIt.next();
            municipiosBeansRetorno.put(key, municipiosBeans.get(key));

        }
        return municipiosBeansRetorno;
    }

    public List getListaHanseniase(Map parametros) {
        List beans = new ArrayList();
        return beans;
    }

    public LinkedHashMap sortHashMapByValues(HashMap passedMap, boolean ascending) {

        List mapKeys = new ArrayList(passedMap.keySet());
        List mapValues = new ArrayList(passedMap.values());
        Collections.sort(mapValues);
        Collections.sort(mapKeys);

        if (!ascending) {
            Collections.reverse(mapValues);
        }

        LinkedHashMap someMap = new LinkedHashMap();
        Iterator valueIt = mapValues.iterator();
        while (valueIt.hasNext()) {
            Object val = valueIt.next();
            Iterator keyIt = mapKeys.iterator();
            while (keyIt.hasNext()) {
                Object key = keyIt.next();
                if (passedMap.get(key).toString().equals(val.toString())) {
                    passedMap.remove(key);
                    mapKeys.remove(key);
                    someMap.put(key, val);
                    break;
                }
            }
        }
        return someMap;
    }

    public static int dataDiff(Date dataLow, Date dataHigh) {

        GregorianCalendar startTime = new GregorianCalendar();
        GregorianCalendar endTime = new GregorianCalendar();

        GregorianCalendar curTime = new GregorianCalendar();
        GregorianCalendar baseTime = new GregorianCalendar();
        try {
            startTime.setTime(dataLow);
            endTime.setTime(dataHigh);
            int dif_multiplier = 1;

            // Verifica a ordem de inicio das datas
            if (dataLow.compareTo(dataHigh) < 0) {
                baseTime.setTime(dataHigh);
                curTime.setTime(dataLow);
                dif_multiplier = 1;
            } else {
                baseTime.setTime(dataLow);
                curTime.setTime(dataHigh);
                dif_multiplier = -1;
            }

            int result_years = 0;
            int result_months = 0;
            int result_days = 0;

            // Para cada mes e ano, vai de mes em mes pegar o ultimo dia para import acumulando
            // no total de dias. Ja leva em consideracao ano bissesto
            while (curTime.get(GregorianCalendar.YEAR) < baseTime.get(GregorianCalendar.YEAR) || curTime.get(GregorianCalendar.MONTH) < baseTime.get(GregorianCalendar.MONTH)) {

                int max_day = curTime.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
                result_months += max_day;
                curTime.add(GregorianCalendar.MONTH, 1);

            }

            // Marca que é um saldo negativo ou positivo
            result_months = result_months * dif_multiplier;

            // Retirna a diferenca de dias do total dos meses
            result_days += (endTime.get(GregorianCalendar.DAY_OF_MONTH) - startTime.get(GregorianCalendar.DAY_OF_MONTH));

            return result_years + result_months + result_days;
        } catch (Exception e) {
            System.out.println(e);
            return -1;
        }
    }

    public boolean verificaMunicipio(String municipioResidencia, String municipio) {
        if (municipioResidencia.equals("")) {
            return true;
        }
        if (municipioResidencia.equals(municipio)) {
            return true;
        }
        return false;
    }

    public Double preencheAno(String dataInicio, String dataFim) {
        String ano = null;
        String[] arrayInicio = dataInicio.split("/");
        String[] arrayFim = dataFim.split("/");
        if (arrayInicio[0].equals("01") && arrayInicio[1].equals("01") && arrayFim[0].equals("31") && arrayFim[1].equals("12")) {
            if (arrayFim[2].equals(arrayInicio[2])) {
                ano = arrayInicio[2];
            }
        }
        if (ano != null) {
            return Double.parseDouble(ano);
        }
        return null;
    }

    public Double preencheAnoSemana(String semanaInicio, String semanaFim) {
        String ano = null;
        String anoInicio = semanaInicio.substring(0, 4);
        String anoFim = semanaFim.substring(0, 4);
        if (semanaInicio.substring(4, 6).equals("01") && semanaFim.substring(4, 6).equals("52")) {
            if (anoInicio.equals(anoFim)) {
                ano = anoFim;
            }
        }
        if (ano != null) {
            return Double.parseDouble(ano);
        }
        return null;
    }

    public int getPopulacao(String ufResidencia, int idade, String ano) throws DBFException {
        if (ufResidencia.length() == 2) {
            return getPopulacaoEstadual(ufResidencia, idade, ano);
        }
        Object[] rowObjects;
        String municipioPesquisa;
        double pop1 = 0, pop1a4 = 0, pop5a9 = 0, pop10a14 = 0;
        DBFReader reader = Util.retornaObjetoDbfCaminhoArquivo("populacao" + ano, "dbf\\");
        DBFUtil utilDbf = new DBFUtil();
        utilDbf.mapearPosicoes(reader);
        while ((rowObjects = reader.nextRecord()) != null) {
            municipioPesquisa = utilDbf.getString(rowObjects, "ID_MUNIC");
            if ((municipioPesquisa.length() > 4 && ufResidencia.length() > 4) || (municipioPesquisa.length() == 4 && ufResidencia.length() == 2)) {
                if (utilDbf.getString(rowObjects, "ID_MUNIC", ufResidencia.length()).equals(ufResidencia)) {
                    pop1 = Double.parseDouble(utilDbf.getString(rowObjects, "NU_POP1ANO"));
                    pop1a4 = Double.parseDouble(utilDbf.getString(rowObjects, "NU_POP1A4A"));
                    pop5a9 = Double.parseDouble(utilDbf.getString(rowObjects, "NU_POP5A9A"));
                    pop10a14 = Double.parseDouble(utilDbf.getString(rowObjects, "NU_POP10A1"));
                }
            }
        }
        if (idade > 5) {
//            total = (int) (pop1 + pop1a4 + pop5a9 + pop10a14);
            return (int) (pop1 + pop1a4 + pop5a9 + pop10a14);
        } else {
            //   total = (int) (pop1 + pop1a4);
            return (int) (pop1 + pop1a4);
        }
        //return total;
    }

    public int getPopulacaoEstadual(String ufResidencia, int idade, String ano) throws DBFException {
        Object[] rowObjects;
        String municipioPesquisa;
        double pop1 = 0, pop1a4 = 0, pop5a9 = 0, pop10a14 = 0, totalPop1a4 = 0, totalPop1a14 = 0;
        DBFReader reader = Util.retornaObjetoDbfCaminhoArquivo("populacao" + ano, "dbf\\");
        DBFUtil utilDbf = new DBFUtil();
        utilDbf.mapearPosicoes(reader);

        while ((rowObjects = reader.nextRecord()) != null) {
            municipioPesquisa = utilDbf.getString(rowObjects, "ID_MUNIC");
            //é necessário comparar o tamanho do campo igual a 2, por que tanto o id do município
            // quanto o da UF compartilham o mesmo campo - ID_MUNICIP - no banco MUNICNET.DBF
            if (municipioPesquisa.length() == 2 && municipioPesquisa.equals(ufResidencia)) {
                pop1 = Double.parseDouble(utilDbf.getString(rowObjects, "NU_POP1ANO"));
                pop1a4 = Double.parseDouble(utilDbf.getString(rowObjects, "NU_POP1A4A"));
                pop5a9 = Double.parseDouble(utilDbf.getString(rowObjects, "NU_POP5A9A"));
                pop10a14 = Double.parseDouble(utilDbf.getString(rowObjects, "NU_POP10A1"));

                totalPop1a4 = pop1 + pop1a4;
                totalPop1a14 = pop1 + pop1a4 + pop5a9 + pop10a14;
                break;
            }
        }
        if (idade > 5) {
            return (int) totalPop1a14;
        } else {
            return (int) totalPop1a4;
        }
    }

    public String buscaRegionalSaude(String idRegiao) throws SQLException {
        if (idRegiao == null) {
            return "";
        }

        DBFReader reader = SinanUtil.retornaObjetoDbfCaminhoArquivo("REGIONET", "dbf\\");
        Object[] rowObjects;
        DBFUtil utilDbf = new DBFUtil();
        try {
            utilDbf.mapearPosicoes(reader);
            while ((rowObjects = reader.nextRecord()) != null) {
                if (idRegiao.equals(utilDbf.getString(rowObjects, "ID_REGIONA"))) {
                    return utilDbf.getString(rowObjects, "NM_REGIONA");
                }
            }
        } catch (DBFException e) {
            Master.mensagem("Erro: regional nao encontrada.Verifique se existe a pasta DBF e se os arquivo REGIAO.DBF está lá:\n" + e);
        }
        return "";
    }

    public String buscaIdRegionalSaude(String idMunicipio) throws SQLException {
        if (idMunicipio == null) {
            return "";
        }

        DBFReader reader = SinanUtil.retornaObjetoDbfCaminhoArquivo("MUNICNET", "dbf\\");
        Object[] rowObjects;
        DBFUtil utilDbf = new DBFUtil();
        try {
            utilDbf.mapearPosicoes(reader);
            while ((rowObjects = reader.nextRecord()) != null) {
                if (idMunicipio.equals(utilDbf.getString(rowObjects, "ID_MUNICIP"))) {
                    return utilDbf.getString(rowObjects, "ID_REGIONA");
                }
            }
        } catch (DBFException e) {
            Master.mensagem("Erro: regional nao encontrada.Verifique se existe a pasta DBF e se os arquivo REGIAO.DBF está lá:\n" + e);
        }
        return "";
    }

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
            Master.mensagem("Erro: regional nao encontrada.Verifique se existe a pasta DBF e se os arquivo REGIAO.DBF está lá:\n" + e);
        }
        return "";
    }

    public String buscaIdRegiaoSaude(String idMunicipio) throws SQLException {
        if (idMunicipio == null) {
            return "";
        }

        DBFReader reader = SinanUtil.retornaObjetoDbfCaminhoArquivo("MUNICNET", "dbf\\");
        Object[] rowObjects;
        DBFUtil utilDbf = new DBFUtil();
        try {
            utilDbf.mapearPosicoes(reader);
            while ((rowObjects = reader.nextRecord()) != null) {
                if (idMunicipio.equals(utilDbf.getString(rowObjects, "ID_MUNICIP"))) {
                    return utilDbf.getString(rowObjects, "ID_REGIAO");
                }
            }
        } catch (DBFException e) {
            Master.mensagem("Erro: regional nao encontrada.Verifique se existe a pasta DBF e se os arquivo REGIAO.DBF está lá:\n" + e);
        }
        return "";
    }

    public List getBeansMunicipioEspecifico(Connection con, Map parametros) throws SQLException {
        return this.getBeanMunicipioEspecifico(con, parametros);
    }

    public String getNomeMunicipio() {
        return nomeMunicipio;
    }

    public void setNomeMunicipio(String nomeMunicipio) {
        this.nomeMunicipio = nomeMunicipio;
    }

    public String getCodMunicipio() {
        return codMunicipio;
    }

    public void setCodMunicipio(String codMunicipio) {
        this.codMunicipio = codMunicipio;
    }

    public String getNumerador() {
        return numerador;
    }

    public void setNumerador(String numerador) {
        this.numerador = numerador;
    }

    public String getTaxa() {
        return taxa;
    }

    public void setTaxa(String taxa) {
        this.taxa = taxa;
    }

    public String getDenominador() {
        return denominador;
    }

    public void setDenominador(String denominador) {
        this.denominador = denominador;
    }

    public String getSqlNumeradorMunicipioEspecifico() {
        return sqlNumeradorMunicipioEspecifico;
    }

    public void setSqlNumeradorMunicipioEspecifico(String sqlNumeradorMunicipioEspecifico) {
        this.sqlNumeradorMunicipioEspecifico = sqlNumeradorMunicipioEspecifico;
    }

    public String getSqlDenominadorMunicipioEspecifico() {
        return sqlDenominadorMunicipioEspecifico;
    }

    public void setSqlDenominadorMunicipioEspecifico(String sqlDenominadorMunicipioEspecifico) {
        this.sqlDenominadorMunicipioEspecifico = sqlDenominadorMunicipioEspecifico;
    }

    public String getSqlNumeradorBeanMunicipios() {
        return sqlNumeradorBeanMunicipios;
    }

    public void setSqlNumeradorBeanMunicipios(String sqlNumeradorBeanMunicipios) {
        this.sqlNumeradorBeanMunicipios = sqlNumeradorBeanMunicipios;
    }

    public String getSqlDenominadorBeanMunicipios() {
        return sqlDenominadorBeanMunicipios;
    }

    public void setSqlDenominadorBeanMunicipios(String sqlDenominadorBeanMunicipios) {
        this.sqlDenominadorBeanMunicipios = sqlDenominadorBeanMunicipios;
    }

    public String getSqlNumeradorEstado() {
        return sqlNumeradorEstado;
    }

    public String getSqlDenominandorEstado() {
        return sqlDenominandorEstado;
    }

    public void setSqlNumeradorEstado(String sqlNumeradorEstado) {
        this.sqlNumeradorEstado = sqlNumeradorEstado;
    }

    public void setSqlDenominandorEstado(String sqlDenominandorEstado) {
        this.sqlDenominandorEstado = sqlDenominandorEstado;
    }

    public String getSqlNumeradorCompletitude() {
        return sqlNumeradorCompletitude;
    }

    public int getMultiplicador() {
        return Multiplicador;
    }

    public void setMultiplicador(int Multiplicador) {
        this.Multiplicador = Multiplicador;
    }

    public void setSqlNumeradorCompletitude(String sqlNumeradorCompletitude) {
        this.sqlNumeradorCompletitude = sqlNumeradorCompletitude;
    }

    public String getTitulo1() {
        return Titulo1;
    }

    public void setTitulo1(String Titulo1) {
        this.Titulo1 = Titulo1;
    }

    public String getTextoCompletitude() {
        return TextoCompletitude;
    }

    public void setTextoCompletitude(String TextoCompletitude) {
        this.TextoCompletitude = TextoCompletitude;
    }

    public String getTituloColuna() {
        return TituloColuna;
    }

    public void setTituloColuna(String TituloColuna) {
        this.TituloColuna = TituloColuna;
    }

    public String getRodape() {
        return Rodape;
    }

    public void setRodape(String Rodape1) {
        this.Rodape = Rodape1;
    }

    public String getTipo() {
        return Tipo;
    }

    public void setTipo(String Tipo) {
        this.Tipo = Tipo;
    }

    public JLabel getLabel() {
        return label;
    }

    public void setLabel(JLabel label) {
        this.label = label;
    }

    public String getTransfNaoEspecificada() {
        return transfNaoEspecificada;
    }

    public void setTransfNaoEspecificada(String transfNaoEspecificada) {
        this.transfNaoEspecificada = transfNaoEspecificada;
    }

    /**
     * @return the barraStatus
     */
    public JProgressBar getBarraStatus() {
        return barraStatus;
    }

    /**
     * @param barraStatus the barraStatus to set
     */
    public void setBarraStatus(JProgressBar barraStatus) {
        this.barraStatus = barraStatus;
    }

    /**
     * @return the colunas
     */
    public HashMap<String, ColunasDbf> getColunas() {
        return colunas;
    }

    /**
     * @param colunas the colunas to set
     */
    public void setColunas(HashMap<String, ColunasDbf> colunas) {
        this.colunas = colunas;
    }

    /**
     * @return the parametros
     */
    public Map getParametros() {
        return parametros;
    }

    /**
     * @param parametros the parametros to set
     */
    public void setParametros(Map parametros) {
        this.parametros = parametros;
    }

    /**
     * @return the caminhoJasper
     */
    public String getCaminhoJasper() {
        return caminhoJasper;
    }

    /**
     * @param caminhoJasper the caminhoJasper to set
     */
    public void setCaminhoJasper(String caminhoJasper) {
        this.caminhoJasper = caminhoJasper;
    }

    /**
     * @return the ordemColunas informa a order do header das colunas do dbf a
     * ser exportado
     */
    public String[] getOrdemColunas() {
        return ordemColunas;
    }

    /**
     * @param ordemColunas the ordemColunas to set
     */
    public void setOrdemColunas(String[] ordemColunas) {
        this.ordemColunas = ordemColunas;
    }

    /**
     * @return the util
     */
    public Util getUtil() {
        return util;
    }

    /**
     * @param util the util to set
     */
    public void setUtil(Util util) {
        this.util = util;
    }

    /**
     * @return the conf
     */
    public Configuracao getConf() {
        return conf;
    }

    /**
     * @param conf the conf to set
     */
    public void setConf(Configuracao conf) {
        this.conf = conf;
    }

    /**
     * @return the arquivo
     */
    public String getArquivo() {
        return arquivo;
    }

    /**
     * @param arquivo the arquivo to set
     */
    public void setArquivo(String arquivo) {
        this.arquivo = arquivo;
    }

    /**
     * @return the taxaEstadual
     */
    public String getTaxaEstadual() {
        return taxaEstadual;
    }

    /**
     * @param taxaEstadual the taxaEstadual to set
     */
    public void setTaxaEstadual(String taxaEstadual) {
        this.taxaEstadual = taxaEstadual;
    }

    /**
     * @return the beans
     */
    public List getBeans() {
        return beans;
    }

    /**
     * @param beans the beans to set
     */
    public void setBeans(List beans) {
        this.beans = beans;
    }

    /**
     * @return the percentualCompletitude
     */
    public String getPercentualCompletitude() {
        return percentualCompletitude;
    }

    /**
     * @param percentualCompletitude the percentualCompletitude to set
     */
    public void setPercentualCompletitude(String percentualCompletitude) {
        this.percentualCompletitude = percentualCompletitude;
    }

    /**
     * @return the DBF
     */
    public boolean isDBF() {
        return DBF;
    }

    /**
     * @param DBF the DBF to set
     */
    public void setDBF(boolean DBF) {
        this.DBF = DBF;
    }

    /**
     * @return the dataInicio
     */
    public String getDataInicio() {
        return dataInicio;
    }

    /**
     * @param dataInicio the dataInicio to set
     */
    public void setDataInicio(String dataInicio) {
        this.dataInicio = dataInicio;
    }

    /**
     * @return the dataFim
     */
    public String getDataFim() {
        return dataFim;
    }

    /**
     * @param dataFim the dataFim to set
     */
    public void setDataFim(String dataFim) {
        this.dataFim = dataFim;
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
     * @return the temListagem
     */
    public boolean isTemListagem() {
        return temListagem;
    }

    /**
     * @param temListagem the temListagem to set
     */
    public void setTemListagem(boolean temListagem) {
        this.temListagem = temListagem;
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

    /**
     * @return the regional
     */
    public String getRegional() {
        return regional;
    }

    /**
     * @param regional the regional to set
     */
    public void setRegional(String regional) {
        this.regional = regional;
    }

    /**
     * @return the listagemCasos
     */
    public List<com.org.model.classes.agravos.oportunidade.CasoOportunidade> getListagemCasos() {
        return listagemCasos;
    }

    /**
     * @param listagemCasos the listagemCasos to set
     */
    public void setListagemCasos(List<com.org.model.classes.agravos.oportunidade.CasoOportunidade> listagemCasos) {
        this.listagemCasos = listagemCasos;
    }

    /**
     * @return the listagemCasos
     */
    public List<com.org.model.classes.agravos.oportunidade.CasoOportunidadeCOAP> getListagemCasosCOAP() {
        return listagemCasosCOAP;
    }

    /**
     * @return the listagemCasos
     */
    public List<com.org.model.classes.agravos.oportunidade.CasoOportunidadePQAVS> getListagemCasosPQAVS() {
        return listagemCasosPQAVS;
    }

    /**
     * @param listagemCasos the listagemCasos to set
     */
    public void setListagemCasosCOAP(List<com.org.model.classes.agravos.oportunidade.CasoOportunidadeCOAP> listagemCasosCOAP) {
        this.listagemCasosCOAP = listagemCasosCOAP;
    }

    /**
     * @param listagemCasos the listagemCasos to set
     */
    public void setListagemCasosPQAVS(List<com.org.model.classes.agravos.oportunidade.CasoOportunidadePQAVS> listagemCasosPQAVS) {
        this.listagemCasosPQAVS = listagemCasosPQAVS;
    }

    /**
     * @return the periodo
     */
    public String getPeriodo() {
        return periodo;
    }

    /**
     * @param periodo the periodo to set
     */
    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    /**
     * @return the tipoAgregacao
     */
    public String getTipoAgregacao() {
        return tipoAgregacao;
    }

    /**
     * @param tipoAgregacao the tipoAgregacao to set
     */
    public void setTipoAgregacao(String tipoAgregacao) {
        this.tipoAgregacao = tipoAgregacao;
    }

    public Map populaSemana(Map parametros) {
        return null;
    }

    public JProgressBar getBarraStatusGeral() {
        return barraStatusGeral;
    }

    public void setBarraStatusGeral(JProgressBar barraStatusGeral) {
        this.barraStatusGeral = barraStatusGeral;
    }

    public String getDtFimReceb() {
        return dtFimReceb;
    }

    public void setDtFimReceb(String dtFimReceb) {
        this.dtFimReceb = dtFimReceb;
    }

    public String getDtFimTransf() {
        return dtFimTransf;
    }

    public void setDtFimTransf(String dtFimTransf) {
        this.dtFimTransf = dtFimTransf;
    }

    public String getDtInicioReceb() {
        return dtInicioReceb;
    }

    public void setDtInicioReceb(String dtInicioReceb) {
        this.dtInicioReceb = dtInicioReceb;
    }

    public String getDtInicioTransf() {
        return dtInicioTransf;
    }

    public void setDtInicioTransf(String dtInicioTransf) {
        this.dtInicioTransf = dtInicioTransf;
    }

    public String getRegiaoSaude() {
        return regiaoSaude;
    }

    public void setRegiaoSaude(String regiaoSaude) {
        this.regiaoSaude = regiaoSaude;
    }

    public String getCodRegional() {
        return codRegional;
    }

    public void setCodRegional(String codRegional) {
        this.codRegional = codRegional;
    }

    public String getCodRegiaoSaude() {
        return codRegiaoSaude;
    }

    public void setCodRegiaoSaude(String codRegiaoSaude) {
        this.codRegiaoSaude = codRegiaoSaude;
    }

    public Integer getNumeradorInt() {
        return numeradorInt;
    }

    public void setNumeradorInt(Integer numeradorInt) {
        this.numeradorInt = numeradorInt;
    }

    public Integer getDenominadorInt() {
        return denominadorInt;
    }

    public void setDenominadorInt(Integer denominadorInt) {
        this.denominadorInt = denominadorInt;
    }

}
