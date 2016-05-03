import static java.lang.Math.abs;
import java.rmi.RemoteException;
import java.util.ArrayList;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.*;
import org.newdawn.slick.state.*;
import org.newdawn.slick.GameContainer;
import java.awt.Font;
/**
 *
 * @author proietfb
 */
public class Play extends BasicGameState {
   
    private int stateID = -1;
    
    Image background,
          boxDiceHoriz,boxDiceHorizRot,
          boxDiceVert,boxDiceVertRot,
          arrowLeft,
          arrowRight,
          selectedPlayerHoriz,
          selectedPlayerVer,
          button;
    
    TrueTypeFont font;
    TrueTypeFont fontTurn;
    ArrayList<Image> dice = new ArrayList<>();
    
    int[][] positionPlayerDice, positionDice;
    int[] amountDicePlayers;
    
    int getX,getY;
    
    int nPlayers,
        initDicePlayer,
        id = 0,
        turn;
    
    
    int drawDieBet,
        drawValueBet,
        lbDrawDieBet,
        lbDrawValueBet;
    
    private boolean clickToChangeDie = false,
                    clickToChangeValue = false,
                    submittedChoice = false,
                    initBoardBool = false;
    Board board;
    DiceLiar dl;
    
    GUIController gC;
    
    public Play(int _stateID, GUIController _gC){
        this.stateID = _stateID;
        this.gC = _gC;
    }
    
    @Override
    public void init(GameContainer gc, StateBasedGame sbg) throws SlickException{ 

        
        background = new Image("img/boardTEMP3.png");
        
        boxDiceHoriz = new Image("img/BoxDiceHoriz.png");
        boxDiceVert = new Image("img/BoxDiceVert.png");
        
        arrowLeft = new Image("img/ArrowLeft.png");
        arrowRight = new Image("img/ArrowRight.png");
        
        selectedPlayerHoriz = new Image("img/SelectedPlayerHoriz.png");
        selectedPlayerVer = new Image("img/SelectedPlayerVert.png");
        
        button = new Image("img/button.png");
        
        dice.add(0, new Image("img/DieDel.png"));
        dice.add(1, new Image("img/Die1.png"));
        dice.add(2, new Image("img/Die2.png"));
        dice.add(3, new Image("img/Die3.png"));
        dice.add(4, new Image("img/Die4.png"));
        dice.add(5, new Image("img/Die5.png"));
        dice.add(6, new Image("img/Die6.png"));
        dice.add(7, new Image("img/DieQM.png"));
        
        turn = 0;
                
        nPlayers = 8;
        initDicePlayer = 5;
        
       // amountDicePlayers = new int[nPlayers][];
        
        positionPlayerDice = new int[nPlayers][initDicePlayer];
        positionDice = new int[nPlayers][2];
        
        drawDieBet=1;
        drawValueBet = 1;
        
        Font awtFont = new Font("Verdana", 0, 50);
        Font awtFontTurn = new Font("Verdana", 0, 35);
        font = new TrueTypeFont(awtFont, true);
        fontTurn = new TrueTypeFont(awtFontTurn, true);
//        textValueDice = new TextField(gc, font, 1000, 376, 60, 60); 
//        textValueDice.setCursorVisible(false);
        
    }
    
