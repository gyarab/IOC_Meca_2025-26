package Chess;

import Chess.Pieces.piece.Pieces;
import java.util.ArrayList;

public class MiniMax implements MoveStrategy {

    private final BoardEvaluator boardEvaluator;
    private final int searchDepth;
    private long numBoardsEvaluated = 0;

    public MiniMax(BoardEvaluator boardEvaluator, int searchDepth) {
        this.boardEvaluator = boardEvaluator;
        this.searchDepth = searchDepth;
    }

    // ✅ metoda požadovaná rozhraním
    @Override   
    public Chessboard.Move execute(Chessboard board) {
        
        long starTime = System.currentTimeMillis();
        
        Move calculatedBestMove = null;
        
        int highestSeenValue = Integer.MIN_VALUE;
        int lowestSeenValue = Integer.MAX_VALUE;
        int currentValue;
        
        System.out.println("computers thinks" + "depth= " + this.searchDepth);
        
        // var numberOfAllMoves = board.getBoard().getAllAvailableMoves(Pieces.color == Chesswindowpanel.BLACK);
        
        return execute(board, board.getSideToMove());
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

    @Override
    public String toString() {
        return "MiniMax{depth=" + searchDepth  + "}";
    }
    
//    @Override
//    public String toString() {
//        return "MiniMax{" +
//                "boardEvaluator=" + boardEvaluator +
//                '}';
//    }
}
