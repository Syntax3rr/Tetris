package TetrisUI;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;

import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

import MiscClasses.Button;
import MiscClasses.Slider;

/** The mouse handler class */
public class MouseHandler extends MouseAdapter {
    // The GameRenderer object
    private GameRenderer renderer;
    // A pointer to the list of buttons
    private HashMap<String, Button> buttons;
    // A pointer to the list of sliders
    private HashMap<String, Slider> sliders;

    //SFX Stuff
    private AudioInputStream btnSelectInputStream;
    private AudioInputStream btnClickInputStream;
    private Clip btnSelectClip;
    private Clip btnClickClip;

    /** Constructor
     * @param gameRenderer the GameRenderer Object
     */
    public MouseHandler(GameRenderer gameRenderer) {
        renderer = gameRenderer;
        buttons = renderer.buttonList;
        sliders = renderer.sliderList;

        try {
            btnSelectInputStream = AudioSystem.getAudioInputStream(new File("TetrisUI/Audio/SFX/btnSelect.wav").getAbsoluteFile());
            btnClickInputStream = AudioSystem.getAudioInputStream(new File("TetrisUI/Audio/SFX/btnClick.wav").getAbsoluteFile());
            btnSelectClip = AudioSystem.getClip();
            btnSelectClip.open(btnSelectInputStream);
            btnClickClip = AudioSystem.getClip();
            btnClickClip.open(btnClickInputStream);
        } catch(Exception e) {
            System.out.println(e);
        }
    
    }
    
    /** Check all buttons in the current appstate if they are being hovered over 
     * @param e the MouseEvent
    */
    @Override
    public void mouseMoved(MouseEvent e) {
        int x = e.getX() * 1280 / renderer.getWidth();
        int y = e.getY() * 720 / renderer.getHeight();
        for(Button button : buttons.values()) {
            if(button.btnCollision(renderer.getAppState(), x, y)) {
                if(button.getHoverState() != 1) {
                    FloatControl volumeCtrl = (FloatControl) btnSelectClip.getControl(FloatControl.Type.MASTER_GAIN);
                    volumeCtrl.setValue(20f * (float) Math.log10(Double.parseDouble(renderer.getOptions().getOption("SFX")) / 100));
                    btnSelectClip.setFramePosition(0);
                    btnSelectClip.start();
                    button.setHoverState(1);
                }
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
        int x = e.getX() * 1280 / renderer.getWidth();
        int y = e.getY() * 720 / renderer.getHeight();
        for(Button button : buttons.values()) {
            if(button.getHoverState() != 0 && button.btnCollision(renderer.getAppState(), x, y)) {
                button.setHoverState(2);
            } else {
                button.setHoverState(0);
            }
        }
        for(Slider slider : sliders.values()) {
            if(slider.sliderCollision(renderer.getAppState(), x, y)) {
                slider.onSlide(x);
            }
        }
    }

    /** Update any sliders when dragging
     * @param e the MouseEvent
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        int x = e.getX() * 1280 / renderer.getWidth();
        int y = e.getY() * 720 / renderer.getHeight();
        for(Slider slider : sliders.values()) {
            if(slider.sliderCollision(renderer.getAppState(), x, y)) {
                slider.onSlide(x);
            }
        }
    }

    /** Double-Check the button being hovered over, and run it's onClick Event
     * @param e the MouseEvent
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        int x = e.getX() * 1280 / renderer.getWidth();
        int y = e.getY() * 720 / renderer.getHeight();
        for(Button button : buttons.values()) {
            button.setHoverState(0);
            if(button.btnCollision(renderer.getAppState(), x, y)) { //Check for collision again, in case you move the mouse off.
                FloatControl volumeCtrl = (FloatControl) btnClickClip.getControl(FloatControl.Type.MASTER_GAIN);
                volumeCtrl.setValue(20f * (float) Math.log10(Double.parseDouble(renderer.getOptions().getOption("SFX")) / 100));
                btnClickClip.setFramePosition(0);
                btnClickClip.start();
                button.onPress(); //Run callback function
                break;
            }
        }
    }

}
