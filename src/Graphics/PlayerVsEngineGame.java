package Graphics;

import ChessGame.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PlayerVsEngineGame extends TwoPlayerGame {

    //Extra instance variables
    JButton undoButton;
    boolean playerSide;
    Engine engine;
    final int INITIAL_DEPTH = 4;
    final int TIMEOUT_MS = 5000;

    public PlayerVsEngineGame(String fen, boolean playerSide){
        super(fen);
        gameFrame.setTitle("Player VS Engine");
        this.playerSide = playerSide;
        engine = new Engine(board, INITIAL_DEPTH, TIMEOUT_MS, !playerSide);
        gameFrame.add(createEngineSettings(), BorderLayout.EAST);
        if(!playerSide) flipBoard(); //flip the board if the player is black
    }

    //Overriding some methods that need some additions
    JButton createUndoButton(){
        JButton button;

        //If the player is white -> undo initially / if black -> move initially
        if(playerSide) button = new JButton("Undo move");
        else button = new JButton("Press for engine's move");

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                //If it is my turn
                if(board.getTurn() == playerSide) {
                    board.undoMove();
                    board.undoMove();
                    boardPanel.refreshBoard();
                }
                //If it is the engine's turn
                else{

                    if(board.MovePiece(engine.getBestMove())) {

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

                        undoButton.setText("Undo move");

                    }
                }
            }
        });

        undoButton = button; //Storing the button in an instance var to show when the engine is thinking
        return button;
    }

    //To create the timeout combo box
    private JComboBox createTimeoutComboBox(){

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
                if(box.getSelectedItem().equals("3s")) engine.setTimeout_time_ms(3000);
                else if(box.getSelectedItem().equals("5s")) engine.setTimeout_time_ms(5000);
                else if(box.getSelectedItem().equals("7s")) engine.setTimeout_time_ms(7000);
                else if(box.getSelectedItem().equals("9s")) engine.setTimeout_time_ms(9000);
                else if(box.getSelectedItem().equals("11s")) engine.setTimeout_time_ms(11000);
                else if(box.getSelectedItem().equals("13s")) engine.setTimeout_time_ms(13000);
                else if(box.getSelectedItem().equals("15s")) engine.setTimeout_time_ms(15000);
            }
        });
        return box;
    }

    //To create the initial depth combo box
    private JComboBox createInitialDepthComboBox(){

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
                engine.setInitialdepth(Integer.parseInt((String) box.getSelectedItem()));
            }
        });
        return box;
    }

    //To create a timeout combo box, add it to a panel, and return the panel
    JPanel createEngineSettings(){

        //Creating the panel and the constraint object
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;

        //Customizing the constraints of the boxes
        c.gridy = 0;
        panel.add(new JLabel("Engine initial depth"),c);
        c.gridy = 1;
        panel.add(createInitialDepthComboBox(), c);
        c.gridy = 2;
        panel.add(new JLabel("Engine thinking time"),c);
        c.gridy = 3;
        panel.add(createTimeoutComboBox(),c);

        //Returning the populated panel
        return panel;
    }

    void onClickPanel(TwoPlayerGame.squarePanel current){

        //If the move is not already highlighted, try to highlight it (Only works for the side of the player)
        if(!highlighted.contains(current)) {
            Piece piece = board.getPiece(current.rank, current.file);
            if (piece != null && board.getTurn() == piece.getPlayer() && board.getTurn() == playerSide) {
                current.highlightNew();
            }
        }

        //If that's the piece that got highlighted previously, remove the highlighting (added first to the array)
        else if(highlighted.get(0) == current){
            if(highlighted.size()>0){
                for(TwoPlayerGame.squarePanel square : highlighted){
                    square.setBorder(null);
                }
                highlighted.clear();
                boardPanel.revalidate();
                boardPanel.repaint();
            }
            lastClickedPiece = null;
        }

        //Else, try to make the move if it is highlighted
        else{

            highlighted.clear();

            int k = playerSide? 8 : 1;
            Promotion promotion = Promotion.NONE;
            int result = JOptionPane.CANCEL_OPTION;
            String selection = "";

            //To account for promotions
            if(lastClickedPiece instanceof Pawn && current.rank == k) {

                while (result == JOptionPane.CANCEL_OPTION) {
                    JPanel panel = new JPanel();
                    panel.add(new JLabel("Choose what to promote to:"));
                    DefaultComboBoxModel model = new DefaultComboBoxModel();
                    model.addElement("Queen");
                    model.addElement("Rook");
                    model.addElement("Bishop");
                    model.addElement("Knight");
                    JComboBox comboBox = new JComboBox(model);
                    panel.add(comboBox);
                    result = JOptionPane.showConfirmDialog(null, panel, "Promotion", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if(result == JOptionPane.OK_OPTION) selection = comboBox.getSelectedItem().toString();
                }
                if(selection.equals("Queen")) promotion = Promotion.QUEEN;
                else if (selection.equals("Rook")) promotion = Promotion.ROOK;
                else if (selection.equals("Bishop")) promotion = Promotion.BISHOP;
                else promotion = Promotion.KNIGHT;
            }

            //Making the move
            lastClickedPiece.Move(current.rank, current.file, promotion);
            boardPanel.refreshBoard();
            undoButton.setText("Press for engine's move");

        }
    }
}
