package ChessGame;

import java.util.LinkedList;

//Constructor that calls the super constructor and initializes the name / value of the pieces
public class Pawn extends Piece {


    public Pawn(Board board, boolean player, int rank, int file) {

        //Initializing my instance variables
        super(board, player, rank, file);

        if(player)
            name = "P";
        else
            name = "p";

        value = 1;

        //Placing the pawn on the board if the position provided is valid (The check is done by the super constructor)
        if (this.rank != -1 && this.file != -1)
            board.setPiece(this, rank, file);
    }

    //Returns a list of all the moves the piece can achieve regardless of checks (each element is in the form rankfile in player coordinates)
    public LinkedList<Move> AllMoves(){

        LinkedList<Move> allMoves = new LinkedList<Move>();
        int k;

        if(player) k = 1;
        else k = -1;

        //1 square forward
        if(board.getPiece(rank+k, file) == null) {

            //Case of promotion
            if ((player && rank == 7) || (!player && rank == 2)) {
                allMoves.add(new Move(rank, file, rank+k, file, Promotion.QUEEN));
                allMoves.add(new Move(rank, file, rank+k, file, Promotion.ROOK));
                allMoves.add(new Move(rank, file, rank+k, file, Promotion.BISHOP));
                allMoves.add(new Move(rank, file, rank+k, file, Promotion.KNIGHT));
            }

            else {
                //Case of no promotion -> normal move forward
                allMoves.add(new Move(rank, file, rank+k, file));
            }
        }

        //Capture diagonal 1 (white can't be on the right / black can't be on the left)
        if(file != k && file!= -8*k && board.getPiece(rank+k, file-k) != null && board.getPiece(rank+k, file-k).player != board.turn) {

            //Case of promotion
            if ((player && rank == 7) || (!player && rank == 2)) {
                allMoves.add(new Move(rank, file, rank+k, file-k, Promotion.QUEEN));
                allMoves.add(new Move(rank, file, rank+k, file-k, Promotion.ROOK));
                allMoves.add(new Move(rank, file, rank+k, file-k, Promotion.BISHOP));
                allMoves.add(new Move(rank, file, rank+k, file-k, Promotion.KNIGHT));
            }

            else {
                //Case of no promotion -> normal move forward
                allMoves.add(new Move(rank, file, rank+k, file-k));
            }
        }

        //Capture diagonal 2 (white can't be on the left / black can't be on the right)
        if(file != -k && file!= 8*k && board.getPiece(rank+k, file+k) != null && board.getPiece(rank+k, file+k).player != board.turn) {

            //Case of promotion
            if ((player && rank == 7) || (!player && rank == 2)) {
                allMoves.add(new Move(rank, file, rank+k, file+k, Promotion.QUEEN));
                allMoves.add(new Move(rank, file, rank+k, file+k, Promotion.ROOK));
                allMoves.add(new Move(rank, file, rank+k, file+k, Promotion.BISHOP));
                allMoves.add(new Move(rank, file, rank+k, file+k, Promotion.KNIGHT));
            }

            else {
                //Case of no promotion -> normal move forward
                allMoves.add(new Move(rank, file, rank+k, file+k));
            }
        }

        //Double move at the beginning
        if(!hasMoved && board.getPiece(rank+k, file) == null && board.getPiece(rank+2*k, file) == null)
            allMoves.add(new Move(rank, file, rank+2*k, file));

        //To use for the two en passant cases
        int r;
        if(player) r=5;
        else r=4;

        //En passant file extraction
        int enPassantFile;
        if(board.enPassantFile.size() != 0) {
            enPassantFile = board.enPassantFile.get(board.enPassantFile.size() - 1);
        }
        else{
            enPassantFile = 0;
        }
        //En passant to the left of the white player
        if(rank == r && file!=1 && enPassantFile != 0 && (file - enPassantFile) == 1 && board.getPiece(rank, file - 1) instanceof Pawn &&
                board.getPiece(rank+k, file-1) == null )
            allMoves.add(new Move(rank, file, rank+k, file-1));

        //En passant to the right of the white player
        if(rank == r && file!=8 && enPassantFile != 0 && (file - enPassantFile) == -1 && board.getPiece(rank, file + 1) instanceof Pawn &&
                board.getPiece(rank+k, file+1) == null)
            allMoves.add(new Move(rank, file, rank+k, file+1));

        return allMoves;
    }
}
