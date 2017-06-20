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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.beanutils.BeanComparator;

/**
 *
 * @author geraldo
 */
public class HepatiteB extends Agravo {

    public HepatiteB(boolean isDbf) {
        this.setDBF(isDbf);
        setPeriodo("de notificação");
        setTipoAgregacao("de Residência");
        init("postgres");
    }

    public HepatiteB() {
    }

    @Override
    public void init(String tipoBanco) {
        this.setArquivo("HEPANET");
        this.setMultiplicador(100);
        this.setTitulo1("Proporção de casos de hepatites B confirmados por sorologia");
        this.setTextoCompletitude("% de não preenchimento do campo classificação etiológica: ");
        this.setTituloColuna("Proporção ");
        this.setRodape("Numerador:  Número de casos de hepatite B confirmados por sorologia reagente, residentes em determinado local e notificados em determinado período \n" + "Denominador:  Número de casos de hepatite B, residentes em determinado local e notificados em determinado período");
        this.setTipo("");
        this.setSqlNumeradorCompletitude("tem completitude");
        if (!isDBF()) {
            this.setSqlNumeradorMunicipioEspecifico("select count(*) as numerador from dbsinan.tb_notificacao as t1, dbsinan.tb_investiga_hepatite as t2 " + "where t1.co_cid = 'B19' and (t1.dt_notificacao BETWEEN ?  " + "AND ?) and " + "t1.co_uf_residencia= ? and " + "t1.co_municipio_residencia = ? and " + "t1.nu_notificacao=t2.nu_notificacao and " + "t1.dt_notificacao=t2.dt_notificacao and " + "t1.co_municipio_notificacao=t2.co_municipio_notificacao and " + "((t1.tp_classificacao_final=1 and t2.tp_classificacao_etiologica = '02' and tp_soro_hbsag = 1) or " + "(t1.tp_classificacao_final=1 and t2.tp_classificacao_etiologica = '02' and tp_soro_hbsag in (2,3) and tp_soro_antihbc_igm =1 ))");
            this.setSqlDenominadorMunicipioEspecifico("select count(*) as denominador from dbsinan.tb_notificacao as t1, dbsinan.tb_investiga_hepatite as t2 " + "where t1.co_cid = 'B19' and (t1.dt_notificacao BETWEEN ?  " + "AND ?) and " + "t1.co_uf_residencia= ? and " + "t1.co_municipio_residencia = ? and " + "t1.nu_notificacao=t2.nu_notificacao and " + "t1.dt_notificacao=t2.dt_notificacao and " + "t1.co_municipio_notificacao=t2.co_municipio_notificacao and " + "t1.tp_classificacao_final=1 and t2.tp_classificacao_etiologica ='02'");

            this.setSqlNumeradorEstado("select count(*) as numerador from dbsinan.tb_notificacao as t1, dbsinan.tb_investiga_hepatite as t2 " + "where t1.co_cid = 'B19' and (t1.dt_notificacao BETWEEN ?  " + "AND ?) and " + "t1.co_uf_residencia= ? and " + "t1.nu_notificacao=t2.nu_notificacao and " + "t1.dt_notificacao=t2.dt_notificacao and " + "t1.co_municipio_notificacao=t2.co_municipio_notificacao and " + "((t1.tp_classificacao_final=1 and t2.tp_classificacao_etiologica = '02' and tp_soro_hbsag = 1) or " + "(t1.tp_classificacao_final=1 and t2.tp_classificacao_etiologica = '02' and tp_soro_hbsag in (2,3) and tp_soro_antihbc_igm =1 ))");
            this.setSqlDenominandorEstado("select count(*) as denominador from dbsinan.tb_notificacao as t1, dbsinan.tb_investiga_hepatite as t2 " + "where t1.co_cid = 'B19' and (t1.dt_notificacao BETWEEN ?  " + "AND ?) and " + "t1.co_uf_residencia= ? and " + "t1.nu_notificacao=t2.nu_notificacao and " + "t1.dt_notificacao=t2.dt_notificacao and " + "t1.co_municipio_notificacao=t2.co_municipio_notificacao and " + "t1.tp_classificacao_final=1 and t2.tp_classificacao_etiologica ='02'");
            this.setSqlNumeradorCompletitude("select count(*) as numerador from dbsinan.tb_notificacao as t1, dbsinan.tb_investiga_hepatite as t2 " + "where t1.co_cid = 'B19' and (t1.dt_notificacao BETWEEN ?  " + "AND ?) and " + "t1.co_uf_residencia= ? and " + "t1.nu_notificacao=t2.nu_notificacao and " + "t1.dt_notificacao=t2.dt_notificacao and " + "t1.co_municipio_notificacao=t2.co_municipio_notificacao and " + "t1.tp_classificacao_final=1 and t2.tp_classificacao_etiologica is null");
            this.setSqlNumeradorBeanMunicipios(this.getSqlNumeradorMunicipioEspecifico());
            this.setSqlDenominadorBeanMunicipios(this.getSqlDenominadorMunicipioEspecifico());
        }
    }

