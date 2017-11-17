/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.org.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.joda.time.DateTime;

/**
 *
 * @author Taidson
 */
public class SinanDateUtil {

    private static DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
    private static DateFormat formatPostgre = new SimpleDateFormat("yyyy-MM-dd");
    public static final Pattern PADRAO_DATA_DDMMYYYY = Pattern.compile("\\d\\d[/-]\\d\\d[/-]\\d\\d\\d\\d"),
            PADRAO_DATA_YYYYMMDD = Pattern.compile("\\d\\d\\d\\d[/-]\\d\\d[/-]\\d\\d");

    /**
     * Método para transformar da string "dd/MM/yyyy" para java.sql.Date.
     *
     * @param strDate
     * @return
     * @throws ParseException
     * @author Rodrigo Freitas
     */
    public static Date stringToDate(String strDate) throws ParseException, java.text.ParseException {
        return stringToDate(strDate, "dd/MM/yyyy");
    }

    public static Date stringToDate(String strDate, String pattern) throws ParseException, java.text.ParseException {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        Date data = new Date(format.parse(strDate).getTime());
        return data;
    }

    /**
     * Método para obter a diferença, em dias, entre duas datas.
     *
     * @param de
     * @param ate
     */
    public static Long calculaDiferencaDias(Date de, Date ate) {
        long dif = ate.getTime() - de.getTime();
        dif /= 1000 * 60 * 60 * 24;
        return dif;
    }

    /**
     * Método para obter a primeira data do mês atual.
     *
     * @return java.sql.Date
     */
    @SuppressWarnings("static-access")
    public static java.sql.Date firstDateOfMonth() {
        Calendar firstData = Calendar.getInstance();
        firstData.set(Calendar.DAY_OF_MONTH, firstData.getActualMinimum(Calendar.DAY_OF_MONTH));
        java.sql.Date data = new java.sql.Date(firstData.getTimeInMillis());
        return data;
    }

    public static String subtrairAno(String data, int y) {
        if (data != null && !data.isEmpty()) {
            Calendar dt = Calendar.getInstance();
            String dataInicioCoortePB;
            try {
                dt.setTime(SinanDateUtil.stringToDate(data));
            } catch (ParseException ex) {
                Logger.getLogger(SinanDateUtil.class.getName()).log(Level.SEVERE, null, ex);
            }
            dt.add(Calendar.YEAR, y);
            dataInicioCoortePB = new SimpleDateFormat("dd/MM/yyyy").format(dt.getTime());
            return dataInicioCoortePB;
        }else{
            return "";
        }

    }

    public static java.sql.Date firstDateOfMonth(Date date) {
        Calendar firstData = SinanDateUtil.dateToCalendar(date);
        firstData.set(Calendar.DAY_OF_MONTH, firstData.getActualMinimum(Calendar.DAY_OF_MONTH));
        java.sql.Date data = new java.sql.Date(firstData.getTimeInMillis());
        return data;
    }

    /**
     * Método para obter a data atual.
     *
     * @return java.sql.Date
     */
    public static java.sql.Date currentDate() {
        return new java.sql.Date(System.currentTimeMillis());
    }

    public static Boolean checkDateMajorCurrentDate(Date d) {
        if (d != null) {
            return d.getTime() > currentDate().getTime();
        } else {
            return false;
        }

    }

