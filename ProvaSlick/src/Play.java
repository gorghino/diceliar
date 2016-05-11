
import java.rmi.RemoteException;
import java.util.ArrayList;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.*;
import org.newdawn.slick.state.*;
import org.newdawn.slick.GameContainer;
import static java.lang.Math.abs;
import static org.lwjgl.opengl.GL11.glRotatef;

/**
 *
 * @author proietfb
 */
public class Play extends BasicGameState {

    Image background,
            boxDiceHoriz,
            boxDiceVert,
            arrowLeft,
            arrowRight,
            selectedPlayerHoriz,
            selectedPlayerVer,
            button;

    TrueTypeFont fontValue, fontTurn;

    UnicodeFont rotatedFont;

    ArrayList<Image> dice = new ArrayList<>();

    Animation animationDie1,
            animationDie2,
            animationDie3,
            animationDie4,
            animationDie5;

    int[][] positionPlayerDice, positionDice, playerNamePosition;
    int[] amountDicePlayers;

    int getX, getY;

    int nPlayers,
            id = 0,
            cnt,
            updateDelta;

    int drawDieBet,
            drawValueBet,
            lbDrawDieBet,
            lbDrawValueBet;
    
    int dimXHor, dimYVer;

    private boolean clickToChangeDie = false,
            clickToChangeValue = false,
            playDiceAnimation = true;

    Board board;
    DiceLiar dl;

    GUIController gC;
    GuiDefineImages guiDefImg;
    GuiDefineButtons gDrawButtons;
    GuiDefineFont gDefFont;

    public Play(GUIController _gC, GuiDefineImages _guiDefImg, GuiDefineButtons _gDrawButtons, GuiDefineFont _gDefFont) {
        this.gC = _gC;
        this.guiDefImg = _guiDefImg;
        this.gDrawButtons = _gDrawButtons;
        this.gDefFont = _gDefFont;
    }

    @Override
    public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {

        background = guiDefImg.getGuiBoard();
        boxDiceHoriz = guiDefImg.getBoxDiceHoriz();
        boxDiceVert = guiDefImg.getBoxDiceVert();
        arrowLeft = guiDefImg.getArrowLeft();
        arrowRight = guiDefImg.getArrowRight();
        selectedPlayerHoriz = guiDefImg.getSelectedPlayerHoriz();
        selectedPlayerVer = guiDefImg.getSelectedPlayerVert();
        button = guiDefImg.getButton();
        dice = guiDefImg.getArrayDice();

        nPlayers = 8;

        positionPlayerDice = new int[nPlayers][5];
        positionDice = new int[nPlayers][2];
        playerNamePosition = new int[nPlayers][2];

        drawDieBet = 1;
        drawValueBet = 1;

        fontValue = gDefFont.getFontValue();
        fontTurn = gDefFont.getFontTurn();

        rotatedFont = gDefFont.getRotatedFont();

        Image[] anDie1 = {dice.get(5), dice.get(3), dice.get(4), dice.get(1), dice.get(3), dice.get(2)};
        Image[] anDie2 = {dice.get(6), dice.get(1), dice.get(5), dice.get(4), dice.get(2), dice.get(3)};
        Image[] anDie3 = {dice.get(2), dice.get(6), dice.get(3), dice.get(5), dice.get(4), dice.get(1)};
        Image[] anDie4 = {dice.get(4), dice.get(2), dice.get(1), dice.get(3), dice.get(5), dice.get(6)};
        Image[] anDie5 = {dice.get(2), dice.get(5), dice.get(6), dice.get(4), dice.get(1), dice.get(3)};

        animationDie1 = new Animation(anDie1, 100, true);
        animationDie2 = new Animation(anDie2, 100, true);
        animationDie3 = new Animation(anDie3, 100, true);
        animationDie4 = new Animation(anDie4, 100, true);
        animationDie5 = new Animation(anDie5, 100, true);
    }

