

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
        gC.countDiceCrashed = false;
        
        rmiBoard.setnTurn(1);
        rmiBoard.status = Board.PLAYING;
        rmiBoard.printCount = 0;
        rmiBoard.printCount2 = 0;
        rmiBoard.currentBet = null;
        rmiBoard.oneJollyEnabled = true;
        
        rmiBoard.winner = board.winner;
        rmiBoard.loser = board.loser;
                    
        
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
        
        System.out.println("Players alive: " + rmiBoard.getCurrentPlayers().getPlayersAlive());
        
        if(rmiBoard.getCurrentPlayers().getPlayersAlive() != rmiBoard.getnPlayers()){ //Se ci sono player crashati, azzero i dadi
            for (int i=0; i<rmiBoard.nPlayers; i++){
                if(rmiBoard.getCurrentPlayers().vectorPlayers[i].playerOut)
                    rmiBoard.getCurrentPlayers().vectorPlayers[i].myDice.deleteDice();
            }
        }
        
        if(rmiBoard.getCurrentPlayers().getPlayersAlive() == 1 && board.winner == rmiBoard.myID){
            System.out.println(DiceLiar.ANSI_GREEN + "Sei rimasto solo tu. HAI VINTO!" + DiceLiar.ANSI_RESET);
            rmiBoard.gC.winGame = true;
            rmiBoard.gC.restartBoard = false;
            rmiBoard.gC.initBoard = false;
            rmiBoard.gC.playDiceAnimation = false;
            return;
        }
        
        System.out.println("Check sul giocatore " + rmiBoard.getCurrentPlayers().vectorPlayers[rmiBoard.myID].myID + " playerOut: " + rmiBoard.getCurrentPlayers().vectorPlayers[rmiBoard.myID].playerOut);
        
        if(rmiBoard.getCurrentPlayers().vectorPlayers[rmiBoard.myID].playerOut){
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
        gC.updateBetValues = true;
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
                rmiBoard.lock.wait(); 
            } catch (InterruptedException ex) {}
        } 
    }
}
