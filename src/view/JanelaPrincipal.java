package view;

import control.Constants;
import control.Database;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Properties;

public class JanelaPrincipal extends JFrame
{
    private JPanel pnlMain;
    private JButton btnBoletim;
    private JButton btnDisciplinas;
    private JButton btnOpcoes;

    private ButtonHandler hdrButton = new ButtonHandler();

    public static void main(String[] args) throws Exception
    {
        setLookAndFeel(Constants.LOOK_AND_FEEL);

        criarPropriedades();

        new JanelaPrincipal();

    }

    public JanelaPrincipal()
    {
        super("Boletim");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setContentPane(pnlMain);
        setResizable(false);

        setButtonIcons();

        btnBoletim.addActionListener(hdrButton);
        btnDisciplinas.addActionListener(hdrButton);
        btnOpcoes.addActionListener(hdrButton);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void setButtonIcons()
    {
        btnBoletim.setText("<html>" +
                "<center>" +
                "<img width='64' height='64' src=\"" + getClass().getResource("/assets/Boletim.png") + "\" />" +
                "<br>" +
                "Boletim" +
                "</center>" +
                "</html>");

        btnDisciplinas.setText("<html>" +
                "<center>" +
                "<img width='64' height='64' src=\"" + getClass().getResource("/assets/Disciplines.png") + "\" />" +
                "<br>" +
                "Disciplinas" +
                "</center>" +
                "</html>");

        btnOpcoes.setText("<html>" +
                "<center>" +
                "<img width='20' height='20' src=\"" + getClass().getResource("/assets/Options.png") + "\" />" +
                "<br><font size='2'>Opcoes</font>" +
                "</center>" +
                "</html>");
    }

    public static void setLookAndFeel(String nameSnippet)
    {
        UIManager.LookAndFeelInfo[] plafs = UIManager.getInstalledLookAndFeels();
        String ClassName = null;
        for (UIManager.LookAndFeelInfo info : plafs)
        {
            if (info.getName().contains(nameSnippet))
            {
                ClassName = info.getClassName();
                break;
            }
        }


        try
        {
            UIManager.setLookAndFeel(ClassName);
        } catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }

    public static void criarPropriedades()
    {
        if (!new File(Constants.Properties.ARQUIVO).exists())
        {
            // Propriedades funcionais
            Properties props = new Properties();

            // Seta as propriedades padroes
            props.setProperty(Constants.Properties.Keys.TIPO, Constants.Properties.Values.Tipo.ACUMULATIVO); // Tipo pode ser acumulativo ou porcentagem
            props.setProperty(Constants.Properties.Keys.PESO_PROVA, "5.0"); //
            props.setProperty(Constants.Properties.Keys.PESO_TRABALHO, "5.0");
            props.setProperty(Constants.Properties.Keys.PESO_TAREFA, "0");
            props.setProperty(Constants.Properties.Keys.MEDIA, "6.0");
            props.setProperty(Constants.Properties.Keys.LAST_NAME, "");
            props.setProperty(Constants.Properties.Keys.PERIODO, Constants.Properties.Values.Periodo.SEMESTRE);


            // Cria o Arquivo PROPERTIES
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

    private class ButtonHandler implements ActionListener
    {

        @Override
        public void actionPerformed(ActionEvent e)
        {
            if (e.getSource().equals(btnOpcoes))
            {
                JanelaPrincipal.this.dispose();
                new JanelaOpcoes();
            }

            if (e.getSource().equals(btnDisciplinas))
            {
                JanelaPrincipal.this.dispose();
                new JanelaDisciplinas();
            }

            if (e.getSource().equals(btnBoletim))
            {
                JanelaPrincipal.this.dispose();
                new JanelaBoletim();
            }
        }
    }

}
