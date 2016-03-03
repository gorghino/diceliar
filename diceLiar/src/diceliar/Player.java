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
    boolean myTurn;
    Bet myBet;
    Players allPlayers;
    Dice myDice;
    
    public Player(Players _allPlayers){
        System.out.println("Creo player");
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
                                case 0: doubt(); break loop;
                                case 1: makeBetConditional(); break loop;
                                default: System.out.println("Valore non ammesso"); System.out.println("(0) Dubiti\n(1) Non dubiti e fai una nuova scommessa");
                        }
                    }
            }
            else{ //Sono il primo giocatore a iniziare il giro
                myBet = makeBet();
                currentBoard.setCurrentBet(myBet);
                currentBoard.setnTurn(currentBoard.getnTurn() + 1);
            }
        }
    }
 
    
    public Bet makeBet(){
        Scanner reader = new Scanner(System.in);  // Reading from System.in
        System.out.println("Inserisci il numero di dadi della scommessa: ");
        int amountDice = reader.nextInt();
        
        System.out.println("Inserisci il valore dei dadi della scommessa: ");
        int valueDie = reader.nextInt(); // Scans the next token of the input as an int.
        
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
            
            if(amountDice <= currentBet.getAmount() && valueDie <= currentBet.getValueDie()){
                System.out.println("Non puoi rilanciare a ribasso o uguale");
            }
            else if(amountDice > currentBet.getAmount() || (amountDice == currentBet.getAmount() && valueDie > currentBet.getValueDie())){
                System.out.println("OK");
                checkBet = false;
            }
                
        }
        
        Bet myNewBet = new Bet(amountDice, valueDie); 
        return myNewBet;
    }
    
    public void doubt(){
        Board currentBoard = allPlayers.getCurrentBoard();     
        System.out.println("Dubito! Non è vero che sul tavolo ci sono almeno " + currentBoard.getCurrentBet().getAmount() + " dadi con valore " + currentBoard.getCurrentBet().getValueDie());
        boolean result = currentBoard.checkBet();
        if(result){ //Hai dubitato giusto
            System.out.println("Hai dubitato giusto");
            //TODO: Chi ha bluffato perde un dado e tocca a me
        }
        else{
            System.out.println("Non avevi ragione!");
            //TODO: Perdo un dado e tocca a quello dopo di me  
        }
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
    
    public int[] getmyDiceValue(){
        return myDice.getDiceValues();
    }
    
    public int[] getmyDiceValueGrouped(){
        return myDice.getDiceValuesGrouped();
    }

    public void setMyDice(Dice _myDice) {
        this.myDice = _myDice;
    }
    
    
    
    
    
    
}
