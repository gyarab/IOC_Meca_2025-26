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
public class Knight extends Pieces {
    
    public Knight(int color, int col, int row) {
        super(color, col, row);
        
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
  }