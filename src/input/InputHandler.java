package input;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


public class InputHandler implements KeyListener, MouseListener, MouseMotionListener{
    // Set of keys for key still used
    private Set<Integer> pressedKeys;

    // Set of key that is only use in a frame
    private Set<Integer> justPressedKeys;
    private volatile boolean leftMousePressed = false;
    private volatile Point currentMousePosition = null;
    
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
        //System.out.println("the following key has been pushed : "+  KeyEvent.getKeyText(e.getKeyCode()));
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
        return justPressedKeys.remove(keyCode);
    }
    
    /**
     * To call at the end of each frames
     * Clean the key just pushed
     */
    public void update() {
        // no-op: justPressed keys are consumed when queried
    }


    // Basic Methods to get the input of moving
    public boolean isMovingLeft() {
        return isKeyPressed(KeyEvent.VK_Q) || isKeyPressed(KeyEvent.VK_A);
    }
    
    public boolean isMovingRight() {
        return isKeyPressed(KeyEvent.VK_D);
    }
    
    public boolean isMovingUp() {
        return isKeyPressed(KeyEvent.VK_Z) || isKeyPressed(KeyEvent.VK_W) ;
    }
    
    public boolean isMovingDown() {
        return isKeyPressed(KeyEvent.VK_S) || isKeyPressed(KeyEvent.VK_X);
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

    public boolean isMenuPressed() {
        return isKeyJustPressed(KeyEvent.VK_M) ;
    }
    
    public boolean isPausePressed() {
        return isKeyJustPressed(KeyEvent.VK_ESCAPE);
    }

    public boolean isFreezePressed() {
        return isKeyJustPressed(KeyEvent.VK_F);
    }

    public boolean isMouseShootPressed() {
        return leftMousePressed;
    }

    public Point getMousePosition() {
        return currentMousePosition;
    }

    public Double getShootAngleFromArrows() {
        int dx = 0;
        int dy = 0;
        if (isKeyPressed(KeyEvent.VK_RIGHT)) {
            dx += 1;
        }
        if (isKeyPressed(KeyEvent.VK_LEFT)) {
            dx -= 1;
        }
        if (isKeyPressed(KeyEvent.VK_DOWN)) {
            dy += 1;
        }
        if (isKeyPressed(KeyEvent.VK_UP)) {
            dy -= 1;
        }
        if (dx == 0 && dy == 0) {
            return null;
        }
        return Math.atan2(dy, dx);
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

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            leftMousePressed = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            leftMousePressed = false;
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        currentMousePosition = e.getPoint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        currentMousePosition = e.getPoint();
    }
}
