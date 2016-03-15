

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;

/**
 *
 * @author gorgo
 */
public class DiceLiar extends UnicastRemoteObject implements RMI{

    public static final int START_TURN = 0;

    
    /**/
    
    Board currentBoard;

    public DiceLiar(Board _currentBoard) throws RemoteException{
        super();
        currentBoard = _currentBoard;
    }

    @Override
    public String getData(String text) throws RemoteException {
        return Integer.toString(currentBoard.getnPlayers());
        //text = "Ciao "+ text;
        //return text;
    }
    
    @Override
    public Board getBoard() throws RemoteException {
        return currentBoard; 
    }
    
    /**/
    
    public static void main(String[] args) throws RemoteException, AlreadyBoundException {
        //System.setProperty("java.security.policy","./server.policy");
	//System.setProperty("java.rmi.server.codebase","file:./src");
        Board startBoard = new Board(START_TURN,2);
        Players currentPlayers = startBoard.getCurrentPlayers();
        currentPlayers.setCurrentBoard(startBoard);
        System.out.println(Integer.toString(startBoard.getnPlayers()));

        currentPlayers.printDice();
        DiceLiar obj = new DiceLiar(startBoard);
            //creo un registro e vi collego il metodo associandolo come istanza
            //ad un nome ("server")

        Registry reg = LocateRegistry.createRegistry(40000);
        reg.bind("server", obj);

        System.out.println("Server Started...");

        //startBoard.initGame(startBoard);


    }

}
