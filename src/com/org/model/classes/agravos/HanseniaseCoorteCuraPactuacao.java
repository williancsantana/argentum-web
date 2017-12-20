package com.org.model.classes.agravos;

import com.linuxense.javadbf.DBFException;
import com.linuxense.javadbf.DBFReader;
import com.linuxense.javadbf.DBFWriter;
import com.org.bd.DBFUtil;
import com.org.model.classes.Agravo;
import com.org.model.classes.ColunasDbf;
import com.org.negocio.Configuracao;
import com.org.negocio.Util;
import com.org.util.SinanDateUtil;
import com.org.view.Master;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.ComparatorChain;

/**
 *
 * @author joao
 */
public class HanseniaseCoorteCuraPactuacao extends Agravo {

    static String ANO;
    private String cura;
    private String subTotal;
    private String total;
    private String perCura;
    static String dtPbInicial;
    static String dtPbFinal;
    static String dtMbInicial;
    static String dtMbFinal;

    public HanseniaseCoorteCuraPactuacao(boolean isDbf) {
        this.setDBF(isDbf);
        setPeriodo("de Diagnóstico");
        setTipoAgregacao("de Residência");
        init("postgres");
    }

    @Override
    public void init(String tipoBanco) {
        this.setMultiplicador(100);
        this.setTipo("hans");
        this.setTitulo1("Proporção de cura dos casos novos de hanseníase diagnosticados nos anos das coortes");
        this.setRodape("OBS. sobre método de cálculo: \nSeleção das coortes: "
                + "Casos novos (modo de entrada = 1-Caso novo) paucibacilares "
                + "(classificação operacional atual) diagnosticados no ano anterior ao ano "
                + "de avaliação e casos novos multibacilares diagnosticados 2 anos anteriores"
                + " ao ano de avaliação. No relatório são somados os PB e os MB selecionados na"
                + " base de dados do Sinan, segundo local de residência atual.\nCálculo do "
                + "percentual de cura: não são considerados no cálculo desses indicadores na avaliação municipal:"
                + "\ntranferência para outro município, transferência para outro estado,"
                + "tranferência para outro país e erro de diagnóstico e transferência não especificada."
                + "\nNão são consierados no cálculo desse indicador na avaliação estadual: transferência para outro estado,"
                + "tranferência para outro país e erro de diagnóstico e transferência não especificada.");
        this.setCura("0");
        this.setSubTotal("0");
        this.setTotal("0");
        this.setTransfNaoEspecificada("0");
        this.setSqlNumeradorCompletitude("");
    }

    private void setStatusBarra(int indexDoRegistroEmLeitura, double TotalRegistros) {
        float percentual = Float.parseFloat(String.valueOf(indexDoRegistroEmLeitura)) / Float.parseFloat(String.valueOf(TotalRegistros)) * 100;
        getBarraStatus().setValue((int) percentual);
    }

