import java.io.Serializable;
import static java.lang.Thread.sleep;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    int broadcastCount = 0;
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
    
    int lockCount = 0;
    int loser = 0, winner = 0;
    
    boolean initBoard;
    
    int printCount = 0, printCount2 = 0;
    
    public int diceUpdated = 1;
    boolean betDone;
    
    GUIController gC;

    public Board(int _id, int _nTurn, int _nPlayers, ArrayList<PlayerEntry> _rmiPlayerArray, Object _lock, GUIController _gC) throws NotBoundException {
        myID = _id;
        nTurn = _nTurn;
        nPlayers = _nPlayers;

        //currentBet = new Bet(2,2);

        currentPlayers = new Players(_nPlayers, _rmiPlayerArray);
        currentPlayers.getAllId();
        //currentPlayers.getVectorPlayers()[0].makeChoice(this);
        
        oneJollyEnabled = true;
        initBoard = true;
        
        status = IDLE;
        
        gC = _gC;
     
        lock = _lock;
    }

    void initGame(Board startBoard, RMI _rmiNextPlayer){
        int playerStarterID = 0; //startBoard.setStarter();
        
        startBoard.getCurrentPlayers().getVectorPlayers()[myID].rmiNextPlayer = _rmiNextPlayer;
        
        Player playerStarter = startBoard.getCurrentPlayers().getVectorPlayers()[playerStarterID];
        setPlayingPlayer(playerStarter);
        gC.setPlayingPlayer(playerStarter.myID);
        
        gC.setTurn(1);
        
                
        if(startBoard.myID == 0){
            //getCurrentPlayers().resetAllDice(myID);
            startBoard.broadcastRMI(startBoard, "RESET_DICE");
        }

        System.out.println("Inizia a giocare il giocatore numero " + playerStarterID);
        playerStarter.setTurn(true);

        
    }

    public void gameLoop(Board board, Player player){
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
                
                if(status == INIT_RESET){
                    //Nuovo turno
                    synchronized (lock) {
                        System.out.println("SBLOCCO TUTTI INIT RESET\n");
                        ready = true;
                        lock.notifyAll();
                    }
                    System.out.println("Passo il turno");
                    status = PLAYING;
                    return;
                }
                
                //Il turno passa al giocatore successivo
             
                if(status != RESET && status != INIT_RESET){
                    player.setTurn(false); //Non tocca piu a questo player
                    
                    System.out.println(myID + ": NOTIFYTURN AL SUCCESSIVO");
                    
                    Player nextPlayer = board.getCurrentPlayers().getVectorPlayers()[(getPlayingPlayer().IDNext)];
                    
                    nextPlayer.setTurn(true);
                    board.setPlayingPlayer(nextPlayer);
                    gC.setPlayingPlayer(nextPlayer.myID);
                    
                    gC.betOnTable = true;
                    gC.diceAmountSelected = board.getCurrentBet().amountDice;
                    gC.diceValueSelected = board.getCurrentBet().valueDie;
                    gC.idLastBet = myID;
     
                    try {
                        this.currentPlayers.vectorPlayers[myID].rmiNextPlayer.notifyTurn(board);
  
                    } catch (RemoteException ex) {
                        System.out.println(DiceLiar.ANSI_RED + "!! CRASH RILEVATO. Il giocatore " + currentPlayers.vectorPlayers[myID].IDNext + "non è raggiungibile." + DiceLiar.ANSI_RESET);
                        currentPlayers.removePlayer(currentPlayers.vectorPlayers[currentPlayers.vectorPlayers[myID].IDNext], true, true);
                        
                        try {
                            System.out.println(DiceLiar.ANSI_RED + " FINALLY " + DiceLiar.ANSI_RESET);
                            // TODO: ------------------------------------------- CONTROLLARE SE SONO OK NUOVE INFO NOTIFYTURN
                            this.currentPlayers.vectorPlayers[myID].rmiNextPlayer.notifyTurn(board);
                        } catch (RemoteException ex2) {
                            System.out.println(DiceLiar.ANSI_RED + "!! FATAL ERROR - RMI ERROR IN FINALLY CODE" + DiceLiar.ANSI_RESET);
                            Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        
                    }
                }
                
                synchronized (lock) {
                    System.out.println("SBLOCCO TUTTI\n");
                    ready = true;
                    lock.notifyAll();
                    }
                System.out.println("Passo il turno");
                    
            }
            else{
                //System.out.println("Gameloop else in. Status: " + status);
                
                if(status == RESET){
                    System.out.println("ID: " + myID + " STATUS: RESET");
                    if (myID == this.getPlayingPlayer().myID && haveToken) { //Reset starter
                        if(roundToken == 2){
                            System.out.println("Pota");
                            gC.restartBoard = true;
                            gC.playDiceAnimation = true;
                            gC.oneJollyEnabled = true;
                            status = PLAYING;
                            roundToken = 0;
                            printCount = 0;
                            printCount2 = 0;
                            return;
                        }
                        
                        //System.out.println("INIZIO IL TOKEN - PASSO I MIEI DADI");
                        roundToken++;
                        
                        try {
                            this.currentPlayers.vectorPlayers[myID].rmiNextPlayer.setDice(myID, currentPlayers);
                        } catch (RemoteException ex) {
                           System.out.println(DiceLiar.ANSI_RED + "!! CRASH RILEVATO. Il giocatore " + currentPlayers.vectorPlayers[myID].IDNext + "non è raggiungibile." + DiceLiar.ANSI_RESET);
                            currentPlayers.removePlayer(currentPlayers.vectorPlayers[currentPlayers.vectorPlayers[myID].IDNext], true, true);
                            try {
                                this.currentPlayers.vectorPlayers[myID].rmiNextPlayer.setDice(myID, currentPlayers);
                            } catch (RemoteException ex2) {
                                System.out.println(DiceLiar.ANSI_RED + "!! FATAL ERROR - RMI ERROR IN FINALLY CODE" + DiceLiar.ANSI_RESET);
                                Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                                              
                        haveToken = false;
                    }
                    else{ //Non ho iniziato io il reset
                        if(haveToken){
                            roundToken++;
                            try {
                                //System.out.println("ID: " + myID + " Ora ho il token e passo i miei dadi");
                                this.currentPlayers.vectorPlayers[myID].rmiNextPlayer.setDice(myID, currentPlayers);
                            } catch (RemoteException ex) {
                                System.out.println(DiceLiar.ANSI_RED + "!! CRASH RILEVATO. Il giocatore " + currentPlayers.vectorPlayers[myID].IDNext + "non è raggiungibile." + DiceLiar.ANSI_RESET);
                                currentPlayers.removePlayer(currentPlayers.vectorPlayers[currentPlayers.vectorPlayers[myID].IDNext], true, true);
                                try {
                                    System.out.println("Rimando i dadi dopo il crash del mio successivo");
                                    this.currentPlayers.vectorPlayers[myID].rmiNextPlayer.setDice(myID, currentPlayers);
                                } catch (RemoteException ex2) {
                                    System.out.println(DiceLiar.ANSI_RED + "!! FATAL ERROR - RMI ERROR IN FINALLY CODE" + DiceLiar.ANSI_RESET);
                                    Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                            
                            haveToken = false;
                        }
                        
                        System.out.println("DiceUpd : " + diceUpdated + " getAlive: " + currentPlayers.getPlayersAlive() + " token ring: " + roundToken);
                        
                        if(diceUpdated == currentPlayers.getPlayersAlive() && roundToken == 2){
                            status = PLAYING;
                            System.out.println("Pota2");
                            gC.restartBoard = true;
                            gC.playDiceAnimation = true;
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
                    status = Board.RESET;
                    
                    this.setPlayingPlayer(board.getCurrentPlayers().vectorPlayers[myID]);
                    gC.setPlayingPlayer(myID);
                    
                    diceUpdated = 1;
                    
                    haveToken = true;
                    
                    oneJollyEnabled = true;    
                    gC.oneJollyEnabled = true;
                }
                else if(status == IDLE || status == PLAYING){
                    if(gC.playDiceAnimation)
                        return;
                    
                    try {
                        System.out.println("Non tocca a me. Mi blocco sul giocatore " + getPlayingPlayer().myID);
                        
                        this.getPlayingPlayer().rmiPointer.checkPlayerCrash(this);
                        System.out.println("ID: " + myID + " MI SBLOCCO DENTRO BOARD");
                        
                        printCount = 0;
                        ready = false;
                        
                    } catch (RemoteException ex) {
                        System.out.println(DiceLiar.ANSI_RED + "!! CRASH DEL PLAYING PLAYER RILEVATO. Il giocatore " + currentPlayers.vectorPlayers[getPlayingPlayer().myID].myID + "non è raggiungibile." + DiceLiar.ANSI_RESET);
                        int newPlaying = currentPlayers.removePlayer(currentPlayers.vectorPlayers[getPlayingPlayer().myID], true, false);
                        
                        if(newPlaying == myID){
                            //Sono il nuovo PlayingPlayer
                            System.out.println(DiceLiar.ANSI_CYAN + "ID: " + myID + " sono il nuovo playingPlayer" + DiceLiar.ANSI_RESET);
                            setPlayingPlayer(currentPlayers.vectorPlayers[myID]);
                            getPlayingPlayer().setTurn(true);
                            this.winner = myID;
                            gC.setPlayingPlayer(currentPlayers.vectorPlayers[myID].myID);
                            gC.turn = board.getnTurn();
                            
                            if(board.getnTurn() == 1){
                                //E' crashato il primo giocatore del turno, ridistribuisco i dadi in caso sia crashato mentre li distribuiva
                                getCurrentPlayers().resetAllDice(myID);
                                this.setCurrentBet(null);
                                broadcastRMI(this, "RESET_DICE");
                                
                                synchronized (lock) {
                                    System.out.println("SBLOCCO TUTTI\n");
                                    ready = true;
                                    lock.notifyAll();
                                }
                            }
                            
                            
                        }
                        else{
                            //Aggiorno il playing su cui bloccarmi
                            setPlayingPlayer(currentPlayers.vectorPlayers[newPlaying]);
                            gC.setPlayingPlayer(currentPlayers.vectorPlayers[newPlaying].myID);
                            
                            System.out.println("Turno GC: " + board.getnTurn());
                            getPlayingPlayer().setTurn(true);
                            gC.turn = board.getnTurn();
                        }
                    }
                }
                //gC.printValues();
            }
    }
 

    void newTurn(Board currentBoard, int starterIDPlayer, Bet starterBet){
        
        getCurrentPlayers().resetAllDice(myID);
        currentPlayers.printDice();   

        currentBoard.setCurrentBet(starterBet);
        //currentPlayers.printDice();
        
        //System.out.println("NEWTURN: Il nuovo turno inizia da " + starterIDPlayer);
        currentBoard.getCurrentPlayers().getVectorPlayers()[starterIDPlayer].setTurn(true);
        
        //System.out.println(myID + ": SETPlayingPlayer a " + starterIDPlayer);
        currentBoard.setPlayingPlayer(currentBoard.getCurrentPlayers().getVectorPlayers()[starterIDPlayer]);
        gC.setPlayingPlayer(starterIDPlayer);
        
        broadcastRMI(currentBoard, "RESET_DICE");
        
        currentBoard.diceUpdated = 1;
        status = Board.INIT_RESET;
       
        
//        if(myID == currentBoard.getPlayingPlayer().myID){
//            currentBoard.setCurrentBet(null);
//            currentBoard.haveToken = true;
//            
//            getCurrentPlayers().getVectorPlayers()[myID].setTurn(true);
//            setPlayingPlayer(getCurrentPlayers().getVectorPlayers()[myID]);
//            gC.playingPlayer = myID;
//            diceUpdated = 1;
//            status = Board.INIT_RESET;
//            
//            //System.out.println("Esco dal NEWTURN");
//        }
//        else{
//            try {
//                //System.out.println("Segnalo SET RESTART");
//                this.currentPlayers.vectorPlayers[myID].rmiNextPlayer.setRestart();
//            } catch (RemoteException ex) {
//                System.out.println(DiceLiar.ANSI_RED + "!! CRASH RILEVATO. Il giocatore " + currentPlayers.vectorPlayers[myID].IDNext + "non è raggiungibile." + DiceLiar.ANSI_RESET);
//                currentPlayers.removePlayer(currentPlayers.vectorPlayers[currentPlayers.vectorPlayers[myID].IDNext], true, true);
//                try {
//                    this.currentPlayers.vectorPlayers[myID].rmiNextPlayer.setRestart();
//                } catch (RemoteException ex2) {
//                    System.out.println(DiceLiar.ANSI_RED + "!! FATAL ERROR - RMI ERROR IN FINALLY CODE" + DiceLiar.ANSI_RESET);
//                    Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//            currentBoard.status = Board.RESET;
//        }
    }
    
    public void broadcastRMI(Board board, String function){
        
        broadcastCount++;
        
        System.out.println(DiceLiar.ANSI_RED + myID + " chiama BROADCAST() " + function + " " + broadcastCount + DiceLiar.ANSI_RESET);
        
        
        int j=0;
        for (int i=(board.myID + 1)%board.getnPlayers(); j<board.getnPlayers(); i=(i+1)%board.getnPlayers()) {
            Player vectorPlayer = board.getCurrentPlayers().vectorPlayers[i];
            j++;
            
//            if(j==1 && board.myID == 0 && board.getnTurn() != 1){
//                System.out.println("Crasho");
//                System.exit(0);
//            }
            
            System.out.println("BROADCAST da " + myID + " a " + i);
            
            if(vectorPlayer.playerOut)
                continue;
            
            RMI rmiPointer = vectorPlayer.getRmiPointer();
            
            if(function.equalsIgnoreCase("RESET_DICE"))
                try {
                    rmiPointer.resetDice2(board);
            } catch (RemoteException ex) {
                System.out.println(DiceLiar.ANSI_RED + "!! CRASH RILEVATO. Il giocatore " + vectorPlayer.myID + " non è raggiungibile." + DiceLiar.ANSI_RESET);
                currentPlayers.removePlayer(currentPlayers.vectorPlayers[vectorPlayer.myID], true, false);
            }
            else if(function.equalsIgnoreCase("CHECK_DOUBT"))
                try {
                    rmiPointer.checkDoubtRMI(board);
            } catch (RemoteException ex) {
                System.out.println(DiceLiar.ANSI_RED + "!! CRASH RILEVATO. Il giocatore " + vectorPlayer.myID + " non è raggiungibile." + DiceLiar.ANSI_RESET);
                currentPlayers.removePlayer(currentPlayers.vectorPlayers[vectorPlayer.myID], true, false);
            }
            else if(function.equalsIgnoreCase("ONE_IS_ONE"))
                try {
                    rmiPointer.oneIsOne(board);
            } catch (RemoteException ex) {
                System.out.println(DiceLiar.ANSI_RED + "!! CRASH RILEVATO. Il giocatore " + vectorPlayer.myID + " non è raggiungibile." + DiceLiar.ANSI_RESET);
                currentPlayers.removePlayer(currentPlayers.vectorPlayers[vectorPlayer.myID], true, false);
            }
            else if(function.equalsIgnoreCase("NOTIFY_MOVE"))
                try {
                     if(this.currentPlayers.vectorPlayers[myID].myID == vectorPlayer.myID)
                         continue;
                    rmiPointer.notifyMove(board);
            } catch (RemoteException ex) {
                System.out.println(DiceLiar.ANSI_RED + "!! CRASH RILEVATO. Il giocatore " + vectorPlayer.myID + " non è raggiungibile." + DiceLiar.ANSI_RESET);
                currentPlayers.removePlayer(currentPlayers.vectorPlayers[vectorPlayer.myID], true, false);
            }
            else if (function.equalsIgnoreCase("SIGNAL_CRASH")) {
                if (this.currentPlayers.vectorPlayers[myID].myID == vectorPlayer.myID || vectorPlayer.playerOut) {
                    continue;
                }
                try {
                    System.out.println("Segnalo crash a " + vectorPlayer.myID);
                    rmiPointer.signalCrash(board);
                } catch (RemoteException ex) {
                    System.out.println(DiceLiar.ANSI_RED + "!! CRASH RILEVATO. Il giocatore " + vectorPlayer.myID + " non è raggiungibile." + DiceLiar.ANSI_RESET);
                    currentPlayers.removePlayer(currentPlayers.vectorPlayers[vectorPlayer.myID], true, true);
                }
            }
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
