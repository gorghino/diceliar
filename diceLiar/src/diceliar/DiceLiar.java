/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diceliar;

import java.util.Arrays;

/**
 *
 * @author gorgo
 */
public class DiceLiar {
    
    public static final int START_TURN = 0;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        Board startBoard = new Board(START_TURN, 2);
        Players currentPlayers = startBoard.getCurrentPlayers();
        currentPlayers.setCurrentBoard(startBoard);
        
        currentPlayers.printDice();
        
        startBoard.initGame(startBoard);
      
        
    }
    
}
