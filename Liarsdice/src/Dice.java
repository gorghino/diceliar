import java.io.Serializable;

public class Dice implements Serializable{
    private int nDice; //Numero di dadi validi
    private Die[] vectorDice;
   

    public Dice(int _nDice){
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
    
    public void deleteDice() {
        for (int i = 0; i < nDice; i += 1) {
            vectorDice[i].value = 0;    
        }
    }

    void removeDie(){
        int arrayLength = vectorDice.length-1;
        for (int i = arrayLength; i >= 0; i -= 1) {
            if(vectorDice[i].getValue() != 0){
                vectorDice[i].setValue(0); 
                nDice--;
                break;
            }
        }
    }

    public int[] getDiceValues(){
        int[] diceValue = new int[nDice];
        for(int i=0; i< nDice; i+=1)
            diceValue[i] = vectorDice[i].getValue();      
        return diceValue;
    }

    public int[] getDiceValuesGrouped(Board currentBoard){
         int[] diceValue = new int[6];
         for(int i=0; i<nDice; i+=1){
             if(vectorDice[i].getValue() != 0) diceValue[vectorDice[i].getValue()-1] = diceValue[vectorDice[i].getValue()-1] + 1;
             else diceValue[i] = 0;
         }
         if(currentBoard.isOneJollyEnabled()){
            for(int i=1; i<diceValue.length; i+=1)
                diceValue[i] += diceValue[0];
            diceValue[0] = 0;
         }

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
