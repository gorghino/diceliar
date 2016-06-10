
import java.util.ArrayList;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.*;
import org.newdawn.slick.state.*;
import org.newdawn.slick.GameContainer;
import static java.lang.Math.abs;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author proietfb animazione dei dadi da ritardare(al primo game loop?)
 */
public class Play extends BasicGameState {

    public ArrayList<Image> dice = new ArrayList<>();

    Animation animationDie1, animationDie2, animationDie3, animationDie4, animationDie5;

    int[][] positionPlayerDice, positionDice, playerNamePosition, selectorPosition, oldPlayerDice;
    int[] amountDicePlayers, countDice;

    private int getX, getY;

    private int nPlayers, cnt, time, timeCheckCrash;//1

    private int drawDieBet, drawValueBet, dimXHor, dimYVer;//0
    public int lbDrawDieBet, lbDrawValueBet, updateNewGamePanel = 0, id;

    public boolean clickToChangeDie = false, clickToChangeValue = false, newTurn = false, newGame = true, forceRefresh;

    Board board;

    GUIController gC;
    GuiDefineImages guiDefImg;
    GuiDefineButtons gDrawButtons;
    GuiDefineFont gDefFont;
    private boolean playerOut;
    private boolean firstUpdate = true;

    public Play(GUIController _gC, GuiDefineImages _guiDefImg, GuiDefineButtons _gDrawButtons, GuiDefineFont _gDefFont) {
        this.gC = _gC;
        this.guiDefImg = _guiDefImg;
        this.gDrawButtons = _gDrawButtons;
        this.gDefFont = _gDefFont;

        countDice = new int[5];

        forceRefresh = false;
    }

    @Override
    public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {

        dice = guiDefImg.getArrayDice();

        nPlayers = 8;
        
        playerOut = true;

        positionPlayerDice = new int[nPlayers][5];
        oldPlayerDice = new int[nPlayers][5];
        positionDice = new int[nPlayers][2];
        playerNamePosition = new int[nPlayers][2];
        selectorPosition = new int[nPlayers][2];

        drawDieBet = 1;
        drawValueBet = 1;

        Image[] anDie1 = {dice.get(5), dice.get(8), dice.get(4), dice.get(1), dice.get(3), dice.get(2)};
        Image[] anDie2 = {dice.get(6), dice.get(1), dice.get(5), dice.get(4), dice.get(2), dice.get(8)};
        Image[] anDie3 = {dice.get(2), dice.get(6), dice.get(3), dice.get(8), dice.get(4), dice.get(1)};
        Image[] anDie4 = {dice.get(4), dice.get(2), dice.get(8), dice.get(3), dice.get(5), dice.get(6)};
        Image[] anDie5 = {dice.get(2), dice.get(5), dice.get(6), dice.get(4), dice.get(8), dice.get(3)};

        animationDie1 = new Animation(anDie1, 100, true);
        animationDie2 = new Animation(anDie2, 100, true);
        animationDie3 = new Animation(anDie3, 100, true);
        animationDie4 = new Animation(anDie4, 100, true);
        animationDie5 = new Animation(anDie5, 100, true);
    }

