package Graphics;

import ChessGame.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TwoEngineGame extends TwoPlayerGame{

    //Extra instance variables
    Engine engineWhite;
    Engine engineBlack;

    //The initial parameters of the engines at launch
    final int INITIAL_DEPTH_WHITE = 4;
    final int TIMEOUT_MS_WHITE = 5000;
    final int INITIAL_DEPTH_BLACK = 4;
    final int TIMEOUT_MS_BLACK = 5000;

    public TwoEngineGame(String fen){

        //Creating the JFrame
        this.gameFrame = new JFrame("Two Engines");
        this.gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.gameFrame.setSize(GAME_FRAME_SIZE);

        //Creating the corresponding board and engines
        board = new Board(fen);
        engineWhite = new Engine(board, INITIAL_DEPTH_WHITE, TIMEOUT_MS_WHITE, true);
        engineBlack = new Engine(board, INITIAL_DEPTH_BLACK, TIMEOUT_MS_BLACK, false);

        //Creating the board panel
        this.boardPanel = new boardPanel();

        //Creating the GUI
        this.gameFrame.setJMenuBar(createMenuBar());
        this.gameFrame.add(createNextTurnButton(), BorderLayout.SOUTH);
        this.gameFrame.add(createEnginesSettings(), BorderLayout.EAST);

        //Setting the gameFrame to be visible
        this.gameFrame.add(this.boardPanel, BorderLayout.CENTER);
        this.gameFrame.setVisible(true);
        this.gameFrame.validate();
    }

    //The undo button here is a nextTurn button
    JButton createNextTurnButton(){
        JButton button = new JButton("Next move");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                //If it is white's turn
                if(board.getTurn()) {
                    board.MovePiece(engineWhite.getBestMove());
                }
                //If it is black's turn
                else{
                    board.MovePiece(engineBlack.getBestMove());
                }

                //Common to both
                boardPanel.refreshBoard();

                //Coloring the checked square with red
                if (board.isCheck()) {
                    squarePanel checkedSquare;
                    //Locating the king that got checked and storing it
                    if (board.getTurn())
                        checkedSquare = boardPanel.getSquare(board.getWhiteKing().getRank(), board.getWhiteKing().getFile());
                    else
                        checkedSquare = boardPanel.getSquare(board.getBlackKing().getRank(), board.getBlackKing().getFile());
                    checkedSquare.setBackground(Color.RED); //Changing the color of the square
                }
            }
        });
        return button;
    }

    //To create the 2 timeout combo boxes for the engines
    private JComboBox createTimeoutComboBoxWhite(){

        JComboBox box = new JComboBox();
        box.addItem("3s");
        box.addItem("5s");
        box.addItem("7s");
        box.addItem("9s");
        box.addItem("11s");
        box.addItem("13s");
        box.addItem("15s");
        box.setSelectedItem("5s");

        box.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(box.getSelectedItem().equals("3s")) engineWhite.setTimeout_time_ms(3000);
                else if(box.getSelectedItem().equals("5s")) engineWhite.setTimeout_time_ms(5000);
                else if(box.getSelectedItem().equals("7s")) engineWhite.setTimeout_time_ms(7000);
                else if(box.getSelectedItem().equals("9s")) engineWhite.setTimeout_time_ms(9000);
                else if(box.getSelectedItem().equals("11s")) engineWhite.setTimeout_time_ms(11000);
                else if(box.getSelectedItem().equals("13s")) engineWhite.setTimeout_time_ms(13000);
                else if(box.getSelectedItem().equals("15s")) engineWhite.setTimeout_time_ms(15000);
            }
        });
        return box;
    }

    private JComboBox createTimeoutComboBoxBlack(){

        JComboBox box = new JComboBox();
        box.addItem("3s");
        box.addItem("5s");
        box.addItem("7s");
        box.addItem("9s");
        box.addItem("11s");
        box.addItem("13s");
        box.addItem("15s");
        box.setSelectedItem("5s");

        box.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(box.getSelectedItem().equals("3s")) engineBlack.setTimeout_time_ms(3000);
                else if(box.getSelectedItem().equals("5s")) engineBlack.setTimeout_time_ms(5000);
                else if(box.getSelectedItem().equals("7s")) engineBlack.setTimeout_time_ms(7000);
                else if(box.getSelectedItem().equals("9s")) engineBlack.setTimeout_time_ms(9000);
                else if(box.getSelectedItem().equals("11s")) engineBlack.setTimeout_time_ms(11000);
                else if(box.getSelectedItem().equals("13s")) engineBlack.setTimeout_time_ms(13000);
                else if(box.getSelectedItem().equals("15s")) engineBlack.setTimeout_time_ms(15000);
            }
        });
        return box;
    }

    //To create the initial depth combo box for white
    private JComboBox createInitialDepthComboBoxWhite(){

        JComboBox box = new JComboBox();
        box.addItem("1");
        box.addItem("2");
        box.addItem("3");
        box.addItem("4");
        box.addItem("5");
        box.addItem("6");
        box.setSelectedItem("4");

        box.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                engineWhite.setInitialdepth(Integer.parseInt((String) box.getSelectedItem()));
            }
        });
        return box;
    }

    //To create the initial depth combo box for white
    private JComboBox createInitialDepthComboBoxBlack(){

        JComboBox box = new JComboBox();
        box.addItem("1");
        box.addItem("2");
        box.addItem("3");
        box.addItem("4");
        box.addItem("5");
        box.addItem("6");
        box.setSelectedItem("4");

        box.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                engineBlack.setInitialdepth(Integer.parseInt((String) box.getSelectedItem()));
            }
        });
        return box;
    }

    //To create two timeout boxes, add them to a panel, and return the panel
    JPanel createEnginesSettings(){

        //Creating the panel and the constraint object
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;

        //Customizing the constraints of the boxes
        panel.add(new JLabel("White initial depth"),c);
        c.gridy = 1;
        panel.add(createInitialDepthComboBoxWhite(), c);
        c.gridy = 2;
        panel.add(new JLabel("White thinking time"),c);
        c.gridy = 3;
        panel.add(createTimeoutComboBoxWhite(),c);
        c.gridy = 4;
        panel.add(new JLabel("Black initial depth"),c);
        c.gridy = 5;
        panel.add(createInitialDepthComboBoxBlack(), c);
        c.gridy = 6;
        panel.add(new JLabel("Black thinking time"),c);
        c.gridy = 7;
        panel.add(createTimeoutComboBoxBlack(),c);

        //Returning the populated panel
        return panel;
    }

    //Clicking square panels does nothing in this mode
    void onClickPanel(TwoPlayerGame.squarePanel current){
        return;
    }
}
