package Tetromino;

import java.util.Map;
import static java.util.Map.entry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

import MiscClasses.Position;
import MiscClasses.AudioStreamData;
import TetrisGame.Board;

/** Basic Implementation of the Tetromino Interface to be expanded on by subclasses */
abstract class BasicTetromino implements Tetromino  {
    /** Object ScheduledThreadPoolExecutor */
    protected ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
    /** A future for the lock timer */
    protected ArrayList<ScheduledFuture<?>> lockTimer;
    /** A future for the drop timer */
    protected ScheduledFuture<?> dropTimer;
    /** Number of Extended Lockdown Movements */
    protected int lockMoves;
    /** Last 4 moves */
    protected ArrayList<Character> lastMoves;
    /** The lowest reached position for the Extended Lockdown Movements Rules */
    protected int lowestPositionReached;
    /** Top-left position of the tetromino bounding box */
    protected Position position;
    /** The lowest position this tetromino can fall to */
    protected Position dropPosition;
    /** An array of every spin position of the tetromino within it's bounding box */
    //Originally, I just had one, which I then rotated to calculate the next version, but hardcoding everything is probably more efficient.
    //This would probably be more efficinet if I just handled every line as a binary number or something, but that would be quite a hassle.
    protected Character[][][] tetromino;
    /** The symbol used for this tetrominos minos */
    protected char minoType;
    /** Spin position relative to it's default state in number of rotations clockwise*/
    protected int spinPosition = 0; //0, 1, 2, 3
    /** The lowest y position this tetromino can currently drop to */
    protected Position dropPostition;
    /** A pointer to the Board Object */
    final Board gameBoardObj;
    /** A pointer to the current gameboard */
    final Character[][] gameBoard;
    /** A Pointer to the current level */
    final protected Integer dropTime;
    /** Whether or to add the softDrop modifier */
    protected int softDrop; //0 or 1
    /** Number of lines dropped during soft or hard drop (hard drop counts for double) for scoring purposes */
    protected int specialDropLines;
    /** Whether or not to accept inputs */
    protected boolean takeInputs = true;
    /** Contains the different points to check for wall and floor kicks */
    protected final Map<Integer, Map<Integer, Position>> kickData = Map.ofEntries(
        entry(0, Map.ofEntries( //For Rotations 0<->1 & 1<->2
            entry(0, new Position(0, 0)),
            entry(1, new Position(1, 0)),
            entry(2, new Position(1, 1)),
            entry(3, new Position(0, -2)),
            entry(4, new Position(1, -2)),
            entry(-1, new Position(-1, 0)),
            entry(-2, new Position(-1, -1)),
            entry(-3, new Position(0, 2)),
            entry(-4, new Position(-1, 2))
        )),
        entry(1, Map.ofEntries( //For Rotations 2<->3 & 3<->0
            entry(0, new Position(0, 0)),
            entry(1, new Position(1, 0)),
            entry(2, new Position(1, -1)),
            entry(3, new Position(0, 2)),
            entry(4, new Position(1, 2)),
            entry(-1, new Position(-1, 0)),
            entry(-2, new Position(-1, 1)),
            entry(-3, new Position(0, -2)),
            entry(-4, new Position(-1, -2))
        ))
    );
    
    //Stuff for SFX
    static String[] soundTypes = {"moveTrue", "moveFalse", "spinTrue", "spinFalse", "softDrop"};
    HashMap<String, AudioStreamData> sfxAudio;

    /** Constructor
     * @param game A pointer to the Board Object
     * @param board A pointer to the game matrix
     * @param levelTime A pointer to the drop speed of the tetromino
     */
    BasicTetromino(Board game, Character[][] board, Integer levelTime) {
        gameBoardObj = game;
        gameBoard = board;
        dropTime = levelTime;
        lowestPositionReached = 0;
        softDrop = 0;
        specialDropLines = 0;
        lastMoves = new ArrayList<Character>();

        scheduledThreadPoolExecutor = game.getSTPE();
        lockTimer = new ArrayList<ScheduledFuture<?>>(); //Funky workaround, letting me indirectly see if lockTimer currently exists.
    
        try { //Initialize all sound clips
            sfxAudio = new HashMap<String, AudioStreamData>();
            for(String i : soundTypes) {
                AudioInputStream tempAIS = AudioSystem.getAudioInputStream(new File("TetrisUI/Audio/SFX/" + i + ".wav").getAbsoluteFile());
                sfxAudio.put(i, new AudioStreamData(tempAIS));
            }
        } catch(Exception e) {
            System.err.println("BasicTetromino: " + e);
        }
    }

    /** @return the mino data of the tetromino with respect to it's bounding box v*/
    public Character[][] getTetromino() {
        return tetromino[spinPosition];
    }

    /** @return the minoType character, which represents the tetromino */
    public Character getMinoType() {
        return minoType;
    }

