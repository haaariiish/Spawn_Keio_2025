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
    private boolean first_launch=true;
    private int subDivisionRender = 8;
    private int totalSteps = 5;


    //Input Handler
    private InputHandler inputHandler;

    


    public Game() {
        
        
        this.frame = new Frame1("Spawn Keio 2025", this);
        this.inputHandler = new InputHandler();
        attachInputHandlers();

        this.gameworld = new GameWorld(4000, 4000,80, this);
        frame.getGamePanel().setSubTileSize(this.gameworld.getTileSize());
        
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
            in_game_time=0;
            this.gameworld.restart(totalSteps);
            
            
            
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

            if ((game_state == GameState.PLAYING || game_state == GameState.FREEZE) && inputHandler.isFreezePressed()) {
                toggleFreeze();
            }

            if ((game_state == GameState.PLAYING || game_state == GameState.PAUSE) && inputHandler.isPausePressed()) {
                togglePause();
            }
            if((game_state == GameState.PLAYING || game_state == GameState.LEVEL_UP_MENU) && inputHandler.isMenuPressed()) {
                toggleMenu();
            }

            if (game_state != previous_state) {
                handleStateChange();

                /*if (game_state == GameState.PLAYING && previous_state != GameState.PAUSE) {
                    game_opening = System.currentTimeMillis();
                    // Initialize or reset game elements here if needed
                    this.gameworld.restart();
                }*/

                previous_state = game_state;
            }
            
            if (game_state == GameState.PLAYING) {
                in_game_time = (int) (startTime-game_opening);
                SwingUtilities.invokeLater(() -> frame.getGamePanel().repaint());
                this.update();
            }

            if(game_state == GameState.HOME){
                frame.getHomeMenuPanel().repaint();
            } else if (game_state == GameState.PAUSE){
                frame.getPauseMenuPanel().repaint();
            } else if (game_state == GameState.GAMEOVER){
                frame.getGameOverPanel().repaint();
            }else if (game_state == GameState.LOADING){
                frame.getLoadingPanel().repaint();
            }else if (game_state == GameState.WAVE_LOADING){
                frame.getLoadingPanel().repaint();
            }else if (game_state == GameState.LEVEL_UP_MENU){
                ;
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

    private void startLoadingInBackground() {
        // Create a new thread
        Thread loadingThread = new Thread(() -> {
            try {
                //script for the loading of the resources
                loadResources();
                
                // When Ended change the Game State 
                SwingUtilities.invokeLater(() -> {
                    changeGameState(GameState.PLAYING);
                });
                
            } catch (Exception e) {
                e.printStackTrace();
                // If an error occurs, force home menu return
                SwingUtilities.invokeLater(() -> {
                    changeGameState(GameState.HOME);
                });
            }
        });
        
        loadingThread.start();
    }

    private void startWaveLoadingInBackground() {
        // Create a new thread
        Thread loadingThread = new Thread(() -> {
            try {
                //script for the loading of the resources
                loadNextWave();
                
                // When Ended change the Game State 
                SwingUtilities.invokeLater(() -> {
                    
                    changeGameState(GameState.PLAYING);
                    frame.getGamePanel().invalidateMinimapCache();
                });
                
            } catch (Exception e) {
                e.printStackTrace();
                // If an error occurs, force home menu return
                SwingUtilities.invokeLater(() -> {
                    changeGameState(GameState.HOME);
                });
            }
        });
        
        loadingThread.start();
    }

    private void loadResources() {
        // Exemple de chargement avec progression
        
        
        // load texture
        /*loadTextures();
        updateLoadingProgress(1, totalSteps);*/
        
        // load sound
        /* loadSounds();
        updateLoadingProgress(2, totalSteps);*/ 
        
        // Generate Map 
        game_opening = System.currentTimeMillis();
        this.gameworld.restart(totalSteps);

        updateLoadingProgress(totalSteps-1, totalSteps);
        
        
        //entities generation
        /*this.gameworld.initializeEntities();
        updateLoadingProgress(2, totalSteps); */
        
        // finalise - Show 100%
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        updateLoadingProgress(totalSteps, totalSteps);
    }

    private void loadNextWave(){
        this.gameworld.nextWave();
        for(int i = 0; i < 500; i++){
            updateWaveLoadingProgress(this.gameworld.getWave(),i, 500);
        }
    }
    
    public void updateLoadingProgress(int current, int total) {
        int percentage =  100 / total;
        
        for(int i=0;i<percentage;i++){
             // Met à jour l'écran de chargement (dans le thread Swing
            final int i_prime = i;
            SwingUtilities.invokeLater(() -> {
                frame.getLoadingPanel().setProgress((current-1)*percentage+i_prime +1);
                frame.getLoadingPanel().repaint();
            });
            
            // Petite pause pour voir la progression
            try {
                Thread.sleep(2*total);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
       
    }

    public void updateWaveLoadingProgress(int wave_number,int current, int total) {
        int percentage = (current * 100) / total;
        
        // Met à jour l'écran de chargement (dans le thread Swing)
        SwingUtilities.invokeLater(() -> {
            
            frame.getWaveLoadingPanel().setProgress(percentage);
            frame.getWaveLoadingPanel().setCurrentTask("WAVE "+(wave_number));
            frame.getWaveLoadingPanel().repaint();
        });
        
        // Petite pause pour voir la progression
        try {
            Thread.sleep(2);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
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
                frame.showPanel("LoadingScreen");
                startLoadingInBackground();      
                break;
            case WAVE_LOADING:
                frame.showPanel("WaveLoadingScreen");
                startWaveLoadingInBackground();      
                break;
            case LEVEL_UP_MENU:
                frame.showPanel("LevelUpMenu");
                frame.getLevelUpPanel().reset();
                frame.getLevelUpPanel().setPlayer(gameworld.getPlayer());
                break;
            case FREEZE:
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
        if (this.game_state == GameState.PAUSE || this.game_state == GameState.LEVEL_UP_MENU ||this.game_state==GameState.FREEZE) {
            changeGameState(GameState.PLAYING);
            //ocusable(true);
        }
    }

    private void togglePause() {
        if (this.game_state == GameState.PLAYING) {
            changeGameState(GameState.PAUSE);
        } else if (this.game_state == GameState.PAUSE) {
            resumeGame();
        }
    }
    private void toggleMenu() {
        if (this.game_state == GameState.PLAYING) {
            changeGameState(GameState.LEVEL_UP_MENU);
            //frame.getLevelUpPanel().setFocusable(true);
        } else if (this.game_state == GameState.LEVEL_UP_MENU) {
            resumeGame();
        }
    }

    private void toggleFreeze() {
        if (this.game_state == GameState.PLAYING) {
            changeGameState(GameState.FREEZE);
            //frame.getLevelUpPanel().setFocusable(true);
        } else if (this.game_state == GameState.FREEZE) {
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

    public int getSubDivision(){
        return this.subDivisionRender;
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
    