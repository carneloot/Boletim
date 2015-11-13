package view;

import control.Database;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InterfaceDisciplina extends JDialog
{
    private JPanel pnlMain;
    private JButton btnSalvar;
    private JButton btnSair;
    private JTextField txtNome;
    private JTextField txtProf;

    private ButtonHandler hdrButton = new ButtonHandler();

    private int id, modo;

    public static final int MODO_INCLUIR = 0;
    public static final int MODO_ALTERAR = 1;

    public InterfaceDisciplina(int id, int modo)
    {
        setTitle("Boletim - Disciplina");
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setContentPane(pnlMain);
        setResizable(false);
        setModal(true);

        this.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                super.windowClosing(e);
                InterfaceDisciplina.this.dispose();
            }
        });

        this.id = id;
        this.modo = modo;

        btnSair.addActionListener(hdrButton);
        btnSalvar.addActionListener(hdrButton);

        if (modo == MODO_ALTERAR)
            preencherCampos();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public InterfaceDisciplina(int modo)
    {
        this(0, modo);
    }

    private class ButtonHandler implements ActionListener
    {

        @Override
        public void actionPerformed(ActionEvent e)
        {
            if (e.getSource().equals(btnSair))
            {
                InterfaceDisciplina.this.dispose();
            }

            if (e.getSource().equals(btnSalvar))
            {

                if (modo == MODO_INCLUIR)
                {
                    String sql = String.format(
                            "INSERT INTO DISCIPLINAS VALUES (NULL,'%s','%s','S')",
                            txtNome.getText().trim(),
                            txtProf.getText().trim()
                    );

                    Database db = new Database();

                    db.execute(sql);

                    db.close();

                    InterfaceDisciplina.this.dispose();
                }

                if (modo == MODO_ALTERAR)
                {
                    String sql = String.format(
                            "UPDATE DISCIPLINAS SET DisNome = '%s', DisProfessor = '%s' WHERE DISCODIGO = %d",
                            txtNome.getText().trim(),
                            txtProf.getText().trim(),
                            id
                    );

                    Database db = new Database();

                    db.execute(sql);

                    db.close();

                    InterfaceDisciplina.this.dispose();
                }
            }
        }
    }

    private void preencherCampos()
    {
        try
        {
            Database db = new Database();

            String sql = "SELECT * FROM DISCIPLINAS WHERE DISCODIGO = " + id;

            ResultSet rs = db.query(sql);

            txtNome.setText(rs.getString("DisNome"));
            txtProf.setText(rs.getString("DisProfessor"));

            db.close();
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
}
