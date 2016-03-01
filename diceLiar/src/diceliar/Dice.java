/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diceliar;

/**
 *
 * @author proietfb
 */
public class Dice {
    int nDice;
    int[] vectorDice;
    
    public Dice(int _nDice, int[] _vectorDice){
        //costruttore
        nDice = _nDice;
        vectorDice = _vectorDice;
    }
    
    void initDice(){
        
    }
    void addDie(){
        
    }
    void removeDie(){
        
    }

    public int getnDice() {
        return nDice;
    }

    public void setnDice(int _nDice) {
        this.nDice = _nDice;
    }

    public int[] getVectorDice() {
        return vectorDice;
    }

    public void setVectorDice(int[] _vectorDice) {
        this.vectorDice = _vectorDice;
    }
    

}
