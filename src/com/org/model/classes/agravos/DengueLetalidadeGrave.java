/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.org.model.classes.agravos;

import com.linuxense.javadbf.DBFException;
import com.linuxense.javadbf.DBFWriter;
import com.org.bd.DBFUtil;
import com.org.model.classes.Agravo;
import com.org.model.classes.ColunasDbf;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author geraldo
 */
public class DengueLetalidadeGrave extends DengueLetalidade {

    static String ANO;
    List<String> semanasAvaliacao = new ArrayList<String>();
    private String semanaInicioDbf;
    private String semanaFimDbf;

    public DengueLetalidadeGrave(boolean isDbf) {
        this.setDBF(isDbf);
        setPeriodo("de Primeiros Sintomas");
        setTipoAgregacao("de Residência");
        init("postgres");
    }

    public DengueLetalidadeGrave() {
    }

    @Override
    public void init(String tipoBanco) {
        this.setArquivo("DENGNET");
        this.setMultiplicador(100);
        this.setTitulo1("Taxa de letalidade das Formas graves de Dengue");
        this.setTextoCompletitude("% de não preenchimento do campo Evolução do caso: ");
        this.setTituloColuna("Taxa Letalidade");
        this.setRodape("Numerador: Número de óbitos confirmados por Dengue com Complicações ou Febre Hemorrágica da Dengue (FHD) ou Síndrome do Choque da Dengue (SCD) por local de residência e ano de primeiros sintomas. \nDenominador: Número de casos confirmados por Dengue com Complicações ou Febre Hemorrágica da Dengue (FHD) ou Síndrome do Choque da Dengue (SCD) por local de residência e ano de primeiros sintomas.");
        this.setTipo("dengueGrave");
        this.setSqlNumeradorCompletitude("tem completitude");
        if (!isDBF()) {
            this.setSqlNumeradorMunicipioEspecifico("SELECT  count(*) as numerador FROM dbsinan.tb_notificacao " + 
                    "where co_cid = 'A90' and ds_semana_sintoma in (?) and 1=? and co_uf_residencia= ? "
                    + "and co_municipio_residencia = ? and tp_evolucao_caso = '2' "
                    + "and tp_classificacao_final in (2,3,4) and tp_criterio_confirmacao in(1,2)");
            
            this.setSqlDenominadorMunicipioEspecifico("SELECT count(*) as denominador FROM  dbsinan.tb_notificacao " + 
                    "where co_cid = 'A90' and ds_semana_sintoma in (?) and 1=? and co_uf_residencia= ? "
                    + "and co_municipio_residencia = ? and tp_classificacao_final in (2,3,4) and tp_criterio_confirmacao in(1,2)");
            
            this.setSqlNumeradorEstado("SELECT  count(*) as numerador FROM  dbsinan.tb_notificacao as t1 "
                    + "where co_cid = 'A90' and ds_semana_sintoma in (?) and 1=? and co_uf_residencia= ? "
                    + "and tp_evolucao_caso = '2' and tp_classificacao_final in (2,3,4) and tp_criterio_confirmacao in(1,2)");
            
            this.setSqlDenominandorEstado("SELECT count(*) as denominador FROM dbsinan.tb_notificacao as t1 " + 
                    "where co_cid = 'A90' and ds_semana_sintoma in (?) and 1=? and co_uf_residencia= ? "
                    + "and tp_classificacao_final in (2,3,4) and tp_criterio_confirmacao in(1,2)");
            
            this.setSqlNumeradorCompletitude("SELECT count(*) as numerador FROM  dbsinan.tb_notificacao as t1 " + 
                    "where co_cid = 'A90' and ds_semana_sintoma in (?) and co_uf_residencia= ? "
                    + "and tp_evolucao_caso is null and tp_classificacao_final in (2,3,4) and tp_criterio_confirmacao in(1,2)");
            
            this.setSqlNumeradorBeanMunicipios(this.getSqlNumeradorMunicipioEspecifico());
            this.setSqlDenominadorBeanMunicipios(this.getSqlDenominadorMunicipioEspecifico());
        }
    }

    @Override
    public boolean verificaClassificacaoFinal(int classificacaoFinal) {
        if (classificacaoFinal == 2 || classificacaoFinal == 3 || classificacaoFinal == 4) {
            return true;
        }
        return false;
    }

