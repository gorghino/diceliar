import org.lwjgl.input.Mouse;
import org.newdawn.slick.*;
import org.newdawn.slick.state.*;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.gui.TextField;
import java.awt.Font;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

/**
 *
 * @author proietfb
 */
public class Connect extends BasicGameState {
    
    private int stateID = -1;
    
    DiceLiar dl;
    GameController gameC;
    RMI rmiNext;
    
    Image background;
    
    TrueTypeFont font;
    
    TextField ipField,portField;
    
    int waitConnections,
        time = 0,
        getX,
        getY;
    boolean loadPlayers = false,
            clickedPlay = false;
    
    public String takeIPAddr,takePort;
    
    public Connect( int _stateID) {
        this.stateID = _stateID;
    } 
    
    public void init(GameContainer gc, StateBasedGame sbg) throws SlickException{        
        
        Font awtFont = new Font("Arial", 0, 30);
       
        font= new TrueTypeFont(awtFont, true);

        ipField = new TextField(gc, font, 550, 290, 600, 50);
        portField = new TextField(gc, font, 550, 386, 150, 50);
        
    }
    
    public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException{
        g.setBackground(new Color(0, 230 , 0));
        
        g.setColor(Color.white);
        g.drawString("Lobby", 643, Main.ySize-689);
        g.drawString("Insert your IP Address", 300, 300);
        g.drawString("Insert your Port", 300, 400);
        
        //Play Button
        g.setColor(Color.red);
        g.fillRect(550, 514, Menu.buttonWidth, Menu.buttonHeigh);
        
        g.setColor(Color.blue);
        g.drawString(""+getX, 50, 70);
        g.drawString(""+getY, 50, 90); 
        
        g.setColor(Color.white);
        ipField.render(gc, g);
        portField.render(gc, g);
        
        g.drawString("Play", 620, 529);
        
        if (clickedPlay == true){
            g.drawString("Wait other players... ", 500, 600);
            if (loadPlayers == true)
                g.drawString("Loading Players...", 500, 630);

        }
        
    }
    
    public void update(GameContainer gc,StateBasedGame sbg,int delta) throws SlickException {
        
        Input input = gc.getInput();
        
        if (input.isMouseButtonDown(0)){
            getX = Mouse.getX();
            getY = Mouse.getY();

        }
        
        if (clickedPlay == false){
            if(input.isMousePressed(Input.MOUSE_LEFT_BUTTON)){
                if((getX>550 && getX<730) && (getY>202 && getY<250)){ // Play
                    clickedPlay = true;
                    takeIPAddr = ipField.getText();
                    takePort = portField.getText();
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
                        
                        Board startBoard;     
                    try {
                        startBoard = dl.initBoard();
                        startBoard.initGame(startBoard, rmiNext); 
                    } catch (RemoteException ex) {
                        Logger.getLogger(Connect.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (NotBoundException ex) {
                        Logger.getLogger(Connect.class.getName()).log(Level.SEVERE, null, ex);
                    }
                          
                        
                        sbg.enterState(2,new FadeOutTransition(Color.gray),new FadeInTransition(Color.gray));
                }
            }
        }
        
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
    
}
