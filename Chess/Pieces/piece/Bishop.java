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
public class Bishop extends Pieces {
    
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
  }
