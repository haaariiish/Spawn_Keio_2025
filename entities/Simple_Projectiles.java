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
    }
    @Override
    public void update(Map map){
        if (this.getRangeLeft()!=0){
        int steps = (int)Math.ceil(Math.max(Math.abs(this.getVelocityX()), Math.abs(this.getVelocityY())));
        steps = Math.max(1, Math.min(steps, 30));
        double stepX = this.getVelocityX() / steps;
        double stepY = this.getVelocityY() / steps;

        for (int i = 0; i < steps; i++) {
            // Mouvement horizontal
            double testX = this.getX() + stepX;
            if (!map.collidesWithWall((int)testX, (int)this.getY(), this.getWidth(), this.getHeight())) {
                this.setX(testX);
            } else {
                this.setVelocityX(0);
                break; // Arrêter si collision
            }
        }
        
        for (int i = 0; i < steps; i++) {
            // Mouvement vertical
            double testY = this.getY() + stepY;
            if (!map.collidesWithWall((int)this.getX(), (int)testY, this.getWidth(), this.getHeight())) {
                this.setY(testY);
            } else {
                this.setVelocityY(0); 
                break;
            }

        }
        // Slow down in any case to have a cap for our speed
        this.setVelocityX(this.getFriction()*this.getVelocityX());
        this.setVelocityY(this.getFriction()*this.getVelocityY());
        
        if (this.getRangeLeft()>1){
            this.setRangeLeft(this.getRangeLeft()-1);
        }
        this.setBounds();
        }
    }

    @Override
        public void render(Graphics g, int x, int y){
            int proj_cameraX = ((int) this.getX())-x;
            int proj_cameraY = ((int) this.getY())-y;

            g.fillOval(proj_cameraX,proj_cameraY, this.getWidth(), this.getHeight());
            g.drawRect(proj_cameraX,proj_cameraY, this.getWidth(), this.getHeight());
        }


    
}



