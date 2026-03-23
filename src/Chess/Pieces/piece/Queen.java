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
public class Queen extends Piece {
    
   
    // GUI konstruktor
    public Queen(int color, int col, int row, boolean isFirstMove) {
        super(color, col, row, isFirstMove);
        this.type = Types.QUEEN;
        this.piecePosition = row * 8 + col;
        this.pceCol = col;
        this.pceRow = row;

        if (color == Chesswindowpanel.WHITE) {
            image = getImage("/Chess/Pieces/piece/w-queen");
        } else {
            image = getImage("/Chess/Pieces/piece/b-queen");
        }
    }
    
     // ENGINE delegátor, pokud chceš convenience overload
    public Queen(final Alliance alliance, final int piecePosition) {
        this(alliance, piecePosition,false);
    }

    // ENGINE konstruktor
    public Queen(final Alliance alliance, final int piecePosition, final boolean isFirstMove) {
        super(alliance, piecePosition, isFirstMove);

        type = Types.QUEEN;

        if (alliance == Alliance.WHITE) {
            image = getImage("/Chess/Pieces/piece/w-queen");
        } else {
            image = getImage("/Chess/Pieces/piece/b-queen");
        }
    }


  @Override
  public boolean canMove(int targetCol, int targetRow){
      if(isWithinBoard(targetCol, targetRow)&& isSameSquare(targetCol, targetRow) == false){
          
          //Vertical & Horizontal
          if(targetCol == pceCol || targetRow == pceRow){
              if(isValidSquare(targetCol, targetRow) && pieceIsOnStraightLine(targetCol, targetRow) == false){
                  return true;
              }
          }
         //Diagonal
         if(Math.abs(targetCol-pceCol)== Math.abs(targetRow-pceRow)){ //Diagonal lines always have the same differences between column and row.
             if(isValidSquare(targetCol, targetRow) && pieceIsOnDiagonalLine(targetCol, targetRow) == false){
                 return true;
             }
         }
      }
      return false;
  }  

    @Override
    public int locationBonus() {
    return this.pieceAlliance.queenBonus(this.piecePosition);
    }

    @Override
    public Piece getMovedPiece(Move move) {
        return new Queen(this.pieceAlliance,move.getDestinationCoordinate(),false);
    }

    @Override
    public Collection<Move> calculateLegalMoves(Chessboard board) {
        
        List<Move> legalMoves = new ArrayList<>();

        int[][] directions = {
            {1, 0}, {-1, 0}, {0, 1}, {0, -1},
            {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
        };

        for (int[] d : directions) {

            int col = pceCol;
            int row = pceRow;

            while (true) {

                col += d[0];
                row += d[1];

                if (col < 0 || col > 7 || row < 0 || row > 7) {
                    break;
                }

                Piece target = board.getPiece(row, col);

                if (target == null) {
                    legalMoves.add(new Move(board, this, col, row));
                } else {

                    if (target.color != this.color) {
                        legalMoves.add(new Move(board, this, col, row));
                    }

                    break;
                }
            }
        }

        return legalMoves;
    }
  }