    private void calculaBrasil(DBFReader reader, Map parametros) throws ParseException {
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
        int soroHBSAG, soroHCV, ANTIHBCIGM, classificacaoFinal, completitude = 0;
        String total;
        String classificacaoEtiologica;
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
                    soroHBSAG = utilDbf.getInt(rowObjects, "AGHBS");
                    soroHCV = utilDbf.getInt(rowObjects, "ANTIHCV");
                    ANTIHBCIGM = utilDbf.getInt(rowObjects, "ANTIHBCIGM");
                    classificacaoEtiologica = utilDbf.getString(rowObjects, "CLAS_ETIOL");
                    classificacaoFinal = utilDbf.getInt(rowObjects, "CLASSI_FIN");
                    if (municipioResidencia != null) {
                        //se estiver no hashmap, inicia o calulo
                        if (classificacaoEtiologica != null) {
                            if (classificacaoFinal == 1 && (classificacaoEtiologica.equals("02"))) {
                                try {
                                    if (isBetweenDates(utilDbf.getDate(rowObjects, "DT_NOTIFIC"), dataInicio, dataFim)) {
                                        //incrementa o denominador
                                        denominador = Integer.parseInt(municipioResidencia.getDenominador());
                                        denominador++;
                                        municipioResidencia.setDenominador(String.valueOf(denominador));
                                        denominadorEstadual++;
                                        if (soroHBSAG == 1) {
                                            numerador = Integer.parseInt(municipioResidencia.getNumerador());
                                            numerador++;
                                            municipioResidencia.setNumerador(String.valueOf(numerador));
                                            numeradorEstadual++;
                                        } else {
                                            if ((soroHBSAG == 1 || soroHBSAG == 2) && ANTIHBCIGM == 1) {
                                                numerador = Integer.parseInt(municipioResidencia.getNumerador());
                                                numerador++;
                                                municipioResidencia.setNumerador(String.valueOf(numerador));
                                                numeradorEstadual++;
                                            }
                                        }

                                    }
                                } catch (NumberFormatException ex) {
                                    Master.mensagem("Erro:\n" + ex);
                                } catch (ParseException ex) {
                                    Master.mensagem("Erro:\n" + ex);
                                }

                            }
                        } else {
                            //entra no incremento da completitude
                            completitude++;
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
        //calcula o percentual da completitude
        setTaxaEstadual("");
        setPercentualCompletitude(df.format(Double.parseDouble(String.valueOf(completitude)) / Double.parseDouble(String.valueOf(denominadorEstadual)) * 100));

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

    private void calculaMunicipios(DBFReader reader, Map parametros) throws ParseException {
        //buscar os municipios que vao para o resultado
        HashMap<String, Agravo> municipiosBeans = new HashMap<String, Agravo>();
        String sgUfResidencia = (String) parametros.get("parSgUf");
        String codRegional = (String) parametros.get("parCodRegional");
        DBFUtil utilDbf = new DBFUtil();

        municipiosBeans = populaMunicipiosBeans(sgUfResidencia, codRegional);

        //inicia o calculo

        Object[] rowObjects;
        int soroHBSAG, soroHCV, ANTIHBCIGM, classificacaoFinal, completitude = 0;
        String total;
        String classificacaoEtiologica;
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
                    soroHBSAG = utilDbf.getInt(rowObjects, "AGHBS");
                    soroHCV = utilDbf.getInt(rowObjects, "ANTIHCV");
                    ANTIHBCIGM = utilDbf.getInt(rowObjects, "ANTIHBCIGM");
                    classificacaoEtiologica = utilDbf.getString(rowObjects, "CLAS_ETIOL");
                    classificacaoFinal = utilDbf.getInt(rowObjects, "CLASSI_FIN");
                    if (municipioResidencia != null) {
                        //se estiver no hashmap, inicia o calulo
                        if (classificacaoEtiologica != null) {
                            if (classificacaoFinal == 1 && classificacaoEtiologica.equals("02")) {
                                try {
                                    if (isBetweenDates(utilDbf.getDate(rowObjects, "DT_NOTIFIC"), dataInicio, dataFim)) {
                                        //incrementa o denominador
                                        denominador = Integer.parseInt(municipioResidencia.getDenominador());
                                        denominador++;
                                        municipioResidencia.setDenominador(String.valueOf(denominador));
                                        denominadorEstadual++;
                                        if (soroHBSAG == 1) {
                                            numerador = Integer.parseInt(municipioResidencia.getNumerador());
                                            numerador++;
                                            municipioResidencia.setNumerador(String.valueOf(numerador));
                                            numeradorEstadual++;
                                        } else {
                                            if ((soroHBSAG == 1 || soroHBSAG == 2) && ANTIHBCIGM == 1) {
                                                numerador = Integer.parseInt(municipioResidencia.getNumerador());
                                                numerador++;
                                                municipioResidencia.setNumerador(String.valueOf(numerador));
                                                numeradorEstadual++;
                                            }
                                        }
                                    }
                                } catch (NumberFormatException ex) {
                                    Master.mensagem("Erro:\n" + ex);
                                } catch (ParseException ex) {
                                    Master.mensagem("Erro:\n" + ex);
                                }

                            }
                        } else {
                            //entra no incremento da completitude
                            completitude++;
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
        setPercentualCompletitude(df.format(Double.parseDouble(String.valueOf(completitude)) / Double.parseDouble(String.valueOf(denominadorEstadual)) * 100));

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
        if (municipios.equals("sim") && !brasil.equals("brasil")) {
            try {
                calculaMunicipios(reader, parametros);
            } catch (ParseException ex) {
                System.out.println(ex);
            }
        } else {

            if (brasil.equals("brasil")) {
                try {
                    calculaBrasil(reader, parametros);
                } catch (ParseException ex) {
                    System.out.println(ex);
                }
            } else {
                Object[] rowObjects;
                DBFUtil utilDbf = new DBFUtil();
                String classificacaoEtiologica;
                int soroHBSAG, soroHCV, ANTIHBCIGM, classificacaoFinal, completitude = 0;
                String total;
                DecimalFormat df = new DecimalFormat("0.00");
                int denominadorEstadual = 0;
                int numeradorEstadual = 0;
                int denominadorEspecifico = 0;
                int numeradorEspecifico = 0;
                String ufResidencia = (String) parametros.get("parUf");
                String municipioResidencia = (String) parametros.get("parMunicipio");
                if (municipioResidencia == null) {
                    municipioResidencia = "";
                }
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
                            if (utilDbf.getString(rowObjects, "SG_UF").equals(ufResidencia)) {
                                //verifica se tem o parametro de municipio de residencia
                                soroHBSAG = utilDbf.getInt(rowObjects, "AGHBS");
                                soroHCV = utilDbf.getInt(rowObjects, "ANTIHCV");
                                ANTIHBCIGM = utilDbf.getInt(rowObjects, "ANTIHBCIGM");
                                classificacaoEtiologica = utilDbf.getString(rowObjects, "CLAS_ETIOL");
                                classificacaoFinal = utilDbf.getInt(rowObjects, "CLASSI_FIN");
                                if (verificaMunicipio(municipioResidencia, utilDbf.getString(rowObjects, "ID_MN_RESI"))) {
                                    if (classificacaoEtiologica != null) {
                                        if (classificacaoFinal == 1 && classificacaoEtiologica.equals("02")) {
                                            try {
                                                if (isBetweenDates(utilDbf.getDate(rowObjects, "DT_NOTIFIC"), dataInicio, dataFim)) {
                                                    //incrementa o denominador
                                                    denominadorEspecifico++;
                                                    denominadorEstadual++;

                                                    if (soroHBSAG == 1) {
                                                        numeradorEspecifico++;
                                                        numeradorEstadual++;
                                                    } else {
                                                        if ((soroHBSAG == 1 || soroHBSAG == 2) && ANTIHBCIGM == 1) {
                                                            numeradorEspecifico++;
                                                            numeradorEstadual++;
                                                        }
                                                    }
                                                }
                                            } catch (NumberFormatException ex) {
                                                Master.mensagem("Erro:\n" + ex);
                                            } catch (ParseException ex) {
                                                Master.mensagem("Erro:\n" + ex);
                                            }


                                        }
                                    } else {
                                        //entra no incremento da completitude

                                        completitude++;

                                    }
                                } else {
                                    //CALCULA A TAXA ESTADUAL
                                    if (classificacaoEtiologica != null) {
                                        if (classificacaoFinal == 1 && classificacaoEtiologica.equals("02")) {
                                            try {
                                                if (isBetweenDates(utilDbf.getDate(rowObjects, "DT_NOTIFIC"), dataInicio, dataFim)) {
                                                    //denominadorEstadual
                                                    denominadorEstadual++;
                                                    if (soroHBSAG == 1) {
                                                        numeradorEstadual++;
                                                    } else {
                                                        if ((soroHBSAG == 1 || soroHBSAG == 2) && ANTIHBCIGM == 1) {
                                                            numeradorEstadual++;
                                                        }
                                                    }
                                                }
                                            } catch (ParseException ex) {
                                                System.out.println(ex);
                                            }

                                        }
                                    }
//                                    else {
//                                        //entra no incremento da completitude
//                                        completitude++;
//                                    }
                                }

                            }
                        }
                        float percentual = Float.parseFloat(String.valueOf(i)) / Float.parseFloat(String.valueOf(TotalRegistros)) * 100;
                        getBarraStatus().setValue((int) percentual);
                        i++;
                    }

                } catch (DBFException ex) {
                    Master.mensagem("Erro:\n" + ex);
                    System.out.println(ex);
                }
                total = df.format(Double.parseDouble(String.valueOf(numeradorEstadual)) / Double.parseDouble(String.valueOf(denominadorEstadual)) * 100);
                setTaxaEstadual(total + " (Numerador:" + String.valueOf(numeradorEstadual) + " / Denominador: " + String.valueOf(denominadorEstadual) + ")");
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
                    //calcula o percentual da completitude
                    setPercentualCompletitude(df.format(Double.parseDouble(String.valueOf(completitude)) / Double.parseDouble(String.valueOf(denominadorEspecifico)) * 100));
                    d1.setTaxa(total);
                } else {
                    d1.setNumerador("0");
                    d1.setDenominador("0");
                    d1.setTaxa("0.00");
                    setPercentualCompletitude("0.00");
                }
                this.setBeans(new ArrayList());
                this.getBeans().add(d1);
            }
        }
    }

    @Override
    public String[] getOrdemColunas() {
        return new String[]{"ID_LOCRES", "DS_LOCRES", "ID_UFRES", "N_SOROHEP", "D_SOROHEP", "I_SOROHEP", "ANO_NOTIF", "DT_NOTINI", "DT_NOTFIN","ORIGEM"};
    }
    @Override
    public HashMap<String, ColunasDbf> getColunas() {
        HashMap<String, ColunasDbf> hashColunas = new HashMap<String, ColunasDbf>();
        hashColunas.put("ID_LOCRES", new ColunasDbf(7));
        hashColunas.put("DS_LOCRES",new ColunasDbf( 30));
        hashColunas.put("ID_UFRES", new ColunasDbf(2));
        hashColunas.put("N_SOROHEP",new ColunasDbf( 10,0));
        hashColunas.put("D_SOROHEP", new ColunasDbf(10,0));
        hashColunas.put("I_SOROHEP", new ColunasDbf(6,2));
        hashColunas.put("ANO_NOTIF", new ColunasDbf(4,0));
        hashColunas.put("DT_NOTINI", new ColunasDbf(10));
        hashColunas.put("DT_NOTFIN", new ColunasDbf(10));
        hashColunas.put("ORIGEM", new ColunasDbf(30));
        this.setColunas(hashColunas);
        return hashColunas;
    }
    static String ANO;

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
        parametros.put("parTitulo1", "Proporção de casos de hepatites B confirmados por sorologia");
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
            rowData[9] = "HEPATITE-SINANNET";

            writer.addRecord(rowData);
        }
        return writer;
    }

    @Override
    public String getCaminhoJasper() {
        return "/com/org/relatorios/agravo1.jasper";
    }

    
}
