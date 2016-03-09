

import java.rmi.Remote;
import java.rmi.RemoteException;

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
    public String getData(String text) throws RemoteException;

    public int sum(int x,int y) throws RemoteException;

    public Board getBoard() throws RemoteException;

}
