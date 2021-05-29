package TetrisGame;

import MiscClasses.Position;
import TetrisUI.AppState;
import TetrisUI.GameRenderer;
import Tetromino.*;

import java.util.ArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/** Class which handles the game part of the game, keeping track of game objects and the like */
public class GameBoard {
    //Adding 2 spaces to each side, because of bounding boxes being larger than the tetromino.
    //The only valid positions are x∈[2, 11] & y∈[0, 23] with y∈[0, 3] being above the playing field
    private Character[][] gameBoard = new Character[26][14]; 
    
    //Constants Defining the valid positions.
    private static final int boardLeft = 2;
    private static final int boardRight = 11;
    private static final int boardBottom = 23;

    //Starting level
    private int startLevel;
    //Current level
    private Integer gameLevel;
    //Time it takes for the active tetromino to drop one line, in ms
    private Integer dropTime;
    // Number of line clears until the next level
    private Integer linesCleared;
    // Constant defining the number of lines per level
    private static final int linesPerLevel = 10;
    // Boolean if the last move was Back to Back eligable (Tetris or T-Spin/Mini w/ Line Clears)
    private boolean backToBackLast;
    // Current Score
    private Integer score;
    // The Currently Active Tetromino
    private Tetromino currentTetromino;
    // The Held Tetromino Type
    private Tetromino heldTetromino;
    // If the tetromino was held. This limits the hold function to once per drop.
    private boolean wasHeld;
    // The Tetromino Queue
    private TetrominoQueue queue;
    // The Renderer
    private GameRenderer gameRenderer;
    /** The ScheduledThreadPoolExecutor. 
     * This should probably be private, but it doesn't really do too much, so it's probably fine */
    public ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;

    /** Constructor
     * @param level Starting level
     * @param renderer the GameRenderer Object
     */
    public GameBoard(int level, GameRenderer renderer) {
        score = 0;  
        linesCleared = 0;
        startLevel = level;
        gameLevel = startLevel;
        dropTime = (int) Math.round(Math.pow(0.8 - ((gameLevel - 1) * 0.007), gameLevel - 1) * 1000); //Add drop speed modifications
        currentTetromino = null;
        heldTetromino = null;
        wasHeld = false;
        gameRenderer = renderer;
        scheduledThreadPoolExecutor = renderer.scheduledThreadPoolExecutor;

        for(int i = 0; i < gameBoard.length; i++) { //Setting up the game 
            for(int j = 0; j < gameBoard[i].length; j++) {
                if(i <= boardBottom && j >= boardLeft && j <= boardRight) {
                    gameBoard[i][j] = 'n';
                } else {
                    gameBoard[i][j] = 'x';
                }
            }
        }

        queue = new TetrominoQueue();
        currentTetromino = getTetromino(queue.getNext());
    }

    /** @return The actual game matrix */
    public Character[][] getBoard() {
        return gameBoard;
    }

    /** @return the active tetromino */
    public Tetromino getActiveTet() {
        return currentTetromino;
    }

    /** @return the held tetromino, or null if it doesn't exist */
    public Tetromino getHeldTet() {
        if(heldTetromino == null) return null;
        return heldTetromino;
    }

    /** Swap the held tetromino with the current active one */
    public void swapHeld() {
        if(wasHeld) return;
        wasHeld = true;

        currentTetromino.stopTetromino();
        Tetromino buffer = currentTetromino;
        
        if(heldTetromino == null) {
            currentTetromino = getTetromino(queue.getNext());
        } else {
            currentTetromino = getTetromino(heldTetromino.getMinoType());
        }
        heldTetromino = buffer;
        return;
    }

    /** @return The visual tetromino data from the {@link TetrominoQueue} */
    public ArrayList<Character[][]> getQueue() {
        return queue.getQueue();
    }

    /** @return the calculated score */
    public Integer getScore() {
        return score;
    }

    /** @return the current level */
    public Integer getLevel() {
        return gameLevel;
    }

    /** @return the total number of lines cleared */
    public Integer getLinesCleared() {
        return linesCleared;
    }

    // Returns an object of a given tetromino type given a minoType character
    private Tetromino getTetromino(Character tetrominoID) {
        switch(tetrominoID) {
            case 'I':
                return new ITetromino(this, gameBoard, dropTime);
            case 'O':
                return new OTetromino(this, gameBoard, dropTime);
            case 'T':
                return new TTetromino(this, gameBoard, dropTime);           
            case 'S':
                return new STetromino(this, gameBoard, dropTime);
            case 'Z':
                return new ZTetromino(this, gameBoard, dropTime);
            case 'L':
                return new LTetromino(this, gameBoard, dropTime);
            case 'J':
                return new JTetromino(this, gameBoard, dropTime);
            default:
                System.err.println("Tetromino Recieved was Invalid!");
                return new TTetromino(this, gameBoard, dropTime);
        }
    }

