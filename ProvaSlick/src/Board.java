import java.io.Serializable;
import static java.lang.Thread.sleep;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author proietfb
 */
public class Board implements Serializable{
    int nTurn;
    int nPlayers;
    
    int myID;

    Bet currentBet;

    Players currentPlayers;
    Player playingPlayer;
    
    boolean okDoubt;
    
    int status;
    public static int IDLE = 0;
    public static int RESET = 1;
    public static int PLAYING = 2;
    public static int INIT_RESET = 3;
    
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";
    
    
    public transient final Object lock;
    public boolean ready;
    
    public boolean oneJollyEnabled;
    
    boolean haveToken = false;
    int roundToken = 0;
    
    int printCount = 0, printCount2 = 0;
    
    RMI rmiNextPlayer;
    public int diceUpdated = 1;
    boolean betDone;
    
    GUIController gC;

    public Board(int _id, int _nTurn, int _nPlayers, ArrayList<PlayerEntry> _rmiPlayerArray, Object _lock, GUIController _gC) throws RemoteException, NotBoundException {
        myID = _id;
        nTurn = _nTurn;
        nPlayers = _nPlayers;

        //currentBet = new Bet(2,2);

        currentPlayers = new Players(_nPlayers, _rmiPlayerArray);
        currentPlayers.getAllId();
        currentPlayers.getVectorPlayers()[0].makeChoice(this);
        
        oneJollyEnabled = true;
        
        status = IDLE;
        
        gC = _gC;
     
        lock = _lock;
    }

    void initGame(Board startBoard, RMI _rmiNextPlayer) throws RemoteException{
        int playerStarterID = 0; //startBoard.setStarter();
        rmiNextPlayer = _rmiNextPlayer;
        Player playerStarter = startBoard.getCurrentPlayers().getVectorPlayers()[playerStarterID];
        setPlayingPlayer(playerStarter);
        
        gC.setTurn(1);

        System.out.println("Inizia a giocare il giocatore numero " + playerStarterID);
        playerStarter.setTurn(true);
        
        
        //gameLoop(startBoard, playerStarter);

        
    }

