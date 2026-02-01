package Chess;

import Chess.Pieces.piece.Pieces;
import java.util.ArrayList;


public class MiniMax implements MoveStrategy {

    private final BoardEvaluator boardEvaluator;
    private int searchDepth;
    private long numBoardsEvaluated = 0;

    public MiniMax(BoardEvaluator boardEvaluator, int searchDepth) {
        this.boardEvaluator = boardEvaluator;
        this.searchDepth = searchDepth;
    }

    // Spustí Minimax a vrátí nejlepší tah
    public Chessboard.Move execute(Chessboard board, int color) {
        long startTime = System.currentTimeMillis();
        Chessboard.Move bestMove = null;
        int bestValue = (color == Chesswindowpanel.WHITE) ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        ArrayList<Chessboard.Move> moves = board.getLegalMoves(color);

        for (Chessboard.Move move : moves) {
            board.movePiece(move);                 // simulace tahu
            int currentValue = minimax(board, searchDepth - 1, color == Chesswindowpanel.BLACK);
            board.undoMove();                      // vrácení tahu

            if (color == Chesswindowpanel.WHITE) { // Maximalizace
                if (currentValue > bestValue) {
                    bestValue = currentValue;
                    bestMove = move;
                }
            } else { // Minimalizace pro černého
                if (currentValue < bestValue) {
                    bestValue = currentValue;
                    bestMove = move;
                }
            }
        }

        long elapsed = System.currentTimeMillis() - startTime;
        System.out.println("MiniMax calculation time: " + elapsed + "ms, bestValue=" + bestValue);
        return bestMove;
    }

    // Rekurzivní Minimax
    private int minimax(Chessboard board, int depth, boolean isMaximizingPlayer) {
        numBoardsEvaluated++;

        if (depth == 0) {
            // Hodnota pozice z pohledu bílého
            int eval = boardEvaluator.evaluate(board, Chesswindowpanel.WHITE);
            return isMaximizingPlayer ? eval : -eval;
        }

        int currentColor = isMaximizingPlayer ? Chesswindowpanel.WHITE : Chesswindowpanel.BLACK;
        ArrayList<Chessboard.Move> moves = board.getLegalMoves(currentColor);

        if (moves.isEmpty()) { 
            // Pat nebo mat
            return board.isKingInCheck(currentColor) ? (isMaximizingPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE) : 0;
        }

        int bestValue = isMaximizingPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (Chessboard.Move move : moves) {
            board.movePiece(move);
            int value = minimax(board, depth - 1, !isMaximizingPlayer);
            board.undoMove();
            if (isMaximizingPlayer) {
                bestValue = Math.max(bestValue, value);
            } else {
                bestValue = Math.min(bestValue, value);
            }
        }
        return bestValue;
    }

    @Override
    public String toString() {
        return "MiniMax{depth=" + searchDepth + "}";
    }

    @Override
    public long getNumBoardsEvaluated() {
        return numBoardsEvaluated;
    }

    @Override
    public Move execute(Chessboard chessboard) {
        
        return null;
        
    }
}
