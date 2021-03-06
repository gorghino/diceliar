
import org.lwjgl.input.Mouse;
import org.newdawn.slick.*;
import org.newdawn.slick.state.*;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.gui.TextField;
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

    DiceLiar dl;
    RMIGameController gameC;
    RMI rmiNext;

    private TextField ipField, portField;

    private int getX, getY;

    public int waitConnections;

    boolean loadPlayers = false,
            clickedPlay = false,
            clickedPlaySkip = true;

    public String takeIPAddr = "", takePort = "";

    Board startBoard;
    GUIController gC;
    GuiDefineImages guiDefImg;
    GuiDefineButtons gDrawButtons;
    GuiDefineFont gDefFont;

    public Connect(GUIController _gC, GuiDefineImages _guiDefImg, GuiDefineButtons _gDrawButtons, GuiDefineFont _gDefFont) {
        this.gC = _gC;
        this.guiDefImg = _guiDefImg;
        this.gDrawButtons = _gDrawButtons;
        this.gDefFont = _gDefFont;
    }

    @Override
    public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {

        ipField = new TextField(gc, gDefFont.getPinesFont(), 535, Main.ySize - 466, 600, 38);
        portField = new TextField(gc, gDefFont.getPinesFont(), 535, Main.ySize - 371, 150, 38);

    }

    @Override
    public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {

        guiDefImg.getBackground().draw();
        guiDefImg.getBackPanel().draw(287, Main.ySize - 519);

        guiDefImg.getBackPanel().draw(550, Main.ySize - 705, GuiDefineButtons.buttonWidth, 60);

        gDefFont.getTextFontLobby().drawString(600, Main.ySize - 689, "Lobby", Color.black);
        gDefFont.getTextFont().drawString(340, 285, "Insert server", Color.black);
        gDefFont.getTextFont().drawString(340, 320, "IP Address", Color.black);
        gDefFont.getTextFont().drawString(315, 400, "Insert your Port", Color.black);

        gDrawButtons.drawButton(550, 514, GuiDefineButtons.buttonWidth, GuiDefineButtons.buttonHeigh, 620, 529, "Play", Color.white);

        g.setColor(Color.black);
        ipField.render(gc, g);
        portField.render(gc, g);

        if (clickedPlaySkip == false) {
            gDefFont.getTextFont().drawString(500, 600, "Connected. Wait  other players... ", Color.white);
        }
    }

    @Override
    public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException {

        Input input = gc.getInput();

        if (input.isMouseButtonDown(0)) {
            getX = Mouse.getX();
            getY = Mouse.getY();

        }

        if (clickedPlay == false && (!"".equals(ipField.getText()) && !"".equals(portField.getText()))) {
            if (input.isMousePressed(Input.MOUSE_LEFT_BUTTON) || clickedPlaySkip == false) {
                if ((getX >= 550 && getX <= 730) && (getY >= 202 && getY <= 250) || clickedPlaySkip == false) { // Play

                    gC.setPlayConnectedClicked(true);

                    if (clickedPlaySkip) {
                        clickedPlaySkip = false;
                        return;
                    }

                    clickedPlay = true;

                    takeIPAddr = ipField.getText();
                    takePort = portField.getText();

                    System.out.println("IP: " + takeIPAddr + " Port: " + takePort);

                    try {
                        dl = new DiceLiar();
                        dl.connectServer(takeIPAddr, takePort);

                        //System.out.println("INITBOARD\n");
                        startBoard = dl.initBoard(gC);
                        gC.setBoard(startBoard);
                        startBoard.initGame(startBoard, dl.rmiNext);
                        runPlay(sbg);

                    } catch (RemoteException | AlreadyBoundException | NotBoundException | UnknownHostException ex) {
                        Logger.getLogger(Connect.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }

        if (gc.getInput().isKeyPressed(Input.KEY_END)) {
            System.exit(0);
        }
        if (gc.getInput().isKeyPressed(Input.KEY_F1)) {
            sbg.enterState(0, new FadeOutTransition(Color.gray), new FadeInTransition(Color.gray));
        }
    }

    @Override
    public int getID() {
        return 1;
    }

    private void runPlay(StateBasedGame sbg) throws SlickException {
        Play playState = (Play) sbg.getState(Main.play);
        playState.setBoard(startBoard);
        sbg.enterState(2, new FadeOutTransition(Color.gray), new FadeInTransition(Color.gray));
    }
}
