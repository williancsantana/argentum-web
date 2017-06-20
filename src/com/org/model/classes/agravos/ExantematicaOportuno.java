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
import com.org.negocio.Util;
import com.org.util.SinanUtil;
import com.org.view.Master;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.beanutils.BeanComparator;

/**
 *
 * @author geraldo
 */
public class ExantematicaOportuno extends Agravo {

    static String ANO;
    private boolean isAdequadamente = false;

    public ExantematicaOportuno(boolean isDbf, boolean relAdequado) {
        this.setDBF(isDbf);
        setPeriodo("de notificação");
        setTipoAgregacao("de Residência");
        if (!relAdequado) {
            init("postgres");
        } else {
            initAdequadamente("postgres");

        }
    }

    @Override
    public void init(String tipoBanco) {
        this.setArquivo("EXANTNET");
        this.setTextoCompletitude("");
        this.setMultiplicador(100);
        this.setTipo("");
        this.setTitulo1("Proporção de doenças exantemáticas investigados oportunamente");
        this.setTituloColuna("Proporção");
        this.setRodape("Numerador: Total de casos suspeitos de sarampo e rubéola investigados em até 48 horas após a notificação, residentes em determinado local e notificados em determinado período \n" + "Denominador: Total de casos suspeitos de sarampo e rubéola,  residentes em determinado local e notificados em determinado período ");
        this.setSqlNumeradorCompletitude("");
        if (!isDBF()) {
            this.setSqlNumeradorMunicipioEspecifico("select count(*) as numerador from dbsinan.tb_notificacao as t1 " + "where t1.co_cid = 'B09' and  (dt_notificacao BETWEEN ?  AND ?) and " + "t1.co_uf_residencia= ? and " + "t1.co_municipio_residencia = ? and " + "dt_investigacao is not null  " + " and (dt_investigacao-dt_notificacao) <= 2" + " and (dt_investigacao-dt_notificacao) >= 0 and (tp_duplicidade is null or tp_duplicidade <2)");
            this.setSqlDenominadorMunicipioEspecifico("select count(*) as denominador from dbsinan.tb_notificacao as t1 " + "where t1.co_cid = 'B09' and  (dt_notificacao BETWEEN ?  AND ?) and " + "t1.co_uf_residencia= ? and " + "t1.co_municipio_residencia = ? and (tp_duplicidade is null or tp_duplicidade <2)");
            this.setSqlNumeradorEstado("select count(*) as numerador from dbsinan.tb_notificacao as t1 " + "where t1.co_cid = 'B09' and  (dt_notificacao BETWEEN ?  AND ?) and " + "t1.co_uf_residencia= ? and " + "dt_investigacao is not null  " + " and (dt_investigacao-dt_notificacao) <= 2" + " and (dt_investigacao-dt_notificacao) >= 0 and (tp_duplicidade is null or tp_duplicidade <2)");
            this.setSqlDenominandorEstado("select count(*) as denominador from dbsinan.tb_notificacao as t1 " + "where t1.co_cid = 'B09' and  (dt_notificacao BETWEEN ?  AND ?) and " + "t1.co_uf_residencia= ? and (tp_duplicidade is null or tp_duplicidade <2)");
        }
        this.setSqlNumeradorBeanMunicipios(this.getSqlNumeradorMunicipioEspecifico());
        this.setSqlDenominadorBeanMunicipios(this.getSqlDenominadorMunicipioEspecifico());
    }

