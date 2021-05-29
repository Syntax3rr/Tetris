package Tetromino;

import java.util.Map;
import static java.util.Map.entry;

import MiscClasses.Position;
import TetrisGame.GameBoard;

/** Class specific to the I Tetromino */
public class ITetromino extends BasicTetromino {
    /** I Tetromino mino data */
    static protected final Character[][][] tetrominoData = new Character[][][]{
        {{'n', 'n', 'n', 'n'},
        {'I', 'I', 'I', 'I'},
        {'n', 'n', 'n', 'n'},
        {'n', 'n', 'n', 'n'}},

        {{'n', 'n', 'I', 'n'},
        {'n', 'n', 'I', 'n'},
        {'n', 'n', 'I', 'n'},
        {'n', 'n', 'I', 'n'}},

        {{'n', 'n', 'n', 'n'},
        {'n', 'n', 'n', 'n'},
        {'I', 'I', 'I', 'I'},
        {'n', 'n', 'n', 'n'}},

        {{'n', 'I', 'n', 'n'},
        {'n', 'I', 'n', 'n'},
        {'n', 'I', 'n', 'n'},
        {'n', 'I', 'n', 'n'}}
    };
    /** I Tetromino special kickData */
    protected final Map<Integer, Map<Integer, Position>> kickData = Map.ofEntries(
        entry(0, Map.ofEntries( //For Rotations 1<->2 & 3<->0
            entry(0, new Position(0, 0)),
            entry(1, new Position(1, 0)),
            entry(2, new Position(-2, 0)),
            entry(3, new Position(1, -2)),
            entry(4, new Position(-2, 1)),
            entry(-1, new Position(-1, 0)),
            entry(-2, new Position(2, 0)),
            entry(-3, new Position(-1, 2)),
            entry(-4, new Position(2, -1))
        )),
        entry(1, Map.ofEntries( //For Rotations 0<->1 & 2<->3
            entry(0, new Position(0, 0)),
            entry(1, new Position(2, 0)),
            entry(2, new Position(-1, 0)),
            entry(3, new Position(2, 1)),
            entry(4, new Position(-1, -2)),
            entry(-1, new Position(-2, 0)),
            entry(-2, new Position(1, 0)),
            entry(-3, new Position(-2, -1)),
            entry(-4, new Position(1, 2))
        ))
    );

    /** Constructor
     * @param game a pointer to the GameBoard Object
     * @param board a pointer to the 2D game matrix
     * @param levelTime a pointer to the drop speed
     */
    public ITetromino(GameBoard game, Character[][] board, Integer levelTime) {
        super(game, board, levelTime);
        minoType = 'I';
        position = new Position(4, 2);
        tetromino = tetrominoData;
        dropCollision(); //These should be in BasicTetromino, but I don't feel like passing position & tetromino collision data up to the superclass.
        moveDown();
    }

    /** Returns the generic tetromino mino data for a given spin
     * @param spin the spin direction of the mino data
     * @return the generic tetromino mino data */
    static public Character[][] getTetStatic(int spin) {
        return tetrominoData[spin];
    }

    @Override
    /** Overloads the {@link BasicTetromino#kickTest(int)} to work with the expanded I Tetromino kick rules
     * @param testSpinPostion the spin position to test
     * @return whether or not the tetromino can spin
     */
    protected boolean kickTest(int testSpinPosition) {
        int testDir = (spinPosition == 0 || testSpinPosition == 2) ? -1 : 1;
        int expandI = (spinPosition + testSpinPosition == 3) ? 0 : 1;
        for(int i = 0; i != 4 * testDir; i += testDir ) {
            if(boardCollision(tetromino[testSpinPosition], kickData.get(expandI).get(i))) {
                position.x += kickData.get(expandI).get(i).x;
                position.y += kickData.get(expandI).get(i).y;
                spinPosition = testSpinPosition;
                refresh('S');
                return true;
            }
        }
        return false;
    }
}
