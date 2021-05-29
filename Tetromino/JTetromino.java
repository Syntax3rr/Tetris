package Tetromino;

import MiscClasses.Position;
import TetrisGame.GameBoard;

/** Class specific to the J Tetromino */
public class JTetromino extends BasicTetromino {
    /** Mino data specific to the J Tetromino */
    static protected final Character[][][] tetrominoData = new Character[][][]{
        {{'J', 'n', 'n'},
        {'J', 'J', 'J'},
        {'n', 'n', 'n'}},

        {{'n', 'J', 'J'},
        {'n', 'J', 'n'},
        {'n', 'J', 'n'}},
        
        {{'n', 'n', 'n'},
        {'J', 'J', 'J'},
        {'n', 'n', 'J'}},

        {{'n', 'J', 'n'},
        {'n', 'J', 'n'},
        {'J', 'J', 'n'}}
    };

    /** Constructor
     * @param game a pointer to the GameBoard Object
     * @param board a pointer to the 2D game matrix
     * @param levelTime a pointer to the drop speed
     */
    public JTetromino(GameBoard game, Character[][] board, Integer levelTime) {
        super(game, board, levelTime);
        position = new Position(4, 2);
        minoType = 'J';
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