    private void calculaRegiao(DBFReader reader, Map parametros) {
        try {
            //buscar os municipios que vao para o resultado
            HashMap<String, Agravo> municipiosBeans = new HashMap<String, Agravo>();
            DBFUtil utilDbf = new DBFUtil();
            String ufResidencia = (String) parametros.get("parUf");
            String sgUfResidencia = (String) parametros.get("parSgUf");
            String codRegional = (String) parametros.get("parCodRegional");
            String codRegiao = (String) parametros.get("parCodRegiaoSaude");
            Object[] rowObjects;
            DecimalFormat df = new DecimalFormat("0.00");
            Agravo regiaoNotificacao;
            String[] arquivos = parametros.get("parArquivos").toString().split("\\|\\|");

            String esquemaDeTratamentoAtual;

            String dataInicioPB = (String) parametros.get("parDataInicioCoortePB");
            String dataFimPB = (String) parametros.get("parDataFimCoortePB");
            String dataInicioMB = (String) parametros.get("parDataInicioCoorteMB");
            String dataFimMB = (String) parametros.get("parDataFimCoorteMB");

            String modoEntrada;
            Date dtDiagnostico;
            String classificacaoOperacionalAtual;

            if (codRegional == null) {
                codRegional = "";
            }
            if ((Boolean) parametros.get("parIsRegiao")) {
                municipiosBeans = populaRegiaoBeans(sgUfResidencia, codRegiao);
            } else {
                municipiosBeans = populaRegionalBeans(sgUfResidencia, codRegional);
            }
            //loop para ler os arquivos selecionados
            for (String arquivo : arquivos) {
                int indexDoRegistroEmLeitura = 1;
                try {
                    reader = Util.retornaObjetoDbfCaminhoArquivo(arquivo.substring(0, arquivo.length() - 4), Configuracao.getPropriedade("caminho"));
                    utilDbf.mapearPosicoes(reader);
                    double TotalRegistros = Double.parseDouble(String.valueOf(reader.getRecordCount()));

                    while ((rowObjects = reader.nextRecord()) != null) {
                        //verifica a uf de residencia
                        if (utilDbf.getString(rowObjects, "UFRESAT") != null) {
                            //verifica se existe a referencia do municipio no bean
                            regiaoNotificacao = getMunicipioPelaRegiaoOuRegional(parametros, municipiosBeans, utilDbf, rowObjects);
                            if (regiaoNotificacao != null) {
                                regiaoNotificacao.setTaxa("0");
                            }

                            modoEntrada = utilDbf.getString(rowObjects, "MODOENTR", 1);
                            classificacaoOperacionalAtual = utilDbf.getString(rowObjects, "CLASSATUAL", 1);
                            dtDiagnostico = utilDbf.getDate(rowObjects, "DT_DIAG");
                            esquemaDeTratamentoAtual = utilDbf.getString(rowObjects, "ESQ_ATU_N");
                            esquemaDeTratamentoAtual = esquemaDeTratamentoAtual != null ? esquemaDeTratamentoAtual : "0";

                            if (regiaoNotificacao != null) {
                                if (modoEntrada.equals("1")) {
                                    if (isBetweenDates(dtDiagnostico, dataInicioPB, dataFimPB)
                                            && classificacaoOperacionalAtual.equals("1")
                                            && esquemaDeTratamentoAtual.equals("1")) {
                                        validaCriterios(rowObjects, utilDbf, classificacaoOperacionalAtual, esquemaDeTratamentoAtual, regiaoNotificacao);
                                    } else if (isBetweenDates(dtDiagnostico, dataInicioMB, dataFimMB)
                                            && classificacaoOperacionalAtual.equals("2")
                                            && esquemaDeTratamentoAtual.equals("2")) {
                                        validaCriterios(rowObjects, utilDbf, classificacaoOperacionalAtual, esquemaDeTratamentoAtual, regiaoNotificacao);
                                    }
                                }
                            }
                        }
                        setStatusBarra(indexDoRegistroEmLeitura, TotalRegistros);
                        indexDoRegistroEmLeitura++;
                    }
                } catch (DBFException ex) {
                    Master.mensagem("Erro:\n" + ex);
                }
            }

            setTaxaEstadual("");
            //CALCULA A TAXA PARA TODOS OS MUNICIPIOS
            this.setBeans(new ArrayList());
            Collection<Agravo> municipioBean = municipiosBeans.values();

            for (Agravo agravoDBF : municipioBean) {
                if (!agravoDBF.getNumerador().equals("0") && !agravoDBF.getDenominador().equals("0")) {
                    agravoDBF.setTaxa(df.format(
                            (Double.parseDouble(agravoDBF.getNumerador()) / Double.parseDouble(agravoDBF.getDenominador())) * 100));
                } else if (null == agravoDBF.getTaxa()) {
                    agravoDBF.setTaxa("0");
                }
                this.getBeans().add(agravoDBF);
                getBarraStatus().setString("Calculando indicador para: " + agravoDBF.getNomeMunicipio());
            }
            getBarraStatus().setString(null);
            ComparatorChain chain;
            chain = new ComparatorChain(Arrays.asList(
                    new BeanComparator("uf"),
                    new BeanComparator("nomeMunicipio")));
            Collections.sort(this.getBeans(), chain);
            //Collections.sort(this.getBeans(), new BeanComparator("nomeMunicipio"));
            //calcular o total
            Agravo total = new Agravo();
            if ((Boolean) parametros.get("parIsRegiao")) {
                total = adicionaTotal(municipioBean, codRegiao);
                adicionaParamentrosTotais(parametros, total);
            } else {
                total = adicionaTotal(municipioBean, codRegional);
                adicionaParamentrosTotais(parametros, total);
            }

            Agravo finalAgravo = new Agravo();
            finalAgravo.setNomeMunicipio("TOTAL");
            this.getBeans().add(finalAgravo);
            setParametroRegiaoOuRegional(parametros);

        } catch (ParseException ex) {
            ex.printStackTrace();
        }
    }

