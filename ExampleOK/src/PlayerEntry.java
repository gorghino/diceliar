
import java.io.Serializable;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author gorgo
 */
public class PlayerEntry implements Serializable{
    public String ip;
    public int port;
    
    public PlayerEntry(String _ip, int _port){
        this.ip = _ip;
        this.port = _port;
    }   
}
