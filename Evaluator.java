/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Chess;

import Chess.Pieces.piece.Pieces;

/**
 *
 * @author mecova
 */
public class Evaluator {
    
    public static int evaluateBoard(Pieces[][] board) {
        int score = 0;
        
        for(int row = 0; row < 8; row++) {
            for(int col = 0; col < 8; col++){
                Pieces piece = board[row][col];
            if(piece != null) {
               int value = piece.getType().getValue();
               if(piece.getAlliance() == Alliance.WHITE){
                   score += value;
               } else {
                   score -= value;
               }
            }
            
            }
        }
        return score; // kladné = výhoda bílé, záporné = výhoda černé
    }
}
