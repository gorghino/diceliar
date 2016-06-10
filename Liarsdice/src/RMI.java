import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;


public interface RMI extends Remote { 
    public ArrayList<PlayerEntry> addClient(String ipPlayer, int portPlayer) throws RemoteException;
    public int getTimer() throws RemoteException;   
    public void notifyTurn(Board board) throws RemoteException;
    public void resetDice(Board board) throws RemoteException; 
    public void notifyMove(Board board) throws RemoteException;
    public void oneIsOne(Board board) throws RemoteException;
    public boolean heartbeat() throws RemoteException;
    public void checkPlayerCrash(Board board) throws RemoteException;
    public void signalCrash(Board board) throws RemoteException;
}
