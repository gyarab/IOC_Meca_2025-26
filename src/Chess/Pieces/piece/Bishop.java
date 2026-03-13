/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Chess.Pieces.piece;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import Chess.Chessboard;
import Chess.Chesswindowpanel;
import Chess.Move;

/**
 *
 * @author Admin
 */
public class Bishop extends Piece {
    
    public Bishop(int color, int col, int row, final boolean isFistMove) {
        super(color, col, row, true);
        
        type = Types.BISHOP;
    
    if(color == Chesswindowpanel.WHITE) {
         image = getImage("/Chess/Pieces/piece/w-bishop");
    } else {
        image = getImage("/Chess/Pieces/piece/b-bishop");
    }   
}
    @Override
  public boolean canMove(int targetCol, int targetRow){
     if(isWithinBoard(targetCol, targetRow)&& isSameSquare(targetCol, targetRow) == false){
         if(Math.abs(targetCol-pceCol) == Math.abs(targetRow-pceRow)){ //the col difference and the row difference always need to be equal -> diagonal move
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
                        legalMoves.add(new Move(board, this, col,row));
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