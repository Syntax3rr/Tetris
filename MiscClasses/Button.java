package MiscClasses;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.Font;
import java.awt.FontMetrics;

import TetrisUI.AppState;

import java.awt.Graphics2D;
import java.awt.Color;

/**
 * Simple Class For Creating and Managing Buttons
 */
public class Button { //Button Class, I probably could've used JButtons, but this is pretty simple.
    /** Name of the button */
    public String btnName;
    // Button top-left position
    private int top, left;
    //Button size
    private int width, height;
    //Padding around images or text
    static private final int padding = 20;
    //Corner radius
    static private final int radius = 15;
    /** Whether or not the button is hovered over, pressed, etc./ */
    private int hoverState;
    /** Image on the button */ 
    public Image btnIcon;
    /** Text on the button */ 
    public String btnText;
    //Rendered Images of the Button, with a normal, hovered, and pressed states.
    private BufferedImage[] btnImages;
    //What screen this button is visible on
    private AppState visibleAppState;
    //Callback
    Runnable onClickFunction;

    /** Constructor
     * @param name Name of the Button
     * @param img Image to be on the button
     * @param x x position (left)
     * @param y y position (top)
     * @param w Width
     * @param h Height
    */
    public Button(String name, Image img, int x, int y, int w, int h, AppState visibleScreen, Runnable callback) {
        btnName = name;
        btnIcon = img;
        btnText = null;
        left = x - (padding / 2);
        top = y - (padding / 2);
        width = w;
        height = h;
        visibleAppState = visibleScreen;
        btnImages = new BufferedImage[3];
        onClickFunction = callback;
        createButtonImages();        
    }

    /** {@link Button(String, Image, int, int, int, int, AppState, Runnable)} with text instead of an image*/
    public Button(String name, String text, int x, int y, int w, int h, AppState visibleScreen, Runnable callback) {
        btnName = name;
        btnIcon = null;
        btnText = text;
        left = x - (padding / 4);
        top = y - (padding / 4);
        width = w;
        height = h;
        visibleAppState = visibleScreen;
        btnImages = new BufferedImage[3];
        onClickFunction = callback;
        createButtonImages();        
    }

    /** Overload {@link Button(String, BufferedImage, int, int, int, int, Appstate, Runnable)} using image size as button size */
    public Button(String name, Image img, int x, int y, AppState visibleScreen, Runnable callback) {
        this(name, img, x, y, img.getWidth(null) + padding, img.getHeight(null) + padding, visibleScreen, callback);
    }

    /** Overload {@link Button(String, String, int, int, int, int, Appstate, Runnable)} using string size as button size */
    public Button(String name, String text, int x, int y, AppState visibleScreen, Runnable callback) {
        this(name, text, x, y, 0, 0, visibleScreen, callback);
    }

    //Create an image from the given btnText.
    private void createButtonIcon() {
        if(btnText == null) return;
        BufferedImage temp = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        
        Graphics2D tempGraphics = temp.createGraphics();
        tempGraphics.setFont(new Font("Courier New", Font.PLAIN, 32));
        FontMetrics fontMetrics = tempGraphics.getFontMetrics();

        if(width == 0 || height == 0 || width < fontMetrics.stringWidth(btnText) + padding) {
            width = fontMetrics.stringWidth(btnText) + (padding / 2);
            height = fontMetrics.getHeight() + (padding / 2);
        }

        btnIcon = new BufferedImage(fontMetrics.stringWidth(btnText), fontMetrics.getHeight(), BufferedImage.TYPE_INT_ARGB);

        Graphics2D btnIconGraphics = ((BufferedImage) btnIcon).createGraphics();
        btnIconGraphics.setColor(Color.WHITE);
        btnIconGraphics.setFont(new Font("Courier New", Font.PLAIN, 32));
        btnIconGraphics.drawString(btnText, 0, fontMetrics.getAscent());

        tempGraphics.dispose();
        btnIconGraphics.dispose();        
        temp.flush();
    }

    /** Generates 3 images for the button: Normal, Hovered, Pressed */
    public void createButtonImages() {
        createButtonIcon();
        for(int i = 0; i < 3; i++) {
            btnImages[i] = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D btnG = btnImages[i].createGraphics();
            btnG.setColor(new Color(0f, 0f, 0f, .3f));
            btnG.fillRoundRect(0, 0, width, height, radius, radius);
            btnG.drawImage(btnIcon, (width / 2) - (btnIcon.getWidth(null) / 2), (height / 2) - (btnIcon.getHeight(null) / 2), null);
            btnG.setColor(new Color(0f, 0f, 0f, 0.2f * i));
            btnG.fillRoundRect(0, 0, width, height, radius, radius);
            btnG.setColor(Color.black);
            btnG.dispose();
        }
    }

    /** Runs the onClickFuntion */
    public void onPress() {
        onClickFunction.run();
    }

    /** Draw the button, darkening it depending on the hoverstate
     * @return The {@link BufferedImage} of the button
    */
    public BufferedImage getBtnImg() {
        return btnImages[hoverState];  
    }

    /** Gets the leftmost x coordinate of the button
     * @return x coordinates for the left side of the button */
    public int getX() {
        return left;
    }
    /** Gets the topmost y coordinate of the button
     * @return y coordinates for the top of the button */
    public int getY() {
        return top;
    }

    /** Gets the {@link setHoverState hover state}
     * @return whether or not the button is being hovered over, pressed, etc. {@link setHoverState} */
    public int getHoverState() {
        return hoverState;
    }

    /** Set whether or not the button is being hovered over 
     * @param isHovered whether or not the button is being hovered over
    */
    public void setHoverState(int isHovered) {
        hoverState = (isHovered % 3); //input auto scaled to 0, 1, or 2 
    }

    /** Changes the string text, and updates the images accordingly */
    public void setText(String text) {
        btnText = text;
        createButtonImages();
    }

    /** Calculates if (x, y) is within the button 
     * @param x x coordinate to be checked
     * @param y y coordinate to be checked
     * @return A boolean of whether or not the coordinates are contained within the button
    */
    public boolean btnCollision(AppState currentScreen, double x, double y) {
        if((visibleAppState == null || currentScreen == visibleAppState) && x >= left && x <= left + width && y >= top && y <= top + height) return true;
        return false;
    }
}
