package Chess;

import Chess.Pieces.piece.Pieces;
import java.util.ArrayList;

public class MiniMax implements MoveStrategy {

    private final BoardEvaluator boardEvaluator;
    private final int searchDepth;
    private long numBoardsEvaluated = 0;

    public MiniMax(BoardEvaluator boardEvaluator, int searchDepth) {
        this.boardEvaluator = new SimpleBoardEvaluator();
        this.searchDepth = searchDepth;
    }

    // ✅ metoda požadovaná rozhraním
    @Override
    public Move execute(Chessboard board, int depth) {

        final long startTime = System.currentTimeMillis();
        
        Move bestMove = MoveFactory.getNullMove();; //because I don't know yet what is the best move and I need to create class MoveFactory in package Chess
        
        int highestSeenValue = Integer.MIN_VALUE;
        
        int lowestSeenValue = Integer.MAX_VALUE;
        
        int currentValue;
        
        System.out.println(board.currentPlayer() + " THINKING with depth = " + depth);
        
        int numMoves = board.currentPlayer().getLegalMoves().size();
        
        for(final Move move : board.currentPlayer().getLegalMoves()) {
            
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if(moveTransition.getMoveStatus().isDone()) {
                
                currentValue = board.currentPlayer().getAlliance().isWhite() ?
                        min(moveTransition.getTransitionBoard(), depth - 1) : //black player is a minimizing player
                        max(moveTransition.getTransitionBoard(), depth - 1); //white player is a maximing player
                
                
                if(board.currentPlayer().getAlliance().isWhite() && currentValue >= highestSeenValue) {
                   highestSeenValue = currentValue;
                   bestMove = move;
                } else if (board.currentPlayer().getAlliance().isBlack() && currentValue <= lowestSeenValue) {
                    lowestSeenValue = currentValue;
                    bestMove = move;
                }
                
            }
            
        }
        
        final long executionTime = System.currentTimeMillis() - startTime;
        
        return bestMove;
    }

    @Override
    public String toString() {
        return "MiniMax";
    }

    public int min(final Chessboard board, final int depth) {

        if(depth == 0){ // || game over
            return this.boardEvaluator.evaluate(board, depth);
        }
        
        int lowestSeenValue = Integer.MAX_VALUE; //imposibly big number, that I am not going to evaluate
        for(final Move move : board.currentPlayer().getLegalMoves()) {
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if(moveTransition.getMoveStatus().isDone()) { //I need to create this method in class MoveStatus
                final int currentValue = max(moveTransition.getTransitionBoard(), depth - 1); // I nedd to create this method in class MoveTransition
                if(currentValue <= lowestSeenValue) {
                    lowestSeenValue = currentValue;
                }
            }
        }
        return lowestSeenValue;
    }

    public int max(final Chessboard board, final int depth) {

         if(depth == 0){ // || game over
            return this.boardEvaluator.evaluate(board, depth);
        }
        
        int highestSeenValue = Integer.MIN_VALUE; //imposibly small number, that I am not going to evaluate
        for(final Move move : board.currentPlayer().getLegalMoves()) {
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if(moveTransition.getMoveStatus().isDone()) { //I need to create this method in class MoveStatus
                final int currentValue = min(moveTransition.getTransitionBoard(), depth - 1); // I nedd to create this method in class MoveTransition
                if(currentValue >= highestSeenValue) {
                    highestSeenValue = currentValue;
                }
            }
        }
        return highestSeenValue;
    }

    long starTime = System.currentTimeMillis();

    Move calculatedBestMove = null;

    int highestSeenValue = Integer.MIN_VALUE;
    int lowestSeenValue = Integer.MAX_VALUE;
    int currentValue;


        @Override
    public long getNumBoardsEvaluated() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    // interní verze s barvou
    public Chessboard.Move execute(Chessboard board, int color) {

        Chessboard.Move bestMove = null;
        int bestValue = (color == Chesswindowpanel.WHITE)
                ? Integer.MIN_VALUE
                : Integer.MAX_VALUE;

        for (Chessboard.Move move : board.getLegalMoves(color)) {
            board.movePiece(move);

            int value = minimax(
                    board,
                    searchDepth - 1,
                    color == Chesswindowpanel.BLACK
            );

            board.undoMove();

            if (color == Chesswindowpanel.WHITE) {
                if (value > bestValue) {
                    bestValue = value;
                    bestMove = move;
                }
            } else {
                if (value < bestValue) {
                    bestValue = value;
                    bestMove = move;
                }
            }
        }

        return bestMove;
    }

    private int minimax(Chessboard board, int depth, boolean maximizing) {
        numBoardsEvaluated++;

        if (depth == 0) {
            return board.getEvaluation();
        }

        int color = maximizing
                ? Chesswindowpanel.WHITE
                : Chesswindowpanel.BLACK;

        ArrayList<Chessboard.Move> moves = board.getLegalMoves(color);

        if (moves.isEmpty()) {
            if (board.isKingInCheck(color)) {
                return maximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;
            }
            return 0;
        }

        int best = maximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (Chessboard.Move move : moves) {
            board.movePiece(move);
            int value = minimax(board, depth - 1, !maximizing);
            board.undoMove();

            best = maximizing
                    ? Math.max(best, value)
                    : Math.min(best, value);
        }

        return best;
    }

    @Override
    public long getNumBoardsEvaluated() {
        return numBoardsEvaluated;
    }

//    @Override
//    public String toString() {
//        return "MiniMax{" +
//                "boardEvaluator=" + boardEvaluator +
//                '}';
//    }
}