    @Override
    public void initAdequadamente(String tipoBanco) {
        this.setArquivo("EXANTNET");
        this.setTextoCompletitude("");
        this.setMultiplicador(100);
        this.setTipo("");
        this.setTitulo1("Proporção de doenças exantemáticas investigados oportunamente e adequadamente");
        this.setTituloColuna("Proporção");
        this.setRodape("Numerador: Total de casos suspeitos de sarampo e rubéola investigados em até 48 horas após a notificação, residentes em determinado local e notificados em determinado período e que tenham as variáveis data da investigação, data do exantema, data da coleta e se realizou bloqueio vacinal prenchidas \n" + "Denominador: Total de casos suspeitos de sarampo e rubéola,  residentes em determinado local e notificados em determinado período ");
        this.setSqlNumeradorCompletitude("");
        this.setSqlNumeradorMunicipioEspecifico("select count(*) as numerador from dbsinan.tb_notificacao as t1 inner join dbsinan.tb_investiga_exantematica t2 on " + "(t1.nu_notificacao=t2.nu_notificacao and t1.dt_notificacao=t2.dt_notificacao and t1.co_municipio_notificacao=t2.co_municipio_notificacao) " + "where t1.co_cid = 'B09' and  (t2.dt_notificacao BETWEEN ?  AND ?) and " + "t1.co_uf_residencia= ? and " + "t1.co_municipio_residencia = ? and " + "dt_investigacao is not null and dt_coleta_soro_1 is not null and dt_exantema is not null and " + "tp_bloqueio_vacinal in (1,2,3,4) and tp_bloqueio_vacinal is not null and tp_bloqueio_vacinal <> 9" + " and ((dt_investigacao-t2.dt_notificacao) <= 2) and (tp_duplicidade is null or tp_duplicidade <2)");
        this.setSqlDenominadorMunicipioEspecifico("select count(*) as denominador from dbsinan.tb_notificacao as t1 " + "where t1.co_cid = 'B09' and  (t1.dt_notificacao BETWEEN ?  AND ?) and " + "t1.co_uf_residencia= ? and " + "t1.co_municipio_residencia = ? and (tp_duplicidade is null or tp_duplicidade <2)");
        this.setSqlNumeradorEstado("select count(*) as numerador from dbsinan.tb_notificacao as t1 inner join dbsinan.tb_investiga_exantematica t2 on " + "(t1.nu_notificacao=t2.nu_notificacao and t1.dt_notificacao=t2.dt_notificacao and t1.co_municipio_notificacao=t2.co_municipio_notificacao) " + "where t1.co_cid = 'B09' and  (t2.dt_notificacao BETWEEN ?  AND ?) and " + "t1.co_uf_residencia= ? and " + "dt_investigacao is not null and dt_coleta_soro_1 is not null and dt_exantema is not null and " + "tp_bloqueio_vacinal in (1,2,3,4) and tp_bloqueio_vacinal is not null and tp_bloqueio_vacinal <> 9" + " and ((dt_investigacao-t2.dt_notificacao) <= 2) and (tp_duplicidade is null or tp_duplicidade <2)");
        this.setSqlDenominandorEstado("select count(*) as denominador from dbsinan.tb_notificacao as t1 " + "where t1.co_cid = 'B09' and  (t1.dt_notificacao BETWEEN ?  AND ?) and " + "t1.co_uf_residencia= ? and (tp_duplicidade is null or tp_duplicidade <2)");
        this.setSqlNumeradorBeanMunicipios(this.getSqlNumeradorMunicipioEspecifico());
        this.setSqlDenominadorBeanMunicipios(this.getSqlDenominadorMunicipioEspecifico());

    }

