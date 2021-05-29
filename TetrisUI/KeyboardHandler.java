package TetrisUI;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.security.KeyException;
import java.util.HashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ScheduledFuture;

/** Keyboard Handler Class */
public class KeyboardHandler extends KeyAdapter {
    // Keylisteners
    private HashMap<String, Runnable> listeners;
    // The GameRenderer object
    private GameRenderer renderer;
    // The ScheduledThreadPoolExecutor object
    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
    /** A map of the different keys as well as thier pressed status */
    protected HashMap<String, Boolean> keyMap;
    /** The newest key pressed */
    protected String newKey = null;
    /** The current move key being pressed */
    protected String moveKey = null; //I only need one key at a time, so this works.
    /** Softdrop key status */
    protected boolean isSoftDropping = false;
    //The future of the autorepeated key
    private ScheduledFuture<?> keyAutoRepeatFuture;

    /** Constructor
     * @param gameRenderer the GameRenderer Object
     */
    public KeyboardHandler(GameRenderer gameRenderer) {
        renderer = gameRenderer;
        scheduledThreadPoolExecutor = renderer.scheduledThreadPoolExecutor;

        keyMap = new HashMap<String, Boolean>();
        listeners = new HashMap<String, Runnable>();
    }

    /** @return the newest pressed key */
    public String getNewKey() {
        return newKey;
    }

    /** Set a new keyListener
     * @param key the key to listen for
     * @param callback the callback to run
     */
    public void addListener(String key, Runnable callback) {
        listeners.put(key, callback);
    }

    /** Remove a keylistener
     * @param key the key to remove it's listener
     */
    public void removeListener(String key) {
        listeners.remove(key);
    }

    /** Clear all keylisteners */
    public void clearListeners() {
        listeners.clear();
    }

    /** Add a one-time key listener that runs on any keypress*/
    public void addListener(Runnable callback) {
        listeners.put("any", callback);
    }

    /** Run the respective listener callback for a given keypress
     * @param pressedKey the key that was pressed
     */
    public void keyPressEvent(String pressedKey) {
        if(listeners.get("any") != null) {
            listeners.get("any").run();
            listeners.remove("any");
        }
        if(listeners.get(renderer.getOptions().getControl("SoftDrop")) != null && !pressedKey.equals(renderer.getOptions().getControl("SoftDrop"))) {
            listeners.get(renderer.getOptions().getControl("SoftDrop")).run();
        }
        
        if(renderer.getAppState() == AppState.PLAY && listeners.get(pressedKey) != null) {
            listeners.get(pressedKey).run();
        }
    }

    /** Autorepeat a given key
     * @param pressedKey the key pressed
     */
    public void keyAutoRepeat(String pressedKey) {
        keyPressEvent(pressedKey);
        keyAutoRepeatFuture = scheduledThreadPoolExecutor.schedule(() -> {keyAutoRepeat(pressedKey);}, 33, TimeUnit.MILLISECONDS); //ARR of 33ms
    }

    /** Detect a keypress and handle what happens to it
     * @param e the keyevent
     */
    public void keyPressed(KeyEvent e) {
        String thisKey = KeyEvent.getKeyText(e.getKeyCode());
        if(!keyMap.containsKey(thisKey)) keyMap.put(thisKey, false);
        if(keyMap.get(thisKey) == true) return;
        newKey = thisKey;
        keyMap.replace(thisKey, true);

        if(thisKey == renderer.getOptions().getControl("SoftDrop")) isSoftDropping = true;

        keyPressEvent(thisKey);
        if(thisKey.equals(renderer.getOptions().getControl("MoveLeft")) || thisKey.equals(renderer.getOptions().getControl("MoveRight"))) {
            moveKey = thisKey;
            if(keyAutoRepeatFuture != null) keyAutoRepeatFuture.cancel(true);
            keyAutoRepeatFuture = scheduledThreadPoolExecutor.schedule(() -> {keyAutoRepeat(thisKey);}, 200, TimeUnit.MILLISECONDS); //DAS of 200ms
        } 
    }

    /** Detect a key release and handle it
     * @param e the key event
     */
    public void keyReleased(KeyEvent e) {
        String thisKey = KeyEvent.getKeyText(e.getKeyCode());
        keyMap.replace(thisKey, false);

        if(thisKey == renderer.getOptions().getControl("SoftDrop")) isSoftDropping = false;

        if(thisKey.equals(renderer.getOptions().getControl("MoveLeft")) || thisKey.equals(renderer.getOptions().getControl("MoveRight"))) {
            String oppositeMoveKey = thisKey.equals(renderer.getOptions().getControl("MoveLeft")) ? renderer.getOptions().getControl("MoveRight") : renderer.getOptions().getControl("MoveLeft");
            if(keyAutoRepeatFuture != null) keyAutoRepeatFuture.cancel(true);
            if(moveKey == thisKey && keyMap.containsKey(oppositeMoveKey) && keyMap.get(oppositeMoveKey)) keyAutoRepeatFuture = scheduledThreadPoolExecutor.schedule(() -> {keyAutoRepeat(oppositeMoveKey);}, 200, TimeUnit.MILLISECONDS); //DAS of 200ms
        } 

        if(listeners.get(renderer.getOptions().getControl("SoftDrop")) != null && isSoftDropping == false) {
            listeners.get(renderer.getOptions().getControl("SoftDrop")).run();
        }
    }

}
