
import java.util.logging.Level;
import java.util.logging.Logger;
import org.newdawn.slick.*;
import org.newdawn.slick.state.*;

public class Main extends StateBasedGame implements Runnable{
    
    public static String gameName = "Liar's Dice";
    public static int menu = 0;
    public static int connect = 1;
    public static int play = 2;
    public static int xSize = 1366;
    public static int ySize = 768;
    
    
    
    public Main (String name){
        super(gameName);
        
    }
    
    public boolean initPlay = false;
    
    @Override
    public void initStatesList(GameContainer gc) throws SlickException{
        
       GUIController gC = new GUIController();
       GuiDefineImages gDefImg = new GuiDefineImages();
       GuiDefineFont gDefFont = new GuiDefineFont();
       GuiDefineButtons gDrawButtons = new GuiDefineButtons(gDefImg,gDefFont);
       
       gDefImg.importImages();
       gDefFont.importFont();
       
       //this.addState(new Menu(gC,gDefImg, gDrawButtons));
       this.addState(new Connect(gC, gDefImg, gDrawButtons,gDefFont));
       this.addState(new Play(gC, gDefImg, gDrawButtons, gDefFont));

    }
    public static void runGraphic() throws SlickException {
        AppGameContainer appgc;
        try {
            appgc = new AppGameContainer(new Main(gameName));
            appgc.setDisplayMode(xSize, ySize, false);
            appgc.setTargetFrameRate(20);
            System.out.println("Start appgc");
            appgc.start();
        }
        catch(SlickException e){}
    }

    @Override
    public void run() {
        try {
            Main.runGraphic();
        } catch (SlickException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }



}