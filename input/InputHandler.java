package input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


public class InputHandler implements KeyListener{
    // Set of keys for key still used
    private Set<Integer> pressedKeys;

    // Set of key that is only use in a frame
    private Set<Integer> justPressedKeys;
    
    public InputHandler() {
        pressedKeys = Collections.synchronizedSet(new HashSet<>());
        justPressedKeys = Collections.synchronizedSet(new HashSet<>());
    }


// KEY LISTENERS ---------------------------------------------------------------
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        
        // if thhe key wasn't used
        if (!pressedKeys.contains(key)) {
            justPressedKeys.add(key);
        }
        
        pressedKeys.add(key);
        System.out.println("the following key has been pushed : "+  KeyEvent.getKeyText(e.getKeyCode()));
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        pressedKeys.remove(e.getKeyCode());
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
        // Not used yet
    }


     // Méthodes de vérification
    // ═══════════════════════════════════════════════════════════════
    
    /**
     * verify if a key is still pushed -> For movement for example
     */
    public boolean isKeyPressed(int keyCode) {
        return pressedKeys.contains(keyCode);
    }
    
    //for the shooting it's nice (for one instant input action)

    public boolean isKeyJustPressed(int keyCode) {
        return justPressedKeys.contains(keyCode);
    }
    
    /**
     * To call at the end of each frames
     * Clean the key just pushed
     */
    public void update() {
        justPressedKeys.clear();
    }


    // Basic Methods to get the input of moving
    public boolean isMovingLeft() {
        return isKeyPressed(KeyEvent.VK_LEFT) || isKeyPressed(KeyEvent.VK_Q) ;
    }
    
    public boolean isMovingRight() {
        return isKeyPressed(KeyEvent.VK_RIGHT) || isKeyPressed(KeyEvent.VK_D);
    }
    
    public boolean isMovingUp() {
        return isKeyPressed(KeyEvent.VK_UP) || isKeyPressed(KeyEvent.VK_Z) ;
    }
    
    public boolean isMovingDown() {
        return isKeyPressed(KeyEvent.VK_DOWN) || isKeyPressed(KeyEvent.VK_S);
    }

    public int getHorizontalDirection() {
        int direction = 0;
        if (isMovingLeft()) direction -= 1;
        if (isMovingRight()) direction += 1;
        return direction;
    }

    public int getVerticalDirection() {
        int direction = 0;
        if (isMovingUp()) direction -= 1;
        if (isMovingDown()) direction += 1;
        return direction;
    }

    public boolean isMoving() {
        return isMovingLeft() || isMovingRight() || isMovingUp() || isMovingDown();
    }

    // Action performed

    public boolean isShootPressed() {
        return isKeyPressed(KeyEvent.VK_SPACE) ;
    }
    
    public boolean isInteractPressed() {
        return isKeyJustPressed(KeyEvent.VK_E) ;
    }
    
    public boolean isPausePressed() {
        return isKeyJustPressed(KeyEvent.VK_ESCAPE);
    }
    
    // Debug

    public void printPressedKeys() {
        Set<Integer> snapshot;
        synchronized (pressedKeys) {
            if (pressedKeys.isEmpty()) {
                return;
            }
            snapshot = new HashSet<>(pressedKeys);
        }
        System.out.print("Touches pressées : ");
        for (int key : snapshot) {
            System.out.print(KeyEvent.getKeyText(key) + " ");
        }
        System.out.println();
    }
}