    private void setParametroRegiaoOuRegional(Map parametros) {
        if ((Boolean) parametros.get("parIsRegiao")) {
            parametros.put("parDescricaoRegional", "Região de residência atual:");
        } else {
            parametros.put("parDescricaoRegional", "Regional de residência atual:");
        }
    }

    private void validaCriterios(Object[] rowObjects, DBFUtil utilDbf, String classificacaoOperacionalAtual,
            String esquemaDeTratamentoAtual, Agravo municipioResidencia) {
        if ((classificacaoOperacionalAtual.equals("1") || classificacaoOperacionalAtual.equals("2"))
                && (esquemaDeTratamentoAtual.equals("1") || esquemaDeTratamentoAtual.equals("2"))) {
            //busca o tipo de alta
            String tipoAlta = utilDbf.getString(rowObjects, "TPALTA_N", 1);
            tipoAlta = tipoAlta != null ? tipoAlta : "";
            municipioResidencia = classificaAlta(tipoAlta, municipioResidencia);
        }
    }

    private Agravo classificaAlta(String tipoAlta, Agravo beanMunicipioResidencia) {
        DecimalFormat df = new DecimalFormat("0.00");
        if (tipoAlta.equals("1") || tipoAlta.equals("2") || tipoAlta.equals("3") || tipoAlta.equals("6")
                || tipoAlta.equals("7") || tipoAlta.equals("")) {
            beanMunicipioResidencia.setDenominador(String.valueOf(1 + Integer.parseInt(beanMunicipioResidencia.getDenominador())));
        }
        if (tipoAlta.equals("1")) {
            beanMunicipioResidencia.setNumerador(String.valueOf(1 + Integer.parseInt(beanMunicipioResidencia.getNumerador())));
        }
        calcularTaxaIndividual(df, beanMunicipioResidencia);
        return beanMunicipioResidencia;
    }

    private Agravo getMunicipioPelaRegiaoOuRegional(Map parametros, HashMap<String, Agravo> municipiosBeans, DBFUtil utilDbf, Object[] rowObjects) {
        Agravo regiaoResidencia = new Agravo();
        try {
            if ((Boolean) parametros.get("parIsRegiao")) {
                regiaoResidencia = municipiosBeans.get(buscaIdRegiaoSaude(utilDbf.getString(rowObjects, "MUNIRESAT")));
            } else {
                regiaoResidencia = municipiosBeans.get(buscaIdRegionalSaude(utilDbf.getString(rowObjects, "MUNIRESAT")));
            }
        } catch (SQLException ex) {
            Logger.getLogger(ViolenciaAgravo.class.getName()).log(Level.SEVERE, null, ex);
        }
        return regiaoResidencia;
    }

