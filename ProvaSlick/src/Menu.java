import org.lwjgl.input.Mouse;
import org.newdawn.slick.*;
import org.newdawn.slick.state.*;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;
/**
 *
 * @author proietfb
 */

public class Menu extends BasicGameState {
    
    private int stateId = -1;
    public static int buttonWidth = 180;
    public static int buttonHeigh = 50;
    
    Image title,
          background,
          button;
    
    public int getX,getY;
    
    boolean enterStatePlay = false,
            enterConnectState = true;
    
    TrueTypeFont fontB;
    
    GUIController gC;
    GuiUtils guiUtils;

    public Menu(int _stateID, GUIController _gC, GuiUtils _gUtils){
        this.stateId = _stateID;
        this.gC = _gC;
        this.guiUtils = _gUtils;
    }
    
    @Override
    public void init(GameContainer gc, StateBasedGame sbg) throws SlickException{
               
        background = guiUtils.getBackground();
        title = guiUtils.getMenuTitle();
        button = guiUtils.getButton();
        
        fontB = guiUtils.getFontButton();
        
    }
    public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException{ // run this every frame to update game logic
        
        background.draw(0, 0);
        title.draw(900, 0,(float)1.1);
        g.setBackground(new Color(0, 230 , 0));
        
        g.setColor(Color.white);
        g.drawString(""+getX, 50, 70);
        g.drawString(""+getY, 50, 90);
        
        g.setColor(Color.red);

        button.draw((Main.xSize/2)-(buttonWidth/2), 438, buttonWidth, buttonHeigh);//550
        button.draw((Main.xSize/2)-(buttonWidth/2), 500, buttonWidth, buttonHeigh);
        
        //Button's Text
        g.setColor(Color.white);
        fontB.drawString(644, 451, "Connect");
        fontB.drawString(660, 515, "Exit");

         
    }
    @Override
    public void update(GameContainer gc,StateBasedGame sbg,int delta) throws SlickException { // run this every frame to display graphics to the player
        
        Input input = gc.getInput();
        if (gc.getInput().isKeyPressed(Input.KEY_0)){
            sbg.enterState(1,new FadeOutTransition(Color.gray),new FadeInTransition(Color.gray));
        }
        if (input.isMouseButtonDown(0)){
            getX = Mouse.getX();
            getY = Mouse.getY();

        }
        if(input.isMousePressed(Input.MOUSE_LEFT_BUTTON)){

            if((getX>(Main.xSize/2)-(buttonWidth/2) && getX<771) && (getY>277 && getY<327)){ // Connect                
                    sbg.enterState(1,new FadeOutTransition(Color.gray),new FadeInTransition(Color.gray));
                }

            if((getX>(Main.xSize/2)-(buttonWidth/2) && getX<771) && (getY>216 && getY<266)){ //Exit
                //sbg.enterState(2,new FadeOutTransition(Color.gray),new FadeInTransition(Color.gray));
                System.exit(0);
            }
        }
    }
    @Override
    public int getID(){
        return 0;
    }
    
}
