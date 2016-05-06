
import java.awt.Font;
import java.io.InputStream;
import java.util.ArrayList;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.util.ResourceLoader;

/**
 *
 * @author proietfb
 */
public class GuiUtils {

    Image backPanel,
          button,
          menuTitle,
          background,
          guiBoard,
          boxDiceHoriz,
          boxDiceVert,
          arrowLeft,
          arrowRight,
          selectedPlayerHoriz,
          selectedPlayerVert;

    ArrayList<Image> arrayDice = new ArrayList<>();
    
    TrueTypeFont fontButton,
                 textFont,
                 textFontLobby,
                 fontValue,
                 fontTurn;

    Font awtFont,
         awtFontButton,
         awtFontText,
         awtFontTextButton,
         awtFontTextLobby,
         awtFontValue,
         awtFontTurn;
    
             
    public GuiUtils() {
    }
    
    public void importFont(){
        
        try {
            InputStream inputStream = ResourceLoader.getResourceAsStream("font/VarsityPlaybook-DEMO.ttf");
            //Menu
            awtFont = Font.createFont(java.awt.Font.TRUETYPE_FONT,inputStream);
            awtFontButton = awtFont.deriveFont(28f);
            fontButton= new TrueTypeFont(awtFontButton, true);
            //Connect
            awtFontText = awtFont;
            awtFontText = awtFontText.deriveFont(36f);
            textFont= new TrueTypeFont(awtFontText, true);
                        
            awtFontTextLobby = awtFont;
            awtFontTextLobby = awtFontText.deriveFont(75);
            textFontLobby = new TrueTypeFont(awtFontTextLobby, true);
            //Play
            awtFontValue = awtFont;
            awtFontValue = awtFont.deriveFont(55f);
            fontValue = new TrueTypeFont(awtFontValue, false);
            
            awtFontTurn = awtFont;
            awtFontTurn = awtFontTurn.deriveFont(32f);
            fontTurn = new TrueTypeFont(awtFontTurn, true);
            
      
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void importImages() throws SlickException{
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
    
    public TrueTypeFont getFontButton() {
        return fontButton;
    }

    public TrueTypeFont getTextFont() {
        return textFont;
    }

    public TrueTypeFont getTextFontLobby() {
        return textFontLobby;
    }

    public TrueTypeFont getFontValue() {
        return fontValue;
    }

    public TrueTypeFont getFontTurn() {
        return fontTurn;
    }
    
    
    
    
}
