import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import org.newdawn.slick.SlickException;

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
   
    public int rmiTimer;


    ArrayList<PlayerEntry> rmiPlayerArray;
    RMI rmiNext;
    RMIGameController rgc;
    
    public DiceLiar() throws RemoteException, AlreadyBoundException, NotBoundException, UnknownHostException {
        rgc = new RMIGameController();

    }
    
    public Board initBoard(GUIController _gC) throws RemoteException, NotBoundException{
        Board startBoard = new Board(rgc.myID, START_TURN, rmiPlayerArray.size(), rmiPlayerArray, rgc.lock, _gC);
        Players currentPlayers = startBoard.getCurrentPlayers();
        currentPlayers.setCurrentBoard(startBoard);
        rgc.rmiBoard = startBoard;
        currentPlayers.vectorPlayers[rgc.myID].setMyDice(new Dice(currentPlayers.startAmountDice));
        //CONNESSIONE CON IL GIOCATORE SUCCESSIVO ---------------------------------------------------------------------------------
        int IDPlayerRequest = (rgc.myID+1)%rmiPlayerArray.size();
        Registry regNext = LocateRegistry.getRegistry(currentPlayers.vectorPlayers[IDPlayerRequest].myIP, currentPlayers.vectorPlayers[IDPlayerRequest].myPort);
        System.out.println("Imposto rmiNext\n");
        rmiNext = (RMI) regNext.lookup("player");
        
        startBoard.ready = false;
        startBoard.haveToken = false;
        
        return startBoard;
    }
    
    public void connectServer(String serverIP, String ServerPort) throws RemoteException, AlreadyBoundException, NotBoundException, UnknownHostException{
        
        //INIZIALIZZAZIONE REGISTRY E SERVER ------------------------------------------------------------------------------------
        System.out.println(ANSI_CYAN + "1: Inizializzazione Registry e Server locale" + ANSI_RESET);
        Registry localReg = LocateRegistry.createRegistry(LOCAL_PORT);
        localReg.bind("player", rgc);
        System.out.println(ANSI_GREEN + "Registry CREATO\n" + ANSI_RESET);
        // ----------------------------------------------------------------------------------------------------------------------

        //CONNESSIONE CON LA LOBBY -----------------------------------------------------------------------------------------------
        System.out.println(ANSI_CYAN + "2: Collegamento con la Lobby" + ANSI_RESET);
        Registry reg = LocateRegistry.getRegistry(serverIP, Integer.parseInt(ServerPort));
        RMI rmi = (RMI) reg.lookup("lobby");
        
        System.out.println(ANSI_GREEN + "Lobby CONNESSA ... Mancano " + rmi.getTimer() + " secondi al VIA\n" + ANSI_RESET); 
        rmiTimer = rmi.getTimer(); //prendo questo valore per il timer della connect
        rmiPlayerArray = rmi.addClient(InetAddress.getLocalHost().getHostAddress(), LOCAL_PORT);

        for (int i = 0; i < rmiPlayerArray.size(); i++) {
            if (rmiPlayerArray.get(i).ip.equalsIgnoreCase(InetAddress.getLocalHost().getHostAddress()) && rmiPlayerArray.get(i).port == LOCAL_PORT) {
                rgc.myID = i;
            }
        }
        System.out.println("Sono il giocatore " + rgc.myID + "\n");
        // ------------------------------------------------------------------------------------------------------------------------
        //Board startBoard = initBoard(); Viene fatta nella Connect
        //startBoard.initGame(startBoard, rmiNext);   
    }
    
    private void shareDice(Players currentPlayers, RMI rmiNext) throws RemoteException{
        //TODO ------------------------------------------------------------------------------------------------------------------------------- DA RIFARE
        System.out.println("\n---- RESET DICE SEQUENCE FROM " + rgc.myID + " -----\n");
        if(rgc.myID == 0){
            //Sono il giocatore 0, inizio il ring condividendo il set di dadi
            //System.out.println("Sono " + myID + " e passo i miei dadi al prossimo!");
            //System.out.println("DiceUpdate: " + rmiBoard.diceUpdated);
            if(!(rmiNext.setDice(rgc.myID, currentPlayers)) || rgc.rmiBoard.diceUpdated != currentPlayers.getVectorPlayers().length){
                synchronized (rgc.lock) {
                    try {
                        while (!rgc.rmiBoard.ready){
                            //System.out.println("Sono " + myID + " e aspetto la fine del ring!");
                            //System.out.println(ANSI_RED + "INIT: LOCK " + lock+ ANSI_RESET);
                            rgc.lock.wait();
                            //System.out.println("Sono " + myID + " e mi sono sbloccato!");
                            rmiNext.setDice(rgc.myID, currentPlayers);
                        }
                    } catch (InterruptedException ex) {}
                }
            }
        }
        else{
            //System.out.println("Sono " + myID + " e non tocca a me iniziare il giro di dadi");
            synchronized (rgc.lock) {
                try {
                    while (!rgc.rmiBoard.ready){
                        //System.out.println("Sono " + myID + " e blocco!");
                        //System.out.println(ANSI_RED + "INIT: LOCK " + lock + ANSI_RESET);
                        rgc.lock.wait();
                        //System.out.println("Sono " + myID + " e mi sono sbloccato!");
                        rmiNext.setDice(rgc.myID, currentPlayers);
                        if(rgc.rmiBoard.diceUpdated != currentPlayers.getVectorPlayers().length){
                            //System.out.println("Non è l'ultimo giro");
                            rgc.rmiBoard.ready = false;
                        }    
                    }
                } catch (InterruptedException ex) {}
            }
        }
    }
    
    public int getRmiTimer() {
        return rmiTimer;
    }
    /**
     * @param args the command line arguments
     * @throws java.rmi.AlreadyBoundException
     * @throws java.rmi.NotBoundException
     * @throws java.net.UnknownHostException
     * @throws org.newdawn.slick.SlickException
     */
    public static void main(String[] args) throws AlreadyBoundException, NotBoundException, UnknownHostException, SlickException{  
        if (args.length > 0){
            LOCAL_PORT = Integer.parseInt(args[0]);
        }
        
        //Main rungraphic = new Main(Main.gameName);
        //rungraphic.run();
        Main threadGR = new Main(Main.gameName);
        new Thread(threadGR).start();
        
    }
    
}