    /**
     * Calcula os numeradores, denominadores e indicadores filtrados por região
     * ou regional
     *
     * @param reader
     * @param parametros
     * @throws ParseException
     */
    private void calculaMunicipios(DBFReader reader, Map parametros) {
        try {
            HashMap<String, Agravo> municipiosBeans = new HashMap<String, Agravo>();
            DBFUtil utilDbf = new DBFUtil();
            Object[] rowObjects;
            Agravo municipioNotificacao;
            String ufResidencia = (String) parametros.get("parUf");
            String sgUfResidencia = (String) parametros.get("parSgUf");
            String codRegional = (String) parametros.get("parCodRegional");
            String codRegiao = (String) parametros.get("parCodRegiaoSaude");
            String[] arquivos = parametros.get("parArquivos").toString().split("\\|\\|");
            String esquemaDeTratamentoAtual;
            String dataInicioPB = (String) parametros.get("parDataInicioCoortePB");
            String dataFimPB = (String) parametros.get("parDataFimCoortePB");
            String dataInicioMB = (String) parametros.get("parDataInicioCoorteMB");
            String dataFimMB = (String) parametros.get("parDataFimCoorteMB");
            String modoEntrada;
            String classificacaoOperacionalAtual;
            Date dtDiagnostico;
            DecimalFormat df = new DecimalFormat("0");
            DecimalFormat df2 = new DecimalFormat("0.00");
            Agravo municipioResidencia;

            if (codRegional == null) {
                codRegional = "";
            }
            if ((Boolean) parametros.get("parIsRegiao")) {
                municipiosBeans = populaMunicipiosBeansMAL(sgUfResidencia, codRegiao, parametros.get("parIsRegiao").toString());
            } else {
                municipiosBeans = populaMunicipiosBeansMAL(sgUfResidencia, codRegional, parametros.get("parIsRegiao").toString());
            }

            //loop para ler os arquivos selecionados
            for (String arquivo : arquivos) {
                int indexDoRegistroEmLeitura = 1;
                try {
                    reader = Util.retornaObjetoDbfCaminhoArquivo(arquivo.substring(0, arquivo.length() - 4), Configuracao.getPropriedade("caminho"));
                    utilDbf.mapearPosicoes(reader);
                    double TotalRegistros = Double.parseDouble(String.valueOf(reader.getRecordCount()));

                    while ((rowObjects = reader.nextRecord()) != null) {
                        //verifica a uf de residencia
                        if (utilDbf.getString(rowObjects, "UFRESAT") != null) {
                            //verifica se existe a referencia do municipio no bean
                            municipioResidencia = municipiosBeans.get(utilDbf.getString(rowObjects, "MUNIRESAT"));
                            if (municipioResidencia != null) {
                                municipioResidencia.setTaxa("0");
                            }

                            modoEntrada = utilDbf.getString(rowObjects, "MODOENTR", 1);
                            classificacaoOperacionalAtual = utilDbf.getString(rowObjects, "CLASSATUAL", 1);
                            dtDiagnostico = utilDbf.getDate(rowObjects, "DT_DIAG");
                            esquemaDeTratamentoAtual = utilDbf.getString(rowObjects, "ESQ_ATU_N");
                            esquemaDeTratamentoAtual = esquemaDeTratamentoAtual != null ? esquemaDeTratamentoAtual : "0";

                            if (municipioResidencia != null) {
                                if (modoEntrada.equals("1")) {
                                    if (isBetweenDates(dtDiagnostico, dataInicioPB, dataFimPB)
                                            && classificacaoOperacionalAtual.equals("1")
                                            && esquemaDeTratamentoAtual.equals("1")) {
                                        validaCriterios(rowObjects, utilDbf, classificacaoOperacionalAtual, esquemaDeTratamentoAtual, municipioResidencia);
                                    } else if (isBetweenDates(dtDiagnostico, dataInicioMB, dataFimMB)
                                            && classificacaoOperacionalAtual.equals("2")
                                            && esquemaDeTratamentoAtual.equals("2")) {
                                        validaCriterios(rowObjects, utilDbf, classificacaoOperacionalAtual, esquemaDeTratamentoAtual, municipioResidencia);
                                    }
                                }
                            }
                        }
                        setStatusBarra(indexDoRegistroEmLeitura, TotalRegistros);
                        indexDoRegistroEmLeitura++;
                    }
                } catch (DBFException ex) {
                    Master.mensagem("Erro:\n" + ex);
                }
            }

            //CALCULA A TAXA PARA TODOS OS MUNICIPIOS
            this.setBeans(new ArrayList());
            Collection<Agravo> municipioBean = municipiosBeans.values();

            for (Agravo agravoDBF : municipioBean) {
                if (!agravoDBF.getNumerador().equals("0") && !agravoDBF.getDenominador().equals("0")) {
                    agravoDBF.setTaxa(df2.format(
                            (Double.parseDouble(agravoDBF.getNumerador()) / Double.parseDouble(agravoDBF.getDenominador())) * 100));
                } else if (null == agravoDBF.getTaxa()) {
                    agravoDBF.setTaxa("0");
                }
                this.getBeans().add(agravoDBF);
                getBarraStatus().setString("Calculando indicador para: " + agravoDBF.getNomeMunicipio());
            }
            getBarraStatus().setString(null);
            ComparatorChain chain;

            if ((Boolean) parametros.get("parIsRegiao")) {
                chain = new ComparatorChain(Arrays.asList(
                        new BeanComparator("uf"),
                        new BeanComparator("regiaoSaude"),
                        new BeanComparator("nomeMunicipio")));
            } else {
                chain = new ComparatorChain(Arrays.asList(
                        new BeanComparator("uf"),
                        new BeanComparator("regional"),
                        new BeanComparator("nomeMunicipio")));
            }

            Collections.sort(this.getBeans(), chain);
            Agravo totalAgravo = new Agravo();
            //calcular o total
            if ((Boolean) parametros.get("parIsRegiao")) {
                totalAgravo = adicionaTotal(municipioBean, codRegiao);
                adicionaParamentrosTotais(parametros, totalAgravo);
            } else {
                totalAgravo = adicionaTotal(municipioBean, codRegional);
                adicionaParamentrosTotais(parametros, totalAgravo);
            }

            Agravo finalAgravo = new Agravo();
            finalAgravo.setNomeMunicipio("TOTAL");
            this.getBeans().add(finalAgravo);
            setParametroRegiaoOuRegional(parametros);

        } catch (ParseException ex) {
            ex.printStackTrace();
        }
    }

