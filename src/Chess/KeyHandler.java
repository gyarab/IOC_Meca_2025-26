/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package Chess;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 *
 * @author mecova
 */
public class KeyHandler implements KeyListener {

    public boolean Spressed;
    
    @Override
    public void keyTyped(KeyEvent ke) {
    }

    @Override
    public void keyPressed(KeyEvent ke) {

        int code = ke.getKeyCode();
        
        if(code == KeyEvent.VK_S) {
            Spressed = true;
        }  
        
    }

    @Override
    public void keyReleased(KeyEvent ke) {

        int code = ke.getKeyCode();
            
        if(code == KeyEvent.VK_S) {
            Spressed = false;
        }  
    }

}


