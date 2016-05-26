

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author gorgo
 */
public class RMIGameController extends UnicastRemoteObject implements RMI {
    
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_CYAN = "\u001B[36m";
    
    Board rmiBoard;
    public final Object lock;
    int myID;
    
    public RMIGameController() throws RemoteException {
     lock = new Object();
     myID = -1;
    }

    @Override
    public ArrayList<PlayerEntry> addClient(String ipPlayer, int portPlayer) throws RemoteException {
        return null;
    }
    
    @Override
    public int getTimer() throws RemoteException {
        return -1;
    }

    @Override //-------------------------------------------------------------------------------- MAI USATA
    public int[] getDice(int idPlayer, Players currentPlayers) throws RemoteException {
        return rmiBoard.currentPlayers.vectorPlayers[idPlayer].getmyDiceValue();
    }

    @Override
    public boolean setDice(int id, Players playersArray) throws RemoteException {
        boolean lastChange = true;
        
        rmiBoard.haveToken = true;
        
        //System.out.println("SET DICE e DiceUpdated = " + rmiBoard.diceUpdated);
        
        if(rmiBoard.diceUpdated == rmiBoard.currentPlayers.getPlayersAlive()){
            System.out.println("DADI COMPLETAMENTE AGGIORNATI");
            return lastChange;  
        }
            
        
        //System.out.println("Sono " + myID + " e mi ha sbloccato " + id);
        for (int i=0; i<playersArray.vectorPlayers.length; i++) {
        //for (int i=0; i<playersArray.getVectorPlayers().length; i++) {
            
           
            if(rmiBoard.currentPlayers.vectorPlayers[i].getMyDiceObject() == null && playersArray.getVectorPlayers()[i].getMyDiceObject() != null){
                System.out.println(myID + ": aggiorno i miei dadi con quelli del giocatore " + i);         
                rmiBoard.diceUpdated++;
                rmiBoard.currentPlayers.vectorPlayers[i] = playersArray.getVectorPlayers()[i];
                
                lastChange = false;

                if(rmiBoard.diceUpdated == rmiBoard.currentPlayers.getPlayersAlive()){
                    //System.out.println("Ho aggiornato rmiBoard diceUpdate " + rmiBoard.diceUpdated + " giocatori");
                    
                    System.out.println("\n------------------TUTTI I DADI---------------------------");
                    rmiBoard.currentPlayers.printDice();
                    System.out.println("\n");

                    
                    lastChange = true;
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
        GUIController gC = rmiBoard.getgC();
        System.out.println("RMI ID: " + board.myID + " NotifyTurn in");
        
        rmiBoard.setnTurn(board.getnTurn());
        gC.turn = board.getnTurn();
        
        rmiBoard.setCurrentBet(board.getCurrentBet());
        gC.diceAmountSelected = board.getCurrentBet().getAmount();
        gC.diceValueSelected = board.getCurrentBet().getValueDie();
        
        gC.setBetOnTable(true);
        gC.idLastBet = board.myID;
        
        //if(rmiBoard.getCurrentBet() == null || board.getCurrentBet() == null)
           //rmiBoard.getCurrentPlayers().vectorPlayers[myID].makeChoice(rmiBoard);
        
        rmiBoard.setPlayingPlayer(board.getPlayingPlayer());
        rmiBoard.getPlayingPlayer().setTurn(true);        
        rmiBoard.getCurrentPlayers().getVectorPlayers()[myID].setTurn(true);
        
        
        rmiBoard.gC.setPlayingPlayer(board.getPlayingPlayer().myID);
        rmiBoard.gC.turn = board.getnTurn();
        
       // System.out.println("Sblocco il giocatore " + rmiBoard.myID);
        
        rmiBoard.status = board.status;
        System.out.println("NotifyTurn out");
    }

    @Override
    public void resetDice(Players currentPlayers) throws RemoteException {
        GUIController gC = rmiBoard.getgC();
        //System.out.println("-------------------------------------------------------------------------------------------");
        //System.out.println("ID: " + myID + " RESET DICEUpDated");
        rmiBoard.getCurrentPlayers().resetAllDice(myID);
        
        rmiBoard.diceUpdated = 1;  
        rmiBoard.status = Board.RESET;
        
        //System.out.println("ID: " + rmiBoard.myID + " Imposto Turno 1");
        rmiBoard.setnTurn(1);
        gC.setTurn(1);
        
        gC.diceAmountSelected = 0;
        gC.diceValueSelected = 0;
        
        rmiBoard.haveToken = false;
        
        gC.setBetOnTable(false);
        
        System.out.println("------------------DADI LOCALI CREATI NELLA RESETDICE---------------------------");
        int[] myDice = rmiBoard.getCurrentPlayers().getVectorPlayers()[myID].getmyDiceValue();
        for(int i=0;i<myDice.length;i++)
            System.out.println("Player " + myID + ": " + myDice[i]);
    }
    
    @Override
    public void resetDice2(Board board) throws RemoteException {
        GUIController gC = rmiBoard.getgC();
        
        for (int i=0; i<rmiBoard.nPlayers; i++)
            rmiBoard.getCurrentPlayers().vectorPlayers[i].myDice = board.getCurrentPlayers().vectorPlayers[i].myDice;
        
        for (int i=0; i<rmiBoard.getCurrentPlayers().startAmountDice; i++)
            if(rmiBoard.getCurrentPlayers().vectorPlayers[rmiBoard.myID].getMyDice()[i].value != 0)
                rmiBoard.gC.dicePlayer[i] = 1;
            else
                rmiBoard.gC.dicePlayer[i] = 0;
                     
        gC.restartBoard = true;
        gC.playDiceAnimation = true;
        gC.oneJollyEnabled = true;
        gC.setTurn(1);
        gC.setBetOnTable(false);
        gC.diceAmountSelected = 0;
        gC.diceValueSelected = 0;
        gC.oneJollyEnabled = true;
        
        rmiBoard.setnTurn(1);
        rmiBoard.status = Board.PLAYING;
        rmiBoard.printCount = 0;
        rmiBoard.printCount2 = 0;
        rmiBoard.currentBet = null;
        rmiBoard.oneJollyEnabled = true;    
                    
        
        System.out.println(DiceLiar.ANSI_CYAN + "TURNO PRECEDENTE VINTO DA " + board.winner + DiceLiar.ANSI_RESET);
        System.out.println(DiceLiar.ANSI_CYAN + "TURNO PRECEDENTE PERSO DA " + board.loser + DiceLiar.ANSI_RESET); 
        
        if(!board.getCurrentPlayers().vectorPlayers[board.loser].playerOut){
            if(board.loser != board.gC.idLastBet && board.loser != board.winner){
                System.out.println(DiceLiar.ANSI_CYAN + "id Last bet in: " + gC.idLastBet + DiceLiar.ANSI_RESET);
                rmiBoard.gC.totalDicePlayer[board.loser]--;

                if (rmiBoard.gC.totalDicePlayer[board.loser] == 0) {
                    System.out.println("Il giocatore " + board.loser + " ha perso!");
                    rmiBoard.getCurrentPlayers().removePlayer(rmiBoard.getCurrentPlayers().getVectorPlayers()[board.loser], false, false);
                }
            }
            else{
                System.out.println(DiceLiar.ANSI_CYAN + "id Last bet else: " + board.gC.idLastBet + DiceLiar.ANSI_RESET);
                System.out.println(DiceLiar.ANSI_RED + "Chi ha perso non è più online, non perde il dado" + DiceLiar.ANSI_RESET);
            }
        }
        else{
            rmiBoard.getCurrentPlayers().removePlayer(rmiBoard.getCurrentPlayers().getVectorPlayers()[board.loser], false, false);
        }
        
        if(rmiBoard.getCurrentPlayers().getPlayersAlive() == 1 && rmiBoard.winner == board.myID){
            System.out.println(DiceLiar.ANSI_GREEN + "Sei rimasto solo tu. HAI VINTO!" + DiceLiar.ANSI_RESET);
            rmiBoard.gC.winGame = true;
            return;
        }
        else if(rmiBoard.getCurrentPlayers().getPlayersAlive() == 1 && rmiBoard.loser == rmiBoard.myID){
            rmiBoard.gC.loseGame = true;
            
            rmiBoard.gC.restartBoard = false;
            rmiBoard.gC.initBoard = false;
            rmiBoard.gC.playDiceAnimation = false;
            System.out.println(DiceLiar.ANSI_RED + "HAI PERSO!" + DiceLiar.ANSI_RESET);
            return;
        }
        
        gC.setPlayingPlayer(board.winner);
        rmiBoard.setPlayingPlayer(rmiBoard.getCurrentPlayers().vectorPlayers[board.winner]);
        rmiBoard.getCurrentPlayers().vectorPlayers[board.winner].setTurn(true);
        
        System.out.println(DiceLiar.ANSI_CYAN + "INIZIA IL TURNO " + board.getPlayingPlayer().myID + DiceLiar.ANSI_RESET);
        
        System.out.println(DiceLiar.ANSI_CYAN + "NUOVI DADI" + DiceLiar.ANSI_RESET);
        rmiBoard.currentPlayers.printDice();
        
        rmiBoard.initBoard = false;
        
        System.out.println("------------------------------------------------------------------");
        
    }

    @Override
    public void checkDoubtRMI(Board board) throws RemoteException {
       System.out.println(ANSI_CYAN + "Il giocatore " + board.getPlayingPlayer().myID + " ha detto che sul tavolo NON ci sono almeno " + board.getCurrentBet().amountDice + " dadi di valore " + board.getCurrentBet().valueDie + ANSI_RESET);
       System.out.println(ANSI_CYAN + "Io sono: " + myID + ANSI_RESET);
       
       int[] vectorDice = board.getCurrentPlayers().getAllDice(true); // Stampo tutti i dadi
       if(board.okDoubt){
           System.out.println(ANSI_GREEN + "Ha ragione, ce ne sono " + vectorDice[board.getCurrentBet().valueDie-1] + "! Tocca a lui iniziare il nuovo giro" + ANSI_RESET);
           
           System.out.println(DiceLiar.ANSI_RED + "Io sono " + rmiBoard.myID + " e ha dubitato " + board.getPlayingPlayer().myID + " e il suo prev è " + board.getPlayingPlayer().IDPrev + DiceLiar.ANSI_RESET);
           if(board.getPlayingPlayer().IDPrev == rmiBoard.myID){
               System.out.println(ANSI_RED + "Perdo un dado :(" + ANSI_RESET);
               //System.out.println(ANSI_CYAN + "Io sono: " + myID + ANSI_RESET);
               rmiBoard.getCurrentPlayers().getVectorPlayers()[myID].getMyDiceObject().removeDie();
               rmiBoard.gC.totalDicePlayer[rmiBoard.getCurrentPlayers().getVectorPlayers()[myID].myID]--;
               
               if(rmiBoard.gC.totalDicePlayer[rmiBoard.getCurrentPlayers().getVectorPlayers()[myID].myID] == 0)
                    rmiBoard.getCurrentPlayers().removePlayer(rmiBoard.getCurrentPlayers().getVectorPlayers()[myID], false, false);
           }
           else{
                rmiBoard.gC.totalDicePlayer[rmiBoard.gC.idLastBet]--;
                
                if(rmiBoard.gC.totalDicePlayer[rmiBoard.gC.idLastBet] == 0){
                    rmiBoard.getCurrentPlayers().removePlayer(rmiBoard.getCurrentPlayers().vectorPlayers[rmiBoard.gC.idLastBet], false, false);
                    if(rmiBoard.myID == rmiBoard.gC.idLastBet)
                        rmiBoard.gC.loseGame = true;
                }
           }
               
       }
       else{
           System.out.println(ANSI_RED + "Non ha ragione, ce ne sono " + vectorDice[board.getCurrentBet().valueDie-1] + "! Perde un dado e inizia il giocatore successivo" + ANSI_RESET);
           //System.out.println(ANSI_GREEN + "Io sono: " + myID + ANSI_RESET + "e ha perso " + board.getPlayingPlayer().myID );
           
           rmiBoard.gC.totalDicePlayer[board.getPlayingPlayer().myID]--;  
       }
       
       System.out.println("\n-------------------------------------- NUOVO TURNO -------------------------------------------\n");

    }

    @Override
    public void setRestart() throws RemoteException {
        System.out.println("SETRESTART - QUELLO PRIMA DI ME HA ERRONEAMENTE DUBITATO");
        rmiBoard.getCurrentPlayers().getVectorPlayers()[myID].setTurn(true);
        rmiBoard.setPlayingPlayer(rmiBoard.getCurrentPlayers().getVectorPlayers()[myID]);
        rmiBoard.diceUpdated = 1;
        rmiBoard.status = Board.INIT_RESET;
        
        rmiBoard.setCurrentBet(null);
        
//        synchronized (rmiBoard.lock){
//            rmiBoard.ready = true;
//            rmiBoard.lock.notify();
//        }
    }

    @Override
    public void oneIsOne(Board board) throws RemoteException {
        System.out.println("Uno vale uno. Non posso più usarlo come jolly fino al prossimo turno");
        rmiBoard.oneJollyEnabled = false;
        rmiBoard.gC.oneJollyEnabled = false;
        rmiBoard.currentPlayers.printDice();
    }  

    @Override
    public void notifyMove(Board board) throws RemoteException {
        GUIController gC = rmiBoard.getgC();
        System.out.println("NOTIFYMOVE: Il giocatore " + board.getPlayingPlayer().myID + " ha rilanciato con " + board.getCurrentBet().amountDice + " dadi di valore " + board.getCurrentBet().valueDie);
        gC.betOnTable = true;
        gC.diceAmountSelected = board.getCurrentBet().amountDice;
        gC.diceValueSelected = board.getCurrentBet().valueDie;
           
        rmiBoard.currentBet = board.currentBet;
        rmiBoard.setPlayingPlayer(board.currentPlayers.vectorPlayers[board.getPlayingPlayer().IDNext]);
        gC.setPlayingPlayer(board.getPlayingPlayer().IDNext);
        
        System.out.println("NOTIFYMOVE - TURN: " + board.getnTurn());
        gC.setTurn(board.getnTurn());
        rmiBoard.setnTurn(board.getnTurn());
        
        gC.idLastBet = board.getPlayingPlayer().myID; 
    }

    @Override
    public boolean heartbeat() throws RemoteException {
        return true;
    }

    @Override
    public void signalCrash(Board board) throws RemoteException {
        for(int i=0; i<board.currentPlayers.vectorPlayers.length; i++){
            if(rmiBoard.currentPlayers.vectorPlayers[i].playerOut != board.currentPlayers.vectorPlayers[i].playerOut){
                System.out.println("ID: " + rmiBoard.myID + DiceLiar.ANSI_RED + " !! SIGNAL CRASH: IL GIOCATORE " + i + " E' CRASHATO" + DiceLiar.ANSI_RESET);
                rmiBoard.currentPlayers.removePlayer(rmiBoard.currentPlayers.vectorPlayers[i], true, false);
                
                if(board.getnTurn() == 1){
                    rmiBoard.initBoard = false;
                }
            }
        }
    }

    @Override
    public void checkPlayerCrash(Board currentBoard) throws RemoteException {
        synchronized (rmiBoard.lock) {
            try {
                //while (!rmiBoard.ready){ 
                System.out.println(currentBoard.myID + " MI BLOCCO!"); 
                rmiBoard.lock.wait(); 
                System.out.println(currentBoard.myID + " MI SBLOCCO e ready: " + rmiBoard.ready); //}
                rmiBoard.lockCount++;
                if(rmiBoard.lockCount == rmiBoard.getCurrentPlayers().getPlayersAlive() - 1){
                    rmiBoard.ready = false;
                    rmiBoard.lockCount = 0;
                }
                System.out.println(currentBoard.myID + " MI SBLOCCO");
            } catch (InterruptedException ex) {}
        } 
    }
    
    
    
}
