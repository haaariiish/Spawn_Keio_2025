

public class Game implements Runnable {
    private Thread gameThread;
    private boolean running = false;
    public static void main(String[] args) {
        Frame1 frame = new Frame1("Spawn Keio 2025");
        
        frame.pack();
        frame.setVisible(true);
    }
    public synchronized void start() {
        running = true;
        gameThread = new Thread(this); // 'this' est l'objet Runnable
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
    
    public void run() {
        while (running) {
            // Logique principale du jeu
        }
    }
}