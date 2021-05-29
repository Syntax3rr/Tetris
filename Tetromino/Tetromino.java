package Tetromino;

import MiscClasses.Position;

/** Simple tetromino interface, defining what methods each tetromino should have*/
public interface Tetromino {
    /** @return tetromino position data within it's bounding box */
    Character[][] getTetromino();
    /** @return the char used to define the mino for this tetromino */
    Character getMinoType();
    /** @return top-left of the tetromino's bounding box */
    Position getPosition();
    /** @return top-left of the tetromino's hard drop position */
    Position getDropPosition();
    /** Stop the tetromino, cancelling falling and locking timers */
    void stopTetromino();
    /** Try to move the tetromino left 
     * @return if the move was successful
    */
    boolean moveLeft();
    /** Try to move the tetromino right
     * @return if the move was successful
    */
    boolean moveRight();
    /** Hard drop the tetromino, setting the position to the dropPosition*/
    void hardDrop();
    /** Soft drop the tetromino, dividing the drop speed by 20 temporarily */
    void setSoftDrop(boolean isTrue);
    /** Lock the tetromino in x milliseconds */
    void setLockDelay(int customDelay);
    /** @return the lowest position the tetromino can fall to*/
    Position dropCollision();
    /** Tells the tetromino to try spin CCW */
    boolean spinLeft();
    /** Tells the tetromino to try spin CW */
    boolean spinRight();
}