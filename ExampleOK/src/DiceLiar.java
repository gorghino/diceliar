

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

/**
 *
 * @author gorgo
 */
public class DiceLiar extends UnicastRemoteObject implements RMI, Serializable{

    public static final int START_TURN = 0;
    public static int LOCAL_PORT = 40000;
    
    public static final String SERVER_IP = "127.0.0.1";
    public static final int SERVER_PORT = 50000;
    
    
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_CYAN = "\u001B[36m";
    
    int myID = -1;
    Board rmiBoard;
    private final Object lock;
    private boolean ready;
    private int diceUpdated = 1;
    
    public DiceLiar() throws RemoteException, AlreadyBoundException, NotBoundException, UnknownHostException{
        
        lock = new Object();
        
        //INIZIALIZZAZIONE REGISTRY E SERVER ------------------------------------------------------------------------------------
        System.out.println(ANSI_CYAN + "1: Inizializzazione Registry e Server locale" + ANSI_RESET);
        Registry localReg = LocateRegistry.createRegistry(LOCAL_PORT);
        localReg.bind("player", this);
        System.out.println(ANSI_GREEN + "Registry CREATO\n" + ANSI_RESET); 
        // ----------------------------------------------------------------------------------------------------------------------
        
        //CONNESSIONE CON LA LOBBY -----------------------------------------------------------------------------------------------
        System.out.println(ANSI_CYAN + "2: Collegamento con la Lobby"+ ANSI_RESET);
        Registry reg= LocateRegistry.getRegistry(SERVER_IP, SERVER_PORT);
        RMI rmi = (RMI) reg.lookup("lobby");
        System.out.println(ANSI_GREEN + "Lobby CONNESSA\n" + ANSI_RESET);
        ArrayList<PlayerEntry> rmiPlayerArray = rmi.addClient(InetAddress.getLocalHost().getHostAddress(), LOCAL_PORT);
        
        for(int i=0;i<rmiPlayerArray.size();i++){
            if(rmiPlayerArray.get(i).ip.equalsIgnoreCase(InetAddress.getLocalHost().getHostAddress()) && rmiPlayerArray.get(i).port == LOCAL_PORT)
                myID = i;
        }
        System.out.println("Sono il giocatore " + myID + "\n");
        // ------------------------------------------------------------------------------------------------------------------------
 
        Board startBoard = new Board(myID, START_TURN, rmiPlayerArray.size(), rmiPlayerArray, lock);
        Players currentPlayers = startBoard.getCurrentPlayers();
        currentPlayers.setCurrentBoard(startBoard);
        rmiBoard = startBoard;        
        currentPlayers.vectorPlayers[myID].setMyDice(new Dice(5));
        
        //CONNESSIONE CON IL GIOCATORE SUCCESSIVO ---------------------------------------------------------------------------------
        int IDPlayerRequest = (myID+1)%rmiPlayerArray.size();
        Registry regNext = LocateRegistry.getRegistry(currentPlayers.vectorPlayers[IDPlayerRequest].myIP, currentPlayers.vectorPlayers[IDPlayerRequest].myPort);
        RMI rmiNext = (RMI) regNext.lookup("player");
        
        shareDice(currentPlayers, rmiNext);
        
        System.out.println("------------------DADI LOCALI---------------------------");
        for(int j=0;j<currentPlayers.getAllId().length;j++){
            int[] myDice = currentPlayers.getVectorPlayers()[j].getmyDiceValue();
            for(int i=0;i<myDice.length;i++)
                System.out.println("Player " + j + ": " + myDice[i]);
        }
        
        //        System.out.println("--------------------DADI REMOTI-------------------------");
        //        for(int j=0;j<currentPlayers.getAllId().length;j++){
        //            int[] dicePlayer = rmiNext.getDice(j,currentPlayers);
        //             for(int i=0;i<dicePlayer.length;i++)
        //            System.out.println("Player " + j + ": " + dicePlayer[i]);
        //        }
        
        startBoard.initGame(startBoard, rmiNext);
    }
    
