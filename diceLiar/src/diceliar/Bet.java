/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diceliar;

/**
 *
 * @author gorgo
 */
public class Bet {
    int amountDice; //Numero dei dadi
    int valueDie; //Valore dei dadi

    public Bet(int _amount, int _valueDie) {
        if (_valueDie >= 1 && _valueDie <= 6) {
            this.valueDie = _valueDie;
        } else {
            throw new IllegalArgumentException("Value out of range");
        }
        this.amountDice = _amount;     
    }

    public int getAmount() {
        return amountDice;
    }

    public void setAmount(int amount) {
        this.amountDice = amount;
    }

    public int getValueDie() {
        return valueDie;
    }

    public void setValueDie(int valueDie) {
        this.valueDie = valueDie;
    }
    
    public int[] getBet(){
        int[] bet = {this.amountDice, this.valueDie}; 
        return bet;
    }
    
}
