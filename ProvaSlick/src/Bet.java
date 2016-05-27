import java.io.Serializable;


public class Bet implements Serializable{
    private int amountDice; //Numero dei dadi
    private int valueDie; //Valore dei dadi

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
