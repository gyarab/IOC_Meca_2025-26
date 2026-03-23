/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Chess.Pieces.piece;

import Chess.Alliance;
import Chess.Chessboard;
import Chess.Chessboard.Builder;
import Chess.Chesswindowpanel;
import Chess.Move;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import javax.imageio.ImageIO;

/**
 *
 * @author Admin
 */
public abstract class Piece {

    public Alliance pieceAlliance;
    public Types type;
    public BufferedImage image;
    public int x, y;
    public int col, row, pceCol, pceRow;
    public int color;
    public Piece hittingP;
    public Piece activeP;
    public int piecePosition;
    public boolean hasmoved, twoStepped;
    private final boolean isFirstMove;
    private final int cachedHashCode;
   
    // GUI konstruktor volající ten hlavní
    public Piece(int color, int col, int row, final boolean isFirstMove) {
        this(color == Chesswindowpanel.WHITE ? Alliance.WHITE : Alliance.BLACK,
                row * 8 + col,
                isFirstMove);
    }

    //ENGINE konstruktor
    public Piece(final Alliance alliance, final int piecePosition, final boolean isFirstMove) {
        this.pieceAlliance = alliance;
        // ADD THIS: Sync the int color with the Alliance
        this.color = alliance.isWhite() ? Chesswindowpanel.WHITE : Chesswindowpanel.BLACK;
        this.piecePosition = piecePosition;
        System.out.println("Pozice figurky je:" + piecePosition);
        this.isFirstMove = isFirstMove;
        // spočítáme col a row z indexu 0..63
        this.row = piecePosition / 8;
        this.col = piecePosition % 8;
        this.pceCol = this.col;
        this.pceRow = this.row;
        this.x = getX(this.col);
        this.y = getY(this.row);
        this.cachedHashCode = computeHashCode();
    }

