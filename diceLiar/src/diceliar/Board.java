package diceliar;

import java.util.concurrent.ThreadLocalRandom;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author proietfb
 */
public class Board {
    int nTurn;
    int nPlayers;
    
    Bet currentBet;
    
    Players currentPlayers;
    
    public Board(int _nTurn, int _nPlayers){
        nTurn = _nTurn;
        nPlayers = _nPlayers;
        
        currentBet = new Bet(2,2);
        
        currentPlayers = new Players(_nPlayers);
        currentPlayers.getAllId();
        currentPlayers.getVectorPlayers()[0].makeChoice();
        
       
    }
    
    void initGame(){}
    
    public boolean checkBet(){
        if(currentBet.getAmount() > currentPlayers.getAllDice()[currentBet.getValueDie()-1]) //Ci sono più o uguale dadi di quelli della scommessa --> OK
            return true;
        else
            return false;       
    }

    public int getnTurn() {
        return nTurn;
    }

    public void setnTurn(int _nTurn) {
        this.nTurn = _nTurn;
    }

    public int getnPlayers() {
        return nPlayers;
    }

    public void setnPlayers(int _nPlayers) {
        this.nPlayers = _nPlayers;
    }

    public Players getCurrentPlayers() {
        return currentPlayers;
    }

    public void setCurrentPlayers(Players currentPlayers) {
        this.currentPlayers = currentPlayers;
    }
    
    public int setStarter(){
         return ThreadLocalRandom.current().nextInt(0, getnPlayers()-1);
    }

    public Bet getCurrentBet() {
        return currentBet;
    }

    public void setCurrentBet(Bet _currentBet) {
        this.currentBet = _currentBet;
    }
    
    
        
}
