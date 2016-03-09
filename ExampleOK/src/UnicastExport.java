
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author davide.aguiari
 */
public class UnicastExport extends UnicastRemoteObject{
    Board currentBoard;
    
    public UnicastExport(Board _currentBoard) throws RemoteException{
        super();
        currentBoard = _currentBoard;
    }
    
    public Board getBoard(){
        return currentBoard;
    }
    
}
