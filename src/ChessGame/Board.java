package ChessGame;

import java.util.HashMap;
import java.util.LinkedList;

public class Board {

    //Instance variables
    Piece[][] boardArray; //To store the pieces in their game positions
    Piece whiteKing; //To locate the white king
    Piece blackKing; //To locate the black king
    boolean turn = true; //To keep track of who's to play (White = true / Black = false)
    LinkedList<Move> moves = new LinkedList<Move>(); //To store the last move for undo
    LinkedList<Piece> promotedPawn = new LinkedList<Piece>(); //To store the promoted pawns in a list and avoid exceptions (restore them on undo)
    LinkedList<Integer> enPassantFile = new LinkedList<Integer>(); //If 0, no en passant possible; if != 0, the en passant is possible on the specified
    LinkedList<Integer> movesToDraw = new LinkedList<Integer>(); //To implement the 50 move rule while keeping track of previous counts for undo
    HashMap<String, Integer> positions = new HashMap<String, Integer>(); //To implement the 3 fold repetition rule
    boolean undoPossible = false; //To see if undoing is possible (Only possible once in a row)
    boolean check = false; //If the king of the player to play is in check
    boolean checkmate = false; //If the player to play is in checkmate
    boolean stalemate = false; //If the player to play is in stalemate
    boolean FiftyMoveDraw = false; //If the fifty move draw is reached
    boolean testMove = false; //Just to check if a move is a real move or a possible move check
    boolean threeFoldRepetition = false; //To keep track of how many times the position was repeated

    //Constructor that takes a string as input (Either new for a new game or the fen of the position
    public Board(String fen) {
        if (fen.equals("new")) {

            boardArray = new Piece[8][8];

            //Adding the pawns
            for(int i = 1; i < 9; i++){
                setPiece(new Pawn(this, true, 2, i), 2, i);
                setPiece(new Pawn(this, false, 7, i), 7, i);
            }

            //Adding the kings
            setPiece(new King(this, true, 1, 5), 1, 5);
            setPiece(new King(this, false, 8, 5), 8, 5);

            //Adding the queens
            setPiece(new Queen(this, true, 1, 4), 1, 4);
            setPiece(new Queen(this, false, 8, 4), 8, 4);

            //Adding the rooks
            setPiece(new Rook(this, true, 1, 1), 1, 1);
            setPiece(new Rook(this, false, 8, 1), 8, 1);
            setPiece(new Rook(this, true, 1, 8), 1, 8);
            setPiece(new Rook(this, false, 8, 8), 8, 8);

            //Adding the bishops
            setPiece(new Bishop(this, true, 1, 3), 1, 3);
            setPiece(new Bishop(this, false, 8, 3), 8, 3);
            setPiece(new Bishop(this, true, 1, 6), 1, 6);
            setPiece(new Bishop(this, false, 8, 6), 8, 6);

            //Adding the knights
            setPiece(new Knight(this, true, 1, 2), 1, 2);
            setPiece(new Knight(this, false, 8, 2), 8, 2);
            setPiece(new Knight(this, true, 1, 7), 1, 7);
            setPiece(new Knight(this, false, 8, 7), 8, 7);
        }

        //Adding the current position to the positions hashMap
        positions.put(generatePosition(), 1);
    }

    //Generate a PGN of the game
    public String generatePGN(){

        String PGN = "";
        int moveNumber = 1;

        for(int i = 0; i < moves.size(); i++){
            Move move = moves.get(i);
            PGN += (i%2 == 0 ? moveNumber + ". " : "") + numberToFile(move.fileFrom) + move.rankFrom + "-" + numberToFile(move.fileTo) + move.rankTo + " ";
            if(i%2 == 0) moveNumber++;
        }

        return PGN;
    }

