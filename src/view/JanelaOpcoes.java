package view;

import control.Constants;

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

        bgTipos.add(rbtnAcumulativa);
        bgTipos.add(rbtnPorcentagem);

        bgPeriodo.add(rbtnBimestre);
        bgPeriodo.add(rbtnTrimestre);
        bgPeriodo.add(rbtnSemestre);

        preencherCampos();

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
                    JOptionPane.showMessageDialog(null, "A soma dos pesos nao corresponde a 10,0.\nPor favor, corrija o erro.", "Erro", JOptionPane.ERROR_MESSAGE);
                } else
                {
                    salvarInformacoes();
                    JanelaOpcoes.this.dispose();
                    new JanelaPrincipal();
                }
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
        if (rbtnSemestre.isSelected())
            props.setProperty(Constants.Properties.Keys.PERIODO, Constants.Properties.Values.Periodo.SEMESTRE);
        if (rbtnBimestre.isSelected())
            props.setProperty(Constants.Properties.Keys.PERIODO, Constants.Properties.Values.Periodo.BIMESTRE);
        if (rbtnTrimestre.isSelected())
            props.setProperty(Constants.Properties.Keys.PERIODO, Constants.Properties.Values.Periodo.TRIMESTRE);

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
