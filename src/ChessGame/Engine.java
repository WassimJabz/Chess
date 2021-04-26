package ChessGame;
import java.util.LinkedList;

public class Engine {

    //Instance variables
    Board board; //To store a reference to the playing board
    boolean side; //To indicate which side it should play as
    int initialdepth; //The minimum depth the engine will go to
    int currentdepth; //To implement iterative deepening (the engine will go as deep as possible if it has time)
    int timeout_time_ms; //The maximum time the engine will take before returning its current best move (in milliseconds)
    long startTime; //The time at which the first maximizer was called (to keep track of the timeout time)
    boolean timeout; //To keep track of if the engine has timed out yet
    Move bestMove; // To store the last best move played in the current tree
    Move absoluteBestMove; //To store the best move of all the trees being traversed
    double objectiveEvaluation; // Stores the evaluation of the current tree being traversed
    double absoluteObjectiveEvaluation; //Stores the best evaluation of all the trees being traversed

    public Engine(Board board, int initialdepth, int timeout, boolean side){
        this.board = board;
        this.side = side;
        this.initialdepth = initialdepth;
        this.timeout_time_ms = timeout;
    }

    public Move getBestMove(){

        timeout = false; //Setting this to false at the start of every loop
        startTime = System.currentTimeMillis(); //Storing the start time
        int d = 0; //To keep track of the next current depth and go as deep as possible within time constraints

        //Will iterate as long as the timeout time was not reached
        while(!timeout){
            if(d>0){
                absoluteBestMove = bestMove; //The absolute best move is only changed when the full tree has been traversed to avoid returning a move without traversing many nodes in the new tree
                absoluteObjectiveEvaluation = objectiveEvaluation;
            }
            currentdepth = initialdepth + d;
            maximizer(currentdepth, Integer.MIN_VALUE, Integer.MAX_VALUE);
            d++;
        }

        return absoluteBestMove;
    }

    //Returns the objective evaluation rounded up to 2 decimal places
    public double getObjectiveEvaluation(){
        return absoluteObjectiveEvaluation;
    }

    private double maximizer(int depth, double alpha, double beta){

        //If there is no time left for further computation
        if(System.currentTimeMillis() - startTime > timeout_time_ms){
            timeout = true;
            return alpha;
        }

        if(depth == 0){
            return board.evaluate(side);
        }

        LinkedList<Move> legalMoves = board.fullPossibleMoves();

        for(Move move : legalMoves){

            if(board.MovePiece(move)) {

                double evaluation;

                if(board.isCheckmate() || board.isStalemate() || board.isFiftyMoveDraw() || board.isThreeFoldRepetition()){
                    evaluation = board.evaluate(side);
                }
                else{
                    evaluation = minimizer(depth - 1, alpha, beta);
                }

                board.undoMove();

                if (evaluation > alpha) {
                    alpha = evaluation;

                    //The best move and evaluation are only returned at the 1st generation
                    if (depth == currentdepth) {
                        bestMove = move;
                        objectiveEvaluation = (side ? 1 : -1) * evaluation;
                    }
                }

            }

            //Implementing alpha-beta
            if(alpha >= beta){
                return alpha; //Breaking the current loop
            }
        }

        return alpha;

    }

    private double minimizer(int depth, double alpha, double beta){

        //If there is no time left for further computation
        if(System.currentTimeMillis() - startTime > timeout_time_ms){
            timeout = true;
            return beta;
        }

        if(depth == 0){
            return board.evaluate(side);
        }

        LinkedList<Move> legalMoves = board.fullPossibleMoves();

        for(Move move : legalMoves){

            if(board.MovePiece(move)) {

                double evaluation;

                if(board.isCheckmate() || board.isStalemate() || board.isFiftyMoveDraw() || board.isThreeFoldRepetition()){
                    evaluation = board.evaluate(side);
                }
                else{
                    evaluation = maximizer(depth - 1, alpha, beta);
                }

                board.undoMove();

                if (evaluation <= beta) {
                    beta = evaluation;
                }

            }

            //Implementing alpha-beta
            if(alpha >= beta){
                return beta; //Breaking the current loop
            }
        }

        return beta;
    }

    //Getter for the side of the engine
    public boolean getSide(){
        return side;
    }

    public void setTimeout_time_ms(int n){
        timeout_time_ms = n;
    }

    public void setInitialdepth(int n){
        initialdepth = n;
    }

}