    //Generates an fen of the position
    public String generateFEN() {

        String position = "";

        //1st part of the FEN notation: position of the pieces (PS: Start from 8th rank and first file)
        for (int i = 8; i > 0; i--) {

            int emptyCount = 0;

            for (int j = 1; j < 9; j++) {
                Piece piece = getPiece(i, j);

                if (piece != null) {
                    if (emptyCount != 0) {
                        position += emptyCount;
                        emptyCount = 0;
                    }
                    position += piece.name;
                } else {
                    emptyCount++;
                    if (j == 8) {
                        position += emptyCount;
                    }
                }
            }
            if (i != 1) position += "/";
        }

        //2nd part of the FEN notation: side to move
        if(turn) position += " w ";
        else position += " b ";


        //3rd part of the FEN notation: castling rights
        boolean castle = false;

        if(canCastle(true,true)){
            position += "K";
            castle = true;
        }
        if(canCastle(true, false)){
            position += "Q";
            castle = true;
        }
        if(canCastle(false, true)){
            position += "k";
            castle = true;
        }
        if(canCastle(false, false)){
            position += "q";
            castle = true;
        }
        if(!castle) position += "-";
        position += " ";

        //4th part of the FEN notation: en passant square
        int enpassant;
        if(enPassantFile.size()!=0) enpassant = enPassantFile.getLast();
        else enpassant = 0;

        if (enpassant != 0 && turn && ((enpassant != 8 && getPiece(5, enpassant+1) instanceof Pawn && getPiece(5, enpassant+1).player) || (enpassant != 1 && getPiece(5, enpassant-1) instanceof Pawn && getPiece(5, enpassant-1).player))){
            position += numberToFile(enpassant) + "6 ";
        }
        else if (enpassant != 0 && !turn && ((enpassant != 8 && getPiece(4, enpassant+1) instanceof Pawn && !getPiece(4, enpassant+1).player) || (enpassant != 1 && getPiece(4, enpassant-1) instanceof Pawn && !getPiece(4, enpassant-1).player))){
            position += numberToFile(enpassant) + "3 ";
        }
        else{
            position += "- ";
        }


        //5th part of the FEN notation: fifty move rule (consecutive non pawn / capture moves)
        int fiftymovedraw;
        if(movesToDraw.size()!=0) fiftymovedraw = movesToDraw.getLast();
        else fiftymovedraw = 0;
        position += fiftymovedraw + " ";

        //6th part of the FEN notation: move number
        int movenumber = moves.size() / 2 + 1;
        position += movenumber;

        //Returning the full FEN!
        return position;
    }

    //Generates a FEN of the position WITHOUT the last part about the moves to check for 3 fold repetitions
    public String generatePosition(){
        String position = "";

        //1st part of the FEN notation: position of the pieces (PS: Start from 8th rank and first file)
        for (int i = 8; i > 0; i--) {

            int emptyCount = 0;

            for (int j = 1; j < 9; j++) {
                Piece piece = getPiece(i, j);

                if (piece != null) {
                    if (emptyCount != 0) {
                        position += emptyCount;
                        emptyCount = 0;
                    }
                    position += piece.name;
                } else {
                    emptyCount++;
                    if (j == 8) {
                        position += emptyCount;
                    }
                }
            }
            if (i != 1) position += "/";
        }

        //2nd part of the FEN notation: side to move
        if(turn) position += " w ";
        else position += " b ";


        //3rd part of the FEN notation: castling rights
        boolean castle = false;

        if(canCastle(true,true)){
            position += "K";
            castle = true;
        }
        if(canCastle(true, false)){
            position += "Q";
            castle = true;
        }
        if(canCastle(false, true)){
            position += "k";
            castle = true;
        }
        if(canCastle(false, false)){
            position += "q";
            castle = true;
        }
        if(!castle) position += "-";
        position += " ";

        //4th part of the FEN notation: en passant square
        int enpassant;
        if(enPassantFile.size()!=0) enpassant = enPassantFile.getLast();
        else enpassant = 0;

        if (enpassant != 0 && turn && ((enpassant != 8 && getPiece(5, enpassant+1) instanceof Pawn && getPiece(5, enpassant+1).player) || (enpassant != 1 && getPiece(5, enpassant-1) instanceof Pawn && getPiece(5, enpassant-1).player))){
            position += numberToFile(enpassant) + "6 ";
        }
        else if (enpassant != 0 && !turn && ((enpassant != 8 && getPiece(4, enpassant+1) instanceof Pawn && !getPiece(4, enpassant+1).player) || (enpassant != 1 && getPiece(4, enpassant-1) instanceof Pawn && !getPiece(4, enpassant-1).player))){
            position += numberToFile(enpassant) + "3 ";
        }
        else{
            position += "- ";
        }
        return position;
    }

