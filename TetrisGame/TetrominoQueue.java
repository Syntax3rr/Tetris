package TetrisGame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Tetromino.*;

/** TetrominoQueue is a class that manages the queue of Tetrominos, based on a 7-bag system. */
public class TetrominoQueue {
    /** The queue itself is an array of characters */
    protected ArrayList<Character> queue;
    // This is the arraylist of the next 7-bag.
    private ArrayList<Character> bag = new ArrayList<Character>();
    // This is an array of the 7 tetromino characters. This is used to refill the bag. 
    private Character[] tetrominoCharacters = {'I', 'O', 'T', 'S', 'Z', 'L', 'J'};

    /** Constructor */
    TetrominoQueue() {
        queue = new ArrayList<Character>();
        bag = new ArrayList<Character>();
        newBag();
        for(int i = 0; i < 7; i++) queue.add(bag.remove(0));
    }

    //Generates a new bag by making a duplicate of all tetromino characters, and then shuffling them
    private void newBag() {
        if(bag.isEmpty()) {
            Collections.addAll(bag, tetrominoCharacters);
            Collections.shuffle(bag);
        }
    }

    /** Gets the static tetromino data of each element in the queue
     * @return the tetromino visual data of every tetromino in the queue
     */
    public ArrayList<Character[][]> getQueue() { //This is really janky, but it seems to work fine
        ArrayList<Character[][]> output = new ArrayList<Character[][]>();
        ArrayList<Character> tempQueue = new ArrayList<Character>(List.copyOf(queue));
    
        for(Character i : tempQueue) {
            switch(i) {
                case 'I':
                    output.add(ITetromino.getTetStatic(0));
                    break;
                case 'O':
                    output.add(OTetromino.getTetStatic(0));
                    break;
                case 'T':
                    output.add(TTetromino.getTetStatic(0));
                    break;            
                case 'S':
                    output.add(STetromino.getTetStatic(0));
                    break;
                case 'Z':
                    output.add(ZTetromino.getTetStatic(0));
                    break;
                case 'L':
                    output.add(LTetromino.getTetStatic(0));
                    break;
                case 'J':
                    output.add(JTetromino.getTetStatic(0));
                    break;
            }
        }
        return output;
    }

    /** Gives the next tetromino, then moves one tetromino from the bag to the queue.
     * @return the minoType of the next tetromino
     */
    public Character getNext() {
        if(bag.isEmpty()) newBag();
        queue.add(bag.remove(0));
        return queue.remove(0);
    }
}
