/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.org.bd;

/**
 *
 * @author Geraldo
 */
import java.sql.*;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author mayron
 */
public class Conexao {

    private String local;
    private String user;
    private String senha;
    private Connection c;
    private Statement statment;
    private String str_conexao;
    private String driverjdbc;

    public Conexao(String bd, String banco, String host, String usuario, String pass) {
        if (bd.equals("POSTGRESQL")) {
            setLocal(host);
            //setSenha("oiretsinimodeduas1953");
            setSenha("*Dtspg.112233*");
            setUser("postgres");
//            setSenha(pass);
//            setUser(usuario);
            setStr_conexao("jdbc:postgresql://" + getLocal() + ":5445/" + banco);
            setDriverjdbc("org.postgresql.Driver");
        } else {
            if (bd.equals("INTERBASE")) {
                setLocal(host);
                setSenha("masterkey");
                setUser("SYSDBA");
                //C:\Arquivos de programas\Firebird\Firebird_2_1\bin\SICEB
//                setStr_conexao("jdbc:firebirdsql:localhost/3050:C:\\Program Files\\Borland\\InterBase\\bin\\SICEB\\sinannet.dat");
//                setStr_conexao("jdbc:firebirdsql:localhost/3050:C:\\SinanNet\\sinannet.dat");
                setStr_conexao("jdbc:firebirdsql:" + getLocal() + "/3050:" + banco);
                setDriverjdbc("org.firebirdsql.jdbc.FBDriver");
            }
        }
    }

    public void configUser(String user, String senha) {
        setUser(user);
        setSenha(senha);
    }

    public void configLocal(String banco) {
        setLocal(banco);
    }

//CONEXAO PARA A VERSAO 4. SENHA É DIFERENTE
    public void conect2() {
        try {
            Class.forName(getDriverjdbc());
            setC(DriverManager.getConnection(getStr_conexao(), getUser(), "SDVESP99DMDS04"));
            setStatment(getC().createStatement());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(new JFrame(), "Não foi possível conectar ao banco de dados. Verifique se esta máquina possui o Sinan NET instalado. Caso não possua, utilize a base DBF\n" + e, "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

//Conexão com o Banco de Dados
    public void conect() {
        try {
            Class.forName(getDriverjdbc());
            setC(DriverManager.getConnection(getStr_conexao(), getUser(), getSenha()));
            setStatment(getC().createStatement());
        } catch (Exception e) {
            if (getUser().equals("postgres")) {
                conect2();
            } else {
                JOptionPane.showMessageDialog(new JFrame(), "Não foi possível conectar ao banco de dados. Verifique se esta máquina possui o Sinan NET instalado. Caso não possua, utilize a base DBF\n" + e, "Erro",
                        JOptionPane.ERROR_MESSAGE);
                System.err.println(e);
                e.printStackTrace();
            }

        }
    }

    public void disconect() {
        try {
            getC().close();
        } catch (SQLException ex) {
            System.err.println(ex);
            ex.printStackTrace();
        }
    }

    public ResultSet query(String query) {
        try {
            return getStatment().executeQuery(query);
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

// GETs AND SETs
    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Connection getC() {
        return c;
    }

    public void setC(Connection c) {
        this.c = c;
    }

    public Statement getStatment() {
        return statment;
    }

    public void setStatment(Statement statment) {
        this.statment = statment;
    }

    public String getStr_conexao() {
        return str_conexao;
    }

    public void setStr_conexao(String str_conexao) {
        this.str_conexao = str_conexao;
    }

    public String getDriverjdbc() {
        return driverjdbc;
    }

    public void setDriverjdbc(String driverjdbc) {
        this.driverjdbc = driverjdbc;
    }
}
