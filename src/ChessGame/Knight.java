package ChessGame;

import java.util.LinkedList;

public class Knight extends Piece {

    //Constructor that calls the super constructor and initializes the name / value of the pieces
    public Knight(Board board, boolean player, int rank, int file) {

        //Initializing my instance variables
        super(board, player, rank, file);

        if(player)
            name = "N";
        else
            name = "n";

        value = 3;

        //Placing the knight on the board if the position provided is valid (The check is done by the super constructor)
        if (this.rank != -1 && this.file != -1)
            board.setPiece(this, rank, file);
    }


    //Returns a list of all the moves the piece can achieve regardless of checks (each element is in the form rankfile in player coordinates)
    public LinkedList<Move> AllMoves(){

        LinkedList<Move> allMoves = new LinkedList<Move>();

        //2 up - 1 right
        if((rank+2)<9 && (file+1)<9 && (board.getPiece(rank+2, file+1) == null ||
                (board.getPiece(rank+2, file+1) != null && (board.getPiece(rank+2, file+1).player != board.turn))))
            allMoves.add(new Move(rank, file, rank+2, file+1));
        //2 up - 1 left
        if((rank+2)<9 && (file-1)>0 && (board.getPiece(rank+2, file-1) == null ||
                (board.getPiece(rank+2, file-1) != null && (board.getPiece(rank+2, file-1).player != board.turn))))
            allMoves.add(new Move(rank, file, rank+2, file-1));
        //2 down - 1 right
        if((rank-2)>0 && (file+1)<9 && (board.getPiece(rank-2, file+1) == null ||
                (board.getPiece(rank-2, file+1) != null && (board.getPiece(rank-2, file+1).player != board.turn))))
            allMoves.add(new Move(rank, file, rank-2, file+1));
        //2 down - 1 left
        if((rank-2)>0 && (file-1)>0 && (board.getPiece(rank-2, file-1) == null ||
                (board.getPiece(rank-2, file-1) != null && (board.getPiece(rank-2, file-1).player != board.turn))))
            allMoves.add(new Move(rank, file, rank-2, file-1));
        //2 right - 1 up
        if((rank+1)<9 && (file+2)<9 && (board.getPiece(rank+1, file+2) == null ||
                (board.getPiece(rank+1, file+2) != null && (board.getPiece(rank+1, file+2).player != board.turn))))
            allMoves.add(new Move(rank, file, rank+1, file+2));
        //2 right - 1 down
        if((rank-1)>0 && (file+2)<9 && (board.getPiece(rank-1, file+2) == null ||
                (board.getPiece(rank-1, file+2) != null && (board.getPiece(rank-1, file+2).player != board.turn))))
            allMoves.add(new Move(rank, file, rank-1, file+2));
        //2 left - 1 up
        if((rank+1)<9 && (file-2)>0 && (board.getPiece(rank+1, file-2) == null ||
                (board.getPiece(rank+1, file-2) != null && (board.getPiece(rank+1, file-2).player != board.turn))))
            allMoves.add(new Move(rank, file, rank+1, file-2));
        //2 left - 1 down
        if((rank-1)>0 && (file-2)>0 && (board.getPiece(rank-1, file-2) == null ||
                (board.getPiece(rank-1, file-2) != null && (board.getPiece(rank-1, file-2).player != board.turn))))
            allMoves.add(new Move(rank, file, rank-1, file-2));

        return allMoves;

    }
}
