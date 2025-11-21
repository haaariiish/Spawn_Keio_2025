
public class Game implements Runnable {
    private Thread gameThread;
    private boolean running = false;
    private Frame1 frame;

    public Game() {
        frame = new Frame1("Spawn Keio 2025");
    }
    public static void main(String[] args) {
        Game game = new Game();
        
            game.frame.pack();
            game.frame.setVisible(true);
            
            game.start();
        };
    
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
        while (running) {
            update(); 
            frame.getHomePanel().repaint();
            // Logique principale du jeu
        }
    }
    private void update() {
        // Mettre à jour l'état du jeu
    }
}
    