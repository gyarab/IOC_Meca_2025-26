/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Chess;

/**
 *
 * @author mecova
 */
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public enum MoveUtils {

    INSTANCE;

    public static final Move NULL_MOVE = new NullMove();
    public static final Comparator<Move> SIMPLE_MOVE_COMPARATOR = (m1, m2) -> {
        if (m1.isCastlingMove() != m2.isCastlingMove()) {
            return m1.isCastlingMove() ? -1 : 1;
        }
        return Integer.compare(mvvlva(m2), mvvlva(m1));
    };

    public static List<Move> getQuiescenceMoves(final Player player) {
        final List<Move> interestingMoves = new ArrayList<>();
        for (final Move move : player.getLegalMoves()) {
            if (move.isAttack()) {
                interestingMoves.add(move);
            } /*else {
                final MoveTransition transition = player.makeMove(move);
                if (transition.getMoveStatus().isDone()) {
                    final Board afterMove = transition.getToBoard();
                    if (afterMove.currentPlayer().isInCheck()) {
                        interestingMoves.add(move);
                    }
                }
            }*/
        }
        return interestingMoves;
    }

    public static List<Move> getPromotionOnlyMoves(final Player player) {
        final List<Move> promotions = new ArrayList<>();
        for (final Move move : player.getLegalMoves()) {
            if (move instanceof PawnPromotion) {
                promotions.add(move);
            } else {
            }
        }
        return promotions;
    }

    public static Move getMove(final Board board,
                               final String from,
                               final String to) {
        return MoveFactory.createMove(board, BoardUtils.INSTANCE.getCoordinateAtPosition(from),
                BoardUtils.INSTANCE.getCoordinateAtPosition(to));
    }

    public enum MoveSorter {
        STANDARD {
            @Override
            public Collection<Move> sort(Collection<Move> moves) {
                final List<Move> sorted = new ArrayList<>(moves);
                sorted.sort(SIMPLE_MOVE_COMPARATOR);
                return sorted;
            }
        },
        NONE {
            @Override
            public Collection<Move> sort(Collection<Move> moves) {
                return moves;
            }
        };
        public abstract Collection<Move> sort(Collection<Move> moves);
    }

    public static class Line {
        private final List<Integer> coordinates;

        public Line() {
            this.coordinates = new ArrayList<>();
        }

        public void addCoordinate(final int coordinate) {
            this.coordinates.add(coordinate);
        }

        public List<Integer> getLineCoordinates() {
            return this.coordinates;
        }

        public boolean isEmpty() {
            return this.coordinates.isEmpty();
        }
    }
}

