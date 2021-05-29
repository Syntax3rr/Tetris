package TetrisUI;

import java.util.ArrayList;
import java.util.HashMap;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.image.Kernel;
import java.awt.image.ConvolveOp;
import javax.swing.JFrame;
import java.awt.Image;
import javax.swing.ImageIcon;
import java.awt.RenderingHints;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import TetrisGame.GameBoard;
import Tetromino.Tetromino;
import MiscClasses.Button;

/** Central Class, handling game state, image rendering, and object initialization. */
public class GameRenderer extends JFrame {
    
    private BufferedImage dbImage; // Secondary Buffered Image for Double Buffering
    private Graphics2D dbg; // Graphics2D of dbImage
    private BufferedImage bgImg; // Background Image
    private int renderWidth, renderHeight; // width and height of the rendering frame
    private int rCenterX, rCenterY; // Center of the rendering frame
    
    private Image imgLogo, imgPlay, imgOptions; // Pre-Loaded Images
    public HashMap<String, Button> buttonList; //Screw JButtons, I don't feel like making a new class for interface stuff... I'm going to regret this later...
    
    private Options options; //Control + Misc Options
    private MouseHandler mouse; // Mouse EventHandlers
    private KeyboardHandler keyboard; // Keyboard EventHandlers
    private GameBoard game; // GameBoard
    
    private AppState lastState; //Previous State of the application
    private AppState currentState; //State of the application

    /** ScheduledThreadPoolExecutor object */
    public ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;

    /** Constructor */
    public GameRenderer() {
        super("tetris");
        scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
        buttonList = new HashMap<String, Button>();

        options = new Options();
        mouse = new MouseHandler(this); addMouseListener(mouse); addMouseMotionListener(mouse); //There's probably a better way to give the mouse access to this obj's methods & stuff, but idk them.
        keyboard = new KeyboardHandler(this); addKeyListener(keyboard);

        currentState = AppState.SPLASH;

        game = null;

        windowManager();
    }

