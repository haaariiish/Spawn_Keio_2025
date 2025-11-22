package core;

import map.Map;

public class Game implements Runnable {
    // private variable

    private Thread gameThread;
    private boolean running = false;
    private Frame1 frame;
    private GameState game_state=GameState.HOME;
    private GameState previous_state = null; // to track previous state if needed
    private Map gameMap;


    public Game() {
        frame = new Frame1("Spawn Keio 2025", this);
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
        long in_game_time=0; // game be useful ? for animation ? 
        long lastTime = System.currentTimeMillis(); // current time
        while (running) {

            long startTime = System.currentTimeMillis(); // start time of the frame
            in_game_time = startTime - lastTime;

            if (game_state != previous_state) {
                handleStateChange();

                if (game_state == GameState.PLAYING) {
                    // Initialize or reset game elements here if needed
                    this.gameMap = new Map(50, 50, 16); // Example: create a new map
                    this.gameMap.createDefaultMap();
                }


                previous_state = game_state;
            }
            
            if (game_state == GameState.PLAYING) {
                frame.getGamePanel().repaint();
                update();
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
        }
    }
    private void handleStateChange() {
        switch (game_state) {
            case HOME:
                frame.showPanel("HomeMenu");
                break;
            case PLAYING:
                frame.showPanel("GamePanel");
                break;
            case PAUSE:
                //frame.showPanel("PauseMenu");
                break;
            case GAMEOVER:
                //frame.showPanel("GameOverMenu");
                break;
            case LOADING:
                //frame.showPanel("LoadingScreen");
                break;
        }
        frame.refresh(); //called one time when state changes because repaint is costly
    }

    private void update() {
        // Update in game objects here
    }

    public void changeGameState(GameState newState) {
        this.game_state = newState;

    }

    // Getters
    public GameState getGameState() {
        return this.game_state;
    }
    public Map getGameMap() {
        return this.gameMap;
    }
    public Frame1 getFrame() {
        return this.frame;
    }
    public GameState getPreviousGameState() {
        return this.previous_state;
    }
}
    