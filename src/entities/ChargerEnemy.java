package entities;
import map.Map;
import java.awt.Graphics;
import java.awt.Color;

public class ChargerEnemy extends Enemy {

    public ChargerEnemy(double x, double y) {
        super(x, y, 15, 15, 25, 2, 1, 30,(int) Math.round(8+Math.random()));
        setStunCoolDown(20);
    }

    protected void updateMovement(Player player,Map map) {
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

    @Override
    public void render(Graphics g,int x, int y){
        // Calculate color without creating new Color every frame
        int knockBackFrame = getKnockBackFrame();
        int red = 255 - 5 * knockBackFrame;
        if (red < 0) red = 0;
        if (red > 255) red = 255;
        g.setColor(new Color(red, 100, 20));
        int screenX = (int)this.getX() - x;
        int screenY = (int)this.getY() - y;
        int width = this.getWidthInPixels();
        int height = this.getHeightInPixels();
        g.fillOval(screenX, screenY, width, height);
        g.drawRect(screenX, screenY, width, height);
    }
}

