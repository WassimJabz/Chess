package ChessGame;

import java.util.LinkedList;

public abstract class Piece {

    //Instance variables common to all the pieces
    Board board; //The board on which the game is played
    String name; //The name of the piece
    int value; //The game value of the piece
    int rank; //The rank in player coordinates!
    int file; //The file in player coordinates!
    boolean player; //The player the piece belongs to
    boolean hasMoved = false; //To check if a piece has already moved

    //Constructor that initializes the common instance variables to all pieces
    public Piece(Board board, boolean player, int rank, int file){

        this.board = board;
        this.player = player;

        if((rank)<=8 && (rank)>=1)
            this.rank = rank;
        else
            this.rank = -1;
        if(file<=8 && file>=1)
            this.file = file;
        else
            this.file = -1;
    }

    //Removing a piece so that hasValid position returns false on it
    public void removePiece(){
        board.setPiece(null, this.rank, this.file);
        rank=-1;
        file=-1;
    }

    //Checks if a move is legal by calling possibleMoves and then performs it (Takes the rank/file in player coordinates)
    //Called by the MovePiece method in the board class to make it easier to move pieces from the main method
    public boolean Move(int rank, int file, Promotion promotion) {

        //Checking that the coordinates provided are valid
        if (!(rank >= 1 && rank <= 8 && file >= 1 && file <= 8))
            return false;

        //Creating the move to search for
        Move move = new Move(this.rank, this.file, rank, file, promotion);

        LinkedList<Move> legalMoves = PossibleMoves();

        int index = legalMoves.indexOf(move);

        //If the move provided is not a legal move (not possible or not the player's turn), return false
        if (index == -1 || player != board.turn)
            return false;

        else {

            //To facilitate coding the promotion
            boolean promote = false;

            //Store the move for undo
            board.moves.addLast(new Move(this.rank, this.file, rank, file, board.getPiece(rank, file), promotion, this.hasMoved, this));
            if(promotion!=Promotion.NONE) // Only gets stored and retrieved in case of a promotion
                board.promotedPawn.addLast(this);

            //Set the undo to be possible if it is the first move of the game
            if(!board.undoPossible) board.undoPossible = true;

            //Perform the move

            //If the move is castling, we also need to move the rook
            if(board.getPiece(this.rank, this.file) instanceof King && Math.abs(file-this.file)>1) {

                Piece rook;

                //Short castle to the right
                if (file == 7) {
                    rook = board.getPiece(rank, 8);
                    rook.removePiece();
                    rook.rank = rank;
                    rook.file = 6;
                }
                //Long castle to the left
                else {
                    rook = board.getPiece(rank, 1);
                    rook.removePiece();
                    rook.rank = rank;
                    rook.file = 4;
                }

                board.setPiece(rook, rook.rank, rook.file);
                rook.hasMoved = true;

            }
            //If the move is en passant, we need to remove the old pawn
            else if(board.getPiece(this.rank, this.file) instanceof Pawn && Math.abs(file - this.file) == 1 && Math.abs(rank-this.rank)==1
                    && board.getPiece(rank,file) == null) {
                Piece capturedpawn = board.getPiece(rank+ (board.turn? -1 : 1), file);
                capturedpawn.removePiece();
            }
            //If the move is a promotion, we need to add the chosen piece
            else if(promotion != Promotion.NONE) {

                //Removing the destination piece
                if (board.getPiece(rank, file) != null) board.getPiece(rank, file).removePiece();

                //Promoting
                if (promotion == Promotion.QUEEN)
                    board.setPiece(new Queen(board, player, rank, file), rank, file);

                else if(promotion == Promotion.ROOK)
                    board.setPiece(new Rook(board, player, rank, file), rank, file);

                else if(promotion == Promotion.BISHOP)
                    board.setPiece(new Bishop(board, player, rank, file), rank, file);

                else
                    board.setPiece(new Knight(board, player, rank, file), rank, file);

                //Setting the piece to have moved
                board.getPiece(rank, file).hasMoved = true;
                promote = true;
            }

            //If the move is a double pawn move -> set en passant to be possible for the next turn
            if(board.getPiece(this.rank, this.file) instanceof Pawn && Math.abs(rank-this.rank)>1) {
                board.enPassantFile.add(this.file);
            }
            //Else, set it to be impossible for the next turn
            else{
                board.enPassantFile.add(0); //Setting en passant to be impossible (Only can be done the move following the double thrust)
            }
            
            board.getPiece(this.rank, this.file).removePiece(); //Remove the piece from its location

            //The following snippet applies to all situations except if there is a promotion
            if(!promote) {
                if (board.getPiece(rank, file) != null) //If there is a capture
                    board.getPiece(rank, file).removePiece(); //Setting the coordinates of the removed piece to -1 (not valid anymore)

                this.rank = rank; //Changing the coordinates of the moved piece
                this.file = file;
                board.setPiece(this, rank, file); //Adding the new piece to our board
            }

            if (!hasMoved) hasMoved = true; //Set that the piece has moved (no more castling...)

            //Check if I need to increment the 50 move rule or reset it to 0 (No captures / ChessGame.Pawn push in 50 moves)
            if(!(this instanceof Pawn) && board.moves.getLast().capture == null) {
                if(board.movesToDraw.size()>0) board.movesToDraw.add(board.movesToDraw.getLast()+1);
                else
                    board.movesToDraw.add(1);
            }
            else
                board.movesToDraw.add(0);

            board.nextTurn(); // Passing the opponent the turn to check if his king is attacked now

            //Storing the position to account for the 3 fold repetition rule
            String position = board.generatePosition();
            Integer i = board.positions.get(position);

            if(i!=null){
                board.positions.put(position, ++i); //If the position was reached before
                if(i == 3) board.threeFoldRepetition = true; //i was already incremented so if 3, this is the 3rd time the position is reached
            }
            else board.positions.put(position, 1); //If it wasn't reached before

            //If the opponent is in check following the move
            if (board.inCheck()) {

                board.check = true;

                //If in check and king has no moves -> checkmate
                if (board.NoMoves())
                    board.checkmate = true;
            }
            //If not in check
            else{

                board.check = false;

                //If king has no moves -> stalemate
                if (board.NoMoves())
                    board.stalemate = true;
            }

            //Check if 50 move rule has been reached
            if(board.movesToDraw.getLast() == 100){
                board.FiftyMoveDraw = true;
            }

            return true;
        }
    }

