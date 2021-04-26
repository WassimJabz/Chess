package ChessGame;

import java.util.LinkedList;

//Constructor that calls the super constructor and initializes the name / value of the pieces
public class Queen extends Piece{

    public Queen(Board board, boolean player, int rank, int file) {

        //Initializing my instance variables
        super(board, player, rank, file);

        if(player)
            name = "Q";
        else
            name = "q";

        value = 9;

        //Placing the queen on the board if the position provided is valid (The check is done by the super constructor)
        if (this.rank != -1 && this.file != -1)
            board.setPiece(this, rank, file);
    }

    //Returns a list of all the moves the piece can achieve regardless of checks (each element is in the form rankfile in player coordinates)
    public LinkedList<Move> AllMoves() {

        LinkedList<Move> allMoves = new LinkedList<Move>();

        boolean stillEmpty = true;

        //The following moves will be considered from white's perspective (No need to have k and -k as backwards movement is possible)
        //Forward vertical movement
        for (int i = 1; i < 9 && stillEmpty; i++) {
            if ((rank + i) < 9 && board.getPiece(rank + i, file) == null){
                allMoves.add(new Move(rank, file, rank+i, file));
            }
            else if((rank + i) < 9 && board.getPiece(rank + i, file) != null && board.getPiece(rank + i, file).player != board.turn) {
                allMoves.add(new Move(rank, file, rank+i, file));
                stillEmpty = false;
            }
            else {
                stillEmpty = false;
            }
        }
        stillEmpty = true; //Reinitializing stillEmpty to true after every check

        //Backwards vertical movement
        for (int i = 1; i < 9 && stillEmpty; i++) {
            if ((rank - i) > 0 && board.getPiece(rank - i, file) == null) {
                allMoves.add(new Move(rank, file, rank-i, file));
            }
            else if((rank - i) > 0 && board.getPiece(rank - i, file) != null && board.getPiece(rank - i, file).player != board.turn) {
                allMoves.add(new Move(rank, file, rank-i, file));
                stillEmpty = false;
            }
            else stillEmpty = false;
        }
        stillEmpty = true;

        //Right horizontal movement
        for (int i = 1; i < 9 && stillEmpty; i++) {
            if ((file + i) < 9 && board.getPiece(rank, file + i) == null) {
                allMoves.add(new Move(rank, file, rank, file+i));
            }
            else if((file + i) < 9 && board.getPiece(rank, file + i) != null && board.getPiece(rank, file + i).player != board.turn) {
                allMoves.add(new Move(rank, file, rank, file+i));
                stillEmpty = false;
            }
            else stillEmpty = false;
        }
        stillEmpty = true;

        //Left horizontal movement
        for (int i = 1; i < 9 && stillEmpty; i++) {
            if ((file - i) > 0 && board.getPiece(rank, file - i) == null) {
                allMoves.add(new Move(rank, file, rank, file-i));
            }
            else if((file - i) > 0 && board.getPiece(rank, file - i) != null && board.getPiece(rank, file - i).player != board.turn) {
                allMoves.add(new Move(rank, file, rank, file-i));
                stillEmpty = false;
            }
            else stillEmpty = false;
        }
        stillEmpty = true;

        //Forward left diagonal movement
        for (int i = 1; i < 9 && stillEmpty; i++) {
            if ((rank + i) < 9 && (file - i) > 0 && board.getPiece(rank + i, file - i) == null) {
                allMoves.add(new Move(rank, file, rank+i, file-i));
            }
            else if((rank + i) < 9 && (file - i) > 0 && board.getPiece(rank + i, file - i) != null && board.getPiece(rank + i, file - i).player != board.turn) {
                allMoves.add(new Move(rank, file, rank+i, file-i));
                stillEmpty = false;
            }
            else stillEmpty = false;
        }
        stillEmpty = true;

        //Forward right diagonal movement
        for (int i = 1; i < 9 && stillEmpty; i++) {
            if ((rank + i) < 9 && (file + i) < 9 && board.getPiece(rank + i, file + i) == null) {
                allMoves.add(new Move(rank, file, rank+i, file+i));
            }
            else if((rank + i) < 9 && (file + i) < 9 && board.getPiece(rank + i, file + i) != null && board.getPiece(rank + i, file + i).player != board.turn) {
                allMoves.add(new Move(rank, file, rank+i, file+i));
                stillEmpty = false;
            }
            else stillEmpty = false;
        }
        stillEmpty = true;

        //Backwards left diagonal movement
        for (int i = 1; i < 9 && stillEmpty; i++) {
            if ((rank - i) > 0 && (file - i) > 0 && board.getPiece(rank - i, file - i) == null) {
                allMoves.add(new Move(rank, file, rank-i, file-i));
            }
            else if((rank - i) > 0 && (file - i) > 0 && board.getPiece(rank - i, file - i) != null && board.getPiece(rank - i, file - i).player != board.turn) {
                allMoves.add(new Move(rank, file, rank-i, file-i));
                stillEmpty = false;
            }
            else stillEmpty = false;
        }
        stillEmpty = true;

        //Backwards right diagonal movement
        for (int i = 1; i < 9 && stillEmpty; i++) {
            if ((rank - i) > 0 && (file + i) < 9 && board.getPiece(rank - i, file + i) == null) {
                allMoves.add(new Move(rank, file, rank-i, file+i));
            }
            else if((rank - i) > 0 && (file + i) < 9 && board.getPiece(rank - i, file + i) != null && board.getPiece(rank - i, file + i).player != board.turn) {
                allMoves.add(new Move(rank, file, rank-i, file+i));
                stillEmpty = false;
            }
            else stillEmpty = false;
        }

        return allMoves;

    }
}