    /** @return the top-left position of the tetromino bounding box */
    public Position getPosition() {
        return position;
    }

    /** @return the top-left position of the lowest position this tetromino can drop to */
    public Position getDropPosition() {
        return dropPosition;
    }

    /** @return the number of lines dropped via softdrop or harddrop (Harddrop lines are counted twice) */
    public int getSpecialDropLines() {
        return specialDropLines;
    }

    /** Stops the tetromino, cancelling all scheduled futures */
    public void stopTetromino() {
        takeInputs = false;
        if(!lockTimer.isEmpty()) { 
            lockTimer.get(0).cancel(true);
        }
        dropTimer.cancel(true);
    }

    /** Makes a copy of, then plays a requested sound 
     * @param soundType the name of the sound to be played
    */
    protected void playSound(String soundType) {
        try {
        Clip tempClip = (Clip) AudioSystem.getLine(sfxAudio.get(soundType).getInfo());
        tempClip.open(sfxAudio.get(soundType).getFormat(), sfxAudio.get(soundType).getByteStream(), 0, sfxAudio.get(soundType).getSize());
        FloatControl volumeCtrl = (FloatControl) tempClip.getControl(FloatControl.Type.MASTER_GAIN);
        volumeCtrl.setValue(20f * (float) Math.log10(Double.parseDouble(gameBoardObj.getOptions().getOption("SFX")) / 100));
        tempClip.start();
        } catch (Exception e) {
            System.out.println("BasicTetromino.playSound(): " + e);
        }
    }

    /** Attempts to move the tetromino left
     * @return if the attempt was succesful
    */
    public boolean moveLeft() {
        if(!takeInputs) return false;
        if(boardCollision(tetromino[spinPosition], new Position(-1, 0))) {
            position.x--;
            playSound("moveTrue");
            refresh('L');
            return true;
        }
        playSound("moveFalse");
        return false;
        
    }

    /** Attempts to move the tetromino right
     * @return if the attempt was succesful
    */
    public boolean moveRight() {
        if(!takeInputs) return false;
        if(boardCollision(tetromino[spinPosition], new Position(1, 0))) {
            position.x++;
            playSound("moveTrue");
            refresh('R');
            return true;
        }
        playSound("moveFalse");
        return false;
    }

    /** Attempts to move the tetromino down, recursivly calling itself back.
     * @return if the attempt was succesful
    */
    protected boolean moveDown() {
        // Callback After x seconds according to the equation (0.8 - ((level - 1) * 0.007)) ^ (level-1)    
        if(dropTimer != null) dropTimer.cancel(true); //Cancel the next sceduled fall, in case of soft drops and the like
        dropTimer = scheduledThreadPoolExecutor.schedule(() -> {moveDown();}, (int) (dropTime / Math.pow(20, softDrop)), TimeUnit.MILLISECONDS);                                    //I'm using Math.pow as a toggle control for softDrop.
        if(position.y < dropPosition.y && boardCollision(tetromino[spinPosition], new Position(0, -1))) { //Test collision of the position directly below before falling.
            position.y++;
            if(softDrop == 1) specialDropLines++;
            refresh('D');
            return true;
        } else {
            if(lockTimer.isEmpty()) refresh('D');
            return false;
        }
    }

    /** Drops the tetromino to the lowest possible position, and locking it into place */
    public void hardDrop() {
        if(!takeInputs) return;
        specialDropLines += 2 * (dropPosition.y - position.y);
        position.y = dropPosition.y; //This feels wrong somehow...
        setLockDelay(1);
    }

    /** Hard drop the tetromino, setting the position to the dropPosition.
     * @param firmDrop if true, the tetromino does not automatically lock on a harddrop.
    */
    public void hardDrop(boolean firmDrop) {
        if(!takeInputs) return;
        position.y = dropPosition.y; //This feels wrong somehow...
        setLockDelay(firmDrop ? 500 : 1);
    }
    
    /** Sets the softDrop status, increasing the fall speed of the tetromino by 20 fold if true
     * @param isTrue if the softDrop status is to be set to true or not
     */
    public void setSoftDrop(boolean isTrue) {
        if(!takeInputs) return;
        int isTrueToInt = isTrue ? 1 : 0;
        if(softDrop == isTrueToInt) return;
        softDrop = isTrueToInt;
        if(isTrue) {
            moveDown();
            playSound("softDrop");
        }
        
    }

    /** Sets (or resets) the lockdown delay to a custom number 
     * @param customDelay the amount of time to wait for in ms
    */
    public void setLockDelay(int customDelay) {
        if(!lockTimer.isEmpty()) lockTimer.remove(0).cancel(true);
        lockTimer.add(0, scheduledThreadPoolExecutor.schedule(new Runnable() {
            public void run() {
                lockDown();
            }
        }, customDelay, TimeUnit.MILLISECONDS));
    }

    /** Overload of {@link setLockDelay(int)}, defaulting customDelay to 500 */
    protected void setLockDelay() {
        setLockDelay(500);
    }

