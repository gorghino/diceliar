


import static java.lang.Math.abs;
import java.util.ArrayList;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.*;
import org.newdawn.slick.state.*;
import org.newdawn.slick.GameContainer;
import java.awt.Font;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

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
          arrowRight;
    
    //Rectangle buttonBet, buttonDoubdt, buttonLeave;
    TrueTypeFont font;
    ArrayList<Image> dice = new ArrayList<>();
    
    int[][] positionPlayerDice, positionDice;
    
    
    int getX,getY;
    int nPlayers,
        nDicePlayer,
        turn;
    
    
    int drawDieBet,drawValueBet;
    private boolean clickToChangeDie = false,
                    clickToChangeValue = false,
                    initBoardBool = false;
    Board board;
    DiceLiar dl;
    GameController gameC;
    ArrayList<PlayerEntry> playerEntryArray;
    RMI rmiNext;
    
                
    
    public Play(int _stateID){
        this.stateID = _stateID;
    }
    
    @Override
    public void init(GameContainer gc, StateBasedGame sbg) throws SlickException{ 

        
        background = new Image("img/boardTEMP1.png");
        //buttonBet = new Rectangle(825, Main.ySize-280, Menu.buttonWidth, Menu.buttonHeigh);
        
        boxDiceHoriz = new Image("img/BoxDiceHoriz.png");
        boxDiceVert = new Image("img/BoxDiceVert.png");
        
        arrowLeft = new Image("img/ArrowLeft.png");
        arrowRight = new Image("img/ArrowRight.png");
        
        dice.add(0, new Image("img/DieDel.png"));
        dice.add(1, new Image("img/Die1.png"));
        dice.add(2, new Image("img/Die2.png"));
        dice.add(3, new Image("img/Die3.png"));
        dice.add(4, new Image("img/Die4.png"));
        dice.add(5, new Image("img/Die5.png"));
        dice.add(6, new Image("img/Die6.png"));
        dice.add(7, new Image("img/DieQM.png"));
        
       
        
                
        nPlayers = 8;
        
        
        nDicePlayer = 5;
        
        positionPlayerDice = new int[nPlayers][5];
        positionDice = new int[nPlayers][2];
        
                    
        drawDieBet=1;
        drawValueBet = 1;
        

        
        
        
        Font awtFont = new Font("Verdana", 0, 50);
        font = new TrueTypeFont(awtFont, true);
        
//        textValueDice = new TextField(gc, font, 1000, 376, 60, 60); 
//        textValueDice.setCursorVisible(false);
        
    }
    
    @Override
    public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException{ // run this every frame to update game logic
       
        background.draw(0, 0);

        //DRAW PLAYERS
        int dimXHor = 240,dimYVer = 377;
        
        positionPlayerDice[0][0] = 5;
        positionPlayerDice[0][1] = 4;
        positionPlayerDice[0][2] = 3;
        positionPlayerDice[0][3] = 2;
        positionPlayerDice[0][4] = 1;
        
        int posX1=249,posX2=334,cnt=0;
        
        
        positionDice[0][0] =249; 
        positionDice[0][1] =334; 
        positionDice[1][0] =699; 
        positionDice[1][1] =784; 
//        positionDice[2][0] =Main.ySize-97;  
//        positionDice[2][1] =Main.ySize-163; 
//        positionDice[3][0] =Main.ySize-470;  
//        positionDice[3][1] =Main.ySize-537; 
//        positionDice[4][0] =699;
//        positionDice[4][1] =784;
//        positionDice[5][0] =250;
//        positionDice[5][1] =334;
//        positionDice[6][0] =Main.ySize-474; 
//        positionDice[6][1] =Main.ySize-539;
//        positionDice[7][0] =Main.ySize-101;
//        positionDice[7][1] =Main.ySize-165;
//        
        for (int i = 0; i< nPlayers;i++){
            for (int j=0;j<nDicePlayer;j++){
                if(i<=1){
                    if(cnt<=1){
                        g.drawImage(boxDiceHoriz, dimXHor, Main.ySize-213);
                        dimXHor += 450; 
                        cnt++;
                    }
                    if(j<3){
                        g.drawImage(dice.get(positionPlayerDice[0][j]), positionDice[i][0], Main.ySize-187);
                        positionDice[i][0] += 171;
                    }
                    else {
                        g.drawImage(dice.get(positionPlayerDice[0][1]), positionDice[i][1], Main.ySize-97);
                        positionDice[i][1] += 173;
                    }
                }
                else if(i>1 && i<4){
                    if(cnt<=3){
                        g.drawImage(boxDiceVert, 1153, Main.ySize-dimYVer);
                        dimYVer += 374;
                        dimXHor = 240;
                        cnt++;
                    }
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
                else if (i>3 && i<6){
                    if(cnt<=5){
                        boxDiceHoriz.setRotation(180);
                        g.drawImage(boxDiceHoriz, dimXHor, Main.ySize-768);
                        boxDiceHoriz.setRotation(0);
                        dimXHor += 450;
                        dimYVer = 377; 
                        cnt++;
                    }
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
                    if(cnt<=7){
                        boxDiceVert.setRotation(180);
                        g.drawImage(boxDiceVert, 0, Main.ySize-dimYVer);
                        boxDiceVert.setRotation(0);
                        dimYVer += 374;
                    }
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
            }   
        }
        
        //PANEL
        
        //button Bet
        g.setColor(Color.red);
        g.fillRect(825, Main.ySize-300, Menu.buttonWidth, Menu.buttonHeigh);
        g.setColor(Color.white);
        g.drawString("Bet", 902, Main.ySize-285);
        
        //button leave
        g.setColor(Color.red);
        g.fillRect(355, Main.ySize-300, Menu.buttonWidth, Menu.buttonHeigh);
        g.setColor(Color.white);
        g.drawString("Leave", 422, Main.ySize-285);
        
        //Draw arrows
        g.drawImage(arrowLeft, 705, Main.ySize-375);
        g.drawImage(arrowRight, 845, Main.ySize-375);
        g.drawImage(arrowLeft, 938, Main.ySize-375);
        g.drawImage(arrowRight, 1078, Main.ySize-375);
        
        //draw dice for bet
        
        if (drawDieBet == 1)
            g.drawImage(dice.get(1), 752, 371);
        if (clickToChangeDie){
            g.drawImage(dice.get(drawDieBet), 752, 371);        
                
        }
        
        //draw value for quantify n° of dice
        g.setColor(Color.black);
       // if (!clickToChangeTextDice) {
            if(drawValueBet == 1){
                font.drawString(1012, 380, ""+drawValueBet, Color.black);

            }
            if (clickToChangeValue) {

                if(drawValueBet < 10)
                    font.drawString(1012, 380, ""+drawValueBet, Color.black);
                else
                    font.drawString(995, 380, ""+drawValueBet, Color.black);
            }
       // }
//        if(clickToChangeTextDice){
//               textValueDice.render(gc, g);
//            }
//        
         
        g.setColor(Color.blue);
        g.drawString(""+getX, 50, 70);
        g.drawString(""+getY, 50, 90);  
        g.drawString(""+nPlayers, 50, 110);
    }
    
    @Override
    public void update (GameContainer gc,StateBasedGame sbg,int delta) throws SlickException { // run this every frame to display graphics to the player
        
        if(initBoardBool == true){
            initBoardBool = true;
            try {
                try {
                    dl = new DiceLiar();
                } catch (AlreadyBoundException | UnknownHostException ex) {
                    Logger.getLogger(Play.class.getName()).log(Level.SEVERE, null, ex);
                }
                //board = new Board(gameC.myID, turn, nPlayers, playerEntryArray, gameC.lock);
                
                                
                 
                //board = new Board(gameC.myID, turn, nPlayers, playerEntryArray, gameC.lock);
                
                
                
                
                Board startBoard = dl.initBoard();
                
                System.out.println("Ciao: "+ nPlayers);
                
                startBoard.initGame(startBoard, rmiNext);
                //nPlayers = board.getnPlayers();
               
                
            } catch (RemoteException | NotBoundException ex) {
                Logger.getLogger(Play.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        
        Input input = gc.getInput();
        if(gc.getInput().isKeyPressed(Input.KEY_1)){
            System.exit(0);
        }
                
        if (input.isMousePressed(Input.MOUSE_LEFT_BUTTON)){
            
            getX = Mouse.getX();
            getY = abs(0-Mouse.getY());
            
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
                    drawValueBet = nPlayers*5; //Temporaneo, devo sapere quanti dadi ogni giocare ha dopo ogni scommessa
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
                System.out.println("Scommessa Effettuata");
                
            }
            
            if((getX > 355 && getX < 535) && (getY > 249 && getY < 299)){ //leave
                System.exit(0);
                
            }
//            if((getX > 1000 && getX < 1056) && (getY > 340 && getY < 376)){ //Textfield value n. Dice
//                clickToChangeTextDice = true;
//                //drawValueBet = Integer.parseInt();
//                System.out.println(textValueDice.getText());
//
//            }
            
 
        }
    }
    public int getID(){
        return 2;
    }
}
