/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Chess;
import javax.swing.JFrame;
public class Chesswindow{

    public static void main(String[] args) {
    JFrame window = new JFrame("Chess");
    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    window.setResizable(false);
   // Add GamePanel to the window
    Chesswindowpanel cp = new Chesswindowpanel();
    window.add(cp);
    window.pack();
    window.setLocationRelativeTo(null);
    window.setVisible(true);
    cp.launchGame();    
    }
}    
        
        

   

