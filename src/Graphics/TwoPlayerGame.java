package Graphics;

//Imports
import ChessGame.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import static java.lang.System.exit;

public class TwoPlayerGame {

    //Instance variables
    JFrame gameFrame;
    Board board;
    boardPanel boardPanel;
    Piece lastClickedPiece;
    ArrayList<squarePanel> highlighted = new ArrayList<squarePanel>();
    final Color TILE_COLOR_LIGHT = Color.WHITE;
    final Color TILE_COLOR_DARK = Color.GRAY;
    final Dimension GAME_FRAME_SIZE = new Dimension(900, 900);
    final Dimension BOARD_PANEL_SIZE = new Dimension(800,800);
    final Dimension SQUARE_PANEL_SIZE = new Dimension(100, 100);

    public TwoPlayerGame(String fen){

        //Creating the corresponding board
        board = new Board(fen);

        //Creating the board panel
        this.boardPanel = new boardPanel();

        //Creating the GUI
        JMenuBar menuBar = createMenuBar();
        JButton button = createUndoButton();

        //Creating the JFrame
        this.gameFrame = new JFrame("Two Players");
        this.gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.gameFrame.setSize(GAME_FRAME_SIZE);

        //Adding everything to the JFrame and setting it to be visible
        this.gameFrame.add(this.boardPanel, BorderLayout.CENTER);
        this.gameFrame.setVisible(true);
        this.gameFrame.validate();
        this.gameFrame.setJMenuBar(menuBar);
        this.gameFrame.add(button, BorderLayout.SOUTH);
    }

    //Default constructor for inheritance purposes
    public TwoPlayerGame(){}

    //To exit the game
    public void exitGame(){
        exit(0);
    }

    //Method that creates the different components of the menu bar
    JMenuBar createMenuBar(){

        //Creating the menuBar
        JMenuBar menuBar = new JMenuBar();

        //Adding its different components
        menuBar.add(createExportMenu());
        menuBar.add(createVisualsMenu());

        //Returning the bar
        return menuBar;
    }

