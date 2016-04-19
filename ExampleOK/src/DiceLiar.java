

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

    public static final int START_TURN = 1;
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
    
    ArrayList<PlayerEntry> rmiPlayerArray;
    RMI rmiNext;
    
    public DiceLiar() throws RemoteException, AlreadyBoundException, NotBoundException, UnknownHostException{
        
        lock = new Object();
        
        connectServer();
        Board startBoard = initBoard();
 
        
        
//        System.out.println("------------------DADI LOCALI---------------------------");
//        for(int j=0;j<currentPlayers.getAllId().length;j++){
//            int[] myDice = currentPlayers.getVectorPlayers()[j].getmyDiceValue();
//            for(int i=0;i<myDice.length;i++)
//                System.out.println("Player " + j + ": " + myDice[i]);
//        }
        
        //        System.out.println("--------------------DADI REMOTI-------------------------");
        //        for(int j=0;j<currentPlayers.getAllId().length;j++){
        //            int[] dicePlayer = rmiNext.getDice(j,currentPlayers);
        //             for(int i=0;i<dicePlayer.length;i++)
        //            System.out.println("Player " + j + ": " + dicePlayer[i]);
        //        }
        
        startBoard.initGame(startBoard, rmiNext);
        
    }
    
    private Board initBoard() throws RemoteException, NotBoundException{
        Board startBoard = new Board(myID, START_TURN, rmiPlayerArray.size(), rmiPlayerArray, lock);
        Players currentPlayers = startBoard.getCurrentPlayers();
        currentPlayers.setCurrentBoard(startBoard);
        rmiBoard = startBoard;        
        currentPlayers.vectorPlayers[myID].setMyDice(new Dice(5));
        
        //CONNESSIONE CON IL GIOCATORE SUCCESSIVO ---------------------------------------------------------------------------------
        int IDPlayerRequest = (myID+1)%rmiPlayerArray.size();
        Registry regNext = LocateRegistry.getRegistry(currentPlayers.vectorPlayers[IDPlayerRequest].myIP, currentPlayers.vectorPlayers[IDPlayerRequest].myPort);
        rmiNext = (RMI) regNext.lookup("player");
        
        shareDice(currentPlayers, rmiNext);
        
        return startBoard;
    }
    
    private void connectServer() throws RemoteException, AlreadyBoundException, NotBoundException, UnknownHostException{
        
        //INIZIALIZZAZIONE REGISTRY E SERVER ------------------------------------------------------------------------------------
        System.out.println(ANSI_CYAN + "1: Inizializzazione Registry e Server locale" + ANSI_RESET);
        Registry localReg = LocateRegistry.createRegistry(LOCAL_PORT);
        localReg.bind("player", this);
        System.out.println(ANSI_GREEN + "Registry CREATO\n" + ANSI_RESET);
        // ----------------------------------------------------------------------------------------------------------------------

        //CONNESSIONE CON LA LOBBY -----------------------------------------------------------------------------------------------
        System.out.println(ANSI_CYAN + "2: Collegamento con la Lobby" + ANSI_RESET);
        Registry reg = LocateRegistry.getRegistry(SERVER_IP, SERVER_PORT);
        RMI rmi = (RMI) reg.lookup("lobby");
        System.out.println(ANSI_GREEN + "Lobby CONNESSA\n" + ANSI_RESET);
        rmiPlayerArray = rmi.addClient(InetAddress.getLocalHost().getHostAddress(), LOCAL_PORT);

        for (int i = 0; i < rmiPlayerArray.size(); i++) {
            if (rmiPlayerArray.get(i).ip.equalsIgnoreCase(InetAddress.getLocalHost().getHostAddress()) && rmiPlayerArray.get(i).port == LOCAL_PORT) {
                myID = i;
            }
        }
        System.out.println("Sono il giocatore " + myID + "\n");
        // ------------------------------------------------------------------------------------------------------------------------
        
    }
   
    
    private void shareDice(Players currentPlayers, RMI rmiNext) throws RemoteException{
        System.out.println("\n---- RESET DICE SEQUENCE FROM " + myID + " -----\n");
        if(myID == 0){
            //Sono il giocatore 0, inizio il ring condividendo il set di dadi
            //System.out.println("Sono " + myID + " e passo i miei dadi al prossimo!");
            //System.out.println("DiceUpdate: " + rmiBoard.diceUpdated);
            if(!(rmiNext.setDice(myID, currentPlayers)) || rmiBoard.diceUpdated != currentPlayers.getVectorPlayers().length){
                synchronized (lock) {
                    try {
                        while (!rmiBoard.ready){
                            //System.out.println("Sono " + myID + " e aspetto la fine del ring!");
                            //System.out.println(ANSI_RED + "INIT: LOCK " + lock+ ANSI_RESET);
                            lock.wait();
                            //System.out.println("Sono " + myID + " e mi sono sbloccato!");
                            rmiNext.setDice(myID, currentPlayers);
                        }
                    } catch (InterruptedException ex) {}
                }
            }
        }
        else{
            //System.out.println("Sono " + myID + " e non tocca a me iniziare il giro di dadi");
            synchronized (lock) {
                try {
                    while (!rmiBoard.ready){
                        //System.out.println("Sono " + myID + " e blocco!");
                        //System.out.println(ANSI_RED + "INIT: LOCK " + lock + ANSI_RESET);
                        lock.wait();
                        //System.out.println("Sono " + myID + " e mi sono sbloccato!");
                        rmiNext.setDice(myID, currentPlayers);
                        if(rmiBoard.diceUpdated != currentPlayers.getVectorPlayers().length){
                            //System.out.println("Non è l'ultimo giro");
                            rmiBoard.ready = false;
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
        
        //System.out.println("SET DICE e DiceUpdated = " + rmiBoard.diceUpdated);
        
        if(rmiBoard.diceUpdated == rmiBoard.currentPlayers.getVectorPlayers().length){
            //System.out.println("DADI COMPLETAMENTE AGGIORNATI");
            return lastChange;  
        }
            
        
        //System.out.println("Sono " + myID + " e mi ha sbloccato " + id);
        for (int i=0; i<playersArray.getVectorPlayers().length;i++) {
            
            //if(rmiBoard.currentPlayers.vectorPlayers[i].getMyDiceObject() == null)
                //System.out.println(i + ": RMIBoard getMyDiceObject NULL");
            
            //if(playersArray.getVectorPlayers()[i].getMyDiceObject() != null)
                //System.out.println(i + ": playersArray getMyDiceObject NOT NULL");
            
            if(rmiBoard.currentPlayers.vectorPlayers[i].getMyDiceObject() == null && playersArray.getVectorPlayers()[i].getMyDiceObject() != null){
                System.out.println(myID + ": aggiorno i miei dadi con quelli del giocatore " + i);         
                rmiBoard.diceUpdated++;
                rmiBoard.currentPlayers.vectorPlayers[i] = playersArray.getVectorPlayers()[i];
                
                lastChange = false;

                if(rmiBoard.diceUpdated == rmiBoard.currentPlayers.getVectorPlayers().length){
                    //System.out.println("Ho aggiornato rmiBoard diceUpdate " + rmiBoard.diceUpdated + " giocatori");
                    
                    System.out.println("\n------------------TUTTI I DADI---------------------------");
                    rmiBoard.currentPlayers.printDice();
//                    for (int j = 0; j < rmiBoard.currentPlayers.getAllId().length; j++) {
//                        int[] myDice = rmiBoard.currentPlayers.getVectorPlayers()[j].getmyDiceValue();
//                        for (int z = 0; z < myDice.length; z++) {
//                            System.out.println("Player " + j + ": " + myDice[z]);
//                        }
//                    }
                    
                    lastChange = true;
                    rmiBoard.status = Board.PLAYING;
                }
                    
            }  
        }
        //rmiBoard.currentPlayers.vectorPlayers[id].setMyDice(dice);
        synchronized (lock) {
            rmiBoard.ready = true;
            lock.notify();
            //System.out.println(myID + " NOTIFY");
        }
        return lastChange;
    }

    @Override
    public void notifyTurn(Board board) throws RemoteException{   
        rmiBoard.setnTurn(board.getnTurn());
        rmiBoard.setCurrentBet(board.getCurrentBet());
        
        //if(rmiBoard.getCurrentBet() == null || board.getCurrentBet() == null)
           //rmiBoard.getCurrentPlayers().vectorPlayers[myID].makeChoice(rmiBoard);
        
        rmiBoard.setPlayingPlayer(board.getPlayingPlayer());
        rmiBoard.getPlayingPlayer().setTurn(true);        
        rmiBoard.getCurrentPlayers().getVectorPlayers()[myID].setTurn(true);
        rmiBoard.setPlayingPlayer(board.getPlayingPlayer());
        
       // System.out.println("Sblocco il giocatore " + rmiBoard.myID);
        
        rmiBoard.status = board.status;
        
        synchronized (rmiBoard.lock){
            rmiBoard.ready = true;
            rmiBoard.lock.notify();
            if(rmiBoard.status == Board.PLAYING){
                //System.out.println("NotifyTurn: Il turno CONTINUA\n");
            }
            //else if(rmiBoard.status == Board.RESET){
               // System.out.println("NotifyTurn: Io " + rmiBoard.myID + " Devo resettare i dadi\n");
            //}
        }
    }

    @Override
    public void resetDice(Players currentPlayers) throws RemoteException {
        //System.out.println("-------------------------------------------------------------------------------------------");
        //System.out.println("ID: " + myID + " RESET DICEUpDated");
        rmiBoard.getCurrentPlayers().resetAllDice(myID);
        
        rmiBoard.diceUpdated = 1;  
        rmiBoard.status = Board.RESET;
        rmiBoard.setnTurn(1);

        
        System.out.println("------------------DADI LOCALI CREATI NELLA RESETDICE---------------------------");
        int[] myDice = rmiBoard.getCurrentPlayers().getVectorPlayers()[myID].getmyDiceValue();
        for(int i=0;i<myDice.length;i++)
            System.out.println("Player " + myID + ": " + myDice[i]);
        //shareDice(currentPlayers, rmiNext);
        
        synchronized (rmiBoard.lock){
            rmiBoard.ready = true;
            rmiBoard.lock.notify();
        }
    }

    @Override
    public void updateBoard(Board board) throws RemoteException {
       System.out.println(ANSI_CYAN + "Il giocatore " + board.getPlayingPlayer().myID + " ha detto che sul tavolo NON ci sono almeno " + board.getCurrentBet().amountDice + " dadi di valore " + board.getCurrentBet().valueDie + ANSI_RESET);
       int[] vectorDice = board.getCurrentPlayers().getAllDice(true); // Stampo tutti i dadi
       if(board.okDoubt){
           System.out.println(ANSI_GREEN + "Ha ragione, ce ne sono " + vectorDice[board.getCurrentBet().valueDie-1] + "! Tocca a lui iniziare il nuovo giro" + ANSI_RESET);
           if( (((board.getPlayingPlayer().myID - myID)%board.getnPlayers()) + board.getnPlayers())%board.getnPlayers() == 1){
               System.out.println(ANSI_RED + "Perdo un dado :(" + ANSI_RESET);
               rmiBoard.getCurrentPlayers().getVectorPlayers()[myID].getMyDiceObject().removeDie();
           }        
       }
       else
           System.out.println(ANSI_RED + "Non ha ragione, ce ne sono " + vectorDice[board.getCurrentBet().valueDie-1] + "! Perde un dado e inizia il giocatore successivo" + ANSI_RESET);
       
       System.out.println("\n-------------------------------------- NUOVO TURNO -------------------------------------------\n");

    }

    @Override
    public void setRestart() throws RemoteException {
        //System.out.println("QUELLO PRIMA DI ME HA ERRONEAMENTE DUBITATO");
        rmiBoard.getCurrentPlayers().getVectorPlayers()[myID].setTurn(true);
        rmiBoard.setPlayingPlayer(rmiBoard.getCurrentPlayers().getVectorPlayers()[myID]);
        rmiBoard.diceUpdated = 1;
        rmiBoard.status = Board.INIT_RESET;
        
        rmiBoard.setCurrentBet(null);
        
        synchronized (rmiBoard.lock){
            rmiBoard.ready = true;
            rmiBoard.lock.notify();
        }
    }

    @Override
    public void oneIsOne(Board board) throws RemoteException {
        System.out.println("Uno vale uno. Non posso più usarlo come jolly fino al prossimo turno");
        rmiBoard.oneJollyEnabled = false;
        rmiBoard.currentPlayers.printDice();
    }
    
    
}