    @Override
    public boolean verificaCriterio(int criterio) {
         if (criterio == 1 || criterio == 2) {
            return true;
        }
        return false;
    }
    
    

    @Override
    public boolean verificaPeriodo(Map parametros, DBFUtil utilDbf, Object[] rowObjects) throws ParseException {
        if (semanasAvaliacao.isEmpty()) {
            parametros = populaSemana(parametros);
        }
        if (parametros.get("parAnoInicial").toString().equals(parametros.get("parAnoFinal").toString())) {
            ANO = parametros.get("parAnoFinal").toString();
        } else {
            ANO = "";
        }
        return isBetweenDates(utilDbf.getString(rowObjects, "SEM_PRI"), (String) parametros.get("parAnoInicial"), (String) parametros.get("parDataFim"));
    }
    @Override
    public Map populaSemana(Map parametros) {
        int semanaInicio = Integer.parseInt(parametros.get("parSemanaInicial").toString());
        int semanaFim = Integer.parseInt(parametros.get("parSemanaFinal").toString());
        setSemanaInicioDbf(parametros.get("parAnoInicial").toString() + formataSemana(parametros.get("parSemanaInicial").toString()));
        setSemanaFimDbf(parametros.get("parAnoFinal").toString() + formataSemana(parametros.get("parSemanaFinal").toString()));
        String semanas = "";

//        if (parametros.get("parAnoInicial").toString().equals(parametros.get("parAnoFinal").toString())) {
//            for (int i = semanaInicio; i <= semanaFim; i++) {
//                semanasAvaliacao.add(parametros.get("parAnoInicial").toString() + formataSemana(String.valueOf(i)));
//                if (i > semanaInicio) {
//                    semanas = semanas + "\"";
//                }
//                semanas = semanas + "" + parametros.get("parAnoInicial").toString() + formataSemana(String.valueOf(i)) + "";
//                if (i < semanaFim) {
//                    semanas = semanas + ",";
//                }
//                if (i == semanaFim) {
//                    semanas = semanas + "\"";
//                }
//            }
//        } else {
//            for (int i = semanaInicio; i <= 53; i++) {
//                semanasAvaliacao.add(parametros.get("parAnoInicial").toString() + formataSemana(String.valueOf(i)));
//                if (i > semanaInicio) {
//                    semanas = semanas + "\"";
//                }
//                semanas = semanas + "" + parametros.get("parAnoInicial").toString() + formataSemana(String.valueOf(i)) + "";
//                if (i < semanaFim) {
//                    semanas = semanas + ",";
//                }
//                if (i == semanaFim) {
//                    semanas = semanas + "\"";
//                }
//            }
//            for (int i = 1; i <= semanaFim; i++) {
//                semanasAvaliacao.add(parametros.get("parAnoInicial").toString() + formataSemana(String.valueOf(i)));
//                if (i > semanaInicio) {
//                    semanas = semanas + "\"";
//                }
//                semanas = semanas + "" + parametros.get("parAnoInicial").toString() + formataSemana(String.valueOf(i)) + "";
//                if (i < semanaFim) {
//                    semanas = semanas + ",";
//                }
//                if (i == semanaFim) {
//                    semanas = semanas + "\"";
//                }
//            }
//        }

        if (parametros.get("parAnoInicial").toString().equals(parametros.get("parAnoFinal").toString())) {
            for (int i = semanaInicio; i <= semanaFim; i++) {
                semanas = semanas + "'" + parametros.get("parAnoInicial").toString() + formataSemana(String.valueOf(i)) + "'";
                semanasAvaliacao.add(parametros.get("parAnoInicial").toString() + formataSemana(String.valueOf(i)));
                if (i < semanaFim) {
                    semanas = semanas + ",";
                }
            }
        } else {
            for (int i = semanaInicio; i <= 53; i++) {
                semanas = semanas + "'" + parametros.get("parAnoInicial").toString() + formataSemana(String.valueOf(i)) + "'";
                semanasAvaliacao.add(parametros.get("parAnoInicial").toString() + formataSemana(String.valueOf(i)));
                if (i < 53) {
                    semanas = semanas + ",";
                }
            }
            for (int i = 1; i <= semanaFim; i++) {
                semanas = semanas + "'" + parametros.get("parAnoInicial").toString() + formataSemana(String.valueOf(i)) + "'";
                semanasAvaliacao.add(parametros.get("parAnoInicial").toString() + formataSemana(String.valueOf(i)));
                if (i < semanaFim) {
                    semanas = semanas + ",";
                }
            }
        }
        System.out.println(semanas);
        parametros.put("parDataInicio", semanas);
        parametros.put("parDataFim", "1");
        if (parametros.get("parAnoInicial").toString().equals(parametros.get("parAnoFinal").toString())) {
            ANO = parametros.get("parAnoFinal").toString();
        } else {
            ANO = "";
        }
        return parametros;
    }