    public static String currentDateString() {
        return new SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date());
    }

    /**
     * Método para obter a última data do mês atual.
     *
     * @return java.sql.Date
     */
    @SuppressWarnings("static-access")
    public static java.sql.Date lastDateOfMonth() {
        Calendar lastData = new GregorianCalendar();
        lastData.set(Calendar.DAY_OF_MONTH, lastData.getActualMaximum(Calendar.DAY_OF_MONTH));
        java.sql.Date data = new java.sql.Date(lastData.getTimeInMillis());
        return data;
    }

    public static java.sql.Date lastDateOfMonth(Date date) {
        Calendar lastData = SinanDateUtil.dateToCalendar(date);
        lastData.set(Calendar.DAY_OF_MONTH, lastData.getActualMaximum(Calendar.DAY_OF_MONTH));
        java.sql.Date data = new java.sql.Date(lastData.getTimeInMillis());
        return data;
    }

    /**
     * Método para incrementar datas.
     *
     * @param data - Data a ser incrementada.
     * @param num - Número de vezes que um campo será incrementado.
     * @param field - Campo a ser incrementado. (Constantes de
     * java.util.Calendar)
     * @return java.sql.Date
     */
    public static java.sql.Date incrementDate(Calendar data, int num, int field) {
        data.add(field, num);
        return (java.sql.Date) new Date(data.getTimeInMillis());
    }

    /**
     * <p>
     * Passa uma data para String no formato dd/MM/yyyy.</p>
     *
     * @param date
     * @return
     */
    public static String toString(java.util.Date date) {
        return format.format(date);
    }

    /**
     * <p>
     * Passa uma data para String no formato yyyy-MM-dd.</p>
     *
     * @param date
     * @return
     */
    public static String toStringPostgre(java.util.Date date) {
        return formatPostgre.format(date);
    }

    /**
     * <p>
     * Passa uma data para String no formato informado.</p>
     *
     * @param date
     * @return
     */
    public static String toString(java.sql.Date date, String format) {
        return new java.text.SimpleDateFormat(format).format(date);
    }

    /**
     * Converte data em string conforme padrão informado como parâmetro e
     * realiza a validação do preenchimento do campo date.
     *
     * @param date
     * @param format
     * @return
     */
    public static String dateToStringException(Date date, String format) {
        try {
            return new java.text.SimpleDateFormat(format).format(date);
        } catch (Exception e) {
            SinanUtil.mensagem("Favor preencher o campo Data corretamente.");
            return null;
        }
    }

    /**
     * Converte data em string conforme padrão informado como parâmetro.
     *
     * @param date
     * @param format
     * @return
     */
    public static String dateToString(Date date, String format) {
        return new java.text.SimpleDateFormat(format).format(date);
    }

    public static String toString(java.sql.Timestamp date, String format) {
        return new java.text.SimpleDateFormat(format).format(date);
    }

    /**
     * <p>
     * Passa uma data para String no formato dd/MM/yyyy.</p>
     *
     * @param date
     * @return
     */
    public static String toString(java.util.Calendar calendar) {
        return toString(new java.util.Date(calendar.getTimeInMillis()));
    }

    /**
     * <p>
     * Verifica se duas datas são iguais, desconsiderando as horas.</p>
     *
     * @param d1
     * @param d2
     * @return
     */
    public static boolean equalsIgnoreHour(java.sql.Date d1, java.sql.Date d2) {
        if (d1 == null && d2 == null) {
            return true;
        } else if ((d1 != null && d2 == null) || (d1 == null && d2 != null)) {
            return false;
        }
        return toString(d1).equals(toString(d2));
    }

    /**
     * Método que retorna a descrição do mês através de uma data fornecida.
     *
     * @param data
     *
     */
    public static String getDescricaoMes(Date data) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(data);

        String mes = "";

        switch (calendar.get(Calendar.MONTH)) {
            case 0:
                mes = "Janeiro";
                break;
            case 1:
                mes = "Fevereiro";
                break;
            case 2:
                mes = "Março";
                break;
            case 3:
                mes = "Abril";
                break;
            case 4:
                mes = "Maio";
                break;
            case 5:
                mes = "Junho";
                break;
            case 6:
                mes = "Julho";
                break;
            case 7:
                mes = "Agosto";
                break;
            case 8:
                mes = "Setembro";
                break;
            case 9:
                mes = "Outubro";
                break;
            case 10:
                mes = "Novembro";
                break;
            case 11:
                mes = "Dezembro";
                break;
            default:
                break;
        }

        return mes;
    }

    /**
     * Calcula a diferença em horas das
     *
     * @param inicioStr
     * @param fimStr
     * @return
     */
    public static Double calculaDiferencaHoras(String inicioStr, String fimStr) {
        String[] inicio = inicioStr.split(":");
        String[] fim = fimStr.split(":");

        Double inicioHora = Double.parseDouble(inicio[0]);
        Double fimHora = Double.parseDouble(fim[0]);

        Double inicioMin = Double.parseDouble(inicio[1]);
        Double fimMin = Double.parseDouble(fim[1]);

        inicioHora = (inicioMin / 60) + inicioHora;
        fimHora = (fimMin / 60) + fimHora;

        inicioHora = Math.round(inicioHora * 100.0) / 100.0;
        fimHora = Math.round(fimHora * 100.0) / 100.0;

        return (fimHora - inicioHora);
    }

    /**
     * Converte strings de datas no formato yyyy-mm-dd ou dd-mm-yyyy em objetos
     * do tipo java.sql.Date. O "-" pode ser substituido por "/".
     *
     * @param data Data nos formatos descritos acima
     * @return java.sql.Date ou null se o formato da data for inválido
     */
    public static java.sql.Date stringToSqlDate(String data) {
        if (data == null) {
            return null;
        }

        data = data.trim();

        if (PADRAO_DATA_YYYYMMDD.matcher(data).matches()) {
            data = data.replace('/', '-');
        } else if (PADRAO_DATA_DDMMYYYY.matcher(data).matches()) {
            String dt[] = data.split("[/-]");
            data = dt[2] + "-" + dt[1] + "-" + dt[0];
        } else {
            return null;
        }

        return java.sql.Date.valueOf(data);
    }

    public static int diferencaDias(java.util.Date data1, java.util.Date data2) {
        return diferencaHoras(data1, data2) / 24;
    }

    public static int diferencaMeses(java.util.Date data1, java.util.Date data2) {
        return diferencaHoras(data1, data2) / 24 / 30;
    }

    public static int diferencaSemanas(java.util.Date data1, java.util.Date data2) {
        return diferencaHoras(data1, data2) / 24 / 7;
    }

    /**
     * Retorna a o resultado de (data1 - data2), em dias inteiros.
     *
     * @param data1
     * @param data2
     * @return diferença em dias dias
     */
    public static int diferencaHoras(java.util.Date data1, java.util.Date data2) {

        Calendar calendar1 = dateToCalendar(data1);
        calendar1.set(Calendar.HOUR_OF_DAY, 0);
        calendar1.set(Calendar.MINUTE, 0);
        calendar1.set(Calendar.SECOND, 0);
        calendar1.set(Calendar.MILLISECOND, 0);

        Calendar calendar2 = dateToCalendar(data2);
        calendar2.set(Calendar.HOUR_OF_DAY, 0);
        calendar2.set(Calendar.MINUTE, 0);
        calendar2.set(Calendar.SECOND, 0);
        calendar2.set(Calendar.MILLISECOND, 0);

        return (int) ((calendar1.getTimeInMillis() - calendar2.getTimeInMillis()) / 1000 / 60 / 60);
    }

    /**
     * Converte um java.sql.Date para Calendar
     *
     * @param java.sql.Date
     * @return Calendar
     */
    public static Calendar dateToCalendar(java.util.Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        return calendar;
    }

    /**
     * Método que adiciona dias a uma determinada Data
     *
     * @param dtReferencia
     * @param dias
     * @return
     */
    public static Date addDiasData(Date dtReferencia, int dias) {
        Calendar dtLimiteAux = Calendar.getInstance();
        dtLimiteAux.setTimeInMillis(dtReferencia.getTime());
        dtLimiteAux.add(Calendar.DAY_OF_MONTH, dias);

        return new Date(dtLimiteAux.getTimeInMillis());
    }

    /**
     * Método que adiciona mês a uma determinada Data
     *
     * @param dtReferencia
     * @param meses
     * @return
     */
    public static Date addMesData(Date dtReferencia, int meses) {
        Calendar dtLimiteAux = Calendar.getInstance();
        dtLimiteAux.setTimeInMillis(dtReferencia.getTime());
        dtLimiteAux.add(Calendar.MONTH, meses);

        return new Date(dtLimiteAux.getTimeInMillis());
    }

    /**
     * Retorna uma nova data no começo do dia
     *
     * @param java.util.data
     * @return
     * @author Tomás Rabelo
     */
    public static java.util.Date dataToEndOfDay(java.util.Date data) {
        if (data != null) {
            return dataToEndOfDay(new java.sql.Date(data.getTime()));
        } else {
            return null;
        }

    }

    public static Integer mesesEntre(Date dtInicio, Date dtFim) {
        Calendar dtInicioAux = Calendar.getInstance();
        Calendar dtFimAux = Calendar.getInstance();

        dtInicioAux.setTimeInMillis(dtInicio.getTime());
        dtFimAux.setTimeInMillis(dtFim.getTime());

        return mesesEntre(dtInicioAux, dtFimAux);
    }

    /**
     * Retorna a quantidade de meses entre duas datas (por exemplo: o período
     * entre 01/2007 e 03/2007 corresponde a 2 meses)
     *
     * @param dtInicio
     * @param dtFim
     * @return
     */
    public static Integer mesesEntre(Calendar dtInicio, Calendar dtFim) {
        if (dtInicio == null || dtFim == null) {
            return null;
        }

        int diaIni = dtInicio.get(Calendar.DAY_OF_MONTH);
        int mesIni = dtInicio.get(Calendar.MONTH) + 1;
        int anoIni = dtInicio.get(Calendar.YEAR);

        int diaFim = dtFim.get(Calendar.DAY_OF_MONTH);
        int mesFim = dtFim.get(Calendar.MONTH) + 1;
        int anoFim = dtFim.get(Calendar.YEAR);

        int anos = 0;
        int meses = 0;

        if ((anoFim < anoIni) || ((anoFim == anoIni) && (mesFim < mesIni))) {
            return 0;
        }

        anos = anoFim - anoIni;

        if (mesFim < mesIni) {
            meses = (12 - mesIni) + mesFim;
            if (anos > 0) {
                --anos;
            }
        } else {
            meses = mesFim - mesIni;
        }

        if (diaFim < diaIni) {
            if (meses == 0) {
                if (anos > 0) {
                    --anos;
                    meses = 11;
                }
            } else {
                --meses;
            }
        }

        meses += anos * 12;
        return meses;
    }

    public static Date convertToDate(String data) throws ParseException {
        try {
            String mes = data.substring(5, 7);
            String dia = data.substring(8, 10);
            String ano = data.substring(2, 4);

            String str = mes + "/" + dia + "/" + ano;
            DateFormat dt = DateFormat.getDateInstance(DateFormat.SHORT, Locale.US);
            java.util.Date date = dt.parse(str);
            return new Date(date.getTime());
        } catch (NumberFormatException ex) {
            SinanUtil.mensagem("Erro:\n" + ex);
        } catch (ParseException ex) {
            SinanUtil.mensagem("Erro:\n" + ex);
        }
        return null;

    }

    public static boolean isBetweenDates(Date dataParametro, String dataInicio, String dataFim) throws ParseException {
        Date dtInicio = SinanDateUtil.convertToDate(dataInicio);
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

    public static boolean isBetweenDates2(Date dataParametro, String dataInicio, String dataFim) throws ParseException {
        Date dtInicio = SinanDateUtil.stringToSqlDate(dataInicio);
        Date dtFim = SinanDateUtil.stringToSqlDate(dataFim);
        if (dataParametro != null) {
            if (dataParametro.after(dtInicio) || dataParametro.equals(dtInicio)) {
                if (dataParametro.before(dtFim) || dataParametro.equals(dtFim)) {
                    return true;
                }
            }
        }
        return false;
    }
    /*
     Retorna o intervalo de datas entre duas semanas. Utiliza a API Joda-Time.
     */
    public static String getIntervaloDatas(Integer ano, Integer semanaInicial, Integer semanaFinal){
        Calendar calendar = Calendar.getInstance();
        calendar.set(ano, 0, 0);
        String intervaloDatas = "";
        DateTime dataInicial = new DateTime(calendar.getTime()).plusWeeks(semanaInicial).minusDays(6);//Pega o primeiro dia da semana Inicial do intervalo
        DateTime dataFinal = new DateTime(calendar.getTime()).plusWeeks(semanaFinal);
        intervaloDatas = dateToString(dataInicial.toDate(), "dd/MM/YYYY") + " a " + dateToString(dataFinal.toDate(),"dd/MM/YYYY");
        
        return intervaloDatas;
    }
}
