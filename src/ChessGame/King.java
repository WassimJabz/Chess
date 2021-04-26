package ChessGame;

import java.util.LinkedList;

//Constructor that calls the super constructor and initializes the name / value of the pieces
public class King extends Piece{

    public King(Board board, boolean player, int rank, int file) {

        //Initializing my instance variables
        super(board, player, rank, file);

        if(player) {
            name = "K";
            board.whiteKing = this;
        }
        else {
            name = "k";
            board.blackKing = this;
        }

        value = 0;

        //Placing the king on the board if the position provided is valid (The check is done by the super constructor)
        if (this.rank != -1 && this.file != -1)
            board.setPiece(this, rank, file);
    }

    //Returns a list of all the moves the piece can achieve regardless of checks (each element is in the form rankfile in player coordinates)
    public LinkedList<Move> AllMoves(){

        LinkedList<Move> allMoves = new LinkedList<Move>();

        //right
        if((file+1)<9 && (board.getPiece(rank, file+1) == null ||
                (board.getPiece(rank, file+1) != null && (board.getPiece(rank, file+1).player != board.turn)))) {
            allMoves.addLast(new Move(rank, file, rank, file+1));
        }
        //left
        if((file-1)>0 && (board.getPiece(rank, file-1) == null ||
                (board.getPiece(rank, file-1) != null && (board.getPiece(rank, file-1).player != board.turn)))) {
            allMoves.addLast(new Move(rank, file, rank, file-1));
        }
        //up
        if((rank+1)<9 && (board.getPiece(rank+1, file) == null ||
                (board.getPiece(rank+1, file) != null && (board.getPiece(rank+1, file).player != board.turn)))) {
            allMoves.addLast(new Move(rank, file, rank+1, file));
        }
        //down
        if((rank-1)>0  && (board.getPiece(rank-1, file) == null ||
                (board.getPiece(rank-1, file) != null && (board.getPiece(rank-1, file).player != board.turn)))) {
            allMoves.addLast(new Move(rank, file, rank-1 , file));
        }
        //diag up-right
        if((rank+1)<9 && (file+1)<9 && (board.getPiece(rank+1, file+1) == null ||
                (board.getPiece(rank+1, file+1) != null && (board.getPiece(rank+1, file+1).player != board.turn)))) {
            allMoves.addLast(new Move(rank, file, rank+1 , file+1));
        }
        //diag up-left
        if((rank+1)<9 && (file-1)>0 && (board.getPiece(rank+1, file-1) == null ||
                (board.getPiece(rank+1, file-1) != null && (board.getPiece(rank+1, file-1).player != board.turn)))) {
            allMoves.addLast(new Move(rank, file, rank+1 , file-1));
        }
        //diag down-right
        if((rank-1)>0 && (file+1)<9 && (board.getPiece(rank-1, file+1) == null ||
                (board.getPiece(rank-1, file+1) != null && (board.getPiece(rank-1, file+1).player != board.turn)))) {
            allMoves.addLast(new Move(rank, file, rank-1 , file+1));
        }
        //diag down-left
        if((rank-1)>0 && (file-1)>0 && (board.getPiece(rank-1, file-1) == null ||
                (board.getPiece(rank-1, file-1) != null && (board.getPiece(rank-1, file-1).player != board.turn)))) {
            allMoves.addLast(new Move(rank, file, rank-1 , file-1));
        }

        //To do both players' castling at once
        int k;
        if(board.turn) k = 1;
        else k = 8;

        //Castling short
        if(!hasMoved && !board.check && board.getPiece(rank, file+1) == null && board.getPiece(rank, file+2) == null &&
                board.getPiece(rank, file+3) instanceof Rook && !board.getPiece(rank, file+3).hasMoved) {
            allMoves.addLast(new Move(rank, file, rank , file+2));
        }

        //Castling long
        if(!hasMoved && !board.check && board.getPiece(rank, file-1) == null && board.getPiece(rank, file-2) == null &&
                board.getPiece(rank, file-3)==null && board.getPiece(rank, file-4) instanceof Rook && !board.getPiece(rank, file-4).hasMoved) {
            allMoves.addLast(new Move(rank, file, rank, file - 2));
        }

        return allMoves;
    }
}

