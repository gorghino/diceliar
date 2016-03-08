/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diceliar;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
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
    public static void main(String[] args) throws RemoteException, AlreadyBoundException {
        
        Board startBoard = new Board(START_TURN, 2);
        Players currentPlayers = startBoard.getCurrentPlayers();
        currentPlayers.setCurrentBoard(startBoard);
        
        currentPlayers.printDice();
        
         RMIServer obj = new RMIServer(startBoard);
            //creo un registro e vi collego il metodo associandolo come istanza
            //ad un nome ("server")
            
            Registry reg = LocateRegistry.createRegistry(5678);
            System.setProperty("java.rmi.server.hostname","lucia.cs.unibo.it");
            reg.bind("server", obj);
            
            System.out.println("Server Started...");
        
        //startBoard.initGame(startBoard);
      
        
    }
    
}
