package Chess;

import Chess.Move.MoveFactory;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

public final class MiniMax implements MoveStrategy {

    private final BoardEvaluator boardEvaluator;
    private final int searchDepth;
    private long numBoardsEvaluated;
    private Chessboard board;
    private FreqTableRow[] freqTable;
    private int freqTableIndex;

    public MiniMax(final Chessboard board,final int searchDepth) {
        this.board = board;
        this.boardEvaluator = SimpleBoardEvaluator.get();
        this.numBoardsEvaluated = 0;
        this.searchDepth = searchDepth;
    }


    @Override
    public String toString() {
        return "MiniMax";
    }

    @Override
    public long getNumBoardsEvaluated() {
        return this.numBoardsEvaluated;
    }


    @Override
    public Move execute(final Chessboard board) {
        final long startTime = System.currentTimeMillis();
        Move bestMove = MoveFactory.getNullMove();
        int highestSeenValue = Integer.MIN_VALUE;
        int lowestSeenValue = Integer.MAX_VALUE;
        int currentValue;
        System.out.println(board.currentPlayer() + " THINKING with depth = " + this.searchDepth);
        this.freqTable = new FreqTableRow[board.currentPlayer().getLegalMoves().size()];
        this.freqTableIndex = 0;
        int moveCounter = 1;
        final int numMoves = board.currentPlayer().getLegalMoves().size();
        for (final Move move : board.currentPlayer().getLegalMoves()) {
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()) {
                final FreqTableRow row = new FreqTableRow(move);
                this.freqTable[this.freqTableIndex] = row;
                currentValue = board.currentPlayer().getAlliance().isWhite()
                        ? min(moveTransition.getToBoard(), this.searchDepth - 1)
                        : max(moveTransition.getToBoard(), this.searchDepth - 1);

                System.out.println("\t" + toString() + " analyzing move (" + moveCounter + "/" + numMoves + ") " + move
                        + " scores " + currentValue + " " + this.freqTable[this.freqTableIndex]);
                this.freqTableIndex++;
                if (board.currentPlayer().getAlliance().isWhite()
                        && currentValue >= highestSeenValue) {
                    highestSeenValue = currentValue;
                    bestMove = move;
                } else if (board.currentPlayer().getAlliance().isBlack()
                        && currentValue <= lowestSeenValue) {
                    lowestSeenValue = currentValue;
                    bestMove = move;
                }
            } else {
                System.out.println("\t" + this + " can't execute move (" + moveCounter + "/" + numMoves + ") " + move);
            }
            moveCounter++;
        }
        final long executionTime = System.currentTimeMillis() - startTime;
        System.out.printf("%s SELECTS %s [#boards = %d time taken = %d ms, rate = %.1f\n", board.currentPlayer(),
                bestMove, this.numBoardsEvaluated, executionTime, (1000 * ((double) this.numBoardsEvaluated / executionTime)));
        long total = 0;
        for (final FreqTableRow row : this.freqTable) {
            if (row != null) {
                total += row.getCount();
            }
        }
        if (this.numBoardsEvaluated != total) {
            System.out.println("somethings wrong with the # of boards evaluated!");
        }
        return bestMove;
    }

    private int min(final Chessboard board, final int depth) {

        if (depth == 0) { // || game over
            this.numBoardsEvaluated++;
            this.freqTable[this.freqTableIndex].increment();
            return this.boardEvaluator.evaluate(board, depth);
        }
        if (isEndGameScenario(board)) {
            return this.boardEvaluator.evaluate(board, depth);
        }
        int lowestSeenValue = Integer.MAX_VALUE; //imposibly big number, that I am not going to evaluate
        for (final Move move : board.currentPlayer().getLegalMoves()) {
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()) { //I need to create this method in class MoveStatus
                final int currentValue = max(moveTransition.getToBoard(), depth - 1); // I nedd to create this method in class MoveTransition
                if (currentValue <= lowestSeenValue) {
                    lowestSeenValue = currentValue;
                }
            }
        }
        return lowestSeenValue;
    }

    private int max(final Chessboard board, final int depth) {

        if (depth == 0) { // || game over
            this.numBoardsEvaluated++;
            this.freqTable[this.freqTableIndex].increment();
            return this.boardEvaluator.evaluate(board, depth);
        }

        if (isEndGameScenario(board)) {
            return this.boardEvaluator.evaluate(board, depth);
        }
        int highestSeenValue = Integer.MIN_VALUE; //imposibly small number, that I am not going to evaluate
        for (final Move move : board.currentPlayer().getLegalMoves()) {
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()) { //I need to create this method in class MoveStatus
                final int currentValue = min(moveTransition.getToBoard(), depth - 1); // I nedd to create this method in class MoveTransition
                if (currentValue >= highestSeenValue) {
                    highestSeenValue = currentValue;
                }
            }
        }
        return highestSeenValue;
    }
    
    private static boolean isEndGameScenario(final Chessboard board) {
        return board.currentPlayer().isInCheckMate() || board.currentPlayer().isInStaleMate();
    }

      private static class FreqTableRow {

        private final Move move;
        private final AtomicLong count;

        FreqTableRow(final Move move) {
            this.count = new AtomicLong();
            this.move = move;
        }

        long getCount() {
            return this.count.get();
        }

        void increment() {
            this.count.incrementAndGet();
        }

        @Override
        public String toString() {
            return BoardUtils.INSTANCE.getPositionAtCoordinate(this.move.getCurrentCoordinate())
                    + BoardUtils.INSTANCE.getPositionAtCoordinate(this.move.getDestinationCoordinate()) + " : " + this.count;
        }
    }   
}