/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Chess.Pieces.piece;
import Chess.Chessboard;
import Chess.Chesswindowpanel;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author Admin
 */
public class Pieces {
    
    public Types type;
    public BufferedImage image;
    public int x,y;
    public int col,row, pceCol, pceRow;
    public int color;
    public Pieces hittingP;
    public Pieces activeP;
    public boolean hasmoved, twoStepped;    
    private final boolean isFirstMove;
    
    public Pieces(int color,int col,int row, final boolean isFirstMove){
        this.color = color;
        this.col = col;
        this.row = row;
        x = getX(col);
        y = getY(row);
        this.pceCol = col;
        this.pceRow = row;
        this.isFirstMove = isFirstMove;
    }
    public BufferedImage getImage(String imagePath){
        BufferedImage image = null;
        
        try {
            image = ImageIO.read(getClass().getResourceAsStream(imagePath + ".png"));
        
        }catch(IOException e) {
            e.printStackTrace();
        }
        return image;
    }
  
    public int getX(int col){
        return col*Chessboard.SQUARE_SIZE; 
    }
    public int getY(int row){
        return row*Chessboard.SQUARE_SIZE;
        
    }
    public int getCol(int x){
       //I addded HALF_SQUARE_SIZE because this piece's x and y is the top left and that is not correct, now after adding HALF_SQUARE_SIZE
       //program will be know, where the object is
       // and to detect its col and row based on the center point of the piece
        return (x + Chessboard.HALF_SQUARE_SIZE)/Chessboard.SQUARE_SIZE;
    }
    public int getRow(int y){
        return (y + Chessboard.HALF_SQUARE_SIZE)/Chessboard.SQUARE_SIZE;
    }
    public int getIndex(){
        for(int index = 0; index < Chesswindowpanel.simPieces.size(); index++){
            if(Chesswindowpanel.simPieces.get(index) == this){
                return index;
            }
        }
        return 0;
    } // it is used for searching the index of the current chess piece (this) in the list of all chess pieces
    
    public Types getType(){
        return type;
    }
    
    public int getColor(){
        return color;
    }
    
    public void updatePosition(){
         
        //To check En passant(Is pawn or not)
        if(type == Types.PAWN){
            if(Math.abs(row-pceRow) == 2){ //If it's pawn, we check if the row difference is 2(so the pawn moved by 2 squares)
                twoStepped = true; 
            }
        }
        //So I update it's X and Y based on it's current col and row
        x = getX(col);
        y = getY(row);
        pceCol = getCol(x); //also I update these previous col and row since the move has been confirmed 
        //and the piece has moved to a new square
        pceRow = getRow(y);
    }
    public void resetPosition(){
        col = pceCol;
        row = pceRow;
        x = getX(col);
        y = getY(row);
       // hasmoved = true; //the piece has moved
        
    }
    public boolean canMove(int targetCol, int targetRow){
        return false; //I don't do anything else cause I am going to override it in each piece class
    }
   //I check if the player's mouse is pointing a place on the board
    public boolean isWithinBoard(int targetCol, int targetRow){
         if(targetCol >= 0 && targetCol <= 7 && targetRow >= 0 && targetRow <= 7) {
             return true; //the square is within the board
         }  
         return false; //otherwise return false
    }
   public boolean isSameSquare(int targetCol, int targetRow){
       //If the target square on the column or row is equal to the original square on which the tower stood, it is the same square
       if(targetCol == pceCol && targetRow == pceRow){
           return true;
       }
       return false;
   }
    public Pieces getHittingP(int targetCol, int targetRow){
        for(Pieces pieces : Chesswindowpanel.simPieces) {
            if(pieces.col == targetCol && pieces.row == targetRow && pieces != this){
                return pieces; //It is used to search for a chess piece that is currently at the specified target position
            }
        }
        return null;
    }
    public boolean isValidSquare(int targetCol,int targetRow){
       
        hittingP = getHittingP(targetCol, targetRow);
        
        if(hittingP == null){ //this square is not occupied by opponent's piece, so it's VACANT
            return true;
        } else { //this square is occupied
            if(hittingP.color!= this.color){ //If the color is different, it can be captured
                return true;
            } else{   //If the color is the same, the player cannot move to this square
                hittingP = null;
            }
        }
        return false;
    }
 
