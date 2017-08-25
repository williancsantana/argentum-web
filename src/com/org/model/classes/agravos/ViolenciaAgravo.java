package com.org.model.classes.agravos;

import com.linuxense.javadbf.DBFException;
import com.linuxense.javadbf.DBFReader;
import com.linuxense.javadbf.DBFWriter;
import com.org.bd.DBFUtil;
import com.org.model.classes.Agravo;
import com.org.model.classes.ColunasDbf;
import com.org.negocio.Configuracao;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.beanutils.BeanComparator;

/**
 *
 * @author joao
 */
public class ViolenciaAgravo extends Agravo {
    
    static String ANO;
    private final int BRANCO = 1;
    private final int PRETA = 2;
    private final int AMARELO = 3;
    private final int PARDO = 4;
    private final int INDIGENA = 5;

    public ViolenciaAgravo(boolean isDbf) {
        this.setDBF(isDbf);
        setPeriodo("de Diagnóstico");
        setTipoAgregacao("de Residência");
        init("postgres");
    }

    @Override
    public void init(String tipoBanco) {
        this.setArquivo("VIOLENET");
        this.setTextoCompletitude("");
        this.setMultiplicador(100);
        this.setTipo("");
        this.setTipo("populacao");
        this.setTitulo1("Proporção de notificações de violência interpessoal e autoprovocada com o campo raça/cor preenchido com informação válida.");
        this.setTituloColuna("Indicador");
        this.setRodape("Indicador: Nº de casos de violencia registrados em  determinado ano, por local de residência  \n");
        this.setSqlNumeradorCompletitude("");
        if (!isDBF()) {
            this.setSqlNumeradorMunicipioEspecifico("select count(*) as numerador from dbsinan.tb_notificacao as t1, " + "dbsinan.tb_investiga_aids_crianca as t2 " + "where  t1.nu_notificacao=t2.nu_notificacao and " + "t1.dt_notificacao=t2.dt_notificacao and " + "t1.co_municipio_notificacao=t2.co_municipio_notificacao" + " and nu_idade < 4005 and tp_criterio_definicao not in (900,901) and (t1.dt_diagnostico_sintoma BETWEEN ?  " + "AND ?) and " + "t1.co_uf_residencia= ? and " + "t1.co_municipio_residencia = ?");
            this.setSqlDenominadorMunicipioEspecifico("select nu_pop1a4anos+nu_pop1ano as denominador from dblocalidade.tb_estatistica_ibge where co_uf_municipio_ibge = ? and nu_ano = ?");
            this.setSqlNumeradorEstado("select count(*) as numerador from dbsinan.tb_notificacao as t1, " + "dbsinan.tb_investiga_aids_crianca as t2 " + "where  t1.nu_notificacao=t2.nu_notificacao and " + "t1.dt_notificacao=t2.dt_notificacao and " + "t1.co_municipio_notificacao=t2.co_municipio_notificacao" + " and nu_idade < 4005 and tp_criterio_definicao not in (900,901) and (t1.dt_diagnostico_sintoma BETWEEN ?  " + "AND ?) and " + "t1.co_uf_residencia= ? ");

            this.setSqlDenominandorEstado(this.getSqlDenominadorMunicipioEspecifico());
            this.setSqlNumeradorBeanMunicipios(this.getSqlNumeradorMunicipioEspecifico());
            this.setSqlDenominadorBeanMunicipios(this.getSqlDenominadorMunicipioEspecifico());
        }
    }
    
