/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Chess;

import Chess.Move.MoveStatus;
/**
 *
 * @author mecova
 */
public final class MoveTransition {
    
    private final Chessboard fromBoard;
    private final Chessboard toBoard;
    private final Move transitionMove;
    private final MoveStatus moveStatus;

    public MoveTransition(final Chessboard fromBoard,
                          final Chessboard toBoard,
                          final Move transitionMove,
                          final MoveStatus moveStatus) {
        
        this.fromBoard = fromBoard;
        this.toBoard = toBoard;
        this.transitionMove = transitionMove;
        this.moveStatus = moveStatus;
    }

    public Chessboard getFromBoard() {
        return this.fromBoard;
    }

    public Chessboard getToBoard() {
        return this.toBoard;
    }

    public Move getTransitionMove() {
        return this.transitionMove;
    }

    public MoveStatus getMoveStatus() {
        return this.moveStatus;
    }    
}
