import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author proietfb
 */
public class Board implements Serializable{
    int nTurn;
    int nPlayers;
    
    int myID;

    Bet currentBet;

    Players currentPlayers;
    Player playingPlayer;
    
    public transient final Object lock;
    public boolean ready;
    
    RMI rmiNextPlayer;
    private int diceUpdated = 1;

    public Board(int _id, int _nTurn, int _nPlayers, ArrayList<PlayerEntry> _rmiPlayerArray, Object _lock) throws RemoteException {
        myID = _id;
        nTurn = _nTurn;
        nPlayers = _nPlayers;

        //currentBet = new Bet(2,2);

        currentPlayers = new Players(_nPlayers, _rmiPlayerArray);
        currentPlayers.getAllId();
        currentPlayers.getVectorPlayers()[0].makeChoice(this);
     
        lock = _lock;


    }

    void initGame(Board startBoard, RMI _rmiNextPlayer) throws RemoteException{
        int playerStarterID = 0; //startBoard.setStarter();
        rmiNextPlayer = _rmiNextPlayer;
        Player playerStarter = startBoard.getCurrentPlayers().getVectorPlayers()[playerStarterID];
        setPlayingPlayer(playerStarter);

        System.out.println("Inizia a giocare il giocatore numero " + playerStarterID);
        playerStarter.setTurn(true);

        gameLoop(startBoard, playerStarter);

    }

    private void gameLoop(Board board, Player player) throws RemoteException{
        while(true){
            if(myID == getPlayingPlayer().getMyID()){
                System.out.println("* Giocatore " + getPlayingPlayer().getMyID() + " tocca a te!");
                System.out.println("Turno: " + this.getnTurn());
                player.makeChoice(board);
                player.setTurn(false); //Non tocca piu a questo player

                //Il turno passa al giocatore successivo
                board.setnTurn(getnTurn() + 1);
                board.setPlayingPlayer(board.getCurrentPlayers().getVectorPlayers()[(getPlayingPlayer().getMyID() + 1) % nPlayers]);
                player = board.getCurrentPlayers().getVectorPlayers()[(getPlayingPlayer().getMyID() + 1) % nPlayers];
                player.setTurn(true);
                
                rmiNextPlayer.notifyTurn(board);
            }
            else{
                synchronized (lock) {
                try {
                    while (!ready){
                         System.out.println("Sono " + myID + " e mi blocco");
                         lock.wait();
                         boolean pota = myID == getPlayingPlayer().getMyID();
                         System.out.println("Mi sblocco e " + pota);
                    }
                } catch (InterruptedException ex) {}
                }
            }
        }
    }

    public boolean checkBet(){
        return currentBet.getAmount() > currentPlayers.getAllDice()[currentBet.getValueDie()-1]; //Ci sono piu o uguale dadi di quelli della scommessa --> OK
    }

    public int getnTurn() {
        return nTurn;
    }

    public void setnTurn(int _nTurn) {
        this.nTurn = _nTurn;
    }

    public int getnPlayers() {
        return nPlayers;
    }

    public void setnPlayers(int _nPlayers) {
        this.nPlayers = _nPlayers;
    }

    public Players getCurrentPlayers() {
        return currentPlayers;
    }

    public void setCurrentPlayers(Players currentPlayers) {
        this.currentPlayers = currentPlayers;
    }

    public int setStarter(){
        return ThreadLocalRandom.current().nextInt(0, nPlayers);
    }

    public Bet getCurrentBet() {
        return currentBet;
    }

    public void setCurrentBet(Bet _currentBet) {
        this.currentBet = _currentBet;
    }

    public Player getPlayingPlayer() {
        return playingPlayer;
    }

    public void setPlayingPlayer(Player playingPlayer) {
        this.playingPlayer = playingPlayer;
    }
    
    public Object getLock(){
        return this.lock;
    }

    void newTurn(Board currentBoard, int starterIDPlayer, Bet starterBet) throws RemoteException {
        System.out.println("--------------- NUOVO TURNO ---------------------\n");
        this.getCurrentPlayers().resetAllDice();
        currentPlayers.printDice();
        currentBoard.setCurrentBet(starterBet);
        currentBoard.getCurrentPlayers().getVectorPlayers()[starterIDPlayer].setTurn(true);
        
        rmiNextPlayer.resetDice(currentPlayers, rmiNextPlayer);
    }
    
    
    
    public void shareDice(Players currentPlayers, RMI rmiNext) throws RemoteException{
        if(myID == 0){
            //Sono il giocatore 0, inizio il ring condividendo il set di dadi
            System.out.println("Sono " + myID + " e passo i miei dadi al prossimo!");
            if(!(rmiNext.setDice(myID, currentPlayers)) || diceUpdated != currentPlayers.getVectorPlayers().length){
                synchronized (lock) {
                    try {
                        while (!ready){
                            System.out.println("Sono " + myID + " e aspetto la fine del ring!");
                            lock.wait();
                            System.out.println("Sono " + myID + " e mi sono sbloccato!");
                            rmiNext.setDice(myID, currentPlayers);
                        }
                    } catch (InterruptedException ex) {}
                }
            }
        }
        else{
            synchronized (lock) {
                try {
                    System.out.println("POOOOTA");
                    while (!ready){
                        System.out.println("Sono " + myID + " e blocco!");
                        lock.wait();
                        System.out.println("Sono " + myID + " e mi sono sbloccato!");
                        rmiNext.setDice(myID, currentPlayers);
                        if(diceUpdated != currentPlayers.getVectorPlayers().length){
                            System.out.println("Non è l'ultimo giro");
                            ready = false;
                        }    
                    }
                } catch (InterruptedException ex) {}
            }
            
        }
    }

}
