

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.Serializable;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author proietfb
 */
public class Die implements Serializable{
    int value;

    public Die(){
        value = ThreadLocalRandom.current().nextInt(1, 6 + 1);
    }

    public void resetValue(){
        value = ThreadLocalRandom.current().nextInt(1, 6 + 1);
    }

    public int getValue() {
        return value;
    }

    public void setValue(int _value) {
        this.value = _value;
    }


}
