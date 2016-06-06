import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Lobby extends UnicastRemoteObject implements RMI {
    
    public static int PORT = 50000;
    public static int WAIT_SECONDS = 30;
      
    private final Object lock;
    ArrayList<PlayerEntry> arrayPlayers;
    private boolean ready;
    private int seconds;

    public Lobby() throws RemoteException {
        super();
        arrayPlayers = new ArrayList<>();
        lock = new Object();
        seconds = WAIT_SECONDS*1000;
    }

    public static void main(String[] args) throws RemoteException, AlreadyBoundException {
        Lobby lobbyServerInterface = new Lobby();
        Registry reg = LocateRegistry.createRegistry(PORT);
        reg.bind("lobby", lobbyServerInterface);
        System.out.println("Server Started...");
        
        lobbyServerInterface.startListener();
    }     
    
    public void startListener(){
        try {
            while(seconds > 0){
                seconds -= 1000;
                System.out.println(seconds);
                Thread.sleep(1000);
            }
           
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
        //System.out.println("Aggiunto giocatore con IP: " + ipPlayer + ":" + portPlayer + "\n");      
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

    @Override
    public int getTimer() throws RemoteException {
        return seconds/1000;
    }
}
