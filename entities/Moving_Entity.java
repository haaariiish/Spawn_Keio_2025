package entities;

import map.Map;

import java.awt.Point;

public class Moving_Entity extends Basic_Entity{

    // Speed
    private double speed=0; // pixels per frame ? 

    // Direction of speed
    private double velocityX = 0; 
    private double velocityY = 0;

    // friction (to limitate the speed of the entity)
    private double friction = 0.85;

    public Moving_Entity(){
        super();
    }

    // Setters -----------------------------------------------------------------
    public void setVelocityX(double vx){
        this.velocityX = vx;
    }

    public void setVelocityY(double vy){
        this.velocityY = vy;
    }

    public void setSpeed(double speed){
        this.speed = speed;
    }

    public void setFriction(double friction){
        this.friction = friction;
    }

    // Getters -----------------------------------------------------------------
    public double getVelocityX(){
        return this.velocityX;
    }

    public double getVelocityY(){
        return this.velocityY;
    }

    public double getSpeed(){
        return this.speed;
    }

    public double getFriction(){
        return this.friction;
    }




    //Moving in the Map ----------------------------------------------------------------------------------------------

    public void update(Map map) {
        // Calculer la nouvelle position
        double newX = this.getX() + velocityX;
        double newY = this.getY()+ velocityY;
        
        // Vérifier collision HORIZONTALE
        if (!map.collidesWithWall((int)newX, (int)this.getY(), this.getWidthInPixels(), this.getHeightInPixels())) {
            this.setX(newX); // if there is no wall
        } else {
            velocityX = 0; //  if there is a Wall
        }
        
        // Vérifier collision VERTICALE (séparément pour sliding sur les murs)
        if (!map.collidesWithWall((int)this.getX(), (int)newY, this.getWidthInPixels(), this.getHeightInPixels())) {
            this.setY(newY);
        } else {
            velocityY = 0;
        }
        
        // Slow down in any case to have a cap for our speed
        velocityX *= this.friction;
        velocityY *= this.friction;
    }

    // Velocity update

    public void moveLeft() {
        velocityX = -speed;
    }
    
    public void moveRight() {
        velocityX = speed;
    }
    
    public void moveUp() {
        velocityY = -speed;
    }
    
    public void moveDown() {
        velocityY = speed;
    }
    
    public void stop() {
        velocityX = 0;
        velocityY = 0;
    }

 // Conversion PIXELS - TILES ( debug or for game logic) ------------------------------------------------

    public Point getTilePosition(int tileSize) {
        int tileX = (int)(this.getX() / tileSize);
        int tileY = (int)(this.getY() / tileSize);
        return new Point(tileX, tileY);
    }


    
}