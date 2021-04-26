package Graphics;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenu {

    //Instance variables
    JFrame mainMenuFrame;
    TwoPlayerGame currentGame;
    final Dimension MAIN_MENU_SIZE = new Dimension(800, 200);

    public MainMenu(){

        //Creating the JFrame
        mainMenuFrame = new JFrame("Main menu - Choose your mode");
        mainMenuFrame.setSize(MAIN_MENU_SIZE);

        //Two player mode button
        JButton TwoPlayers = new JButton("Two players");
        TwoPlayers.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentGame = new TwoPlayerGame("new");
            }
        });

        //Player VS Engine mode button
        JButton PlayerVsEngine = new JButton("Player VS Engine");
        PlayerVsEngine.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { ;

                int result = JOptionPane.CANCEL_OPTION;
                boolean playerSide = true; //Just initializing for the compiler

                while (result == JOptionPane.CANCEL_OPTION) {
                    JPanel panel = new JPanel();
                    panel.add(new JLabel("Which side do you want to play as?"));
                    DefaultComboBoxModel model = new DefaultComboBoxModel();
                    model.addElement("White");
                    model.addElement("Black");
                    JComboBox comboBox = new JComboBox(model);
                    panel.add(comboBox);
                    result = JOptionPane.showConfirmDialog(null, panel, "Choose side", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if(result == JOptionPane.OK_OPTION) playerSide = (comboBox.getSelectedItem().toString().equals("White")? true : false);
                }

                currentGame = new PlayerVsEngineGame("new", playerSide);
            }
        });

        //Two engine mode button
        JButton TwoEngines= new JButton("Two engines");
        TwoEngines.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentGame = new TwoEngineGame("new");
            }
        });

        //Creating a JPanel with a gridLayout that contains the buttons to add to the JFrame
        JPanel p = new JPanel(new GridLayout(1, 3));
        p.add(TwoPlayers);
        p.add(PlayerVsEngine);
        p.add(TwoEngines);
        mainMenuFrame.setContentPane(p);

        //Validating the frame
        mainMenuFrame.setVisible(true);
        mainMenuFrame.validate();
        mainMenuFrame.repaint();
    }
}
