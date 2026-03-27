/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Chess.Pieces.piece;

import Chess.Alliance;
import Chess.Chessboard;
import Chess.Chesswindowpanel;
import Chess.Move;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Admin
 */
public class King extends Piece {

    public King king;
    private boolean isCastled;
    private boolean kingSideCastleCapable;
    private boolean queenSideCastleCapable;

     // GUI konstruktor
    public King(int color, int col, int row, boolean isGui) {
        super(color, col, row, isGui);
        this.type = Types.KING;
        this.piecePosition = row * 8 + col;
        this.pceCol = col;
        this.pceRow = row;

        if (color == Chesswindowpanel.WHITE) {
            image = getImage("/Chess/Pieces/piece/w-king");
        } else {
            image = getImage("/Chess/Pieces/piece/b-king");
        }
    }
    
     // ENGINE delegátor, pokud chceš convenience overload
    public King(final Alliance alliance, final int piecePosition) {
        this(alliance, piecePosition, false);
    }

    // ENGINE konstruktor
    public King(final Alliance alliance, final int piecePosition, final boolean isFirstMove) {
        super(alliance, piecePosition, isFirstMove);

        type = Types.KING;

        if (alliance == Alliance.WHITE) {
            image = getImage("/Chess/Pieces/piece/w-king");
        } else {
            image = getImage("/Chess/Pieces/piece/b-king");
        }
    }


    // I pass the col and the row of a square and check if this King can move to
    // this square
    @Override
    // this targetCol and targetRow is the square that the player is selecting with
    // their mouse
    public boolean canMove(int targetCol, int targetRow) {
        // So if the targeted square is within the board maybe this King can move
        if (isWithinBoard(targetCol, targetRow)) {
            // I check 8 directions(left,right,up and down)and pceCol and pceRow are
            // basically in this logic the King's previous position
            if (Math.abs(targetCol - pceCol) + Math.abs(targetRow - pceRow) == 1 || // Math.abs()is basically in this
                                                                                    // situation a function
            // that returns the difference between two values and one of these answers must
            // be 1 and the other must be 1
                    Math.abs(targetCol - pceCol) * Math.abs(targetRow - pceRow) == 1) {
                if (isValidSquare(targetCol, targetRow)) {
                    return true; // the two answers muss be one and which covers these four diagonal squares
                }
            }
            // Castling
            if (hasmoved == false) {
                // Right castling
                if (targetCol == pceCol + 2 && targetRow == pceRow
                        && pieceIsOnStraightLine(targetCol, targetRow) == false) {
                    if (isSquareAttacked(pceCol + 1, pceRow) == false
                            && isSquareAttacked(pceCol + 2, pceRow) == false) {
                        for (Piece pieces : Chesswindowpanel.simPieces) {
                            if (pieces.col == pceCol + 3 && pieces.row == pceRow && pieces.hasmoved == false) {
                                Chesswindowpanel.castlingP = pieces; // castlingP = Rook
                                return true;
                            }
                        }
                    }
                }
                // Left castling
                if (targetCol == pceCol - 2 && targetRow == pceRow
                        && pieceIsOnStraightLine(targetCol, targetRow) == false) {
                    if (!isSquareAttacked(pceCol - 1, pceRow) && !isSquareAttacked(pceCol - 2, pceRow)) {
                        Piece p[] = new Piece[3];
                        for (Piece pieces : Chesswindowpanel.simPieces) { // I scan the simPieces
                            if (pieces.col == pceCol - 3 && pieces.row == targetRow) {
                                p[0] = pieces; // If there is a piece with col-3, then put the piece in the slot 0
                            }
                            if (pieces.col == pceCol - 4 && pieces.row == targetRow) {
                                p[1] = pieces;
                            }
                            if (pieces.col == pceCol - 2 && pieces.row == targetRow) {
                                p[2] = pieces;
                            }
                            if (p[0] == null && p[2] == null && p[1] != null && p[1].hasmoved == false) {
                                Chesswindowpanel.castlingP = p[1];
                                return true;
                            }

                        }
                    } else if (hittingP != null) {
                    }
                }
            }
        }
        return false; // the King cannnot move to this square
    }

    public boolean isSquareAttacked(int col, int row) {
        for (Piece piece : Chesswindowpanel.simPieces) {
            // If it is a opponent's piece
            if (piece.color != this.color) {
                // If a piece can legally move to a given square, it means that it is endangered
                if (piece.canMove(col, row)) {
                    return true;
                }
            }
        }
        return false; // The pole is not under attack
    }
  
     public King getKing() {
        return king;
    }

    public boolean isCastled() {
        return this.isCastled;
    }

    public void setCastled(boolean isCastled) {
        this.isCastled = isCastled;
    }

    public boolean isKingSideCastleCapable() {
        return this.kingSideCastleCapable;
    }

    public boolean isQueenSideCastleCapable() {
        return this.queenSideCastleCapable;
    }

    @Override
    public int getPiecePosition() {
        return this.piecePosition;
    }

    @Override
    public int locationBonus() {
       return this.pieceAlliance.kingBonus(this.piecePosition);   
    }

    @Override
    public Piece getMovedPiece(Move move) {
        return new King(this.pieceAlliance,move.getDestinationCoordinate(),false);   
    }    

    @Override
    public Collection<Move> calculateLegalMoves(Chessboard board) {

        List<Move> legalMoves = new ArrayList<>();

        for (int r = -1; r <= 1; r++) {
            for (int c = -1; c <= 1; c++) {

                if (r == 0 && c == 0) {
                    continue;
                }

                int columns = pceCol + c;
                int rows = pceRow + r;

                if (columns < 0 || columns > 7 || rows < 0 || rows > 7) {
                    continue;
                }

                Piece target = board.getPiece(columns, rows);

                if (target == null || target.color != this.color) {
                    legalMoves.add(new Move(board, this, columns, rows));
                }
            }
        }

        return legalMoves;
    }
}
}
