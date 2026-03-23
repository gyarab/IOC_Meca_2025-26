package Chess;

import Chess.Pieces.piece.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.awt.Color;
import java.awt.Graphics2D;

public final class Chessboard {

    public static final int MAX_COL = 8;
    public static final int MAX_ROW = 8;
    public static final int SQUARE_SIZE = 100;
    public static final int HALF_SQUARE_SIZE = SQUARE_SIZE / 2;
    public static final int BOARD_SIZE = 64;

    private final Alliance alliance = Alliance.WHITE;
    private final Piece[] boardPieces;
    private final int[] whitePieceCoordinates;
    private final int[] blackPieceCoordinates;
    private final WhitePlayer whiteplayer;
    private final BlackPlayer blackPlayer;
    private final Player currentPlayer;
    private final Pawn enPassantPawn;

    private int evaluation; // actual value of position
    private Piece[][] board = new Piece[MAX_ROW][MAX_COL];
    private ArrayList<Piece> pieces = new ArrayList<>();
    private Stack<Move> moveHistory = new Stack<>();

    private static final Chessboard STANDARD_BOARD = createStandardBoardImpl();

    private int sideToMove = Chesswindowpanel.WHITE;

    public Chessboard(Builder builder) {
        this.boardPieces = builder.boardPieces;
        this.whitePieceCoordinates = calculateActiveIndexes(boardPieces, Alliance.WHITE);
        this.blackPieceCoordinates = calculateActiveIndexes(boardPieces, Alliance.BLACK);
        this.enPassantPawn = builder.enPassantPawn;
        final Collection<Move> whiteStandardMoves = calculateLegalMoves(boardPieces, this.whitePieceCoordinates);
        final Collection<Move> blackStandardMoves = calculateLegalMoves(boardPieces, this.blackPieceCoordinates);
        this.whiteplayer = new WhitePlayer(this, establishKing(this.whitePieceCoordinates, this.boardPieces),
                whiteStandardMoves, blackStandardMoves);
        this.blackPlayer = new BlackPlayer(this, establishKing(this.blackPieceCoordinates, this.boardPieces),
                whiteStandardMoves, blackStandardMoves);
        this.currentPlayer = builder.nextMoveMaker.choosePlayerByAlliance(this.whiteplayer, this.blackPlayer);
    }