    private String formataSemana(String semana) {
        if (Integer.parseInt(semana) < 10) {
            return "0" + semana;
        } else {
            return semana;
        }
    }

    public boolean isBetweenDates(String dataParametro, String dataInicio, String dataFim) throws ParseException {
        if (semanasAvaliacao.contains(dataParametro)) {
            return true;
        }
        return false;
    }

    @Override
    public Map getParametros() {
        Map parametros = new HashMap();


        parametros.put("parTituloColuna", this.getTituloColuna());
        parametros.put("parFator", String.valueOf(this.getMultiplicador()));
        // parametros.put("parAno", Util.getAno(this.getDataFim()));
        parametros.put("parRodape", this.getRodape());
        parametros.put("parConfig", "");
        parametros.put("parTitulo1", "Taxa de letalidade das Formas graves de Dengue");        
        return parametros;
    }

    @Override
    public HashMap<String, ColunasDbf> getColunas() {
        HashMap<String, ColunasDbf> hashColunas = new HashMap<String, ColunasDbf>();
        hashColunas.put("ID_LOCRES", new ColunasDbf(7));
        hashColunas.put("DS_LOCRES", new ColunasDbf(30));
        hashColunas.put("ID_UFRES", new ColunasDbf(2));
        hashColunas.put("N_LETDENG", new ColunasDbf(10, 0));
        hashColunas.put("D_LETDENG", new ColunasDbf(10, 0));
        hashColunas.put("I_LETDENG", new ColunasDbf(6, 2));
        hashColunas.put("ANO_EPPRI", new ColunasDbf(4, 0));
        hashColunas.put("SE_PRINIC", new ColunasDbf(6));
        hashColunas.put("SE_PRIFIN", new ColunasDbf(6));
        hashColunas.put("ORIGEM", new ColunasDbf(30));
        this.setColunas(hashColunas);
        return hashColunas;
    }

    @Override
    public String[] getOrdemColunas() {
        return new String[]{"ID_LOCRES", "DS_LOCRES", "ID_UFRES", "N_LETDENG", "D_LETDENG",
        "I_LETDENG", "ANO_EPPRI", "SE_PRINIC", "SE_PRIFIN", "ORIGEM"};
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
                rowData[0] = agravo.getCodMunicipio();
                rowData[2] = agravo.getCodMunicipio().substring(0, 2);
            }
            rowData[1] = agravo.getNomeMunicipio();
            rowData[3] = Double.parseDouble(agravo.getNumerador());
            rowData[4] = Double.parseDouble(agravo.getDenominador());
            rowData[5] = Double.parseDouble(agravo.getTaxa().replace(",", "."));
            rowData[6] = preencheAnoSemana(getSemanaInicioDbf(),getSemanaFimDbf());
            rowData[7] = getSemanaInicioDbf();
            rowData[8] = getSemanaFimDbf();
            rowData[9] = "DENGUE-SINANNET";

            writer.addRecord(rowData);
        }
        return writer;
    }

    /**
     * @return the semanaInicioDbf
     */
    public String getSemanaInicioDbf() {
        return semanaInicioDbf;
    }

    /**
     * @param semanaInicioDbf the semanaInicioDbf to set
     */
    public void setSemanaInicioDbf(String semanaInicioDbf) {
        this.semanaInicioDbf = semanaInicioDbf;
    }

    /**
     * @return the semanaFimDbf
     */
    public String getSemanaFimDbf() {
        return semanaFimDbf;
    }

    /**
     * @param semanaFimDbf the semanaFimDbf to set
     */
    public void setSemanaFimDbf(String semanaFimDbf) {
        this.semanaFimDbf = semanaFimDbf;
    }
}
