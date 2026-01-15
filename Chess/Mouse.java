/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Chess;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 *
 * @author Admin
 */
public class Mouse extends MouseAdapter {
 
    public int x,y;
    public boolean pressed;
    
    @Override
    public void mousePressed(MouseEvent e){    
        pressed = true;
    }
    @Override
    public void mouseReleased(MouseEvent e){
        pressed = false;
    }
    @Override 
    public void mouseDragged(MouseEvent e){
        x = e.getX();
        y = e.getY();
    }
    @Override
    public void mouseMoved(MouseEvent e){
        x = e.getX();
        y = e.getY();
    }
}