    //To create the "export" menu
    JMenu createExportMenu(){

        //Creating the export component of the menu
        JMenu exportMenu = new JMenu("Export");

        //To load the FEN of the current position
        JMenuItem loadFen = new JMenuItem("Generate FEN");
        loadFen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                    JOptionPane.showMessageDialog(null, board.generateFEN(), "FEN", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        exportMenu.add(loadFen);

        //To load the PGN of the current position
        JMenuItem loadPgn = new JMenuItem("Generate PGN");
        loadPgn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, board.generatePGN(), "PGN", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        exportMenu.add(loadPgn);

        //Returning our JMenu
        return exportMenu;
    }

    //To undo moves
    JButton createUndoButton(){
        JButton button = new JButton("Undo move");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                board.undoMove();
                boardPanel.refreshBoard();
            }
        });
        return button;
    }

    //To create the "Visuals" menu
    JMenu createVisualsMenu(){

        //Creating the visuals component of the menu
        JMenu VisualsMenu = new JMenu("Visuals");

        //To flip the board
        JMenuItem FlipBoard = new JMenuItem("Flip board");
        FlipBoard.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                flipBoard();
            }
        });
        VisualsMenu.add(FlipBoard);

        //Returning our JMenu
        return VisualsMenu;
    }

    //To flip the board
    void flipBoard(){
        boardPanel.removeAll(); //Removing everything first

        //If white is at the bottom
        if(boardPanel.orientation){
            for(int i = boardPanel.boardSquares.size()-1; i >= 0; i--){
                boardPanel.add(boardPanel.boardSquares.get(i));
            }
            boardPanel.orientation = false;
        }
        else{
            for(int i = 0; i < boardPanel.boardSquares.size(); i++){
                boardPanel.add(boardPanel.boardSquares.get(i));
            }
            boardPanel.orientation = true;
        }

        //Making the interface refresh
        boardPanel.validate();
        boardPanel.repaint();
    }

    //To implement what happens when a square is clicked
    //Outside of the nested class to facilitate inheritance complications
    void onClickPanel(squarePanel current){
        //If the move is not already highlighted, try to highlight it
        if(!highlighted.contains(current)) {
            Piece piece = board.getPiece(current.rank, current.file);
            if (piece != null && board.getTurn() == piece.getPlayer()) {
                current.highlightNew();
            }
        }

        //If that's the piece that got highlighted previously, remove the highlighting (added first to the array)
        else if(highlighted.get(0) == current){
            if(highlighted.size()>0){
                for(squarePanel square : highlighted){
                    square.setBorder(null);
                }
                highlighted.clear();
            }
            lastClickedPiece = null;
        }

        //Else, try to make the move if it is highlighted
        else{

            int k = board.getTurn()? 8 : 1;
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
            lastClickedPiece.Move(current.rank, current.file, promotion);
            boardPanel.refreshBoard();
        }
    }

    //Nested class to draw the board
    class boardPanel extends JPanel{

        //Storing the squarePanels
        ArrayList<squarePanel> boardSquares = new ArrayList<squarePanel>();
        boolean orientation = true; //False for black at the bottom / true for white at the bottom

        //Constructor (Creates 64 squares and stores them in the arraylist)
        //Notice how we traverse from the bottom left of the array to its top right as the boardArray has white at the top
        public boardPanel(){
            super(new GridLayout(8,8));
            for(int i = board.getBoardArray().length; i > 0; i--){
                for(int j = 1; j <= board.getBoardArray()[0].length; j++){
                    squarePanel square = new squarePanel(i, j, (i%2 != j%2)? TILE_COLOR_LIGHT : TILE_COLOR_DARK, this);
                    boardSquares.add(square);
                    add(square);
                }
            }
            setPreferredSize(BOARD_PANEL_SIZE);
            validate();
        }

        //To refresh the board
        //Refreshes every square on the board
        public void refreshBoard(){

            //Refreshing every square
            for(squarePanel square : boardSquares){
                square.refreshSquare();
            }

            //Highlighting the last move
            Move lastMove = board.getLastMove();
            if(lastMove != null){
                getSquare(lastMove.getRankFrom(), lastMove.getFileFrom()).drawGreenBackground();
                getSquare(lastMove.getRankTo(), lastMove.getFileTo()).drawGreenBackground();
            }

            boardPanel.validate();
            boardPanel.repaint();
        }

        //To get a specific square of the board (or null if no such square)
        public squarePanel getSquare(int rank, int file){
            for(squarePanel square : boardSquares){
                if(square.rank == rank && square.file == file) return square;
            }
            return null;
        }
    }

    //Nested class to create square objects
    class squarePanel extends JPanel{

        //Instance vars of every square
        int rank, file;
        Color color;
        boardPanel boardPanel;

        //Constructor that creates a square given rank / file / color and adds a mouseListener to that square
        public squarePanel(int rank, int file, Color color, boardPanel boardPanel){

            //Refreshing every square (with the piece on it)
            super(new GridBagLayout());
            this.rank = rank;
            this.file = file;
            this.color = color;
            this.boardPanel = boardPanel;
            refreshSquare();
            setPreferredSize(SQUARE_PANEL_SIZE);
            validate();

            //Adding the mouse listener to highlight possible moves
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    onClickPanel(squarePanel.this);

                    if(board.isCheck()){

                        squarePanel checkedSquare;

                        //Locating the king that got checked and storing it
                        if(board.getTurn()) checkedSquare = boardPanel.getSquare(board.getWhiteKing().getRank(), board.getWhiteKing().getFile());
                        else checkedSquare = boardPanel.getSquare(board.getBlackKing().getRank(), board.getBlackKing().getFile());
                        checkedSquare.setBackground(Color.RED); //Changing the color of the square

                    }

                    //The following events cannot happen simultaneously so no need to repeat ifs
                    if(board.isCheckmate()) {
                        String msg = (board.getTurn()? "Black" : "White") + " wins!";
                        JOptionPane.showMessageDialog(null, msg, "Game over", JOptionPane.INFORMATION_MESSAGE);
                    }
                    else if(board.isFiftyMoveDraw()){
                        JOptionPane.showMessageDialog(null, "Draw by the fifty move rule", "Game over", JOptionPane.INFORMATION_MESSAGE);
                    }
                    else if(board.isThreeFoldRepetition()){
                        JOptionPane.showMessageDialog(null, "Draw by the three fold repetition rule", "Game over", JOptionPane.INFORMATION_MESSAGE);
                    }
                    else if(board.isStalemate()){
                        JOptionPane.showMessageDialog(null, "Draw by the stalemate rule", "Game over", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            });
        }

        //Method that highlights the possible moves of a piece
        void highlightNew(){

            //Removing the highlighting of the previous squares
            if(highlighted.size()>0){
                for(squarePanel square : highlighted){
                    square.setBorder(null);
                }
                highlighted.clear();
            }


            //Setting this piece to be the last one clicked
            lastClickedPiece = board.getPiece(this.rank, this.file);

            //Highlighting this square first
            drawRedBorded();
            highlighted.add(this);

            //Highlighting the possible squares
            LinkedList<Move> possibleMoves = lastClickedPiece.PossibleMoves();
            for(Move move : possibleMoves){
                squarePanel square = getSquarePanel(move.getRankTo(), move.getFileTo());
                square.drawRedBorded();
                highlighted.add(square);
            }
        }

        //To light up a given square in red
        void drawRedBorded(){
            setBorder(BorderFactory.createLineBorder(Color.RED, 5));
        }

        //To light up a given square in green
        void drawGreenBackground(){
            setBackground(Color.GREEN);
        }

        //To retrieve the squarePanel of some coordinates
        squarePanel getSquarePanel(int rank, int file){
            for(squarePanel square : boardPanel.boardSquares){
                if(square.rank == rank && square.file == file) return square;
            }
            return null;
        }

        //Method that refreshes the piece placement on a given squarePanel
        void refreshSquare(){

            this.removeAll();
            setBackground(color);
            setBorder(null);
            Piece piece = board.getPiece(rank, file);

            if(piece!=null){

                String path;
                if(piece.getName().equals(piece.getName().toUpperCase())) path = piece.getName() + "W";
                else path = piece.getName().toUpperCase() + "B";

                try{
                    BufferedImage image = ImageIO.read(new File("src/Graphics/PieceIcons/" + path + ".png"));
                    add(new JLabel(new ImageIcon(image)));
                }
                catch(IOException e){
                    e.printStackTrace();
                }
            }

            //Making the square refresh
            revalidate();
            repaint();
        }
    }
}