    @Override
    public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException { // run this every frame to update game logic

        background.draw(0, 0);

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
        
        playerNamePosition[0][0]=408;
        playerNamePosition[0][1]=Main.ySize-226;
        playerNamePosition[1][0]= 859;
        playerNamePosition[1][1]= playerNamePosition[0][1]; 
//        playerNamePosition[2][0]=;
//        playerNamePosition[2][1]=;
//        playerNamePosition[3][0]=;
//        playerNamePosition[3][1]=;
//        playerNamePosition[4][0]=;
//        playerNamePosition[4][1]=;
//        playerNamePosition[5][0]=;
//        playerNamePosition[5][1]=;
//        playerNamePosition[6][0]=;
//        playerNamePosition[6][1]=;
//        playerNamePosition[7][0]=;
//        playerNamePosition[7][1]=;
        
        
        rotatedFont.drawString(playerNamePosition[1][0], playerNamePosition[1][1], "Player 8", Color.black);

        selectedPlayerHoriz.draw(230, Main.ySize - 240);//solo prova

        cnt = 0;

        //DRAW PLAYERS
        dimXHor = 240;
        dimYVer = 377;


        for (int i = 0; i < nPlayers; i++) {
            for (int j = 0; j < 5; j++) {
                if (i <= 1) {
                    drawPlayerOneTwo(i, j);
                } else if (i > 1 && i < 4) {
                    drawPlayerThreeFour(i, j);
                } else if (i > 3 && i < 6) {
                    drawPlayerFiveSix(i, j);
                } else {
                    drawPlayerSevenEight(i, j);
                }
            }
        }

        fontTurn.drawString(628, Main.ySize - 522, "Turn: ", Color.white);
        fontTurn.drawString(701, Main.ySize - 522, "" + gC.getTurn(), Color.black);
        
        fontTurn.drawString(850, Main.ySize-471, "Panel Bet", Color.black);

        //PANEL DX
        if (gC.makeChoice && gC.getTurn() > 1 && id == getBoard().getPlayingPlayer().myID) {
            //button Doubt
            gDrawButtons.drawButton(825, Main.ySize - 390, GuiDefineButtons.buttonWidth, GuiDefineButtons.buttonHeigh, 878, Main.ySize - 375, "Make Bet", Color.white);
            //button Doubt
            gDrawButtons.drawButton(825, Main.ySize - 300, GuiDefineButtons.buttonWidth, GuiDefineButtons.buttonHeigh, 885, Main.ySize - 285, "Doubt", Color.white);

        } else if (id == getBoard().getPlayingPlayer().myID) {
           
            //button Bet
            gDrawButtons.drawButton(825, Main.ySize - 300, GuiDefineButtons.buttonWidth, GuiDefineButtons.buttonHeigh, 902, Main.ySize - 285, "Bet", Color.white);

            //Draw arrows
            g.drawImage(arrowLeft, 695, 380);
            g.drawImage(arrowRight, 835, 380);
            g.drawImage(arrowLeft, 932, 380);
            g.drawImage(arrowRight, 1055, 380);

            //draw dice for bet
            if (drawValueBet == 1 || drawDieBet == 1) {
                fontValue.drawString(1005, 385, "" + drawValueBet, Color.black);
                g.drawImage(dice.get(1), 752, 371);
            }

            if (clickToChangeDie) {
                g.drawImage(dice.get(drawDieBet), 752, 371);
            }

            if (clickToChangeValue == true) {
                if (drawValueBet < 10) {
                    fontValue.drawString(1005, 385, "" + drawValueBet, Color.black);
                } else {
                    fontValue.drawString(995, 385, "" + drawValueBet, Color.black);
                }
            }

        }
        //button leave
        gDrawButtons.drawButton(355, Main.ySize - 300, GuiDefineButtons.buttonWidth, GuiDefineButtons.buttonHeigh, 416, Main.ySize - 285, "Leave", Color.white);

        /////////////////////
        //////////////////// Panel SX
        if (gC.betOnTable == true) {
            g.setColor(Color.black);
            g.drawString("Bet made by player " + gC.idLastBet, 325, Main.ySize - 410);
            g.drawString(gC.getDiceAmountSelected() + " dice that value ", 325, Main.ySize - 370);
            g.drawImage(dice.get(gC.getDiceValueSelected()), 527, Main.ySize - 408);
        }

        g.setColor(Color.black);
        g.drawString("" + getX, 50, 70);
        g.drawString("" + getY, 50, 90);

    }