    public void gameLoop(Board board, Player player) throws RemoteException{
        player = getCurrentPlayers().getVectorPlayers()[myID];
            if(printCount == 0){
                System.out.println("INIZIO GAMELOOP - TOCCA A " + getPlayingPlayer().getMyID());
                System.out.println("Turno: " + this.getnTurn() + " - Player myTurn: " +  player.myTurn);
                printCount = 1;
            }
            if(myID == getPlayingPlayer().getMyID() && ( status != Board.INIT_RESET && status != Board.RESET )){
                status = PLAYING;
                if(printCount2 == 0){
                    System.out.println("* Giocatore " + player.getMyID() + " tocca a te!");
                    System.out.println("Turno: " + this.getnTurn());
                    printCount2 = 1;
                }
                
                if(!player.makeChoice(board))
                    return;
                
                gC.betClicked = false;
                
                printCount = 1;
                printCount2 = 1;
                
                
                
                //Il turno passa al giocatore successivo
             
                if(status != RESET && status != INIT_RESET){
                    player.setTurn(false); //Non tocca piu a questo player
                    board.setnTurn(getnTurn() + 1);
                    gC.setTurn(getnTurn());
                    
                    //System.out.println(myID + ": NOTIFYTURN AL SUCCESSIVO");
                    
                    Player nextPlayer = board.getCurrentPlayers().getVectorPlayers()[(getPlayingPlayer().getMyID() + 1) % nPlayers];
                    nextPlayer.setTurn(true);
                    board.setPlayingPlayer(board.getCurrentPlayers().getVectorPlayers()[(getPlayingPlayer().getMyID() + 1) % nPlayers]);
                    
                    rmiNextPlayer.notifyTurn(board);
                    System.out.println("Passo il turno");
                }
                    
            }
            else{
                //System.out.println("Gameloop else in");
                
                if(status == RESET){
                    System.out.println("ID: " + myID + " STATUS: RESET");
                    if (myID == this.getPlayingPlayer().myID && haveToken) { //Reset starter
                        if(roundToken == 2){
                            gC.restartBoard = true;
                            gC.oneJollyEnabled = true;
                            status = PLAYING;
                            roundToken = 0;
                            printCount = 0;
                            printCount2 = 0;
                            return;
                        }
                        
                        System.out.println("INIZIO IL TOKEN - PASSO I MIEI DADI");
                        roundToken++;
                        rmiNextPlayer.setDice(myID, currentPlayers);
                        haveToken = false;
                    }
                    else{ //Non ho iniziato io il reset
                        if(haveToken){
                            roundToken++;
                            System.out.println("ID: " + myID + " Ora ho il token e passo i miei dadi");
                            rmiNextPlayer.setDice(myID, currentPlayers);
                            haveToken = false;
                        }
                        
                        if(diceUpdated == currentPlayers.getVectorPlayers().length && roundToken == 2){
                            status = PLAYING;
                            gC.restartBoard = true;
                            gC.oneJollyEnabled = true;
                            roundToken = 0;
                            printCount = 0;
                            printCount2 = 0;
                        }
                            
                    }               
                }
                else if(status == INIT_RESET){
                    System.out.println("ID: " + myID + " STATUS: INIT_RESET");
                    System.out.println("!! START TR: Sono " + myID + " e devo far partire il token ring !!");
                    ready = true;
                    status = Board.RESET;
                    this.setPlayingPlayer(board.getCurrentPlayers().vectorPlayers[myID]);
                    diceUpdated = 1;
                    
                    haveToken = true;
                    
                    oneJollyEnabled = true;    
                    gC.oneJollyEnabled = true;
                }
                else if(status == PLAYING){
                    //System.out.println("ID: " + myID + " PLAYING");
                }
            }
    }
 
//                synchronized (lock) {
//                try {
//                    while (!ready){ 
//                         //System.out.println("Non tocca a me (" + myID + ") o tocca prima fare il token ring");
//                         
//                         if(status != RESET){
//                            System.out.println("Sono " + myID + " e mi blocco nel GAMELOOP");
//                            lock.wait();
//                         }
//                         
//                         //if(status == PLAYING){
//                             //System.out.println("Il turno CONTINUA e sono " + myID);
//                         //}
//                         
//                         if(status == RESET){
//                             System.out.println("Devo resettare i dadi\n");
//                             //this.getCurrentPlayers().resetAllDice(myID);
//                             //rmiNextPlayer.resetDice(currentPlayers);
//                             
////                             System.out.println("------------------ DADI LOCALI CREATI GAMELOOP ---------------------------");
////                             int[] myDice = currentPlayers.getVectorPlayers()[myID].getmyDiceValue();
////                             for (int i = 0; i < myDice.length; i++) {
////                                 System.out.println("Player " + myID + ": " + myDice[i]);
////                             }
//                             //diceUpdated = 1;
//                             //ready = false;
//                             
//                             if(ready == true && diceUpdated == currentPlayers.getVectorPlayers().length){
//                                 System.out.println("Fine giro");
//                                 rmiNextPlayer.setDice(myID, currentPlayers);
//                             }
//                             
//                             ready = false;
//                             oneJollyEnabled = true;
//                             
//                             if (myID == this.getPlayingPlayer().myID) {
//                                 //Sono il giocatore 0, inizio il ring condividendo il set di dadi
//                                 System.out.println("INIZIO TOKEN RING: Sono " + myID + " e passo i miei dadi al prossimo!");
//                                 if (!(rmiNextPlayer.setDice(myID, currentPlayers)) || diceUpdated != currentPlayers.getVectorPlayers().length) {
//                                         try {
//                                             while (!ready) {
//                                                 System.out.println("START TR: Sono " + myID + " e aspetto la fine del ring!");
//                                                 lock.wait();
//                                                 
//                                                 oneJollyEnabled = true;
//                                                 
//                                                 System.out.println("START TR: Sono " + myID + " e mi sono sbloccato!");
//                                                 ready = rmiNextPlayer.setDice(myID, currentPlayers);
//                                             }
//                                         } catch (InterruptedException ex) { System.out.println("POTA ERROR");}
//                                 }
//                             } else {
//                                     try {
//                                         System.out.println("Non ho iniziato io il ring");
//                                         while (!ready) {
//                                             System.out.println("NO START TR: Sono " + myID + " e blocco e diceUpdated vale " + diceUpdated);
//                                             lock.wait(); 
//                                             System.out.println("NO START TR: Sono " + myID + " e mi sono sbloccato!");
//                                             
//                                             if(status == Board.INIT_RESET){
//                                                 System.out.println("!! START TR: Sono " + myID + " e devo far partire il token ring !!");
//                                                 ready = true;
//                                                 status = Board.RESET;
//                                                 this.setPlayingPlayer(board.getCurrentPlayers().vectorPlayers[myID]);
//                                                 diceUpdated = 1;
//                                                 oneJollyEnabled = true;
//                                                 break;  
//                                             }
//                                             
//                                             rmiNextPlayer.setDice(myID, currentPlayers);
//                                                 
//                                             if (diceUpdated != currentPlayers.getVectorPlayers().length) {
//                                                 System.out.println("Non è l'ultimo giro");
//                                                 ready = false;
//                                             }
//
//                                         }
//                                     } catch (InterruptedException ex) { System.out.println("POTA ERROR");}
//                             }
//                             
//                         }
//                    }
//                } catch (InterruptedException ex) { System.out.println("POTA ERROR");}
//                }
//                System.out.println("Gameloop else out");
//                diceUpdated = 1;
//                ready = false;
//            }
//            //System.out.println("Fine GAMELOOP");
//    }

