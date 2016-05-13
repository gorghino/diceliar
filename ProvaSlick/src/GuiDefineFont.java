
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.util.ResourceLoader;

/**
 *
 * @author proietfb
 */
public class GuiDefineFont {
   
    TrueTypeFont fontButton, textFont, textFontLobby, fontValue, fontTurn;

    Font awtFont, awtFontButton, awtFontText, awtFontTextButton, awtFontTextLobby, awtFontValue, awtFontTurn;
    
    public GuiDefineFont(){}
    
    public void importFont() throws SlickException {

        try {
            InputStream inputStream = ResourceLoader.getResourceAsStream("font/VarsityPlaybook-DEMO.ttf");
            
            //Menu
            awtFont = Font.createFont(java.awt.Font.TRUETYPE_FONT, inputStream);
            awtFontButton = awtFont.deriveFont(28f);
            fontButton = new TrueTypeFont(awtFontButton, true);
            //Connect
            awtFontText = awtFont;
            awtFontText = awtFontText.deriveFont(36f);
            textFont = new TrueTypeFont(awtFontText, true);

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

        } catch (FontFormatException | IOException e) {
        }
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