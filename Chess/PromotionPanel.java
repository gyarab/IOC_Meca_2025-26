/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Chess;

import Chess.Pieces.piece.Types;
import java.awt.GridLayout;
import java.util.function.Consumer;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;

/**
 *
 * @author Admin
 */
public class PromotionPanel extends JDialog{
    
    public PromotionPanel(JFrame parent, int color, Consumer <Types> callback){
        super(parent, "Choose promotion", true);
        
        setLayout(new GridLayout(1,4));
        setSize(400,120);
        setLocationRelativeTo(parent);
        
        addButton("Queen", Types.QUEEN, callback);
        addButton("Rook", Types.ROOK, callback);
        addButton("Bishop", Types.BISHOP, callback);
        addButton("Knight", Types.KNIGHT, callback);    
    }
    
    private void addButton(String text, Types type, Consumer <Types> callback){
        JButton btn = new JButton(text);  
        btn.addActionListener(e -> {
           callback.accept(type);
           dispose();
        });
        add(btn);
    }
    
}
