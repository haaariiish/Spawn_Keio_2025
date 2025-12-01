package entities;
import map.Map;
import java.awt.Graphics;

public class Simple_Projectiles extends Projectiles{
    

    public Simple_Projectiles(Basic_Entity source_Entity,int h, int w, double friction){
        super(source_Entity);
        this.setWidth(w);
        this.setHeight(h);
        this.alignWithSource();
        this.setFriction(friction);
        this.setKnockBack(40);
        this.setRecoil(0);
    
    }
    @Override
    public void update(Map map){
        int rangeLeft = this.getRangeLeft();
        if (rangeLeft == 0) {
            return;
        }
        
        // Cache frequently accessed values
        double vx = this.getVelocityX();
        double vy = this.getVelocityY();
        double x = this.getX();
        double y = this.getY();
        int width = this.getWidth();
        int height = this.getHeight();
        double friction = this.getFriction();
        
        int steps = (int)Math.ceil(Math.max(Math.abs(vx), Math.abs(vy)));
        steps = Math.max(1, Math.min(steps, 30));
        double stepX = vx / steps;
        double stepY = vy / steps;

        // Horizontal movement
        for (int i = 0; i < steps; i++) {
            double testX = x + stepX;
            if (!map.collidesWithWall((int)testX, (int)y, width, height)) {
                x = testX;
            } else {
                vx = 0;
                setToDestroy();
                break;
            }
        }
        
        // Vertical movement
        for (int i = 0; i < steps; i++) {
            double testY = y + stepY;
            if (!map.collidesWithWall((int)x, (int)testY, width, height)) {
                y = testY;
            } else {
                vy = 0; 
                setToDestroy();
                break;
            }
        }
        
        // Apply friction
        vx *= friction;
        vy *= friction;
        this.setVelocityX(vx);
        this.setVelocityY(vy);
        
        // Update position
        this.setX(x);
        this.setY(y);
        
        // Decrement range
        if (rangeLeft > 1) {
            this.setRangeLeft(rangeLeft - 1);
        }
        this.setBounds();
    }

    @Override
    public void render(Graphics g, int x, int y){
        // Cache values to avoid multiple method calls
        int projX = (int)this.getX();
        int projY = (int)this.getY();
        int width = this.getWidth();
        int height = this.getHeight();
        int screenX = projX - x;
        int screenY = projY - y;
        
        g.fillOval(screenX, screenY, width, height);
        g.drawRect(screenX, screenY, width, height);
    }

    
    
}



