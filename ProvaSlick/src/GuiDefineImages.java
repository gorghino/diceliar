
import java.util.ArrayList;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

/**
 *
 * @author proietfb
 */
public class GuiDefineImages {

    Image backPanel,button, menuTitle, background, guiBoard, boxDiceHoriz,boxDiceVert,
            arrowLeft,arrowRight, selectedPlayerHoriz, selectedPlayerVert;

    ArrayList<Image> arrayDice = new ArrayList<>();

    public GuiDefineImages() {}

    public void importImages() throws SlickException {
        background = new Image("img/bgDiceLiar.png");
        guiBoard = new Image("img/boardTEMP4.png");
        menuTitle = new Image("img/diceTitle.png");
        button = new Image("img/button.png");
        backPanel = new Image("img/BackPanel.png");
        boxDiceHoriz = new Image("img/BoxDiceHoriz.png");
        boxDiceVert = new Image("img/BoxDiceVert.png");
        arrowLeft = new Image("img/ArrowLeft.png");
        arrowRight = new Image("img/ArrowRight.png");
        selectedPlayerHoriz = new Image("img/SelectedPlayerHoriz.png");
        selectedPlayerVert = new Image("img/SelectedPlayerVert.png");

        arrayDice.add(0, new Image("img/DieDel.png"));
        arrayDice.add(1, new Image("img/Die1.png"));
        arrayDice.add(2, new Image("img/Die2.png"));
        arrayDice.add(3, new Image("img/Die3.png"));
        arrayDice.add(4, new Image("img/Die4.png"));
        arrayDice.add(5, new Image("img/Die5.png"));
        arrayDice.add(6, new Image("img/Die6.png"));
        arrayDice.add(7, new Image("img/DieQM.png"));
        arrayDice.add(8, new Image("img/DieJoker.png"));
    }

    public Image getBackground() {
        return background;
    }

    public Image getGuiBoard() {
        return guiBoard;
    }

    public Image getMenuTitle() {
        return menuTitle;
    }

    public Image getButton() {
        return button;
    }

    public Image getBackPanel() {
        return backPanel;
    }

    public Image getBoxDiceHoriz() {
        return boxDiceHoriz;
    }

    public Image getBoxDiceVert() {
        return boxDiceVert;
    }

    public Image getArrowLeft() {
        return arrowLeft;
    }

    public Image getArrowRight() {
        return arrowRight;
    }

    public Image getSelectedPlayerHoriz() {
        return selectedPlayerHoriz;
    }

    public Image getSelectedPlayerVert() {
        return selectedPlayerVert;
    }

    public ArrayList<Image> getArrayDice() {
        return arrayDice;
    }

}