    public BufferedImage getImage(String imagePath) {
        BufferedImage image = null;

        try {
            image = ImageIO.read(getClass().getResourceAsStream(imagePath + ".png"));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public int getX(int col) {
        return col * Chessboard.SQUARE_SIZE;
    }

    public int getY(int row) {
        return row * Chessboard.SQUARE_SIZE;

    }

    public int getCol() {
        //I addded HALF_SQUARE_SIZE because this piece's x and y is the top left and that is not correct, now after adding HALF_SQUARE_SIZE
        //program will be know, where the object is
        // and to detect its col and row based on the center point of the piece
        return (x + Chessboard.HALF_SQUARE_SIZE) / Chessboard.SQUARE_SIZE;
    }

    public int getRow() {
        return (y + Chessboard.HALF_SQUARE_SIZE) / Chessboard.SQUARE_SIZE;
    }

    public int getIndex() {
        for (int index = 0; index < Chesswindowpanel.simPieces.size(); index++) {
            if (Chesswindowpanel.simPieces.get(index) == this) {
                return index;
            }
        }
        return 0;
    } // it is used for searching the index of the current chess piece (this) in the list of all chess pieces

    public Types getType() {
        return type;
    }

    public int getColor() {
        return color;
    }

    public Types getPieceType() {
        return type;
    }

    public int getPiecePosition() {
        return piecePosition;
    }

    public Alliance getPieceAllegiance() {
        return pieceAlliance;
    }

    public boolean isWhite() {
        return this.color == Chesswindowpanel.WHITE;
    }

    public boolean isBlack() {
        return this.color == Chesswindowpanel.BLACK;
    }

    public boolean isFirstMove() {
        return this.isFirstMove;
    }

    public int getPieceValue() {
        return type.getPieceValue();
    } 

    public Collection<Move> getLegalMoves(Chessboard board) {
        
        System.out.println("Legální thay jsou:" + calculateLegalMoves(board));

        return calculateLegalMoves(board);

    }
    
    public abstract int locationBonus();

    public abstract Piece getMovedPiece(Move move);

    public abstract Collection<Move> calculateLegalMoves(final Chessboard board);

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Piece)) {
            return false;
        }
        final Piece otherPiece = (Piece) other;
        return this.piecePosition == otherPiece.piecePosition && this.type == otherPiece.type &&
               this.pieceAlliance == otherPiece.pieceAlliance && this.isFirstMove == otherPiece.isFirstMove;
    }

    @Override
    public int hashCode() {
        return this.cachedHashCode;
    }

    private int computeHashCode() {
        if (type == null) {
            return 0;
        }
        int result = this.type.hashCode();
        result = 31 * result + this.pieceAlliance.hashCode();
        result = 31 * result + this.piecePosition;
        result = 31 * result + (this.isFirstMove ? 1 : 0);
        return result;
    }

    public void updatePosition() {

        //To check En passant(Is pawn or not)
        if (type == Types.PAWN) {
            if (Math.abs(row - pceRow) == 2) { //If it's pawn, we check if the row difference is 2(so the pawn moved by 2 squares)
                twoStepped = true;
            }
        }
        //So I update it's X and Y based on it's current col and row
        x = getX(col);
        y = getY(row);
        pceCol = getCol(); //also I update these previous col and row since the move has been confirmed
        //and the piece has moved to a new square
        pceRow = getRow();

        // PŘIDEJ TENTO ŘÁDEK - ZÁCHRANA PŘED TELEPORTACÍ:
        this.piecePosition = this.pceRow * 8 + this.pceCol;
    }

    public void resetPosition() {
        col = pceCol;
        row = pceRow;
        x = getX(col);
        y = getY(row);
        // hasmoved = true; //the piece has moved

    }

    public boolean canMove(int targetCol, int targetRow) {
        return false; //I don't do anything else cause I am going to override it in each piece class
    }
    //I check if the player's mouse is pointing a place on the board

    public boolean isWithinBoard(int targetCol, int targetRow) {
        if (targetCol >= 0 && targetCol <= 7 && targetRow >= 0 && targetRow <= 7) {
            return true; //the square is within the board
        }
        return false; //otherwise return false
    }

    public boolean isSameSquare(int targetCol, int targetRow) {
        //If the target square on the column or row is equal to the original square on which the tower stood, it is the same square
        if (targetCol == pceCol && targetRow == pceRow) {
            return true;
        }
        return false;
    }

    public Piece getHittingP(int targetCol, int targetRow) {
        for (Piece pieces : Chesswindowpanel.simPieces) {
            if (pieces.col == targetCol && pieces.row == targetRow && pieces != this) {
                return pieces; //It is used to search for a chess piece that is currently at the specified target position
            }
        }
        return null;
    }

        public boolean isValidSquare(int targetCol, int targetRow) {

        // Získáme figurku, která případně stojí na cílovém poli
        hittingP = null;
        for (Piece piece : Chesswindowpanel.simPieces) {
            if (piece.col == targetCol && piece.row == targetRow && piece != this) {
                hittingP = piece;
                break;
            }
        }

        if (hittingP == null) { // Pole je prázdné
            return true;
        } else { // Pole je obsazené
            if (hittingP.color != this.color) { // Můžeme vzít nepřátelskou
                return true;
            } else {   // Vlastní figurka, sem nemůžeme
                hittingP = null;
                return false; // Přidáno false!
            }
        }
    }

    public boolean pieceIsOnStraightLine(int targetCol, int targetRow) {
        //When this piece is moving to the left
        for (int c = pceCol - 1; c > targetCol; c--) { //passes all the fields to the left of pceCol until the values are greater than targetCol
            for (Piece pieces : Chesswindowpanel.simPieces) { //I scan the simPieces  
                if (pieces.col == c && pieces.row == targetRow) { //and check if there is a piece on the square
                    hittingP = pieces;
                    return true;
                }
            }
        }
        //When this piece is moving to the right
        for (int c = pceCol + 1; c < targetCol; c++) { //passes all the fields to the rigth of pceCol until the values are smaller than targetCol
            for (Piece pieces : Chesswindowpanel.simPieces) { //I scan the simPieces  
                if (pieces.col == c && pieces.row == targetRow) { //and check if there is a piece on the square
                    hittingP = pieces;
                    return true;
                }
            }
        }
        //When this piece is moving up
        for (int r = pceRow - 1; r > targetRow; r--) { //passes all the fields to the up from the pceRow until the values are greater than targetRow
            for (Piece pieces : Chesswindowpanel.simPieces) { //I scan the simPieces  
                if (pieces.col == targetCol && pieces.row == r) { //and check if there is a piece on the square
                    hittingP = pieces;
                    return true;
                }
            }
        }
        //When this piece is moving down
        for (int r = pceRow + 1; r < targetRow; r++) { //loops all the fields to the down from the pceRow until the values are smaller than targetRow
            for (Piece pieces : Chesswindowpanel.simPieces) { //I scan the simPieces  
                if (pieces.col == targetCol && pieces.row == r) { //and check if there is a piece on the square
                    hittingP = pieces;
                    return true;
                }
            }
        }

        return false; //if none of them returns true that means there is a no piece on the line so I return false
    }

    public boolean pieceIsOnDiagonalLine(int targetCol, int targetRow) {
        if (targetRow < pceRow) { //The piece is moving either up left or up right diagonally
            //Up left
            for (int c = pceCol - 1; c > targetCol; c--) { //passes all the field to the left of the pceCol until the values are greater than targetCol
                int dif = Math.abs(c - pceCol); //I get the difference of the currently checking col and the pceCol  
                for (Piece pieces : Chesswindowpanel.simPieces) { //I scan the list and check if there is a piece that has the same col and row
                    if (pieces.col == c && pieces.row == pceRow - dif) { //dif is used to determine the line position -> might be occupied by piece
                        hittingP = pieces;
                        return true;
                    }
                }
            }
            //Up right
            for (int c = pceCol + 1; c < targetCol; c++) { //passes all the field to the right of the pceCol until the values are smaller than targetCol
                int dif = Math.abs(c - pceCol); //I get the difference of the currently checking col and the pceCol  
                for (Piece pieces : Chesswindowpanel.simPieces) { //I scan the list and check if there is a piece that has the same col and row
                    if (pieces.col == c && pieces.row == pceRow - dif) { //dif is used to determine the line position -> might be occupied by piece
                        hittingP = pieces;
                        return true;
                    }
                }
            }
        }
        if (targetRow > pceRow) { //The piece is moving either down left or down right diagonally
            //Down left
            for (int c = pceCol - 1; c > targetCol; c--) {
                int dif = Math.abs(c - pceCol); //I get the difference of the currently checking col and the pceCol  
                for (Piece pieces : Chesswindowpanel.simPieces) { //I scan the list and check if there is a piece that has the same col and row
                    if (pieces.col == c && pieces.row == pceRow + dif) { //dif is used to determine the line position -> might be occupied by piece
                        hittingP = pieces;
                        return true;
                    }
                }
            }
            //Down right
            for (int c = pceCol + 1; c < targetCol; c++) {
                int dif = Math.abs(c - pceCol); //I get the difference of the currently checking col and the pceCol  
                for (Piece pieces : Chesswindowpanel.simPieces) { //I scan the list and check if there is a piece that has the same col and row
                    if (pieces.col == c && pieces.row == pceRow + dif) { //dif is used to determine the line position -> might be occupied by piece
                        hittingP = pieces;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean IsPieceNearTheKing(int targetCol, int targetRow) {
        for (Piece pieces : Chesswindowpanel.simPieces) {
            if (pieces.col == 1 && pieces.row == 0) {
                if (pieces.col == 6 && pieces.row == 0) {
                    System.out.println("Skvele");
                    return true;
                }
            }
        }
        return false;
    }

    public void draw(Graphics2D g2) {
        if (image != null) {
            g2.drawImage(image, x, y, Chessboard.SQUARE_SIZE, Chessboard.SQUARE_SIZE, null);
        } else {
            System.out.println("Obrázek nebyl načten!");
        }
    }

    public Iterable<Move> getLegalMoves(Piece[][] boardPieces) {
        return null; //TODO: more work here
    }

     @Override
    public String toString() {
        //TODO dodelat
        return type.toString();
    }

    }
    