    public void calculaBrasil(DBFReader reader, Map parametros) throws ParseException {
        //buscar os municipios que vao para o resultado
        HashMap<String, Agravo> municipiosBeans = new HashMap<String, Agravo>();
        DBFUtil utilDbf = new DBFUtil();

        String coluna;
        if (parametros.get("municipios").toString().equals("sim")) {
            municipiosBeans = populaMunicipiosBeans("BR", "");
            coluna = "ID_MN_RESI";
        } else {
            municipiosBeans = populaUfsBeans();
            coluna = "SG_UF";
        }

        //inicia o calculo

        Object[] rowObjects;
        Date dtInvestigacao;
        Date dtNotificacao;
        int completitude = 0;
        String total;
        DecimalFormat df = new DecimalFormat("0.00");
        int denominador = 0;
        int numerador = 0;
        int numeradorEstadual = 0;
        int denominadorEstadual = 0;

        Agravo municipioResidencia;
        String dataInicio = (String) parametros.get("parDataInicio");
        String dataFim = (String) parametros.get("parDataFim");
        int i = 1;
        try {
            utilDbf.mapearPosicoes(reader);
            double TotalRegistros = Double.parseDouble(String.valueOf(reader.getRecordCount()));
            while ((rowObjects = reader.nextRecord()) != null) {
                //cálculo da taxa estadual
                //verifica a uf de residencia
                if (utilDbf.getString(rowObjects, coluna) != null) {
                    //verifica se existe a referencia do municipio no bean
                    municipioResidencia = municipiosBeans.get(utilDbf.getString(rowObjects, coluna));
                    dtNotificacao = utilDbf.getDate(rowObjects, "DT_NOTIFIC");
                    dtInvestigacao = utilDbf.getDate(rowObjects, "DT_INVEST");
                    if (municipioResidencia != null) {

                        if (isBetweenDates(dtNotificacao, dataInicio, dataFim)) {
                            //incrementa o denominador
                            denominador = Integer.parseInt(municipioResidencia.getDenominador());
                            denominador++;
                            municipioResidencia.setDenominador(String.valueOf(denominador));
                            denominadorEstadual++;
                            //se dt_investigacao <= 2 entao entra no calculo do numerador
                            if (dtInvestigacao != null) {
                                if (this.dataDiff(dtNotificacao, dtInvestigacao) <= 2) {
                                    numerador = Integer.parseInt(municipioResidencia.getNumerador());
                                    numerador++;
                                    municipioResidencia.setNumerador(String.valueOf(numerador));
                                    numeradorEstadual++;
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
//        denominadorEstadual--;
//        total = df.format(Double.parseDouble(String.valueOf(numeradorEstadual)) / Double.parseDouble(String.valueOf(denominadorEstadual)) * 100);
//        setTaxaEstadual(total + " (Numerador:" + String.valueOf(numeradorEstadual) + " / Denominador: " + String.valueOf(denominadorEstadual) + ")");
        setTaxaEstadual("");
        //calcula o percentual da completitude
       // setPercentualCompletitude(df.format(Double.parseDouble(String.valueOf(completitude)) / Double.parseDouble(String.valueOf(denominadorEstadual)) * 100));

        //CALCULA A TAXA PARA TODOS OS MUNICIPIOS
        this.setBeans(new ArrayList());
        Collection<Agravo> municipioBean = municipiosBeans.values();
        if (parametros.get("parSgUf").toString().equals("TODAS") || parametros.get("municipios").toString().equals("sim")) {
            for (Iterator<Agravo> it = municipioBean.iterator(); it.hasNext();) {
                Agravo agravoDBF = it.next();
                double num = Double.parseDouble(agravoDBF.getNumerador());
                double den = Double.parseDouble(agravoDBF.getDenominador());
                if (den == 0) {
                    agravoDBF.setTaxa("0.00");
                } else {
                    agravoDBF.setTaxa(df.format(num / den * 100));
                }
                this.getBeans().add(agravoDBF);
            }
            Collections.sort(this.getBeans(), new BeanComparator("nomeMunicipio"));
        }
        //calcular o total
        this.getBeans().add(adicionaBrasil(municipioBean));
    }

    public void calculaMunicipios(DBFReader reader, Map parametros) throws ParseException {
        //buscar os municipios que vao para o resultado
        HashMap<String, Agravo> municipiosBeans = new HashMap<String, Agravo>();
        String ufResidencia = (String) parametros.get("parUf");
        String sgUfResidencia = (String) parametros.get("parSgUf");
        String codRegional = (String) parametros.get("parCodRegional");
        DBFUtil utilDbf = new DBFUtil();

        municipiosBeans = populaMunicipiosBeans(sgUfResidencia, codRegional);

        //inicia o calculo

        Object[] rowObjects;
        Date dtInvestigacao;
        Date dtNotificacao;
        int completitude = 0;
        String total;
        DecimalFormat df = new DecimalFormat("0.00");
        int denominador = 0;
        int numerador = 0;
        int numeradorEstadual = 0;
        int denominadorEstadual = 0;

        Agravo municipioResidencia;
        String dataInicio = (String) parametros.get("parDataInicio");
        String dataFim = (String) parametros.get("parDataFim");
        int i = 1;
        try {
            utilDbf.mapearPosicoes(reader);
            double TotalRegistros = Double.parseDouble(String.valueOf(reader.getRecordCount()));
            while ((rowObjects = reader.nextRecord()) != null) {
                //cálculo da taxa estadual
                //verifica a uf de residencia
                if (utilDbf.getString(rowObjects, "SG_UF") != null) {
                    //verifica se existe a referencia do municipio no bean
                    municipioResidencia = municipiosBeans.get(utilDbf.getString(rowObjects, "ID_MN_RESI"));
                    dtNotificacao = utilDbf.getDate(rowObjects, "DT_NOTIFIC");
                    dtInvestigacao = utilDbf.getDate(rowObjects, "DT_INVEST");
                    if (municipioResidencia != null) {

                        if (isBetweenDates(dtNotificacao, dataInicio, dataFim)) {
                            //incrementa o denominador
                            denominador = Integer.parseInt(municipioResidencia.getDenominador());
                            denominador++;
                            municipioResidencia.setDenominador(String.valueOf(denominador));
                            denominadorEstadual++;
                            //se dt_investigacao <= 2 entao entra no calculo do numerador
                            if (dtInvestigacao != null) {
                                if (this.dataDiff(dtNotificacao, dtInvestigacao) <= 2) {
                                    numerador = Integer.parseInt(municipioResidencia.getNumerador());
                                    numerador++;
                                    municipioResidencia.setNumerador(String.valueOf(numerador));
                                    numeradorEstadual++;
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
//        denominadorEstadual--;
        total = df.format(Double.parseDouble(String.valueOf(numeradorEstadual)) / Double.parseDouble(String.valueOf(denominadorEstadual)) * 100);
        setTaxaEstadual(total + " (Numerador:" + String.valueOf(numeradorEstadual) + " / Denominador: " + String.valueOf(denominadorEstadual) + ")");
        //calcula o percentual da completitude
       // setPercentualCompletitude(df.format(Double.parseDouble(String.valueOf(completitude)) / Double.parseDouble(String.valueOf(denominadorEstadual)) * 100));

        //CALCULA A TAXA PARA TODOS OS MUNICIPIOS
        this.setBeans(new ArrayList());
        Collection<Agravo> municipioBean = municipiosBeans.values();

        for (Iterator<Agravo> it = municipioBean.iterator(); it.hasNext();) {
            Agravo agravoDBF = it.next();
            double num = Double.parseDouble(agravoDBF.getNumerador());
            double den = Double.parseDouble(agravoDBF.getDenominador());
            if (den == 0) {
                agravoDBF.setTaxa("0.00");
            } else {
                agravoDBF.setTaxa(df.format(num / den * 100));
            }
            this.getBeans().add(agravoDBF);
        }
        Collections.sort(this.getBeans(), new BeanComparator("nomeMunicipio"));
    }

    @Override
    public void calcula(DBFReader reader, Map parametros) {
        String municipios = (String) parametros.get("municipios");
        String brasil = (String) parametros.get("parUf");
        if(parametros.get("parTitulo1").equals("Proporção de doenças exantemáticas investigados oportunamente e adequadamente")){
            isAdequadamente = true;
        }
        
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
                    Date dtInvestigacao;
                    Date dtNotificacao;
                    int completitude = 0;
                    String total;
                    DecimalFormat df = new DecimalFormat("0.00");
                    int denominadorEstadual = 0;
                    int numeradorEstadual = 0;
                    int denominadorEspecifico = 0;
                    int numeradorEspecifico = 0;
//                and dt_coleta_soro_1 is not null and dt_exantema is not null and " +
//                    "tp_bloqueio_vacinal in (1,2,3,4) and tp_bloqueio_vacinal is not null and tp_bloqueio_vacinal <> 9
                    //variaveis para relatorio oportuna e adequadamente
                    Date dtColetaSoro = null, dtEnxantema = null;
                    String tpBloqueioVacinal = "";
                    String ufResidencia = (String) parametros.get("parUf");
                    String municipioResidencia = (String) parametros.get("parMunicipio");
                    if (municipioResidencia == null) {
                        municipioResidencia = "";
                    }
                    String dataInicio = (String) parametros.get("parDataInicio");
                    String dataFim = (String) parametros.get("parDataFim");
                    int i = 1;
                    utilDbf.mapearPosicoes(reader);
                    double TotalRegistros = Double.parseDouble(String.valueOf(reader.getRecordCount()));
                    while ((rowObjects = reader.nextRecord()) != null) {
                        //cálculo da taxa estadual
                        //verifica a uf de residencia
                        if (utilDbf.getString(rowObjects, "SG_UF") != null) {
                            if (utilDbf.getString(rowObjects, "SG_UF").equals(ufResidencia)) {
                                //verifica se tem o parametro de municipio de residencia
                                
                                dtNotificacao = utilDbf.getDate(rowObjects, "DT_NOTIFIC");
                                dtInvestigacao = utilDbf.getDate(rowObjects, "DT_INVEST");
                                if (isAdequadamente) {
                                    dtColetaSoro = utilDbf.getDate(rowObjects, "DT_COL_1");
                                    try {
                                        dtEnxantema = utilDbf.getDate(rowObjects, "DT_INICIO_");
                                    } catch (Exception e) {
                                        dtEnxantema = null;
                                    }

                                    tpBloqueioVacinal = utilDbf.getString(rowObjects, "CS_VACINAL");
                                    if (tpBloqueioVacinal == null) {
                                        tpBloqueioVacinal = "";
                                    }
                                }
                                if (verificaMunicipio(municipioResidencia, utilDbf.getString(rowObjects, "ID_MN_RESI"))) {
                                    if (isBetweenDates(dtNotificacao, dataInicio, dataFim)) {
                                        System.out.println(utilDbf.getString(rowObjects, "NU_NOTIFIC"));
                                        //incrementa o denominador
                                        denominadorEspecifico++;
                                        denominadorEstadual++;
                                        //se dt_investigacao <= 2 entao entra no calculo do numerador
                                        if (dtInvestigacao != null) {
                                            if (this.dataDiff(dtNotificacao, dtInvestigacao) <= 2) {
                                                if (isAdequadamente) {
                                                    if (dtColetaSoro != null && dtEnxantema != null && (tpBloqueioVacinal.equals("1") || tpBloqueioVacinal.equals("2") || tpBloqueioVacinal.equals("3") || tpBloqueioVacinal.equals("4"))) {
                                                        numeradorEspecifico++;
                                                    }
                                                    numeradorEstadual++;
                                                } else {
                                                    numeradorEspecifico++;
                                                    numeradorEstadual++;
                                                }
                                            }
                                        }
                                    }

                                } else {
                                    //CALCULA A TAXA ESTADUAL
                                    if (isBetweenDates(dtNotificacao, dataInicio, dataFim)) {
                                        //denominadorEstadual
                                        denominadorEstadual++;
                                        //se dt_investigacao <= 2 entao entra no calculo do numerador
                                        if (dtInvestigacao != null) {
                                            if (this.dataDiff(dtNotificacao, dtInvestigacao) <= 2) {
                                                numeradorEstadual++;
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
                    total = df.format(Double.parseDouble(String.valueOf(numeradorEstadual)) / Double.parseDouble(String.valueOf(denominadorEstadual)) * 100);

                    setTaxaEstadual(total + " (Numerador:" + String.valueOf(numeradorEstadual) + " / Denominador: " + String.valueOf(denominadorEstadual) + ")");
                    //calcula o percentual da completitude
                   // setPercentualCompletitude(df.format(Double.parseDouble(String.valueOf(completitude)) / Double.parseDouble(String.valueOf(denominadorEstadual)) * 100));
                    //começa o preencher o bean para estado ou 1 municipio
                    Agravo d1 = new Agravo();
                    d1.setCodMunicipio((String) parametros.get("parMunicipio"));//falta aqui
                    if (municipioResidencia.equals("")) {
                        d1.setNomeMunicipio((String) parametros.get("parSgUf"));
                    } else {
                        d1.setNomeMunicipio((String) parametros.get("parNomeMunicipio"));
                    }
                    if (!String.valueOf(denominadorEspecifico).equals("0.0")) {
                        d1.setNumerador(String.valueOf(NumberFormat.getNumberInstance().format(Double.parseDouble(String.valueOf(numeradorEspecifico)))));
                        d1.setDenominador(String.valueOf(NumberFormat.getNumberInstance().format(Double.parseDouble(String.valueOf(denominadorEspecifico)))));
                        total = df.format(Double.parseDouble(String.valueOf(numeradorEspecifico)) / Double.parseDouble(String.valueOf(denominadorEspecifico)) * 100);
                        d1.setTaxa(total);
                    } else {
                        d1.setNumerador("0");
                        d1.setDenominador("0");
                        d1.setTaxa("0.00");
                    }
                    this.setBeans(new ArrayList());
                    this.getBeans().add(d1);
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
    public HashMap<String, ColunasDbf> getColunas() {
        HashMap<String, ColunasDbf> hashColunas = new HashMap<String, ColunasDbf>();
        hashColunas.put("ID_LOCRES", new ColunasDbf(7));
        hashColunas.put("DS_LOCRES", new ColunasDbf(30));
        hashColunas.put("ID_UFRES", new ColunasDbf(2));
        if(isAdequadamente){
            hashColunas.put("N_ADQEXAN", new ColunasDbf(10, 0));
            hashColunas.put("D_ADQEXAN", new ColunasDbf(10, 0));
            hashColunas.put("I_ADQEXAN", new ColunasDbf(6, 2));
        }else{
            hashColunas.put("N_OPOEXAN", new ColunasDbf(10, 0));
            hashColunas.put("D_OPOEXAN", new ColunasDbf(10, 0));
            hashColunas.put("I_OPOEXAN", new ColunasDbf(6, 2));
        }
        hashColunas.put("ANO_NOTIF", new ColunasDbf(4, 0));
        hashColunas.put("DT_NOTINI", new ColunasDbf(10));
        hashColunas.put("DT_NOTFIN", new ColunasDbf(10));
        hashColunas.put("ORIGEM", new ColunasDbf(30));
        this.setColunas(hashColunas);
        return hashColunas;
    }

    @Override
    public String[] getOrdemColunas() {
        if(isAdequadamente)
            return new String[]{"ID_LOCRES", "DS_LOCRES", "ID_UFRES", "N_ADQEXAN", "D_ADQEXAN",
            "I_ADQEXAN", "ANO_NOTIF", "DT_NOTINI", "DT_NOTFIN", "ORIGEM"};
        return new String[]{"ID_LOCRES", "DS_LOCRES", "ID_UFRES", "N_OPOEXAN", "D_OPOEXAN",
        "I_OPOEXAN", "ANO_NOTIF", "DT_NOTINI", "DT_NOTFIN", "ORIGEM"};
    }

    @Override
    public Map getParametros() {
        Util util = new Util();
        Map parametros = new HashMap();
        parametros.put("parDataInicio", util.formataData(this.getDataInicio()));
        parametros.put("parDataFim", util.formataData(this.getDataFim()));
        parametros.put("parPeriodo", "de " + this.getDataInicio() + " a " + this.getDataFim());
        parametros.put("parTituloColuna", this.getTituloColuna());
        parametros.put("parFator", String.valueOf(this.getMultiplicador()));
        parametros.put("parAno", util.getAno(this.getDataFim()));
        parametros.put("parRodape", this.getRodape());
        parametros.put("parConfig", "");
        parametros.put("parTitulo1", this.getTitulo1());
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
            rowData[1] = agravo.getNomeMunicipio();
            rowData[3] = Double.parseDouble(agravo.getNumerador());
            rowData[4] = Double.parseDouble(agravo.getDenominador());
            rowData[5] = Double.parseDouble(agravo.getTaxa().replace(",", "."));
            rowData[6] = preencheAno(getDataInicio(),getDataFim());
            rowData[7] = getDataInicio();
            rowData[8] = getDataFim();
            rowData[9] = "EXANTEMATICAS-SINANNET";

            writer.addRecord(rowData);
        }
        return writer;
    }

    @Override
    public String getCaminhoJasper() {
        return "/com/org/relatorios/agravo1.jasper";
    }
}
