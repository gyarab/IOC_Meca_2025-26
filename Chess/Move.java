package Chess;

import Chess.Pieces.piece.Pieces;

public class Move {
    public Pieces piece;

    public int startCol;
    public int startRow;

    public int targetCol;
    public int targetRow;

    public Pieces capturedPiece;
    public int value;

    public Move(Pieces piece,
                int startCol, int startRow,
                int targetCol, int targetRow,
                Pieces capturedPiece,
                int value) {

        this.piece = piece;
        this.startCol = startCol;
        this.startRow = startRow;
        this.targetCol = targetCol;
        this.targetRow = targetRow;
        this.capturedPiece = capturedPiece;
        this.value = value;
    }

    static enum MoveStatus {

        DONE, //move has done
        ILLEGAL_MOVE, //not legal move
        LEAVES_KING_IN_CHECK; //after move the king is in check    
    }
}
