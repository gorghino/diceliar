
import org.newdawn.slick.Color;
/**
 *
 * @author proietfb
 */
public class GuiDefineButtons {
    
    public static int buttonWidth = 180;
    public static int buttonHeigh = 50;
    
    int coordX, coordY;
    
    GuiDefineImages gDefImg;
    GuiDefineFont gDefFont;
    
    public GuiDefineButtons(GuiDefineImages _gDefImg, GuiDefineFont _gDefFont) {
        this.gDefImg = _gDefImg;
        this.gDefFont = _gDefFont;
    }
    
    public void drawButton(int imageX, int imageY, int imageWidth, int imageHeigh, int strX, int strY, String str, Color color) {

        gDefImg.getButton().draw(imageX, imageY, imageWidth, imageHeigh);
        gDefFont.getFontButton().drawString(strX, strY, str, color);
    }

    
    
    

}