    @Override
    public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException { // run this every frame to display graphics to the player

        updateDelta += delta;

        if (updateDelta >= 6000) {
            playDiceAnimation = false;
        }

        if (gC.initBoard == true || gC.restartBoard == true) {
            restartInitBoard();
        }

        ///////////////////// CHECK GAME STATE ////////////////////////////////
        try {
            board.gameLoop(board, board.getCurrentPlayers().vectorPlayers[id]);
        } catch (RemoteException ex) {
        }

        ///////////////////////////////////////////////////////////////////
        Input input = gc.getInput();
        if (gc.getInput().isKeyPressed(Input.KEY_1)) {
            System.exit(0);
        }

        if (input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) {

            getX = Mouse.getX();
            getY = abs(0 - Mouse.getY());
            
            if ((getX >= 0 && getX <= 100) && (getY >= 0 && getY <= 100)) {
                gC.printValues();
            }

            if (gC.makeChoice) {
                if ((getX >= 825 && getX <= 1005) && (getY >= 339 && getY <= 389)) { //Make bet
                    gC.setMakeBetClicked(true);
                }

                if ((getX >= 825 && getX <= 1005) && (getY >= 249 && getY <= 299)) { //doubt
                    gC.setDoubtClicked(true);
                    System.out.println("Dubito!");
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
                    if (drawValueBet < 1) {
                        drawValueBet = nPlayers * gC.totalDicePlayer; //Temporaneo, devo sapere quanti dadi ogni giocare ha dopo ogni scommessa
                    }
                }
                if ((getX >= 1055 && getX <= 1111) && (getY >= 332 && getY <= 389)) { //right arrow Value
                    clickToChangeValue = true;
                    drawValueBet += 1;
                    if (drawValueBet > nPlayers * 5) {
                        drawValueBet = 1;
                    }
                }

                //////////////////////////////////////
                ///////////////////////////////////// Buttons
                if ((getX >= 825 && getX <= 1005) && (getY >= 249 && getY <= 299)) { //bet
                    gC.setBetClicked(true);
                    lbDrawDieBet = drawDieBet;
                    lbDrawValueBet = drawValueBet;

                    board.betDone = true;

                    gC.setDiceValueSelected(drawDieBet);
                    gC.setDiceAmountSelected(drawValueBet);

                }
            }
            if ((getX >= 355 && getX <= 535) && (getY >= 249 && getY <= 299)) { //leave
                gC.setLeaveClicked(true);
                System.exit(0);
            }
        }
    }
    
    private void restartInitBoard(){
        if(gC.restartBoard){
            for (int s = 0; s < nPlayers; s++)
                 for (int i = 0; i < 5; i++)
                     positionPlayerDice[s][i] = 0;
            gC.restartBoard = false;
        }
        
        gC.initBoard = false;

        id = getBoard().myID;
        gC.setId(id);

        nPlayers = getBoard().getnPlayers();
        gC.setnPlayers(nPlayers);

        for (int s = 0; s < nPlayers; s++) {
            amountDicePlayers = getBoard().getCurrentPlayers().getVectorPlayers()[s].getmyDiceValue();
            System.out.println("Player: " + s + " Lenght: " + amountDicePlayers.length);
            for (int i = 0; i < amountDicePlayers.length; i++) {
                positionPlayerDice[s][i] = amountDicePlayers[i];
                System.out.println("player " + s + " dice: " + positionPlayerDice[s][i]);
            }
        }
    }

