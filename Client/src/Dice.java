/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.Serializable;

/**
 *
 * @author proietfb
 */
public class Dice implements Serializable{
    int nDice; //Numero di dadi validi
    Die[] vectorDice;

    public Dice(int _nDice){
        //costruttore
        nDice = _nDice;

        vectorDice = new Die[_nDice];
        for (int i = 0; i < _nDice; i += 1) {
            vectorDice[i] = new Die();
        }
    }

    public void resetDice(){
        for (int i = 0; i < nDice; i += 1) {
            vectorDice[i].resetValue();
        }
    }

    void addDie(){}

    void removeDie(){
        int arrayLength = vectorDice.length;
        for (int i = arrayLength; i > 0; i -= 1) {
            if(vectorDice[i].getValue() != 0){
                vectorDice[i].setValue(0);
                break;
            }
        }
    }

    public int[] getDiceValues(){
        int[] diceValue = new int[5];
        for(int i=0; i< nDice; i+=1)
            diceValue[i] = vectorDice[i].getValue();
        return diceValue;
    }

    public int[] getDiceValuesGrouped(){
         int[] diceValue = new int[6];
         for(int i=0; i<5; i+=1)
             diceValue[vectorDice[i].getValue()-1] = diceValue[vectorDice[i].getValue()-1] + 1;

        return diceValue;
    }

    public int getnDice() {
        return nDice;
    }

    public void setnDice(int _nDice) {
        this.nDice = _nDice;
    }

    public Die[] getVectorDice() {
        return vectorDice;
    }

    public void setVectorDice(Die[] _vectorDice) {
        this.vectorDice = _vectorDice;
    }


}
