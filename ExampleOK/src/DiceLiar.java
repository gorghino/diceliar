

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

/**
 *
 * @author gorgo
 */
public class DiceLiar{

    public static final int START_TURN = 1;
    public static int LOCAL_PORT = 40000;
    
    public static final String SERVER_IP = "127.0.0.1";
    public static final int SERVER_PORT = 50000;
    
    
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_CYAN = "\u001B[36m";
   

    ArrayList<PlayerEntry> rmiPlayerArray;
    RMI rmiNext;
    GameController gc;
    
    public DiceLiar() throws RemoteException, AlreadyBoundException, NotBoundException, UnknownHostException{
        gc = new GameController();
        this.connectServer();       
        Board startBoard = initBoard();     
        startBoard.initGame(startBoard, rmiNext);    
    }
    
    private Board initBoard() throws RemoteException, NotBoundException{
        Board startBoard = new Board(gc.myID, START_TURN, rmiPlayerArray.size(), rmiPlayerArray, gc.lock);
        Players currentPlayers = startBoard.getCurrentPlayers();
        currentPlayers.setCurrentBoard(startBoard);
        gc.rmiBoard = startBoard;        
        currentPlayers.vectorPlayers[gc.myID].setMyDice(new Dice(5));
        
        //CONNESSIONE CON IL GIOCATORE SUCCESSIVO ---------------------------------------------------------------------------------
        int IDPlayerRequest = (gc.myID+1)%rmiPlayerArray.size();
        Registry regNext = LocateRegistry.getRegistry(currentPlayers.vectorPlayers[IDPlayerRequest].myIP, currentPlayers.vectorPlayers[IDPlayerRequest].myPort);
        rmiNext = (RMI) regNext.lookup("player");
        
        shareDice(currentPlayers, rmiNext);
        
        return startBoard;
    }
    
    private void connectServer() throws RemoteException, AlreadyBoundException, NotBoundException, UnknownHostException{
        
        //INIZIALIZZAZIONE REGISTRY E SERVER ------------------------------------------------------------------------------------
        System.out.println(ANSI_CYAN + "1: Inizializzazione Registry e Server locale" + ANSI_RESET);
        Registry localReg = LocateRegistry.createRegistry(LOCAL_PORT);
        localReg.bind("player", gc);
        System.out.println(ANSI_GREEN + "Registry CREATO\n" + ANSI_RESET);
        // ----------------------------------------------------------------------------------------------------------------------

        //CONNESSIONE CON LA LOBBY -----------------------------------------------------------------------------------------------
        System.out.println(ANSI_CYAN + "2: Collegamento con la Lobby" + ANSI_RESET);
        Registry reg = LocateRegistry.getRegistry(SERVER_IP, SERVER_PORT);
        RMI rmi = (RMI) reg.lookup("lobby");
        System.out.println(ANSI_GREEN + "Lobby CONNESSA ... Mancano " + rmi.getTimer() + " secondi al VIA\n" + ANSI_RESET);  
        rmiPlayerArray = rmi.addClient(InetAddress.getLocalHost().getHostAddress(), LOCAL_PORT);

        for (int i = 0; i < rmiPlayerArray.size(); i++) {
            if (rmiPlayerArray.get(i).ip.equalsIgnoreCase(InetAddress.getLocalHost().getHostAddress()) && rmiPlayerArray.get(i).port == LOCAL_PORT) {
                gc.myID = i;
            }
        }
        System.out.println("Sono il giocatore " + gc.myID + "\n");
        // ------------------------------------------------------------------------------------------------------------------------
        
    }
    
    private void shareDice(Players currentPlayers, RMI rmiNext) throws RemoteException{
        System.out.println("\n---- RESET DICE SEQUENCE FROM " + gc.myID + " -----\n");
        if(gc.myID == 0){
            //Sono il giocatore 0, inizio il ring condividendo il set di dadi
            //System.out.println("Sono " + myID + " e passo i miei dadi al prossimo!");
            //System.out.println("DiceUpdate: " + rmiBoard.diceUpdated);
            if(!(rmiNext.setDice(gc.myID, currentPlayers)) || gc.rmiBoard.diceUpdated != currentPlayers.getVectorPlayers().length){
                synchronized (gc.lock) {
                    try {
                        while (!gc.rmiBoard.ready){
                            //System.out.println("Sono " + myID + " e aspetto la fine del ring!");
                            //System.out.println(ANSI_RED + "INIT: LOCK " + lock+ ANSI_RESET);
                            gc.lock.wait();
                            //System.out.println("Sono " + myID + " e mi sono sbloccato!");
                            rmiNext.setDice(gc.myID, currentPlayers);
                        }
                    } catch (InterruptedException ex) {}
                }
            }
        }
        else{
            //System.out.println("Sono " + myID + " e non tocca a me iniziare il giro di dadi");
            synchronized (gc.lock) {
                try {
                    while (!gc.rmiBoard.ready){
                        //System.out.println("Sono " + myID + " e blocco!");
                        //System.out.println(ANSI_RED + "INIT: LOCK " + lock + ANSI_RESET);
                        gc.lock.wait();
                        //System.out.println("Sono " + myID + " e mi sono sbloccato!");
                        rmiNext.setDice(gc.myID, currentPlayers);
                        if(gc.rmiBoard.diceUpdated != currentPlayers.getVectorPlayers().length){
                            //System.out.println("Non è l'ultimo giro");
                            gc.rmiBoard.ready = false;
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
        DiceLiar diceliar = new DiceLiar();
    }   
}
