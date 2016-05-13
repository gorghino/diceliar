

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
    public int[] getDice(int idPlayer, Players currentPlayers) throws RemoteException {
        return rmiBoard.currentPlayers.vectorPlayers[idPlayer].getmyDiceValue();
    }

    @Override
    public boolean setDice(int id, Players playersArray) throws RemoteException {
        boolean lastChange = true;
        
        rmiBoard.haveToken = true;
        
        //System.out.println("SET DICE e DiceUpdated = " + rmiBoard.diceUpdated);
        
        if(rmiBoard.diceUpdated == rmiBoard.currentPlayers.getVectorPlayers().length){
            System.out.println("DADI COMPLETAMENTE AGGIORNATI");
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
                    System.out.println("\n");
//                    for (int j = 0; j < rmiBoard.currentPlayers.getAllId().length; j++) {
//                        int[] myDice = rmiBoard.currentPlayers.getVectorPlayers()[j].getmyDiceValue();
//                        for (int z = 0; z < myDice.length; z++) {
//                            System.out.println("Player " + j + ": " + myDice[z]);
//                        }
//                    }
                    
                    lastChange = true;
                    //rmiBoard.status = Board.PLAYING;
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
        System.out.println("NotifyTurn in");
        
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
        rmiBoard.setPlayingPlayer(board.getPlayingPlayer());
        
       // System.out.println("Sblocco il giocatore " + rmiBoard.myID);
        
        rmiBoard.status = board.status;
        
        synchronized (rmiBoard.lock){
            rmiBoard.ready = true;
            rmiBoard.lock.notify();
            if(rmiBoard.status == Board.PLAYING){
                System.out.println("NotifyTurn: Il turno CONTINUA\n");
            }
            //else if(rmiBoard.status == Board.RESET){
               // System.out.println("NotifyTurn: Io " + rmiBoard.myID + " Devo resettare i dadi\n");
            //}
        }
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
        
        System.out.println("ID: " + rmiBoard.myID + "Imposto Turno 1");
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
       
        //shareDice(currentPlayers, rmiNext);
        
//        synchronized (rmiBoard.lock){
//            rmiBoard.ready = true;
//            rmiBoard.lock.notify();
//        }
    }

    @Override
    public void checkDoubtRMI(Board board) throws RemoteException {
       System.out.println(ANSI_CYAN + "Il giocatore " + board.getPlayingPlayer().myID + " ha detto che sul tavolo NON ci sono almeno " + board.getCurrentBet().amountDice + " dadi di valore " + board.getCurrentBet().valueDie + ANSI_RESET);
       System.out.println(ANSI_RED + "Io sono: " + myID + ANSI_RESET);
       
       int[] vectorDice = board.getCurrentPlayers().getAllDice(true); // Stampo tutti i dadi
       if(board.okDoubt){
           System.out.println(ANSI_GREEN + "Ha ragione, ce ne sono " + vectorDice[board.getCurrentBet().valueDie-1] + "! Tocca a lui iniziare il nuovo giro" + ANSI_RESET);
           
           if( (((board.getPlayingPlayer().myID - myID)%board.getnPlayers()) + board.getnPlayers())%board.getnPlayers() == 1){
               System.out.println(ANSI_RED + "Perdo un dado :(" + ANSI_RESET);
                System.out.println(ANSI_CYAN + "Io sono: " + myID + ANSI_RESET);
               rmiBoard.getCurrentPlayers().getVectorPlayers()[myID].getMyDiceObject().removeDie();
               rmiBoard.gC.totalDicePlayer[rmiBoard.getCurrentPlayers().getVectorPlayers()[myID].myID]--;
           }
           else{
                System.out.println("Io sono: " + myID );
                rmiBoard.gC.totalDicePlayer[rmiBoard.gC.idLastBet]--;
           }
               
       }
       else{
           System.out.println(ANSI_RED + "Non ha ragione, ce ne sono " + vectorDice[board.getCurrentBet().valueDie-1] + "! Perde un dado e inizia il giocatore successivo" + ANSI_RESET);
           System.out.println(ANSI_GREEN + "Io sono: " + myID + ANSI_RESET + "e ha perso " + board.getPlayingPlayer().myID );
           
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
        gC.idLastBet = board.getPlayingPlayer().myID; 
    }

    @Override
    public boolean heartbeat() throws RemoteException {
        return true;
    }
    
    
    
}
