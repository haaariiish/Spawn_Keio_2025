package entities;

import java.awt.Graphics;
import java.awt.Color;

public class ChargerEnemy extends Enemy {

    public ChargerEnemy(double x, double y) {
        super(x, y, 15, 15, 25, 2, 1, 30,(int) Math.round(8+Math.random()));

    }

    
 
    protected void updateMovement(Player player) {
        double vx = getVelocityX();
        double vy = getVelocityY();
        
        if(!this.getStun()){
            facePlayer(player);
            double angle = this.getFacingAngle();
            double speed = this.getSpeed();
            
            vx += (Math.cos(angle) * speed +Math.random());
            vy += (Math.sin(angle)* speed)+Math.random();
            
            
    }
        
        setVelocityX(vx);
        setVelocityY(vy);
    }

    @Override
    public void render(Graphics g,int x, int y){
        g.setColor(new Color(255-10*getKnockBackFrame(),20,20));
        g.fillOval((int) this.getX() -x,(int) this.getY() -y ,this.getWidthInPixels() ,this.getHeightInPixels() );
        g.drawRect((int) this.getX() -x,(int) this.getY() -y ,this.getWidthInPixels() ,this.getHeightInPixels() );
    }
}

