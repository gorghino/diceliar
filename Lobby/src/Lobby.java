/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import static java.lang.Thread.sleep;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gorgo
 */
public class Lobby extends UnicastRemoteObject implements RMI {
      
    private final Object lock;
    ArrayList<PlayerEntry> arrayPlayers;
    private boolean ready;
    private int INTERVAL = 10*1000;

    public Lobby() throws RemoteException {
        super();
        arrayPlayers = new ArrayList<>();
        lock = new Object();
    }

    public static void main(String[] args) throws RemoteException, AlreadyBoundException {
        RMI lobbyServerInterface = new Lobby();
        Registry reg = LocateRegistry.createRegistry(50000);
        reg.bind("lobby", lobbyServerInterface);
        System.out.println("Server Started...");
        
        lobbyServerInterface.startListener();
        return;
    }  
    
    @Override
    public void startListener() throws RemoteException{
        try {
            sleep(INTERVAL);
        } catch (InterruptedException ex) {
            Logger.getLogger(Lobby.class.getName()).log(Level.SEVERE, null, ex);
        }
            synchronized (lock) {
                ready = true;
                lock.notifyAll();
            }
        }

    @Override
    public synchronized ArrayList<PlayerEntry> addClient(String ipPlayer, int portPlayer) throws RemoteException {
        PlayerEntry newPlayer = new PlayerEntry(ipPlayer, portPlayer);
        arrayPlayers.add(newPlayer);
        System.out.println("Aggiunto giocatore con IP: " + ipPlayer + ":" + portPlayer + "\n");      
        synchronized (lock) {
            try {
                while (!ready){
                    lock.wait();
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(Lobby.class.getName()).log(Level.SEVERE, null, ex);
            }
            return arrayPlayers;
        }
    }
}
