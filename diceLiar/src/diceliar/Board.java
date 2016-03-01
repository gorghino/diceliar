package diceliar;

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
    
    Players currentPlayers;
    
    public Board(int _nTurn, int _nPlayers){
        nTurn = _nTurn;
        nPlayers = _nPlayers;
        
        currentPlayers = new Players(_nPlayers);
        currentPlayers.getAllId();
    }
    
    void initGame(){}
    void checkBet(){}

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
    
    
    
    
    
}