    @Override
    public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException { // run this every frame to update game logic

        guiDefImg.getGuiBoard().draw(0, 0);

        positionDice[0][0] = 249;
        positionDice[0][1] = 334;
        positionDice[1][0] = 699;
        positionDice[1][1] = 784;
        positionDice[2][0] = Main.ySize - 97;
        positionDice[2][1] = Main.ySize - 163;
        positionDice[3][0] = Main.ySize - 470;
        positionDice[3][1] = Main.ySize - 537;
        positionDice[4][0] = positionDice[1][0];
        positionDice[4][1] = positionDice[1][1];
        positionDice[5][0] = positionDice[0][0];
        positionDice[5][1] = positionDice[0][1];
        positionDice[6][0] = Main.ySize - 474;
        positionDice[6][1] = Main.ySize - 539;
        positionDice[7][0] = Main.ySize - 101;
        positionDice[7][1] = Main.ySize - 165;

        selectorPosition[0][0] = 230;
        selectorPosition[0][1] = Main.ySize - 240;

        selectorPosition[1][0] = 680;
        selectorPosition[1][1] = selectorPosition[0][1];

        selectorPosition[2][0] = 1127;
        selectorPosition[2][1] = Main.ySize - 388;
        selectorPosition[3][0] = selectorPosition[2][0];
        selectorPosition[3][1] = Main.ySize - 762;
        selectorPosition[4][0] = selectorPosition[1][0];
        selectorPosition[4][1] = Main.ySize - 762;
        selectorPosition[5][0] = selectorPosition[0][0];
        selectorPosition[5][1] = Main.ySize - 762;
        selectorPosition[6][0] = 3;
        selectorPosition[6][1] = selectorPosition[3][1];
        selectorPosition[7][0] = 3;
        selectorPosition[7][1] = selectorPosition[2][1];

        drawSelectorPlayer(); //disegna il selettore del giocatore a cui tocca fare la mossa

        drawPlayerName(); //disegna i nomi dei giocatori

        //DRAW PLAYERS
        cnt = 0;
        countDice = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
        dimXHor = 240;
        dimYVer = 377;

        for (int i = 0; i < gC.getBoard().getnPlayers(); i++) {
            for (int j = 0; j < 5; j++) {
                if (i <= 1) {
                    if (!gC.isShowDice()) {
                        //System.out.println("Disegna i dadi di 0 e 1 ");
                        drawPlayerOneTwo(i, j);
                    } else {
                        //System.out.println("Mostra i dadi di 0 e 1");
                        playerOut = false;
                        showPlayerDiceOneTwo(i, j);
                    }

                } else if (i > 1 && i < 4) {
                    if (!gC.isShowDice()) {
                        //System.out.println("Disegna i dadi di 2 e 3 ");
                        drawPlayerThreeFour(i, j);
                    } else {
                        //System.out.println("Mostra i dadi di 2 e 3");
                        showPlayerDiceThreeFour(i, j);
                    }
                } else if (i > 3 && i < 6) {
                    if (!gC.isShowDice()) {
                        drawPlayerFiveSix(i, j);
                    } else {
                        showPlayerDiceFiveSix(i, j);
                    }
                } else 
                    if (!gC.isShowDice()) {
                    drawPlayerSevenEight(i, j);
                } else {
                    showPlayerDiceSevenEight(i, j);
                }
            }
        }

        //TURN PANEL
        gDefFont.getFontTurn().drawString(628, Main.ySize - 522, "Turn: ", Color.white);
        gDefFont.getFontTurn().drawString(701, Main.ySize - 522, "" + gC.getTurn(), Color.black);

        //PANEL DX
        gDefFont.getFontTurn().drawString(850, Main.ySize - 471, "Panel Bet", Color.black);

        if (gC.makeChoice && gC.getTurn() > 1 && gC.getId() == gC.getBoard().getPlayingPlayer().getMyID()) {
            //button MakeBet
            if (!gC.isBetMax) {
                gDrawButtons.drawButton(825, Main.ySize - 390, GuiDefineButtons.buttonWidth, GuiDefineButtons.buttonHeigh, 878, Main.ySize - 375, "Make Bet", Color.white);
            }
            //button Doubt
            gDrawButtons.drawButton(825, Main.ySize - 300, GuiDefineButtons.buttonWidth, GuiDefineButtons.buttonHeigh, 885, Main.ySize - 285, "Doubt", Color.white);

        } else if (gC.getId() == gC.getBoard().getPlayingPlayer().getMyID()) {

            //button Bet
            gDrawButtons.drawButton(825, Main.ySize - 300, GuiDefineButtons.buttonWidth, GuiDefineButtons.buttonHeigh, 902, Main.ySize - 285, "Bet", Color.white);

            //Draw arrows
            g.drawImage(guiDefImg.getArrowLeft(), 695, 380);
            g.drawImage(guiDefImg.getArrowRight(), 835, 380);
            g.drawImage(guiDefImg.getArrowLeft(), 932, 380);
            g.drawImage(guiDefImg.getArrowRight(), 1055, 380);

            //draw dice for bet
            if ((gC.getDiceAmountSelected() == 0 || gC.getDiceValueSelected() == 0) && drawValueBet == 1) {
                gDefFont.getFontValue().drawString(1005, 385, "1", Color.black);
                g.drawImage(dice.get(1), 752, 371);
            } else {
                gDefFont.getFontValue().drawString(1005, 385, "" + drawValueBet, Color.black);
                g.drawImage(dice.get(drawDieBet), 752, 371);
            }

            if (clickToChangeDie) {
                g.drawImage(dice.get(drawDieBet), 752, 371);
            }

            if (clickToChangeValue) {
                gDefFont.getFontValue().drawString(1005, 385, "" + drawValueBet, Color.black);
            }

        }

        /////////////////////
        //////////////////// Panel SX     
        gDefFont.getFontTurn().drawString(269, Main.ySize - 471, "Last bet made by: ", Color.black);

        //Playing Player stat
        gDefFont.getFontTurn().drawString(275, Main.ySize - 305, "Current playing player", Color.black);
        gDefFont.getPinesFont().drawString(525, Main.ySize - 310, "' ", Color.black);
        gDefFont.getFontTurn().drawString(533, Main.ySize - 305, "s state:", Color.black);
        gDefFont.getFontTurn().drawString(307, Main.ySize - 272, "Player " + gC.getPlayingPlayer(), Color.black);
        gDefFont.getFontTurn().drawString(524, Main.ySize - 272, "N Dice: " + gC.totalDicePlayer[gC.getPlayingPlayer()], Color.black);

        if (gC.betOnTable == true) {
            g.setColor(Color.black);
            gDefFont.getFontTurn().drawString(534, Main.ySize - 471, "Player " + gC.getIdLastBet(), Color.black);
            gDefFont.getFontTurn().drawString(285, Main.ySize - 370, gC.getDiceAmountSelected() + " dice whose value is", Color.black);
            g.drawImage(dice.get(gC.getDiceValueSelected()), 527, Main.ySize - 408);
        }

        //OVERWRITE PANELS
        if (newTurn == true) {
            guiDefImg.getBackPanel().draw(245, Main.ySize - 524);

            if (newGame) {
                gDefFont.getFontValue().drawString(590, Main.ySize - 420, "New Game", Color.black);
            } else if (gC.getBoard().getWinner() != gC.getBoard().getLoser()) {
                gDefFont.getFontValue().drawString(420, Main.ySize - 470, "Player " + gC.getBoard().getLoser() + "  lost the previous turn", Color.black);
                gDefFont.getFontValue().drawString(590, Main.ySize - 386, "New Turn", Color.black);
            }
        }

        if (gC.isErrorAmountMinore()) {
            guiDefImg.getBackPanel().draw(245, Main.ySize - 524);
            gDefFont.getFontValue().drawString(330, Main.ySize - 416, "You cannot raise less or equal to last bet", Color.black);
        }

        if (gC.isErrorRibasso()) {
            guiDefImg.getBackPanel().draw(245, Main.ySize - 524);
            gDefFont.getFontValue().drawString(380, Main.ySize - 416, "You cannot revive a downward", Color.black);
        }

        if (gC.isWinGame()) {
            guiDefImg.getBackPanel().draw(245, Main.ySize - 524);
            gDefFont.getFontValue().drawString(590, Main.ySize - 416, "You win", Color.black);
        }

        if (gC.isLoseGame()) {
            guiDefImg.getBackPanel().draw(245, Main.ySize - 524);
            gDefFont.getFontValue().drawString(590, Main.ySize - 416, "You lose", Color.black);
        }

        //CHECK PLAYER OUT
        for (int i = 0; i < gC.getBoard().getnPlayers(); i++) {
            if (i == 0 || i == 1 || i == 4 || i == 5) {
                if (gC.getBoard().getCurrentPlayers().getVectorPlayers()[i].isPlayerOut() && playerOut) {
                    guiDefImg.getPlayerRemovedHoriz().draw(selectorPosition[i][0], selectorPosition[i][1]);
                }
            } else if (gC.getBoard().getCurrentPlayers().getVectorPlayers()[i].isPlayerOut() && playerOut) {
                guiDefImg.getPlayerRemovedVert().draw(selectorPosition[i][0], selectorPosition[i][1]);
            }
        }
    }