    /** Lock the tetromino down, pushing it to the board */
    protected void lockDown() {
        if(position.y == dropCollision().y) { //Ensure the block isn't floating
            stopTetromino();
            gameBoardObj.pushToBoard(minoType, tetromino[spinPosition], position); 
        } 
    }
    
    /** Update various things and resets the lockTime future after every movement
     * @param mvmtType the type of movement that caused this refresh
     */
    protected void refresh(Character mvmtType) {
        if(!lockTimer.isEmpty()) { //Reset lockdown timer
            if(mvmtType == 'D') {
                if(position.y > lowestPositionReached) {
                    lowestPositionReached = position.y;
                    setLockDelay();
                }
            } else {
                if(lockMoves < 15) { //extended movement
                    lockMoves++;
                    setLockDelay();
                } else {
                    setLockDelay(1);
                }
            }
        } else if(position.y == dropPosition.y) {
            setLockDelay();
        }
            
        lastMoves.add(0, mvmtType);
        if(lastMoves.size() > 4) lastMoves.remove(4); //Arbitrary number of stored moves
        dropCollision();
    }

    /** Checks each column of the tetromino bounding box, calling {@link verticalDropCollision} to check for the lowest acceptable position.
     * @return The lowest position this tetromino can drop to.
     */
    public Position dropCollision() {
        dropPosition = new Position(position.x, gameBoard.length);
        for( int cols = 0; cols < tetromino[spinPosition][0].length; cols++ ) {
            for( int rows = tetromino[spinPosition].length - 1; rows >= 0 ; rows-- ) {
                if( tetromino[spinPosition][rows][cols] == minoType ) {
                    int testY = verticalDropCollision(cols, rows);
                    if(testY < dropPosition.y) dropPosition.y = testY;
                    continue;
                }
            }
        }
        return dropPosition;
    }

    /** Checks every square in a given row underneath the given mino 
     * @param minoCol Column of the given mino
     * @param minoRowOffset Row offset of the given mino relative to the tetramino position
     * @return the y position of the lowest position this mino can drop
     */
    protected int verticalDropCollision(int minoCol, int minoRowOffset) {
        for(int boardRow = position.y + minoRowOffset; boardRow < gameBoard.length; boardRow++) {
            if(gameBoard[boardRow][1 + position.x + minoCol] != 'n') { //If there's a block
                return boardRow - (1 + minoRowOffset);
            }
        }
        return gameBoard.length - (1 + minoRowOffset);
    }

    /** Finds the next spin position and calls {@link kickTest} for collision of wall/floor kicks */
    public boolean spinLeft() {
        return kickTest(Math.floorMod(spinPosition - 1, 4)); //Limits to 0, 1, 2, or 3
    }

    /** Rotates the 2d array CW and tests for collision + wall/floor kicks */
    public boolean spinRight() {
        return kickTest(Math.floorMod(spinPosition + 1, 4)); //Limits to 0, 1, 2, or 3
    }

    /** Tests the spun tetromino in a number of potential kick positions
     * @param testSpinPosition the new spin position
     */
    protected boolean kickTest(int testSpinPosition) {
        int testDir = (spinPosition == 3 || testSpinPosition == 1) ? -1 : 1;
        int leftOrRight = (spinPosition == 3 || testSpinPosition == 3) ? 1 : 0;
        for(int i = 0; i != 5 * testDir; i += testDir) { //Either counts up to +4 or down to -4 
            if(boardCollision(tetromino[testSpinPosition], kickData.get(leftOrRight).get(i))) {
                position.x += kickData.get(leftOrRight).get(i).x; //Kick position
                position.y += kickData.get(leftOrRight).get(i).y;
                spinPosition = testSpinPosition; //Change spin
                refresh('S');
                playSound("spinTrue");
                return true;
            }
        }
        playSound("spinFalse");
        return false;
    }

    /** Tests the collision at a certain position offset to see if it's valid.
     * @param testTetromino The tetromino data to test
     * @param testPos Position offset to Test
     * @return Whether or not the given position is valid
     */
    protected boolean boardCollision(Character[][] testTetromino, Position testPos) {
        for(int offSetY = 0; offSetY < testTetromino.length; offSetY++) {
            for(int offSetX = 0; offSetX < testTetromino[0].length; offSetX++) {
                if((position.y + testPos.y + offSetY >= gameBoard.length || position.y + testPos.y + offSetY < 0 || position.x + testPos.x + offSetX + 1 >= gameBoard[0].length || position.x + testPos.x + offSetX + 1 < 0) /*&& testTetromino[offSetY][offSetX] == minoType*/) return false;
                if(gameBoard[position.y + testPos.y + offSetY][position.x + testPos.x + offSetX + 1] != 'n' && testTetromino[offSetY][offSetX] == minoType) {
                    return false;
                }
            }
        }
        return true;
    }


}