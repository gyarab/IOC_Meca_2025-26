/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Chess;
/**
 *
 * @author Admin
 */
// Source - https://stackoverflow.com/q
// Posted by David weng, modified by community. See post 'Timeline' for change history
// Retrieved 2025-12-31, License - CC BY-SA 4.0

public class MiniMax implements MoveStrategy {

    private final BoardEvaluator boardEvaluator;
    private int searchDepth;


    public MiniMax(BoardEvaluator boardEvaluator, int searchDepth) {
        this.boardEvaluator = boardEvaluator;
        this.searchDepth = searchDepth;
    }

    @Override
    public String toString() {
        return "MiniMax{" +
                "boardEvaluator=" + boardEvaluator +
                '}';
    }

    @Override
    public Move execute(ChessGame game) {

        long startTime = System.currentTimeMillis();

        Move calculatedBestMove = null;

        int highestSeenValue = Integer.MIN_VALUE;
        int lowestSeenValue = Integer.MAX_VALUE;
        int currentValue;

        System.out.println("computer thinks" + " depth= " + this.searchDepth);

        var numberOfAllMoves = game.getBoard().getAllAvailableMoves(PieceColor.BLACK);

        for(Move move : game.getBoard().getAllAvailableMoves(PieceColor.BLACK)){
             game.getBoard().movePiece(move.getSelectedPiece(), move);
             currentValue = calculateValue(game);
             if(game.getCurrentTurn() == game.getPlayers().get(0) && currentValue >= highestSeenValue)
             {
                 highestSeenValue = currentValue;
                 calculatedBestMove = move;
             }
             else if(game.getCurrentTurn() == game.getPlayers().get(1) && currentValue <= lowestSeenValue){
                 lowestSeenValue = currentValue;
                 calculatedBestMove = move;
             }

        }
        long CalculationTime = System.currentTimeMillis() - startTime;
        return  calculatedBestMove;
    }

    public int calculateValue(ChessGame game){
        if(game.getCurrentTurn() == game.getPlayers().get(0)){
           return  min(game, -1);
        }
            return max(game,  -1);
    }


    public int min(ChessGame game, int depth){

        if(depth == 0 || game.getGameStatus() == GameStatus.BLACK_CHECK_MATE || game.getGameStatus() == GameStatus.WHITE_CHECK_MATE){
            return this.boardEvaluator.evaluate(game, depth);
        }

        int lowestValue = Integer.MAX_VALUE;

        for(Move move: game.getBoard().getAllAvailableMoves(PieceColor.BLACK)){
            game.getBoard().movePiece(move.getSelectedPiece(), move);
            int currentValue = max(game, depth -1);
            if(currentValue <= lowestValue)
            {
                lowestValue = currentValue;
            }
        }
        return lowestValue;
    }

    public int max(ChessGame game, int depth){
        if(depth == 0 || game.getGameStatus() == GameStatus.BLACK_CHECK_MATE || game.getGameStatus() == GameStatus.WHITE_CHECK_MATE){
            return this.boardEvaluator.evaluate(game, depth);
        }

        int highestSeenValue = Integer.MIN_VALUE;

        for(Move move: game.getBoard().getAllAvailableMoves(PieceColor.BLACK)){
            game.getBoard().movePiece(move.getSelectedPiece(), move);

            int currentValue = min(game, depth -1);
            if(currentValue <= highestSeenValue)
            {
                highestSeenValue = currentValue;
            }
        }
        return highestSeenValue;
    }
}
