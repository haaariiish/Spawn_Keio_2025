package entities;
import java.awt.Graphics;
import java.awt.Color;


public class HeavyEnemy extends Enemy {

    public HeavyEnemy(double x, double y) {
        super(x, y, 22, 22, 80, 6, 4, 40,(int) Math.round(4+Math.random()));
        
    }

    protected void updateMovement(Player player) {
        double vx = 0;
        double vy = 0;
        
        if(!this.getStun()){
            facePlayer(player);
            double angle = this.getFacingAngle();
            double speed = this.getSpeed();
            
            vx += (Math.cos(angle) * speed +Math.random());
            vy += (Math.sin(angle)* speed)+Math.random();
            
            
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

    public void render(Graphics g,int x, int y){
        g.setColor(Color.GREEN);
        g.fillOval((int) this.getX() -x,(int) this.getY() -y ,this.getWidthInPixels() ,this.getHeightInPixels() );
        g.drawRect((int) this.getX() -x,(int) this.getY() -y ,this.getWidthInPixels() ,this.getHeightInPixels() );
    }
}

