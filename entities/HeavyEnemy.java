package entities;
import java.awt.Graphics;
import java.awt.Color;


public class HeavyEnemy extends Enemy {

    public HeavyEnemy(double x, double y) {
        super(x, y,19, 19, 80, 6, 4, 40,(int) Math.round(4+Math.random()));
        setStunCoolDown(10);
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

    public void render(Graphics g,int x, int y){
        g.setColor(new Color(100,255 - 5*getKnockBackFrame(),0));
        g.fillOval((int) this.getX() -x,(int) this.getY() -y ,this.getWidthInPixels() ,this.getHeightInPixels() );
        g.drawRect((int) this.getX() -x,(int) this.getY() -y ,this.getWidthInPixels() ,this.getHeightInPixels() );
    }
}

