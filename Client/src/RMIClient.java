
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author proietfb
 */
public class RMIClient  {


    public int sum(int x, int y){
        return x+y;
    }
    public static void main(String[] args) {
        RMIClient client = new RMIClient();
        client.connectServer();

    }
    private void connectServer(){
      //  if (System.getSecurityManager() == null) {
            //System.setSecurityManager(new SecurityManager());
       // }
        try{

            Registry reg= LocateRegistry.getRegistry("localhost", 40000);
            //ottengo lo stub dell'oggetto remoto contenuto nel server
            RMI rmi = (RMI) reg.lookup("server");
            System.out.println("Connected to server");

            //uso il metodo remoto
            Board text = rmi.getBoard();
            System.out.println(Integer.toString(text.getnPlayers()));


        }
        catch(Exception e){
            System.out.println(e);
        }
    }
}
