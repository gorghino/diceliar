import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class RMIGameController extends UnicastRemoteObject implements RMI {
    
    Board rmiBoard;
    public final Object lock;
    int myID;
    
    public RMIGameController() throws RemoteException {
     lock = new Object();
     myID = -1;
    }

    @Override
    public ArrayList<PlayerEntry> addClient(String ipPlayer, int portPlayer) throws RemoteException { return null; }
    
    @Override
    public int getTimer() throws RemoteException { return -1; }
    
    @Override
    public void notifyTurn(Board board) throws RemoteException{
        GUIController gC = rmiBoard.getgC();

        rmiBoard.setnTurn(board.getnTurn());  
        rmiBoard.setCurrentBet(board.getCurrentBet());
        
        gC.setDiceAmountSelected(board.getCurrentBet().getAmount());
        gC.setDiceValueSelected(board.getCurrentBet().getValueDie());
        
        gC.setBetOnTable(true);
        rmiBoard.setIdLastBet(board.getMyID());
        rmiBoard.setPlayingPlayer(board.getPlayingPlayer());
        rmiBoard.getPlayingPlayer().setTurn(true);        
        rmiBoard.getCurrentPlayers().getVectorPlayers()[myID].setTurn(true);
        rmiBoard.setStatus(board.getStatus());
    }
    
    @Override
    public void resetDice(Board board) throws RemoteException {
        GUIController gC = rmiBoard.getgC();
        
        for (int i=0; i<rmiBoard.getnPlayers(); i++)
            rmiBoard.getCurrentPlayers().getVectorPlayers()[i].setMyDice(board.getCurrentPlayers().getVectorPlayers()[i].getMyDiceObject());
        
        for (int i=0; i<rmiBoard.getCurrentPlayers().getStartAmountDice(); i++)
            if(rmiBoard.getCurrentPlayers().getVectorPlayers()[rmiBoard.getMyID()].getMyDice()[i].value != 0)
                rmiBoard.gC.getDicePlayer()[i] = 1;
            else
                rmiBoard.gC.getDicePlayer()[i] = 0;
                     
        gC.setRestartBoard(true);
        gC.setTurn(1);
        gC.setBetOnTable(false);
        gC.setDiceAmountSelected(0);
        gC.setDiceValueSelected(0);
        gC.setCountDiceCrashed(false);
        gC.setPlayDiceAnimation(true);
        
        rmiBoard.setnTurn(1);
        rmiBoard.setStatus(Board.PLAYING);
        rmiBoard.setCurrentBet(null);
        rmiBoard.setOneJollyEnabled(true);  
        rmiBoard.setWinner(board.getWinner()); 
        rmiBoard.setLoser(board.getLoser());
                    
        
        System.out.println(DiceLiar.ANSI_CYAN + "TURNO PRECEDENTE VINTO DA " + board.getWinner() + DiceLiar.ANSI_RESET);
        System.out.println(DiceLiar.ANSI_CYAN + "TURNO PRECEDENTE PERSO DA " + board.getLoser() + DiceLiar.ANSI_RESET); 
        
        if(!board.getCurrentPlayers().getVectorPlayers()[board.getLoser()].isPlayerOut()){
            if(board.getLoser() != board.getIdLastBet() && board.getLoser() != board.getWinner()){
                rmiBoard.gC.getTotalDicePlayer()[board.getLoser()]--;

                if (rmiBoard.gC.getTotalDicePlayer()[board.getLoser()] == 0) {
                    System.out.println("Il giocatore " + board.getLoser() + " ha perso!");
                    rmiBoard.getCurrentPlayers().removePlayer(rmiBoard.getCurrentPlayers().getVectorPlayers()[board.getLoser()], false, false);
                }
            }
        }
        else{
            rmiBoard.getCurrentPlayers().removePlayer(rmiBoard.getCurrentPlayers().getVectorPlayers()[board.getLoser()], false, false);
        }

        if(rmiBoard.getCurrentPlayers().getPlayersAlive() != rmiBoard.getnPlayers()){ //Se ci sono player crashati, azzero i dadi
            for (int i=0; i<rmiBoard.getnPlayers(); i++){
                if(rmiBoard.getCurrentPlayers().getVectorPlayers()[i].isPlayerOut())
                    rmiBoard.getCurrentPlayers().getVectorPlayers()[i].getMyDiceObject().deleteDice();
            }
        }
        
        if(rmiBoard.getCurrentPlayers().getPlayersAlive() == 1 && board.getWinner() == rmiBoard.getMyID()){
            System.out.println(DiceLiar.ANSI_GREEN + "Sei rimasto solo tu. HAI VINTO!" + DiceLiar.ANSI_RESET);
            rmiBoard.gC.setWinGame(true);
            rmiBoard.gC.setRestartBoard(false);
            rmiBoard.gC.setInitBoard(false);
            rmiBoard.gC.playDiceAnimation = false;
            return;
        }
                
        if(rmiBoard.getCurrentPlayers().getVectorPlayers()[rmiBoard.getMyID()].isPlayerOut()){
            rmiBoard.gC.setLoseGame(true);
            rmiBoard.gC.setRestartBoard(false);
            rmiBoard.gC.setInitBoard(false);
            rmiBoard.gC.playDiceAnimation = false;
            System.out.println(DiceLiar.ANSI_RED + "HAI PERSO!" + DiceLiar.ANSI_RESET);
            return;
        }
        
        rmiBoard.setPlayingPlayer(rmiBoard.getCurrentPlayers().getVectorPlayers()[board.getWinner()]);
        rmiBoard.getCurrentPlayers().getVectorPlayers()[board.getWinner()].setTurn(true);
        
        //System.out.println(DiceLiar.ANSI_CYAN + "INIZIA IL TURNO " + board.getPlayingPlayer().myID + DiceLiar.ANSI_RESET);
        //System.out.println(DiceLiar.ANSI_CYAN + "NUOVI DADI" + DiceLiar.ANSI_RESET);
        
        rmiBoard.getCurrentPlayers().printDice();
        rmiBoard.setInitGame(false);
        
        System.out.println("------------------------------------------------------------------");
        
    }

    @Override
    public void oneIsOne(Board board) throws RemoteException {
        System.out.println("Uno vale uno. Non posso più usarlo come jolly fino al prossimo turno");
        rmiBoard.setOneJollyEnabled(false);
        rmiBoard.getCurrentPlayers().printDice();
    }  

    @Override
    public void notifyMove(Board board) throws RemoteException {
        GUIController gC = rmiBoard.getgC();
        //System.out.println("NOTIFYMOVE: Il giocatore " + board.getPlayingPlayer().myID + " ha rilanciato con " + board.getCurrentBet().amountDice + " dadi di valore " + board.getCurrentBet().valueDie);
        
        gC.setBetOnTable(true);
        gC.setDiceAmountSelected(board.getCurrentBet().getAmount());
        gC.setDiceValueSelected(board.getCurrentBet().getValueDie());
        gC.setUpdateBetValues(true);
           
        rmiBoard.setCurrentBet(board.getCurrentBet());
        rmiBoard.setPlayingPlayer(board.getCurrentPlayers().getVectorPlayers()[board.getPlayingPlayer().IDNext]);
        rmiBoard.setnTurn(board.getnTurn());
        rmiBoard.setIdLastBet(board.getPlayingPlayer().getMyID());    
    }

    @Override
    public boolean heartbeat() throws RemoteException { return true; }

    @Override
    public void signalCrash(Board board) throws RemoteException {
        for(int i=0; i<board.getCurrentPlayers().getVectorPlayers().length; i++){
            if(rmiBoard.getCurrentPlayers().getVectorPlayers()[i].isPlayerOut() != board.getCurrentPlayers().getVectorPlayers()[i].isPlayerOut()){
                System.out.println("ID: " + rmiBoard.getMyID() + DiceLiar.ANSI_RED + " !! SIGNAL CRASH: IL GIOCATORE " + i + " E' CRASHATO" + DiceLiar.ANSI_RESET);
                rmiBoard.getCurrentPlayers().removePlayer(rmiBoard.getCurrentPlayers().getVectorPlayers()[i], true, false);
                
                if(board.getnTurn() == 1){
                    rmiBoard.setInitGame(false);
                }
            }
        }
    }

    @Override
    public void checkPlayerCrash(Board currentBoard) throws RemoteException {
        synchronized (rmiBoard.lock) {
            try { rmiBoard.lock.wait();  } catch (InterruptedException ex) {}
        } 
    }
}
