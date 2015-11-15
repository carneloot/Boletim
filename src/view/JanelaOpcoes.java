package view;

import control.Constants;
import control.Database;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.Properties;

public class JanelaOpcoes extends JFrame
{
    private JPanel pnlMain;
    private JButton btnCancelar;
    private JButton btnSalvar;
    private JRadioButton rbtnAcumulativa;
    private JRadioButton rbtnPorcentagem;
    private JTextField txtProva;
    private JTextField txtTrabalho;
    private JTextField txtTarefas;
    private JTextField txtMedia;
    private JRadioButton rbtnSemestre;
    private JRadioButton rbtnTrimestre;
    private JRadioButton rbtnBimestre;

    private ButtonHandler hdrButton = new ButtonHandler();
    private ButtonGroup bgTipos = new ButtonGroup();
    private ButtonGroup bgPeriodo = new ButtonGroup();

    public static final int PERIODO_SEMESTRE = 1;
    public static final int PERIODO_TRIMESTRE = 2;
    public static final int PERIODO_BIMESTRE = 3;

    public JanelaOpcoes()
    {
        super("Boletim - Opcoes");
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setContentPane(pnlMain);
        setResizable(false);

        this.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                super.windowClosing(e);
                JanelaOpcoes.this.dispose();
                new JanelaPrincipal();
            }
        });

        btnSalvar.addActionListener(hdrButton);
        btnCancelar.addActionListener(hdrButton);

        rbtnSemestre.addActionListener(hdrButton);
        rbtnTrimestre.addActionListener(hdrButton);
        rbtnBimestre.addActionListener(hdrButton);

        bgTipos.add(rbtnAcumulativa);
        bgTipos.add(rbtnPorcentagem);

        bgPeriodo.add(rbtnBimestre);
        bgPeriodo.add(rbtnTrimestre);
        bgPeriodo.add(rbtnSemestre);

        preencherCampos();

        txtProva.setEnabled(false);
        txtProva.setToolTipText(Constants.Mensagens.FUNCAO_NAO_IMPLEMENTADA);

        txtTrabalho.setEnabled(false);
        txtTrabalho.setToolTipText(Constants.Mensagens.FUNCAO_NAO_IMPLEMENTADA);

        txtTarefas.setEnabled(false);
        txtTarefas.setToolTipText(Constants.Mensagens.FUNCAO_NAO_IMPLEMENTADA);

        rbtnAcumulativa.setEnabled(false);

        rbtnPorcentagem.setEnabled(false);
        rbtnPorcentagem.setToolTipText(Constants.Mensagens.FUNCAO_NAO_IMPLEMENTADA);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private class ButtonHandler implements ActionListener
    {

        @Override
        public void actionPerformed(ActionEvent e)
        {
            if (e.getSource().equals(btnCancelar))
            {
                JanelaOpcoes.this.dispose();
                new JanelaPrincipal();
            }

            if (e.getSource().equals(btnSalvar))
            {
                float tarefa,
                        prova,
                        trabalho;

                tarefa = Float.parseFloat(txtTarefas.getText().trim().replace(',', '.'));
                prova = Float.parseFloat(txtProva.getText().trim().replace(',', '.'));
                trabalho = Float.parseFloat(txtTrabalho.getText().trim().replace(',', '.'));

                float total = tarefa + prova + trabalho;

                if (total != 10)
                {
                    JOptionPane.showMessageDialog(null, Constants.Mensagens.ERRO_SOMA_PESOS, "Erro", JOptionPane.ERROR_MESSAGE);
                } else
                {
                    salvarInformacoes();
                    JanelaOpcoes.this.dispose();
                    new JanelaPrincipal();
                }
            }

            if (e.getSource().equals(rbtnTrimestre) || e.getSource().equals(rbtnBimestre) || e.getSource().equals(rbtnSemestre))
            {
                JOptionPane.showMessageDialog(null, Constants.Mensagens.MUDANCA_DE_PERIODO, "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void preencherCampos()
    {
        Properties props = new Properties();
        try
        {
            File configFile = new File(Constants.Properties.ARQUIVO);
            InputStream is = new FileInputStream(configFile);

            props.load(is);

            is.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        txtMedia.setText(props.getProperty(Constants.Properties.Keys.MEDIA).replace('.', ','));
        txtProva.setText(props.getProperty(Constants.Properties.Keys.PESO_PROVA).replace('.', ','));
        txtTarefas.setText(props.getProperty(Constants.Properties.Keys.PESO_TAREFA).replace('.', ','));
        txtTrabalho.setText(props.getProperty(Constants.Properties.Keys.PESO_TRABALHO).replace('.', ','));

        String tipo = props.getProperty(Constants.Properties.Keys.TIPO);

        switch (tipo)
        {
            case Constants.Properties.Values.Tipo.ACUMULATIVO:
                rbtnAcumulativa.setSelected(true);
                break;

            case Constants.Properties.Values.Tipo.PORCENTAGEM:
                rbtnPorcentagem.setSelected(true);
                break;
        }

        String periodo = props.getProperty(Constants.Properties.Keys.PERIODO);

        switch (periodo)
        {
            case Constants.Properties.Values.Periodo.SEMESTRE:
                rbtnSemestre.setSelected(true);
                break;

            case Constants.Properties.Values.Periodo.BIMESTRE:
                rbtnBimestre.setSelected(true);
                break;

            case Constants.Properties.Values.Periodo.TRIMESTRE:
                rbtnTrimestre.setSelected(true);
                break;
        }
    }

    private void salvarInformacoes()
    {

        Properties props = new Properties();

        props.setProperty(Constants.Properties.Keys.MEDIA, txtMedia.getText().trim().replace(',', '.'));
        props.setProperty(Constants.Properties.Keys.PESO_TAREFA, txtTarefas.getText().trim().replace(',', '.'));
        props.setProperty(Constants.Properties.Keys.PESO_PROVA, txtProva.getText().trim().replace(',', '.'));
        props.setProperty(Constants.Properties.Keys.PESO_TRABALHO, txtTrabalho.getText().trim().replace(',', '.'));

        // Salvar tipo de nota
        if (rbtnAcumulativa.isSelected())
            props.setProperty(Constants.Properties.Keys.TIPO, Constants.Properties.Values.Tipo.ACUMULATIVO);
        if (rbtnPorcentagem.isSelected())
            props.setProperty(Constants.Properties.Keys.TIPO, Constants.Properties.Values.Tipo.PORCENTAGEM);

        // Salvar Periodo

        int periodoSelecionado = 0;
        int periodoConfigurado = 0;

        if (rbtnSemestre.isSelected())
            periodoSelecionado = PERIODO_SEMESTRE;
        if (rbtnBimestre.isSelected())
            periodoSelecionado = PERIODO_BIMESTRE;
        if (rbtnTrimestre.isSelected())
            periodoSelecionado = PERIODO_TRIMESTRE;

        try
        {
            Properties props2 = new Properties();

            props2.load(new FileInputStream(new File(Constants.Properties.ARQUIVO)));

            switch (props2.getProperty(Constants.Properties.Keys.PERIODO))
            {
                case Constants.Properties.Values.Periodo.SEMESTRE:
                    periodoConfigurado = PERIODO_SEMESTRE;
                    break;

                case Constants.Properties.Values.Periodo.TRIMESTRE:
                    periodoConfigurado = PERIODO_TRIMESTRE;
                    break;

                case Constants.Properties.Values.Periodo.BIMESTRE:
                    periodoConfigurado = PERIODO_BIMESTRE;
                    break;
            }

            if (periodoSelecionado != periodoConfigurado)
            {
                Database db = new Database();

                db.execute("DELETE FROM NOTAS");

                db.close();
            }

            switch (periodoSelecionado)
            {
                case PERIODO_SEMESTRE:
                    props.setProperty(Constants.Properties.Keys.PERIODO, Constants.Properties.Values.Periodo.SEMESTRE);
                    break;

                case PERIODO_TRIMESTRE:
                    props.setProperty(Constants.Properties.Keys.PERIODO, Constants.Properties.Values.Periodo.TRIMESTRE);
                    break;

                case PERIODO_BIMESTRE:
                    props.setProperty(Constants.Properties.Keys.PERIODO, Constants.Properties.Values.Periodo.BIMESTRE);
                    break;
            }

        } catch (Exception ex)
        {
            ex.printStackTrace();
        }



        try
        {
            File configFile = new File(Constants.Properties.ARQUIVO);
            OutputStream os = new FileOutputStream(configFile);

            props.store(os, Constants.Properties.COMENTARIO);

            os.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }


    }
}