    private void adicionaParamentrosTotais(Map parametros, Agravo totalAgravo) {
        DecimalFormat df = new DecimalFormat("0.00");
        parametros.put("numeradorTotal", totalAgravo.getNumerador());
        parametros.put("denominadorTotal", totalAgravo.getDenominador());
        Double numerador = Double.parseDouble(totalAgravo.getNumerador());
        Double denominador = Double.parseDouble(totalAgravo.getDenominador());
        double taxa;

        if (numerador == 0l && denominador == 0l) {
            taxa = 0l;
        } else {
            taxa = (Double.parseDouble(totalAgravo.getNumerador()) / Double.parseDouble(totalAgravo.getDenominador())) * 100;
        }

        parametros.put("taxaTotal", df.format(taxa));
    }

    private Agravo calcularTaxaTotal(DecimalFormat df) {
        Agravo agravo = (Agravo) this.getBeans().get(this.getBeans().size() - 1);
        if ((null != agravo.getNumerador() && Double.parseDouble(agravo.getNumerador()) != 0l)
                && (null != agravo.getDenominador() && Double.parseDouble(agravo.getDenominador()) != 0l)) {
            agravo.setTaxa(df.format(
                    (Double.parseDouble(agravo.getNumerador()) / (Double.parseDouble(agravo.getDenominador())) * 100)));
        } else {
            agravo.setTaxa("0");
        }
        return agravo;
    }

    private void calcularTaxaIndividual(DecimalFormat df, Agravo municipioResidencia) {
        if ((null != municipioResidencia.getNumerador() && Double.parseDouble(municipioResidencia.getNumerador()) != 0)
                && (null != municipioResidencia.getDenominador() && Double.parseDouble(municipioResidencia.getDenominador()) != 0)) {

            municipioResidencia.setTaxa(df.format(
                    Double.parseDouble(municipioResidencia.getNumerador()) / Double.parseDouble(municipioResidencia.getDenominador()) * 100));

        } else {
            municipioResidencia.setTaxa("0");
        }
    }

    /**
     * Calcula total, denominador, numerador do municipio específico.
     *
     * @param reader
     * @param parametros
     */
    @Override
    public void calcula(DBFReader reader, Map parametros) {
        String municipios = (String) parametros.get("municipios");
        String municipioEspecifico = (String) parametros.get("municipioEspecifico");
        String filtroUF = (String) parametros.get("parUf");
        boolean isRegiaoSelecionada = (boolean) parametros.get("parIsRegiao");
        boolean isRegionalSelecionada = (boolean) parametros.get("parIsRegional");
        boolean estadoEMunicipio = municipios.equals("sim") && !filtroUF.equals("brasil");

        setDtPbInicial((String) parametros.get("parDataInicioCoortePB"));
        setDtPbFinal((String) parametros.get("parDataFimCoortePB"));
        setDtMbInicial((String) parametros.get("parDataInicioCoorteMB"));
        setDtMbFinal((String) parametros.get("parDataFimCoorteMB"));

        //filtro municipios = TODOS e 7UF selecionado algum estado
        if ((isRegionalSelecionada || isRegiaoSelecionada) && municipioEspecifico == "NENHUM") {
            calculaRegiao(reader, parametros);
        } else if (municipios.equals("sim")) {
            calculaMunicipios(reader, parametros);
        } else {
            calculaMunicipioIndividual(municipios, filtroUF, reader, parametros);
        }
    }

