package view;

import control.Constants;
import control.Database;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;


public class JanelaBoletim extends JFrame
{
    private JPanel pnlMain;
    private JTable tblBoletim;
    private JButton btnRemover;
    private JButton btnAlterar;
    private JButton btnAdicionar;
    private JScrollPane sclTabela;

    private int[][] codigos;
    private ButtonHandler hdrButton = new ButtonHandler();

    private String periodo, tipo;
    private float media, pesoProva, pesoTrab, pesoCad;

    private static final int TAMANHO_COLUNA_NOTAS = 50;

    private DefaultTableModel tmdlNotas = new DefaultTableModel()
    {
        @Override
        public boolean isCellEditable(int row, int column)
        {
            return false;
        }
    };

    public JanelaBoletim()
    {
        super("Notas");
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setContentPane(pnlMain);
        setResizable(false);

        setButtonIcons();
        setProperties();
        configuraTabela();

        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                super.windowClosing(e);
                JanelaBoletim.this.dispose();
                new JanelaPrincipal();
            }

            @Override
            public void windowActivated(WindowEvent e)
            {
                super.windowActivated(e);
                preencheTabela();
            }
        });

        btnAdicionar.addActionListener(hdrButton);
        btnAlterar.addActionListener(hdrButton);
        btnRemover.addActionListener(hdrButton);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void setButtonIcons()
    {
        btnAdicionar.setText("<html>" +
                "<center>" +
                "<img width='32' height='32' src=\"" + getClass().getResource("/assets/add.png") + "\" />" +
                "<br><font size='3'>Adicionar</font>" +
                "</center>" +
                "</html>");

        btnRemover.setText("<html>" +
                "<center>" +
                "<img width='32' height='32' src=\"" + getClass().getResource("/assets/remover.png") + "\" />" +
                "<br><font size='3'>Remover</font>" +
                "</center>" +
                "</html>");

        btnAlterar.setText("<html>" +
                "<center>" +
                "<img width='32' height='32' src=\"" + getClass().getResource("/assets/change.png") + "\" />" +
                "<br><font size='3'>Alterar</font>" +
                "</center>" +
                "</html>");
    }

    public void preencheTabela()
    {

        // Deleta as linhas antes de adicionar
        if (tmdlNotas.getRowCount() != 0)
            tmdlNotas.setRowCount(0);

        // Faz a query na database e adiciona os resultados na tabela

        try
        {
            Database db = new Database();

            ResultSet rs = db.query("SELECT DISCODIGO, DISNOME FROM DISCIPLINAS ORDER BY DISNOME");

            while (rs.next())
            {
                codigos[tblBoletim.getRowCount()][0] = rs.getInt(1);

                switch (periodo)
                {
                    case Constants.Properties.Values.Periodo.SEMESTRE:
                        tmdlNotas.addRow(new Object[]{rs.getString(2), "", "", "", ""});
                        break;
                    case Constants.Properties.Values.Periodo.TRIMESTRE:
                        tmdlNotas.addRow(new Object[]{rs.getString(2), "", "", "", "", ""});
                        break;
                    case Constants.Properties.Values.Periodo.BIMESTRE:
                        tmdlNotas.addRow(new Object[]{rs.getString(2), "", "", "", "", "", ""});
                        break;
                }

            }

            rs = db.query("SELECT NOTCODIGO, NOTDISCIPLINA, NOTNOTA, NOTPERIODO FROM NOTAS");

            while (rs.next())
            {
                for (int i = 0; i < codigos.length; i++)
                {
                    if (codigos[i][0] == rs.getInt(2))
                    {
                        switch (rs.getInt(4))
                        {
                            case 1:
                                codigos[i][1] = rs.getInt(1);
                                tmdlNotas.setValueAt(rs.getFloat(3), i, 1);
                                break;

                            case 2:
                                codigos[i][2] = rs.getInt(1);
                                tmdlNotas.setValueAt(rs.getFloat(3), i, 2);
                                break;

                            case 3:
                                codigos[i][3] = rs.getInt(1);
                                tmdlNotas.setValueAt(rs.getFloat(3), i, 3);
                                break;

                            case 4:
                                codigos[i][4] = rs.getInt(1);
                                tmdlNotas.setValueAt(rs.getFloat(3), i, 4);
                                break;
                        }
                    }
                }
            }

            db.close();
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    public void configuraTabela()
    {
        // Seta os headers da tabela
        ArrayList<String> colunas = new ArrayList<>();

        colunas.add("Disciplina");

        switch (periodo)
        {
            case Constants.Properties.Values.Periodo.SEMESTRE:
                colunas.add("1o Sem");
                colunas.add("2o Sem");
                break;
            case Constants.Properties.Values.Periodo.TRIMESTRE:
                colunas.add("1o Trim");
                colunas.add("2o Trim");
                colunas.add("3o Trim");
                break;
            case Constants.Properties.Values.Periodo.BIMESTRE:
                colunas.add("1o Bim");
                colunas.add("2o Bim");
                colunas.add("3o Bim");
                colunas.add("4o Bim");
                break;
        }

        colunas.add("Media Final");
        colunas.add("Quanto falta");

        String colunasSimple[] = colunas.toArray(new String[colunas.size()]);

        tmdlNotas.setColumnIdentifiers(colunasSimple);

        // Area de configurações
        tblBoletim.setModel(tmdlNotas);
        tblBoletim.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblBoletim.setColumnSelectionAllowed(true);
        tblBoletim.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        tblBoletim.getTableHeader().setReorderingAllowed(false);
        tblBoletim.getTableHeader().setResizingAllowed(false);

        // Seta o tamanho da tabela

        int tamNotas = 0;

        tblBoletim.getColumn("Disciplina").setPreferredWidth(100);
        tamNotas += 100;

        switch (periodo)
        {
            case Constants.Properties.Values.Periodo.SEMESTRE:
                tblBoletim.getColumn("1o Sem").setPreferredWidth(TAMANHO_COLUNA_NOTAS);
                tblBoletim.getColumn("2o Sem").setPreferredWidth(TAMANHO_COLUNA_NOTAS);
                tamNotas += TAMANHO_COLUNA_NOTAS * 2;
                break;
            case Constants.Properties.Values.Periodo.TRIMESTRE:
                tblBoletim.getColumn("1o Trim").setPreferredWidth(TAMANHO_COLUNA_NOTAS);
                tblBoletim.getColumn("2o Trim").setPreferredWidth(TAMANHO_COLUNA_NOTAS);
                tblBoletim.getColumn("3o Trim").setPreferredWidth(TAMANHO_COLUNA_NOTAS);
                tamNotas += TAMANHO_COLUNA_NOTAS * 3;
                break;
            case Constants.Properties.Values.Periodo.BIMESTRE:
                tblBoletim.getColumn("1o Bim").setPreferredWidth(TAMANHO_COLUNA_NOTAS);
                tblBoletim.getColumn("2o Bim").setPreferredWidth(TAMANHO_COLUNA_NOTAS);
                tblBoletim.getColumn("3o Bim").setPreferredWidth(TAMANHO_COLUNA_NOTAS);
                tblBoletim.getColumn("4o Bim").setPreferredWidth(TAMANHO_COLUNA_NOTAS);
                tamNotas += TAMANHO_COLUNA_NOTAS * 4;
                break;
        }

        tblBoletim.getColumn("Media Final").setPreferredWidth(TAMANHO_COLUNA_NOTAS + 20);
        tamNotas += TAMANHO_COLUNA_NOTAS + 20;

        tblBoletim.getColumn("Quanto falta").setPreferredWidth(TAMANHO_COLUNA_NOTAS + 30);
        tamNotas += TAMANHO_COLUNA_NOTAS + 30;

        int numDisc = 0;
        int numCol = 0;

        try
        {
            Database db = new Database();

            ResultSet rs = db.query("SELECT COUNT(*) FROM DISCIPLINAS");

            numDisc = rs.getInt(1);

            sclTabela.setPreferredSize(new Dimension(tamNotas, 31 + (tblBoletim.getRowHeight() * numDisc)));

            db.close();
        } catch (SQLException e)
        {
            e.printStackTrace();
        }

        // Inicializa a matriz de controle

        switch (periodo)
        {
            case Constants.Properties.Values.Periodo.SEMESTRE:
                numCol += 2;
            case Constants.Properties.Values.Periodo.TRIMESTRE:
                numCol += 1;
            case Constants.Properties.Values.Periodo.BIMESTRE:
                numCol += 1;
        }

        numCol += 2;
        codigos = new int[numDisc][numCol];
    }

    private void setProperties()
    {
        Properties props = new Properties();

        try
        {
            File configFile = new File(Constants.Properties.ARQUIVO);
            InputStream is = new FileInputStream(configFile);

            props.load(is);

            is.close();

        } catch (Exception ex)
        {
            ex.printStackTrace();
        }

        periodo = props.getProperty(Constants.Properties.Keys.PERIODO);
        tipo = props.getProperty(Constants.Properties.Keys.TIPO);
        media = Float.parseFloat(props.getProperty(Constants.Properties.Keys.MEDIA));
        pesoCad = Float.parseFloat(props.getProperty(Constants.Properties.Keys.PESO_TAREFA));
        pesoProva = Float.parseFloat(props.getProperty(Constants.Properties.Keys.PESO_PROVA));
        pesoTrab = Float.parseFloat(props.getProperty(Constants.Properties.Keys.PESO_TRABALHO));
    }

    private class ButtonHandler implements ActionListener
    {

        @Override
        public void actionPerformed(ActionEvent e)
        {
            if (e.getSource().equals(btnAdicionar))
                new InterfaceNota(InterfaceNota.MODO_INCLUIR, 0, periodo);

            if (e.getSource().equals(btnRemover))
                removerNota();
        }
    }

    private void removerNota()
    {
        int opcao = JOptionPane.showConfirmDialog(null, "Tem certeza que deseja remover essa nota?", "Aviso", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (opcao == JOptionPane.YES_OPTION)
        {
            try
            {
                Database db = new Database();

                int notCodigo = codigos[tblBoletim.getSelectedRow()][tblBoletim.getSelectedColumn()];
                String sql = String.format("DELETE FROM NOTAS WHERE NOTCODIGO = %d", notCodigo);
                db.execute(sql);

                db.close();
            } catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }
}