    //Converts a file number to its corresponding letter (null if invalid number)
    public String numberToFile(int number){
        switch(number){
            case 1: return "a";
            case 2: return "b";
            case 3: return "c";
            case 4: return "d";
            case 5: return "e";
            case 6: return "f";
            case 7: return "g";
            case 8: return "h";
            default: return null;
        }
    }

    //The 5 following methods are getters that return if we have a check, checkmate, stalemate, or fifty move draw
    public boolean isCheck(){
        return check;
    }

    public boolean isCheckmate() {
        return checkmate;
    }

    public boolean isStalemate() {
        return stalemate;
    }

    public boolean isFiftyMoveDraw() {
        return FiftyMoveDraw;
    }

    public boolean isThreeFoldRepetition(){
        return threeFoldRepetition;
    }

    //Other getters
    public boolean getTurn(){
        return turn;
    }

    public Piece[][] getBoardArray(){
        return this.boardArray;
    }

    //This method locates the piece to move and calls the move method from the ChessGame.Piece class on it
    //Returns a boolean value indicating if the move was successful or not
    public boolean MovePiece(int rankfrom, int filefrom, int rankto, int fileto, Promotion promotion){

        //Checking that the game didn't end to avoid continuing while the 50 move draw has been reached
        if(!(FiftyMoveDraw | checkmate | stalemate | threeFoldRepetition)) {

            //Checking that the coordinates from which we are getting the piece are valid
            if (!(rankfrom >= 1 && rankfrom <= 8 && filefrom >= 1 && filefrom <= 8))
                return false;

            Piece toMove = getPiece(rankfrom, filefrom);

            //Checking if the piece is null before calling to move (NullPointerException)
            if (toMove != null) {
                return(toMove.Move(rankto, fileto, promotion));
            } else {
                return false; //If the piece is null, return false
            }
        }

        //If the game has ended by one of the 4 reasons stated, don't allow any further moves
        else
            return false;
    }

    //Same as the above method but takes a move as input
    public boolean MovePiece(Move move){
        if(MovePiece(move.rankFrom, move.fileFrom, move.rankTo, move.fileTo, move.promotion))
            return true;
        else
            return false;
    }

    //To check if a player can castle (boardSide is true for the kingside / false otherwise)
    public boolean canCastle(boolean player, boolean boardSide){

        //Checking for white
        if(player){
            if(whiteKing.hasMoved) return false;
            else if(boardSide) {
                Piece piece = getPiece(1,8);
                if (piece instanceof Rook && !piece.hasMoved) return true;
            }
            else{
                Piece piece = getPiece(1,1);
                if (piece instanceof Rook && !piece.hasMoved) return true;
            }
        }

        //Checking for black
        else{
            if(blackKing.hasMoved) return false;
            else if(boardSide) {
                Piece piece = getPiece(8,8);
                if (piece instanceof Rook && !piece.hasMoved) return true;
            }
            else {
                Piece piece = getPiece(8, 1);
                if (piece instanceof Rook && !piece.hasMoved) return true;
            }
        }

        //If the conditions aren't met -> can't castle
        return false;
    }

