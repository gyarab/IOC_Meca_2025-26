package Chess;

import Chess.Chessboard.Builder;
import Chess.Pieces.piece.King;
import Chess.Pieces.piece.Pawn;
import Chess.Pieces.piece.Piece;
import Chess.Pieces.piece.Rook;
import java.util.Objects;

public class Move {

    protected final Chessboard board;
    protected final Piece movedPiece;
    protected final int destinationCoordinate;
    protected final boolean isFirstMove;

    public Piece capturedPiece;
    public Piece piece;
    public int value;
    private int x;
    private int y;

    private Move(final Chessboard board, final Piece pieceMov,
            final int destinationCoordinate) {
        this.board = board;
        this.movedPiece = pieceMov;
        this.destinationCoordinate = destinationCoordinate;
        this.isFirstMove = movedPiece.isFirstMove();
    }

    private Move(final Chessboard board,
            final int destinationCoordinate) {

        this.board = board;
        this.destinationCoordinate = destinationCoordinate;
        this.movedPiece = null;
        this.isFirstMove = false;
    }

    public Move(final Chessboard board, Piece piece, int col, int row) {
        this.board = board;
        this.movedPiece = piece;
        this.destinationCoordinate = row * 8 + col;
        this.isFirstMove = piece.isFirstMove();
    }

    public static class PawnPromotion
            extends PawnMove {

        final Move decoratedMove;
        final Pawn promotedPawn;
        final Piece promotionPiece;

        public PawnPromotion(final Move decoratedMove,
                final Piece promotionPiece) {
            super(decoratedMove.getBoard(), decoratedMove.getMovedPiece(), decoratedMove.getDestinationCoordinate());
            this.decoratedMove = decoratedMove;
            this.promotedPawn = (Pawn) decoratedMove.getMovedPiece();
            this.promotionPiece = promotionPiece;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            if (!super.equals(o)) {
                return false;
            }
            final PawnPromotion that = (PawnPromotion) o;
            return Objects.equals(decoratedMove, that.decoratedMove) && Objects.equals(promotedPawn, that.promotedPawn)
                    && Objects.equals(promotionPiece, that.promotionPiece);
        }

        public int hashCode() {
            return Objects.hash(super.hashCode(), decoratedMove, promotedPawn, promotionPiece);
        }

        @Override
        public Chessboard execute() {
            final Chessboard pawnMovedBoard = this.decoratedMove.execute();
            final Chessboard.Builder builder = new Builder();
            final Piece[] boardConfig = pawnMovedBoard.getBoardCopy();
            final int[] currentActive = pawnMovedBoard.currentPlayer().getActivePieces();
            final int[] opponentActive = pawnMovedBoard.currentPlayer().getOpponent().getActivePieces();

            for (final int index : currentActive) {
                final Piece pieces = boardConfig[index];
                if (!this.promotedPawn.equals(pieces)) {
                    builder.setPiece(pieces);
                }
            }

            for (final int index : opponentActive) {
                builder.setPiece(boardConfig[index]);
            }

            builder.setPiece(this.promotionPiece.getMovedPiece(this));
            builder.setMoveMaker(pawnMovedBoard.currentPlayer().getAlliance());
            return builder.build();
        }

    }

    @Override
    public String toString() {
        return movedPiece + BoardUtils.INSTANCE.getPositionAtCoordinate(this.destinationCoordinate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCurrentCoordinate(), destinationCoordinate, movedPiece);
    }

    @Override
    public boolean equals(final Object oth) {
        if (this == oth) {
            return true;
        }
        if (oth == null) {
            return false;
        }
        if (!(oth instanceof Move)) {
            return false;
        }

        if (getClass() != oth.getClass()) {
            return false;
        }

        final Move othMove = (Move) oth; // cast variable of type Object

        if (this.destinationCoordinate != othMove.destinationCoordinate) {
            return false;
        }

        if (this.movedPiece != othMove.movedPiece) {
            return false;
        }

        return getCurrentCoordinate() == othMove.getCurrentCoordinate()
                && getDestinationCoordinate() == othMove.getDestinationCoordinate()
                && getMovedPiece().equals(othMove.getMovedPiece());
    }

    public Chessboard getBoard() {
        return this.board;
    }

    public int getCurrentCoordinate() {
        return this.movedPiece.getPiecePosition();
    }

    public int getDestinationCoordinate() {
        return this.destinationCoordinate;
    }

    public Piece getMovedPiece() {
        return this.movedPiece;
    }

    public boolean isAttack() {
        return false; // default value
    }

    public boolean isCastlingMove() {
        return false; // default move
    }

    public Piece getAttackedPiece() {
        return null;
    }

    public Piece getPiece() {
        return movedPiece;
    }

    public int getDestinationRow() {
        return this.destinationCoordinate / 8;
    }

    public int getDestinationCol() {
        return this.destinationCoordinate % 8;
    }

    static abstract class CastleMove extends Move {

        final Rook castleRook;
        final int castleRookDestination;

