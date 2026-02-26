package Chess;

import Chess.Pieces.piece.*;
import java.util.*;
import java.awt.Color;
import java.awt.Graphics2D;

public final class Chessboard {

    public static final int MAX_COL = 8;
    public static final int MAX_ROW = 8;
    public static final int SQUARE_SIZE = 100;
    public static final int HALF_SQUARE_SIZE = SQUARE_SIZE / 2;
    
   private final Pieces[] boardPieces;
   private final int[] whitePieceCoordinates;
   private final int[] blackPieceCoordinates;
   private final WhitePlayer whiteplayer;
   private final BlackPlayer blackPlayer;
   private final Player currentPlayer;
   private final Pawn enPassantPawn;
    
    private int evaluation; //actual value of position
    private Pieces[][] board = new Pieces[MAX_ROW][MAX_COL];
    private ArrayList<Pieces> pieces = new ArrayList<>();
    private Stack<Move> moveHistory = new Stack<>();
    
//    private static final Chessboard STANDARD_BOARD = createStandardBoardImpl();

    private int sideToMove = Chesswindowpanel.WHITE;

    Chessboard(Builder builder) {
        this.boardPieces = builder.boardPieces;
        this.whitePieceCoordinates = calculateActiveIndexes(boardPieces, Alliance.WHITE);
        this.blackPieceCoordinates = calculateActiveIndexes(boardPieces, Alliance.BLACK);
        this.enPassantPawn = builder.enPassantPawn;
        final Collection<Move> whiteStandardMoves = calculateLegalMoves(boardPieces, this.whitePieceCoordinates);
        final Collection<Move> blackStandardMoves = calculateLegalMoves(boardPieces, this.blackPieceCoordinates);
        this.whiteplayer = new WhitePlayer(this, establishKing(this.whitePieceCoordinates, this.boardPieces), whiteStandardMoves, blackStandardMoves);
        this.blackPlayer = new BlackPlayer(this, establishKing(this.blackPieceCoordinates, this.boardPieces), whiteStandardMoves, blackStandardMoves);
        this.currentPlayer = builder.nextMoveMaker.choosePlayerByAlliance(this.whiteplayer, this.blackPlayer);
    }
    
    
    public int getSideToMove() {
        return sideToMove;
    }
    
    public WhitePlayer whitePlayer(){
        return this.whiteplayer;
    }
    
    public BlackPlayer blackPlayer(){
        return this.blackPlayer;
    }
    
    public Player currentPlayer(){
        return this.currentPlayer;
    }
    
    public Pieces[] getBoardPieces(){
        return this.boardPieces;
    }
    
    public Pieces[] getPieces(){
        return this.boardPieces;
    }
    
     public Pieces[] getBoardCopy() {
        return this.boardPieces.clone();
    }
     
    public static Chessboard createStandardBoard() {
        return STANDARD_BOARD;
    } 
    
    public Pawn getEnPassantPawn(){
        return this.enPassantPawn;
    }
   
    
    public void switchSide() {
        sideToMove = (sideToMove == Chesswindowpanel.WHITE)
                ? Chesswindowpanel.BLACK
                : Chesswindowpanel.WHITE;
    }

    boolean isOpenFile(int col) {
        
        return true;
        
    }    

    /* =========================
       GETTERS
       ========================= */
       public Collection<Pieces> getAllPieces() {
        final List<Pieces> allPieces = new ArrayList<>(this.whitePieceCoordinates.length + this.blackPieceCoordinates.length);
        for (final int index : this.whitePieceCoordinates) {
            allPieces.add(this.boardPieces[index]);
        }
        for (final int index : this.blackPieceCoordinates) {
            allPieces.add(this.boardPieces[index]);
        }
        return Collections.unmodifiableList(allPieces);
    }

    public Pieces getPiece(int row, int col) {
        return board[row][col];
    }

    /* =========================
       BOARD SETUP
       ========================= */
    public void setPieces(ArrayList<Pieces> newPieces) {
        pieces = newPieces;
        rebuildBoard();
    }

    private void rebuildBoard() {
        board = new Pieces[MAX_ROW][MAX_COL];
        for (Pieces p : pieces) {
            board[p.row][p.col] = p;
        }
    }

    /* =========================
       MOVE / UNDO
       ========================= */
    private final PSQT psqt = new PSQT();
   
    public int getEvaluation(){
        return evaluation;
    }

    /* =========================
       CHECK
       ========================= */
    public boolean isKingInCheck(int color) {
        Pieces king = null;

        for (Pieces p : pieces) {
            if (p.color == color && p.type == Types.KING) {
                king = p;
                break;
            }
        }
        if (king == null){ 
            return false;
        
        }

        for (Pieces p : pieces) {
            if (p.color != color && p.canMove(king.col, king.row)) {
                return true;
            }
        }
        return false;
    }
    
    public void initializeEvaluation() {
        evaluation = 0;
        for (Pieces p : pieces) {
        int sq = p.row * 8 + p.col;
        if (!p.isWhite()){ 
            sq ^= 56;
        }

        evaluation += p.isWhite()
            ? psqt.getPieceTableValue(p.getType(), sq)
            : -psqt.getPieceTableValue(p.getType(), sq);
    }
}
    
    private static Chessboard createStandardBoardImpl(){
      final Builder builder = new Builder(); 
      //Black layout
      //White layout
      //return builder.build();
      return null;
    }
    
    public Collection<Move> getAllLegalMoves() {
    return Stream.concat(this.whiteplayer.getLegalMoves().stream(),
                         this.blackPlayer.getLegalMoves().stream())
                         .collect(Collectors.toList());
    }

    private Collection<Move> calculateLegalMoves(final Pieces[] boardConfig, 
                                                 final int[] pieces){
        final Collection<Move> legalsMove = new ArrayList<>();
        for(final int piece_index : pieces) {
           legalsMove.addAll(boardConfig[piece_index].calculateLegalMoves(this));  
        }
         System.out.println("Legals move are these:" + legalsMove);
        return legalsMove;
    }
    
    private static int[] calculateActiveIndexes(final Pieces[] boardConfig, final Alliance alliance){
        
        final int[] result = new int[boardConfig.length];
        int count = 0;
        for(int idx = 0; idx < boardConfig.length; idx++){
            final Pieces piece = boardConfig[idx];
            if(piece != null && piece.getPieceAllegiance() == alliance){ // I need to create this method in class Pieces
                result[count++] = idx;
            }
        }
        return Arrays.copyOf(result, count);
    }
    
    private static King establishKing(final int[] activeIndexes, final Pieces[] boardConfig){
        
        for(final int index : activeIndexes) {
            final Pieces piece = boardConfig[index];
            if(piece.getPieceType() == Pieces.PieceType.KING){
                return (King) piece;
            }
        }
        throw new RuntimeException("No king found for player!");
    }
    
    public static class Builder {
        
        Pieces[] boardPieces;
        Alliance nextMoveMaker;
        Pawn enPassantPawn;
        
        public Builder() {
            this.boardPieces = new Pieces[BoardUtils.NUM_TILES];
        }
        
        public Builder setBoardConfig(final Pieces[] boardConfig) {
            this.boardPieces = boardConfig;
            return this;
        }
        
        public Builder setPiece(final Pieces piece) {
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
        
        public Builder build(){
           return new Chessboard(this);
        }
    }
    
    
    
    
    
    
    /* =========================
       DRAW
       ========================= */
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
