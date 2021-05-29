package TetrisUI;

import java.util.HashMap;
/** An object storing the controls of the Game */
public class Options {
    //Default Controls
    private final HashMap<String, String> defaultControls = new HashMap<String, String>() {{ // I may or may not have learned about this double bracket magic recently, since this needs to be mutable. Soooo I'll leave the rest as immutable Maps.
        put("Pause", "Escape");
        put("MoveLeft", "Left");
        put("MoveRight", "Right");
        put("SpinLeft", "Z");
        put("SpinRight", "X");
        put("SoftDrop", "Down");
        put("HardDrop", "Space");
        put("Hold", "C");
    }};

    //Current Controls
    private HashMap<String, String> controls = new HashMap<String, String>() {{
        put("Pause", "Escape");
        put("MoveLeft", "Left");
        put("MoveRight", "Right");
        put("SpinLeft", "Z");
        put("SpinRight", "X");
        put("SoftDrop", "Down");
        put("HardDrop", "Space");
        put("Hold", "C");
    }};

    /** Reset current controls to the default ones */
    public void resetOptions() {
        controls.replaceAll((k, v) -> { return defaultControls.get(k); });
    }
    
    /** returns the button fora given control */
    public String getControl(String control) {
        return controls.get(control);
    }

    /** Changes the control to a specific key
     * @param control the control to change
     * @param key the key to change it to
     */
    public void setControl(String control, String key) {
        controls.replace(control, key);
    }
}