        CastleMove(final Chessboard board,
                final King movedKing,
                final int destinationCoordinate,
                final Rook castleRook,
                final int castleRookDestination) {
            super(board, movedKing, destinationCoordinate);
            this.castleRook = castleRook;
            this.castleRookDestination = castleRookDestination;
        }

        Rook getCastleRook() {
            return this.castleRook;
        }

        @Override
        public boolean isCastlingMove() {
            return true;
        }

        @Override
        public Chessboard execute() {
            if (this.movedPiece == null) {
                throw new RuntimeException(
                        "Chyba: Pokus o vykonání tahu bez definované figurky (movedPiece je null) na pozici "
                                + destinationCoordinate);
            }
            final Piece[] newBoardConfig = board.getBoardCopy();
            newBoardConfig[this.movedPiece.getPiecePosition()] = null;
            newBoardConfig[this.castleRook.getPiecePosition()] = null;
            final Piece newKing = this.movedPiece.getMovedPiece(this);
            newBoardConfig[this.destinationCoordinate] = newKing;
            final Move syntheticMove = new MajorMove(this.board, this.castleRook, this.castleRookDestination);
            final Piece newRook = this.castleRook.getMovedPiece(syntheticMove);
            newBoardConfig[this.castleRookDestination] = newRook;
            final Builder builder = new Builder();
            return builder.setBoardConfiguration(newBoardConfig)
                    .setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance())
                    .build();
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), castleRook, castleRookDestination);
        }

        @Override
        public boolean equals(final Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof CastleMove)) {
                return false;
            }
            final CastleMove otherCastleMove = (CastleMove) other;
            return super.equals(otherCastleMove) && this.castleRook.equals(otherCastleMove.getCastleRook());
        }
    }

    public static class KingSideCastleMove
            extends CastleMove {

        public KingSideCastleMove(final Chessboard board,
                final King movedKing,
                final int kingDestination,
                final Rook castleRook,
                final int rookDestination) {
            super(board, movedKing, kingDestination, castleRook, rookDestination);
        }

        @Override
        public boolean equals(final Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof KingSideCastleMove)) {
                return false;
            }
            final KingSideCastleMove otherKingSideCastleMove = (KingSideCastleMove) other;
            return super.equals(otherKingSideCastleMove)
                    && this.castleRook.equals(otherKingSideCastleMove.getCastleRook());
        }

        @Override
        public String toString() {
            return "O-O";
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode());
        }
    }

    public static class QueenSideCastleMove
            extends CastleMove {

        public QueenSideCastleMove(final Chessboard board,
                final King pieceMoved,
                final int kingDestination,
                final Rook castleRook,
                final int rookCastleDestination) {
            super(board, pieceMoved, kingDestination, castleRook, rookCastleDestination);
        }

        @Override
        public boolean equals(final Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof QueenSideCastleMove)) {
                return false;
            }
            final QueenSideCastleMove otherQueenSideCastleMove = (QueenSideCastleMove) other;
            return super.equals(otherQueenSideCastleMove)
                    && this.castleRook.equals(otherQueenSideCastleMove.getCastleRook());
        }

        @Override
        public String toString() {
            return "O-O-O";
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode());
        }

    }

    static abstract class AttackMove
            extends Move {

        private final Piece attackedPiece;

        AttackMove(final Chessboard board,
                final Piece pieceMoved,
                final int destinationCoordinate,
                final Piece pieceAttacked) {
            super(board, pieceMoved, destinationCoordinate);
            this.attackedPiece = pieceAttacked;
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), attackedPiece);
        }

        @Override
        public boolean equals(final Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof AttackMove)) {
                return false;
            }
            final AttackMove otherAttackMove = (AttackMove) other;
            return super.equals(otherAttackMove) && getAttackedPiece().equals(otherAttackMove.getAttackedPiece());
        }

        @Override
        public Piece getAttackedPiece() {
            return this.attackedPiece;
        }

        @Override
        public boolean isAttack() {
            return true;
        }

    }

    static class NullMove
            extends Move {

        NullMove() {
            super(null, -1);
        }

        @Override
        public int getCurrentCoordinate() {
            return -1;
        }

        @Override
        public int getDestinationCoordinate() {
            return -1;
        }

        @Override
        public Chessboard execute() {
            throw new RuntimeException("cannot execute null move!");
        }

        @Override
        public String toString() {
            return "Null Move";
        }

    }

    public static class PawnAttackMove
            extends AttackMove {

        public PawnAttackMove(final Chessboard board,
                final Piece pieceMoved,
                final int destinationCoordinate,
                final Piece pieceAttacked) {
            super(board, pieceMoved, destinationCoordinate, pieceAttacked);
        }

        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof PawnAttackMove && super.equals(other);
        }

        @Override
        public String toString() {
            return BoardUtils.INSTANCE.getPositionAtCoordinate(this.movedPiece.getPiecePosition()).charAt(0) + "x" +
                    BoardUtils.INSTANCE.getPositionAtCoordinate(this.destinationCoordinate);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode());
        }

    }

    public static class PawnMove
            extends Move {

        public PawnMove(final Chessboard board,
                final Piece pieceMoved,
                final int destinationCoordinate) {
            super(board, pieceMoved, destinationCoordinate);
        }

        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof PawnMove && super.equals(other);
        }

        @Override
        public String toString() {
            return BoardUtils.INSTANCE.getPositionAtCoordinate(this.destinationCoordinate);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode());
        }

    }

    public static class MoveFactory {

        private MoveFactory() {
            throw new RuntimeException("Not instantiatable!");
        }

        public static Move getNullMove() {
            return MoveUtils.NULL_MOVE;
        }

        public static Move createMove(final Chessboard board,
                final int currentCoordinate,
                final int destinationCoordinate) {
            for (final Move move : board.getAllLegalMoves()) {
                if (move.getCurrentCoordinate() == currentCoordinate
                        && move.getDestinationCoordinate() == destinationCoordinate) {
                    return move;
                }
            }
            return MoveUtils.NULL_MOVE;
        }
    }

    public Chessboard execute() {
        final Chessboard.Builder builder = new Chessboard.Builder();

        // 1. Projdeme všech 64 políček staré desky jedno po druhém
        Piece[] currentBoardConfig = this.board.getBoardPieces();
        for (int i = 0; i < 64; i++) {
            Piece currentPiece = currentBoardConfig[i];

            if (currentPiece != null) {
                // 2. Pokud to NENÍ figurka, kterou právě táhneme, a NENÍ to figurka, kterou
                // bereme (pokud je to AttackMove), přidáme ji na novou desku.
                // K tomu použijeme equals porovnání, nebo jednoduše porovnáme pozice.
                if (!this.movedPiece.equals(currentPiece)) {
                    // Zkontrolujeme, zda se nejedná o útočný tah, který tuto figurku právě bere
                    if (this.isAttack() && this.getAttackedPiece().equals(currentPiece)) {
                        // Tuto figurku bereme, takže ji NEPŘIDÁVÁME na novou desku
                    } else {
                        // Není to pohnutá ani sebraná figurka, zachováme ji
                        builder.setPiece(currentPiece);
                    }
                }
            }
        }

        // 3. Umístíme pohnutou figurku na její nové políčko
        builder.setPiece(this.movedPiece.getMovedPiece(this));

        // 4. Předáme tah soupeři
        builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());

        // 5. Postavíme novou šachovnici
        return builder.build();
    }

    public Chessboard undo() {
        final Piece[] newBoardConfig = board.getBoardCopy();
        final Builder builder = new Builder();
        return builder.setBoardConfiguration(newBoardConfig)
                .setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance())
                .build();
    }

    String disambiguation() {
        final Piece movedP = this.getMovedPiece();
        final String from = BoardUtils.INSTANCE.getPositionAtCoordinate(this.getCurrentCoordinate());
        final char fromFile = from.charAt(0);
        final char fromRank = from.charAt(1);
        boolean fileNeeded = false;
        boolean rankNeeded = false;

        for (final Move move : board.currentPlayer().getLegalMoves()) {
            if (move == this) {
                continue;
            }
            if (move.getMovedPiece().getPieceType() == movedP.getPieceType()
                    && move.getDestinationCoordinate() == this.getDestinationCoordinate()) {
                final MoveTransition otherTransition = board.currentPlayer().makeMove(move);
                if (!otherTransition.getMoveStatus().isDone()) {
                    continue;
                }
                final String otherFrom = BoardUtils.INSTANCE.getPositionAtCoordinate(move.getCurrentCoordinate());
                final char otherFile = otherFrom.charAt(0);
                final char otherRank = otherFrom.charAt(1);
                if (fromFile != otherFile) {
                    fileNeeded = true;
                }
                if (fromRank != otherRank) {
                    rankNeeded = true;
                }
            }
        }

        if (fileNeeded) {
            return "" + fromFile;
        }
        if (rankNeeded) {
            return "" + fromRank;
        }
        return "";
    }

    public static class MajorMove
            extends Move {

        public MajorMove(final Chessboard board,
                final Piece pieceMoved,
                final int destinationCoordinate) {
            super(board, pieceMoved, destinationCoordinate);
        }

        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof MajorMove && super.equals(other);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode());
        }

        @Override
        public String toString() {
            return movedPiece.getPieceType().toString() + disambiguation()
                    + BoardUtils.INSTANCE.getPositionAtCoordinate(this.destinationCoordinate);
        }
    }

    public static class MajorAttackMove
            extends AttackMove {

        private final Piece attackedPiece = super.attackedPiece;

        public MajorAttackMove(final Chessboard board, final Piece pieceMoved, final int destinationCoordinate,
                final Piece pieceAttacked) {
            super(board, pieceMoved, destinationCoordinate, pieceAttacked);

        }

        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof MajorAttackMove && super.equals(other);
        }

        @Override
        public String toString() {
            return movedPiece.getPieceType() +
                    disambiguation() + "x" +
                    BoardUtils.INSTANCE.getPositionAtCoordinate(this.destinationCoordinate);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), attackedPiece);
        }
    }

}