    private void drawPlayerOneTwo(int iterI, int iterJ) {
        if (cnt <= 1) {
            boxDiceHoriz.draw(dimXHor, Main.ySize - 213);
            fontTurn.drawString(408, Main.ySize-226, "Player "+iterI, Color.black);
            if (dimXHor < 690) {
                dimXHor += 450;
            } else {
                dimXHor = 240;
            }
            cnt++;
        }
        if (iterI == id) {
            if (iterJ < 3) {
                if (playDiceAnimation == true) {
                    animationDie1.draw(positionDice[iterI][0], Main.ySize - 187);
                    animationDie2.draw(positionDice[iterI][0] + 171, Main.ySize - 187);
                    animationDie3.draw(positionDice[iterI][0] + (171 * 2), Main.ySize - 187);
                } else {
                    //System.out.println(positionPlayerDice[iterI][iterJ] + " - " + gC.oneJollyEnabled);
                    if(positionPlayerDice[iterI][iterJ] == 1 && gC.oneJollyEnabled)
                        dice.get(8).draw(positionDice[iterI][0], Main.ySize - 187);
                    else
                        dice.get(positionPlayerDice[iterI][iterJ]).draw(positionDice[iterI][0], Main.ySize - 187);

                    positionDice[iterI][0] += 171;
                }

            } else if (playDiceAnimation == true) {
                animationDie4.draw(positionDice[iterI][1], Main.ySize - 97);
                animationDie5.draw(positionDice[iterI][1] + 173, Main.ySize - 97);
            } else {
                if(positionPlayerDice[iterI][iterJ] == 1 && gC.oneJollyEnabled)
                     dice.get(8).draw(positionDice[iterI][1], Main.ySize - 97);
                else
                    dice.get(positionPlayerDice[iterI][iterJ]).draw(positionDice[iterI][1], Main.ySize - 97);
                
                positionDice[iterI][1] += 173;
            }
        } else if (iterJ < 3) {
            dice.get(7).draw(positionDice[iterI][0], Main.ySize - 187);
            positionDice[iterI][0] += 171;
        } else {
            dice.get(7).draw(positionDice[iterI][1], Main.ySize - 97);
            positionDice[iterI][1] += 173;
        }
    }

    private void drawPlayerThreeFour(int iterI, int iterJ) {
        if (cnt <= 3) {
            boxDiceVert.draw(1153, Main.ySize - dimYVer);
            dimYVer += 374;
            dimYVer = 240;
            cnt++;
        }
        if (iterI == id) {
            if (iterJ < 3) {
                if (playDiceAnimation == true) {
                    animationDie1.draw(1175, positionDice[iterI][0]);
                    animationDie2.draw(1175, positionDice[iterI][0] - 134);
                    animationDie3.draw(1175, positionDice[iterI][0] - (134 * 2));

                } else {
                    dice.get(positionPlayerDice[iterI][iterJ]).setRotation(-90);
                    dice.get(positionPlayerDice[iterI][iterJ]).draw(1175, positionDice[iterI][0]);
                    dice.get(positionPlayerDice[iterI][iterJ]).setRotation(0);
                    positionDice[iterI][0] -= 134;
                }
            } else if (playDiceAnimation == true) {
                animationDie4.draw(1265, positionDice[iterI][1]);
                animationDie5.draw(1265, positionDice[iterI][1] - 136);
            } else {
                dice.get(positionPlayerDice[iterI][iterJ]).setRotation(-90);
                dice.get(positionPlayerDice[iterI][iterJ]).draw(1265, positionDice[iterI][1]);
                dice.get(positionPlayerDice[iterI][iterJ]).setRotation(0);
                positionDice[iterI][1] -= 136;
            }
        } else if (iterJ < 3) {
            dice.get(positionPlayerDice[iterI][iterJ]).setRotation(-90);
            dice.get(7).draw(1175, positionDice[iterI][0]);
            dice.get(positionPlayerDice[iterI][iterJ]).setRotation(0);
            positionDice[iterI][0] -= 134;
        } else {
            dice.get(positionPlayerDice[iterI][iterJ]).setRotation(-90);
            dice.get(7).draw(1265, positionDice[iterI][1]);
            dice.get(positionPlayerDice[iterI][iterJ]).setRotation(0);
            positionDice[iterI][1] -= 136;

        }
    }

