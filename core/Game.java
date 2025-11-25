package core;

import map.Map;
import input.InputHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.SwingUtilities;
import java.awt.event.*;


import org.w3c.dom.events.MouseEvent;

import java.awt.Point;


import entities.*;



public class Game implements Runnable {
    // private variable for running the game
    private Thread gameThread;
    private boolean running = false;
    private Frame1 frame;
    private GameState game_state=GameState.HOME;
    private GameState previous_state = null; // to track previous state if needed
    private Map gameMap;
    private int in_game_time=0;


    //Input Handler
    private InputHandler inputHandler;

    // In-game element
    private Player player;
    
    //private Boss currentBoss;

    private List<Enemy> enemies;

    //private List<Projectile> playerProjectiles;
    //private List<Projectile> enemyProjectiles;
    //private List<Item> items;
    //private List<Particle> particles;

    //private Map<String, Enemy> enemyMap;


    public Game() {
        
        
        this.frame = new Frame1("Spawn Keio 2025", this);
        this.inputHandler = new InputHandler();
        this.frame.addKeyListener(inputHandler);
        this.frame.getGamePanel().addKeyListener(inputHandler);
        this.frame.getGamePanel().setFocusable(true);
        

    


        this.enemies = new ArrayList<>();
        //playerProjectiles = new ArrayList<>();
        //enemyProjectiles = new ArrayList<>();
        //items = new ArrayList<>();
        //particles = new ArrayList<>();
        
        //enemyMap = new HashMap<>();
    }

    // reset in-game map and mob
    public void reset(){
        
        this.enemies = new ArrayList<>();
        this.inputHandler = new InputHandler();
        this.frame.addKeyListener(inputHandler);
        this.frame.getGamePanel().addKeyListener(inputHandler);
        this.frame.getGamePanel().setFocusable(true);

        SwingUtilities.invokeLater(() -> {
            this.frame.requestFocus();
            this.frame.getGamePanel().requestFocusInWindow();
        });

        if (this.game_state==GameState.HOME){
            this.gameMap = null;
            this.player = null;
        }
        else if(this.game_state==GameState.PLAYING){
            // Same as when starting to play
            this.gameMap = new Map(100, 100, 20); 
            this.gameMap.createDefaultMap();
                    
            Point spawnPoint = this.gameMap.getSpawnPoint();
            this.player = new Player(spawnPoint.getX(),spawnPoint.getY(),20,20,100,1,1,10);
            spawnPoint = null;
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
        this.in_game_time=0; // game be useful ? for animation ? 
        long lastTime = System.currentTimeMillis(); // current time
        while (running) {

            long startTime = System.currentTimeMillis(); // start time of the frame
            in_game_time =(int) (startTime - lastTime);

            if (game_state != previous_state) {
                handleStateChange();

                if (game_state == GameState.PLAYING) {
                    // Initialize or reset game elements here if needed
                    this.gameMap = new Map(100, 100, 40);  // Example: create a new map
                    this.gameMap.createDefaultMap();
                    // Player initialization

                    Point spawnPoint = this.gameMap.getSpawnPoint();
                    this.player = new Player(spawnPoint.getX(),spawnPoint.getY(),10,10,100,1,1,10);
                    spawnPoint = null;
                }


                previous_state = game_state;
            }
            
            if (game_state == GameState.PLAYING) {
                frame.getGamePanel().repaint();
                this.update();
            }

            if(game_state == GameState.HOME){
                frame.getHomeMenuPanel().repaint();
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
            this.inputHandler.printPressedKeys();
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
        this.player.update_input(gameMap, inputHandler);

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

    public int getInGameTime(){
        return this.in_game_time;
    }

    public Player getPlayer(){
        return this.player;
    }

    public InputHandler getInputHandler() {
        return inputHandler;
    }

    
}
    