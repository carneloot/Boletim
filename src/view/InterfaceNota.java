package view;

import control.Constants;
import control.Database;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;


public class InterfaceNota extends JDialog
{
    private JPanel pnlMain;
    private JButton btnSalvar;
    private JButton btnCancelar;
    private JComboBox cbbDisciplina;
    private JComboBox cbbPeriodo;
    private JTextField txtNota;

    private DefaultComboBoxModel cbbmDisciplinas = new DefaultComboBoxModel();
    private DefaultComboBoxModel cbbmPeriodos = new DefaultComboBoxModel();

    private ButtonHandler hdrButton = new ButtonHandler();

    private HashMap<String, Integer> disciplinas = new HashMap<>();
    private HashMap<String, Integer> periodos = new HashMap<>();

    private int id, modo;
    private String periodo;

    public static final int MODO_INCLUIR = 0;
    public static final int MODO_ALTERAR = 1;

    public InterfaceNota(int modo, int id, String periodo)
    {
        setTitle("Boletim - Nota");
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
                InterfaceNota.this.dispose();
            }
        });

        this.id = id;
        this.modo = modo;
        this.periodo = periodo;

        preencherCampos();

        btnCancelar.addActionListener(hdrButton);
        btnSalvar.addActionListener(hdrButton);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void preencherCampos()
    {
        // Seta os elementos do ComboBox

        try
        {
            Database db = new Database();

            ResultSet rs = db.query("SELECT DISCODIGO, DISNOME FROM DISCIPLINAS ORDER BY DISNOME");

            while (rs.next())
            {
                disciplinas.put(rs.getString(2), rs.getInt(1));
                cbbmDisciplinas.addElement(rs.getString(2));
            }

            db.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        // Seta os periodos

        switch (periodo)
        {
            case Constants.Properties.Values.Periodo.SEMESTRE:
                periodos.put("1o Semestre", 1);
                periodos.put("2o Semestre", 2);

                cbbmPeriodos.addElement("1o Semestre");
                cbbmPeriodos.addElement("2o Semestre");
                break;

            case Constants.Properties.Values.Periodo.TRIMESTRE:
                periodos.put("1o Trimestre", 1);
                periodos.put("2o Trimestre", 2);
                periodos.put("3o Trimestre", 3);

                cbbmPeriodos.addElement("1o Trimestre");
                cbbmPeriodos.addElement("2o Trimestre");
                cbbmPeriodos.addElement("3o Trimestre");
                break;

            case Constants.Properties.Values.Periodo.BIMESTRE:
                periodos.put("1o Bimestre", 1);
                periodos.put("2o Bimestre", 2);
                periodos.put("3o Bimestre", 3);
                periodos.put("4o Bimestre", 4);

                cbbmPeriodos.addElement("1o Bimestre");
                cbbmPeriodos.addElement("2o Bimestre");
                cbbmPeriodos.addElement("3o Bimestre");
                cbbmPeriodos.addElement("4o Bimestre");
                break;
        }

        cbbDisciplina.setModel(cbbmDisciplinas);
        cbbPeriodo.setModel(cbbmPeriodos);

        // Caso for para alterar ele bloqueia a mudança do periodo e disciplina e seta a nota

        if (modo == MODO_ALTERAR)
        {

            try
            {
                Database db = new Database();

                ResultSet rs = db.query("SELECT NOTNOTA, NOTPERIODO, NOTDISCIPLINA FROM NOTAS WHERE NOTCODIGO = " + id);

                cbbPeriodo.setSelectedItem(getKeyFromValue(periodos, rs.getInt(2)));

                cbbDisciplina.setSelectedItem(getKeyFromValue(disciplinas, rs.getInt(3)));

                txtNota.setText(String.valueOf(rs.getFloat(1)).replace('.', ','));

                db.close();
            } catch (Exception ex)
            {
                ex.printStackTrace();
            }

            int disciplinaSelecionada = cbbDisciplina.getSelectedIndex();
            cbbDisciplina.addActionListener(e -> {
                cbbDisciplina.setSelectedIndex(disciplinaSelecionada);
            });

            int periodoSelecionado = cbbPeriodo.getSelectedIndex();
            cbbPeriodo.addActionListener(e -> {
                cbbPeriodo.setSelectedIndex(periodoSelecionado);
            });
        }
    }

    public static Object getKeyFromValue(Map hm, Object value)
    {
        for (Object o : hm.keySet())
        {
            if (hm.get(o).equals(value))
            {
                return o;
            }
        }
        return null;
    }

    private class ButtonHandler implements ActionListener
    {

        @Override
        public void actionPerformed(ActionEvent e)
        {
            if (e.getSource().equals(btnCancelar))
                InterfaceNota.this.dispose();

            if (e.getSource().equals(btnSalvar))
            {
                if (modo == MODO_INCLUIR)
                {
                    try
                    {
                        Database db = new Database();

                        String sql = String.format(
                                "INSERT INTO NOTAS VALUES(NULL, %d, %s, %d)",
                                disciplinas.get(cbbDisciplina.getSelectedItem()),
                                txtNota.getText().trim().replace(',', '.'),
                                periodos.get(cbbPeriodo.getSelectedItem())
                                );

                        db.execute(sql);

                        db.close();
                    } catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }
                }

                InterfaceNota.this.dispose();
            }
        }
    }
}
