package control;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Properties;

public class Database
{
    private Connection conn = null;

    public Database()
    {
        try
        {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + Constants.DB.ARQUIVO);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void execute(String sql)
    {
        try
        {
            conn.createStatement().executeUpdate(sql);
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public ResultSet query(String sql)
    {
        try
        {
            return conn.createStatement().executeQuery(sql);
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public void close()
    {
        try
        {
            conn.close();
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public int getNotCodigo(int NotDisciplina, int NotPeriodo)
    {
        String sql = String.format(
                "SELECT NOTCODIGO FROM NOTAS WHERE NotDisciplina = %d AND NotPeriodo = %d",
                NotDisciplina,
                NotPeriodo
        );

        ResultSet rs = query(sql);
        try
        {
            if (rs.next())
                return rs.getInt(1);
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return 0;
    }

    public float getNota(int disciplina, int periodo)
    {
        String sql = String.format(
                "SELECT NOTNOTA FROM NOTAS WHERE NOTDISCIPLINA = %d AND NOTPERIODO = %d",
                disciplina,
                periodo
        );

        ResultSet rs = query(sql);

        try
        {
            if (rs.next())
                return rs.getFloat(1);
            else
                return (float) 0.0;
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return 0;
    }

    public float getMedia(int DisCodigo)
    {
        String periodo = "";
        try
        {
            Properties props = new Properties();
            props.load(new FileInputStream(new File(Constants.Properties.ARQUIVO)));
            periodo = props.getProperty(Constants.Properties.Keys.PERIODO);
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        DecimalFormat df = new DecimalFormat("##.#");

        if (periodo.equals(Constants.Properties.Values.Periodo.SEMESTRE))
            return Float.parseFloat(df.format(getSomaNotas(DisCodigo) / 2).replace(',', '.'));
        if (periodo.equals(Constants.Properties.Values.Periodo.TRIMESTRE))
            return Float.parseFloat(df.format(getSomaNotas(DisCodigo) / 3).replace(',', '.'));
        if (periodo.equals(Constants.Properties.Values.Periodo.BIMESTRE))
            return Float.parseFloat(df.format(getSomaNotas(DisCodigo) / 4).replace(',', '.'));

        return 0;
    }

    public float getSomaNotas(int DisCodigo)
    {
        String periodo = "";
        try
        {
            Properties props = new Properties();
            props.load(new FileInputStream(new File(Constants.Properties.ARQUIVO)));
            periodo = props.getProperty(Constants.Properties.Keys.PERIODO);
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        DecimalFormat df = new DecimalFormat("##.#");

        float soma = 0;

        if (periodo.equals(Constants.Properties.Values.Periodo.SEMESTRE))
        {
            soma += getNota(DisCodigo, 1);
            soma += getNota(DisCodigo, 2);
        }

        if (periodo.equals(Constants.Properties.Values.Periodo.TRIMESTRE))
        {
            soma += getNota(DisCodigo, 1);
            soma += getNota(DisCodigo, 2);
            soma += getNota(DisCodigo, 3);
        }

        if (periodo.equals(Constants.Properties.Values.Periodo.BIMESTRE))
        {
            soma += getNota(DisCodigo, 1);
            soma += getNota(DisCodigo, 2);
            soma += getNota(DisCodigo, 3);
            soma += getNota(DisCodigo, 4);
        }

        return Float.parseFloat(df.format(soma).replace(',', '.'));

    }

    public float getQuantoFalta(int DisCodigo)
    {
        String periodo = "";
        float media = (float) 0.0;
        try
        {
            Properties props = new Properties();
            props.load(new FileInputStream(new File(Constants.Properties.ARQUIVO)));
            periodo = props.getProperty(Constants.Properties.Keys.PERIODO);
            media = Float.parseFloat(props.getProperty(Constants.Properties.Keys.MEDIA));
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        DecimalFormat df = new DecimalFormat("##.#");

        if (periodo.equals(Constants.Properties.Values.Periodo.SEMESTRE))
            media *= 2;
        if (periodo.equals(Constants.Properties.Values.Periodo.TRIMESTRE))
            media *= 3;
        if (periodo.equals(Constants.Properties.Values.Periodo.BIMESTRE))
            media *= 4;

        float quantoFalta = Float.parseFloat(df.format(media - getSomaNotas(DisCodigo)).replace(',', '.'));

        if (quantoFalta >= 0.0)
            return quantoFalta;
        else
            return (float) 0.0;

    }
}