    private void calculaMunicipioIndividual(String municipios, String filtroUF, DBFReader reader, Map parametros) {
        try {
            Object[] rowObjects;
            DBFUtil utilDbf = new DBFUtil();
            String total;
            DecimalFormat df = new DecimalFormat("0.00");
            String ufResidencia = (String) parametros.get("parUf");
            String municipioResidencia = (String) parametros.get("parMunicipio");
            String[] arquivos = parametros.get("parArquivos").toString().split("\\|\\|");
            String esquemaDeTratamentoAtual;
            String dataInicioPB = (String) parametros.get("parDataInicioCoortePB");
            String dataFimPB = (String) parametros.get("parDataFimCoortePB");
            String dataInicioMB = (String) parametros.get("parDataInicioCoorteMB");
            String dataFimMB = (String) parametros.get("parDataFimCoorteMB");
            String modoEntrada;
            String classificacaoOperacionalAtual;
            Date dtDiagnostico;

            if (municipioResidencia == null) {
                municipioResidencia = "";
            }

            //loop para ler os arquivos selecionados
            for (String arquivo : arquivos) {
                int indexDoRegistroEmLeitura = 1;
                reader = Util.retornaObjetoDbfCaminhoArquivo(arquivo.substring(0, arquivo.length() - 4), Configuracao.getPropriedade("caminho"));
                utilDbf.mapearPosicoes(reader);
                double TotalRegistros = Double.parseDouble(String.valueOf(reader.getRecordCount()));
                while ((rowObjects = reader.nextRecord()) != null) {
                    //verifica a uf de residencia
                    if (utilDbf.getString(rowObjects, "UFRESAT") != null
                            && utilDbf.getString(rowObjects, "UFRESAT").equals(ufResidencia)) {

                        modoEntrada = utilDbf.getString(rowObjects, "MODOENTR", 1);
                        classificacaoOperacionalAtual = utilDbf.getString(rowObjects, "CLASSATUAL", 1);
                        dtDiagnostico = utilDbf.getDate(rowObjects, "DT_DIAG");
                        esquemaDeTratamentoAtual = utilDbf.getString(rowObjects, "ESQ_ATU_N");
                        esquemaDeTratamentoAtual = esquemaDeTratamentoAtual != null ? esquemaDeTratamentoAtual : "0";

                        if (verificaMunicipio(municipioResidencia, utilDbf.getString(rowObjects, "MUNIRESAT"))) {
                            if (modoEntrada.equals("1")) {
                                if (isBetweenDates(dtDiagnostico, dataInicioPB, dataFimPB)
                                        && classificacaoOperacionalAtual.equals("1")
                                        && esquemaDeTratamentoAtual.equals("1")) {
                                    validaCriteriosSemReferenciaDeMunicipio(rowObjects, utilDbf, classificacaoOperacionalAtual, esquemaDeTratamentoAtual, this);
                                } else if (isBetweenDates(dtDiagnostico, dataInicioMB, dataFimMB)
                                        && classificacaoOperacionalAtual.equals("2")
                                        && esquemaDeTratamentoAtual.equals("2")) {
                                    validaCriteriosSemReferenciaDeMunicipio(rowObjects, utilDbf, classificacaoOperacionalAtual, esquemaDeTratamentoAtual, this);
                                }
                            }
                        }
                    }
                    setStatusBarra(indexDoRegistroEmLeitura, TotalRegistros);
                    indexDoRegistroEmLeitura++;
                }
            }
            Agravo hanseniaseMunicipio = new Agravo();
            hanseniaseMunicipio.setCodMunicipio((String) parametros.get("parMunicipio"));
            hanseniaseMunicipio.setUf((String) parametros.get("parSgUf"));
            hanseniaseMunicipio.setNomeMunicipio((String) parametros.get("parNomeMunicipio"));
            hanseniaseMunicipio.setCodMunicipio(municipioResidencia);

            if (!String.valueOf(this.getTotal()).equals("0.0")) {
                total = "";
                preencheResultadoDaBusca(hanseniaseMunicipio, Integer.parseInt(this.getCura()), Integer.parseInt(this.getSubTotal()), total, df);
            } else {
                hanseniaseMunicipio.setNumerador("0");
                hanseniaseMunicipio.setDenominador("0");
                hanseniaseMunicipio.setTaxa("0.00");
            }
            this.setBeans(new ArrayList());
            this.getBeans().add(hanseniaseMunicipio);
            setParametroRegiaoOuRegional(parametros);

        } catch (NumberFormatException | ParseException | DBFException ex) {
            ex.printStackTrace();
        }
    }

