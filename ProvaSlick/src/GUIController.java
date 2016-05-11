
import java.io.Serializable;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author gorgo
 */
public class GUIController implements Serializable{
    Board board;


    
    boolean initBoardBool; //Primo Update della Board Play
    
    int turn; // Numero turn
    int id; //ID current Player
    
    int initDicePlayer; //Numero iniziale di dadi del giocatore
    int nPlayers; // Numero giocatori attivi
       
    
    // Check button Clicked
    boolean PlayConnectedClicked = false;
    boolean makeBetClicked = false;
    boolean doubtClicked = false;
    
    boolean betClicked = false;
    boolean leaveClicked = false;
    
    boolean betOnTable = false;
    boolean makeChoice = false;
    
    int idLastBet;
    
    int diceValueSelected;
    int diceAmountSelected;

    public GUIController() {
    }
    
    public void printValues() {
        System.out.println("--------------------------------");
        System.out.println("id: " + id);
        System.out.println("initBoardBool: " + initBoardBool);
        System.out.println("turn: " + turn);
        System.out.println("nPlayers: " + nPlayers);
        System.out.println("PlayConnectedClicked: " + PlayConnectedClicked);
        System.out.println("makeChoice: " + makeChoice);
        System.out.println("makeBetClicked: " + makeBetClicked);
        System.out.println("doubtClicked: " + doubtClicked);
        System.out.println("betClicked: " + betClicked);
        System.out.println("betOnTable: " + betOnTable);
        System.out.println("diceValueSelected: " + diceValueSelected);
        System.out.println("diceAmountSelected: " + diceAmountSelected);
        System.out.println("--------------------------------");
    }

    public boolean isPlayConnectedClicked() {
        return PlayConnectedClicked;
    }

    public void setPlayConnectedClicked(boolean PlayConnectedClicked) {
        this.PlayConnectedClicked = PlayConnectedClicked;
    }

    public boolean isBetClicked() {
        return betClicked;
    }

    public void setBetClicked(boolean betClicked) {
        this.betClicked = betClicked;
    }

    public int getDiceAmountSelected() {
        return diceAmountSelected;
    }

    public void setDiceAmountSelected(int diceAmountSelected) {
        this.diceAmountSelected = diceAmountSelected;
    }

    public int getDiceValueSelected() {
        return diceValueSelected;
    }

    public void setDiceValueSelected(int diceValueSelected) {
        this.diceValueSelected = diceValueSelected;
    }

    public boolean isDoubtClicked() {
        return doubtClicked;
    }

    public void setDoubtClicked(boolean doubtClicked) {
        this.doubtClicked = doubtClicked;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isInitBoardBool() {
        return initBoardBool;
    }

    public void setInitBoardBool(boolean initBoardBool) {
        this.initBoardBool = initBoardBool;
    }

    public int getInitDicePlayer() {
        return initDicePlayer;
    }

    public void setInitDicePlayer(int initDicePlayer) {
        this.initDicePlayer = initDicePlayer;
    }

    public boolean isLeaveClicked() {
        return leaveClicked;
    }

    public void setLeaveClicked(boolean leaveClicked) {
        this.leaveClicked = leaveClicked;
    }

    public boolean isMakeBetClicked() {
        return makeBetClicked;
    }

    public void setMakeBetClicked(boolean makeBetClicked) {
        this.makeBetClicked = makeBetClicked;
    }

    public int getnPlayers() {
        return nPlayers;
    }

    public void setnPlayers(int nPlayers) {
        this.nPlayers = nPlayers;
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }
    
    public Board getBoard() {
        return board;
    }

    public boolean isBetOnTable() {
        return betOnTable;
    }

    public void setBetOnTable(boolean betOnTable) {
        this.betOnTable = betOnTable;
    }
    
     
    
    
    
}
