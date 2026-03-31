package Chess;

import Chess.Chessboard.Builder;
import Chess.Move.MoveFactory;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;

public final class MiniMax implements MoveStrategy {

    public static final void main(String[] args) {
        Chessboard STANDARD_BOARD = new Chessboard(new Builder()).getStandardBoard();
        System.out.println(STANDARD_BOARD);
    }

    private final BoardEvaluator boardEvaluator;
    private final int searchDepth;
    private long boardsEvaluated;
    private Chessboard board;
    private FreqTableRow[] freqTable;
    private int freqTableIndex;

    public MiniMax(final Chessboard board, final int searchDepth) {
        this.board = board;
        // System.out.println("Šachovnice: " + this.board);
        this.boardEvaluator = SimpleBoardEvaluator.get();
        // System.out.println("Hodnocení: " + SimpleBoardEvaluator.get());
        this.boardsEvaluated = 0;
        this.searchDepth = searchDepth;
        // System.out.println("Hloubka prohledávání: " + searchDepth);
    }

    @Override
    public String toString() {
        return "Minimax{" + "boardEvaluator=" + boardEvaluator + ", searchDepth=" + searchDepth
                + ", numBoardsEvaluated=" + boardsEvaluated + ", board=" + board
                + ", freqTable=" + Arrays.toString(freqTable) + ", freqTableIndex=" + freqTableIndex + ")";
    }

    @Override
    public long getNumBoardsEvaluated() {
        return this.boardsEvaluated;
    }


    public Move execute(Chessboard board) {
        Move bestMove = MoveFactory.getNullMove();
        int highestSeenValue = Integer.MIN_VALUE;
        int lowestSeenValue = Integer.MAX_VALUE;
        int currentValue;

        System.out.println("AI přemýšlí s hloubkou: " + this.searchDepth);

        for (final Move move : board.currentPlayer().getLegalMoves()) {
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            
            if (moveTransition.getMoveStatus().isDone()) {
                // Pokud hraje bílý, chce MAXIMALIZOVAT, pokud černý, MINIMALIZOVAT
                currentValue = board.currentPlayer().getAlliance().isWhite() ?
                        min(moveTransition.getToBoard(), this.searchDepth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE) :
                        max(moveTransition.getToBoard(), this.searchDepth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE);

                if (board.currentPlayer().getAlliance().isWhite() && currentValue >= highestSeenValue) {
                    highestSeenValue = currentValue;
                    bestMove = move;
                } else if (board.currentPlayer().getAlliance().isBlack() && currentValue <= lowestSeenValue) {
                    lowestSeenValue = currentValue;
                    bestMove = move;
                }
            }
        }

        return bestMove;
    }

