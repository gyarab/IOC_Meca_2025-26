/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Chess;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JFrame;

/**
 *
 * @author Admin
 */
public class Background {
    public static void main(String[] args) {
        Background black = new Background();
        Chesswindowpanel panel = new Chesswindowpanel();
        Chessboard board = new Chessboard();
        Mouse mouse = new Mouse();
        JFrame frame = new JFrame ("Chess"); 
        frame.setPreferredSize(new Dimension(1100,1000));
        frame.setBackground(Color.BLACK);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.add(panel);
        frame.setVisible(true);
        panel.launchGame();
        
        
    }
    private boolean pressed;

    private void button(){
       javax.swing.JButton onePlayer = new javax.swing.JButton("One player");
       onePlayer.setBounds(400,200,50,50);
       onePlayer.setFont(new Font("Book Antiqua",Font.BOLD, 20));
       onePlayer.setFocusPainted(false);
       onePlayer.setBackground(new Color(200,200,200));
       
       onePlayer.addActionListener(e -> {
           if(pressed){
               
           }
       
       } ); }
    }

               
       
      
     
      

    
    
