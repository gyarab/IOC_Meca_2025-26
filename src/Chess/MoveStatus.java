/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package Chess;
/**
 *
 * @author mecova
 */
public enum MoveStatus {
       
          DONE { //move has done
            public boolean isDone() {
                return true;
            }
        },
        ILLEGAL_MOVE {  //not legal move
            public boolean isDone() {
                return false;
            }
        },
        LEAVES_PLAYER_IN_CHECK { //after move the king is in check  
            public boolean isDone() {
                return false;
            }
        };
    }


