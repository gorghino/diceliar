



import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author proietfb
 */
public interface RMI extends Remote {
    
    public ArrayList<PlayerEntry> addClient(String ipPlayer, int portPlayer) throws RemoteException;
    public int getTimer() throws RemoteException;   
    
    public int[] getDice(int idPlayer, Players currentPlayers) throws RemoteException;
    public boolean setDice(int id, Players playersArray) throws RemoteException;

    public void notifyTurn(Board board) throws RemoteException;
    
    public void resetDice(Players currentPlayers) throws RemoteException;
    public void resetDice2(Board board) throws RemoteException;
    
    public void checkDoubtRMI(Board board) throws RemoteException;
    public void notifyMove(Board board) throws RemoteException;
    public void oneIsOne(Board board) throws RemoteException;
    
    public boolean heartbeat() throws RemoteException;
    public void checkPlayerCrash(Board board) throws RemoteException;
    
    public void setRestart() throws RemoteException;

    public void signalCrash(Board board) throws RemoteException;


}
