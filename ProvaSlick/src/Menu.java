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
    
    Image title,
          background,
          button;
    
    public int getX,getY;
    
    boolean enterStatePlay = false,
            enterConnectState = true;
    
    GUIController gC;
    GuiDefineImages guiDefImg;
    GuiDefineButtons gDrawButtons;

    public Menu(GUIController _gC, GuiDefineImages _guiDefImg, GuiDefineButtons _gDrawButtons){
        this.gC = _gC;
        this.guiDefImg = _guiDefImg;
        this.gDrawButtons = _gDrawButtons;
    }
    
    @Override
    public void init(GameContainer gc, StateBasedGame sbg) throws SlickException{
               
        background = guiDefImg.getBackground();
        title = guiDefImg.getMenuTitle();
        button = guiDefImg.getButton();
        
    }
    @Override
    public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException{ // run this every frame to update game logic
        
        background.draw(0, 0);
        title.draw(900, 0,(float)1.1);
        
        gDrawButtons.drawButton((Main.xSize/2)-(GuiDefineButtons.buttonWidth/2), 438, GuiDefineButtons.buttonWidth, GuiDefineButtons.buttonHeigh, 644, 451, "Connect", Color.white);
        gDrawButtons.drawButton((Main.xSize/2)-(GuiDefineButtons.buttonWidth/2), 500, GuiDefineButtons.buttonWidth, GuiDefineButtons.buttonHeigh, 660, 515, "Exit", Color.white);       
 
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

            if((getX>=(Main.xSize/2)-(GuiDefineButtons.buttonWidth/2) && getX<=771) && (getY>=277 && getY<=327)){ // Connect                
                    sbg.enterState(1,new FadeOutTransition(Color.gray),new FadeInTransition(Color.gray));
                }

            if((getX>=(Main.xSize/2)-(GuiDefineButtons.buttonWidth/2) && getX<=771) && (getY>=216 && getY<=266)){ //Exit
                System.exit(0);
            }
        }
    }
    @Override
    public int getID(){
        return 0;
    }
    
}