    private void shareDice(Players currentPlayers, RMI rmiNext) throws RemoteException{
        if(myID == 0){
            //Sono il giocatore 0, inizio il ring condividendo il set di dadi
            System.out.println("Sono " + myID + " e passo i miei dadi al prossimo!");
            if(!(rmiNext.setDice(myID, currentPlayers)) || diceUpdated != currentPlayers.getVectorPlayers().length){
                synchronized (lock) {
                    try {
                        while (!ready){
                            System.out.println("Sono " + myID + " e aspetto la fine del ring!");
                            lock.wait();
                            System.out.println("Sono " + myID + " e mi sono sbloccato!");
                            rmiNext.setDice(myID, currentPlayers);
                        }
                    } catch (InterruptedException ex) {}
                }
            }
        }
        else{
            synchronized (lock) {
                try {
                    while (!ready){
                        System.out.println("Sono " + myID + " e blocco!");
                        lock.wait();
                        System.out.println("Sono " + myID + " e mi sono sbloccato!");
                        rmiNext.setDice(myID, currentPlayers);
                        if(diceUpdated != currentPlayers.getVectorPlayers().length){
                            System.out.println("Non è l'ultimo giro");
                            ready = false;
                        }    
                    }
                } catch (InterruptedException ex) {}
            }
            
        }
    }

    /**
     * @param args the command line arguments
     * @throws java.rmi.RemoteException
     * @throws java.rmi.AlreadyBoundException
     * @throws java.rmi.NotBoundException
     * @throws java.net.UnknownHostException
     */
    public static void main(String[] args) throws RemoteException, AlreadyBoundException, NotBoundException, UnknownHostException{  
        if (args.length > 0){
            LOCAL_PORT = Integer.parseInt(args[0]);
        }
        RMI diceliar = new DiceLiar();
    }

    @Override
    public ArrayList<PlayerEntry> addClient(String ipPlayer, int portPlayer) throws RemoteException {
        return null;
    }

    @Override
    public void startListener() throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int[] getDice(int idPlayer, Players currentPlayers) throws RemoteException {
        return rmiBoard.currentPlayers.vectorPlayers[idPlayer].getmyDiceValue();
    }

    @Override
    public boolean setDice(int id, Players playersArray) throws RemoteException {
        boolean lastChange = true;
        
        if(diceUpdated == rmiBoard.currentPlayers.getVectorPlayers().length)
            return lastChange;
        
        System.out.println("Sono " + myID + " e mi ha sbloccato " + id);
        for (int i=0; i<playersArray.getVectorPlayers().length;i++) {
            if(rmiBoard.currentPlayers.vectorPlayers[i].getMyDiceObject() == null && playersArray.getVectorPlayers()[i].getMyDiceObject() != null){
                System.out.println("Sono " + myID + " e aggiorno i miei dadi di " + i);         
                diceUpdated++;
                rmiBoard.currentPlayers.vectorPlayers[i] = playersArray.getVectorPlayers()[i];
                
                lastChange = false;

                if(diceUpdated == rmiBoard.currentPlayers.getVectorPlayers().length){
                    System.out.println("Ho aggiornato " + diceUpdated + "giocatori");
                    lastChange = true;
                }
                    
            }  
        }
        //rmiBoard.currentPlayers.vectorPlayers[id].setMyDice(dice);
        synchronized (lock) {
            ready = true;
            lock.notify();
        }
        System.out.println(lastChange);
        return lastChange;
    }

    @Override
    public void notifyTurn(Board board) throws RemoteException{   
        
        rmiBoard.setnTurn(board.getnTurn());
        rmiBoard.setCurrentBet(board.getCurrentBet());
        if(rmiBoard.getCurrentBet() == null || board.getCurrentBet() == null)
            System.out.println("AHHHHHHHHHHHHHHHHHHHHHH");
        rmiBoard.setPlayingPlayer(board.getPlayingPlayer());
        
        rmiBoard.getPlayingPlayer().setTurn(true);
        
    
        rmiBoard.getCurrentPlayers().getVectorPlayers()[myID].setTurn(true);
        
        System.out.println("Sblocco il giocatore " + rmiBoard.getPlayingPlayer().getMyID());
        
        synchronized (rmiBoard.lock) {
            rmiBoard.ready = true;
            rmiBoard.lock.notify();
        }
    }

    @Override
    public void resetDice(Players currentPlayers, RMI rmiNext) throws RemoteException {
        shareDice(currentPlayers, rmiNext);
    }
}
