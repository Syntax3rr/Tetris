package Tetromino;

import MiscClasses.Position;

import TetrisGame.GameBoard;

/** Class specific to the Z Tetromino */
public class ZTetromino extends BasicTetromino {
    /** Mino data specific to the Z Tetromino */
    static protected final Character[][][] tetrominoData = new Character[][][]{
        {{'Z', 'Z', 'n'},
        {'n', 'Z', 'Z'},
        {'n', 'n', 'n'}},

        {{'n', 'n', 'Z'},
        {'n', 'Z', 'Z'},
        {'n', 'Z', 'n'}},

        {{'n', 'n', 'n'},
        {'Z', 'Z', 'n'},
        {'n', 'Z', 'Z'}},

        {{'n', 'Z', 'n'},
        {'Z', 'Z', 'n'},
        {'Z', 'n', 'n'}}
    }; 

    /** Constructor
     * @param game a pointer to the GameBoard Object
     * @param board a pointer to the 2D game matrix
     * @param levelTime a pointer to the drop speed
     */
    public ZTetromino(GameBoard game, Character[][] board, Integer levelTime) {
        super(game, board, levelTime);
        position = new Position(4, 2);
        minoType = 'Z';
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