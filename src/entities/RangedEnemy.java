package entities;

import java.awt.Graphics;
import java.awt.Color;
import map.Map;
import core.GameWorld;

public class RangedEnemy extends Enemy {
    public double brighness = 0;
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

    protected void updateMovement(Player player, Map map, GameWorld gameWorld) {
        double vx = getVelocityX();
        double vy = getVelocityY();
        if (!getStun()) {
                double ex = getX() + getWidthInPixels() * 0.5;
                double ey = getY() + getHeightInPixels() * 0.5;
                double px = player.getX() + player.getWidthInPixels() * 0.5;
                double py = player.getY() + player.getHeightInPixels() * 0.5;

                boolean hasLOS = map.hasLineOfSight(ex, ey, px, py);
                //System.out.println(hasLOS);
                if (hasLOS) {
                    
                    // if in direct sight, go straight to the player
                    double angle = Math.atan2(py - ey, px - ex);
                    setFacingAngle(angle);
                    double speed = getSpeed();
                    
                    double dx = player.getX() - this.getX();
                    double dy = player.getY() - this.getY();
                    vx += Math.cos(angle) * speed;
                    vy += Math.sin(angle) * speed;

                    double distanceSq = dx * dx + dy * dy;
                    double distance = Math.sqrt(distanceSq);
                    if (distance > preferredRange + retreatPadding) {
                        vx += Math.cos(angle) * speed;
                        vy += Math.sin(angle) * speed;
                    } else if (distance < preferredRange - retreatPadding) {
                        vx += -Math.cos(angle) * speed;
                        vy += -Math.sin(angle) * speed;
                    }
                } else {
                    // if not seen-able, we use djikstra
                    int[] best = Djikstra(gameWorld, map, ex, ey);

                    int bestX = best[0];
                    int bestY = best[1];
                    if (bestX != -1 && bestY != -1) {
                        // calculate the angle to go to the center of the aimed tile
                        double targetCenterX = bestX * map.getTileSize() + map.getTileSize() * 0.5;
                        double targetCenterY = bestY * map.getTileSize() + map.getTileSize() * 0.5;
                        
                        double angle = Math.atan2(targetCenterY - ey, targetCenterX - ex);
                        setFacingAngle(angle);
                        
                        double speed = getSpeed();
                        vx += Math.cos(angle) * speed;
                        vy += Math.sin(angle) * speed;
                    }else {
                        // Fallback : naive behavior
                        facePlayer(player);
                        double angle = this.getFacingAngle();
                        double speed = this.getSpeed();
                        double dx = player.getX() - this.getX();
                        double dy = player.getY() - this.getY();
                        vx += Math.cos(angle) * speed;
                        vy += Math.sin(angle) * speed;

                        double distanceSq = dx * dx + dy * dy;
                        double distance = Math.sqrt(distanceSq);
                        if (distance > preferredRange + retreatPadding) {
                            vx += Math.cos(angle) * speed;
                            vy += Math.sin(angle) * speed;
                        } else if (distance < preferredRange - retreatPadding) {
                            vx += -Math.cos(angle) * speed;
                            vy += -Math.sin(angle) * speed;
                        }
                    }
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
    public void render(Graphics g,int x, int y,  int screenHeight, int screenWidth, int SHADOW_DISTANCE){
        // Calculate color components without creating new Color object
        int stunFrame = getStunFrame();
        int green = 210 - 3 * stunFrame;
        if (green < 0) green = 0;
        if (green > 255) green = 255;
   
        int screenX = (int)this.getX() - x;
        int screenY = (int)this.getY() - y;
        int width = this.getWidthInPixels();
        int height = this.getHeightInPixels();
        

        int centerX =screenX+width/2 ;
        int centerY = screenY+height/2;
        int[] xPoints = {centerX +(int) (width*Math.cos(getFacingAngle())), centerX+ +(int) (width*Math.cos(getFacingAngle()-Math.PI/2)/1.5) , centerX +(int) (width*Math.cos(getFacingAngle()+Math.PI/2)/1.5)};
        int[] yPoints = {centerY+(int) (height*Math.sin(getFacingAngle())), centerY+(int) (height*Math.sin(getFacingAngle()-Math.PI/2)/1.5)   ,centerY +(int) (height*Math.sin(getFacingAngle()+Math.PI/2)/1.5)}; 
        brighness = Math.max(1 - Math.sqrt((centerX-screenWidth/2) *(centerX-screenWidth/2)  + (centerY-screenHeight/2)*(centerY-screenHeight/2)) / SHADOW_DISTANCE,0);
        //System.out.println(brighness);
       
        g.setColor(Color.BLUE);
        
        g.setColor(new Color((int)(brighness*g.getColor().getRed()), (int) (g.getColor().getGreen()*brighness), (int) (brighness*g.getColor().getBlue())));
        g.fillPolygon(xPoints, yPoints, 3);

        g.setColor(new Color((int)(brighness*255), (int)(brighness*green), (int)(brighness*255)));
        
        g.fillOval(screenX, screenY, width, height);
       // g.drawRect(screenX, screenY, width, height);
    }

    public double estimated_distance_future(int speed, int frames){

        double distance = speed * (1-Math.pow(this.friction_used,frames))/(1-this.friction_used);
        return distance;
    }
}

