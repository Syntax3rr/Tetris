package Tetromino;

import MiscClasses.Position;
import TetrisGame.Board;

/** Class specific to the S Tetromino */
public class STetromino extends BasicTetromino {
    /** Mino data specific to the S Tetromino */
    static protected final Character[][][] tetrominoData = new Character[][][]{
        {{'n', 'S', 'S'},
        {'S', 'S', 'n'},
        {'n', 'n', 'n'}},

        {{'n', 'S', 'n'},
        {'n', 'S', 'S'},
        {'n', 'n', 'S'}},

        {{'n', 'n', 'n'},
        {'n', 'S', 'S'},
        {'S', 'S', 'n'}},

        {{'S', 'n', 'n'},
        {'S', 'S', 'n'},
        {'n', 'S', 'n'}}
    };
    
    /** Constructor
     * @param game a pointer to the Board Object
     * @param board a pointer to the 2D game matrix
     * @param levelTime a pointer to the drop speed
     */
    public STetromino(Board game, Character[][] board, Integer levelTime) {
        super(game, board, levelTime);
        position = new Position(4, 2);
        minoType = 'S';
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