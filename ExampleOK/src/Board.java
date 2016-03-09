

import java.io.Serializable;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Random;

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

    Bet currentBet;

    Players currentPlayers;
    Player playingPlayer;

    public Board(int _nTurn, int _nPlayers){
        nTurn = _nTurn;
        nPlayers = _nPlayers;

        //currentBet = new Bet(2,2);

        currentPlayers = new Players(_nPlayers);
        currentPlayers.getAllId();
        currentPlayers.getVectorPlayers()[0].makeChoice();


    }

    void initGame(Board startBoard){
        int playerStarterID = startBoard.setStarter();
        Player playerStarter = startBoard.getCurrentPlayers().getVectorPlayers()[playerStarterID];
        setPlayingPlayer(playerStarter);

        System.out.println("Inizia a giocare il giocatore numero " + playerStarterID);
        playerStarter.setTurn(true);

        gameLoop(startBoard, playerStarter);

    }

    private void gameLoop(Board board, Player player){
        while(true){
            System.out.println("* Giocatore " + getPlayingPlayer().getMyID()+ " tocca a te!");

            System.out.println("Turno: " + this.getnTurn());
            player.makeChoice();

            player.setTurn(false); //Non tocca piu a questo player

            //Il turno passa al giocatore successivo
            board.setnTurn(getnTurn() + 1);
            board.setPlayingPlayer(board.getCurrentPlayers().getVectorPlayers()[(getPlayingPlayer().getMyID() + 1) % nPlayers]);
            player = board.getCurrentPlayers().getVectorPlayers()[(getPlayingPlayer().getMyID() + 1) % nPlayers];
            player.setTurn(true);
        }
    }

    public boolean checkBet(){
        if(currentBet.getAmount() > currentPlayers.getAllDice()[currentBet.getValueDie()-1]) //Ci sono piu o uguale dadi di quelli della scommessa --> OK
            return true;
        else
            return false;
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

    void newTurn(Board currentBoard, int starterIDPlayer, Bet starterBet) {
        System.out.println("--------------- NUOVO TURNO ---------------------\n");
        this.getCurrentPlayers().resetAllDice();
        currentPlayers.printDice();
        currentBoard.setCurrentBet(starterBet);
        currentBoard.getCurrentPlayers().getVectorPlayers()[starterIDPlayer].setTurn(true);
    }





}
