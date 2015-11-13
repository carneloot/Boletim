package control;

import java.sql.*;

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
}
