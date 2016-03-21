/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lobby;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 *
 * @author gorgo
 */
public class Lobby extends UnicastRemoteObject implements RMI{

     public Lobby() throws RemoteException{
        super();
    }
     
    public static void main(String[] args) throws RemoteException {
        Registry reg = LocateRegistry.createRegistry(40000);
        reg.bind("server", obj);
    }

    @Override
    public void getClient() throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
