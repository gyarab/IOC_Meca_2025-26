/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Chess.Pieces.piece;
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
    
    public Queen(int color, int col, int row, final boolean isFirstMove) {
        super(color, col, row, true);
        
        type = Types.QUEEN;
    
    if(color == Chesswindowpanel.WHITE) {
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
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Piece getMovedPiece(Move move) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
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