    @Override
    public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException{ // run this every frame to update game logic
       
        background.draw(0, 0);

        //DRAW PLAYERS
        int dimXHor = 240,dimYVer = 377;
                
        positionDice[0][0] =249; 
        positionDice[0][1] =334; 
        positionDice[1][0] =699; 
        positionDice[1][1] =784; 
        positionDice[2][0] =Main.ySize-97;  
        positionDice[2][1] =Main.ySize-163; 
        positionDice[3][0] =Main.ySize-470;  
        positionDice[3][1] =Main.ySize-537; 
        positionDice[4][0] =699;
        positionDice[4][1] =784;
        positionDice[5][0] =250;
        positionDice[5][1] =334;
        positionDice[6][0] =Main.ySize-474; 
        positionDice[6][1] =Main.ySize-539;
        positionDice[7][0] =Main.ySize-101;
        positionDice[7][1] =Main.ySize-165;
        
        int cnt=0;
        
        //if (turn == 0) //solo prova
            selectedPlayerHoriz.draw(230, Main.ySize-240);
        
        for (int i = 0; i<nPlayers;i++){
            for (int j=0;j<initDicePlayer;j++){
                if(i<=1){
                    if(cnt<=1){
                        g.drawImage(boxDiceHoriz, dimXHor, Main.ySize-213);
                        dimXHor += 450; 
                        cnt++;
                    }
                    if (i == id){
                        if(j<3){
                            g.drawImage(dice.get(positionPlayerDice[i][j]), positionDice[i][0], Main.ySize-187);
                            positionDice[i][0] += 171;
                        }
                        else {
                            g.drawImage(dice.get(positionPlayerDice[i][j]), positionDice[i][1], Main.ySize-97);
                            positionDice[i][1] += 173;
                        }
                    }
                    else{
                        if(j<3){
                            g.drawImage(dice.get(7), positionDice[i][0], Main.ySize-187);
                            positionDice[i][0] += 171;
                        }
                        else {
                            g.drawImage(dice.get(7), positionDice[i][1], Main.ySize-97);
                            positionDice[i][1] += 173;
                        }
                    }
                }
                else if(i>1 && i<4){
                    if(cnt<=3){
                        g.drawImage(boxDiceVert, 1153, Main.ySize-dimYVer);
                        dimYVer += 374;
                        dimXHor = 240;
                        cnt++;
                    }
                    if (i == id){
                        if(j<3){
                            dice.get(positionPlayerDice[0][4]).setRotation(-90);
                            g.drawImage(dice.get(positionPlayerDice[0][4]), 1175 , positionDice[i][0]);
                            dice.get(positionPlayerDice[0][4]).setRotation(0);
                            positionDice[i][0] -= 134;
                        }
                        else{
                            dice.get(positionPlayerDice[0][4]).setRotation(-90);
                            g.drawImage(dice.get(positionPlayerDice[0][4]), 1265, positionDice[i][1]);
                            dice.get(positionPlayerDice[0][4]).setRotation(0);
                            positionDice[i][1] -= 136;

                        } 
                    }
                    else{
                        if(j<3){
                            dice.get(positionPlayerDice[0][4]).setRotation(-90);
                            g.drawImage(dice.get(7), 1175 , positionDice[i][0]);
                            dice.get(positionPlayerDice[0][4]).setRotation(0);
                            positionDice[i][0] -= 134;
                        }
                        else{
                            dice.get(positionPlayerDice[0][4]).setRotation(-90);
                            g.drawImage(dice.get(7), 1265, positionDice[i][1]);
                            dice.get(positionPlayerDice[0][4]).setRotation(0);
                            positionDice[i][1] -= 136;

                        }
                    }

                }
                else if (i>3 && i<6){
                    if(cnt<=5){
                        boxDiceHoriz.setRotation(180);
                        g.drawImage(boxDiceHoriz, dimXHor, Main.ySize-768);
                        boxDiceHoriz.setRotation(0);
                        dimXHor += 450;
                        dimYVer = 377; 
                        cnt++;
                    }
                    if (i == id){
                        if(j<3){
                            dice.get(positionPlayerDice[0][4]).setRotation(180);
                            g.drawImage(dice.get(positionPlayerDice[0][4]), positionDice[i][0], Main.ySize-653);
                            dice.get(positionPlayerDice[0][4]).setRotation(0);
                            positionDice[i][0] += 171;
                        }
                        else{
                            dice.get(positionPlayerDice[0][4]).setRotation(180);
                            g.drawImage(dice.get(positionPlayerDice[0][4]), positionDice[i][1], Main.ySize-743);
                           dice.get(positionPlayerDice[0][4]).setRotation(0);
                            positionDice[i][1] += 173;

                        }
                    }
                    else{
                        if(j<3){
                            dice.get(positionPlayerDice[0][4]).setRotation(180);
                            g.drawImage(dice.get(7), positionDice[i][0], Main.ySize-653);
                            dice.get(positionPlayerDice[0][4]).setRotation(0);
                            positionDice[i][0] += 171;
                        }
                        else{
                            dice.get(positionPlayerDice[0][4]).setRotation(180);
                            g.drawImage(dice.get(7), positionDice[i][1], Main.ySize-743);
                           dice.get(positionPlayerDice[0][4]).setRotation(0);
                            positionDice[i][1] += 173;

                        }
                    }

                } 
                else{
                    if(cnt<=7){
                        boxDiceVert.setRotation(180);
                        g.drawImage(boxDiceVert, 0, Main.ySize-dimYVer);
                        boxDiceVert.setRotation(0);
                        dimYVer += 374;
                    }
                    if (i == id) {
                        if(j<3){
                            dice.get(positionPlayerDice[0][4]).setRotation(90);
                            g.drawImage(dice.get(positionPlayerDice[0][4]), 115, positionDice[i][0]);
                            dice.get(positionPlayerDice[0][4]).setRotation(0);
                            positionDice[i][0] -= 134;
                        }
                        else{
                            dice.get(positionPlayerDice[0][4]).setRotation(90);
                            g.drawImage(dice.get(positionPlayerDice[0][4]), 25, positionDice[i][1]);
                            dice.get(positionPlayerDice[0][4]).setRotation(0);
                            positionDice[i][1] -= 136;

                        }
                    }
                    else{
                        if(j<3){
                            dice.get(positionPlayerDice[0][4]).setRotation(90);
                            g.drawImage(dice.get(7), 115, positionDice[i][0]);
                            dice.get(positionPlayerDice[0][4]).setRotation(0);
                            positionDice[i][0] -= 134;
                        }
                        else{
                            dice.get(positionPlayerDice[0][4]).setRotation(90);
                            g.drawImage(dice.get(7), 25, positionDice[i][1]);
                            dice.get(positionPlayerDice[0][4]).setRotation(0);
                            positionDice[i][1] -= 136;

                        }
                    }

                }
            }   
        }
        
        //PANEL DX
        
        if(getBoard().initChoice == true){
            //button MakeBet
//            g.setColor(Color.red);
//            g.fillRect(825, Main.ySize-390, Menu.buttonWidth, Menu.buttonHeigh);
            button.draw(825, Main.ySize-390, Menu.buttonWidth, Menu.buttonHeigh);
            g.setColor(Color.white);
            g.drawString("Make Bet", 878, Main.ySize-375);
            
            //button Doubt

            button.draw(825, Main.ySize-300, Menu.buttonWidth, Menu.buttonHeigh);
            g.setColor(Color.white);
            g.drawString("Doubt", 885, Main.ySize-285);
        }
        else {
            //button Bet
            button.draw(825, Main.ySize-300, Menu.buttonWidth, Menu.buttonHeigh);
            g.setColor(Color.white);
            g.drawString("Bet", 902, Main.ySize-285);

            //Draw arrows
            g.drawImage(arrowLeft, 705, Main.ySize-375);
            g.drawImage(arrowRight, 845, Main.ySize-375);
            g.drawImage(arrowLeft, 938, Main.ySize-375);
            g.drawImage(arrowRight, 1078, Main.ySize-375);

            //draw dice for bet

            if (drawDieBet == 1)
                g.drawImage(dice.get(1), 752, 371);
            if (clickToChangeDie)
                g.drawImage(dice.get(drawDieBet), 752, 371);        

            //draw value for quantify n° of dice
            g.setColor(Color.black);
           // if (!clickToChangeTextDice) {
                if(drawValueBet == 1)
                    font.drawString(1012, 380, ""+drawValueBet, Color.black);
                
                if (clickToChangeValue) {
                    if(drawValueBet < 10)
                        font.drawString(1012, 380, ""+drawValueBet, Color.black);
                    else
                        font.drawString(995, 380, ""+drawValueBet, Color.black);
                }
        }
            //button leave
            button.draw(355, Main.ySize-300, Menu.buttonWidth, Menu.buttonHeigh);
            g.setColor(Color.white);
            g.drawString("Leave", 422, Main.ySize-285);
        
        /////////////////////
        
        //////////////////// Panel SX
        
        if(submittedChoice == true){
            g.setColor(Color.black);
            g.drawString("Bet made by player "+ id, 325, Main.ySize-410);
            g.drawString(lbDrawValueBet + " dice that value ", 325, Main.ySize-370);
            g.drawImage(dice.get(lbDrawDieBet), 527,Main.ySize-408);
        }
            
            
        fontTurn.drawString(710, Main.ySize-531,""+turn,Color.black);
            
        g.setColor(Color.blue);
        g.drawString(""+getX, 50, 70);
        g.drawString(""+getY, 50, 90);
    }
    
