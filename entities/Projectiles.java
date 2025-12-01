package entities;

import map.Map;
import java.awt.Graphics;
import java.awt.Rectangle;


public abstract class Projectiles {
    
    //which enity shoot that
    private Basic_Entity source_Entity;

    // boundaries
    private Rectangle bounds;
    // how many times the projectile exist ( can be usefull in the future)
    private int living_time;
    //velocity 
    private double velocityX  ;
    private double velocityY  ;
    // Range is not the range but the time (frames) that left to exist before being removed
    private int range_left; 
    // Dimension
    private int width ;
    private int height ;
    //pos
    private double x;
    private double y ;
    // Angle
    private double directionAngle;

    // Some characteristics of the projectiles for the physics of the game

    private double recoil; // the shooting recoil (not implemented)
    private double knockback; // knockback taken by the entity because of the shoot of enemy
    private boolean bounce_able = false;// Not used but the goal was to make bounce_able projectiles 
    private int penetration = 0; 
    private double friction ;  // friction (to limitate the speed of the entity)
    
    private boolean hasToDestroy; // is true, in GameWorld we remove the projectile from the list of projectiles

    
    
    

    public Projectiles(Basic_Entity source){
        this.source_Entity = source;
        this.range_left = source.getRange();
        this.directionAngle = source.getFacingAngle();
        double bulletSpeed = source.getBulletFast();
        double cosAngle = Math.cos(this.directionAngle);
        double sinAngle = Math.sin(this.directionAngle);
        this.velocityX = bulletSpeed * cosAngle;
        this.velocityY = bulletSpeed * sinAngle;
        this.x = source.getX();
        this.y = source.getY();
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

    public double getDirectionAngle(){
        return this.directionAngle;
    }

    public boolean getVertical(){
        // Cache abs values to avoid multiple calls
        double absVx = this.velocityX < 0 ? -this.velocityX : this.velocityX;
        double absVy = this.velocityY < 0 ? -this.velocityY : this.velocityY;
        return absVx < absVy;
    }

    public boolean getDiagonal(){
        // Cache abs values to avoid multiple calls
        double absVx = this.velocityX < 0 ? -this.velocityX : this.velocityX;
        double absVy = this.velocityY < 0 ? -this.velocityY : this.velocityY;
        return absVx == absVy;
    }

    public Rectangle getBounds(){
        return this.bounds;
    }

    public double getRecoil(){
        return this.recoil;
    }

    public double getKnockBack(){
        return this.knockback;
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
    public void setBounds(){
        // Reuse existing Rectangle if possible to avoid allocations
        if (this.bounds == null) {
            this.bounds = new Rectangle((int) this.x, (int) this.y, this.width, this.height);
        } else {
            this.bounds.setBounds((int) this.x, (int) this.y, this.width, this.height);
        }
    }

    public void setKnockBack(double back){
        this.knockback = back;
    }

    public void setRecoil(double recoil){
        this.recoil = recoil;
    }

    // to align the projectiles with the source
    protected void alignWithSource(){
        if (this.source_Entity == null || this.width == 0 || this.height == 0) {
            return;
        }
        // Cache entity values to avoid multiple method calls
        double entityX = this.source_Entity.getX();
        double entityY = this.source_Entity.getY();
        int entityWidth = this.source_Entity.getWidthInPixels();
        int entityHeight = this.source_Entity.getHeightInPixels();
        
        double entityCenterX = entityX + entityWidth * 0.5;
        double entityCenterY = entityY + entityHeight * 0.5;
        double projectileRadius = Math.max(this.width, this.height) * 0.5;
        double entityRadius = Math.max(entityWidth, entityHeight) * 0.5;
        double offset = entityRadius + projectileRadius + 2;
        double cosAngle = Math.cos(this.directionAngle);
        double sinAngle = Math.sin(this.directionAngle);
        double centerX = entityCenterX + cosAngle * offset;
        double centerY = entityCenterY + sinAngle * offset;
        this.x = centerX - this.width * 0.5;
        this.y = centerY - this.height * 0.5;
        setBounds();
    }

    // SOME utils 

    public boolean isNotMoving(){
        // Compare squared distance to avoid sqrt
        double speedSq = this.velocityX * this.velocityX + this.velocityY * this.velocityY;
        return speedSq < 16.0; // 4^2 = 16
    }
    public void setToDestroy(){
        this.hasToDestroy=true;
    }

    // call this function to define which projectiles to destroy 
    public boolean toDestroy(){
       return (isNotMoving()||(this.range_left==0))||(this.hasToDestroy);
    }

    // drawing 

    public abstract void render(Graphics g, int x, int y);
    
    }


    

    



