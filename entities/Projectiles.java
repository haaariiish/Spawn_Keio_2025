package entities;

import map.Map;
import java.awt.Graphics;


public abstract class Projectiles {
    
    private Basic_Entity source_Entity;

    // Direction of speed
    private int living_time;
    private double velocityX  ; 
    private double velocityY  ;
    private int range_left; 
    private int width ;
    private int height ;
    private double x;
    private double y ;
    private boolean isVertical;
    private boolean isDiagonal;

    private boolean bounce_able = false;
    // friction (to limitate the speed of the entity)
    private double friction ;

    public Projectiles(Basic_Entity source){
        this.source_Entity = source;
        this.range_left = source.getRange();
        this.x = this.source_Entity.getX();
        this.y = this.source_Entity.getY();

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
                this.velocityX = +this.source_Entity.getBulletFast();
                break;
        }
    }
        
        
    public abstract void update(Map Map);
        // to implement for each types of projectiles 


    //GETTERS 
    public double getX(){
        return this.x;
    }
    public double getY(){
        return this.y;
    }

    public double getVelocityX(){
        return this.velocityX;
    }

    public double getVelocityY(){
        return this.velocityY;
    }

    public boolean getBounceAble(){
        return this.bounce_able;
    }

    public int getRangeLeft(){
        return this.range_left;
    }
        
    public int getWidth(){
        return this.width;
    }

    public int getHeight(){
        return this.height;
    }

    public double getFriction(){
        return this.friction;
    }

    public Basic_Entity getSourceEntity(){
        return this.source_Entity;
    }

    public boolean getVertical(){
        return Math.abs(this.velocityX)<Math.abs(this.velocityY);
    }

    public boolean getDiagonal(){
        return  Math.abs(this.velocityX)==Math.abs(this.velocityY);
    }


    //SETTERS 
    public void setX(double x){
        this.x= x;
    }
    public void setY(double y){
        this.y= y;
    } 

    public void setWidth(int w){
        this.width = w;
    }

    public void setHeight(int h){
        this.height = h;
    }

    public void setFriction(double f){
        this.friction= f;
    }

    public void setSourceEntity(Basic_Entity be){
        this.source_Entity = be;
    }

    public void setRangeLeft(int range){
        this.range_left = range;
    }

    public void setVelocityX(double vx){
        this.velocityX = vx;
    }
    public void setVelocityY(double vy){
        this.velocityY = vy;
    }
    public void setBounceAble(boolean bounce){
        this.bounce_able = bounce;
    }

    // SOME utils 

    public boolean isNotMoving(){
        return (this.velocityX==0)&&(this.velocityY==0);
    }

    public boolean toDestroy(){
        return isNotMoving()||(this.range_left==0);
    }

    // drawing 

    public abstract void render(Graphics g, int x, int y);
    
    }



