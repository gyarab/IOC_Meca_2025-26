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
public class MoveResult {

    MoveStatus status;
    boolean check;
    boolean checkmate;
    boolean stalemate;
    Pieces capturedPiece;

    public MoveResult(MoveStatus status) {
        this.status = status;
    }
}
