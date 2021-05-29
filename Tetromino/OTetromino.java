package Tetromino;

import MiscClasses.Position;

import TetrisGame.GameBoard;

/** Class specific to the O Tetromino */
public class OTetromino extends BasicTetromino {
    /** Mino data specific to the O Tetromino */
    static protected final Character[][][] tetrominoData = new Character[][][]{ // Ideally, I would overload all the methods to only use a Character[][], but I can't really be bothered, lol.
        {{'O', 'O'},
        {'O', 'O'}}
        };
        
    /** Constructor
     * @param game a pointer to the GameBoard Object
     * @param board a pointer to the 2D game matrix
     * @param levelTime a pointer to the drop speed
     */
    public OTetromino(GameBoard game, Character[][] board, Integer levelTime) {
        super(game, board, levelTime);
        position = new Position(5, 2);
        minoType = 'O';
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

    /** O Tetrominos do not spin. */
    @Override
    public boolean spinLeft() {
        return false;
    }

    /** O Tetrominos do not spin. */
    @Override
    public boolean spinRight() {
        return false;
    }
}