    private void setStatusBarra(int indexDoRegistroEmLeitura, double TotalRegistros){
        float percentual = Float.parseFloat(String.valueOf(indexDoRegistroEmLeitura)) / Float.parseFloat(String.valueOf(TotalRegistros)) * 100;
        getBarraStatus().setValue((int) percentual);
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
        Date dataNotificacao;
        DecimalFormat df = new DecimalFormat("0.00");
        int completitude = 0, numeradorMunicipio = 0, numeradorBrasil = 0, denominadorMunicipio = 0, denominadorBrasil = 0, raca = 0;
        Agravo municipioResidencia;
        String racaCor;
        String dataInicio = (String) parametros.get("parDataInicio");
        String dataFim = (String) parametros.get("parDataFim");
        String[] arquivos = parametros.get("parArquivos").toString().split("\\|\\|");

        //loop para ler os arquivos selecionados
        for (String arquivo : arquivos) {
            int indexDoRegistroEmLeitura = 1;
            try {
                reader = Util.retornaObjetoDbfCaminhoArquivo(arquivo.substring(0, arquivo.length() - 4), Configuracao.getPropriedade("caminho"));
                utilDbf.mapearPosicoes(reader);
                double TotalRegistros = Double.parseDouble(String.valueOf(reader.getRecordCount()));
                
                while ((rowObjects = reader.nextRecord()) != null) {
                    //verifica a uf de residencia
                    if (utilDbf.getString(rowObjects, coluna) != null) {
                        //verifica se existe a referencia do municipio no bean
                        municipioResidencia = municipiosBeans.get(utilDbf.getString(rowObjects, coluna));
                        dataNotificacao = utilDbf.getDate(rowObjects, "DT_NOTIFIC");
                        racaCor = utilDbf.getString(rowObjects, "CS_RACA", 1);
                        raca = racaCor != null ? Integer.parseInt(racaCor) : 0;
                        
                        if (municipioResidencia != null && isBetweenDates(dataNotificacao, dataInicio, dataFim)){
                            if(raca >= BRANCO && raca <= INDIGENA) {
                                numeradorMunicipio = Integer.parseInt(municipioResidencia.getNumerador());
                                numeradorMunicipio++;
                                municipioResidencia.setNumerador(String.valueOf(numeradorMunicipio));
                            }
                            denominadorMunicipio = Integer.parseInt(municipioResidencia.getDenominador());
                            denominadorMunicipio++;
                            municipioResidencia.setDenominador(String.valueOf(denominadorMunicipio));
                            calcularTaxaIndividual(df, municipioResidencia);
                        }
                    }
                    setStatusBarra(indexDoRegistroEmLeitura, TotalRegistros);
                    indexDoRegistroEmLeitura++;
                }
            }catch (DBFException ex) {
                Master.mensagem("Erro:\n" + ex);
            }
        }
        
        setTaxaEstadual("");
        //CALCULA A TAXA PARA TODOS OS MUNICIPIOS
        this.setBeans(new ArrayList());
        Collection<Agravo> municipioBean = municipiosBeans.values();
        
        for (Iterator<Agravo> it = municipioBean.iterator(); it.hasNext();) {
            Agravo agravoDBF = it.next();
            this.getBeans().add(agravoDBF);
            getBarraStatus().setString("Calculando indicador para: " + agravoDBF.getNomeMunicipio());
        }
        getBarraStatus().setString(null);
        Collections.sort(this.getBeans(), new BeanComparator("nomeMunicipio"));
        //calcular o total
        this.getBeans().add(adicionaBrasil(municipioBean));
        
        if (!parametros.get("parSgUf").toString().equals("TODAS") && !parametros.get("municipios").toString().equals("sim")) {
            Agravo agravoBrasil = (Agravo) this.getBeans().get(27);
            List arrayT = new ArrayList();
            arrayT.add(agravoBrasil);
            this.setBeans(arrayT);
        }
    }

