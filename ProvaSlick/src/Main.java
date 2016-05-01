
import java.util.logging.Level;
import java.util.logging.Logger;
import org.newdawn.slick.*;
import org.newdawn.slick.state.*;

public class Main extends StateBasedGame implements Runnable{
    
    public static final String gameName = "Liar's Dice";
    public static final int menu = 0;
    public static final int connect = 1;
    public static final int play = 2;
    public static final int xSize = 1366;
    public static final int ySize = 768;
    
    public Main (String name){
        super(gameName);
        
    }
    
    public boolean initPlay = false;
    
    @Override
    public void initStatesList(GameContainer gc) throws SlickException{
       this.addState(new Menu(menu));
       this.addState(new Connect(connect));
       this.addState(new Play(play));
       


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
        catch(SlickException e){
            e.printStackTrace();
        }
    }
//    public static void main(String[] args) {
//        try {
//            runInterface();
//        } catch (SlickException ex) {
//            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }

    
    public void run() {
        try {
            Main.runGraphic();
        } catch (SlickException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }



}