     public boolean pieceIsOnStraightLine(int targetCol, int targetRow){
        //When this piece is moving to the left
        for(int c = pceCol-1; c > targetCol; c--){ //passes all the fields to the left of pceCol until the values are greater than targetCol
            for(Pieces pieces:Chesswindowpanel.simPieces){ //I scan the simPieces  
                if(pieces.col == c && pieces.row == targetRow){ //and check if there is a piece on the square
                    hittingP = pieces;
                    return true;
                }
            }
        }
        //When this piece is moving to the right
            for(int c = pceCol+1; c < targetCol; c++){ //passes all the fields to the rigth of pceCol until the values are smaller than targetCol
            for(Pieces pieces:Chesswindowpanel.simPieces){ //I scan the simPieces  
                if(pieces.col == c && pieces.row == targetRow){ //and check if there is a piece on the square
                    hittingP = pieces;
                    return true;
                }
            }
        }
        //When this piece is moving up
         for(int r = pceRow-1; r > targetRow; r--){ //passes all the fields to the up from the pceRow until the values are greater than targetRow
            for(Pieces pieces:Chesswindowpanel.simPieces){ //I scan the simPieces  
                if(pieces.col == targetCol && pieces.row == r){ //and check if there is a piece on the square
                    hittingP = pieces;
                    return true;
                }
            }
         }
        //When this piece is moving down
          for(int r = pceRow+1; r < targetRow; r++){ //loops all the fields to the down from the pceRow until the values are smaller than targetRow
            for(Pieces pieces:Chesswindowpanel.simPieces){ //I scan the simPieces  
                if(pieces.col == targetCol && pieces.row == r){ //and check if there is a piece on the square
                    hittingP = pieces;
                    return true;
                }
            }
         }  
        
        return false; //if none of them returns true that means there is a no piece on the line so I return false
    }
    public boolean pieceIsOnDiagonalLine(int targetCol,int targetRow){
        if(targetRow< pceRow){ //The piece is moving either up left or up right diagonally
        //Up left
        for(int c = pceCol -1; c > targetCol; c--){ //passes all the field to the left of the pceCol until the values are greater than targetCol
         int dif = Math.abs(c-pceCol); //I get the difference of the currently checking col and the pceCol   
           for(Pieces pieces:Chesswindowpanel.simPieces){ //I scan the list and check if there is a piece that has the same col and row
               if(pieces.col == c && pieces.row == pceRow-dif){ //dif is used to determine the line position -> might be occupied by piece
                   hittingP = pieces;
                   return true;
               }
           }
        }
        //Up right
                for(int c = pceCol+1; c < targetCol; c++){ //passes all the field to the right of the pceCol until the values are smaller than targetCol
         int dif = Math.abs(c-pceCol); //I get the difference of the currently checking col and the pceCol   
           for(Pieces pieces:Chesswindowpanel.simPieces){ //I scan the list and check if there is a piece that has the same col and row
               if(pieces.col == c && pieces.row == pceRow-dif){ //dif is used to determine the line position -> might be occupied by piece
                   hittingP = pieces;
                   return true;
               }
           }
        }
        }
        if(targetRow> pceRow){ //The piece is moving either down left or down right diagonally 
        //Down left
            for(int c = pceCol -1; c > targetCol; c--){ 
         int dif = Math.abs(c-pceCol); //I get the difference of the currently checking col and the pceCol   
           for(Pieces pieces:Chesswindowpanel.simPieces){ //I scan the list and check if there is a piece that has the same col and row
               if(pieces.col == c && pieces.row == pceRow+dif){ //dif is used to determine the line position -> might be occupied by piece
                   hittingP = pieces;
                   return true;
               }
           }
        }
        //Down right
            for(int c = pceCol+1; c < targetCol; c++){ 
         int dif = Math.abs(c-pceCol); //I get the difference of the currently checking col and the pceCol   
           for(Pieces pieces:Chesswindowpanel.simPieces){ //I scan the list and check if there is a piece that has the same col and row
               if(pieces.col == c && pieces.row == pceRow+dif){ //dif is used to determine the line position -> might be occupied by piece
                   hittingP = pieces;
                   return true;
               }
           }
        }
        }
        return false;
    }

   
    public boolean IsPieceNearTheKing(int targetCol, int targetRow){
      for(Pieces pieces:Chesswindowpanel.simPieces){
          if(pieces.col == 1 && pieces.row == 0){
               if(pieces.col == 6 && pieces.row == 0){
              System.out.println("Skvele");
              return true;
                } 
           }
           }
      return false;
    }
    
    public boolean isWhite(){
        Pieces p = new Pieces(color, col, row, isFirstMove);
            if(p.color == Chesswindowpanel.WHITE){
                return true;
            } 
        return false;
    }
    
    public void draw(Graphics2D g2){
        if (image != null) {
    g2.drawImage(image, x, y,Chessboard.SQUARE_SIZE,Chessboard.SQUARE_SIZE, null);
} else {
    System.out.println("Obrázek nebyl načten!");
}
    }
}