    /**
     * Calcula os numeradores, denominadores e indicadores filtrados por região ou regional
     * @param reader
     * @param parametros
     * @throws ParseException 
     */
    private void calculaMunicipios(DBFReader reader, Map parametros) throws ParseException {
        //buscar os municipios que vao para o resultado
        HashMap<String, Agravo> municipiosBeans = new HashMap<String, Agravo>();
        String ufResidencia = (String) parametros.get("parUf");
        String sgUfResidencia = (String) parametros.get("parSgUf");
        String codRegional = (String) parametros.get("parCodRegional");
        String codRegiao = (String) parametros.get("parCodRegiaoSaude");
        DBFUtil utilDbf = new DBFUtil();
        
        if (codRegional == null) {
            codRegional = "";
        }
        if ((Boolean)parametros.get("parIsRegiao")) {
           municipiosBeans = populaMunicipiosBeansPactuacao(sgUfResidencia,codRegiao);
        }else{
           municipiosBeans = populaMunicipiosBeans(sgUfResidencia, codRegional);
        }

        //inicia o calculo
        Object[] rowObjects;
        Date dataNotificacao;
        String racaCor, total;
        DecimalFormat df = new DecimalFormat("0");
        DecimalFormat df2 = new DecimalFormat("0.00");
        int numerador = 0, numeradorEstadual = 0, denominadorEstadual = 0, denominadorMunicipal = 0, raca = 0, completitude = 0;
        Agravo municipioResidencia;
        String dataInicio = (String) parametros.get("parDataInicio");
        String dataFim = (String) parametros.get("parDataFim");
        String[] arquivos = parametros.get("parArquivos").toString().split("\\|\\|");
        
        //loop para ler os arquivos selecionados
        for (String arquivo : arquivos) {
            int indexDoRegistroEmLeitura = 1;
            try {
                reader = Util.retornaObjetoDbfCaminhoArquivo(arquivo.substring(0, arquivo.length() - 4), Configuracao.getPropriedade("caminho"));
                utilDbf.mapearPosicoes(reader);
                double TotalRegistros = Double.parseDouble(String.valueOf(reader.getRecordCount()));
                while ((rowObjects = reader.nextRecord()) != null) {
                    //verifica a uf de residencia
                    if (utilDbf.getString(rowObjects, "SG_UF") != null) {
                        //verifica se existe a referencia do municipio no bean
                        municipioResidencia = municipiosBeans.get(utilDbf.getString(rowObjects, "ID_MN_RESI"));
                        if(municipioResidencia != null ){
                            municipioResidencia.setTaxa("0");
                        }
                        //municipioResidencia.setTaxa(String.valueOf(0));
                        dataNotificacao = utilDbf.getDate(rowObjects, "DT_NOTIFIC");
                        racaCor = utilDbf.getString(rowObjects, "CS_RACA", 1);
                        raca = racaCor != null ? Integer.parseInt(racaCor) : 0;
                        
                        if (municipioResidencia != null && raca > 0) {
                            if (isBetweenDates(dataNotificacao, dataInicio, dataFim)) {
                                if (raca >= BRANCO && raca <= INDIGENA) {
                                    numerador = Integer.parseInt(municipioResidencia.getNumerador());
                                    numerador++;
                                    municipioResidencia.setNumerador(String.valueOf(numerador));
                                }
                                    denominadorMunicipal = Integer.parseInt(municipioResidencia.getDenominador());
                                    denominadorMunicipal++;
                                    municipioResidencia.setDenominador(String.valueOf(denominadorMunicipal));
                                    calcularTaxaIndividual(df2, municipioResidencia);
                            }
                        } 
                    }
                    setStatusBarra(indexDoRegistroEmLeitura, TotalRegistros);
                    indexDoRegistroEmLeitura++;
                }
            }catch (DBFException ex) {
                Master.mensagem("Erro:\n" + ex);
            }
        }
        String ano = dataInicio.substring(0, 4);

        //CALCULA A TAXA PARA TODOS OS MUNICIPIOS
        this.setBeans(new ArrayList());
        Collection<Agravo> municipioBean = municipiosBeans.values();

        for (Iterator<Agravo> it = municipioBean.iterator(); it.hasNext();) {
            Agravo agravoDBF = it.next();
            if(null == agravoDBF.getTaxa()){
                agravoDBF.setTaxa("0");
            }
            this.getBeans().add(agravoDBF);
            getBarraStatus().setString("Calculando indicador para: " + agravoDBF.getNomeMunicipio());
        }
        getBarraStatus().setString(null);
        Collections.sort(this.getBeans(), new BeanComparator("nomeMunicipio"));
        
        //calcular o total
        if ((Boolean)parametros.get("parIsRegiao")) {
            this.getBeans().add(adicionaTotal(municipioBean,codRegiao));
             this.getBeans().set(this.getBeans().size() - 1, calcularTaxaTotal(df2));
        }else{
             this.getBeans().add(adicionaTotal(municipioBean,codRegional));
             this.getBeans().set(this.getBeans().size() - 1, calcularTaxaTotal(df2));
        }
    }
    
