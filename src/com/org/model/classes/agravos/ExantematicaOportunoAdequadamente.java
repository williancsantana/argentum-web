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
 * @author taidson
 */
public class ExantematicaOportunoAdequadamente extends ExantematicaOportuno {

    public ExantematicaOportunoAdequadamente(boolean isDbf, boolean relAdequado) {
        super(isDbf, relAdequado);
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
        if (!isDBF()) {
            this.setSqlNumeradorMunicipioEspecifico("select count(*) as numerador from dbsinan.tb_notificacao as t1 inner join dbsinan.tb_investiga_exantematica t2 on " + "(t1.nu_notificacao=t2.nu_notificacao and t1.dt_notificacao=t2.dt_notificacao and t1.co_municipio_notificacao=t2.co_municipio_notificacao) " + "where t1.co_cid = 'B09' and  (t2.dt_notificacao BETWEEN ?  AND ?) and " + "t1.co_uf_residencia= ? and " + "t1.co_municipio_residencia = ? and " + "dt_investigacao is not null and dt_coleta_soro_1 is not null and dt_exantema is not null and " + "tp_bloqueio_vacinal in (1,2,3,4) and tp_bloqueio_vacinal is not null and tp_bloqueio_vacinal <> 9" + " and ((dt_investigacao-t2.dt_notificacao) <= 2) and (tp_duplicidade is null or tp_duplicidade <2)");
            this.setSqlDenominadorMunicipioEspecifico("select count(*) as denominador from dbsinan.tb_notificacao as t1 " + "where t1.co_cid = 'B09' and  (t1.dt_notificacao BETWEEN ?  AND ?) and " + "t1.co_uf_residencia= ? and " + "t1.co_municipio_residencia = ? and (tp_duplicidade is null or tp_duplicidade <2)");
            this.setSqlNumeradorEstado("select count(*) as numerador from dbsinan.tb_notificacao as t1 inner join dbsinan.tb_investiga_exantematica t2 on " + "(t1.nu_notificacao=t2.nu_notificacao and t1.dt_notificacao=t2.dt_notificacao and t1.co_municipio_notificacao=t2.co_municipio_notificacao) " + "where t1.co_cid = 'B09' and  (t2.dt_notificacao BETWEEN ?  AND ?) and " + "t1.co_uf_residencia= ? and " + "dt_investigacao is not null and dt_coleta_soro_1 is not null and dt_exantema is not null and " + "tp_bloqueio_vacinal in (1,2,3,4) and tp_bloqueio_vacinal is not null and tp_bloqueio_vacinal <> 9" + " and ((dt_investigacao-t2.dt_notificacao) <= 2) and (tp_duplicidade is null or tp_duplicidade <2)");
            this.setSqlDenominandorEstado("select count(*) as denominador from dbsinan.tb_notificacao as t1 " + "where t1.co_cid = 'B09' and  (t1.dt_notificacao BETWEEN ?  AND ?) and " + "t1.co_uf_residencia= ? and (tp_duplicidade is null or tp_duplicidade <2)");
        }
        this.setSqlNumeradorBeanMunicipios(this.getSqlNumeradorMunicipioEspecifico());
        this.setSqlDenominadorBeanMunicipios(this.getSqlDenominadorMunicipioEspecifico());

    }

    @Override
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
        Date dtColeta;
        Date dtExantema;
        String bloqueioVacinal;
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
                    dtColeta = utilDbf.getDate(rowObjects, "DT_COL_1");
                    dtExantema = utilDbf.getDate(rowObjects, "DT_INICIO_");
                    bloqueioVacinal = utilDbf.getString(rowObjects, "CS_VACINAL");
                    
                    if (municipioResidencia != null) {

                        if (isBetweenDates(dtNotificacao, dataInicio, dataFim)) {
                            //incrementa o denominador
                            denominador = Integer.parseInt(municipioResidencia.getDenominador());
                            denominador++;
                            municipioResidencia.setDenominador(String.valueOf(denominador));
                            denominadorEstadual++;
                            //se dt_investigacao <= 2 entao entra no calculo do numerador
                            if (dtInvestigacao != null && dtColeta != null && dtExantema != null && bloqueioVacinal != null) {
                                if (this.dataDiff(dtNotificacao, dtInvestigacao) <= 2 
                                        && (bloqueioVacinal.equals("1") || bloqueioVacinal.equals("2") 
                                        || bloqueioVacinal.equals("3") || bloqueioVacinal.equals("4")) && !bloqueioVacinal.equals("9")) {
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

    @Override
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
        Date dtColeta;
        Date dtExantema;
        String bloqueioVacinal;
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
                    dtColeta = utilDbf.getDate(rowObjects, "DT_COL_1");
                    dtExantema = utilDbf.getDate(rowObjects, "DT_INICIO_");
                    bloqueioVacinal = utilDbf.getString(rowObjects, "CS_VACINAL");
                    if (municipioResidencia != null) {

                        if (isBetweenDates(dtNotificacao, dataInicio, dataFim)) {
                            //incrementa o denominador
                            denominador = Integer.parseInt(municipioResidencia.getDenominador());
                            denominador++;
                            municipioResidencia.setDenominador(String.valueOf(denominador));
                            denominadorEstadual++;
                            //se dt_investigacao <= 2 entao entra no calculo do numerador
                            if (dtInvestigacao != null && dtColeta != null && dtExantema != null && bloqueioVacinal != null) {
                                if (this.dataDiff(dtNotificacao, dtInvestigacao) <= 2 
                                        && (bloqueioVacinal.equals("1") || bloqueioVacinal.equals("2") 
                                        || bloqueioVacinal.equals("3") || bloqueioVacinal.equals("4")) && !bloqueioVacinal.equals("9")) {
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
}