    //This method undoes the last move played
    public boolean undoMove(){

        if(undoPossible) {

            //Extracting the coordinates of the last move as well as the captured piece and if it had moved
            int rankFrom = moves.getLast().rankFrom;
            int fileFrom = moves.getLast().fileFrom;
            int rankTo = moves.getLast().rankTo;
            int fileTo = moves.getLast().fileTo;
            Promotion promotion= moves.getLast().promotion;
            Piece capturedPiece = moves.getLast().capture;
            boolean moveStatus = moves.getLast().hadMoved;

            //As a test move does not add the enPassant file or the move status to the respective list
            //Also, the position undo must go first because generating an FEN uses the other characteristics
            //Consequently, we must generate the FEN before deleting anything else
            if(!testMove) {
                String position = generatePosition();
                positions.put(position, positions.get(position)-1);
                enPassantFile.removeLast();
                movesToDraw.removeLast();
            }

            //Removing the elements from the linkedLists
            moves.removeLast();

            //Fixing the end of the game conditions to their previous state
            if(checkmate && !testMove) checkmate = false;
            if(stalemate && !testMove) stalemate = false;
            if(FiftyMoveDraw && !testMove) FiftyMoveDraw = false;
            if(threeFoldRepetition && !testMove) threeFoldRepetition = false;
            if(check && !testMove) check = false;

            //If there was a promotion on the last move -> retrieve the pawn from the promotedPawn array and remove it from that array
            if(promotion != Promotion.NONE) {
                getPiece(rankTo, fileTo).removePiece();
                Piece pawn = promotedPawn.getLast();
                setPiece(pawn, rankFrom, fileFrom);
                pawn.rank = rankFrom;
                pawn.file = fileFrom;
                promotedPawn.removeLast();
            }
            else {

                //Returning the moved piece to its place
                Piece movedPiece = getPiece(rankTo, fileTo);

                //Castling case
                if (movedPiece instanceof King && Math.abs(fileFrom - fileTo) > 1) {

                    Piece Rook;
                    int rookFileFrom;

                    if (fileTo == 7) { //Short castle
                        Rook = getPiece(rankTo, 6);
                        rookFileFrom = 8;
                    } else { //Long castle
                        Rook = getPiece(rankTo, 4);
                        rookFileFrom = 1;
                    }

                    Rook.removePiece();
                    Rook.rank = rankTo;
                    Rook.file = rookFileFrom;
                    setPiece(Rook, Rook.rank, Rook.file);
                    Rook.hasMoved = false;

                }

                //En passant case (if the pawn moved diagonally but didn't capture anything ~ did not jump over a pawn)
                else if (movedPiece instanceof Pawn && Math.abs(fileTo - fileFrom) == 1 && Math.abs(rankTo - rankFrom) == 1
                        && capturedPiece == null) {

                    Pawn oldPawn = new Pawn(this, turn, rankFrom, fileTo);
                    oldPawn.hasMoved = true; //New pawn was created with the same properties as the captured old one
                    setPiece(oldPawn, rankFrom, fileTo); //It's my opponent turn and his piece was captured
                }

                movedPiece.removePiece();
                movedPiece.rank = rankFrom;
                movedPiece.file = fileFrom;
                setPiece(movedPiece, rankFrom, fileFrom);
                movedPiece.hasMoved = moveStatus;
            }

            //Returning the captured piece (if any) to its place
            if (capturedPiece != null) {
                capturedPiece.rank = rankTo;
                capturedPiece.file = fileTo;
                setPiece(capturedPiece, rankTo, fileTo);
            }

            //Returning the turn to the previous player
            nextTurn();

            //Setting the undo to not be possible
            if(moves.size() == 0)
                undoPossible = false;

            //Just removing the testMove option as the test just ended
            if(testMove) testMove = false;

            //Returning true as the undo was successful
            return true;
        }

        //Return false as an undo on move 0 is impossible
        else{
            return false;
        }
    }

    //Getters for the kings on the board
    public Piece getWhiteKing(){
        return whiteKing;
    }

    public Piece getBlackKing(){
        return blackKing;
    }

    //This method prints the board with the current positions of the pieces (stub until proper graphics are implemented)
    public void printBoard(){
        for(int i = 0; i < 8; i++){

            if(i==0) System.out.println("---------------------------------");

            for(int j = 0; j < 8; j++){
                if(j==0) System.out.print("|");
                if(boardArray[i][j] != null)
                    System.out.print(" " + boardArray[i][j].name + " |");
                else
                    System.out.print("   |");
                if(j==7) System.out.print(" " + (8-i));
            }
            System.out.println("\n---------------------------------");
            if(i==7) System.out.println("  a   b   c   d   e   f   g   h");
        }
    }