    // Calculate the current level & dropTime
    private void calculateLevel() {
        gameLevel = startLevel + (int) Math.floor(linesCleared / linesPerLevel);
        dropTime = (int) Math.round(Math.pow(0.8 - ((gameLevel - 1) * 0.007), gameLevel - 1) * 1000); //Add drop speed modifications
    }

    /** Push a tetomino to a given position on the board, then {@link calculateScore} and queue the next tetromino 
     * @param minoCharType the minoType character which represents the tetromino
     * @param tetrominoData the visual position data of the minos in the tetromino
     * @param tetrominoPos the top-left position of the tetromino bounding box
     */
    public void pushToBoard(Character minoCharType, Character[][] tetrominoData, Position tetrominoPos) {
        wasHeld = false;

        for(int yOffset = 0; yOffset < tetrominoData.length; yOffset++) {
            for(int xOffset = 0; xOffset < tetrominoData[yOffset].length; xOffset++) {
                if(tetrominoData[yOffset][xOffset] == minoCharType) {
                    if(gameBoard[tetrominoPos.y + yOffset][1 + tetrominoPos.x + xOffset] == 'n') {
                        gameBoard[tetrominoPos.y + yOffset][1 + tetrominoPos.x + xOffset] = minoCharType;
                    } else {
                        System.err.println("GameBoard: Tetromino Is Being Pushed To An Invalid Position, Tetromino Collision Has Failed!");
                        for(Character[] i : gameBoard) {
                            for(Character j : i) {
                                System.err.print(j);
                            }
                            System.err.print('\n');
                        }
                        System.err.println("Trying to push to: " + tetrominoPos.x + " " + tetrominoPos.y);
                    }
                }               
            }
        }

        calculateScore();
        if(gameRenderer.getAppState() != AppState.GAME_OVER) scheduledThreadPoolExecutor.schedule(() -> {currentTetromino = getTetromino(queue.getNext());}, 10, TimeUnit.MILLISECONDS); //ARE of 6f, or 10ms
    }

    //Check if any lines are full and remove them, returning the number of lines cleared
    private int clearLines() {
        int tempLinesCleared = 0;
        nextLine: for(int i = boardBottom; i >= 0; i--) {
            gameBoard[i + tempLinesCleared] = gameBoard[i]; //Shift rows to fill cleared rows
            for(int j = boardLeft; j <= boardRight; j++) { 
                if(gameBoard[i][j] == 'n') continue nextLine; //If the line is incomplete, jump to the next iteration of the outer loop. I feel like this is heretical, but I think it works, so ¯\_(ツ)_/¯
            }
            gameBoard[i] = gameBoard[i + tempLinesCleared];
            tempLinesCleared++; //Increment the number of lines cleared
            gameBoard[i] = new Character[]{'x', 'x', 'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n', 'x', 'x'};
        }
        
        for(int i = 3; i >= 0; i--) {  //Check for game over
            for(int j = boardLeft; j <= boardRight; j++) { 
                if(gameBoard[i][j] != 'n') {
                    currentTetromino.stopTetromino();
                    gameRenderer.setAppState(AppState.GAME_OVER);
                }
            }
        }

        linesCleared += tempLinesCleared;
        calculateLevel();
        return tempLinesCleared;
    }

    // Calculate the score
    private void calculateScore() {
        int tSpinStatus = currentTetromino.getMinoType() == 'T' ? ((TTetromino) currentTetromino).getTSpinState() : 0; //T-Spin or No T-Spin
        int clearLines = clearLines(); //number of lines cleared
        boolean tempBacktoBack = false; //back2back variable
        int tempScore = 0;
        
        switch(tSpinStatus) { 
            case 1:
                tempScore += 100;
            case 0:
                if(clearLines == 4) {
                    tempScore += 800;
                    tempBacktoBack = true;
                } else if (clearLines > 0) {
                    tempScore += (clearLines * 200) - 100;
                }
                break;
            case 2:
                tempScore += 400 * (clearLines + 1);

        }
        
        tempScore *= gameLevel; // Multiply Score by Level

        if(tSpinStatus > 0 && clearLines > 0) tempBacktoBack = true;

        if(backToBackLast && tempBacktoBack) {
            tempScore *= 1.5;
        }

        backToBackLast = (tSpinStatus == 0 && clearLines > 0) ? false : backToBackLast; //If it's a normal line clear, break the B2B. Otherwise continue as is.
        score += tempScore;
    }

}