    private Agravo calcularTaxaTotal(DecimalFormat df){
        Agravo agravo = (Agravo) this.getBeans().get(this.getBeans().size() - 1);
        if((null != agravo.getNumerador() && Double.parseDouble(agravo.getNumerador()) != 0l)
                && (null != agravo.getDenominador() && Double.parseDouble(agravo.getDenominador()) != 0l)){
            agravo.setTaxa(df.format(
                (Double.parseDouble(agravo.getNumerador())/(Double.parseDouble(agravo.getDenominador()))*100)));
        }else{
            agravo.setTaxa("0");
        }
        return agravo;
    }
    
    private void calcularTaxaIndividual(DecimalFormat df, Agravo municipioResidencia){
        if((null != municipioResidencia.getNumerador() && Double.parseDouble(municipioResidencia.getNumerador()) != 0l)
                && (null != municipioResidencia.getDenominador() && Double.parseDouble(municipioResidencia.getDenominador()) != 0l)){
            
            municipioResidencia.setTaxa(df.format(
                Double.parseDouble(municipioResidencia.getNumerador()) / Double.parseDouble(municipioResidencia.getDenominador()) * 100));
            
        }else{
            municipioResidencia.setTaxa("0");
        }
    }

    /**
     * Calcula total, denominador, numerador do municipio específico.
     * @param reader
     * @param parametros 
     */
    @Override
    public void calcula(DBFReader reader, Map parametros) {
        String municipios = (String) parametros.get("municipios");
        String filtroUF = (String) parametros.get("parUf");
        //filtro municipios = TODOS e UF selecionado algum estado
        if (municipios.equals("sim") && !filtroUF.equals("brasil")) {
            try {
                calculaMunicipios(reader, parametros);
            } catch (ParseException ex) {
                ex.printStackTrace();
            }
        } else if (filtroUF.equals("brasil")) {
                try {
                    calculaBrasil(reader, parametros);
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }
        } else {
            calculaMunicipioIndividual(municipios, filtroUF, reader, parametros);
        }
    }
    