    /** Manages the window, I think. */
    public void windowManager() {
        //JFrame f = new JFrame();
        setTitle("Tetris");
        setVisible(true);
        setResizable(false);
        setSize(1280, 720);
        setBackground(Color.white);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    /** Gets the State of the App
     * @return The current state
     */
    public AppState getAppState() {
        return currentState;
    }

    /** Change the State of the App.
     * @param newState
     */
    public void setAppState(AppState newState) {
        lastState = currentState;
        currentState = newState;
    }

    /** Returns the Options object */
    public Options getOptions() {
        return options;
    }

    /** Stops and resets the GameBoard */
    public void resetGame() {
        game.getActiveTet().stopTetromino(); 
        game = null; 
        setAppState(AppState.MAIN_MENU);
    }

    /**Loads a .png file from PNG/ folder and resizes an {@link Image}
     * @param filename Name of .png File
     * @param imgWidth Desired image width
     * @param imgHeight Desired image height
     * @return Resized image
     */
    public Image loadImg(String filename, int imgWidth, int imgHeight) {
        BufferedImage resizedImg = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g2 = resizedImg.createGraphics();
	    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    g2.drawImage(new ImageIcon("./TetrisUI/Images/" + filename + ".png").getImage(), 0, 0, imgWidth, imgHeight, null);
	    g2.dispose();
	    return resizedImg;
    }

    /** Draw on the {@link dbImage}
     *  @param g object to be drawn on
     */
    public void paintComponent(Graphics2D g) { //Draw
        g.setFont(new Font("TimesRoman", Font.PLAIN, 14));
        switch(currentState) {
            case SPLASH: //Spashscreen + Make the background
                renderHeight = 720; //Idk why, but getHeight() and getWidth() don't always work?
                renderWidth = 1280;
                rCenterX = renderWidth / 2;
                rCenterY = renderHeight / 2;

                imgLogo = loadImg("Logo", (int) (3 * (renderWidth / 8)),(int) (2 * (renderWidth / 8))); //Load Logo image
                //Legal Statement
                g.setColor(Color.BLACK);
                g.fillRect(0 ,0, renderWidth, renderHeight);
                g.drawImage(imgLogo, rCenterX - (imgLogo.getWidth(null) / 2), rCenterY - (int) (imgLogo.getHeight(null) * 0.75), this);
                g.setColor(Color.WHITE);
                g.drawString("Tetris ® & © 1985~2021 Tetris Holding, LLC.", 15,  renderHeight - 120);
                g.drawString("Tetris logo, Tetris theme song and Tetriminos are trademarks of Tetris Holding, LLC.", 15, renderHeight - 100);
                g.drawString("Licensed to The Tetris Company.", 15, renderHeight - 80);
                g.drawString("Game Design by Alexey Pajitnov.", 15, renderHeight - 60);
                g.drawString("(Original) Logo Design by Roger Dean.", 15, renderHeight - 40);
                g.drawString("All Rights Reserved.", 15, renderHeight - 20);
                
                scheduledThreadPoolExecutor.schedule(() -> {currentState = AppState.CREATE_BACKGROUND;}, 100, TimeUnit.MILLISECONDS);
                break;
            case CREATE_BACKGROUND: //Initially Create Background Image & Do some basic initialization for images & buttons etc.
                final int blockSize = 30;
                bgImg = new BufferedImage(renderWidth + 200, renderHeight + 200, BufferedImage.TYPE_INT_RGB);
                Graphics2D bgG = bgImg.createGraphics();
                bgG.setPaint(new GradientPaint(0f, 0f, new Color(60, 60, 155), 0f, 300f, new Color(100, 155, 255), true));
                bgG.fillRect(0, 0, bgImg.getWidth(), bgImg.getHeight());
                //Fill Initial Color
                
                bgG.setStroke(new BasicStroke(1));
                for(int i = 0; i < bgImg.getWidth(); i += blockSize) { //Draw Lines
                    bgG.setColor(Color.decode("#5599FF"));
                    bgG.drawLine(i, 0, i , bgImg.getHeight());
                    bgG.setColor(Color.decode("#000033"));
                    bgG.setStroke(new BasicStroke(3));
                    bgG.drawLine(i + 5, 0, i + 5, bgImg.getHeight());
                }
                for(int i = 0; i < bgImg.getHeight(); i += blockSize) {
                    bgG.setColor(Color.decode("#5599FF"));
                    bgG.drawLine(0, i, bgImg.getWidth() , i);
                    bgG.setColor(Color.decode("#000033"));
                    bgG.drawLine(0, i + 5, bgImg.getWidth() , i + 5);
                }
                bgG.dispose();
                int blurSize = 32;
                float[] data = new float[blurSize * blurSize]; //Setup kernel data for a very simple blur (Average all values within a 6x6 centered on the specific value)
                for(int i = 0; i < data.length; i++) data[i] = 1f / (float) (blurSize * blurSize);

                bgImg = new ConvolveOp(new Kernel(blurSize, blurSize, data)).filter(bgImg, null).getSubimage(100, 100, renderWidth, renderHeight); //Apply Filter
                g.drawImage(bgImg, 0, 0, this);

                //Load Images
                imgPlay = loadImg("Play", (int) (3 * (renderWidth / 12)), (int) (renderWidth / 12));
                imgOptions = loadImg("Options", (int) (renderWidth / 16),(int) (renderWidth / 16));

                //Create Buttons
                buttonList.put("Play", new Button("Play", imgPlay, rCenterX - (int) (imgPlay.getWidth(null)/ 2), rCenterY + (int) (imgPlay.getHeight(null) * 1.35), AppState.MAIN_MENU, () -> {setAppState(AppState.PLAY);}));
                buttonList.put("OptionsMain", new Button("OptionsMain", imgOptions, renderWidth - (int) (imgOptions.getWidth(null) * 1.45), (int) (imgOptions.getHeight(null) * 0.75), AppState.MAIN_MENU, () -> {setAppState(AppState.OPTIONS);}));
                buttonList.put("OptionsPlay", new Button("OptionsPlay", imgOptions, renderWidth - (int) (imgOptions.getWidth(null) * 1.45), (int) (imgOptions.getHeight(null) * 0.75), AppState.PLAY, () -> {setAppState(AppState.OPTIONS);}));
                buttonList.put("OptionsBack", new Button("OptionsBack", imgOptions, renderWidth - (int) (imgOptions.getWidth(null) * 1.45), (int) (imgOptions.getHeight(null) * 0.75), AppState.OPTIONS, () -> {setAppState(lastState);}));
                
                buttonList.put("Restart", new Button("Restart", "Reset Game", (int) (renderWidth * 0.725), (int) (renderHeight * 0.8), AppState.OPTIONS, () -> {resetGame();}));
                buttonList.put("NewGame", new Button("NewGame", "Retry?", (int) (renderWidth * 0.47), (int) (renderHeight * 0.6), AppState.GAME_OVER, () -> {resetGame();}));

                buttonList.put("ControlML", new Button("ControlML", options.getControl("MoveLeft"), (int) (renderWidth * 0.4), (int) (renderHeight * 0.115), AppState.OPTIONS, () -> {keyboard.addListener(() -> {options.setControl("MoveLeft", keyboard.getNewKey()); buttonList.get("ControlML").setText(options.getControl("MoveLeft"));});})); // I should simplify this, but I don't feel like it....
                buttonList.put("ControlMR", new Button("ControlMR", options.getControl("MoveRight"), (int) (renderWidth * 0.4), (int) (renderHeight * 0.195), AppState.OPTIONS, () -> {keyboard.addListener(() -> {options.setControl("MoveRight", keyboard.getNewKey()); buttonList.get("ControlMR").setText(options.getControl("MoveRight"));});}));
                buttonList.put("ControlSL", new Button("ControlSL", options.getControl("SpinLeft"), (int) (renderWidth * 0.4), (int) (renderHeight * 0.275), AppState.OPTIONS, () -> {keyboard.addListener(() -> {options.setControl("SpinLeft", keyboard.getNewKey()); buttonList.get("ControlSL").setText(options.getControl("SpinLeft"));});})); 
                buttonList.put("ControlSR", new Button("ControlSR", options.getControl("SpinRight"), (int) (renderWidth * 0.4), (int) (renderHeight * 0.355), AppState.OPTIONS, () -> {keyboard.addListener(() -> {options.setControl("SpinRight", keyboard.getNewKey()); buttonList.get("ControlSR").setText(options.getControl("SpinRight"));});})); 
                buttonList.put("ControlSD", new Button("ControlSD", options.getControl("SoftDrop"), (int) (renderWidth * 0.4), (int) (renderHeight * 0.435), AppState.OPTIONS, () -> {keyboard.addListener(() -> {options.setControl("SoftDrop", keyboard.getNewKey()); buttonList.get("ControlSD").setText(options.getControl("SoftDrop"));});})); 
                buttonList.put("ControlHD", new Button("ControlHD", options.getControl("HardDrop"), (int) (renderWidth * 0.4), (int) (renderHeight * 0.515), AppState.OPTIONS, () -> {keyboard.addListener(() -> {options.setControl("HardDrop", keyboard.getNewKey()); buttonList.get("ControlHD").setText(options.getControl("HardDrop"));});})); 
                buttonList.put("ControlH", new Button("ControlH", options.getControl("Hold"), (int) (renderWidth * 0.4), (int) (renderHeight * 0.595), AppState.OPTIONS, () -> {keyboard.addListener(() -> {options.setControl("Hold", keyboard.getNewKey()); buttonList.get("ControlH").setText(options.getControl("Hold"));});})); 

                buttonList.put("ControlDefault", new Button("ControlDefault", "Reset to Defaults", (int) (renderWidth * 0.125), (int) (renderHeight * 0.8), AppState.OPTIONS, () -> {options.resetOptions(); 
                //This is probably condensable, but it's fine for now.
                buttonList.get("ControlML").setText(options.getControl("MoveLeft"));
                buttonList.get("ControlMR").setText(options.getControl("MoveRight"));
                buttonList.get("ControlSL").setText(options.getControl("SpinLeft"));
                buttonList.get("ControlSR").setText(options.getControl("SpinRight"));
                buttonList.get("ControlSD").setText(options.getControl("SoftDrop"));
                buttonList.get("ControlHD").setText(options.getControl("HardDrop"));
                buttonList.get("ControlH").setText(options.getControl("Hold"));
                }));

                currentState = AppState.MAIN_MENU; //Set gamestate to "Main menu"
                break;
            case MAIN_MENU:
                g.drawImage(bgImg, 0, 0, this); //Draw Background
                g.setColor(new Color(0f, 0f, 0f, 0.5f)); //Backdrop
                //g.fillRoundRect(rCenterX - (int) (renderWidth * 0.25), rCenterY - (int) (renderHeight * 0.375), (int) (renderWidth * 0.5), (int) (renderHeight * 0.75), 40, 40);
                g.drawImage(imgLogo, rCenterX - (imgLogo.getWidth(null) / 2), rCenterY - (int) (imgLogo.getHeight(null) * 0.75), null); //Title
                
                //Buttons
                g.drawImage(buttonList.get("Play").getBtnImg(), buttonList.get("Play").getX(), buttonList.get("Play").getY(), this); //Play Button
                g.drawImage(buttonList.get("OptionsMain").getBtnImg(), buttonList.get("OptionsMain").getX(), buttonList.get("OptionsMain").getY(), this); //Options Button
                break;
            case OPTIONS:
                g.drawImage(bgImg, 0, 0, this); //Draw Background
                if(buttonList.get("OptionsBack").getHoverState() == 0) buttonList.get("OptionsBack").setHoverState(1);
                g.drawImage(buttonList.get("OptionsBack").getBtnImg(), buttonList.get("OptionsBack").getX(), buttonList.get("OptionsBack").getY(), this);
                
                g.setColor(new Color(0f, 0f, 0f, 0.4f)); //Backdrop
                g.fillRect((int) (renderWidth * 0.1), (int) (renderHeight * 0.1), (int) (renderWidth * 0.8), (int) (renderHeight * 0.8));
                g.setFont(new Font("Courier New", Font.PLAIN, 32));
                g.setColor(Color.WHITE);
                //Controls
                g.drawString("Move Left", (int) (renderWidth * 0.115), (int) (renderHeight * 0.15));
                g.drawImage(buttonList.get("ControlML").getBtnImg(), buttonList.get("ControlML").getX(), buttonList.get("ControlML").getY(), this);
                g.drawString("Move Right", (int) (renderWidth * 0.115), (int) (renderHeight * 0.15) + 60);
                g.drawImage(buttonList.get("ControlMR").getBtnImg(), buttonList.get("ControlMR").getX(), buttonList.get("ControlMR").getY(), this);
                g.drawString("Spin Left", (int) (renderWidth * 0.115), (int) (renderHeight * 0.15) + 120);
                g.drawImage(buttonList.get("ControlSL").getBtnImg(), buttonList.get("ControlSL").getX(), buttonList.get("ControlSL").getY(), this);
                g.drawString("Spin Right", (int) (renderWidth * 0.115), (int) (renderHeight * 0.15) + 180);
                g.drawImage(buttonList.get("ControlSR").getBtnImg(), buttonList.get("ControlSR").getX(), buttonList.get("ControlSR").getY(), this);
                g.drawString("Soft Drop", (int) (renderWidth * 0.115), (int) (renderHeight * 0.15) + 240);
                g.drawImage(buttonList.get("ControlSD").getBtnImg(), buttonList.get("ControlSD").getX(), buttonList.get("ControlSD").getY(), this);
                g.drawString("Hard Drop", (int) (renderWidth * 0.115), (int) (renderHeight * 0.15) + 300);
                g.drawImage(buttonList.get("ControlHD").getBtnImg(), buttonList.get("ControlHD").getX(), buttonList.get("ControlHD").getY(), this);
                g.drawString("Hold", (int) (renderWidth * 0.115), (int) (renderHeight * 0.15) + 360);
                g.drawImage(buttonList.get("ControlH").getBtnImg(), buttonList.get("ControlH").getX(), buttonList.get("ControlH").getY(), this);

                g.drawImage(buttonList.get("ControlDefault").getBtnImg(), buttonList.get("ControlDefault").getX(), buttonList.get("ControlDefault").getY(), this);
                if(game != null) g.drawImage(buttonList.get("Restart").getBtnImg(), buttonList.get("Restart").getX(), buttonList.get("Restart").getY(), this);
                break;
            case GAME_OVER: //Drop through to play
            case PLAY:
                g.drawImage(bgImg, 0, 0, this); //Draw Background
                if(game == null) game = new GameBoard(1, this); //Create the Game if not Initialized
                //Super inefficient to do this every frame, but it'll do for now.
                keyboard.addListener(options.getControl("MoveLeft"), () -> {game.getActiveTet().moveLeft();});
                keyboard.addListener(options.getControl("MoveRight"), () -> {game.getActiveTet().moveRight();});
                keyboard.addListener(options.getControl("SpinLeft"), () -> {game.getActiveTet().spinLeft();});
                keyboard.addListener(options.getControl("SpinRight"), () -> {game.getActiveTet().spinRight();});
                keyboard.addListener(options.getControl("Hold"), () -> {game.swapHeld();});
                keyboard.addListener(options.getControl("SoftDrop"), () -> {game.getActiveTet().setSoftDrop(keyboard.isSoftDropping);});
                keyboard.addListener(options.getControl("HardDrop"), () -> {game.getActiveTet().hardDrop();});

                //Get game objects
                Character[][] board = game.getBoard();
                Tetromino activeTet = game.getActiveTet();
                Tetromino heldTet = game.getHeldTet();
                ArrayList<Character[][]> queue = game.getQueue();

                //Draw Settings Button
                g.drawImage(buttonList.get("OptionsPlay").getBtnImg(), buttonList.get("OptionsPlay").getX(), buttonList.get("OptionsPlay").getY(), this);
                
                //Draw Backdrop

                //Draw Stats & Labels
                g.setFont(new Font("Courier New", Font.PLAIN, 32));
                g.setColor(Color.WHITE);
                g.drawString("Score", (int) (renderWidth * 0.3), (int) (renderHeight * 0.4));
                g.drawString(game.getScore().toString(), (int) (renderWidth * 0.3), (int) (renderHeight * 0.45));
                g.drawString("Level", (int) (renderWidth * 0.3), (int) (renderHeight * 0.5));
                g.drawString(game.getLevel().toString(), (int) (renderWidth * 0.3), (int) (renderHeight * 0.55));
                g.drawString("Goal", (int) (renderWidth * 0.3), (int) (renderHeight * 0.6));
                g.drawString(((Integer) (10 - (game.getLinesCleared() % 10))).toString(), (int) (renderWidth * 0.3), (int) (renderHeight * 0.65));
                g.drawString("Lines", (int) (renderWidth * 0.3), (int) (renderHeight * 0.7));
                g.drawString(game.getLinesCleared().toString(), (int) (renderWidth * 0.3), (int) (renderHeight * 0.75));

                //Draw GameBoard
                for(int i = 4; i < board.length - 1; i++) {
                    for(int j = 1; j < board[i].length - 1; j++) {
                        if(board[i][j] == 'n') {
                            g.setColor(new Color(0f, 0f, 0f, 0.8f));
                            g.fillRect((int) (renderWidth * 0.4) + j * 20, (int) (renderHeight * 0.1) + i * 20, 20, 20);
                            g.setColor(new Color(1f, 1f, 1f, 0.2f));
                            g.drawRect((int) (renderWidth * 0.4) + j * 20, (int) (renderHeight * 0.1) + i * 20, 20, 20);
                            continue;
                        }
                        g.drawImage(loadImg("mino" + board[i][j], 20, 20), (int) (renderWidth * 0.4) + j * 20, (int) (renderHeight * 0.1) + i * 20, this);
                    }
                }

                //Draw Ghost Tetromino
                for(int i = 0; i < activeTet.getTetromino().length; i++) { 
                    for(int j = 0; j < activeTet.getTetromino()[i].length; j++) {
                        if(activeTet.getTetromino()[i][j] == 'n') continue;
                        g.drawImage(loadImg("mino" + activeTet.getMinoType(), 20, 20), (int) (renderWidth * 0.4) + (activeTet.getDropPosition().x + 1) * 20 + j * 20, (int) (renderHeight * 0.1) + activeTet.getDropPosition().y * 20 + i * 20, this);
                        g.setColor(new Color(0f, 0f, 0f, 0.75f));
                        g.fillRect((int) (renderWidth * 0.4) + (activeTet.getDropPosition().x + 1) * 20 + j * 20, (int) (renderHeight * 0.1) + activeTet.getDropPosition().y * 20 + i * 20, 20, 20);
                    }
                }

                //Draw Active Tetromino
                for(int i = 0; i < activeTet.getTetromino().length; i++) { 
                    for(int j = 0; j < activeTet.getTetromino()[i].length; j++) {
                        if(activeTet.getTetromino()[i][j] == 'n') continue;
                        g.drawImage(loadImg("mino" + activeTet.getMinoType(), 20, 20), (int) (renderWidth * 0.4) + (activeTet.getPosition().x + 1) * 20 + j * 20, (int) (renderHeight * 0.1) + activeTet.getPosition().y * 20 + i * 20, this);
                    }
                }
                
                //Draw Held Tetromino
                if(heldTet != null) {
                    for(int i = 0; i < heldTet.getTetromino().length; i++) { 
                        for(int j = 0; j < heldTet.getTetromino()[i].length; j++) {
                            if(heldTet.getTetromino()[i][j] == 'n') continue;
                            g.drawImage(loadImg("mino" + heldTet.getMinoType(), 20, 20), (int) (renderWidth * 0.35) + j * 20, (int) (renderHeight * 0.18) + i * 20, this);
                        }
                    }
                }
                
                //Draw First of Queue
                for(int i = 0; i < queue.get(0).length; i++) { 
                    for(int j= 0; j< queue.get(0)[i].length; j++) {
                        if(queue.get(0)[i][j] == 'n') continue;
                        g.drawImage(loadImg("mino" + queue.get(0)[i][j], 20, 20), (int) (renderWidth * 0.64) + j * 20, (int) (renderHeight * 0.18) + i * 20, this);
                    }
                }

                //Draw Rest of Queue
                for(int i = 1; i < queue.size(); i++) { 
                    for(int j = 0; j < queue.get(i).length; j++) {
                        for(int k = 0; k < queue.get(i)[j].length; k++) {
                            if(queue.get(i)[j][k] == 'n') continue;
                            g.drawImage(loadImg("mino" + queue.get(i)[j][k], 15, 15),  (int) (renderWidth * 0.64) + k * 15, (int) (renderHeight * 0.25) + j * 15 + i * 50, this);
                        }
                    }
                }

                if(currentState == AppState.GAME_OVER) { //Game Over Overlay
                    g.setColor(new Color(0f, 0f, 0f, 0.8f));
                    g.fillRect(0, 0, renderWidth, renderHeight);
                    g.setColor(Color.white);
                    g.drawString("Game Over", (int) (renderWidth * 0.47), rCenterY - 50);
                    g.drawString("Score", (int) (renderWidth * 0.47), rCenterY);
                    g.drawString(game.getScore().toString(), (int) (renderWidth * 0.47), rCenterY + 35);
                    
                    keyboard.clearListeners();
                    g.drawImage(buttonList.get("NewGame").getBtnImg(), buttonList.get("NewGame").getX(), buttonList.get("NewGame").getY(), null);
                }
                break;
            default:
                g.setColor(Color.GREEN);
                g.fillRect(0, 0, renderWidth, renderHeight);
                break;
        }
       repaint();
    }

    /** Creates a second buffered image, for double buffering, and draws it to the provided graphics object
     * @param g graphics object to draw to
     */
    @Override
    public void paint(Graphics g) {
        dbImage = new BufferedImage(1280, 720, BufferedImage.TYPE_INT_RGB);
        dbg = dbImage.createGraphics();
        paintComponent(dbg);
        g.drawImage(dbImage, 0, 0, getWidth(), getHeight(), this);
    }
}