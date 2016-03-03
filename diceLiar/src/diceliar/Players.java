/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diceliar;

import java.util.Arrays;
import java.util.UUID;

/**
 *
 * @author proietfb
 */
public class Players {
    
    Player[] vectorPlayers;
    Board currentBoard;
    
    public Players(int _nPlayers){
        //costruttore
        vectorPlayers = new Player[_nPlayers]; 
        for (int i = 0; i < _nPlayers; i += 1) {
            vectorPlayers[i] = new Player(this);
        }
        
    }

    public Board getCurrentBoard() {
        return currentBoard;
    }

    public void setCurrentBoard(Board _currentBoard) {
        this.currentBoard = _currentBoard;
    }  
    
    
    void addPlayer(){
        
    }
    void removePlayer(){
        
    }
    void initDice(){
        
    }
    public int[] getAllDice(){
        int[] allDiceVector = new int[6]; 
        for (int i = 0; i < vectorPlayers.length; i += 1) {
            Player player = vectorPlayers[i];
            for(int j=0; j<6; j += 1){
                //System.out.println("Il giocatore " + i + " ha " + player.getmyDiceValueGrouped()[j] + " dadi di valore " + (j+1));
                allDiceVector[j] += player.getmyDiceValueGrouped()[j];
            }
        }
        
        System.out.println(Arrays.toString(allDiceVector));
        
        return allDiceVector;
        
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