    private void calculaMunicipioIndividual(String municipios, String filtroUF, DBFReader reader, Map parametros){
        try {
            Object[] rowObjects;
            DBFUtil utilDbf = new DBFUtil();
            Date dtNotificacao;
            String total;
            DecimalFormat df = new DecimalFormat("0.00");
            int denominadorEstadual = 0, numeradorEstadual = 0, denominadorEspecifico = 0, numeradorEspecifico = 0, raca = 0, completitude = 0;
            String racaCor = "";
            String ufResidencia = (String) parametros.get("parUf");
            String municipioResidencia = (String) parametros.get("parMunicipio");
            String dataInicio = (String) parametros.get("parDataInicio");
            String dataFim = (String) parametros.get("parDataFim");
            String[] arquivos = parametros.get("parArquivos").toString().split("\\|\\|");

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
                    if (utilDbf.getString(rowObjects, "SG_UF") != null && utilDbf.getString(rowObjects, "SG_UF").equals(ufResidencia)) {
                        //verifica se tem o parametro de municipio de residencia
                        dtNotificacao = utilDbf.getDate(rowObjects, "DT_NOTIFIC");
                        racaCor = utilDbf.getString(rowObjects, "CS_RACA", 1);
                        raca = racaCor != null ? Integer.parseInt(racaCor) : 0;

                        if (verificaMunicipio(municipioResidencia, utilDbf.getString(rowObjects, "ID_MN_RESI"))) {
                            //verifica se a raca/cor é branca, preta, amarela, parda, indigena
                            if (isBetweenDates(dtNotificacao, dataInicio, dataFim)) {
                                denominadorEspecifico ++;
                                denominadorEstadual++;
                                if ((raca >= BRANCO && raca <= INDIGENA)) {
                                    numeradorEspecifico++;
                                    numeradorEstadual++;
                                }
                            }
                        } else {
                            //verifica se a raca/cor é branca, preta, amarela, parda, indigena
                            if (isBetweenDates(dtNotificacao, dataInicio, dataFim)) {
                                denominadorEstadual++;
                                if ((raca >= BRANCO && raca <= INDIGENA)) {
                                    numeradorEstadual++;
                                }
                            }
                        }
                    }
                    setStatusBarra(indexDoRegistroEmLeitura, TotalRegistros);
                    indexDoRegistroEmLeitura++;
                }
            }
            //começa o preencher o bean para estado ou 1 municipio
            Agravo violenciaMunicipio = new Agravo();
            violenciaMunicipio.setCodMunicipio((String) parametros.get("parMunicipio"));
            if (municipioResidencia.equals("")) {
                total = "";
                violenciaMunicipio.setNomeMunicipio((String) parametros.get("parSgUf"));
                violenciaMunicipio.setCodMunicipio(ufResidencia);
                preencheResultadoDaBusca(violenciaMunicipio, numeradorEstadual, denominadorEstadual, total, df);
            } else {
                violenciaMunicipio.setNomeMunicipio((String) parametros.get("parNomeMunicipio"));
                violenciaMunicipio.setCodMunicipio(municipioResidencia);
            }
            if (!String.valueOf(denominadorEspecifico).equals("0.0")) {
                total = "";
                preencheResultadoDaBusca(violenciaMunicipio, numeradorEspecifico, denominadorEspecifico, total, df);
            } else {
                violenciaMunicipio.setNumerador("0");
                violenciaMunicipio.setDenominador("0");
                violenciaMunicipio.setTaxa("0.00");
            }
            this.setBeans(new ArrayList());
            this.getBeans().add(violenciaMunicipio);
        } catch (NumberFormatException | ParseException | DBFException ex) {
            ex.printStackTrace();
        }
    }
    
    private void preencheResultadoDaBusca(Agravo violenciaMunicipio, int numerador, int denominador, String total, DecimalFormat df){
        violenciaMunicipio.setNumerador(String.valueOf(NumberFormat.getNumberInstance().format(Double.parseDouble(String.valueOf(numerador)))));
        violenciaMunicipio.setDenominador(String.valueOf(NumberFormat.getNumberInstance().format(Double.parseDouble(String.valueOf(denominador)))));
        total = df.format(Double.parseDouble(String.valueOf(numerador)) / Double.parseDouble(String.valueOf(denominador)) * 100);
        violenciaMunicipio.setTaxa(total);
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
        return new String[]{"ID_LOCRES", "DS_LOCRES", "ID_UFRES", "CS_RACA", "NU_ANO", "DT_NOTIFIC", "ID_REGIONA"};
    }

    @Override
    public HashMap<String, ColunasDbf> getColunas() {
        HashMap<String, ColunasDbf> hashColunas = new HashMap<String, ColunasDbf>();
        hashColunas.put("ID_LOCRES", new ColunasDbf(7));
        hashColunas.put("DS_LOCRES", new ColunasDbf(30));
        hashColunas.put("ID_UFRES", new ColunasDbf(2));
        hashColunas.put("CS_RACA", new ColunasDbf(1, 0));
        hashColunas.put("P_RACPRE", new ColunasDbf(4, 0));
        hashColunas.put("DT_NOTIFIC", new ColunasDbf(10));
        hashColunas.put("DT_NOTIFI", new ColunasDbf(10));
        this.setColunas(hashColunas);
        return hashColunas;
    }

    @Override
    public DBFWriter getLinhas(HashMap<String, ColunasDbf> colunas, List bean, DBFWriter writer) throws DBFException, IOException {
        for (int i = 0; i < bean.size(); i++) {
            Object rowData[] = new Object[colunas.size()];
            Agravo agravo = (Agravo) bean.get(i);
            if (agravo.getNomeMunicipio().equals("BRASIL") || agravo.getNomeMunicipio().equals("TOTAL")) {
                rowData[0] = null;
                rowData[2] = null;
            } else {
                rowData[0] = agravo.getCodMunicipio();
                rowData[2] = agravo.getCodMunicipio().substring(0, 2);
            }
            rowData[1] = agravo.getNomeMunicipio();
            rowData[3] = Double.parseDouble(agravo.getNumerador());
            rowData[4] = Double.parseDouble(agravo.getTaxa());
            rowData[5] = Double.parseDouble(agravo.getDenominador());
            rowData[6] = getDataFim();
            //rowData[7] = "VIOLENCIA-SINANNET";

            writer.addRecord(rowData);
        }
        return writer;
    }

    @Override
    public String getCaminhoJasper() {
        return "/com/org/relatorios/violencia.jasper";
    }
}