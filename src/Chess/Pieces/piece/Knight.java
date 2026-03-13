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
public class Knight extends Piece {
    
    public Knight(int color, int col, int row,final boolean isFirstMove) {
        super(color, col, row, true);
        
        type = Types.KNIGHT;
    
    if(color == Chesswindowpanel.WHITE) {
         image = getImage("/Chess/Pieces/piece/w-knight");
    } else {
        image = getImage("/Chess/Pieces/piece/b-knight");
    }
    
}
  @Override
  public boolean canMove(int targetCol,int targetRow){
      
      if(isWithinBoard(targetCol, targetRow)){
          //knight can move if its movement ratio of col and row is 1:2 or 2:1
          if(Math.abs(targetCol-pceCol)* Math.abs(targetRow-pceRow) == 2){
              //so one of these needs to be 1 and one of these needs to be 2
              if(isValidSquare(targetCol, targetRow)){
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

        int[][] moves = {
            {2, 1}, {1, 2}, {-1, 2}, {-2, 1},
            {-2, -1}, {-1, -2}, {1, -2}, {2, -1}
        };

        for (int[] m : moves) {

            int col = pceCol + m[0];
            int row = pceRow + m[1];

            if (col < 0 || col > 7 || row < 0 || row > 7) {
                continue;
            }

            Piece target = board.getPiece(row, col);

            if (target == null || target.color != this.color) {
                legalMoves.add(new Move(board, this, col, row));
            }
        }

        return legalMoves;
    }
  }