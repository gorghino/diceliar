
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author proietfb
 */
public class RMIServer extends UnicastRemoteObject implements RMI{
    
    public RMIServer() throws RemoteException{
        super();
    }
    
    @Override
    public String getData(String text) throws RemoteException {
        text = "Hello "+ text;
        return text;
    }
    
    @Override
    public int sum(int x, int y){
        return x+y;
    }
    
    public static void main(String[] args) {
       // if (System.getSecurityManager() == null) {
        //    System.setSecurityManager(new SecurityManager());
       // }
        try{
            RMIServer obj = new RMIServer();
            //creo un registro e vi collego il metodo associandolo come istanza
            //ad un nome ("server")
            
            Registry reg = LocateRegistry.createRegistry(1099);
            reg.bind("server", obj);
            
            System.out.println("Server Started...");
            int s = new RMIServer().sum(1, 1);
            System.out.println();
        }
        catch(Exception e){
            System.out.println(e);
        }
    }
    
    
}
 