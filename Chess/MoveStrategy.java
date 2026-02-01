/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Chess;
import Chess.Chesswindowpanel.Move;
/**
 *
 * @author mecova
 */
public interface MoveStrategy {
    
    long getNumBoardsEvaluated();
    
    Move execute(Chessboard chessboard);
}