    // REKURZIVNÍ ČÁST - MAX
    public int max(final Chessboard board, final int depth, int alpha, int beta) {
        if (depth == 0 || isEndGameScenario(board)) {
            return this.boardEvaluator.evaluate(board, depth);
        }

        int highestSeenValue = Integer.MIN_VALUE;
        for (final Move move : board.currentPlayer().getLegalMoves()) {
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()) {
                highestSeenValue = Math.max(highestSeenValue, min(moveTransition.getToBoard(), depth - 1, alpha, beta));
                alpha = Math.max(alpha, highestSeenValue);
                if (beta <= alpha) {
                    break; // Alfa-beta ořezání
                }
            }
        }
        return highestSeenValue;
    }

    // REKURZIVNÍ ČÁST - MIN
    public int min(final Chessboard board, final int depth, int alpha, int beta) {
        if (depth == 0 || isEndGameScenario(board)) {
            return this.boardEvaluator.evaluate(board, depth);
        }

        int lowestSeenValue = Integer.MAX_VALUE;
        for (final Move move : board.currentPlayer().getLegalMoves()) {
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()) {
                lowestSeenValue = Math.min(lowestSeenValue, max(moveTransition.getToBoard(), depth - 1, alpha, beta));
                beta = Math.min(beta, lowestSeenValue);
                if (beta <= alpha) {
                    break; // Alfa-beta ořezání
                }
            }
        }
        return lowestSeenValue;
    }

    @Override
    public Move execute(final Chessboard board, int searchDepth) {
        final long startTime = System.currentTimeMillis();
        Move bestMove = MoveFactory.getNullMove();

        int highestSeenValue = Integer.MIN_VALUE;
        int lowestSeenValue = Integer.MAX_VALUE;
        int currentValue;

        System.out.println(toString() + ": " + board.currentPlayer() + " THINKING with depth = " + this.searchDepth);

        this.freqTable = new FreqTableRow[board.currentPlayer().getLegalMoves().size()];
        this.freqTableIndex = 0;
        int moveCounter = 1;
        final int numMoves = board.currentPlayer().getLegalMoves().size();
        System.out.println("Figury na šachovnici jsou tyto:" + board.getAllPieces());
        for (final Move move : board.currentPlayer().getLegalMoves()) {
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()) {

                final FreqTableRow row = new FreqTableRow(move);
                this.freqTable[this.freqTableIndex] = row;

                // Pokud hraje bílý, minimalizujeme následný tah černého a naopak
                currentValue = board.currentPlayer().getAlliance().isWhite()
                        ? min(moveTransition.getToBoard(), this.searchDepth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE)
                        : max(moveTransition.getToBoard(), this.searchDepth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE);

                System.out.println("\t" + toString() + " analyzing move (" + moveCounter + "/" + numMoves + ") " + move
                        + " scores" + currentValue + " " + this.freqTable[this.freqTableIndex]);
                this.freqTableIndex++;
                if (board.currentPlayer().getAlliance().isWhite() && currentValue >= highestSeenValue) {
                    highestSeenValue = currentValue;
                    bestMove = move;
                } else if (board.currentPlayer().getAlliance().isBlack() && currentValue <= lowestSeenValue) {
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
                bestMove, this.boardsEvaluated, executionTime,
                (1000 * ((double) this.boardsEvaluated / executionTime)));

        long total = 0;
        for (final FreqTableRow row : this.freqTable) {
            if (row != null) {
                total += row.getCount();
            }
        }
        if (this.boardsEvaluated != total) {
            System.out.println("something wrong with the # of board evaluated!");
        }
        return bestMove;
    }

    private int min(final Chessboard board, final int depth) {

        if (depth == 0) { // || game over
            this.boardsEvaluated++;
            this.freqTable[this.freqTableIndex].increment();
            return this.boardEvaluator.evaluate(board, depth);
        }
        if (isEndGameScenario(board)) {
            return this.boardEvaluator.evaluate(board, depth);
        }
        int lowestSeenValue = Integer.MAX_VALUE; // imposibly big number, that I am not going to evaluate
        for (final Move move : board.currentPlayer().getLegalMoves()) {
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()) {
                final int currentValue = max(moveTransition.getToBoard(), depth - 1);
                if (currentValue <= lowestSeenValue) {
                    lowestSeenValue = currentValue;
                }
            }
        }
        return lowestSeenValue;
    }

    private int max(final Chessboard board, final int depth) {

        if (depth == 0) { // || game over
            this.boardsEvaluated++;
            this.freqTable[this.freqTableIndex].increment();
            return this.boardEvaluator.evaluate(board, depth);
        }

        if (isEndGameScenario(board)) {
            return this.boardEvaluator.evaluate(board, depth);
        }
        int highestSeenValue = Integer.MIN_VALUE; // imposibly small number, that I am not going to evaluate
        for (final Move move : board.currentPlayer().getLegalMoves()) {
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()) { // I need to create this method in class MoveStatus
                final int currentValue = min(moveTransition.getToBoard(), depth - 1); // I nedd to create this method in
                                                                                      // class MoveTransition
                if (currentValue >= highestSeenValue) {
                    highestSeenValue = currentValue;
                }
            }
        }
        return highestSeenValue;
    }

    public int alphaBeta(Chessboard board, int depth, int alpha, int beta) {
        if (depth == 0 || isEndGameScenario(board)) {
            return boardEvaluator.evaluate(board, depth);
        }

        for (Move move : board.currentPlayer().getLegalMoves()) {
            MoveTransition transition = board.currentPlayer().makeMove(move);
            if (transition.getMoveStatus().isDone()) {
                // Negamax volání: otočíme skóre i hranice alfa/beta
                int score = -alphaBeta(transition.getToBoard(), depth - 1, -beta, -alpha);

                if (score >= beta) {
                    return beta; // Beta-cut: tah je pro soupeře příliš dobrý
                }
                if (score > alpha) {
                    alpha = score; // Aktualizace nejlepšího nalezeného tahu
                }
            }
        }
        return alpha;
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
                    + BoardUtils.INSTANCE.getPositionAtCoordinate(this.move.getDestinationCoordinate()) + " : "
                    + this.count;
        }
    }
}