    void newTurn(Board currentBoard, int starterIDPlayer, Bet starterBet) throws RemoteException {
        
        broadcastRMI(currentBoard, "RESET_DICE");
        //this.getCurrentPlayers().resetAllDice(myID);
        
        //if(starterIDPlayer == myID)
            //currentBoard.setCurrentBet(currentBoard.getCurrentPlayers().vectorPlayers[myID].makeBet(currentBoard));
        //else
        currentBoard.setCurrentBet(starterBet);
        //currentPlayers.printDice();
        
        System.out.println("NEWTURN: Il nuovo turno inizia da " + starterIDPlayer);
        currentBoard.getCurrentPlayers().getVectorPlayers()[starterIDPlayer].setTurn(true);
        
        System.out.println(myID + ": SETPlayingPlayer a " + starterIDPlayer);
        currentBoard.setPlayingPlayer(currentBoard.getCurrentPlayers().getVectorPlayers()[starterIDPlayer]);
        currentBoard.diceUpdated = 1;
        
        //broadcastRMI(currentBoard, "NOTIFY_WINLOSE");           
        //rmiNextPlayer.notifyTurn(currentBoard);       
        
        diceUpdated = 1;
        ready = false;
           
        
        if(myID == currentBoard.getPlayingPlayer().myID){
            currentBoard.setCurrentBet(null);
            currentBoard.haveToken = true;
            
            getCurrentPlayers().getVectorPlayers()[myID].setTurn(true);
            setPlayingPlayer(getCurrentPlayers().getVectorPlayers()[myID]);
            diceUpdated = 1;
            status = Board.INIT_RESET;
            
            //currentBoard.broadcastRMI(currentBoard, "NOTIFY_MOVE");
            System.out.println("Esco dal NEWTURN");
        }
        else{
            rmiNextPlayer.setRestart();
            currentBoard.status = Board.RESET;
        }
    }
            
//            //Sono il giocatore che ha vinto, inizio il ring condividendo il set di dadi
//            System.out.println("NEWTURN: Sono " + myID + " e passo i miei dadi al prossimo!");
//            if(!(rmiNextPlayer.setDice(myID, currentPlayers)) || diceUpdated != currentPlayers.getVectorPlayers().length){
//                synchronized (lock) {
//                    try {
//                        while (!ready){
//                            //System.out.println("NEWTURN: Sono " + myID + " e aspetto la fine del ring!");
//                            lock.wait();
//                            //System.out.println("NEWTURN: Sono " + myID + " e mi sono sbloccato!");
//                            rmiNextPlayer.setDice(myID, currentPlayers);
//                        }
//                    } catch (InterruptedException ex) {}
//                }
//            }
//            currentBoard.setCurrentBet(currentBoard.getCurrentPlayers().vectorPlayers[myID].makeBet(currentBoard));
//            currentBoard.broadcastRMI(currentBoard, "NOTIFY_MOVE");
//            System.out.println("Esco dal NEWTURN");
//        }
//        else{
//            System.out.println("NEWTURN: Sono " + myID + " e non tocca a me iniziare il giro di dadi");      
//            rmiNextPlayer.setRestart();
//            currentBoard.status = Board.RESET;
//            System.out.println("FINE NEWTURN");
////            synchronized (lock) {
////                try {
////                    while (!ready){
////                        System.out.println("NEWTURN: Sono " + myID + " e blocco dentro NEWTURN!");
////                        lock.wait();
////                        System.out.println("NEWTURN: Sono " + myID + " e mi sono sbloccato!");
////                        ready = rmiNextPlayer.setDice(myID, currentPlayers);
////                        if(!ready && diceUpdated != currentPlayers.getVectorPlayers().length){
////                            System.out.println("Non è l'ultimo giro");
////                            ready = false;
////                        }    
////                    }
////                } catch (InterruptedException ex) {}
////            }
//            
//        }
//    }
    
