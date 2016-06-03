import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Board implements Serializable {
    
    public static int IDLE = 0;
    public static int PLAYING = 2;
    public static int INIT_RESET = 3;
    
    private int nTurn;
    private int nPlayers;
    private int myID;

    private int status;
    private int loser = 0, winner = 0;
    private int idLastBet;
    
    private boolean oneJollyEnabled;
    private boolean okDoubt;
    private boolean initGame;
    
    private Bet currentBet;
    private Players currentPlayers;
    private Player playingPlayer;

    public transient final Object lock;

    GUIController gC;

    public Board(int _id, int _nTurn, int _nPlayers, ArrayList<PlayerEntry> _rmiPlayerArray, Object _lock, GUIController _gC) throws NotBoundException {
        this.myID = _id;
        this.nTurn = _nTurn;
        this.nPlayers = _nPlayers;
        this.currentPlayers = new Players(_nPlayers, _rmiPlayerArray);
        this.currentPlayers.getAllId();
        this.oneJollyEnabled = true;
        this.initGame = true;
        this.status = PLAYING;
        this.gC = _gC;
        this.lock = _lock;
    }

    void initGame(Board startBoard, RMI _rmiNextPlayer) {
        int playerStarterID = 0;
        startBoard.getCurrentPlayers().getVectorPlayers()[myID].rmiNextPlayer = _rmiNextPlayer;
        Player playerStarter = startBoard.getCurrentPlayers().getVectorPlayers()[playerStarterID];
        startBoard.setPlayingPlayer(playerStarter);
        startBoard.setnTurn(1);

        if (startBoard.myID == 0) startBoard.broadcastRMI(startBoard, "RESET_DICE");

        System.out.println("Inizia a giocare il giocatore numero " + playerStarterID);
        playerStarter.setTurn(true);
    }

    public void gameLoop(Board board, Player player) {
        player = getCurrentPlayers().getVectorPlayers()[myID];

        if (myID == getPlayingPlayer().getMyID() && status != Board.INIT_RESET) {
            status = PLAYING;

            if (!player.makeChoice(board)) return;
            
            gC.betClicked = false;

            if (status == INIT_RESET) {
                //Nuovo turno
                synchronized (lock) { lock.notifyAll(); }
                System.out.println("Passo il turno");
                status = PLAYING;
                return;
            }

            //Il turno passa al giocatore successivo
            player.setTurn(false); //Non tocca piu a questo player

            System.out.println(myID + ": NOTIFYTURN AL SUCCESSIVO");

            Player nextPlayer = board.getCurrentPlayers().getVectorPlayers()[(getPlayingPlayer().IDNext)];

            nextPlayer.setTurn(true);
            board.setPlayingPlayer(nextPlayer);

            gC.betOnTable = true;
            gC.setDiceAmountSelected(board.getCurrentBet().getAmount());
            gC.setDiceValueSelected(board.getCurrentBet().getValueDie());
            setIdLastBet(myID);

            try {
                this.currentPlayers.getVectorPlayers()[myID].rmiNextPlayer.notifyTurn(board);
            } catch (RemoteException ex) {
                System.out.println(DiceLiar.ANSI_RED + "!! CRASH RILEVATO. Il giocatore " + currentPlayers.getVectorPlayers()[myID].IDNext + "non è raggiungibile." + DiceLiar.ANSI_RESET);
                currentPlayers.removePlayer(currentPlayers.getVectorPlayers()[currentPlayers.getVectorPlayers()[myID].IDNext], true, true);

                try {
                    System.out.println(DiceLiar.ANSI_RED + " FINALLY " + DiceLiar.ANSI_RESET);
                    this.currentPlayers.getVectorPlayers()[myID].rmiNextPlayer.notifyTurn(board);
                } catch (RemoteException ex2) {
                    System.out.println(DiceLiar.ANSI_RED + "!! FATAL ERROR - RMI ERROR IN FINALLY CODE" + DiceLiar.ANSI_RESET);
                    Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            synchronized (lock) { lock.notifyAll(); }
            
        } else if (status == PLAYING) {

            if (gC.playDiceAnimation) return;

            System.out.println("Non tocca a me. Mi blocco sul giocatore " + getPlayingPlayer().getMyID());
            try {
                this.getPlayingPlayer().rmiPointer.checkPlayerCrash(this);
            } catch (RemoteException ex) {
                int idCrashed = getPlayingPlayer().getMyID();
                System.out.println(DiceLiar.ANSI_RED + "!! CRASH DEL PLAYING PLAYER RILEVATO. Il giocatore " + idCrashed + " non è raggiungibile." + DiceLiar.ANSI_RESET);
                int newPlaying = currentPlayers.removePlayer(currentPlayers.getVectorPlayers()[idCrashed], true, false);

                if (getCurrentPlayers().getPlayersAlive() == 1 && getWinner() == myID) {
                    System.out.println(DiceLiar.ANSI_GREEN + "Sei rimasto solo tu. HAI VINTO!" + DiceLiar.ANSI_RESET);
                    gC.setWinGame(true);
                    gC.setRestartBoard(false);
                    gC.setInitBoard(false);
                    gC.setPlayDiceAnimation(false);
                    return;
                }

                if (newPlaying == myID) { //Sono il nuovo PlayingPlayer
                    System.out.println(DiceLiar.ANSI_CYAN + "ID: " + myID + " sono il nuovo playingPlayer" + DiceLiar.ANSI_RESET);
                    setPlayingPlayer(currentPlayers.getVectorPlayers()[myID]);
                    getPlayingPlayer().setTurn(true);
                    setWinner(myID);
                    setLoser(idCrashed);
                    
                    if (board.getnTurn() == 1) {
                        //E' crashato il primo giocatore del turno, ridistribuisco i dadi in caso sia crashato mentre li distribuiva
                        getCurrentPlayers().resetAllDice(myID);
                        setCurrentBet(null);
                        broadcastRMI(this, "RESET_DICE");
                        synchronized (lock) { lock.notifyAll(); }
                    }
                } else { //Aggiorno il playing su cui bloccarmi
                    setPlayingPlayer(currentPlayers.getVectorPlayers()[newPlaying]);
                    System.out.println("Turno GC: " + board.getnTurn());
                    getPlayingPlayer().setTurn(true);
                }
            }
        }
    }

    void newTurn(Board currentBoard, int starterIDPlayer, Bet starterBet) {
        getCurrentPlayers().resetAllDice(myID);
        currentBoard.setCurrentBet(starterBet);
        currentBoard.getCurrentPlayers().getVectorPlayers()[starterIDPlayer].setTurn(true);
        currentBoard.setPlayingPlayer(currentBoard.getCurrentPlayers().getVectorPlayers()[starterIDPlayer]);
        broadcastRMI(currentBoard, "RESET_DICE");
        status = Board.INIT_RESET;
    }

    public void broadcastRMI(Board board, String function) {
        int j = 0;
        for (int i = (board.myID + 1) % board.getnPlayers(); j < board.getnPlayers(); i = (i + 1) % board.getnPlayers()) {
            Player vectorPlayer = board.getCurrentPlayers().getVectorPlayers()[i];
            j++;
            
            //if(myID==0)
               // System.exit(0);

            if (vectorPlayer.isPlayerOut())
                continue;

            RMI rmiPointer = vectorPlayer.getRmiPointer();

            if (function.equalsIgnoreCase("RESET_DICE")) {
                try {
                    rmiPointer.resetDice(board);
                } catch (RemoteException ex) {
                    System.out.println(DiceLiar.ANSI_RED + "!! CRASH RILEVATO. Il giocatore " + vectorPlayer.getMyID() + " non è raggiungibile." + DiceLiar.ANSI_RESET);
                    currentPlayers.removePlayer(currentPlayers.getVectorPlayers()[vectorPlayer.getMyID()], true, false);
                }
            } else if (function.equalsIgnoreCase("ONE_IS_ONE")) {
                try {
                    rmiPointer.oneIsOne(board);
                } catch (RemoteException ex) {
                    System.out.println(DiceLiar.ANSI_RED + "!! CRASH RILEVATO. Il giocatore " + vectorPlayer.getMyID() + " non è raggiungibile." + DiceLiar.ANSI_RESET);
                    currentPlayers.removePlayer(currentPlayers.getVectorPlayers()[vectorPlayer.getMyID()], true, false);
                }
            } else if (function.equalsIgnoreCase("NOTIFY_MOVE")) {
                try {
                    if (this.currentPlayers.getVectorPlayers()[myID].getMyID() == vectorPlayer.getMyID()) {
                        continue;
                    }
                    rmiPointer.notifyMove(board);
                } catch (RemoteException ex) {
                    System.out.println(DiceLiar.ANSI_RED + "!! CRASH RILEVATO. Il giocatore " + vectorPlayer.getMyID() + " non è raggiungibile." + DiceLiar.ANSI_RESET);
                    currentPlayers.removePlayer(currentPlayers.getVectorPlayers()[vectorPlayer.getMyID()], true, false);
                }
            } else if (function.equalsIgnoreCase("SIGNAL_CRASH")) {
                if (this.currentPlayers.getVectorPlayers()[myID].getMyID() == vectorPlayer.getMyID() || vectorPlayer.isPlayerOut()) {
                    continue;
                }
                try {
                    System.out.println("Segnalo crash a " + vectorPlayer.getMyID());
                    rmiPointer.signalCrash(board);
                } catch (RemoteException ex) {
                    System.out.println(DiceLiar.ANSI_RED + "!! CRASH RILEVATO. Il giocatore " + vectorPlayer.getMyID() + " non è raggiungibile." + DiceLiar.ANSI_RESET);
                    currentPlayers.removePlayer(currentPlayers.getVectorPlayers()[vectorPlayer.getMyID()], true, true);
                }
            }
        }
    }

    public boolean checkBet() {
        System.out.println("Sul tavolo ci sono " + currentPlayers.getAllDice(false)[currentBet.getValueDie() - 1] + " dadi");
        return currentBet.getAmount() > currentPlayers.getAllDice(false)[currentBet.getValueDie() - 1]; //Ci sono piu o uguale dadi di quelli della scommessa --> OK
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

    public int setStarter() {
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

    public Object getLock() {
        return this.lock;
    }

    public GUIController getgC() {
        return gC;
    }

    public int getIdLastBet() {
        return idLastBet;
    }

    public void setIdLastBet(int idLastBet) {
        this.idLastBet = idLastBet;
    }

    public int getMyID() {
        return myID;
    }

    public void setMyID(int myID) {
        this.myID = myID;
    }

    public boolean isOkDoubt() {
        return okDoubt;
    }

    public void setOkDoubt(boolean okDoubt) {
        this.okDoubt = okDoubt;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isOneJollyEnabled() {
        return oneJollyEnabled;
    }

    public void setOneJollyEnabled(boolean oneJollyEnabled) {
        this.oneJollyEnabled = oneJollyEnabled;
    }

    public int getLoser() {
        return loser;
    }

    public void setLoser(int loser) {
        this.loser = loser;
    }

    public int getWinner() {
        return winner;
    }

    public void setWinner(int winner) {
        this.winner = winner;
    }

    public boolean getInitGame() {
        return initGame;
    }

    public void setInitGame(boolean initGame) {
        this.initGame = initGame;
    }
}
