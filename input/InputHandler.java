package input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Set;


public class InputHandler implements KeyListener{
    // Set of keys for key still used
    private Set<Integer> pressedKeys;

    // Set of key that is only use in a frame
    private Set<Integer> justPressedKeys;
    
    public InputHandler() {
        pressedKeys = new HashSet<>();
        justPressedKeys = new HashSet<>();
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

}
