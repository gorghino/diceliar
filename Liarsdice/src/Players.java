import java.io.Serializable;
import java.rmi.NotBoundException;
import java.util.ArrayList;
import java.util.Arrays;

public class Players implements Serializable{

    private Player[] vectorPlayers;
    private Board currentBoard;
    private int startAmountDice;

    public Players(int _nPlayers, ArrayList<PlayerEntry> _rmiPlayerArray) throws NotBoundException{
        vectorPlayers = new Player[_nPlayers];
        startAmountDice = 5;
        
        for (int i = 0; i < _nPlayers; i += 1) {
            vectorPlayers[i] = new Player(this, i, _rmiPlayerArray.get(i).ip, _rmiPlayerArray.get(i).port, startAmountDice);
        }
    }

    public Board getCurrentBoard() {
        return currentBoard;
    }

    public void setCurrentBoard(Board _currentBoard) {
        this.currentBoard = _currentBoard;
    }

    int removePlayer(Player playerToRemove, boolean isCrashed, boolean broadcasted){
        int prev = -1;
        int next = -1;
        
        if(isCrashed){
            prev = playerToRemove.findPrevPlayer();
            next = playerToRemove.findNextPlayer();
            vectorPlayers[prev].IDNext = next; //Faccio puntare al precedente, il registro RMI del successivo
            vectorPlayers[next].IDPrev = prev;
            vectorPlayers[prev].rmiNextPlayer = vectorPlayers[next].rmiPointer;
            vectorPlayers[playerToRemove.getMyID()].setPlayerOut(true);
            
            if(!broadcasted)
                currentBoard.broadcastRMI(currentBoard, "SIGNAL_CRASH");
            
            //System.out.println(DiceLiar.ANSI_GREEN + "Ora il player " + prev + " invia a " + next + DiceLiar.ANSI_RESET);
        } 
        //System.out.println(DiceLiar.ANSI_RED + "Il giocatore " + playerToRemove.myID + " non gioca più." + DiceLiar.ANSI_RESET);
        vectorPlayers[playerToRemove.getMyID()].setPlayerOut(true);
        currentBoard.gC.setCountDiceCrashed(true);
         
        return next;
    }
    
    public int[] getAllDice(boolean printValues){
        GUIController gC = currentBoard.gC;
        int[] allDiceVector = new int[6];
        for (int i = 0; i < vectorPlayers.length; i += 1) {
            Player player = vectorPlayers[i];
            
            if(player.isPlayerOut() && !gC.isCountDiceCrashed())  continue;  
            
            for(int j=0; j<6; j += 1){
                //System.out.println("Il giocatore " + i + " ha " + player.getmyDiceValueGrouped(currentBoard)[j] + " dadi di valore " + (j+1));
                allDiceVector[j] += player.getmyDiceValueGrouped(currentBoard)[j];
            }
        }

        if(printValues)
            System.out.println(Arrays.toString(allDiceVector));

        return allDiceVector;

    }

    public Player[] getVectorPlayers() {
        return vectorPlayers;
    }

    public void setVectorPlayers(Player[] _vectorPlayers) {
        this.vectorPlayers = _vectorPlayers;
    }

    public int[] getAllId(){
        int[] idArray = new int[vectorPlayers.length];
        for (int i=0;i<vectorPlayers.length;i++)
            idArray[i] = vectorPlayers[i].getMyID();
        return idArray;
    }

    public void resetAllDice(int myID){
        for(Player vectorPlayer : vectorPlayers) {
            if(vectorPlayer.isPlayerOut())
                continue;
            vectorPlayer.resetDice();
        }
    }

    public void printDice(){
        String jollyString;
        
        if(currentBoard.isOneJollyEnabled())
            jollyString = "1 VALE Jolly";
        else
            jollyString = "1 VALE 1";    
        
         for (int i=0;i<vectorPlayers.length;i++)
               System.out.println(i + ": " + Arrays.toString(vectorPlayers[i].getmyDiceValue()) + "\t -- "+ jollyString + " -->\t" + Arrays.toString(vectorPlayers[i].getmyDiceValueGrouped(currentBoard)));
    }
    
    public int getPlayersAlive(){
        int j=0;
        for (Player vectorPlayer : vectorPlayers)
            if (!vectorPlayer.isPlayerOut())
                j++;
        return j;
    }

    public int getStartAmountDice() {
        return startAmountDice;
    }

    public void setStartAmountDice(int startAmountDice) {
        this.startAmountDice = startAmountDice;
    }
}
