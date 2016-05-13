

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

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
    
    boolean playerOut = false;
    
    int IDPrev;
    int IDNext; 
    
    boolean myTurn;
    Bet myBet;
    Players allPlayers;
    Dice myDice;

    public Player(Players _allPlayers, int _myID, String _myIP, int _myPort) throws RemoteException, NotBoundException{
        System.out.println("Creo player con ID " + _myID + ", IP: " + _myIP + ":" + _myPort);
        myID = _myID;
        myIP = _myIP;
        myPort = _myPort;
        allPlayers = _allPlayers;
        myTurn = false;
        //myDice = new Dice(5);
        rmiPointer = (RMI)LocateRegistry.getRegistry(myIP, myPort).lookup("player");
    }

    public boolean makeChoice(Board currentBoard) throws RemoteException{
        GUIController gC = currentBoard.getgC();
        if(myTurn){ //Tocca a me fare il turno
                Bet betOnTable = currentBoard.getCurrentBet();        
                if(betOnTable != null){
                    gC.makeChoice = true;
                    if(gC.doubtClicked){ // HO CLICCATO DUBITO
                        
                        gC.makeChoice = false;
                        gC.doubtClicked = false;
                        
                        if(doubt(currentBoard)){
                            currentBoard.status = Board.INIT_RESET;
                            //Ho dubitato. Avevo ragione. tocca a me iniziare un nuovo turno..
                            currentBoard.broadcastRMI(currentBoard, "NOTIFY_WINLOSE");

                            //myBet = makeBet(currentBoard);

                            currentBoard.diceUpdated = 1;
                            currentBoard.oneJollyEnabled = true;
                            
                            currentBoard.newTurn(currentBoard, this.getMyID(), null); 
                            return true;
                        }
                        else{
                            System.out.println("Non avevo ragione (" + this.getMyID() + "). Inizierà " + (this.getMyID() + 1) % currentBoard.getnPlayers());
                            //Ho Dubitato. NON avevo Ragione. Tocca al giocatore dopo di me
                            currentBoard.broadcastRMI(currentBoard, "NOTIFY_WINLOSE");

                            myDice.removeDie();
                            //gC.totalDicePlayer[myID]--;
                            
                            if(myDice.vectorDice.length == 0){
                                currentBoard.getCurrentPlayers().removePlayer(this);
                                System.out.println("Ho perso :(");
                            }

                            currentBoard.newTurn(currentBoard, (this.getMyID() + 1) % currentBoard.getnPlayers(), null);
                            return true;
                        }
                    }
                    else if(gC.makeBetClicked){ // NON DUBITO E RILANCIO
                        
                        gC.makeChoice = false;
                        
                        if(gC.betClicked == false){
                            return false;
                        }
                        
                        System.out.println(DiceLiar.ANSI_RED + "fatta!" + DiceLiar.ANSI_RESET);
                            
      
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
                        
                        System.out.println("Scommesso!");
                        
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
                    currentBoard.broadcastRMI(currentBoard, "NOTIFY_MOVE");
                    return true;
                }      
        }
        
        return false;
    }
//                    
//                    System.out.println("C'e gia una scommessa: ci sono " + currentBoard.getCurrentBet().getAmount() + " dadi con valore " + currentBoard.getCurrentBet().getValueDie());
//                        System.out.println("(0) Dubiti\n(1) Non dubiti e fai una nuova scommessa");
//                        Scanner reader = new Scanner(System.in);  // Reading from System.in
//                        loop: while(reader.hasNextInt()){
//                                int choice = reader.nextInt();
//                                switch(choice){
//                                        case 0: //DUBITO
//                                            if(doubt(currentBoard)){
//                                                currentBoard.status = Board.RESET;
//                                                //Ho dubitato. Avevo ragione. tocca a me iniziare un nuovo turno..
//                                                currentBoard.broadcastRMI(currentBoard, "NOTIFY_WINLOSE");
//                                                
//                                                //myBet = makeBet(currentBoard);
//
//                                                currentBoard.diceUpdated = 1;
//                                                currentBoard.oneJollyEnabled = true;
//                                                currentBoard.newTurn(currentBoard, this.getMyID(), myBet);  
//                                            }
//                                            else{
//                                                System.out.println("Non avevo ragione (" + this.getMyID() + "). Inizierà " + (this.getMyID() + 1) % currentBoard.getnPlayers());
//                                                //Ho Dubitato. NON avevo Ragione. Tocca al giocatore dopo di me
//                                                currentBoard.broadcastRMI(currentBoard, "NOTIFY_WINLOSE");
//                                                
//                                                myDice.removeDie();
//                                                 
//                                                currentBoard.newTurn(currentBoard, (this.getMyID() + 1) % currentBoard.getnPlayers(), null);
//                                            }
//                                            reader.close();
//                                            break loop;
//                                        case 1: //NON DUBITO
//                                            currentBoard.setCurrentBet(makeBetConditional(currentBoard));
//                                            currentBoard.broadcastRMI(currentBoard, "NOTIFY_MOVE");
//                                            reader.close();
//                                            break loop;
//                                        default: System.out.println("Valore non ammesso"); System.out.println("(0) Dubiti\n(1) Non dubiti e fai una nuova scommessa");
//                                }
//                                }
//                }
//                else{ //Sono il primo giocatore a iniziare il giro
//                    System.out.println("NON CI SONO SCOMMESSE SUL TAVOLO");
//                    myBet = makeBet(currentBoard);
//                    currentBoard.setCurrentBet(myBet);
//                    currentBoard.broadcastRMI(currentBoard, "NOTIFY_MOVE");
//                }
//        }

    public Bet makeBet(Board currentBoard) throws RemoteException{
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
        
//        Scanner reader = new Scanner(System.in);  // Reading from System.in
//        System.out.println("Inserisci il numero di dadi della scommessa: ");
//        int amountDice = reader.nextInt();
//
//        while(checkValue){
//            System.out.println("Inserisci il valore dei dadi della scommessa: ");
//            valueDie = reader.nextInt(); // Scans the next token of the input as an int.
//
//              if(valueDie <= 0 || valueDie > 6){
//                    System.out.println("Valore del dado non valido");
//
//                }
//              else{
//                  System.out.println("OK");
//                  checkValue = false;
//              }
//              
//              if (!checkValue && valueDie == 1 && currentBoard.oneJollyEnabled) {
//                //Utilizzo 1 come valore e non come jolly. Nessuno può più usare 1 come Jolly
//                System.out.println("Il valore 1 non vale più come JOLLY");
//                currentBoard.oneJollyEnabled = false;
//                currentBoard.broadcastRMI(currentBoard, "ONE_IS_ONE");
//            }
//        }
//
//        Bet myNewBet = new Bet(amountDice, valueDie);
//        reader.close();
//        return myNewBet;
    }

    public Bet makeBetConditional(Board currentBoard) throws RemoteException{
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
//        
//        
//        Scanner reader = new Scanner(System.in);
//
//        while(checkBet){
//            System.out.println("Inserisci il numero di dadi della scommessa: ");
//            amountDice = reader.nextInt();
//
//            System.out.println("Inserisci il valore dei dadi della scommessa: ");
//            valueDie = reader.nextInt();
//
//            if(valueDie <= 0 || valueDie > 6){
//                System.out.println("Valore del dado non valido");
//                continue;
//            }
//
//
//            if(amountDice < currentBet.getAmount()){
//                System.out.println("Non puoi rilanciare a ribasso");
//            }
//            else if(amountDice == currentBet.getAmount()){
//                if(valueDie > currentBet.getValueDie() && valueDie <= 6){
//                    System.out.println("OK");
//                    checkBet = false;
//                }
//                else
//                    System.out.println("Non puoi rilanciare uguale o minore");
//            }
//            else if(amountDice > currentBet.getAmount() && valueDie <= 6){
//                   System.out.println("OK");
//                   checkBet = false;
//            }
//            
//            if(!checkBet && valueDie == 1 && currentBoard.oneJollyEnabled) {
//                //Utilizzo 1 come valore e non come jolly. Nessuno può più usare 1 come Jolly
//                System.out.println("Il valore 1 non vale più come JOLLY");
//                currentBoard.oneJollyEnabled = false;
//                currentBoard.broadcastRMI(currentBoard, "ONE_IS_ONE");
//            }
//        }
//
//        Bet myNewBet = new Bet(amountDice, valueDie);
//        return myNewBet;
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
            System.out.println("OH NO, Ho perso un dado!");
            System.out.println("Non avevi ragione!");
            currentBoard.okDoubt = false;
            return false;
        }
    }
    
        
    public int findNextPlayer(){
        //for (int i = 0; i < vectorPlayers.length; i += 1) {  
            int i = myID;
            
            int countOthers = 1;
            
            int nextCandidate = getAllPlayers().vectorPlayers[(i+countOthers)%getAllPlayers().vectorPlayers.length].myID;
            
            while(true){
                try {
                    if(getAllPlayers().vectorPlayers[nextCandidate].rmiPointer.heartbeat()){   
                        //getAllPlayers().vectorPlayers[i].IDNext = getAllPlayers().vectorPlayers[(i+countOthers)%getAllPlayers().vectorPlayers.length].myID;
                        System.out.println(DiceLiar.ANSI_GREEN + "NEXT: " + i + ": TROVATO! Il player " + nextCandidate + " è raggiungibile" + Board.ANSI_RESET);
                        return getAllPlayers().vectorPlayers[(i+countOthers)%getAllPlayers().vectorPlayers.length].myID;
                    }
                } catch (RemoteException ex) {
                    System.out.println(Board.ANSI_RED + "NEXT: " + i + ": ATTENZIONE! Il player " + nextCandidate + " non è più raggiungibile" + Board.ANSI_RESET);
                    countOthers++;
                    if(countOthers == getAllPlayers().vectorPlayers.length){
                        System.out.println(Board.ANSI_RED + "NEXT: " + i + ": ATTENZIONE! Non ci sono più giocatori a parte te" + Board.ANSI_RESET);
                        return -1;
                    }
                    System.out.println(DiceLiar.ANSI_CYAN + "NEXT: " + i + ": Chiedo al player " + (i+countOthers)%getAllPlayers().vectorPlayers.length + " se è vivo" + Board.ANSI_RESET);
                    nextCandidate = getAllPlayers().vectorPlayers[(i+countOthers)%getAllPlayers().vectorPlayers.length].myID;
                }
                
            }  
            //vectorPlayers[i].IDNext= vectorPlayers[(i-1)%vectorPlayers.length].myID;
        //}
    }
    
    public int findPrevPlayer() {
        int i = myID;

        int countOthers = 1;
        int prevCandidate = getAllPlayers().vectorPlayers[(((i-countOthers)%getAllPlayers().vectorPlayers.length) + getAllPlayers().vectorPlayers.length)%getAllPlayers().vectorPlayers.length].myID;

        while (true) {
            try {
                if (getAllPlayers().vectorPlayers[prevCandidate].rmiPointer.heartbeat()) {
                    //getAllPlayers().vectorPlayers[i].IDPrev = getAllPlayers().vectorPlayers[(i - countOthers) % getAllPlayers().vectorPlayers.length].myID;
                    System.out.println(DiceLiar.ANSI_GREEN + "PREV: " + i + ": TROVATO! Il player " + prevCandidate + " è raggiungibile" + Board.ANSI_RESET);
                    return getAllPlayers().vectorPlayers[(((i-countOthers)%getAllPlayers().vectorPlayers.length) + getAllPlayers().vectorPlayers.length)%getAllPlayers().vectorPlayers.length].myID;

                }
            } catch (RemoteException ex) {
                System.out.println(Board.ANSI_RED + "PREV: " + i + ": ATTENZIONE! Il player " + prevCandidate + " non è più raggiungibile" + Board.ANSI_RESET);
                countOthers++;
                if (countOthers == getAllPlayers().vectorPlayers.length) {
                    System.out.println(Board.ANSI_RED + "PREV: " + i + ": ATTENZIONE! Non ci sono più giocatori a parte te" + Board.ANSI_RESET);
                    return -1;
                }
                System.out.println(DiceLiar.ANSI_CYAN + "PREV: " + i + ": Chiedo al player " + (((i-countOthers)%getAllPlayers().vectorPlayers.length) + getAllPlayers().vectorPlayers.length)%getAllPlayers().vectorPlayers.length + " se è vivo" + Board.ANSI_RESET);
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
