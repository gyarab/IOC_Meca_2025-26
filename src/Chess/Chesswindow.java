/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Chess;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
public class Chesswindow{

    public static void main(String[] args) {
    JFrame frame = new JFrame("Chess");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setResizable(false);
   // Add GamePanel to the window
    Chesswindowpanel cp = new Chesswindowpanel();

    Chessboard board = Chessboard.createStandardBoard();
    MiniMax ai = new MiniMax(board,1);
    System.out.println(ai.toString());
    
    frame.add(cp);
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
    cp.launchGame();    
    }

    private final JFrame frame;
    private final Chesswindowpanel panel = new Chesswindowpanel();

    public Chesswindow() {
        this.frame = new JFrame("Chess");
        JMenuBar backgroundMenuBar = new JMenuBar();
        populateMenuBar(backgroundMenuBar);
        this.frame.setJMenuBar(backgroundMenuBar);
        this.frame.setPreferredSize(new Dimension(1100, 800));
        this.frame.setBackground(Color.BLACK);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setResizable(false);
        this.frame.pack();
        this.frame.setLocationRelativeTo(null);
        this.frame.add(panel);
        this.frame.setVisible(true);
        panel.launchGame();

    }

    private boolean pressed;

    private void createButton() {
        javax.swing.JButton onePlayer = new javax.swing.JButton("One player");
        onePlayer.setBounds(400, 200, 50, 50);
        onePlayer.setFont(new Font("Book Antiqua", Font.BOLD, 20));
        onePlayer.setFocusPainted(false);
        onePlayer.setBackground(new Color(200, 200, 200));

        onePlayer.addActionListener(e -> {
            if (pressed) {
                onePlayer.setBackground(new Color(200, 200, 200));
                pressed = false;
            } else {
                onePlayer.setBackground(new Color(150, 150, 150));
                pressed = true;
            }

        });
        onePlayer.add(onePlayer);
    }

    private void populateMenuBar(final JMenuBar backgroundMenuBar) {
        backgroundMenuBar.add(createFileMenu());
    }

    private JMenu createFileMenu() {
        final JMenu fileMenu = new JMenu("File");

        final JMenuItem openPGN = new JMenuItem("Load PGN File");
        openPGN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                System.out.println("open up that pgn file!");
            }
        });
        fileMenu.add(openPGN);

        return fileMenu;
    }
}    
        
        

   

