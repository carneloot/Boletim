package view;

import control.Constants;
import control.Database;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.event.*;
import java.lang.invoke.ConstantCallSite;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.PatternSyntaxException;

public class JanelaDisciplinas extends JFrame
{
    private JPanel pnlMain;
    private JTable tblDisciplinas;
    private JButton btnAdicionar;
    private JButton btnRemover;
    private JButton btnAlterar;
    private JComboBox cbbFiltro;
    private JTextField txtFiltro;

    private DefaultTableModel tmdlDisciplinas = new DefaultTableModel()
    {
        @Override
        public boolean isCellEditable(int row, int column)
        {
            return false;
        }
    };

    private DefaultComboBoxModel cbbmFiltro = new DefaultComboBoxModel();

    private ButtonHandler hdrButton = new ButtonHandler();

    public JanelaDisciplinas()
    {
        super("Boletim - Disciplinas");
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setContentPane(pnlMain);
        setResizable(false);

        this.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowActivated(WindowEvent e)
            {
                super.windowActivated(e);
                preencheTabela();
            }

            @Override
            public void windowClosing(WindowEvent e)
            {
                super.windowClosing(e);
                JanelaDisciplinas.this.dispose();
                new JanelaPrincipal();
            }
        });

        setButtonIcons();

        btnAdicionar.addActionListener(hdrButton);
        btnRemover.addActionListener(hdrButton);
        btnAlterar.addActionListener(hdrButton);

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

    private void preencheTabela()
    {
        // Seta os headers da tabela
        String[] colunas = {"ID", "Disciplina", "Professor(a)"};
        tmdlDisciplinas.setColumnIdentifiers(colunas);

        // Area de configurações
        tblDisciplinas.setModel(tmdlDisciplinas);
        tblDisciplinas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblDisciplinas.setColumnSelectionAllowed(false);

        tblDisciplinas.getTableHeader().setReorderingAllowed(false);
        tblDisciplinas.getTableHeader().setResizingAllowed(false);

        tblDisciplinas.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tblDisciplinas.getColumn("ID").setPreferredWidth(40);
        tblDisciplinas.getColumn("Disciplina").setPreferredWidth(200);
        tblDisciplinas.getColumn("Professor(a)").setPreferredWidth(170);

        // Deleta as linhas antes de adicionar
        if (tmdlDisciplinas.getRowCount() != 0)
            tmdlDisciplinas.setRowCount(0);

        // Faz a query na database e adiciona os resultados na tabela
        try
        {
            Database db = new Database();
            String sql = "SELECT * FROM DISCIPLINAS WHERE DISATIVADO = 'S' ORDER BY DISNOME";
            ResultSet rs = db.query(sql);

            while (rs.next())
            {
                tmdlDisciplinas.addRow(new Object[]
                        {
                                rs.getString(1),
                                rs.getString(2),
                                rs.getString(3)
                        });
            }

            db.close();
        } catch (SQLException e)
        {
            e.printStackTrace();
        }

        // Seleciona a primeira linha
        if (tblDisciplinas.getRowCount() != 0)
            tblDisciplinas.setRowSelectionInterval(0, 0);

        // ----------------------------------------------------------------------------

        // Area de configuração do ComboBoxFiltro
        cbbmFiltro.removeAllElements();
        for (String coluna : colunas)
            cbbmFiltro.addElement(coluna);
        cbbFiltro.setModel(cbbmFiltro);
        cbbFiltro.setEditable(false);
        cbbFiltro.setSelectedItem("Disciplina");

        // Cria o sorter e seta ele como sorter da tabela
        TableRowSorter sorter = new TableRowSorter<DefaultTableModel>(tmdlDisciplinas);
        tblDisciplinas.setRowSorter(sorter);

        // Nao deixa as colunas sorteables

        sorter.setSortable(0, false);
        sorter.setSortable(1, false);
        sorter.setSortable(2, false);

        // Adiciona a função de filtrar enquanto escreve no txtFiltro
        txtFiltro.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyReleased(KeyEvent e)
            {
                super.keyReleased(e);

                RowFilter<DefaultTableModel, Object> rf;
                try
                {
                    int filtroSelecionado = cbbFiltro.getSelectedIndex();
                    rf = RowFilter.regexFilter("(?i)" + txtFiltro.getText(), filtroSelecionado); // (?i) significa que nao é case-sensitive
                } catch (PatternSyntaxException ex)
                {
                    return;
                }
                sorter.setRowFilter(rf);
            }
        });

    }

    private void gerarRelatorio()
    {

    }

    private class ButtonHandler implements ActionListener
    {

        @Override
        public void actionPerformed(ActionEvent e)
        {
            if (e.getSource().equals(btnAdicionar))
            {
                new InterfaceDisciplina(InterfaceDisciplina.MODO_INCLUIR);
            }

            if (e.getSource().equals(btnRemover))
            {
                int opcao = JOptionPane.showConfirmDialog(null, Constants.Mensagens.AVISO_REMOVER_DISCIPLINA, "Aviso", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

                if (opcao == JOptionPane.YES_OPTION)
                {
                    int id = Integer.parseInt(String.valueOf(tblDisciplinas.getValueAt(tblDisciplinas.getSelectedRow(), 0)));

                    Database db = new Database();

                    String sql = "UPDATE DISCIPLINAS SET DISATIVADO = 'N' WHERE DISCODIGO = " + id;

                    db.execute(sql);

                    db.close();

                }
            }

            if (e.getSource().equals(btnAlterar))
            {
                int id = Integer.parseInt(String.valueOf(tblDisciplinas.getValueAt(tblDisciplinas.getSelectedRow(), 0)));

                new InterfaceDisciplina(id, InterfaceDisciplina.MODO_ALTERAR);
            }
        }
    }
}
