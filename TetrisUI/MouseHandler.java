package TetrisUI;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;

import MiscClasses.Button;

/** The mouse handler class */
public class MouseHandler extends MouseAdapter {
    // The GameRenderer object
    private GameRenderer renderer;
    // A pointer to the list of buttons
    private HashMap<String, Button> buttons;
    
    /** Constructor
     * @param gameRenderer the GameRenderer Object
     */
    public MouseHandler(GameRenderer gameRenderer) {
        renderer = gameRenderer;
        buttons = renderer.buttonList;
    
    }
    
    /** Check all buttons in the current appstate if they are being hovered over 
     * @param e the MouseEvent
    */
    @Override
    public void mouseMoved(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        for(Button button : buttons.values()) {
            if(button.btnCollision(renderer.getAppState(), x, y)) {
                button.setHoverState(1);
            } else {
                button.setHoverState(0);
            }
        }
    }

    /** Change the state of any buttons being hovered over on a press
     * @param e the MouseEvent
     */
    @Override
    public void mousePressed(MouseEvent e) {
        for(Button button : buttons.values()) {
            if(button.getHoverState() != 0) {
                button.setHoverState(2);
            } else {
                button.setHoverState(0);
            }
        }
    }

    /** Double-Check the button being hovered over, and run it's onClick Event
     * @param e the MouseEvent
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        for(Button button : buttons.values()) {
            button.setHoverState(0);
            if(button.btnCollision(renderer.getAppState(), x, y)) { //Check for collision again, in case you move the mouse off.
                button.onPress(); //Run callback function
                break;
            }
        }
    }

}
