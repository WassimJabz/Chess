package ChessGame;

public class Move {

    int rankFrom; //The rank from which the piece is moving
    int fileFrom; //The file from which the piece is moving
    int rankTo; //The rank to which the piece is moving
    int fileTo; //The file to which the piece is moving
    boolean hadMoved; //A boolean indicating if the piece had moved before this move (for undo)
    Piece capture; //The captured piece
    Piece movedPiece; //The piece that was moved
    Promotion promotion; //The piece to promote to (if a pawn is pushed to the eight rank)

    //Initializes promotion to none
    public Move(int rankFrom, int fileFrom, int rankTo, int fileTo) {
        this.rankFrom = rankFrom;
        this.fileFrom = fileFrom;
        this.rankTo = rankTo;
        this.fileTo = fileTo;
        promotion = Promotion.NONE;
    }

    //Takes a promotion in case it is a pawn move
    public Move(int rankFrom, int fileFrom, int rankTo, int fileTo, Promotion promotion) {
        this.rankFrom = rankFrom;
        this.fileFrom = fileFrom;
        this.rankTo = rankTo;
        this.fileTo = fileTo;
        this.promotion = promotion;
    }

    //Takes the capture AND promotion
    public Move(int rankFrom, int fileFrom, int rankTo, int fileTo, Piece capture, Promotion promotion) {
        this.rankFrom = rankFrom;
        this.fileFrom = fileFrom;
        this.rankTo = rankTo;
        this.fileTo = fileTo;
        this.capture = capture;
        this.promotion = promotion;
    }

    //Takes the hadMoved boolean at the end too
    public Move(int rankFrom, int fileFrom, int rankTo, int fileTo, Piece capture, Promotion promotion, boolean hadMoved) {
        this.rankFrom = rankFrom;
        this.fileFrom = fileFrom;
        this.rankTo = rankTo;
        this.fileTo = fileTo;
        this.capture = capture;
        this.promotion = promotion;
        this.hadMoved = hadMoved;
    }

    //Takes every attribute (movedPiece too)
    public Move(int rankFrom, int fileFrom, int rankTo, int fileTo, Piece capture, Promotion promotion, boolean hadMoved, Piece movedPiece) {
        this.rankFrom = rankFrom;
        this.fileFrom = fileFrom;
        this.rankTo = rankTo;
        this.fileTo = fileTo;
        this.capture = capture;
        this.promotion = promotion;
        this.hadMoved = hadMoved;
        this.movedPiece = movedPiece;
    }

    //Overriding the equals method
    public boolean equals(Object obj){
        if(obj instanceof Move){
            if(rankFrom == ((Move) obj).rankFrom && fileFrom == ((Move) obj).fileFrom
                    && rankTo == ((Move) obj).rankTo && fileTo == ((Move) obj).fileTo && promotion == ((Move) obj).promotion)
                return true;
        }
        return false;
    }

    //Getters / Setters

    public int getRankTo() { return rankTo; }
    public int getFileTo(){ return fileTo; }
    public int getRankFrom() { return rankFrom; }
    public int getFileFrom() {return fileFrom; }

}

