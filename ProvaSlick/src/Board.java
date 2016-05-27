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
    public static int PLAYING = 2;
    public static int INIT_RESET = 3;
    
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";
        
    public transient final Object lock;
    
    public boolean oneJollyEnabled;
    
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
        currentPlayers = new Players(_nPlayers, _rmiPlayerArray);
        currentPlayers.getAllId();
        
        oneJollyEnabled = true;
        initBoard = true;

        status = PLAYING;
        
        gC = _gC;
     
        lock = _lock;
    }

    void initGame(Board startBoard, RMI _rmiNextPlayer){
        int playerStarterID = 0;
        
        startBoard.getCurrentPlayers().getVectorPlayers()[myID].rmiNextPlayer = _rmiNextPlayer;
        
        Player playerStarter = startBoard.getCurrentPlayers().getVectorPlayers()[playerStarterID];
        setPlayingPlayer(playerStarter);
        
        gC.setPlayingPlayer(playerStarter.myID);
        gC.setTurn(1);
                
        if(startBoard.myID == 0){
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
            
            if(myID == getPlayingPlayer().getMyID() && (status != Board.INIT_RESET)){
                status = PLAYING;

                if(!player.makeChoice(board))
                    return;
                
                gC.betClicked = false;
                
                printCount = 1;
                printCount2 = 1;    
                
                if(status == INIT_RESET){
                    //Nuovo turno
                    
                    synchronized (lock) {
                        lock.notifyAll();
                    }
                    
                    System.out.println("Passo il turno");
                    status = PLAYING;
                    return;
                }
                
                //Il turno passa al giocatore successivo
             
                if(status != INIT_RESET){
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
                            this.currentPlayers.vectorPlayers[myID].rmiNextPlayer.notifyTurn(board);
                        } catch (RemoteException ex2) {
                            System.out.println(DiceLiar.ANSI_RED + "!! FATAL ERROR - RMI ERROR IN FINALLY CODE" + DiceLiar.ANSI_RESET);
                            Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        
                    }
                }
                
                synchronized (lock) {
                    System.out.println("SBLOCCO TUTTI");
                    lock.notifyAll();
                    }
                System.out.println("Passo il turno");
                    
            }
            else{
                if(status == PLAYING){
                    
                    if(gC.playDiceAnimation)
                        return;
                    
                    System.out.println("Non tocca a me. Mi blocco sul giocatore " + getPlayingPlayer().myID);
                    try {
                        this.getPlayingPlayer().rmiPointer.checkPlayerCrash(this);
    
                    } catch (RemoteException ex) {
                        System.out.println(DiceLiar.ANSI_RED + "!! CRASH DEL PLAYING PLAYER RILEVATO. Il giocatore " + currentPlayers.vectorPlayers[getPlayingPlayer().myID].myID + "non è raggiungibile." + DiceLiar.ANSI_RESET);
                        int newPlaying = currentPlayers.removePlayer(currentPlayers.vectorPlayers[getPlayingPlayer().myID], true, false);
                        
                        if (getCurrentPlayers().getPlayersAlive() == 1 && this.winner == myID) {
                            System.out.println(DiceLiar.ANSI_GREEN + "Sei rimasto solo tu. HAI VINTO!" + DiceLiar.ANSI_RESET);
                            gC.winGame = true;
                            gC.restartBoard = false;
                            gC.initBoard = false;
                            gC.playDiceAnimation = false;
                            return;
                        }
                        
                        if(newPlaying == myID){ //Sono il nuovo PlayingPlayer
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
                                    lock.notifyAll();
                                }
                            }
                        }
                        else{ //Aggiorno il playing su cui bloccarmi
                            setPlayingPlayer(currentPlayers.vectorPlayers[newPlaying]);
                            gC.setPlayingPlayer(currentPlayers.vectorPlayers[newPlaying].myID);
                            
                            System.out.println("Turno GC: " + board.getnTurn());
                            getPlayingPlayer().setTurn(true);
                            gC.turn = board.getnTurn();
                        }
                    }
                    
                    printCount = 0;
                }
            }
    }
 

    void newTurn(Board currentBoard, int starterIDPlayer, Bet starterBet){ 
        getCurrentPlayers().resetAllDice(myID);

        currentBoard.setCurrentBet(starterBet);
        currentBoard.getCurrentPlayers().getVectorPlayers()[starterIDPlayer].setTurn(true); 
        currentBoard.setPlayingPlayer(currentBoard.getCurrentPlayers().getVectorPlayers()[starterIDPlayer]);
        gC.setPlayingPlayer(starterIDPlayer);
        
        broadcastRMI(currentBoard, "RESET_DICE");
        
        currentBoard.diceUpdated = 1;
        status = Board.INIT_RESET;
    }
    
    public void broadcastRMI(Board board, String function){
        
        broadcastCount++;
        
        System.out.println(DiceLiar.ANSI_RED + myID + " chiama BROADCAST() " + function + " " + broadcastCount + DiceLiar.ANSI_RESET);

        int j=0;
        for (int i=(board.myID + 1)%board.getnPlayers(); j<board.getnPlayers(); i=(i+1)%board.getnPlayers()) {
            Player vectorPlayer = board.getCurrentPlayers().vectorPlayers[i];
            j++;
            
//            if(j==1 && board.myID == 0 && board.getnTurn() == 1){
//                System.out.println("Crasho");
//                System.exit(0);
//            }
            
            //System.out.println("BROADCAST da " + myID + " a " + i);
            
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
        System.out.println("Sul tavolo ci sono " + currentPlayers.getAllDice(false)[currentBet.getValueDie()-1] + " dadi");
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
