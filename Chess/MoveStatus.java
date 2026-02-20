/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package Chess;
/**
 *
 * @author mecova
 */
public class MoveStatus {
//This class is for AI 
    
    boolean legal;
    boolean check;
    boolean checkmate;
    boolean stalemate;
    boolean capture;
    boolean promotion;
    boolean castling;

    public MoveStatus(boolean legal) {
        this.legal = legal;
    }
}

