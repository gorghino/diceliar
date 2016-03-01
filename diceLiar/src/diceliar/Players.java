/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diceliar;

import java.util.UUID;

/**
 *
 * @author proietfb
 */
public class Players {
    
    Player[] vectorPlayers;
    
    public Players(int _nPlayers){
        //costruttore
        vectorPlayers = new Player[_nPlayers]; 
        for (int i = 0; i < _nPlayers; i += 1) {
            vectorPlayers[i] = new Player();
        }
        
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
    
    public UUID[] getAllId(){
        int i=0;
        UUID[] idArray = new UUID[vectorPlayers.length];
        for (i=0;i<vectorPlayers.length;i++)
            idArray[i] = vectorPlayers[i].getId();
        
        return idArray;
    }
    
}