        @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final Chessboard other = (Chessboard) obj;
        return Arrays.equals(this.boardPieces, other.boardPieces) &&
                this.currentPlayer.getAlliance() == other.currentPlayer.getAlliance() &&
                Objects.equals(this.enPassantPawn, other.enPassantPawn) &&
                this.whiteplayer.isKingSideCastleCapable() == other.whiteplayer.isKingSideCastleCapable() &&
                this.whiteplayer.isQueenSideCastleCapable() == other.whiteplayer.isQueenSideCastleCapable() &&
                this.blackPlayer.isKingSideCastleCapable() == other.blackPlayer.isKingSideCastleCapable() &&
                this.blackPlayer.isQueenSideCastleCapable() == other.blackPlayer.isQueenSideCastleCapable();
    }

       @Override
    public int hashCode() {
        return Objects.hash(
                Arrays.hashCode(this.boardPieces),
                this.currentPlayer.getAlliance(),
                this.enPassantPawn,
                this.whiteplayer.isKingSideCastleCapable(),
                this.whiteplayer.isQueenSideCastleCapable(),
                this.blackPlayer.isKingSideCastleCapable(),
                this.blackPlayer.isQueenSideCastleCapable()
        );
    }
    
     @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder("\n");
        for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
            final String tileText = prettyPrint(this.boardPieces[i]);
            builder.append(String.format("%3s", tileText));
            if ((i + 1) % 8 == 0) {
                builder.append("\n");
            }
        }
        return builder.toString();
    }
    
    
    private static String prettyPrint(final Piece piece) {
        if(piece != null) {
            return piece.getPieceAllegiance().isBlack() ?
                    piece.toString().toLowerCase() : piece.toString();
        }
        return "-";
    }


    public int getSideToMove() {
        return sideToMove;
    }

    
    public int[] getWhitePieceCoordinates() {
        return this.whitePieceCoordinates;
    }

    public int[] getBlackPieceCoordinates() {
        return this.blackPieceCoordinates;
    }

    public WhitePlayer whitePlayer() {
        return this.whiteplayer;
    }

    public BlackPlayer blackPlayer() {
        return this.blackPlayer;
    }

    public Player currentPlayer() {
        return this.currentPlayer;
    }

    public Piece[] getBoardPieces() {
        return this.boardPieces;
    }

    public Piece getPiece(int coordinate) {
        return boardPieces[coordinate];
    }

    public Piece[] getBoardCopy() {
        return this.boardPieces.clone();
    }

    public static Chessboard createStandardBoard() {
        return STANDARD_BOARD;
    }

    public Chessboard getStandardBoard() {
        return STANDARD_BOARD;
    }

    public Pawn getEnPassantPawn() {
        return this.enPassantPawn;
    }

    public Stack<Move> getMoveHistory() {
        return this.moveHistory;
    }

    public void switchSide() {
        sideToMove = (sideToMove == Chesswindowpanel.WHITE)
                ? Chesswindowpanel.BLACK
                : Chesswindowpanel.WHITE;
    }

    /*
     * =========================
     * GETTERS
     * =========================
     */
    public Collection<Piece> getAllPieces() {
        final List<Piece> allPieces = new ArrayList<>(
                this.whitePieceCoordinates.length + this.blackPieceCoordinates.length);
        for (final int index : this.whitePieceCoordinates) {
            allPieces.add(this.boardPieces[index]);
        }
        for (final int index : this.blackPieceCoordinates) {
            allPieces.add(this.boardPieces[index]);
        }
        return Collections.unmodifiableList(allPieces);
    }


    public Piece getPiece(int col, int row) {

        if (row < 0 || row >= MAX_ROW || col < 0 || col >= MAX_COL) {
            return null;
        }
        return board[row][col];
    }

    public Alliance getPieceAllegiance() {
        return alliance;
    }

    /*
     * =========================
     * BOARD SETUP
     * =========================
     */
    public void setPieces(ArrayList<Piece> newPieces) {
        pieces = newPieces;
        rebuildBoard();
    }

    private void rebuildBoard() {
        board = new Piece[MAX_ROW][MAX_COL];
        for (Piece p : pieces) {
            board[p.row][p.col] = p;
        }
    }

    /*
     * =========================
     * MOVE / UNDO
     * =========================
     */
    private final PSQT psqt = new PSQT();

    public int getEvaluation() {
        return evaluation;
    }

     public boolean isEmpty(int col, int row) {
        return getPiece(col, row) == null;
    }

    public boolean isEnemy(int col, int row, boolean isWhite) {
        Piece p = getPiece(col, row);
        return p != null && p.isWhite() != isWhite;
    }

    /*
     * =========================
     * CHECK
     * =========================
     */
    public boolean isKingInCheck(int color) {
        Piece king = null;

        for (Piece p : pieces) {
            if (p.color == color && p.type == Types.KING) {
                king = p;
                break;
            }
        }
        if (king == null) {
            return false;

        }

        for (Piece p : pieces) {
            if (p.color != color && p.canMove(king.col, king.row)) {
                return true;
            }
        }
        return false;
    }

    public void initializeEvaluation() {
        evaluation = 0;
        for (Piece p : pieces) {
            int sq = p.row * 8 + p.col;
            if (!p.isWhite()) {
                sq ^= 56;
            }

            evaluation += psqt.getPieceTableValue(p.getType(), p.col, p.row, p.isWhite());
        }
    }

       private static Chessboard createStandardBoardImpl() {
        Builder builder = new Chessboard.Builder();

        builder.setMoveMaker(Alliance.WHITE);

        for (Piece p : Chesswindowpanel.pieces) {
            builder.setPiece(p); // boardPieces[piecePosition] = p
        }

        Chessboard board = builder.build();

        board.pieces = new ArrayList<>(Chesswindowpanel.pieces);

        board.rebuildBoard();

        board.initializeEvaluation();

        System.out.println("Pieces on board: " + board.pieces.size());
        System.out.println("White pieces: " + board.whitePieceCoordinates.length);
        System.out.println("Black pieces: " + board.blackPieceCoordinates.length);

        return board;
    }

    public Collection<Move> getAllLegalMoves() {
        return Stream.concat(this.whiteplayer.getLegalMoves().stream(),
                this.blackPlayer.getLegalMoves().stream())
                .collect(Collectors.toList());
    }

    private Collection<Move> calculateLegalMoves(final Piece[] boardConfig,
            final int[] pieces) {
        final Collection<Move> legalsMove = new ArrayList<>();
        for (final int piece_index : pieces) {
            legalsMove.addAll(boardConfig[piece_index].calculateLegalMoves(this));
        }
        System.out.println("Legals move are these:" + legalsMove);
        return legalsMove;
    }

    //
    // private Collection<Move> calculateLegalMoves(Alliance alliance) {
    //
    // List<Move> legalMoves = new ArrayList<>();
    //
    // for(int i = 0; i < 64; i++) {
    //
    // Piece piece = board[i];
    //
    // if(piece != null && piece.getPieceAllegiance() == alliance) {
    //
    // legalMoves.addAll(piece.calculateLegalMoves(this));
    //
    // }
    //
    // }
    //
    // return legalMoves;
    // }

    private static int[] calculateActiveIndexes(final Piece[] boardConfig, final Alliance alliance) {

        final int[] result = new int[boardConfig.length];
        int count = 0;
        for (int idx = 0; idx < boardConfig.length; idx++) {
            final Piece piece = boardConfig[idx];
            if (piece != null && piece.getPieceAllegiance() == alliance) { // I need to create this method in class
                                                                           // Pieces
                result[count++] = idx;
            }
        }
        return Arrays.copyOf(result, count);
    }

    private static King establishKing(final int[] activeIndexes, final Piece[] boardConfig) {

        for (final int index : activeIndexes) {
            final Piece piece = boardConfig[index];
            if (piece.getPieceType() == Types.KING) {
                return (King) piece;
            }
        }
        // throw new RuntimeException("No king found for player!");
        return null; // TODO : more work here
    }

    static int movePiece(Move bestMove) {
        return 0; // TODO: more work here
    }

    public static class Builder {

        Piece[] boardPieces = new Piece[64];
        Alliance nextMoveMaker;
        Pawn enPassantPawn;

        public Builder() {
            this.boardPieces = new Piece[BoardUtils.NUM_TILES];
        }

        public Builder setBoardConfig(final Piece[] boardConfig) {
            this.boardPieces = boardConfig;
            return this;
        }

        public Builder setPiece(final Piece piece) {
            this.boardPieces[piece.getPiecePosition()] = piece;
            return this;
        }

        public Builder setMoveMaker(final Alliance nextMoveMaker) {
            
                this.nextMoveMaker = nextMoveMaker;
            
            return this;
        }

        public Builder setEnPassantPawn(final Pawn enPassantPawn) {
            this.enPassantPawn = enPassantPawn;
            return this;
        }

        public Builder setBoardConfiguration(Piece[] newBoardConfig) {
            this.boardPieces = newBoardConfig;
            return this;
        }

        public Chessboard build() {
            return new Chessboard(this);
        }

        // public Chessboard execute() {
        //
        // Piece[][] newBoard = board.clone();
        //
        // newBoard[movedPiece.getPiecePosition()] = null;
        //
        // newBoard[destinationCoordinate]
        // = movedPiece.getMovedPiece(this);
        //
        // return new Chessboard.Builder()
        // .setBoardConfiguration(newBoard)
        // .setMoveMaker(board.currentPlayer().getOpponent().getAlliance())
        // .build();
        // }
    }

    /*
     * =========================
     * DRAW
     * =========================
     */
    public void draw(Graphics2D g2) {
        int c = 0;
        for (int r = 0; r < MAX_ROW; r++) {
            for (int col = 0; col < MAX_COL; col++) {
                g2.setColor(c == 0
                        ? new Color(210, 165, 125)
                        : new Color(175, 115, 70));
                g2.fillRect(col * SQUARE_SIZE, r * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
                c = 1 - c;
            }
            c = 1 - c;
        }
    }
}

