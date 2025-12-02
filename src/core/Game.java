package core;

import map.Map;
import input.InputHandler;



import javax.swing.SwingUtilities;


import entities.*;



public class Game implements Runnable {
    // private variable for running the game
    private Thread gameThread;
    private boolean running = false;
    private Frame1 frame;
    private GameState game_state=GameState.HOME;
    private GameState previous_state = null; // to track previous state if needed
    private int open_time=0;
    private long game_opening=0;
    private int in_game_time=0;
    private GameWorld gameworld=null;


    //Input Handler
    private InputHandler inputHandler;

    


    public Game() {
        
        
        this.frame = new Frame1("Spawn Keio 2025", this);
        this.inputHandler = new InputHandler();
        attachInputHandlers();

        this.gameworld = new GameWorld(2000, 2000,40, this);

        
    }

    // reset in-game map and mob
    public void reset(){
        
        
        detachInputHandlers(this.inputHandler);
        this.inputHandler = new InputHandler();
        attachInputHandlers();

        SwingUtilities.invokeLater(() -> {
            this.frame.requestFocus();
            this.frame.getGamePanel().requestFocusInWindow();
        });

        if (this.game_state==GameState.HOME){
            this.gameworld.reset();
        }
        else if(this.game_state==GameState.PLAYING){
            // Same as when starting to play
            game_opening = System.currentTimeMillis();
            in_game_time = 0;
            this.gameworld.restart();
        }
        
        System.gc();
    }

    public static void main(String[] args) {
        Game game = new Game();
            game.frame.pack();
            game.frame.setVisible(true);
            game.start();
        }
    
    public synchronized void start() {
        running = true;
        gameThread = new Thread(this); 
        gameThread.start();
    }

    public synchronized void stop() {
        running = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

@Override
    public void run() {
        final int FPS = 60;
        final long frameTime = 1000 / FPS;
        this.open_time=0; // game be useful ? for animation ? 
        long lastTime = System.currentTimeMillis(); // current time
        while (running) {

            long startTime = System.currentTimeMillis(); // start time of the frame
            open_time =(int) (startTime - lastTime);

            if ((game_state == GameState.PLAYING || game_state == GameState.PAUSE) && inputHandler.isPausePressed()) {
                togglePause();
            }

            if (game_state != previous_state) {
                handleStateChange();

                if (game_state == GameState.PLAYING && previous_state != GameState.PAUSE) {
                    game_opening = System.currentTimeMillis();
                    // Initialize or reset game elements here if needed
                    this.gameworld.restart();
                }

                previous_state = game_state;
            }
            
            if (game_state == GameState.PLAYING) {
                in_game_time = (int) (startTime-game_opening);
                frame.getGamePanel().repaint();
                this.update();
            }

            if(game_state == GameState.HOME){
                frame.getHomeMenuPanel().repaint();
            } else if (game_state == GameState.PAUSE){
                frame.getPauseMenuPanel().repaint();
            } else if (game_state == GameState.GAMEOVER){
                frame.getGameOverPanel().repaint();
            }
           
            
            long elapsed = System.currentTimeMillis() - startTime;
            long sleepTime = frameTime - elapsed;
            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            //this.update(); depend on what I want to do with update ?
            // this.inputHandler.printPressedKeys(); // Print the pressed key
        }
    }
    private void handleStateChange() {
        switch (game_state) {
            case HOME:
                frame.showPanel("HomeMenu");
                break;
            case PLAYING:
                frame.showPanel("GamePanel");
                frame.getGamePanel().requestFocusInWindow();
                break;
            case PAUSE:
                frame.showPanel("PauseMenu");
                break;
            case GAMEOVER:
                frame.getGameOverPanel().refreshStats();
                frame.showPanel("GameOverMenu");
                break;
            case LOADING:
                //frame.showPanel("LoadingScreen");
                break;
        }
        frame.refresh(); //called one time when state changes because repaint is costly
    }
    private void update() {
        // Update in game objects here
        this.gameworld.update(inputHandler,this.getInGameTime());
    }

    public void changeGameState(GameState newState) {
        this.game_state = newState;

    }
    
    public void resumeGame() {
        if (this.game_state == GameState.PAUSE) {
            changeGameState(GameState.PLAYING);
        }
    }

    private void togglePause() {
        if (this.game_state == GameState.PLAYING) {
            changeGameState(GameState.PAUSE);
        } else if (this.game_state == GameState.PAUSE) {
            resumeGame();
        }
    }
    // Getters

    public GameWorld getGameWorld(){
        return this.gameworld;
    }
    public GameState getGameState() {
        return this.game_state;
    }
    public Map getGameMap() {
        return this.gameworld.getMap();
    }
    public Frame1 getFrame() {
        return this.frame;
    }
    public GameState getPreviousGameState() {
        return this.previous_state;
    }

    public int getOpenTime(){
        return this.open_time;
    }

    public int getInGameTime(){
        return this.in_game_time;
    }

    public Player getPlayer(){
        return this.gameworld.getPlayer();
    }

    public InputHandler getInputHandler() {
        return inputHandler;
    }

    private void attachInputHandlers() {
        if (this.frame == null || this.inputHandler == null) {
            return;
        }
        this.frame.addKeyListener(inputHandler);
        if (this.frame.getGamePanel() != null) {
            this.frame.getGamePanel().addKeyListener(inputHandler);
            this.frame.getGamePanel().addMouseListener(inputHandler);
            this.frame.getGamePanel().addMouseMotionListener(inputHandler);
            this.frame.getGamePanel().setFocusable(true);
        }
    }

    private void detachInputHandlers(InputHandler handler) {
        if (handler == null || this.frame == null) {
            return;
        }
        this.frame.removeKeyListener(handler);
        if (this.frame.getGamePanel() != null) {
            this.frame.getGamePanel().removeKeyListener(handler);
            this.frame.getGamePanel().removeMouseListener(handler);
            this.frame.getGamePanel().removeMouseMotionListener(handler);
        }
    }
    
}
    