    @Override
    public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException { // run this every frame to display graphics to the player 
        timeCheckCrash += delta;
        
        if(firstUpdate){
            timeCheckCrash = 0;
            firstUpdate = false;
        }
        
        if (timeCheckCrash >= (8000+(gC.getId()*1000)) && gC.getBoard().getInitGame() == true) {
            System.out.println(DiceLiar.ANSI_RED + timeCheckCrash + " ATTENZIONE! PROBABILE CRASH DI CHI DOVEVA INVIARE I DADI " + DiceLiar.ANSI_RESET);
            gC.getBoard().setInitGame(false);
            gC.setPlayDiceAnimation(false);
        }
            
      
        //System.out.println("GC.InitBoard: "+ gC.initBoard + "  restartBoard: " + gC.restartBoard + "   board.initBoard: " + getBoard().initBoard);
        if ((gC.isInitBoard() == true || gC.isRestartBoard() == true) && gC.getBoard().getInitGame() == false) {
            //System.out.println("RESTART INIT BOARD");
            restartInitBoard();
            time = 0;
            timeCheckCrash = 0;
            delta = 0; 
            forceRefresh = true;
        }

        if (gC.isPlayDiceAnimation() || gC.isErrorAmountMinore() || gC.isErrorAmountMinore() || gC.isWinGame() || gC.isLoseGame()) {
            if (gC.getTurn() == 1 || gC.isErrorAmountMinore() || gC.isErrorAmountMinore() || gC.isWinGame() || gC.isLoseGame()) {
                time += delta;
            }

            if (time >= gC.getTimeMin() && time < gC.getTimeMax()) {
                gC.setShowDice(false);
            }

            if (time >= gC.getTimeMax()) {
                newTurn = false;
                gC.setErrorAmountMinore(false);
                gC.setErrorRibasso(false);

                if (gC.getBoard().getInitGame() == false) {
                    gC.setPlayDiceAnimation(false);
                    time = 0;
                    playerOut = true;
                }
            }
        }

        if (gC.playDiceAnimation) {
            return;
        }

        if (forceRefresh) {
            forceRefresh = false;
            return;
        }

        if (gC.isWinGame() || gC.isLoseGame()) {
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException ex) {
                Logger.getLogger(Play.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.exit(0);
        }

        if (gC.isUpdateBetValues()) {
            drawValueBet = gC.getDiceAmountSelected();
            gC.setUpdateBetValues(false);
        }

        newGame = false;

        board.gameLoop(board, board.getCurrentPlayers().getVectorPlayers()[gC.getId()]);

        ///////////////////////////////////////////////////////////////////
        Input input = gc.getInput();

        if (input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) {

            getX = Mouse.getX();
            getY = abs(0 - Mouse.getY());

            if ((getX >= 0 && getX <= 100) && (getY >= 0 && getY <= 100)) {
                gC.printValues();
            }

            if (gC.makeChoice) {
                if ((getX >= 825 && getX <= 1005) && (getY >= 339 && getY <= 389) && !gC.isBetMax) { //Make bet
                    gC.setMakeBetClicked(true);
                }

                if ((getX >= 825 && getX <= 1005) && (getY >= 249 && getY <= 299)) { //doubt
                    gC.setDoubtClicked(true);
                }
            } else {
                ////////////////////////////////////// Arrows

                if ((getX >= 697 && getX <= 747) && (getY >= 332 && getY <= 389)) { //left arrow Die
                    clickToChangeDie = true;
                    drawDieBet -= 1;
                    if (drawDieBet < 1) {
                        drawDieBet = 6;
                    }
                }
                if ((getX >= 835 && getX <= 889) && (getY >= 332 && getY <= 389)) { //right arrow Die
                    clickToChangeDie = true;
                    drawDieBet += 1;
                    if (drawDieBet > 6) {
                        drawDieBet = 1;
                    }
                }
                if ((getX >= 929 && getX <= 985) && (getY >= 332 && getY <= 389)) { //left arrow Value
                    clickToChangeValue = true;
                    drawValueBet -= 1;
                    if (gC.getDiceAmountSelected() == 0) {
                        if (drawValueBet < 1) {
                            drawValueBet = sumOf(gC.totalDicePlayer); 
                        }
                    } else if (drawValueBet < gC.getDiceAmountSelected()) {
                        drawValueBet = sumOf(gC.totalDicePlayer); 
                    }
                }
                if ((getX >= 1055 && getX <= 1111) && (getY >= 332 && getY <= 389)) { //right arrow Value
                    clickToChangeValue = true;
                    drawValueBet += 1;

                    if (drawValueBet > sumOf(gC.totalDicePlayer)) {
                        if (gC.getDiceAmountSelected() == 0) {
                            drawValueBet = 1;
                        } else {
                            drawValueBet = gC.getDiceAmountSelected();
                        }

                    }
                }
                //////////////////////////////////////
                ///////////////////////////////////// Buttons
                if ((getX >= 825 && getX <= 1005) && (getY >= 249 && getY <= 299)) { //bet
                    gC.setBetClicked(true);
                    lbDrawDieBet = drawDieBet;
                    lbDrawValueBet = drawValueBet;
                    gC.setDiceValueSelected(drawDieBet);
                    gC.setDiceAmountSelected(drawValueBet);

                }
            }
        }
    }

    private void restartInitBoard() {

        if (gC.isRestartBoard()) {

            for (int s = 0; s < gC.getBoard().getnPlayers(); s++) {
                System.arraycopy(positionPlayerDice[s], 0, oldPlayerDice[s], 0, positionPlayerDice[s].length);
                for (int i = 0; i < 5; i++) {
                    positionPlayerDice[s][i] = 0;
                }
                //System.out.println("old dice: "+ s + Arrays.toString(oldPlayerDice[s]));
            }
            

            if ((this.drawValueBet = gC.getDiceAmountSelected()) == 0) {
                drawValueBet = 1;
            }

            if ((this.drawDieBet = gC.getDiceValueSelected()) == 0) {
                drawDieBet = 1;
            }

            gC.isBetMax = false;
            gC.setRestartBoard(false);

            forceRefresh = true;
        }

        forceRefresh = true;

        newTurn = true; // Start delle animazioni e del pannello del nuovo turno
        gC.setInitBoard(false);

        id = gC.getBoard().getMyID();

        nPlayers = gC.getBoard().getnPlayers();
        gC.setnPlayers(nPlayers);

        gC.totalDicePlayer = new int[nPlayers];

        for (int i = 0; i < nPlayers; i++) {
            if (gC.getBoard().getCurrentPlayers().getVectorPlayers()[i].isPlayerOut()) {
                gC.totalDicePlayer[i] = 0;
            } else {
                gC.totalDicePlayer[i] = gC.getBoard().getCurrentPlayers().getVectorPlayers()[i].getMyDiceObject().getnDice();
            }
        }

        for (int s = 0; s < nPlayers; s++) {

            amountDicePlayers = gC.getBoard().getCurrentPlayers().getVectorPlayers()[s].getmyDiceValue();
            for (int i = 0; i < amountDicePlayers.length; i++) {
                positionPlayerDice[s][i] = amountDicePlayers[i];
            }
        }
    }

    private void drawSelectorPlayer() {
        selectorPosition[0][0] = 230;
        selectorPosition[0][1] = Main.ySize - 240;
        selectorPosition[1][0] = 680;
        selectorPosition[1][1] = selectorPosition[0][1];
        selectorPosition[2][0] = 1127;
        selectorPosition[2][1] = Main.ySize - 388;
        selectorPosition[3][0] = selectorPosition[2][0];
        selectorPosition[3][1] = Main.ySize - 762;
        selectorPosition[4][0] = selectorPosition[1][0];
        selectorPosition[4][1] = Main.ySize - 762;
        selectorPosition[5][0] = selectorPosition[0][0];
        selectorPosition[5][1] = Main.ySize - 762;
        selectorPosition[6][0] = 3;
        selectorPosition[6][1] = selectorPosition[3][1];
        selectorPosition[7][0] = 3;
        selectorPosition[7][1] = selectorPosition[2][1];

        if (gC.getPlayingPlayer() == 0 || gC.getPlayingPlayer() == 1 || gC.getPlayingPlayer() == 4 || gC.getPlayingPlayer() == 5) {
            guiDefImg.getSelectedPlayerHoriz().draw(selectorPosition[gC.getPlayingPlayer()][0], selectorPosition[gC.getPlayingPlayer()][1]);
        } else {
            guiDefImg.getSelectedPlayerVert().draw(selectorPosition[gC.getPlayingPlayer()][0], selectorPosition[gC.getPlayingPlayer()][1]);
        }

    }

    private void drawPlayerName() {

        playerNamePosition[0][0] = 408;
        playerNamePosition[0][1] = Main.ySize - 226;
        playerNamePosition[1][0] = 865;
        playerNamePosition[1][1] = playerNamePosition[0][1];
        playerNamePosition[2][0] = 1143;
        playerNamePosition[2][1] = Main.ySize - 241;
        playerNamePosition[3][0] = playerNamePosition[2][0];
        playerNamePosition[3][1] = Main.ySize - 628;
        playerNamePosition[4][0] = playerNamePosition[1][0];
        playerNamePosition[4][1] = Main.ySize - 568;
        playerNamePosition[5][0] = playerNamePosition[0][0];
        playerNamePosition[5][1] = playerNamePosition[4][1];
        playerNamePosition[6][0] = 199;
        playerNamePosition[6][1] = playerNamePosition[2][1];
        playerNamePosition[7][0] = playerNamePosition[6][0];
        playerNamePosition[7][1] = playerNamePosition[3][1];

        for (int i = 0; i < nPlayers; i++) {
            switch (i) {
                case 2:
                    guiDefImg.player2Text.draw(playerNamePosition[2][0], playerNamePosition[2][1]);
                    break;
                case 3:
                    guiDefImg.player3Text.draw(playerNamePosition[3][0], playerNamePosition[3][1]);
                    break;
                case 6:
                    guiDefImg.player6Text.draw(playerNamePosition[6][0], playerNamePosition[6][1]);
                    break;
                case 7:
                    guiDefImg.player7Text.draw(playerNamePosition[7][0], playerNamePosition[7][1]);
                    break;
                default:
                    gDefFont.getFontTurn().drawString(playerNamePosition[i][0], playerNamePosition[i][1], "Player " + i, Color.black);
                    break;
            }
        }
    }

    private void showPlayerDiceOneTwo(int iterI, int iterJ) {
        if (cnt <= 1) {
            guiDefImg.getBoxDiceHoriz().draw(dimXHor, Main.ySize - 213);
            if (dimXHor < 690) {
                dimXHor += 450;
            } else {
                dimXHor = 240;
            }
            cnt++;
        }
        if (iterJ < 3) {
            dice.get(oldPlayerDice[iterI][0]).draw(positionDice[iterI][0], Main.ySize - 187);
            dice.get(oldPlayerDice[iterI][1]).draw(positionDice[iterI][0] + 171, Main.ySize - 187);
            dice.get(oldPlayerDice[iterI][2]).draw(positionDice[iterI][0] + (171 * 2), Main.ySize - 187);
        } else {
            dice.get(oldPlayerDice[iterI][3]).draw(positionDice[iterI][1], Main.ySize - 97);
            dice.get(oldPlayerDice[iterI][4]).draw(positionDice[iterI][1] + 173, Main.ySize - 97);
        }
    }

    private void showPlayerDiceThreeFour(int iterI, int iterJ) {
        if (cnt <= 3) {
            guiDefImg.getBoxDiceVert().draw(1153, Main.ySize - dimYVer);
            dimYVer += 374;
            dimXHor = 240;
            cnt++;
        }
        if (iterJ < 3) {
            dice.get(oldPlayerDice[iterI][0]).draw(1175, positionDice[iterI][0]);
            dice.get(oldPlayerDice[iterI][1]).draw(1175, positionDice[iterI][0] - 134);
            dice.get(oldPlayerDice[iterI][2]).draw(1175, positionDice[iterI][0] - (134 * 2));
        } else {
            dice.get(oldPlayerDice[iterI][3]).draw(1265, positionDice[iterI][1]);
            dice.get(oldPlayerDice[iterI][4]).draw(1265, positionDice[iterI][1] - 136);
        }

    }

    private void showPlayerDiceFiveSix(int iterI, int iterJ) {
        if (cnt <= 5) {
            guiDefImg.getBoxDiceHoriz().draw(dimXHor, Main.ySize - 768);
            dimXHor += 450;
            dimYVer = 377;
            cnt++;
        }
        if (iterJ < 3) {
            dice.get(oldPlayerDice[iterI][0]).draw(positionDice[iterI][0], Main.ySize - 743);
            dice.get(oldPlayerDice[iterI][1]).draw(positionDice[iterI][0] + 171, Main.ySize - 743);
            dice.get(oldPlayerDice[iterI][2]).draw(positionDice[iterI][0] + (171 * 2), Main.ySize - 743);
        } else {
            dice.get(oldPlayerDice[iterI][3]).draw(positionDice[iterI][1], Main.ySize - 653);
            dice.get(oldPlayerDice[iterI][4]).draw(positionDice[iterI][1] + 173, Main.ySize - 653);

        }

    }

    private void showPlayerDiceSevenEight(int iterI, int iterJ) {
        if (cnt <= 7) {
            guiDefImg.getBoxDiceVert().setRotation(180);
            guiDefImg.getBoxDiceVert().draw(0, Main.ySize - dimYVer);
            guiDefImg.getBoxDiceVert().setRotation(0);
            dimYVer += 374;
        }
        if (iterJ<3){
            dice.get(oldPlayerDice[iterI][0]).draw(115, positionDice[iterI][0]);
            dice.get(oldPlayerDice[iterI][1]).draw(115, positionDice[iterI][0]-134);
            dice.get(oldPlayerDice[iterI][2]).draw(115, positionDice[iterI][0] - (134*2));
        }
        else{
            dice.get(oldPlayerDice[iterI][3]).draw(25, positionDice[iterI][1]);
            dice.get(oldPlayerDice[iterI][4]).draw(25, positionDice[iterI][1]-136);
        }
    }

    private void drawPlayerOneTwo(int iterI, int iterJ) {
        if (cnt <= 1) {
            guiDefImg.getBoxDiceHoriz().draw(dimXHor, Main.ySize - 213);
            if (dimXHor < 690) {
                dimXHor += 450;
            } else {
                dimXHor = 240;
            }
            cnt++;
        }

        if (iterI == gC.getId()) {
            if (iterJ < 3) { //Fila sopra
                if (gC.playDiceAnimation == true && !(gC.isWinGame() || gC.isLoseGame())) {
                    if (gC.dicePlayer[0] == 1) {
                        animationDie1.draw(positionDice[iterI][0], Main.ySize - 187);
                    } else {
                        dice.get(0).draw(positionDice[iterI][0], Main.ySize - 187);
                    }
                    if (gC.dicePlayer[1] == 1) {
                        animationDie2.draw(positionDice[iterI][0] + 171, Main.ySize - 187);
                    } else {
                        dice.get(0).draw(positionDice[iterI][0] + 171, Main.ySize - 187);
                    }
                    if (gC.dicePlayer[2] == 1) {
                        animationDie3.draw(positionDice[iterI][0] + (171 * 2), Main.ySize - 187);
                    } else {
                        dice.get(0).draw(positionDice[iterI][0] + (171 * 2), Main.ySize - 187);
                    }
                } else {
                    if (positionPlayerDice[iterI][iterJ] == 1 && gC.isOneJollyEnabled()) {
                        dice.get(8).draw(positionDice[iterI][0], Main.ySize - 187);
                    } else {
                        dice.get(positionPlayerDice[iterI][iterJ]).draw(positionDice[iterI][0], Main.ySize - 187);
                    }

                    positionDice[iterI][0] += 171;
                }

            } else if (gC.playDiceAnimation == true) { //Fila sotto

                if (gC.dicePlayer[3] == 1) {
                    animationDie4.draw(positionDice[iterI][1], Main.ySize - 97);
                } else {
                    dice.get(0).draw(positionDice[iterI][1], Main.ySize - 97);
                }

                if (gC.dicePlayer[4] == 1) {
                    animationDie5.draw(positionDice[iterI][1] + 173, Main.ySize - 97);
                } else {
                    dice.get(0).draw(positionDice[iterI][1] + 173, Main.ySize - 97);
                }

            } else {

                if (positionPlayerDice[iterI][iterJ] == 1 && gC.isOneJollyEnabled()) {
                    dice.get(8).draw(positionDice[iterI][1], Main.ySize - 97);
                } else {
                    dice.get(positionPlayerDice[iterI][iterJ]).draw(positionDice[iterI][1], Main.ySize - 97);
                }

                positionDice[iterI][1] += 173;
            }
        } else if (iterJ < 3) {
            if (countDice[iterI] < gC.totalDicePlayer[iterI]) {
                dice.get(7).draw(positionDice[iterI][0], Main.ySize - 187);
                positionDice[iterI][0] += 171;
                countDice[iterI]++;
            } else {
                dice.get(0).draw(positionDice[iterI][0], Main.ySize - 187);
                positionDice[iterI][0] += 171;
            }

        } else if (countDice[iterI] < gC.totalDicePlayer[iterI]) {
            dice.get(7).draw(positionDice[iterI][1], Main.ySize - 97);
            positionDice[iterI][1] += 173;
            countDice[iterI]++;
        } else {
            dice.get(0).draw(positionDice[iterI][1], Main.ySize - 97);
            positionDice[iterI][1] += 173;
        }
    }

    private void drawPlayerThreeFour(int iterI, int iterJ) {
        if (cnt <= 3) {
            guiDefImg.getBoxDiceVert().draw(1153, Main.ySize - dimYVer);
            dimYVer += 374;
            dimXHor = 240;
            cnt++;
        }
        if (iterI == gC.getId()) {
            if (iterJ < 3) {
                if (gC.playDiceAnimation == true && !(gC.isWinGame() || gC.isLoseGame())) {
                    if (gC.dicePlayer[0] == 1) {
                        animationDie1.draw(1175, positionDice[iterI][0]);
                    } else {
                        dice.get(0).draw(1175, positionDice[iterI][0]);
                    }
                    if (gC.dicePlayer[1] == 1) {
                        animationDie2.draw(1175, positionDice[iterI][0] - 134);
                    } else {
                        dice.get(0).draw(1175, positionDice[iterI][0] - 134);
                    }
                    if (gC.dicePlayer[2] == 1) {
                        animationDie3.draw(1175, positionDice[iterI][0] - (134 * 2));
                    } else {
                        dice.get(0).draw(1175, positionDice[iterI][0] - (134 * 2));
                    }

                } else {
                    if (positionPlayerDice[iterI][iterJ] == 1 && gC.isOneJollyEnabled()) {
                        dice.get(8).draw(1175, positionDice[iterI][0]);
                    } else {
                        dice.get(positionPlayerDice[iterI][iterJ]).draw(1175, positionDice[iterI][0]);
                    }
                    positionDice[iterI][0] -= 134;
                }
            } else if (gC.playDiceAnimation == true) {

                if (gC.dicePlayer[3] == 1) {
                    animationDie4.draw(1265, positionDice[iterI][1]);
                } else {
                    dice.get(0).draw(1265, positionDice[iterI][1]);
                }

                if (gC.dicePlayer[4] == 1) {
                    animationDie5.draw(1265, positionDice[iterI][1] - 136);
                } else {
                    dice.get(0).draw(1265, positionDice[iterI][1] - 136);
                }

            } else {
                if (positionPlayerDice[iterI][iterJ] == 1 && gC.isOneJollyEnabled()) {
                    dice.get(8).draw(1265, positionDice[iterI][1]);
                } else {
                    dice.get(positionPlayerDice[iterI][iterJ]).draw(1265, positionDice[iterI][1]);
                }
                positionDice[iterI][1] -= 136;
            }
        } else if (iterJ < 3) {
            if (countDice[iterI] < gC.totalDicePlayer[iterI]) {
                dice.get(7).draw(1175, positionDice[iterI][0]);
                countDice[iterI]++;
            } else {
                dice.get(0).draw(1175, positionDice[iterI][0]);
            }
            positionDice[iterI][0] -= 134;

        } else {
            if (countDice[iterI] < gC.totalDicePlayer[iterI]) {
                dice.get(7).draw(1265, positionDice[iterI][1]);
                countDice[iterI]++;
            } else {
                dice.get(0).draw(1265, positionDice[iterI][1]);
            }
            positionDice[iterI][1] -= 136;
        }
    }

    private void drawPlayerFiveSix(int iterI, int iterJ) {
        if (cnt <= 5) {
            guiDefImg.getBoxDiceHoriz().draw(dimXHor, Main.ySize - 768);
            dimXHor += 450;
            dimYVer = 377;
            cnt++;
        }
        if (iterI == gC.getId()) {
            if (iterJ < 3) {
                if (gC.playDiceAnimation == true && !(gC.isWinGame() || gC.isLoseGame())) {

                    if (gC.dicePlayer[0] == 1) {
                        animationDie1.draw(positionDice[iterI][0], Main.ySize - 743);
                    } else {
                        dice.get(0).draw(positionDice[iterI][0], Main.ySize - 743);
                    }
                    if (gC.dicePlayer[1] == 1) {
                        animationDie2.draw(positionDice[iterI][0] + 171, Main.ySize - 743);
                    } else {
                        dice.get(0).draw(positionDice[iterI][0] + 171, Main.ySize - 743);
                    }
                    if (gC.dicePlayer[2] == 1) {
                        animationDie3.draw(positionDice[iterI][0] + (171 * 2), Main.ySize - 743);
                    } else {
                        dice.get(0).draw(positionDice[iterI][0] + (171 * 2), Main.ySize - 743);
                    }

                } else {
                    if (positionPlayerDice[iterI][iterJ] == 1 && gC.isOneJollyEnabled()) {
                        dice.get(8).draw(positionDice[iterI][0], Main.ySize - 743);
                    } else {
                        dice.get(positionPlayerDice[iterI][iterJ]).draw(positionDice[iterI][0], Main.ySize - 743);
                    }
                    positionDice[iterI][0] += 171;
                }
            } else if (gC.playDiceAnimation == true) {
                if (gC.dicePlayer[3] == 1) {
                    animationDie4.draw(positionDice[iterI][1], Main.ySize - 653);
                } else {
                    dice.get(0).draw(positionDice[iterI][1], Main.ySize - 653);
                }
                if (gC.dicePlayer[4] == 1) {
                    animationDie5.draw(positionDice[iterI][1] + 173, Main.ySize - 653);
                } else {
                    dice.get(0).draw(positionDice[iterI][1] + 173, Main.ySize - 653);
                }
            } else {
                if (positionPlayerDice[iterI][iterJ] == 1 && gC.isOneJollyEnabled()) {
                    dice.get(8).draw(positionDice[iterI][1], Main.ySize - 653);
                } else {
                    dice.get(positionPlayerDice[iterI][iterJ]).draw(positionDice[iterI][1], Main.ySize - 653);
                }
                positionDice[iterI][1] += 173;
            }
        } else if (iterJ < 3) {
            if (countDice[iterI] < gC.totalDicePlayer[iterI]) {
                dice.get(7).draw(positionDice[iterI][0], Main.ySize - 743);
                countDice[iterI]++;
            } else {
                dice.get(0).draw(positionDice[iterI][0], Main.ySize - 743);
            }
            positionDice[iterI][0] += 171;

        } else {
            if (countDice[iterI] < gC.totalDicePlayer[iterI]) {
                dice.get(7).draw(positionDice[iterI][1], Main.ySize - 653);
                countDice[iterI]++;
            } else {
                dice.get(0).draw(positionDice[iterI][1], Main.ySize - 653);
            }
            positionDice[iterI][1] += 173;

        }

    }

    private void drawPlayerSevenEight(int iterI, int iterJ) {
        if (cnt <= 7) {
            guiDefImg.getBoxDiceVert().setRotation(180);
            guiDefImg.getBoxDiceVert().draw(0, Main.ySize - dimYVer);
            guiDefImg.getBoxDiceVert().setRotation(0);
            dimYVer += 374;
        }
        if (iterI == gC.getId()) {
            if (iterJ < 3) {

                if (gC.playDiceAnimation == true) {
                    if (gC.dicePlayer[0] == 1) {
                        animationDie1.draw(115, positionDice[iterI][0]);
                    } else {
                        dice.get(0).draw(115, positionDice[iterI][0]);
                    }
                    if (gC.dicePlayer[1] == 1) {
                        animationDie2.draw(115, positionDice[iterI][0] - 134);
                    } else {
                        dice.get(0).draw(115, positionDice[iterI][0] - 134);
                    }
                    if (gC.dicePlayer[2] == 1) {
                        animationDie3.draw(115, positionDice[iterI][0] - (134 * 2));
                    } else {
                        dice.get(0).draw(115, positionDice[iterI][0] - (134 * 2));
                    }
                } else {
                    if (positionPlayerDice[iterI][iterJ] == 1 && gC.isOneJollyEnabled()) {
                        dice.get(8).draw(115, positionDice[iterI][0]);
                    } else {
                        dice.get(positionPlayerDice[iterI][iterJ]).draw(115, positionDice[iterI][0]);
                    }
                    positionDice[iterI][0] -= 134;
                }
            } else if (gC.playDiceAnimation == true) {
                if (gC.dicePlayer[3] == 1) {
                    animationDie4.draw(25, positionDice[iterI][1]);
                } else {
                    dice.get(0).draw(25, positionDice[iterI][1]);
                }

                if (gC.dicePlayer[4] == 1) {
                    animationDie5.draw(25, positionDice[iterI][1] - 136);
                } else {
                    dice.get(0).draw(25, positionDice[iterI][1] - 136);
                }
            } else {
                if (positionPlayerDice[iterI][iterJ] == 1 && gC.isOneJollyEnabled()) {
                    dice.get(8).draw(25, positionDice[iterI][1]);
                } else {
                    dice.get(positionPlayerDice[iterI][iterJ]).draw(25, positionDice[iterI][1]);
                }
                positionDice[iterI][1] -= 136;
            }
        } else if (iterJ < 3) {
            if (countDice[iterI] < gC.totalDicePlayer[iterI]) {
                dice.get(7).draw(115, positionDice[iterI][0]);
                countDice[iterI]++;
            } else {
                dice.get(0).draw(115, positionDice[iterI][0]);
            }
            positionDice[iterI][0] -= 134;
        } else {
            if (countDice[iterI] < gC.totalDicePlayer[iterI]) {
                dice.get(7).draw(25, positionDice[iterI][1]);
                countDice[iterI]++;
            } else {
                dice.get(0).draw(25, positionDice[iterI][1]);
            }
            positionDice[iterI][1] -= 136;

        }

    }

    @SuppressWarnings("empty-statement")
    public static int sumOf(int... integers) {
        int total = 0;
        for (int i = 0; i < integers.length; total += integers[i++]);
        return total;
    }

    @Override
    public int getID() {
        return 2;
    }

    public void setBoard(Board board) {
        this.board = board;
        gC.playDiceAnimation = true;
        
        gC.setTimeMin(3000);
        gC.setTimeMax(5000);
        
        time = 0;
        timeCheckCrash = 0;
    }
}
