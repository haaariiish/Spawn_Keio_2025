package entities;
import java.awt.Graphics;
import java.awt.Color;
import map.Map;


public class HeavyEnemy extends Enemy {

    public HeavyEnemy(double x, double y) {
        super(x, y,19, 19, 80, 6, 4, 40,(int) Math.round(4+Math.random()));
        setStunCoolDown(10);
    }

    protected void updateMovement(Player player, Map map) {
        double vx = getVelocityX();
        double vy = getVelocityY();
        
        if(!this.getStun()){
            facePlayer(player);
            double angle = this.getFacingAngle();
            double speed = this.getSpeed();
            // Remove Math.random() to reduce CodeCache usage
            vx += Math.cos(angle) * speed;
            vy += Math.sin(angle) * speed;   
    }
        setVelocityX(vx);
        setVelocityY(vy);
    }

    public void render(Graphics g,int x, int y){
        // Calculate color without creating new Color every frame
        int knockBackFrame = getKnockBackFrame();
        int green = 255 - 5 * knockBackFrame;
        if (green < 0) green = 0;
        if (green > 255) green = 255;
        g.setColor(new Color(100, green, 0));
        int screenX = (int)this.getX() - x;
        int screenY = (int)this.getY() - y;
        int width = this.getWidthInPixels();
        int height = this.getHeightInPixels();
        g.fillOval(screenX, screenY, width, height);
        g.drawRect(screenX, screenY, width, height);
    }
}