    //This method is used to check if a square is attacked or not
    public boolean isAttacked(int rank, int file){

        nextTurn(); //Passing the turn to check from the opponent's perspective

        for(int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {

                Piece piece = getPiece(i, j);

                if (piece != null && piece.player == turn) {
                    for(Move move : piece.AllMoves()){
                        if(move.rankTo == rank && move.fileTo == file) {
                            nextTurn(); // Passing back the turn
                            return true;
                        }
                    }
                }
            }
        }
        nextTurn(); // Passing back the turn
        return false;
    }

    //This method checks if the king of the player that has the turn is attacked
    //It calls the above method which passes the turn to the opponent to check from their perspective
    public boolean inCheck(){
        int kingRank = (turn? whiteKing.rank : blackKing.rank);
        int kingFile = (turn? whiteKing.file : blackKing.file);
        return isAttacked(kingRank, kingFile);
    }

    //Called by the Move method to check for checkmate / stalemate
    //To check for checkmate, make sure the player is in check and this returns true
    //To check for stalemate, make sure the player is not in check and this returns true
    public boolean NoMoves(){

        for(int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {

                Piece piece = getPiece(i, j);

                if (piece != null && piece.player == turn){
                    int moves = piece.PossibleMoves().size();
                    if(moves > 0)//If the player still has a move that can be played
                        return false;
                }
            }
        }
        return true;
    }

    //Changes the turn from white to black
    public void nextTurn(){
        turn = !turn;
    }

    //The two following methods are used to convert from game to array coordinates

    //Returns a piece from the boardArray when given the game coordinates
    public Piece getPiece(int rank, int file){
        return boardArray[8-rank][file-1];
    }

    //Sets a piece in the boardArray when given the game coordinates
    public void setPiece(Piece piece, int rank, int file){
        boardArray[8-rank][file-1] = piece;
        if(piece!=null) {
            piece.rank = rank;
            piece.file = file;
        }
    }

    //To get the last move that was played (or null if no moves were played)
    public Move getLastMove(){
        if(moves.size()>0) return moves.getLast();
        return null;
    }

    //To return the list of all the possible moves with the starting and destination squares for the engine to use
    //Just calls the possible moves method on every piece and appends the starting square to the beginning
    public LinkedList<Move> fullPossibleMoves(){

        LinkedList<Move> moves = new LinkedList<Move>();

        for(int i = 1; i < 9; i++){
            for(int j = 1; j < 9; j++){

                Piece piece = getPiece(i,j);

                if(piece != null && piece.player == turn){
                    for(Move move : piece.PossibleMoves()){
                        moves.addLast(move);
                    }
                }
            }
        }
        return moves;
    }


    //The methods that follow are all used by the engine class to evaluate the position and make a move (engine methods)

