/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Chess;
/**
 *
 * @author mecova
 */
public interface MoveStrategy {
    
    long getNumBoardsEvaluated();
    
    /**
     *
     * @param chessboard
     * @return
     */
    Chessboard.Move execute(Chessboard chessboard);
}
