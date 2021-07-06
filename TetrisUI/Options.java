package TetrisUI;

import java.util.HashMap;
/** An object storing the controls of the Game */
public class Options {
    //Default Controls
    private final HashMap<String, String> defaultOptions = new HashMap<String, String>() {{ // I may or may not have learned about this double bracket magic recently, since this needs to be mutable. Soooo I'll leave the rest as immutable Maps.
        put("Pause", "Escape");
        put("MoveLeft", "Left");
        put("MoveRight", "Right");
        put("SpinLeft", "Z");
        put("SpinRight", "X");
        put("SoftDrop", "Down");
        put("HardDrop", "Space");
        put("Hold", "C");
        put("StartLevel", "1");
        put("Music", "50");
        put("SFX", "50");
    }};

    //Current Controls
    private HashMap<String, String> options = new HashMap<String, String>() {{
        put("Pause", "Escape");
        put("MoveLeft", "Left");
        put("MoveRight", "Right");
        put("SpinLeft", "Z");
        put("SpinRight", "X");
        put("SoftDrop", "Down");
        put("HardDrop", "Space");
        put("Hold", "C");
        put("StartLevel", "1");
        put("Music", "50");
        put("SFX", "50");
    }};

    /** Reset current controls to the default ones */
    public void resetOptions() {
        options.replaceAll((k, v) -> { return defaultOptions.get(k); });
    }
    
    /** returns the setting for a given option 
     * @param option the option to look for
     * @return the setting of the given option
    */
    public String getOption(String option) {
        return options.get(option);
    }

    /** Changes the setting of a given option
     * @param option the control to change
     * @param setting the key to change it to
     */
    public void setOption(String option, String setting) {
        options.replace(option, setting);
    }
}