    public void broadcastRMI(Board board, String function) throws RemoteException{
        System.out.println(myID + " chiama BROADCAST() " + function);
        for (Player vectorPlayer : currentPlayers.vectorPlayers) {
            RMI rmiPointer = vectorPlayer.getRmiPointer();
            
            if(function.equalsIgnoreCase("RESET_DICE"))
                rmiPointer.resetDice(currentPlayers);
            else if(function.equalsIgnoreCase("NOTIFY_WINLOSE"))
                rmiPointer.checkDoubtRMI(board);
            else if(function.equalsIgnoreCase("ONE_IS_ONE"))
                rmiPointer.oneIsOne(board);
            else if(function.equalsIgnoreCase("NOTIFY_MOVE"))
                rmiPointer.notifyMove(board);
        }
    }
    
    public boolean checkBet(){
        return currentBet.getAmount() > currentPlayers.getAllDice(false)[currentBet.getValueDie()-1]; //Ci sono piu o uguale dadi di quelli della scommessa --> OK
    }

    public int getnTurn() {
        return nTurn;
    }

    public void setnTurn(int _nTurn) {
        this.nTurn = _nTurn;
    }

    public int getnPlayers() {
        return nPlayers;
    }

    public void setnPlayers(int _nPlayers) {
        this.nPlayers = _nPlayers;
    }

    public Players getCurrentPlayers() {
        return currentPlayers;
    }

    public void setCurrentPlayers(Players currentPlayers) {
        this.currentPlayers = currentPlayers;
    }

    public int setStarter(){
        return ThreadLocalRandom.current().nextInt(0, nPlayers);
    }

    public Bet getCurrentBet() {
        return currentBet;
    }

    public void setCurrentBet(Bet _currentBet) {
        this.currentBet = _currentBet;
    }

    public Player getPlayingPlayer() {
        return playingPlayer;
    }

    public void setPlayingPlayer(Player playingPlayer) {
        this.playingPlayer = playingPlayer;
    }
    
    public Object getLock(){
        return this.lock;
    }

    public GUIController getgC() {
        return gC;
    }   
}
