
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
   
    TrueTypeFont fontButton, textFont, textFontLobby, fontValue, fontTurn, pinesFont;

    Font awtFont, awtFontButton, awtFontText, awtFontTextButton, awtFontTextLobby, awtFontValue, awtFontTurn, awtPinesFont;
    
    public GuiDefineFont(){}
    
    public void importFont() throws SlickException {

        try {
            InputStream inputStream = ResourceLoader.getResourceAsStream("font/VarsityPlaybook-DEMO.ttf");
            InputStream inputStream2 = ResourceLoader.getResourceAsStream("font/TT Pines Bold Italic DEMO.otf");
            
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
            
            //Font2
            awtPinesFont = Font.createFont(java.awt.Font.TRUETYPE_FONT, inputStream2);
            awtPinesFont = awtPinesFont.deriveFont(30f);
            pinesFont = new TrueTypeFont(awtPinesFont, true);
            

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

    public TrueTypeFont getPinesFont() {
        return pinesFont;
    }
    
    
    
}