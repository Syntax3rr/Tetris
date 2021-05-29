package Tetromino;

import MiscClasses.Position;

import TetrisGame.GameBoard;

/** Class specific to the T Tetromino */
public class TTetromino extends BasicTetromino {
    /** The mino data specific to the T Tetromino, containing markers for T-spin checking */
    static protected final Character[][][] tetrominoData = new Character[][][]{ //I'm just going to hard-code these in, which removes some variablility.
        {{'a', 'T', 'b'},
        {'T', 'T', 'T'},
        {'c', 'n', 'd'}},

        {{'c', 'T', 'a'},
        {'n', 'T', 'T'},
        {'d', 'T', 'b'}},

        {{'d', 'n', 'c'},
        {'T', 'T', 'T'},
        {'b', 'T', 'a'}},

        {{'b', 'T', 'd'},
        {'T', 'T', 'n'},
        {'a', 'T', 'c'}}
    };
    /** The clean mino data for this tetromino */
    static protected final Character[][][] tetrominoClean = {
        {{'n', 'T', 'n'},
        {'T', 'T', 'T'},
        {'n', 'n', 'n'}},

        {{'n', 'T', 'n'},
        {'n', 'T', 'T'},
        {'n', 'T', 'n'}},

        {{'n', 'n', 'n'},
        {'T', 'T', 'T'},
        {'n', 'T', 'n'}},

        {{'n', 'T', 'n'},
        {'T', 'T', 'n'},
        {'n', 'T', 'n'}}
    };
    /** If the tetromino detected a T-Spin, T-Spin Mini, or not */
    public int tSpinState;

    /** Constructor
     * @param game a pointer to the GameBoard Object
     * @param board a pointer to the 2D game matrix
     * @param levelTime a pointer to the drop speed
     */
    public TTetromino(GameBoard game, Character[][] board, Integer levelTime) {
        super(game, board, levelTime);
        position = new Position(4, 2);
        tSpinState = 0;
        minoType = 'T';
        tetromino = tetrominoData;
    dropCollision();
    moveDown();
    }

    /** Returns the generic tetromino mino data for a given spin
     * @param spin the spin direction of the mino data
     * @return the generic tetromino mino data */
    static public Character[][] getTetStatic(int spin) {
        return tetrominoClean[spin];
    }
    
    /** Returns the tetromino visual data, getting rid of the T-Spin detecting variables
     * @return the tetromino with respect to it's bounding box
     */
    @Override
    public Character[][] getTetromino() {
        return tetrominoClean[spinPosition];
    }

    /** @return if the tetromino detected a T-Spin (2) a T-Spin Mini (1) or none (0) */
    public int getTSpinState() {
        return lastMoves.get(0) == 'S' ? tSpinState : 0;
    }

    /** Overload for {@link BasicTetromino#lockDown()} using the clean mino data */
    @Override
    protected void lockDown() {
        gameBoardObj.pushToBoard(minoType, tetrominoClean[spinPosition], position);
    }

    /** Overload for {@link BasicTetromino#boardCollision()} that also checks for T-Spins */
    @Override
    /** Tests the collision at a certain position offset to see if it's valid.
     * @param testTetromino The tetromino data to test
     * @param testPos Position offset to Test
     * @return Whether or not the given position is valid
     */
    protected boolean boardCollision(Character[][] testTetromino, Position testPos) {
        boolean a, b, c, d; a = b = c = d = false;
        for(int offSetY = 0; offSetY < testTetromino.length; offSetY++ ) {
            for(int offSetX = 0; offSetX < testTetromino[0].length; offSetX++ ) {
                if(gameBoard[position.y + testPos.y + offSetY][position.x + testPos.x + offSetX + 1] != 'n') {
                    switch(testTetromino[offSetY][offSetX]) {
                        case 'T': 
                            return false;
                        case 'a':
                            a = true;
                            break;
                        case 'b':
                            b = true;
                            break;
                        case 'c':
                            c = true;
                            break;
                        case 'd':
                            d = true;
                            break;
                        default:
                            continue;
                    }
                }
            }
        }

        if((a || b) && c && d) { //T-Spin mini
            tSpinState = 1;
        } else if((a && b && (c || d))) { //T-Spin
            tSpinState = 2;
        } else {
            tSpinState = 0;
        }
        return true;
    }
}