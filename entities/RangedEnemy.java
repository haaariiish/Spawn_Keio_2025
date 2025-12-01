package entities;

import java.awt.Graphics;
import java.awt.Color;
import map.Map;


public class RangedEnemy extends Enemy {
    private final double preferredRange;
    private final double fireRange;
    private final double retreatPadding;
    private final double estimatedRange;
    private double friction_used=0.95;

    public RangedEnemy(double x, double y) {
        super(x, y, 14, 14, 20, 2, 1, 1000,(int) Math.round(5+Math.random()));

        this.setBulletFast(20);
        this.setShootCooldownFrames(60);
        this.estimatedRange = estimated_distance_future(this.getBulletFast(), 60);
        this.fireRange = this.estimatedRange; // Different from range because of the friciton
        this.retreatPadding = this.estimatedRange*0.2;
        this.preferredRange = this.estimatedRange*0.8;
        
    }

    protected void updateMovement(Player player, Map map) {
        double vx = getVelocityX();
        double vy = getVelocityY();
        if(!this.getStun()){
            facePlayer(player);
            double angle = this.getFacingAngle();
            double speed = this.getSpeed();
            double dx = player.getX() - this.getX();
            double dy = player.getY() - this.getY();
            // Remove Math.random() calls - they create too much code cache
            vx += Math.cos(angle) * speed;
            vy += Math.sin(angle) * speed;
            // Use distance squared comparison to avoid sqrt
            double distanceSq = dx * dx + dy * dy;
            double distance = Math.sqrt(distanceSq);
            if (distance > preferredRange + retreatPadding) {
                vx += (Math.cos(angle) * speed);
                vy += (Math.sin(angle) * speed);
            } else if (distance < preferredRange - retreatPadding) {
                vx += (-Math.cos(angle) * speed);
                vy+=(-Math.sin(angle) * speed);
            } 
    }
        
        setVelocityX(vx);
        setVelocityY(vy);
    }

    @Override
    protected Projectiles attemptSpecialAction(Player player) {
        if (!this.getStun()){ // If not stun can perform his special action
        double dx = player.getX() - this.getX();
        double dy = player.getY() - this.getY();
        double distanceSq = dx * dx + dy * dy;
        double fireRangeSq = fireRange * fireRange;
        // Compare squared distances to avoid sqrt
        if (distanceSq <= fireRangeSq && isShootReady()) {
            resetShootCooldown();
            switch (this.getProjectilesTypes()) {
                case Simple_Projectiles:
                    return new Simple_Projectiles(this, 10, 10, this.friction_used );
            
                default:
                    return new Simple_Projectiles(this, 6, 6, 0.8 );
            } 
        }
    }
        return null;
    }

    @Override
    public void render(Graphics g,int x, int y){
        // Calculate color components without creating new Color object
        int stunFrame = getStunFrame();
        int green = 210 - 3 * stunFrame;
        if (green < 0) green = 0;
        if (green > 255) green = 255;
        g.setColor(new Color(255, green, 255));
        int screenX = (int)this.getX() - x;
        int screenY = (int)this.getY() - y;
        int width = this.getWidthInPixels();
        int height = this.getHeightInPixels();
        g.fillOval(screenX, screenY, width, height);
        g.drawRect(screenX, screenY, width, height);
    }

    public double estimated_distance_future(int speed, int frames){

        double distance = speed * (1-Math.pow(this.friction_used,frames))/(1-this.friction_used);
        return distance;
    }
}