    //Returns the material difference between the two players
    public int evalMaterial() {

        int difference = 0;

        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {

                Piece piece = getPiece(i, j);

                if (piece != null) {
                    difference += ((piece.player? +1 : -1)*piece.value);
                }
            }
        }
        return difference;
    }

    //Returns the evaluation of the development of the pieces to push the engine to develop in the opening
    public int evalDevelopment(){

        if(isOpening()) {
            //To store the difference in scores
            int difference = 0;

            //Iterating over all the pieces
            for (int i = 1; i < 9; i++) {
                for (int j = 1; j < 9; j++) {

                    Piece piece = getPiece(i, j);

                    //So that the queen doesn't go out in the opening - If it is a queen and we are in the opening - or a king -> doesn't count for points
                    if (piece != null && piece.hasMoved && !(piece instanceof Pawn) && !(piece instanceof King) && !((piece instanceof Queen) && isOpening())) {
                        if (piece.player) difference++;
                        else difference--;
                    }
                }
            }
            return difference;
        }

        return 0;
    }

    //Returns the evaluation of the positioning of the pieces
    public int evalMobility(){

        //To store the difference in scores
        int difference = 0;

        //To keep track of the initial turn as we'll need to pass it around
        boolean initialTurn = turn;

        //Iterating over all the pieces
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {

                Piece piece = getPiece(i, j);

                if (piece != null && !(piece instanceof Pawn)) {

                    //We need to set the turn to the player of the piece as that's how the allMoves method works
                    if(piece.player) {
                        if (!turn) turn = true;
                        if(piece instanceof Queen && isOpening()) difference += piece.getNumMoves()/3; //Less points
                        else difference += piece.getNumMoves();
                    }
                    else {
                        if (turn) turn = false;
                        if(piece instanceof Queen && isOpening()) difference -= piece.getNumMoves()/3; //Less points
                        else difference -= piece.getNumMoves();
                    }
                }
            }
        }
        turn = initialTurn;
        return difference;
    }

    //To get the evaluation of who has more central control
    public int evalCenter(){
        int difference = 0;
        Piece piece;
        for(int i = 3; i <= 6; i++){
            for(int j = 3; j <= 6; j++){
                piece = getPiece(i,j);
                if(piece != null){
                    if(piece.player){
                        if((i==4 || i==5) && (j==4 || j==5)) difference += 2;
                        else difference += 1;
                    }
                    else{
                        if((i==4 || i==5) && (j==4 || j==5)) difference -= 2;
                        else difference -= 1;
                    }
                }
            }
        }
        return difference;
    }

    //The following set of methods is made to personalize mobility points for individual pieces

    public double evalPieces() {

        //Initializing all the difference counters
        int differenceQueen = 0;
        int differenceBishop = 0;
        int differencePawnStructure = 0;
        int differencePassedPawn = 0;
        int differenceRook = 0;
        int differenceKnight = 0;
        int differenceKing = 0;

        //Looping over all the pieces
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {

                Piece piece = getPiece(i, j);

                //If the piece is a bishop
                if (piece instanceof Bishop) {
                    if (piece.player) differenceBishop++;
                    else differenceBishop--;
                }

                //If the piece is a pawn
                else if (piece instanceof Pawn) {

                    //First the pawn structure test

                    //Until proven the opposite
                    boolean isIsolated = true;
                    boolean isDoubled = false;
                    //Isolated pawn test
                    for (int s = 1; s < 9; s++) {
                        if (j != 1 && getPiece(s, j - 1) instanceof Pawn && getPiece(s, j - 1).player == piece.player) {
                            isIsolated = false;
                            break;
                        }
                        if (j != 8 && getPiece(s, j + 1) instanceof Pawn && getPiece(s, j + 1).player == piece.player) {
                            isIsolated = false;
                            break;
                        }
                    }
                    //Doubled pawn test
                    for (int s = 1; s < 9; s++) {
                        //Start by checking that the pawn is not the same pawn I am checking
                        if (s != i && getPiece(s, j) instanceof Pawn && getPiece(s, j).player == piece.player) {
                            isDoubled = true;
                            break;
                        }
                    }
                    //The player who has an isolated / doubled pawn loses points for each attribute
                    if (isIsolated) differencePawnStructure += (piece.player ? -2 : 2);
                    if (isDoubled) differencePawnStructure += (piece.player ? -1 : 1);

                    //Now the passed pawn test

                    boolean passed = true; //True until proven false
                    Piece filepiece;
                    //Checking for a white passed pawn
                    if (piece.player) {
                        for (int s = i + 1; s < 9; s++) {
                            //Left
                            if (j != 1) {
                                filepiece = getPiece(s, j - 1);
                                if (filepiece instanceof Pawn && filepiece.player != piece.player) {
                                    passed = false;
                                    break;
                                }
                            }
                            //Right
                            if (j != 8) {
                                filepiece = getPiece(s, j + 1);
                                if (filepiece instanceof Pawn && filepiece.player != piece.player) {
                                    passed = false;
                                    break;
                                }
                            }
                            //Middle
                            filepiece = getPiece(s, j);
                            if (filepiece instanceof Pawn && filepiece.player != piece.player) {
                                passed = false;
                                break;
                            }
                        }
                        if (passed) differencePassedPawn++; //If passed, increment
                    }
                    //Checking for a black passed pawn
                    else {
                        for (int s = i - 1; s > 0; s--) {
                            //Left
                            if (j != 1) {
                                filepiece = getPiece(s, j - 1);
                                if (filepiece instanceof Pawn && filepiece.player != piece.player) {
                                    passed = false;
                                    break;
                                }
                            }
                            //Right
                            if (j != 8) {
                                filepiece = getPiece(s, j + 1);
                                if (filepiece instanceof Pawn && filepiece.player != piece.player) {
                                    passed = false;
                                    break;
                                }
                            }
                            //Middle
                            filepiece = getPiece(s, j);
                            if (filepiece instanceof Pawn && filepiece.player != piece.player) {
                                passed = false;
                                break;
                            }
                        }
                        if (passed) differencePassedPawn--;
                    }

                    //Players gain extra points for pushing passed pawns (A pawn on the 7th/2nd rank gains 2 extra pawns)
                    if (piece.player && piece.rank == 7) {
                        differencePassedPawn += 2;
                    } else if (!piece.player && piece.rank == 2) {
                        differencePassedPawn -= 2;
                    }

                }

                //If the piece is a rook
                else if (piece instanceof Rook) {

                    //Setting these to true until found false
                    boolean semiopen = true;
                    boolean open = true;

                    //Looping over all the ranks on the file of the piece to search for pawns
                    for (int s = 1; s < 9; s++) {

                        Piece filepiece = getPiece(s, j);

                        if (filepiece instanceof Pawn) {
                            //If the pawn belongs to the player -> the file is neither open nor semiopen
                            if (piece.player == filepiece.player) {
                                semiopen = false;
                            }
                            open = false; //If belongs to the other player -> only semiopen
                        }
                    }

                    if (open)
                        differenceRook += (piece.player ? 2 : -2);
                    else if (semiopen)
                        differenceRook += (piece.player ? 1 : -1);
                }

                //If the piece is a knight
                else if (piece instanceof Knight) {

                    boolean outpost = true;
                    Piece filepiece;

                    //No points awarded for knights on the rim
                    if (i != 1 && i != 8 && j != 1 && j != 8) {

                        if (piece.player) {
                            for (int s = i + 1; s < 9; s++) {
                                filepiece = getPiece(s, j - 1);
                                if (filepiece instanceof Pawn && filepiece.player != piece.player) {
                                    outpost = false;
                                    break;
                                }
                                filepiece = getPiece(s, j + 1);
                                if (filepiece instanceof Pawn && filepiece.player != piece.player) {
                                    outpost = false;
                                    break;
                                }
                            }
                            if (outpost) differenceKnight++;
                        } else {
                            for (int s = i - 1; s > 0; s--) {
                                filepiece = getPiece(s, j - 1);
                                if (filepiece instanceof Pawn && filepiece.player != piece.player) {
                                    outpost = false;
                                    break;
                                }
                                filepiece = getPiece(s, j + 1);
                                if (filepiece instanceof Pawn && filepiece.player != piece.player) {
                                    outpost = false;
                                    break;
                                }
                            }
                            if (outpost) differenceKnight--;
                        }
                    }
                }

                //If the piece is queen
                else if (piece instanceof Queen) {

                    //If we're in the endgame
                    if(isEndgame()) {
                        if (piece.rank >= 3 && piece.rank <= 6 && piece.file >= 3 && piece.file <= 6) {
                            differenceQueen += (piece.player ? 1 : -1);
                            if ((piece.rank == 4 || piece.rank == 5) && (piece.file == 4 || piece.file == 5))
                                differenceQueen += (piece.player ? 1 : -1);
                        }
                    }

                    //If we're in the opening
                    else if(isOpening()){

                        //Counting how many times the queen has been moved
                        int queenMoves = 0;
                        for(Move move : moves) if(move.movedPiece == piece) queenMoves++;

                        //If the queen was moved 3 times or more
                        if(queenMoves>=3){
                            differenceQueen += (piece.player? -queenMoves/2 : queenMoves/2); //Awarding the opponent points so that the engine avoids moving the queen too early
                        }
                    }
                }
            }
        }

        //Evaluating the kings separately as we always have pointers to where they are

        //If we are still not in the endgame
        if(!isEndgame()){

            //Checking that the kings are well shielded by pawns
            //If the king has a pawn in front of it
            if(whiteKing.rank<=2 && getPiece(whiteKing.rank+1, whiteKing.file) instanceof Pawn)
                differenceKing++;
            //If the king is on the edge of the board or has a pawn at its top left
            if(whiteKing.rank<=2 && ((whiteKing.file!=1 && getPiece(whiteKing.rank+1, whiteKing.file-1) instanceof Pawn) || (whiteKing.file == 1)))
                differenceKing++;
            //If the king is on the edge of the board or has a pawn at its top right
            if(whiteKing.rank<=2 && ((whiteKing.file!=8 && getPiece(whiteKing.rank+1, whiteKing.file+1) instanceof Pawn) || (whiteKing.file == 8)))
                differenceKing++;
            //If the king has a pawn under it
            if(blackKing.rank>=6 && getPiece(blackKing.rank-1, blackKing.file) instanceof Pawn)
                differenceKing--;
            //If the king is on the edge of the board or has a pawn at its bottom left
            if(blackKing.rank>=6 && ((blackKing.file!=1 && getPiece(blackKing.rank-1, blackKing.file-1) instanceof Pawn) || (blackKing.file == 1)))
                differenceKing--;
            //If the king is on the edge of the board or has a pawn at its bottom right
            if(blackKing.rank>=6 && ((blackKing.file!=8 && getPiece(blackKing.rank-1, blackKing.file+1) instanceof Pawn) || (blackKing.file == 8)))
                differenceKing--;

            //Checking that the kings are castled for 2 bonus points (1 bonus point for not being on files 4-5 and another for also not being on 3-6
            if(whiteKing.file != 4 && whiteKing.file != 5) {
                differenceKing += 2;
                if (whiteKing.file != 3 && whiteKing.file != 6)
                    differenceKing++;
            }
            if(blackKing.file != 4 && blackKing.file != 5) {
                differenceKing -= 2;
                if (whiteKing.file != 3 && whiteKing.file != 6)
                    differenceKing--;
            }
        }
        //If it is the endgame, we award 3 points for rank advancement to the 2nd half and 1 point for file centralization
        else{
            if(whiteKing.rank >= 4) {
                differenceKing++;
                if(whiteKing.rank > 4)
                    differenceKing+=2;
            }
            if(blackKing.rank <= 5) {
                differenceKing--;
                if(blackKing.rank < 5)
                    differenceKing-=2;
            }
            if(whiteKing.file > 2 && whiteKing.file < 8) {
                differenceKing++;
            }
            if(blackKing.file > 2 && blackKing.file < 8) {
                differenceKing--;
            }
        }

        //Weighting the piece scores and returning a unified piece score
        return (differenceQueen / 3.0
                + differenceBishop / 3.0
                + differencePawnStructure / 3.0
                + differencePassedPawn / 1.5
                + differenceRook / 2.0
                + differenceKnight / 3.0
                + differenceKing / 5.0);
    }

    //This method returns true if the position is an endgame (1 or 2 non pawn pieces left for every side) and false if it is not
    public boolean isEndgame() {

        int player1 = 0; //The number of pieces player 1 has left
        int player2 = 0; //The number of pieces player 2 has left

        //Looping over all the pieces
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                Piece piece = getPiece(i,j);
                if(piece!=null){
                    if(piece.player && !(piece instanceof Pawn) && !(piece instanceof King)) player1++;
                    else if(!piece.player && !(piece instanceof Pawn) && !(piece instanceof King)) player2++;
                }
            }
        }
        if((player1 < 3 && player2 < 3))
            return true;
        else
            return false;
    }

    //Returns true if we're still in the opening phase of the game (~ first 8 moves)
    public boolean isOpening(){
        return (moves.size() <= 16); //As every moves is a move by me and another by my opponent
    }

    //The function that calls all the other evaluation functions and returns a number
    //The evaluation is positive if the side passed is better, and negative otherwise
    public double evaluate(boolean side){

        //End of the game cases
        if(checkmate && !turn) return (side? 1 : -1)*1000;
        else if(checkmate && turn) return (side? 1 : -1)*(-1000);
        else if(stalemate) return 0;
        else if(FiftyMoveDraw) return 0;
        else if(threeFoldRepetition) return 0;

        //If the game is still going
        else
            return
                    (side ? +1 : -1)
                    *(evalMaterial()
                    + evalDevelopment() / 4.0
                    + evalMobility() / 15.0
                    + evalCenter() / 4.0
                    + evalPieces());
    }
}