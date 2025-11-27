package entities;

import java.awt.Graphics;
import java.awt.Color;


public class RangedEnemy extends Enemy {
    private final double preferredRange;
    private final double fireRange;
    private final double retreatPadding;
    private final double estimatedRange;

    public RangedEnemy(double x, double y) {
        super(x, y, 14, 14, 20, 2, 1, 1000,(int) Math.round(5+Math.random()));

        this.setBulletFast(12);
        this.setShootCooldownFrames(60);
        this.estimatedRange = estimated_distance_future(this.getBulletFast(), 60);
        this.fireRange = this.estimatedRange; // Different from range because of the friciton
        this.retreatPadding = this.estimatedRange*0.2;
        this.preferredRange = this.estimatedRange*0.8;
        
    }

    protected void updateMovement(Player player) {
        double vx = 0;
        double vy = 0;
        if(!this.getStun()){
            facePlayer(player);
            double angle = this.getFacingAngle();
            double speed = this.getSpeed();
            double dx = player.getX() - this.getX();
            double dy = player.getY() - this.getY();
            vx += (Math.cos(angle) * speed +Math.random());
            vy += (Math.sin(angle)* speed)+Math.random();
            double distance = Math.hypot(dx, dy);
            if (distance > preferredRange + retreatPadding) {
                vx += (Math.cos(angle) * speed);
                vy += (Math.sin(angle) * speed);
            } else if (distance < preferredRange - retreatPadding) {
                vx += (-Math.cos(angle) * speed);
                vy+=(-Math.sin(angle) * speed);
            } 
    }
        else if(this.getJustKnockBack()){
            double angle = this.getFacingAngle();
            vx += -(Math.cos(angle) ) * getKnockBackIntensity()/getWeight();
            vy += -(Math.sin(angle) ) * getKnockBackIntensity()/getWeight();
            setJustKnockBack(false);// Faut faire le calcul pour avoir une belle fonction avec poids etc
        }
        setVelocityX(vx);
        setVelocityY(vy);
    }

    @Override
    protected Projectiles attemptSpecialAction(Player player) {
        System.out.println(this.getStun());
        if (!this.getStun()){
        double distance = Math.hypot(player.getX() - this.getX(), player.getY() - this.getY());
        if (distance <= fireRange && isShootReady()) {
            resetShootCooldown();
            switch (this.getProjectilesTypes()) {
                case Simple_Projectiles:
                    return new Simple_Projectiles(this, 6, 6, 0.9 );
            
                default:
                    return new Simple_Projectiles(this, 6, 6, 0.9 );
            } 
        }
    }
        return null;
    }

    @Override
    public void render(Graphics g,int x, int y){
        g.setColor(Color.PINK);
        g.fillOval((int) this.getX() -x,(int) this.getY() -y ,this.getWidthInPixels() ,this.getHeightInPixels() );
        g.drawRect((int) this.getX() -x,(int) this.getY() -y ,this.getWidthInPixels() ,this.getHeightInPixels() );
    }

    public double estimated_distance_future(int speed, int frames){

        double distance = speed * (1-Math.pow(0.9,frames))/(1-0.9);
        return distance;
    }
}

