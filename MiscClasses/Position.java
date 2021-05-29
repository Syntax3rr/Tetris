package MiscClasses;

/** A simple class containing two integer values, x and y 
 * I'm treating it more like a data structure.
*/
public class Position {
    /** x position, stored as an integer */
    public int x;
    /** y position, stored as an integer */
    public int y;

    /** Constructor
     * @param setX the X position
     * @param setY the Y position
     */
    public Position(int setX, int setY) {
        set(setX, setY);
    }

    /** Simple way of setting both x and y simultaneously */
    public void set(int setX, int setY) {
        x = setX;
        y = setY;
    }
}