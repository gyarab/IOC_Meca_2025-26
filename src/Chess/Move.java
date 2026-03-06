package Chess;

import Chess.Pieces.piece.Pieces;
import java.util.Objects;

public abstract class Move {

    protected final Chessboard board;
    protected final Pieces movedPiece;
    protected final int destinationCoordinate;
    protected final boolean isFirstMove;
    protected final int targetRow = 0;
    protected final int targetCol = 0;
   

    public Pieces capturedPiece;
    public Pieces piece;
    public int value;
    

    
    private Move(final Chessboard board, final Pieces pieceMov,
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
    
    @Override
    public int hashCode(){
        int result = 1;
        result = 31 * result + this.destinationCoordinate;
        result = 31 * result + this.movedPiece.hashCode();
        result = result + (isFirstMove ? 1 : 0);
        return result;
    }

    @Override
    public boolean equals(final Object oth) {
        if (this == oth) {
            return true;
        }
        if (oth == null) {
            return false;
        }
        if(!(oth instanceof Move)) {
            return false;
        }
        
        if (getClass() != oth.getClass()) {
            return false;
        }
        
        final Move othMove = (Move) oth; // přetypovaná proměnná typu Object
                
        if (this.destinationCoordinate != othMove.destinationCoordinate) {
            return false;
        }
        
        if(this.movedPiece != othMove.movedPiece) {
            return false;
        }
        
        return getCurrentCoordinate() == othMove.getCurrentCoordinate() &&
                getDestinationCoordinate() == othMove.getDestinationCoordinate() &&
                getMovedPiece().equals(othMove.getMovedPiece());
    }
    
    public Chessboard getBoard(){
        return this.board;
    }
    
    public int getCurrentCoordinate(){
        return this.movedPiece.getPiecePosition();
    }
    
    public int getDestinationCoordinate(){
        return this.destinationCoordinate;
    }
    
    public Pieces getMovedPiece(){
        return this.movedPiece;
    }
    
     public boolean isAttack() {
        return false; // default value
    }

    public boolean isCastlingMove() {
        return false; // default move
    }

    public Pieces getAttackedPiece() {
        return null;
    }

    public Pieces getPiece() {
        return movedPiece;
    }
    
    public int getDestinationRow(){
        return destinationCoordinate;
    }
    
    
    public Chessboard execute() {
        final Pieces[] newBoardConfig = board.getBoardCopy(); // i need to create this method in class Chessboard
        newBoardConfig[this.movedPiece.getPiecePosition()] = null;
        newBoardConfig[this.destinationCoordinate] = this.movedPiece.getMovedPiece(this);
        final Chessboard.Builder builder = new Chessboard.Builder();
        return builder.setBoardConfiguration(newBoardConfig)
                .setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance())
                .build();
    }

    
}
