/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diceliar;

/**
 *
 * @author proietfb
 */
public class Players {
    
    Player[] vectorPlayers;
    
    public Players(int _nPlayers){
        //costruttore
        vectorPlayers = new Player[_nPlayers];
        
    }
    
    void addPlayer(){
        
    }
    void removePlayer(){
        
    }
    void initDice(){
        
    }
    void getAllDice(){
        
    }

    public Player[] getVectorPlayers() {
        return vectorPlayers;
    }

    public void setVectorPlayers(Player[] _vectorPlayers) {
        this.vectorPlayers = _vectorPlayers;
    }
    
}
