/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diceliar;

import java.util.Arrays;
import java.util.UUID;

/**
 *
 * @author proietfb
 */
public class Player {
    String name;
    UUID id;
    int myTurn;
    int myBet;
    
    public Player(){
        System.out.println("Creo player");
        id = UUID.randomUUID();
        myTurn = 0;
        
        Dice myDice = new Dice(5);
    }
    
    public Player(String _name, UUID _id, int _myTurn, int _myBet){
        name = _name;
        id = _id;
        myTurn = _myTurn;
        myBet = _myBet;
    }
    
    public void makeChoice(){}
    public void makeBet(){}
    public void doubt(){}
    public boolean isMyTurn(){ 
        return true; 
    }
    
    public UUID getId() {
        return id;
    }

    public void setId(UUID _id) {
        this.id = _id;
    }

    public int getMyTurn() {
        return myTurn;
    }

    public void setMyTurn(int _myTurn) {
        this.myTurn = _myTurn;
    }

    public int getMyBet() {
        return myBet;
    }

    public void setMyBet(int _myBet) {
        this.myBet = _myBet;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    
}
