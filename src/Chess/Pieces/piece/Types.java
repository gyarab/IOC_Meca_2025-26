/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Chess.Pieces.piece;

/**
 *
 * @author Admin
 */
public enum Types {
   PAWN(100),
   KNIGHT(300),
   ROOK(500),
   QUEEN(900),
   BISHOP(300),
   KING(10000);
   
   private final int value;
   
   private Types(int value){
       this.value = value;
   }    
   
   public int getValue() {
       return this.value;
   }
}

