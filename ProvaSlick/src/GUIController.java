
import java.io.Serializable;
import java.util.Arrays;

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
   
    private int nPlayers; // Numero giocatori attivi
   
       
    
    // Check button Clicked
    boolean PlayConnectedClicked = false;
    boolean makeBetClicked = false;
    boolean doubtClicked = false;
    
    boolean betClicked = false;
    boolean leaveClicked = false;
    
    boolean betOnTable = false;
    boolean makeChoice = false;
    
    boolean isBetMax = false;
    
    boolean playDiceAnimation = false;
    
    boolean winGame = false, loseGame = false;
    
    int diceValueSelected;
    int diceAmountSelected;
    
    boolean initBoard = true;
    boolean initGame = true;
    
    boolean restartBoard = false;
    
    boolean countDiceCrashed = false;
    boolean updateBetValues = false;
    boolean showDice = false;
    
    int[] totalDicePlayer;
    int[] dicePlayer;
    
    boolean oneJollyEnabled = true;
    boolean errorRibasso = false;
    boolean errorAmountMinore = false;
    
    public GUIController() {
        totalDicePlayer = new int[]{0,0,0,0,0,0,0,0};
        dicePlayer = new int[]{1,1,1,1,1};
        playDiceAnimation = false;
    }
    
    public void printValues() {
        System.out.println("--------------------------------");
        System.out.println("id: " + getId());
        System.out.println("turn: " + getTurn());
        System.out.println("nPlayers: " + nPlayers);
        System.out.println("PlayConnectedClicked: " + PlayConnectedClicked);
        System.out.println("makeChoice: " + makeChoice);
        System.out.println("makeBetClicked: " + makeBetClicked);
        System.out.println("doubtClicked: " + doubtClicked);
        System.out.println("betClicked: " + betClicked);
        System.out.println("betOnTable: " + betOnTable);
        System.out.println("diceValueSelected: " + diceValueSelected);
        System.out.println("diceAmountSelected: " + diceAmountSelected);
        System.out.println("totalDicePlayer: " + Arrays.toString(totalDicePlayer));
        System.out.println("TotalDice: " + sumOf(totalDicePlayer));
        System.out.println("Playing Player: " + getPlayingPlayer());
        System.out.println("Dadi : " + Arrays.toString(board.getCurrentPlayers().vectorPlayers[getId()].getmyDiceValue()));
        System.out.println("Dice check:" + Arrays.toString(dicePlayer));
        System.out.println("--------------------------------");
    }
    
    public static int sumOf(int... integers) {
        int total = 0;
        for (int i = 0; i < integers.length; total += integers[i++]);
        return total;
    } 

//    public void setPlayingPlayer(int _playingPlayer) {
//        System.out.println("Aggiorno PP da " + this.playingPlayer + " a " + _playingPlayer);
//        this.board.playingPlayer = this.board.geplayingPlayer;
//    }
    
    public int getPlayingPlayer(){
        return this.board.getPlayingPlayer().myID;
    }
    
    public int getIdLastBet(){
        return this.board.getIdLastBet();
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
        return this.getBoard().getMyID();
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
        return this.board.getnTurn();
    }

    public void setTurn(int turn) {
        this.board.setnTurn(turn);
    }
    
    public Board getBoard() {
        return board;
    }
    
    public void setBoard(Board _currentBoard){
        this.board = _currentBoard;
    }

    public boolean isBetOnTable() {
        return betOnTable;
    }

    public void setBetOnTable(boolean betOnTable) {
        this.betOnTable = betOnTable;
    }

    public boolean isMakeChoice() {
        return makeChoice;
    }

    public void setMakeChoice(boolean makeChoice) {
        this.makeChoice = makeChoice;
    }

    public boolean isIsBetMax() {
        return isBetMax;
    }

    public void setIsBetMax(boolean isBetMax) {
        this.isBetMax = isBetMax;
    }

    public boolean isPlayDiceAnimation() {
        return playDiceAnimation;
    }

    public void setPlayDiceAnimation(boolean playDiceAnimation) {
        this.playDiceAnimation = playDiceAnimation;
    }

    public boolean isWinGame() {
        return winGame;
    }

    public void setWinGame(boolean winGame) {
        this.winGame = winGame;
    }

    public boolean isLoseGame() {
        return loseGame;
    }

    public void setLoseGame(boolean loseGame) {
        this.loseGame = loseGame;
    }

    public boolean isInitBoard() {
        return initBoard;
    }

    public void setInitBoard(boolean initBoard) {
        this.initBoard = initBoard;
    }

    public boolean isInitGame() {
        return initGame;
    }

    public void setInitGame(boolean initGame) {
        this.initGame = initGame;
    }

    public boolean isRestartBoard() {
        return restartBoard;
    }

    public void setRestartBoard(boolean restartBoard) {
        this.restartBoard = restartBoard;
    }

    public boolean isCountDiceCrashed() {
        return countDiceCrashed;
    }

    public void setCountDiceCrashed(boolean countDiceCrashed) {
        this.countDiceCrashed = countDiceCrashed;
    }

    public boolean isUpdateBetValues() {
        return updateBetValues;
    }

    public void setUpdateBetValues(boolean updateBetValues) {
        this.updateBetValues = updateBetValues;
    }

    public boolean isShowDice() {
        return showDice;
    }

    public void setShowDice(boolean showDice) {
        this.showDice = showDice;
    }

    public int[] getTotalDicePlayer() {
        return totalDicePlayer;
    }

    public void setTotalDicePlayer(int[] totalDicePlayer) {
        this.totalDicePlayer = totalDicePlayer;
    }

    public int[] getDicePlayer() {
        return dicePlayer;
    }

    public void setDicePlayer(int[] dicePlayer) {
        this.dicePlayer = dicePlayer;
    }

    public boolean isOneJollyEnabled() {
        return oneJollyEnabled;
    }

    public void setOneJollyEnabled(boolean oneJollyEnabled) {
        this.oneJollyEnabled = oneJollyEnabled;
    }

    public boolean isErrorRibasso() {
        return errorRibasso;
    }

    public void setErrorRibasso(boolean errorRibasso) {
        this.errorRibasso = errorRibasso;
    }

    public boolean isErrorAmountMinore() {
        return errorAmountMinore;
    }

    public void setErrorAmountMinore(boolean errorAmountMinore) {
        this.errorAmountMinore = errorAmountMinore;
    }

    
    
    
    
}
