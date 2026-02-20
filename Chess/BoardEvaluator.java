/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Chess;
/**
 *
 * @author mecova
 */
// Rozhraní pro ohodnocení šachovnice
public interface BoardEvaluator {
    //Chessboard board;
     /*
     * Vrací ohodnocení šachovnice pro danou barvu.
     * 
     * @param board instance Chessboard
     * @param depth aktuální hloubka rekurze (pro příp. preferenci rychlejšího matu)
     * @param color barva hráče, pro kterého se hodnotí (Chesswindowpanel.WHITE / BLACK)
     * @return skóre šachovnice (vyšší = lepší pro danou barvu)
     */
    
    public abstract int evaluate(Chessboard board, int depth);
    //return board.getEvaluation();
}
