package entities;

public class Projectiles {
    
    private Basic_Entity source_Entity;



    // Direction of speed
    private double velocityX = 0; 
    private double velocityY = 0;
    private int range_left; 
    private int width = 5;
    private int height = 5;
    // friction (to limitate the speed of the entity)
    private double friction =1 ;

    public Projectiles(Basic_Entity source){
        this.source_Entity = source;
        this.range_left = source.getRange();

        // I'll define the base velocity of my projectile
        Direction facing_source = this.source_Entity.getFacing();
        switch (facing_source){
            case UP :
                this.velocityY = -this.source_Entity.getBulletFast();
                break;
            case DOWN_LEFT :
                this.velocityY = this.source_Entity.getBulletFast()*Math.sqrt(2)/2;
                this.velocityX = -this.source_Entity.getBulletFast()*Math.sqrt(2)/2;
                break;
            case DOWN_RIGHT :
                this.velocityY = this.source_Entity.getBulletFast()*Math.sqrt(2)/2;
                this.velocityX = this.source_Entity.getBulletFast()*Math.sqrt(2)/2;
                break;
            case UP_LEFT :
                this.velocityY = -this.source_Entity.getBulletFast()*Math.sqrt(2)/2;
                this.velocityX = -this.source_Entity.getBulletFast()*Math.sqrt(2)/2;
                break;
            case UP_RIGHT :
                this.velocityY = -this.source_Entity.getBulletFast()*Math.sqrt(2)/2;
                this.velocityX = this.source_Entity.getBulletFast()*Math.sqrt(2)/2;
                break;
            case DOWN :
                this.velocityY = this.source_Entity.getBulletFast();
                break;
            case LEFT :
                this.velocityX = -this.source_Entity.getBulletFast();
                break;
            case RIGHT :
                this.velocityY = -this.source_Entity.getBulletFast();
                break;
        }
        
        
    }


}
