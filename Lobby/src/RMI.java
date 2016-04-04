

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author proietfb
 */
public interface RMI extends Remote{
    
    public ArrayList<PlayerEntry> addClient(String ipPlayer, int portPlayer) throws RemoteException;   
    public void startListener() throws RemoteException;
    

}
