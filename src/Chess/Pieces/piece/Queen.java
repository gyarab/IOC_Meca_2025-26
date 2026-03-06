/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Chess.Pieces.piece;
import Chess.Chessboard;
import Chess.Chesswindowpanel;
import Chess.Move;
import java.util.Collection;

/**
 *
 * @author Admin
 */
public class Queen extends Pieces {
    
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
    public Pieces getMovedPiece(Move move) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Collection<Move> calculateLegalMoves(Chessboard board) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
  }