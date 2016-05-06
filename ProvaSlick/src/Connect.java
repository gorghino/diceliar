import org.lwjgl.input.Mouse;
import org.newdawn.slick.*;
import org.newdawn.slick.state.*;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.gui.TextField;
import java.awt.Font;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;
import org.newdawn.slick.util.ResourceLoader;

/**
 *
 * @author proietfb
 */
public class Connect extends BasicGameState {
    
    private int stateID = -1;
    
    DiceLiar dl;
    RMIGameController gameC;
    RMI rmiNext;
    
    Image background,
          backPanel,
          button;
    
    TrueTypeFont textFont, textFontButton, textFontLobby;
    
    TextField ipField,portField;
    
    int waitConnections,
        time = 0,
        getX,
        getY;
    
    boolean loadPlayers = false,
            clickedPlay = false;
    
    public String takeIPAddr,takePort;
    
    Board startBoard;  
    
    GUIController gC;
    GuiUtils gUtils;
    
    public Connect(int _stateID, GUIController _gC, GuiUtils _gUtils){
        this.stateID = _stateID;
        this.gC = _gC;
        this.gUtils = _gUtils;
    }
    
    @Override
    public void init(GameContainer gc, StateBasedGame sbg) throws SlickException{        
        
        background = gUtils.getBackground();
        backPanel = gUtils.getBackPanel();
        button = gUtils.getButton();
        
        textFontButton = gUtils.getFontButton();
        textFont = gUtils.getTextFont();
        textFontLobby = gUtils.getTextFontLobby();
        
        ipField = new TextField(gc, textFont, 535, Main.ySize-466, 600, 38);
        portField = new TextField(gc, textFont, 535, Main.ySize-371, 150, 38);
        
    }
        
    @Override
    public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException{

        
        background.draw();
        backPanel.draw(287, Main.ySize-519);

        backPanel.draw(550,Main.ySize-705,Menu.buttonWidth, 60);
        
        textFontLobby.drawString(600, Main.ySize-689, "Lobby", Color.black);
        textFont.drawString(340, 285, "Insert server",Color.black);
        textFont.drawString(340, 320, "IP Address",Color.black);
        textFont.drawString(315 , 400, "Insert your Port",Color.black);
        
        
        
        //Play Button
        
        //g.fillRect(550, 514, Menu.buttonWidth, Menu.buttonHeigh);
        button.draw(550, 514, Menu.buttonWidth, Menu.buttonHeigh);
        
        g.setColor(Color.blue);
        g.drawString(""+getX, 50, 70);
        g.drawString(""+getY, 50, 90); 
        
        g.setColor(Color.black);
        ipField.render(gc, g);
        portField.render(gc, g);
        g.setColor(Color.white);
        textFontButton.drawString(620, 529, "Play",Color.white);
        
        if (clickedPlay == true){
            g.drawString("Wait other players... ", 500, 600);
            if (loadPlayers == true)
                g.drawString("Loading Players...", 500, 630);

        }
        
    }
    
    @Override
    public void update(GameContainer gc,StateBasedGame sbg,int delta) throws SlickException {

        
        Input input = gc.getInput();
        
        if (input.isMouseButtonDown(0)){
            getX = Mouse.getX();
            getY = Mouse.getY();

        }
        
        if (clickedPlay == false){
            if(input.isMousePressed(Input.MOUSE_LEFT_BUTTON)){
                if((getX>550 && getX<730) && (getY>202 && getY<250)){ // Play
                    
                    gC.setPlayConnectedClicked(true);
                    
                    clickedPlay = true;
                    //takeIPAddr = ipField.getText();
                    //takePort = portField.getText();
                    takeIPAddr = "127.0.0.1";
                    takePort = "50000";
                    
                    System.out.println("IP: " + takeIPAddr + " Port: " + takePort);

                    try {
                        dl = new DiceLiar();
                        dl.connectServer(takeIPAddr, takePort);
                        waitConnections = dl.rmiTimer;
                        
                        sbg.enterState(2,new FadeOutTransition(Color.gray),new FadeInTransition(Color.gray));
                    } catch (RemoteException ex) {
                        Logger.getLogger(Connect.class.getName()).log(Level.SEVERE, null, ex);
                        System.out.println("cane");
                    } catch (AlreadyBoundException ex) {
                        Logger.getLogger(Connect.class.getName()).log(Level.SEVERE, null, ex);
                        System.out.println("cane1");
                    } catch (NotBoundException ex) {
                        Logger.getLogger(Connect.class.getName()).log(Level.SEVERE, null, ex);
                        System.out.println("cane2");
                    } catch (UnknownHostException ex) {
                        Logger.getLogger(Connect.class.getName()).log(Level.SEVERE, null, ex);
                        System.out.println("cane3");
                    }
                        
   
                    try {
                        System.out.println("INITBOARD\n");
                        startBoard = dl.initBoard(gC);
                        startBoard.initGame(startBoard, rmiNext); 
                        runPlay(sbg, gc);
                        
                    } catch (RemoteException | NotBoundException ex) {
                        Logger.getLogger(Connect.class.getName()).log(Level.SEVERE, null, ex);
                    }
                          
                        
                        
                }
            }
        }
        else
            gC.setPlayConnectedClicked(false);
        
        time += delta;
        
            if (waitConnections > 0)
                waitConnections -= 1;
            else{
                waitConnections = 0;
                loadPlayers = true;
            }
                
        if(gc.getInput().isKeyPressed(Input.KEY_END)){
            System.exit(0);
        }
        if(gc.getInput().isKeyPressed(Input.KEY_F1)){
            sbg.enterState(0,new FadeOutTransition(Color.gray),new FadeInTransition(Color.gray));
        }
    }
    
    public int getID(){
        return 1;
    }
    
    public void runPlay(StateBasedGame sbg,GameContainer gc) throws SlickException{
        Play playState = (Play)sbg.getState(Main.play);
        playState.setBoard(startBoard);
        sbg.enterState(2,new FadeOutTransition(Color.gray),new FadeInTransition(Color.gray));
    }
}
