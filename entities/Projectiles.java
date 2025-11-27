package entities;

import map.Map;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.text.Normalizer.Form;


public abstract class Projectiles {
    
    private Basic_Entity source_Entity;


    private Rectangle bounds;
    // Direction of speed
    private int living_time;
    private double velocityX  ; 
    private double velocityY  ;
    private int range_left; 
    private int width ;
    private int height ;
    private double x;
    private double y ;
    private double directionAngle;

    private double recoil;
    private double knockback;

    private boolean hasToDestroy;

    private boolean bounce_able = false;
    // friction (to limitate the speed of the entity)
    private double friction ;

    public Projectiles(Basic_Entity source){
        this.source_Entity = source;
        this.range_left = source.getRange();
        this.directionAngle = source.getFacingAngle();
        double bulletSpeed = this.source_Entity.getBulletFast();
        this.velocityX = bulletSpeed * Math.cos(this.directionAngle);
        this.velocityY = bulletSpeed * Math.sin(this.directionAngle);
        this.x = source_Entity.getX();
        this.y = source_Entity.getY();
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
        return Math.abs(this.velocityX)<Math.abs(this.velocityY);
    }

    public boolean getDiagonal(){
        return  Math.abs(this.velocityX)==Math.abs(this.velocityY);
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
        this.bounds = new Rectangle((int) this.x,(int) this.y,this.width,this.height);
    }

    public void setKnockBack(double back){
        this.knockback = back;
    }

    public void setRecoil(double recoil){
        this.recoil = recoil;
    }

    protected void alignWithSource(){
        if (this.source_Entity == null || this.width == 0 || this.height == 0) {
            return;
        }
        double entityCenterX = this.source_Entity.getX() + this.source_Entity.getWidthInPixels() / 2.0;
        double entityCenterY = this.source_Entity.getY() + this.source_Entity.getHeightInPixels() / 2.0;
        double projectileRadius = Math.max(this.width, this.height) / 2.0;
        double entityRadius = Math.max(this.source_Entity.getWidthInPixels(), this.source_Entity.getHeightInPixels()) / 2.0;
        double offset = entityRadius + projectileRadius + 2;
        double centerX = entityCenterX + Math.cos(this.directionAngle) * offset;
        double centerY = entityCenterY + Math.sin(this.directionAngle) * offset;
        this.x = centerX - this.width / 2.0;
        this.y = centerY - this.height / 2.0;
        setBounds();
    }

    // SOME utils 

    public boolean isNotMoving(){
        return (Math.abs(this.velocityX)<=0.1)&&(Math.abs(this.velocityY)<=0.1);
    }
    public void setToDestroy(){
        this.hasToDestroy=true;
    }
    public boolean toDestroy(){
       return (isNotMoving()||(this.range_left==0))||(this.hasToDestroy);
    }

    // drawing 

    public abstract void render(Graphics g, int x, int y);
    
    }


    

    



