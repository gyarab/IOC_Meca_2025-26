/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Chess.Pieces.piece;
import Chess.Chesswindowpanel;

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
  }
