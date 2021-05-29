package Tetromino;

import MiscClasses.Position;
import TetrisGame.GameBoard;

/** Class specific to the L Tetromino */
public class LTetromino extends BasicTetromino {
    /** Mino data specific to the L Tetromino */
    static protected final Character[][][] tetrominoData = new Character[][][]{
        {{'n', 'n', 'L'},
        {'L', 'L', 'L'},
        {'n', 'n', 'n'}},

        {{'n', 'L', 'n'},
        {'n', 'L', 'n'},
        {'n', 'L', 'L'}},
        
        {{'n', 'n', 'n'},
        {'L', 'L', 'L'},
        {'L', 'n', 'n'}},

        {{'L', 'L', 'n'},
        {'n', 'L', 'n'},
        {'n', 'L', 'n'}}
    };

    /** Constructor
     * @param game a pointer to the GameBoard Object
     * @param board a pointer to the 2D game matrix
     * @param levelTime a pointer to the drop speed
     */
    public LTetromino(GameBoard game, Character[][] board, Integer levelTime) {
        super(game, board, levelTime);
        position = new Position(4, 2);
        minoType = 'L';
        tetromino = tetrominoData;
        dropCollision();
        moveDown();
    }

    /** Returns the generic tetromino mino data for a given spin
     * @param spin the spin direction of the mino data
     * @return the generic tetromino mino data */
    static public Character[][] getTetStatic(int spin) {
        return tetrominoData[spin];
    }
}