package view;

import control.Constants;
import control.Database;
import net.sf.jasperreports.types.date.DateRangeBaseSQLEqualityClause;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Arc2D;
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
                int DisCodigo = rs.getInt(1);

                switch (periodo)
                {
                    case Constants.Properties.Values.Periodo.SEMESTRE:
                        tmdlNotas.addRow(new Object[]{
                                        DisCodigo,
                                        rs.getString(2),
                                        db.getNota(DisCodigo, 1),
                                        db.getNota(DisCodigo, 2),
                                        db.getMedia(DisCodigo),
                                        db.getQuantoFalta(DisCodigo)}
                        );
                        break;
                    case Constants.Properties.Values.Periodo.TRIMESTRE:
                        tmdlNotas.addRow(new Object[]{
                                        DisCodigo,
                                        rs.getString(2),
                                        db.getNota(DisCodigo, 1),
                                        db.getNota(DisCodigo, 2),
                                        db.getNota(DisCodigo, 3),
                                        db.getMedia(DisCodigo),
                                        db.getQuantoFalta(DisCodigo)}
                        );
                        break;
                    case Constants.Properties.Values.Periodo.BIMESTRE:
                        tmdlNotas.addRow(new Object[]{
                                        DisCodigo,
                                        rs.getString(2),
                                        db.getNota(DisCodigo, 1),
                                        db.getNota(DisCodigo, 2),
                                        db.getNota(DisCodigo, 3),
                                        db.getNota(DisCodigo, 4),
                                        db.getMedia(DisCodigo),
                                        db.getQuantoFalta(DisCodigo)}
                        );
                        break;
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

        colunas.add("DisCodigo");

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

        tblBoletim.removeColumn(tblBoletim.getColumn("DisCodigo"));

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

        try
        {
            Database db = new Database();

            ResultSet rs = db.query("SELECT COUNT(*) FROM DISCIPLINAS");

            int numDisc = rs.getInt(1);

            sclTabela.setPreferredSize(new Dimension(tamNotas, 31 + (tblBoletim.getRowHeight() * numDisc)));

            db.close();
        } catch (SQLException e)
        {
            e.printStackTrace();
        }

        // Seta as colunas de notas para alinhamento à direita

        CellRenderer cellRenderer = new CellRenderer();
//        cellRenderer.setHorizontalAlignment(SwingConstants.RIGHT);

        switch (periodo)
        {
            case Constants.Properties.Values.Periodo.SEMESTRE:
                tblBoletim.getColumn("1o Sem").setCellRenderer(cellRenderer);
                tblBoletim.getColumn("2o Sem").setCellRenderer(cellRenderer);
                break;
            case Constants.Properties.Values.Periodo.TRIMESTRE:
                tblBoletim.getColumn("1o Trim").setCellRenderer(cellRenderer);
                tblBoletim.getColumn("2o Trim").setCellRenderer(cellRenderer);
                tblBoletim.getColumn("3o Trim").setCellRenderer(cellRenderer);
                break;
            case Constants.Properties.Values.Periodo.BIMESTRE:
                tblBoletim.getColumn("1o Bim").setCellRenderer(cellRenderer);
                tblBoletim.getColumn("2o Bim").setCellRenderer(cellRenderer);
                tblBoletim.getColumn("3o Bim").setCellRenderer(cellRenderer);
                tblBoletim.getColumn("4o Bim").setCellRenderer(cellRenderer);
                break;
        }

        tblBoletim.getColumn("Media Final").setCellRenderer(cellRenderer);
        tblBoletim.getColumn("Quanto falta").setCellRenderer(cellRenderer);
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

    private void removerNota()
    {
        int opcao = JOptionPane.showConfirmDialog(null, "Tem certeza que deseja remover essa nota?", "Aviso", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (opcao == JOptionPane.YES_OPTION)
        {
            try
            {
                Database db = new Database();

                int DisCodigo = (int) tmdlNotas.getValueAt(tblBoletim.getSelectedRow(), 0);

                int notCodigo = db.getNotCodigo(DisCodigo, tblBoletim.getSelectedColumn());

                String sql = String.format("DELETE FROM NOTAS WHERE NOTCODIGO = %d", notCodigo);
                db.execute(sql);

                db.close();
            } catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }

    private class ButtonHandler implements ActionListener
    {

        @Override
        public void actionPerformed(ActionEvent e)
        {
            if (e.getSource().equals(btnAdicionar))
            {
                if (tblBoletim.getSelectedRow() == -1)
                    new InterfaceNota(InterfaceNota.MODO_INCLUIR, -1, -1, periodo);

                int NotDisciplina = (int) tmdlNotas.getValueAt(tblBoletim.getSelectedRow(), 0);
                int NotPeriodo = tblBoletim.getSelectedColumn();

                if (NotPeriodo >= tblBoletim.getColumnCount() - 2 || tblBoletim.getSelectedColumn() == 0)
                    JOptionPane.showMessageDialog(null, "Esse campo e preenchido automaticamente.", "Aviso", JOptionPane.WARNING_MESSAGE);
                else if ((Float) tblBoletim.getValueAt(tblBoletim.getSelectedRow(), tblBoletim.getSelectedColumn()) != 0.0)
                    JOptionPane.showMessageDialog(null, "Essa nota ja foi preenchida.\nPor favor, exclua a nota para poder adicionar.", "Aviso", JOptionPane.WARNING_MESSAGE);
                else
                    new InterfaceNota(InterfaceNota.MODO_INCLUIR, NotDisciplina, NotPeriodo, periodo);
            }

            if (e.getSource().equals(btnRemover))
            {
                if (tblBoletim.getSelectedColumn() >= tblBoletim.getColumnCount() - 2 || tblBoletim.getSelectedColumn() == 0)
                    JOptionPane.showMessageDialog(null, "Esse campo e preenchido automaticamente.", "Aviso", JOptionPane.WARNING_MESSAGE);
                else if ((Float) tblBoletim.getValueAt(tblBoletim.getSelectedRow(), tblBoletim.getSelectedColumn()) == 0.0)
                    JOptionPane.showMessageDialog(null, "Essa nota ainda nao foi preenchida.\nPor favor, escolha outra nota.", "Aviso", JOptionPane.WARNING_MESSAGE);
                else
                    removerNota();

            }

            if (e.getSource().equals(btnAlterar))
            {
                int NotDisciplina = (int) tmdlNotas.getValueAt(tblBoletim.getSelectedRow(), 0);
                int NotPeriodo = tblBoletim.getSelectedColumn();

                if (tblBoletim.getSelectedColumn() >= tblBoletim.getColumnCount() - 2 || tblBoletim.getSelectedColumn() == 0)
                    JOptionPane.showMessageDialog(null, "Esse campo e preenchido automaticamente.", "Aviso", JOptionPane.WARNING_MESSAGE);
                else if ((Float) tblBoletim.getValueAt(tblBoletim.getSelectedRow(), tblBoletim.getSelectedColumn()) == 0.0)
                    JOptionPane.showMessageDialog(null, "Essa nota ainda nao foi preenchida.\nPor favor, escolha outra nota.", "Aviso", JOptionPane.WARNING_MESSAGE);
                else
                    new InterfaceNota(InterfaceNota.MODO_ALTERAR, NotDisciplina, NotPeriodo, periodo);
            }
        }
    }

    private class CellRenderer extends DefaultTableCellRenderer
    {
        public CellRenderer()
        {
            setHorizontalAlignment(RIGHT);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
        {
            Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (column < tblBoletim.getColumnCount() - 2)
            {
                if ((Float) value < media && (Float) value != 0.0)
                {
                    cell.setForeground(Color.RED);
                }
                else
                {
                    cell.setForeground(Color.BLACK);
                }
            }

            return cell;
        }
    }
}