    @Override
    public void update (GameContainer gc,StateBasedGame sbg,int delta) throws SlickException { // run this every frame to display graphics to the player
        
        if(initBoardBool == false){
            initBoardBool =  true; 
            
            turn = board.nTurn;
            id = getBoard().myID;
            System.out.println("my ID: "+ id );
            initDicePlayer = 5; //da fare
            nPlayers = getBoard().getnPlayers();
            
            for (int s=0;s<nPlayers;s++){
                amountDicePlayers = getBoard().getCurrentPlayers().getVectorPlayers()[s].getmyDiceValue();
                for(int i=0;i<amountDicePlayers.length;i++){
                    positionPlayerDice[s][i] = amountDicePlayers[i];
                    System.out.println("player " + s + " dice: "+ positionPlayerDice[s][i]);
                }
            }
        }
        
        ///////////////////// CHECK GAME STATE ////////////////////////////////
        
        
        try {
            board.gameLoop(board, board.getCurrentPlayers().vectorPlayers[id]);
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
        
        
        ///////////////////////////////////////////////////////////////////
        
        Input input = gc.getInput();
        if(gc.getInput().isKeyPressed(Input.KEY_1)){
            System.exit(0);
        }
                
        if (input.isMousePressed(Input.MOUSE_LEFT_BUTTON)){
            
            getX = Mouse.getX();
            getY = abs(0-Mouse.getY());
            
            if (getBoard().initChoice == true){
                if((getX > 825 && getX < 1005) && (getY > 339 && getY < 389)){ //Make bet
                    getBoard().initChoice = false;
                }
                
                if((getX > 825 && getX < 1005) && (getY > 249 && getY < 299)){ //doubt
                    System.out.println("Dubito!");
                }
            }
            else {
                ////////////////////////////////////// Arrows
            
                if((getX >706 && getX < 739) && (getY > 338 && getY < 373) ){ //left arrow Die
                    clickToChangeDie = true;
                    drawDieBet -= 1;
                    if(drawDieBet < 1)
                        drawDieBet = 6;
                }
                if((getX > 845 && getX < 879) && (getY> 338 && getY < 373) ){ //right arrow Die
                    clickToChangeDie = true;
                    drawDieBet += 1;
                    if(drawDieBet > 6)
                        drawDieBet = 1;
                }
                if((getX > 938 && getX < 974) && (getY > 338 && getY < 373) ){ //right arrow Value
                    clickToChangeValue = true;
                    drawValueBet -= 1;
                    if(drawValueBet < 1)
                        drawValueBet = nPlayers*initDicePlayer; //Temporaneo, devo sapere quanti dadi ogni giocare ha dopo ogni scommessa
                }
                if((getX > 1080 && getX < 1111) && (getY > 338 && getY < 373) ){ //right arrow Value
                    clickToChangeValue = true;
                    drawValueBet += 1;
                    if(drawValueBet > nPlayers*5)
                        drawValueBet = 1;
                }

                //////////////////////////////////////
                
                
                ///////////////////////////////////// Buttons
                
                if((getX > 825 && getX < 1005) && (getY > 249 && getY < 299)){ //bet
                    lbDrawDieBet = drawDieBet;
                    lbDrawValueBet = drawValueBet;
                    submittedChoice = true;
                    
                    board.betDone = true;
                    
                    System.out.println("Scommessa Effettuata");
                
                }
            }
            if((getX > 355 && getX < 535) && (getY > 249 && getY < 299)){ //leave
                    System.exit(0);
            }
        }
    }
    @Override
    public int getID(){
        return 2;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }
}