    private void validaCriteriosSemReferenciaDeMunicipio(Object[] rowObjects, DBFUtil utilDbf, String classificacaoOperacionalAtual,
            String esquemaDeTratamentoAtual, HanseniaseCoorteCuraPactuacao municipioResidencia) {
        if ((classificacaoOperacionalAtual.equals("1") || classificacaoOperacionalAtual.equals("2"))
                && (esquemaDeTratamentoAtual.equals("1") || esquemaDeTratamentoAtual.equals("2"))) {
            //busca o tipo de alta
            String tipoAlta = utilDbf.getString(rowObjects, "TPALTA_N", 1);
            tipoAlta = tipoAlta != null ? tipoAlta : "";
            classificaAlta(tipoAlta);
        }
    }

    private void classificaAlta(String tipoAlta) {
        if (tipoAlta.equals("1") || tipoAlta.equals("2") || tipoAlta.equals("6")
                || tipoAlta.equals("7") || tipoAlta.equals("")) {
            this.setSubTotal(String.valueOf(1 + Integer.parseInt(getSubTotal())));
        }
        if (tipoAlta.equals("1")) {
            this.setCura(String.valueOf(1 + Integer.parseInt(getCura())));
        }
        this.setSubTotal(String.valueOf(Integer.parseInt(getSubTotal())));
    }

    private void preencheResultadoDaBusca(Agravo hanseniaseMunicipio, int numerador, int denominador, String total, DecimalFormat df) {
        hanseniaseMunicipio.setNumerador(String.valueOf(NumberFormat.getNumberInstance().format(Double.parseDouble(String.valueOf(numerador)))));
        hanseniaseMunicipio.setDenominador(String.valueOf(NumberFormat.getNumberInstance().format(Double.parseDouble(String.valueOf(denominador)))));
        if (numerador != 0 && denominador != 0) {
            total = df.format(Double.parseDouble(String.valueOf(numerador)) / Double.parseDouble(String.valueOf(denominador)) * 100);
        } else {
            total = "0.00";
        }
        hanseniaseMunicipio.setTaxa(total);
    }

