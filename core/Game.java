package core;



public class Game implements Runnable {
    // private variable

    private Thread gameThread;
    private boolean running = false;
    private Frame1 frame;
    private GameState game_state=GameState.HOME;
    private GameState previous_state = null; // to track previous state if needed


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
        long in_game_time=0;
        long lastTime = System.currentTimeMillis();
        while (running) {
            long startTime = System.currentTimeMillis();
            in_game_time = startTime - lastTime;
            
            // Testing code to cycle through game states every second

            /* System.err.println("Game State: " + this.game_state);
            System.out.println("in_game_time: " + in_game_time);
            System.err.println("in_game_time % 5: " + (in_game_time % 5));
            System.err.println((int) in_game_time%5);
            switch ((int) in_game_time%5 ){
                case 0 :
                     this.game_state=GameState.HOME;
                     break;
                case 1 :
                     this.game_state=GameState.PLAYING;
                     break;
                case 2 :
                     this.game_state=GameState.PAUSE;
                     break;
                case 3 :
                     this.game_state=GameState.GAMEOVER;
                     break;
                case 4 : 
                    this.game_state=GameState.LOADING;
                    break;
            } */

            if (game_state != previous_state) {
                handleStateChange();
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
    public GameState getGameState() {
        return this.game_state;
    }
}
    