/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diceliar;

import java.util.Scanner;
import java.util.UUID;

/**
 *
 * @author proietfb
 */
public class Player {
    String name;
    UUID id;
    int myID;
    boolean myTurn;
    Bet myBet;
    Players allPlayers;
    Dice myDice;
    
    public Player(Players _allPlayers, int _myID){
        System.out.println("Creo player");
        myID = _myID;
        allPlayers = _allPlayers;
        id = UUID.randomUUID();
        myTurn = false;
        
        myDice = new Dice(5);
    }
    
    public Player(String _name, UUID _id, boolean _myTurn, Bet _myBet){
        name = _name;
        id = _id;
        myTurn = _myTurn;
        myBet = _myBet;
    }
    
    public void makeChoice(){
        Scanner reader = new Scanner(System.in);  // Reading from System.in
        Board currentBoard = getAllPlayers().getCurrentBoard();
        if(myTurn){ //Tocca a me fare il turno
                Bet betOnTable = currentBoard.getCurrentBet();
                if(betOnTable != null){
                        System.out.println("C'è già una scommessa: ci sono " + currentBoard.getCurrentBet().getAmount() + " dadi con valore " + currentBoard.getCurrentBet().getValueDie());
                        System.out.println("(0) Dubiti\n(1) Non dubiti e fai una nuova scommessa");
                        loop: while(reader.hasNextInt()){
                                int choice = reader.nextInt();
                                switch(choice){
                                        case 0: if(doubt()){
                                            //Ho dubitato. Avevo ragione. tocca a me iniziare un nuovo turno..
                                            System.out.println("Pota");
                                            myBet = makeBet();
                                            currentBoard.newTurn(currentBoard, this.getMyID(), myBet);                                         
                                        }
                                        else{
                                            currentBoard.newTurn(currentBoard, (this.getMyID() + 1) % currentBoard.getnPlayers(), null);
                                        }
                                        break loop;
                                        case 1: currentBoard.setCurrentBet(makeBetConditional());  break loop;
                                        default: System.out.println("Valore non ammesso"); System.out.println("(0) Dubiti\n(1) Non dubiti e fai una nuova scommessa");
                                }
                                }
                }
                else{ //Sono il primo giocatore a iniziare il giro
                    myBet = makeBet();
                    currentBoard.setCurrentBet(myBet);                 
                }
        }
    }
 
    
    public Bet makeBet(){
        boolean checkValue = true;
        int valueDie = 0;
        Scanner reader = new Scanner(System.in);  // Reading from System.in
        System.out.println("Inserisci il numero di dadi della scommessa: ");
        int amountDice = reader.nextInt();
        
        while(checkValue){
            System.out.println("Inserisci il valore dei dadi della scommessa: ");
            valueDie = reader.nextInt(); // Scans the next token of the input as an int.

              if(valueDie <= 0 || valueDie > 6){
                    System.out.println("Valore del dado non valido");

                }
              else
                  checkValue = false;
        }
        
        Bet myNewBet = new Bet(amountDice, valueDie);
        
        return myNewBet;
    }
    
    public Bet makeBetConditional(){
        int amountDice = 0;
        int valueDie = 0;
        boolean checkBet = true;
        
        Bet currentBet = getAllPlayers().getCurrentBoard().getCurrentBet();     
        Scanner reader = new Scanner(System.in);  
        
        while(checkBet){
            System.out.println("Inserisci il numero di dadi della scommessa: ");
            amountDice = reader.nextInt();

            System.out.println("Inserisci il valore dei dadi della scommessa: ");
            valueDie = reader.nextInt();
            
            if(valueDie <= 0 || valueDie > 6){
                System.out.println("Valore del dado non valido");
                continue;
            }
                
            
            if(amountDice < currentBet.getAmount()){
                System.out.println("Non puoi rilanciare a ribasso");
            }
            else if(amountDice == currentBet.getAmount()){
                if(valueDie > currentBet.getValueDie() && valueDie <= 6){
                    System.out.println("OK");
                    checkBet = false;
                }  
                else
                    System.out.println("Non puoi rilanciare uguale o minore");
            }
            else if(amountDice > currentBet.getAmount() && valueDie <= 6){
                   System.out.println("OK");
                   checkBet = false;
            }
        }
        
        Bet myNewBet = new Bet(amountDice, valueDie); 
        return myNewBet;
    }
    
    public boolean doubt(){
        Board currentBoard = allPlayers.getCurrentBoard();     
        System.out.println("Dubito! Non è vero che sul tavolo ci sono almeno " + currentBoard.getCurrentBet().getAmount() + " dadi con valore " + currentBoard.getCurrentBet().getValueDie());
        boolean result = currentBoard.checkBet();
        if(result){ //Hai dubitato giusto
            System.out.println("Hai dubitato giusto");
            return true;
        }
        else{
            System.out.println("Non avevi ragione!");
            return false; 
        }
    }
    
    public void resetDice(){
        Dice myDice = this.getMyDiceObject();
        myDice.resetDice();
    }
    
    public UUID getId() {
        return id;
    }

    public void setId(UUID _id) {
        this.id = _id;
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
    
    public int[] getmyDiceValueGrouped(){
        return myDice.getDiceValuesGrouped();
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
    
    
    
    
    
    
    
    
}
