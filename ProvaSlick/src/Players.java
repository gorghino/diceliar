/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.Serializable;
import java.rmi.NotBoundException;
import java.util.ArrayList;
import java.util.Arrays;
/**
 *
 * @author proietfb
 */
public class Players implements Serializable{

    Player[] vectorPlayers;
    Board currentBoard;
    int startAmountDice;

    public Players(int _nPlayers, ArrayList<PlayerEntry> _rmiPlayerArray) throws NotBoundException{
        //costruttore
        
        vectorPlayers = new Player[_nPlayers];
        startAmountDice = 1;
        
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
            
            vectorPlayers[playerToRemove.myID].playerOut = true;
            
            if(!broadcasted)
                currentBoard.broadcastRMI(currentBoard, "SIGNAL_CRASH");
            
            System.out.println(DiceLiar.ANSI_GREEN + "Ora il player " + prev + " invia a " + next + Board.ANSI_RESET);
        }
        
        System.out.println(DiceLiar.ANSI_RED + "Il giocatore " + playerToRemove.myID + " non gioca più." + DiceLiar.ANSI_RESET);
        
        vectorPlayers[playerToRemove.myID].playerOut = true;
        
        if(this.getPlayersAlive() == 1 && currentBoard.winner == currentBoard.myID){
            System.out.println(DiceLiar.ANSI_GREEN + "Sei rimasto solo tu. HAI VINTO!" + DiceLiar.ANSI_RESET);
            currentBoard.gC.winGame = true;
        }
        else if(this.getPlayersAlive() == 1 && currentBoard.loser == currentBoard.myID){
            currentBoard.gC.loseGame = true;
        }
        
        return next;
    }
    
    public int[] getAllDice(boolean printValues){
        int[] allDiceVector = new int[6];
        for (int i = 0; i < vectorPlayers.length; i += 1) {
            Player player = vectorPlayers[i];
            
            if(player.playerOut)
                continue;
            
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
            if(vectorPlayer.playerOut)
                continue;
           
            vectorPlayer.resetDice();
        }
    }

    public void printDice(){
        String jollyString;
        
        if(currentBoard.oneJollyEnabled)
            jollyString = "1 VALE Jolly";
        else
            jollyString = "1 VALE 1";    
        
         for (int i=0;i<vectorPlayers.length;i++)
               System.out.println(i + ": " + Arrays.toString(vectorPlayers[i].getmyDiceValue()) + "\t -- "+ jollyString + " -->\t" + Arrays.toString(vectorPlayers[i].getmyDiceValueGrouped(currentBoard)));
    }
    
    public int getPlayersAlive(){
        int j=0;
        
        for (Player vectorPlayer : vectorPlayers)
            if (!vectorPlayer.playerOut)
                j++;
        
        return j;
    
    }

}
