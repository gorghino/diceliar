

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author proietfb
 */
public class Player implements Serializable{
    String name;
    int myID;
    String myIP;
    int myPort;
    
    RMI rmiPointer;
    
    RMI rmiNextPlayer;
    
    boolean playerOut = false;
    
    int IDPrev;
    int IDNext; 
    
    boolean myTurn;
    Bet myBet;
    Players allPlayers;
    Dice myDice;

    public Player(Players _allPlayers, int _myID, String _myIP, int _myPort, int startAmountDice) throws NotBoundException{
        System.out.println("Creo player con ID " + _myID + ", IP: " + _myIP + ":" + _myPort);
        myID = _myID;
        myIP = _myIP;
        myPort = _myPort;
        allPlayers = _allPlayers;
        myTurn = false;
        
        IDNext = ((((myID + 1) % allPlayers.vectorPlayers.length) + allPlayers.vectorPlayers.length) % allPlayers.vectorPlayers.length);
        IDPrev = ((((myID - 1) % allPlayers.vectorPlayers.length) + allPlayers.vectorPlayers.length) % allPlayers.vectorPlayers.length);
       

        try {
            myDice = new Dice(startAmountDice);
            rmiPointer = (RMI)LocateRegistry.getRegistry(myIP, myPort).lookup("player");
        } catch (RemoteException ex) {
            System.out.println(DiceLiar.ANSI_RED + "!! FATAL ERROR - RMI ERROR NELLA CREAZIONE DI UN GIOCATORE" + DiceLiar.ANSI_RESET);
            Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean makeChoice(Board currentBoard){
        GUIController gC = currentBoard.getgC();
        if(myTurn){ //Tocca a me fare il turno
                Bet betOnTable = currentBoard.getCurrentBet();        
                if(betOnTable != null){
                    
                    if (betOnTable.valueDie == 6 && betOnTable.amountDice == GUIController.sumOf(gC.totalDicePlayer)) {
                        //System.out.println("MASSIMO!!");
                        gC.isBetMax = true;
                    }
                    
                    gC.makeChoice = true;
                    if(gC.doubtClicked){ // HO CLICCATO DUBITO
                        
                        gC.makeChoice = false;
                        gC.doubtClicked = false;
                        
                        if(doubt(currentBoard)){
                            currentBoard.status = Board.INIT_RESET;
                            //Ho dubitato. Avevo ragione. tocca a me iniziare un nuovo turno..
                            
                            currentBoard.winner = this.getMyID();
                            currentBoard.loser = this.getAllPlayers().vectorPlayers[myID].IDPrev;
                            
                            currentBoard.getCurrentPlayers().getVectorPlayers()[currentBoard.loser].getMyDiceObject().removeDie();
                            //currentBoard.broadcastRMI(currentBoard, "CHECK_DOUBT");

                            //myBet = makeBet(currentBoard);

                            currentBoard.diceUpdated = 1;
                            currentBoard.oneJollyEnabled = true;
                            
                            gC.idLastBet = myID;
                            
                            currentBoard.newTurn(currentBoard, this.getMyID(), null); 
                            return true;
                        }
                        else{
                            System.out.println("Non avevo ragione (" + this.getMyID() + "). Inizierà " + this.IDNext);
                            //Ho Dubitato. NON avevo Ragione. Tocca al giocatore dopo di me
                            
                            currentBoard.loser = this.getMyID();
                            currentBoard.winner = this.getAllPlayers().vectorPlayers[myID].IDNext;
                            
                            gC.idLastBet = myID;
                            
//                            currentBoard.getCurrentPlayers().getVectorPlayers()[currentBoard.loser].getMyDiceObject().removeDie();
                            //currentBoard.broadcastRMI(currentBoard, "CHECK_DOUBT");

                            //myDice.removeDie();
                            //gC.totalDicePlayer[myID]--;
                            
                            if(myDice.nDice == 0){
                                currentBoard.getCurrentPlayers().removePlayer(this, false, false);
                                gC.loseGame = true;
                                System.out.println("Ho perso :(");
                            }

                            currentBoard.newTurn(currentBoard, this.IDNext, null);
                            return true;
                        }
                    }
                    else if(gC.makeBetClicked){ // NON DUBITO E RILANCIO
                        
                        gC.makeChoice = false;
                        
                        if(gC.betClicked == false){
                            return false;
                        }   
      
                        gC.makeBetClicked = false;
                        
                        Bet tempBet = currentBoard.getCurrentBet();
                        
                        currentBoard.setCurrentBet(makeBetConditional(currentBoard));
                        
                        if(currentBoard.getCurrentBet() == null){
                            currentBoard.setCurrentBet(tempBet);
                            
                            gC.diceValueSelected = tempBet.valueDie;
                            gC.diceAmountSelected = tempBet.amountDice;
                            gC.betClicked = false;
                            gC.makeBetClicked = true;
                            
                            return false;
                        }
                        
                        //System.out.println("Scommesso!");
                        
                        currentBoard.setnTurn(currentBoard.getnTurn() + 1);
                        gC.setTurn(currentBoard.getnTurn());
                        
                        currentBoard.broadcastRMI(currentBoard, "NOTIFY_MOVE");
                        return true;
                    }
                }
                else{ //Sono il primo giocatore a iniziare il giro
                    //System.out.println("NON CI SONO SCOMMESSE SUL TAVOLO");
                    
                    if(gC.betClicked == false)
                        return false;
                    
                    myBet = makeBet(currentBoard);
                    gC.setBetOnTable(true);
                    currentBoard.setCurrentBet(myBet);
                    currentBoard.setnTurn(currentBoard.getnTurn() + 1);
                    gC.setTurn(currentBoard.getnTurn());
                        
                    currentBoard.broadcastRMI(currentBoard, "NOTIFY_MOVE");
                    return true;
                }      
        }
        
        return false;
    }

    public Bet makeBet(Board currentBoard){
        GUIController gC = currentBoard.getgC();
        boolean checkValue = true;
        int valueDie = 0;
        
        valueDie = gC.diceValueSelected;
        int amountDice = gC.diceAmountSelected;
        
        if (valueDie == 1 && currentBoard.oneJollyEnabled) {
                //Utilizzo 1 come valore e non come jolly. Nessuno può più usare 1 come Jolly
                System.out.println("Il valore 1 non vale più come JOLLY");
                currentBoard.oneJollyEnabled = false;
                currentBoard.gC.oneJollyEnabled = false;
                currentBoard.broadcastRMI(currentBoard, "ONE_IS_ONE");
        }
        
        System.out.println("Scommetto " + amountDice + " dadi di valore " + valueDie);
        gC.idLastBet = myID;
        Bet myNewBet = new Bet(amountDice, valueDie);
        return myNewBet;
        
    }

    public Bet makeBetConditional(Board currentBoard){
        GUIController gC = currentBoard.getgC();
        int amountDice = 0;
        int valueDie = 0;
        boolean checkBet = true;

        Bet currentBet = currentBoard.getCurrentBet();
             
        valueDie = gC.diceValueSelected;
        amountDice = gC.diceAmountSelected;
        
        if(amountDice < currentBet.getAmount()){
                System.out.println("Non puoi rilanciare a ribasso");
                gC.errorRibasso = true;
                return null;
        }
        else if(amountDice == currentBet.getAmount()){
            if(valueDie > currentBet.getValueDie() && valueDie <= 6){
                System.out.println("OK");
                checkBet = false;
            }
            else{
                System.out.println("Non puoi rilanciare uguale o minore");
                gC.errorAmountMinore = true;
                return null;
            }
        }
        else if(amountDice > currentBet.getAmount() && valueDie <= 6){
               System.out.println("OK");
               checkBet = false;
        }

        if(!checkBet && valueDie == 1 && currentBoard.oneJollyEnabled) {
            //Utilizzo 1 come valore e non come jolly. Nessuno può più usare 1 come Jolly
            System.out.println("Il valore 1 non vale più come JOLLY");
            currentBoard.oneJollyEnabled = false;
            currentBoard.gC.oneJollyEnabled = false;
            currentBoard.broadcastRMI(currentBoard, "ONE_IS_ONE");
        }
        
        gC.idLastBet = myID;
        Bet myNewBet = new Bet(amountDice, valueDie);
        return myNewBet;

    }

    public boolean doubt(Board currentBoard){
        System.out.println("Dubito! Non e' vero che sul tavolo ci sono almeno " + currentBoard.getCurrentBet().getAmount() + " dadi con valore " + currentBoard.getCurrentBet().getValueDie());
        boolean result = currentBoard.checkBet();
        if(result){ //Hai dubitato giusto
            System.out.println("Hai dubitato giusto");
            currentBoard.okDoubt = true;
            return true;
        }
        else{
            //System.out.println("OH NO, Ho perso un dado!");
            System.out.println("Non avevi ragione!");
            currentBoard.okDoubt = false;
            return false;
        }
    }
    
        
    public int findNextPlayer(){
            int i = myID;
            
            int countOthers = 1;
            
            int nextCandidate = getAllPlayers().vectorPlayers[(i+countOthers)%getAllPlayers().vectorPlayers.length].myID;
            
            while(true){
                try {
                    if(getAllPlayers().vectorPlayers[nextCandidate].rmiPointer.heartbeat()){   
                        //getAllPlayers().vectorPlayers[i].IDNext = getAllPlayers().vectorPlayers[(i+countOthers)%getAllPlayers().vectorPlayers.length].myID;
                        System.out.println(DiceLiar.ANSI_GREEN + "NEXT di " + i + ": TROVATO! Il player " + nextCandidate + " è raggiungibile" + Board.ANSI_RESET);
                        return getAllPlayers().vectorPlayers[(i+countOthers)%getAllPlayers().vectorPlayers.length].myID;
                    }
                } catch (RemoteException ex) {
                    System.out.println(Board.ANSI_RED + "NEXT di " + i + ": ATTENZIONE! Il player " + nextCandidate + " non è più raggiungibile" + Board.ANSI_RESET);
                    getAllPlayers().removePlayer(getAllPlayers().vectorPlayers[nextCandidate], false, false);
                    countOthers++;
                    if(countOthers == getAllPlayers().vectorPlayers.length){
                        System.out.println(Board.ANSI_RED + "NEXT di " + i + ": ATTENZIONE! Non ci sono più giocatori a parte te" + Board.ANSI_RESET);
                        return -1;
                    }
                    System.out.println(DiceLiar.ANSI_CYAN + "NEXT di " + i + ": Chiedo al player " + (i+countOthers)%getAllPlayers().vectorPlayers.length + " se è vivo" + Board.ANSI_RESET);
                    nextCandidate = getAllPlayers().vectorPlayers[(i+countOthers)%getAllPlayers().vectorPlayers.length].myID;
                }
                
            }  
    }
    
    public int findPrevPlayer() {
        int i = myID;

        int countOthers = 1;
        int prevCandidate = getAllPlayers().vectorPlayers[(((i-countOthers)%getAllPlayers().vectorPlayers.length) + getAllPlayers().vectorPlayers.length)%getAllPlayers().vectorPlayers.length].myID;

        while (true) {
            try {
                if (getAllPlayers().vectorPlayers[prevCandidate].rmiPointer.heartbeat()) {
                    //getAllPlayers().vectorPlayers[i].IDPrev = getAllPlayers().vectorPlayers[(i - countOthers) % getAllPlayers().vectorPlayers.length].myID;
                    System.out.println(DiceLiar.ANSI_GREEN + "PREV di " + i + ": TROVATO! Il player " + prevCandidate + " è raggiungibile" + Board.ANSI_RESET);
                    return getAllPlayers().vectorPlayers[(((i-countOthers)%getAllPlayers().vectorPlayers.length) + getAllPlayers().vectorPlayers.length)%getAllPlayers().vectorPlayers.length].myID;

                }
            } catch (RemoteException ex) {
                System.out.println(Board.ANSI_RED + "PREV di " + i + ": ATTENZIONE! Il player " + prevCandidate + " non è più raggiungibile" + Board.ANSI_RESET);
                countOthers++;
                getAllPlayers().removePlayer(getAllPlayers().vectorPlayers[prevCandidate], false, false);
                if (countOthers == getAllPlayers().vectorPlayers.length) {
                    System.out.println(Board.ANSI_RED + "PREV di " + i + ": ATTENZIONE! Non ci sono più giocatori a parte te" + Board.ANSI_RESET);
                    return -1;
                }
                System.out.println(DiceLiar.ANSI_CYAN + "PREV di " + i + ": Chiedo al player " + (((i-countOthers)%getAllPlayers().vectorPlayers.length) + getAllPlayers().vectorPlayers.length)%getAllPlayers().vectorPlayers.length + " se è vivo" + Board.ANSI_RESET);
                prevCandidate = getAllPlayers().vectorPlayers[(((i-countOthers)%getAllPlayers().vectorPlayers.length) + getAllPlayers().vectorPlayers.length)%getAllPlayers().vectorPlayers.length].myID;
            }
        }
    }

    public void resetDice(){
        myDice.resetDice();
    }

    public String getMyIP() {
        return myIP;
    }

    public void setMyIP(String myIP) {
        this.myIP = myIP;
    }

    public int getMyPort() {
        return myPort;
    }

    public void setMyPort(int myPort) {
        this.myPort = myPort;
    }   

    public boolean getMyTurn() {
        return myTurn;
    }

    public void setTurn(boolean _myTurn){
        this.myTurn = _myTurn;
    }

    public Bet getMyBet() {
        return myBet;
    }

    public void setMyBet(Bet _myBet) {
        this.myBet = _myBet;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Players getAllPlayers() {
        return allPlayers;
    }

    public void setAllPlayers(Players _allPlayers) {
        this.allPlayers = _allPlayers;
    }
    

    public Die[] getMyDice() {
        return myDice.getVectorDice();
    }

    public Dice getMyDiceObject(){
        return myDice;
    }

    public int[] getmyDiceValue(){
        return myDice.getDiceValues();
    }

    public int[] getmyDiceValueGrouped(Board currentBoard){
        return myDice.getDiceValuesGrouped(currentBoard);
    }

    public void setMyDice(Dice _myDice) {
        this.myDice = _myDice;
    }

    public int getMyID() {
        return myID;
    }

    public void setMyID(int myID) {
        this.myID = myID;
    }

    public RMI getRmiPointer() {
        return rmiPointer;
    }

    public void setRmiPointer(RMI rmiPointer) {
        this.rmiPointer = rmiPointer;
    }

}
