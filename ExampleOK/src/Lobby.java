

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author proietfb
 */
public class Lobby {
    
    private static final int MAX_PLAYERS = 8;
    private static final int MIN_PLAYERS = 3;
    
    private int nLobbyPlayers;

    public Lobby() {
        nLobbyPlayers=0;
    }
    

    public int addLobbyPlayer(){
        if (nLobbyPlayers<MAX_PLAYERS)
            nLobbyPlayers++;
        else
            nLobbyPlayers--;
        return nLobbyPlayers;
    }
    
    
    public int getnLobbyPlayers() {
        return nLobbyPlayers;
    }
    
    
    
}