    @Override
    public Map getParametros() {
        Map parametros = new HashMap();
        parametros.put("parDataInicio", Util.formataData(this.getDataInicio()));
        parametros.put("parDataFim", Util.formataData(this.getDataFim()));

        parametros.put("parDataInicioCoortePB", Util.formataData(SinanDateUtil.subtrairAno(this.getDataInicio(), -1)));
        parametros.put("parDataFimCoortePB", Util.formataData(SinanDateUtil.subtrairAno(this.getDataFim(), -1)));
        parametros.put("parPeriodoCoortePB", SinanDateUtil.subtrairAno(this.getDataInicio(), -1) + " a " + SinanDateUtil.subtrairAno(this.getDataFim(), -1) + " (PB)");

        parametros.put("parDataInicioCoorteMB", Util.formataData(SinanDateUtil.subtrairAno(this.getDataInicio(), -2)));
        parametros.put("parDataFimCoorteMB", Util.formataData(SinanDateUtil.subtrairAno(this.getDataFim(), -2)));
        parametros.put("parPeriodoCoorteMB", SinanDateUtil.subtrairAno(this.getDataInicio(), -2) + " a " + SinanDateUtil.subtrairAno(this.getDataFim(), -2) + " (MB)");
        parametros.put("parPeriodoAvaliacao", this.getDataInicio() + " a " + this.getDataFim());

        parametros.put("parPeriodo", "de " + this.getDataInicio() + " a " + this.getDataFim());
        parametros.put("parTituloColuna", this.getTituloColuna());
        parametros.put("parFator", String.valueOf(this.getMultiplicador()));
        parametros.put("parAno", Util.getAno(this.getDataFim()));
        parametros.put("parRodape", this.getRodape());
        parametros.put("parConfig", "");
        parametros.put("parTitulo1", "Proporção de notificações de violência interpessoal e autoprovocada com o campo raça/cor preenchido com informação válida.");
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
    public String[] getOrdemColunas() {
        return new String[]{"ID_LOCRES", "DS_LOCRES", "COUUFRESAT", "N_CURHANS", "D_SUBHANS", "I_CURHANS",
            "TOTAL_NOT", "DT_DPBINI", "DT_DPBFIN", "DT_DMBINI", "DT_DMBFIN", "ORIGEM"};
    }

    @Override
    public HashMap<String, ColunasDbf> getColunas() {
        HashMap<String, ColunasDbf> hashColunas = new HashMap<String, ColunasDbf>();
        hashColunas.put("ID_LOCRES", new ColunasDbf(7));
        hashColunas.put("DS_LOCRES", new ColunasDbf(30));
        hashColunas.put("COUUFRESAT", new ColunasDbf(2));
        hashColunas.put("N_CURHANS", new ColunasDbf(10, 0));
        hashColunas.put("D_SUBHANS", new ColunasDbf(4, 0));
        hashColunas.put("I_CURHANS", new ColunasDbf(4, 2));
        hashColunas.put("TOTAL_NOT", new ColunasDbf(4, 0));
        hashColunas.put("ORIGEM", new ColunasDbf(30));
        hashColunas.put("DT_DPBINI", new ColunasDbf(10));
        hashColunas.put("DT_DPBFIN", new ColunasDbf(10));
        hashColunas.put("DT_DMBINI", new ColunasDbf(10));
        hashColunas.put("DT_DMBFIN", new ColunasDbf(10));
        this.setColunas(hashColunas);
        return hashColunas;
    }

    @Override
    public DBFWriter getLinhas(HashMap<String, ColunasDbf> colunas, List bean, DBFWriter writer) throws DBFException, IOException {
        for (int i = 0; i < bean.size(); i++) {
            Object rowData[] = new Object[colunas.size()];
            Agravo agravo = (Agravo) bean.get(i);
            if (agravo.getCodMunicipio() != null && !agravo.getCodMunicipio().equals("")) {
                if (agravo.getNomeMunicipio().equals("BRASIL")) {
                    rowData[0] = null;
                    rowData[2] = null;
                } else {
                    if (agravo.getCodMunicipio() != null) {
                        rowData[0] = agravo.getCodMunicipio();
                        rowData[2] = agravo.getCodMunicipio().substring(0, 2);
                    } else {
                        rowData[0] = "";
                        rowData[2] = "";
                    }
                }
                rowData[1] = agravo.getNomeMunicipio();
                rowData[3] = Double.parseDouble(agravo.getNumerador().replace(",", "."));
                rowData[5] = Double.parseDouble(agravo.getTaxa().replace(",", "."));
                rowData[4] = Double.parseDouble(agravo.getDenominador().replace(",", "."));
                rowData[6] = Double.parseDouble(agravo.getDenominador().replace(",", "."));
                rowData[7] = getDtPbInicial();
                rowData[8] = getDtPbFinal();
                rowData[9] = getDtMbInicial();
                rowData[10] = getDtMbFinal();
                rowData[11] = "HANSENIASE-SINANNET";

                writer.addRecord(rowData);
            }
        }
        return writer;
    }

    @Override
    public String getCaminhoJasper() {
        return "/com/org/relatorios/hanseniaseCoorteCuraPactuacao.jasper";
    }

    public String getCura() {
        return cura;
    }

    public void setCura(String cura) {
        this.cura = cura;
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

    public String getPerCura() {
        return perCura;
    }

    public void setPerCura(String perCura) {
        this.perCura = perCura;
    }

    public static String getDtPbInicial() {
        return dtPbInicial;
    }

    public static void setDtPbInicial(String dtPbInicial) {
        HanseniaseCoorteCuraPactuacao.dtPbInicial = dtPbInicial;
    }

    public static String getDtPbFinal() {
        return dtPbFinal;
    }

    public static void setDtPbFinal(String dtPbFinal) {
        HanseniaseCoorteCuraPactuacao.dtPbFinal = dtPbFinal;
    }

    public static String getDtMbInicial() {
        return dtMbInicial;
    }

    public static void setDtMbInicial(String dtMbInicial) {
        HanseniaseCoorteCuraPactuacao.dtMbInicial = dtMbInicial;
    }

    public static String getDtMbFinal() {
        return dtMbFinal;
    }

    public static void setDtMbFinal(String dtMbFinal) {
        HanseniaseCoorteCuraPactuacao.dtMbFinal = dtMbFinal;
    }

}
