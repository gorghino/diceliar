import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Player implements Serializable{
    private int myID;
    private String myIP;
    private int myPort;

    public RMI rmiPointer; 
    public RMI rmiNextPlayer;
    public int IDPrev;
    public int IDNext; 

    private boolean myTurn;
    private boolean playerOut = false;
    private Bet myBet;
    private Players allPlayers;
    private Dice myDice;

    public Player(Players _allPlayers, int _myID, String _myIP, int _myPort, int startAmountDice) throws NotBoundException{
        System.out.println("Creo player con ID " + _myID + ", IP: " + _myIP + ":" + _myPort);
        myID = _myID;
        myIP = _myIP;
        myPort = _myPort;
        allPlayers = _allPlayers;
        myTurn = false;
        
        IDNext = ((((myID + 1) % allPlayers.getVectorPlayers().length) + allPlayers.getVectorPlayers().length) % allPlayers.getVectorPlayers().length);
        IDPrev = ((((myID - 1) % allPlayers.getVectorPlayers().length) + allPlayers.getVectorPlayers().length) % allPlayers.getVectorPlayers().length);

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
                    
                    if(betOnTable.getValueDie() == 6 && betOnTable.getAmount() == GUIController.sumOf(gC.totalDicePlayer)) 
                        gC.isBetMax = true;
                    
                    gC.makeChoice = true;
                    
                    if (getAllPlayers().getPlayersAlive() == 1) {
                        //System.out.println(DiceLiar.ANSI_GREEN + "Sei rimasto solo tu. HAI VINTO!" + DiceLiar.ANSI_RESET);
                        gC.setWinGame(true);
                        gC.setShowDice(false);
                        gC.setRestartBoard(false);
                        gC.setInitBoard(false);
                        gC.playDiceAnimation = false;
                        return false;
                    }
                    
                    if(gC.doubtClicked){ // HO CLICCATO DUBITO 
                        gC.makeChoice = false;
                        gC.doubtClicked = false;
                        
                        if(doubt(currentBoard)){
                            currentBoard.setStatus(Board.INIT_RESET);
                            //Ho dubitato. Avevo ragione. tocca a me iniziare un nuovo turno..
                            
                            currentBoard.setWinner(myID);
                            currentBoard.setLoser(IDPrev);
                            
                            currentBoard.getCurrentPlayers().getVectorPlayers()[currentBoard.getLoser()].getMyDiceObject().removeDie();
                            currentBoard.setOneJollyEnabled(true); 
                            currentBoard.setIdLastBet(myID);
                            currentBoard.newTurn(currentBoard, this.getMyID(), null); 
                            return true;
                        }
                        else{
                            //System.out.println("Non avevo ragione (" + this.getMyID() + "). Inizierà " + this.IDNext);
                            //Ho Dubitato. NON avevo Ragione. Tocca al giocatore dopo di me
                            
                            currentBoard.setLoser(myID);
                            currentBoard.setWinner(IDNext);
                            
                            currentBoard.setIdLastBet(myID);
                            currentBoard.getCurrentPlayers().getVectorPlayers()[currentBoard.getLoser()].getMyDiceObject().removeDie();

                            if(myDice.getnDice() == 0){
                                currentBoard.getCurrentPlayers().removePlayer(this, false, false);
                                gC.setLoseGame(true);
                                gC.setRestartBoard(false);
                                //System.out.println("Ho perso :(");
                            }
                            
                            currentBoard.newTurn(currentBoard, this.IDNext, null);
                            return true;
                        }
                    }
                    else if(gC.makeBetClicked){ // NON DUBITO E RILANCIO
                        
                        gC.makeChoice = false;
                        
                        if(gC.betClicked == false) return false; 
      
                        gC.makeBetClicked = false;
                        
                        Bet tempBet = currentBoard.getCurrentBet();
                        currentBoard.setCurrentBet(makeBetConditional(currentBoard));
                        
                        if(currentBoard.getCurrentBet() == null){
                            currentBoard.setCurrentBet(tempBet);
                            gC.betClicked = false;
                            gC.makeBetClicked = true;                           
                            return false;
                        }
                        
                        currentBoard.setnTurn(currentBoard.getnTurn() + 1);
                        gC.setTurn(currentBoard.getnTurn());
                        currentBoard.broadcastRMI(currentBoard, "NOTIFY_MOVE");
                        return true;
                    }
                }
                else{ //Sono il primo giocatore a iniziare il giro
                    
                    if(gC.betClicked == false) return false;
                    
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
        int valueDie = 0;
        
        valueDie = gC.getDiceValueSelected();
        int amountDice = gC.getDiceAmountSelected();
        
        if (valueDie == 1 && currentBoard.isOneJollyEnabled()) {
            //Utilizzo 1 come valore e non come jolly. Nessuno può più usare 1 come Jolly
            currentBoard.setOneJollyEnabled(false);
            currentBoard.broadcastRMI(currentBoard, "ONE_IS_ONE");
        }
        
        currentBoard.setIdLastBet(myID);
        Bet myNewBet = new Bet(amountDice, valueDie);
        return myNewBet;
        
    }

    public Bet makeBetConditional(Board currentBoard){
        GUIController gC = currentBoard.getgC();
        int amountDice = 0;
        int valueDie = 0;
        boolean checkBet = true;

        Bet currentBet = currentBoard.getCurrentBet();    
        valueDie = gC.getDiceValueSelected();
        amountDice = gC.getDiceAmountSelected();
        
        if(amountDice < currentBet.getAmount()){
            gC.setErrorRibasso(true);
            return null;
        }
        else if(amountDice == currentBet.getAmount()){
            if(valueDie > currentBet.getValueDie() && valueDie <= 6){
                //System.out.println("OK");
                checkBet = false;
            }
            else{
                gC.setErrorAmountMinore(true);
                return null;
            }
        }
        else if(amountDice > currentBet.getAmount() && valueDie <= 6){
               checkBet = false;
        }

        if(!checkBet && valueDie == 1 && currentBoard.isOneJollyEnabled()) {
            //Utilizzo 1 come valore e non come jolly. Nessuno può più usare 1 come Jolly
            currentBoard.setOneJollyEnabled(false);
            currentBoard.broadcastRMI(currentBoard, "ONE_IS_ONE");
        }
        
        currentBoard.setIdLastBet(myID);
        Bet myNewBet = new Bet(amountDice, valueDie);
        return myNewBet;
    }

    public boolean doubt(Board currentBoard){
        //System.out.println("Dubito! Non e' vero che sul tavolo ci sono almeno " + currentBoard.getCurrentBet().getAmount() + " dadi con valore " + currentBoard.getCurrentBet().getValueDie());
        boolean result = currentBoard.checkBet();
        if(result){ //Hai dubitato giusto
            //System.out.println("Hai dubitato giusto");
            currentBoard.setOkDoubt(true);
            return true;
        }
        else{
            //System.out.println("Non avevi ragione!");
            currentBoard.setOkDoubt(false);
            return false;
        }
    }
         
    public int findNextPlayer(){
            int i = myID;          
            int countOthers = 1;
            int nextCandidate = getAllPlayers().getVectorPlayers()[(i+countOthers)%getAllPlayers().getVectorPlayers().length].myID;
            
            while(true){
                try {
                    if(getAllPlayers().getVectorPlayers()[nextCandidate].rmiPointer.heartbeat()){   
                        System.out.println(DiceLiar.ANSI_GREEN + "NEXT di " + i + ": TROVATO! Il player " + nextCandidate + " è raggiungibile" + DiceLiar.ANSI_RESET);
                        return getAllPlayers().getVectorPlayers()[(i+countOthers)%getAllPlayers().getVectorPlayers().length].myID;
                    }
                } catch (RemoteException ex) {
                    System.out.println(DiceLiar.ANSI_RED + "NEXT di " + i + ": ATTENZIONE! Il player " + nextCandidate + " non è più raggiungibile" + DiceLiar.ANSI_RESET);
                    getAllPlayers().removePlayer(getAllPlayers().getVectorPlayers()[nextCandidate], false, false);
                    countOthers++;
                    if(countOthers == getAllPlayers().getVectorPlayers().length){
                        System.out.println(DiceLiar.ANSI_RED + "NEXT di " + i + ": ATTENZIONE! Non ci sono più giocatori a parte te" + DiceLiar.ANSI_RESET);
                        return -1;
                    }
                    System.out.println(DiceLiar.ANSI_CYAN + "NEXT di " + i + ": Chiedo al player " + (i+countOthers)%getAllPlayers().getVectorPlayers().length + " se è vivo" + DiceLiar.ANSI_RESET);
                    nextCandidate = getAllPlayers().getVectorPlayers()[(i+countOthers)%getAllPlayers().getVectorPlayers().length].myID;
                }        
            }  
    }
    
    public int findPrevPlayer() {
        int i = myID;
        int countOthers = 1;
        int prevCandidate = getAllPlayers().getVectorPlayers()[(((i-countOthers)%getAllPlayers().getVectorPlayers().length) + getAllPlayers().getVectorPlayers().length)%getAllPlayers().getVectorPlayers().length].myID;

        while (true) {
            try {
                if (getAllPlayers().getVectorPlayers()[prevCandidate].rmiPointer.heartbeat()) {
                    System.out.println(DiceLiar.ANSI_GREEN + "PREV di " + i + ": TROVATO! Il player " + prevCandidate + " è raggiungibile" + DiceLiar.ANSI_RESET);
                    return getAllPlayers().getVectorPlayers()[(((i-countOthers)%getAllPlayers().getVectorPlayers().length) + getAllPlayers().getVectorPlayers().length)%getAllPlayers().getVectorPlayers().length].myID;
                }
            } catch (RemoteException ex) {
                System.out.println(DiceLiar.ANSI_RED + "PREV di " + i + ": ATTENZIONE! Il player " + prevCandidate + " non è più raggiungibile" + DiceLiar.ANSI_RESET);
                countOthers++;
                getAllPlayers().removePlayer(getAllPlayers().getVectorPlayers()[prevCandidate], false, false);
                if (countOthers == getAllPlayers().getVectorPlayers().length) {
                    System.out.println(DiceLiar.ANSI_RED + "PREV di " + i + ": ATTENZIONE! Non ci sono più giocatori a parte te" + DiceLiar.ANSI_RESET);
                    return -1;
                }
                System.out.println(DiceLiar.ANSI_CYAN + "PREV di " + i + ": Chiedo al player " + (((i-countOthers)%getAllPlayers().getVectorPlayers().length) + getAllPlayers().getVectorPlayers().length)%getAllPlayers().getVectorPlayers().length + " se è vivo" + DiceLiar.ANSI_RESET);
                prevCandidate = getAllPlayers().getVectorPlayers()[(((i-countOthers)%getAllPlayers().getVectorPlayers().length) + getAllPlayers().getVectorPlayers().length)%getAllPlayers().getVectorPlayers().length].myID;
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

    public boolean isPlayerOut() {
        return playerOut;
    }

    public void setPlayerOut(boolean playerOut) {
        this.playerOut = playerOut;
    }

    public boolean isMyTurn() {
        return myTurn;
    }

    public void setMyTurn(boolean myTurn) {
        this.myTurn = myTurn;
    }
}
