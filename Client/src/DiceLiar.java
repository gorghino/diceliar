/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
//        System.setProperty("java.security.policy","./server.policy");
//	System.setProperty("java.rmi.server.codebase","file:./src");
        Board startBoard = new Board(START_TURN, 2);
        Players currentPlayers = startBoard.getCurrentPlayers();
        currentPlayers.setCurrentBoard(startBoard);

        currentPlayers.printDice();
               if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
         RMIServer obj = new RMIServer(startBoard);
            //creo un registro e vi collego il metodo associandolo come istanza
            //ad un nome ("server")

            Registry reg = LocateRegistry.createRegistry(5678);
            reg.bind("server", obj);

            System.out.println("Server Started...");

        //startBoard.initGame(startBoard);


    }

}