    //Tries the all the moves in the allMoves method and only returns the ones that don't lead to check
    public LinkedList<Move> PossibleMoves(){

        LinkedList<Move> allMoves = AllMoves();
        LinkedList<Move> possibleMoves = new LinkedList<Move>();

        //Loop over all the moves that are possible regardless of check
        for (Move i : allMoves) {

            //If the move results in the king not being in check, it is legal
            if(tryMoveLegal(i.rankTo, i.fileTo, i.promotion))
                possibleMoves.add(i);
        }

        //Return all the moves that are legal and don't result in a check to the king
        return possibleMoves;
    }

    //To try moves and see if they can block check or lead to the king being attacked (Illegal)
    //The parameters are the rank and the file the piece wants to move to (Will be part of AllMoves)
    //Helper method for PossibleMoves // Not to be used separately (PMs checks for the availability)
    //Returns true if the move is legal and false if it is not legal
    private boolean tryMoveLegal(int rank, int file, Promotion promotion){

        //Can't move there if my piece is already there
        Piece target = board.getPiece(rank, file);
        if(target != null && target.player == this.player) return false;

        //Can't castle if in check
        if(board.getPiece(this.rank, this.file) instanceof King && Math.abs(file-this.file)>1 && board.check)
            return false;

        //Setting the testMove instance variable of board to true so that we know it is not an actual move
        board.testMove = true;

        //Store the move for undo
        board.moves.add(new Move(this.rank, this.file, rank, file, board.getPiece(rank, file), promotion, hasMoved));
        if(promotion!=Promotion.NONE) // Only gets stored and retrieved in case of a promotion
            board.promotedPawn.addLast(this);

        //Set the undo to be possible if it is the first move of the game
        if(!board.undoPossible) board.undoPossible = true;


        //Perform the move

        //If the move is castling, we also need to move the rook
        //If the move is en passant, we need to remove the pawn
        //If the move is a promotion, we need to add the chosen piece

        boolean castle = false;
        boolean promote = false;

        //Castle
        if(board.getPiece(this.rank, this.file) instanceof King && Math.abs(file-this.file)>1) {

            castle = true;
            Piece rook;

            //Short castle to the right
            if (file == 7) {
                rook = board.getPiece(rank, 8);
                rook.removePiece();
                rook.rank = rank;
                rook.file = 6;
            }
            //Long castle to the left
            else {
                rook = board.getPiece(rank, 1);
                rook.removePiece();
                rook.rank = rank;
                rook.file = 4;
            }

            board.setPiece(rook, rook.rank, rook.file); //Setting the rook on the board
            rook.hasMoved = true;
        }

        //En passant
        else if(board.getPiece(this.rank, this.file) instanceof Pawn && Math.abs(file - this.file) == 1 && Math.abs(rank-this.rank)==1
                && board.getPiece(rank,file) == null) {

            Piece capturedpawn = board.getPiece(rank + (board.turn? -1 : 1), file); //If white is playing, remove 1 // If black is playing, add 1
            capturedpawn.removePiece();

        }

        //If the move is a promotion, we need to add the chosen piece
        else if(promotion != Promotion.NONE) {

            if (promotion == Promotion.QUEEN){
                if (board.getPiece(rank, file) != null) board.getPiece(rank, file).removePiece();
                board.setPiece(new Queen(board, player, rank, file), rank, file);
            }
            else if(promotion == Promotion.ROOK) {
                if (board.getPiece(rank, file) != null) board.getPiece(rank, file).removePiece();
                board.setPiece(new Rook(board, player, rank, file), rank, file);
            }
            else if(promotion == Promotion.BISHOP) {
                if (board.getPiece(rank, file) != null) board.getPiece(rank, file).removePiece();
                board.setPiece(new Bishop(board, player, rank, file), rank, file);
            }
            else {
                if (board.getPiece(rank, file) != null) board.getPiece(rank, file).removePiece();
                board.setPiece(new Knight(board, player, rank, file), rank, file);
            }
            board.getPiece(rank, file).hasMoved = true;
            promote = true;
        }

        board.getPiece(this.rank, this.file).removePiece(); //Remove the piece from its location

        //Only happen if no promotion
        if(!promote) {
            if (board.getPiece(rank, file) != null) //If there is a capture
                board.getPiece(rank, file).removePiece(); //Setting the coordinates of the removed piece to -1 (not valid anymore)
            this.rank = rank; //Changing the coordinates of the moved piece
            this.file = file;
            board.setPiece(this, rank, file); //Adding the new piece to our board
        }

        if (!hasMoved) hasMoved = true; //Set that the piece has moved (no more castling...)

        //If the king is in check -> Illegal
        //If the rook is attacked, the king went through check -> Illegal
        if(board.inCheck() || (castle && board.isAttacked(rank, (file==7? 6 : 4)))){
            board.nextTurn(); //Pass the turn as undo will pass it back
            board.undoMove(); //Undo the last move I just performed
            return false; //Return false as the move isn't legal
        }

        //If both conditions are fullfilled
        else {
            board.nextTurn(); //Pass the turn as undo will pass it back
            board.undoMove(); //Undo the last move I just performed
            return true; //Return true as the move is legal
        }
    }

    //To get the number of moves a piece can make
    public int getNumMoves(){
        return AllMoves().size();
    }

    //Getters

    public String getName(){
        return this.name;
    }

    public boolean getPlayer(){
        return player;
    }

    public int getRank(){
        return rank;
    }

    public int getFile(){
        return file;
    }

    //Common to all subclasses
    //Returns all the moves a piece can make (disregards check)
    public abstract LinkedList<Move> AllMoves();

}
