/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Chess;

/**
 *
 * @author mecova
 */
public enum Alliance {
WHITE {
    
    @Override
    public int getDirection(){
        return -1;
    }
    
    @Override
    public int getOppositeDirection(){
        return 1;
    }
    
    @Override
    public boolean isWhite(){
        return true;
    }
    
    @Override
    public boolean isBlack(){
        return false;
    }
    
    @Override 
    public boolean isPawnPromotionSquare(int position){
        return BoardUtils.FIRST_RANK[position];
    }
    
    @Override
    public Player choosePlayer(final Whiteplayer whitePlayer,
            final BlackPlayer blackPlayer){
        return whitePlayer;
    }
},
BLACK{
    @Override
    public int getDirection(){
        return 1;
    }
    
    @Override
    public int getOppositeDirection(){
        return -1;
    }
    
    @Override 
    public boolean isWhite(){
        return false;
    }
    
    @Override
    public boolean isBlack(){
        return true;
    }
    
    @Override 
    public boolean isPawnPromotionSquare(int position){
        return BoardUtils.EIGHT_RANK[position];
    }   
    
    @Override 
    public Player choosePlayer(final Whiteplayer whitePlayer,
            final BlackPlayer blackPlayer){
        return blackPlayer;
    
}
};

public abstract int getDirection();
public abstract int getOppositeDirection();
public abstract boolean isWhite();
public abstract boolean isBlack();

}