    private void drawPlayerFiveSix(int iterI, int iterJ) {
        if (cnt <= 5) {
            boxDiceHoriz.setRotation(180);
            boxDiceHoriz.draw(dimXHor, Main.ySize - 768);
            boxDiceHoriz.setRotation(0);
            dimXHor += 450;
            dimYVer = 377;
            cnt++;
        }
        if (iterI == id) {
            if (iterJ < 3) {
                if (playDiceAnimation == true) {
                    animationDie1.draw(positionDice[iterI][0], Main.ySize - 653);
                    animationDie2.draw(positionDice[iterI][0] + 171, Main.ySize - 653);
                    animationDie3.draw(positionDice[iterI][0] + (171 * 2), Main.ySize - 653);

                } else {
                    dice.get(positionPlayerDice[iterI][iterJ]).setRotation(180);
                    dice.get(positionPlayerDice[iterI][iterJ]).draw(positionDice[iterI][0], Main.ySize - 653);
                    dice.get(positionPlayerDice[iterI][iterJ]).setRotation(0);
                    positionDice[iterI][0] += 171;
                }
            } else if (playDiceAnimation == true) {
                animationDie4.draw(positionDice[iterI][1], Main.ySize - 743);
                animationDie5.draw(positionDice[iterI][1] + 173, Main.ySize - 743);
            } else {
                dice.get(positionPlayerDice[iterI][iterJ]).setRotation(180);
                dice.get(positionPlayerDice[iterI][iterJ]).draw(positionDice[iterI][1], Main.ySize - 743);
                dice.get(positionPlayerDice[iterI][iterJ]).setRotation(0);
                positionDice[iterI][1] += 173;
            }
        } else if (iterJ < 3) {
            dice.get(positionPlayerDice[iterI][iterJ]).setRotation(180);
            dice.get(7).draw(positionDice[iterI][0], Main.ySize - 653);
            dice.get(positionPlayerDice[iterI][iterJ]).setRotation(0);
            positionDice[iterI][0] += 171;
        } else {
            dice.get(positionPlayerDice[iterI][iterJ]).setRotation(180);
            dice.get(7).draw(positionDice[iterI][1], Main.ySize - 743);
            dice.get(positionPlayerDice[iterI][iterJ]).setRotation(0);
            positionDice[iterI][1] += 173;

        }

    }

    private void drawPlayerSevenEight(int iterI, int iterJ) {
        if (cnt <= 7) {
            boxDiceVert.setRotation(180);
            boxDiceVert.draw(0, Main.ySize - dimYVer);
            boxDiceVert.setRotation(0);
            dimYVer += 374;
        }
        if (iterI == id) {
            if (iterJ < 3) {

                if (playDiceAnimation == true) {
                    animationDie1.draw(115, positionDice[iterI][0]);
                    animationDie2.draw(115, positionDice[iterI][0] - 134);
                    animationDie3.draw(115, positionDice[iterI][0] - (134 * 2));

                } else {
                    dice.get(positionPlayerDice[iterI][iterJ]).setRotation(90);
                    dice.get(positionPlayerDice[iterI][iterJ]).draw(115, positionDice[iterI][0]);
                    dice.get(positionPlayerDice[iterI][iterJ]).setRotation(0);
                    positionDice[iterI][0] -= 134;
                }
            } else if (playDiceAnimation == true) {
                animationDie4.draw(25, positionDice[iterI][1]);
                animationDie5.draw(25, positionDice[iterI][1] - 136);
            } else {
                dice.get(positionPlayerDice[iterI][iterJ]).setRotation(90);
                dice.get(positionPlayerDice[iterI][iterJ]).draw(25, positionDice[iterI][1]);
                dice.get(positionPlayerDice[iterI][iterJ]).setRotation(0);
                positionDice[iterI][1] -= 136;
            }
        } else if (iterJ < 3) {
            dice.get(positionPlayerDice[iterI][iterJ]).setRotation(90);
            dice.get(7).draw(115, positionDice[iterI][0]);
            dice.get(positionPlayerDice[iterI][iterJ]).setRotation(0);
            positionDice[iterI][0] -= 134;
        } else {
            dice.get(positionPlayerDice[iterI][iterJ]).setRotation(90);
            dice.get(7).draw(25, positionDice[iterI][1]);
            dice.get(positionPlayerDice[iterI][iterJ]).setRotation(0);
            positionDice[iterI][1] -= 136;

        }

    }

    @Override
    public int getID() {
        return 2;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }
}
